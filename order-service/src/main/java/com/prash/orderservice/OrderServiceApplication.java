package com.prash.orderservice;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
@LoadBalancerClient(name="payment-service", configuration = LoadBalancerAlgorithmConfig.class)
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalancedWebClientBuilder() {
		return WebClient.builder();
	}

}

@RestController
class HelloController {
	@Autowired
	private WebClient.Builder webClient;

	@Autowired
	private ReactiveCircuitBreakerFactory circuitBreakerFactory;

	@GetMapping("/hello")
	public String getHello() {

		ReactiveCircuitBreaker rcb = circuitBreakerFactory.create("paymentservice-cb");

		String resp = rcb.run( webClient.build().get()
			.uri("http://payment-service/hello")
			.retrieve()
			.bodyToMono(String.class),
			throwable -> getDefaultPaymentServiceResponse()).block();
		return resp + " via Order Service";
	}

	private Mono<String> getDefaultPaymentServiceResponse() {
		System.out.println("Fallback for payment service called");
		return Mono.just("Fallback payment service");
	}
}
@RestController
class ServiceInstanceRestController {

	@Autowired
	private DiscoveryClient discoveryClient;

	@RequestMapping("/service-instances/{applicationName}")
	public List<ServiceInstance> serviceInstancesByApplicationName(
		@PathVariable String applicationName) {
		return this.discoveryClient.getInstances(applicationName);
	}
}
