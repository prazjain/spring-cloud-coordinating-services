
# Readme

**Eureka Server**

Start eureka server:
`cd eureka-server`  
`./mvnw spring-boot:run`  
Navigate to `http://localhost:8761/`  

**Order Service**  
`cd order-service`  
`./mvnw spring-boot:run`  

**Payment Service**  
`cd payment-service`  
`./mvnw spring-boot:run`  

Create 2 instances of Payment Service (keep a few min differe nce between starting the two instances)

Open Order Server and Payment Service instance via url from Eureka server page.
Hit the `/hello` endpoint for the 2 Payment Service instances.
Then hit `/hello` endpoint for Order Service, and refresh that page.  
You will notice the requests to order services are using load balanced payment service.  

Circuit Breakers are setup when Order Service calls Payment service.  
Open eureka server page, from there open pages for order server and payment service.  
Hit `/hello` endpoint for both order and payment services few times.  
Now open the `/actuator` endpoint for order service, and also view `/actuator/circuitbreakerevents` to view circuit breaker events.  

Explanation of circuit breaker configuration

* Trigger circuit breaker if minimum 3 calls have failed, otherwise below settings (or failure rate, slow call rate) will not be used.  
    resilience4j.circuitbreaker.instances.paymentservice-cb.minimum-number-of-calls=6  
* Sliding window size 3, use last 3 requests to decide the state of circuit breaker (once it has been triggered).    
    resilience4j.circuitbreaker.instances.paymentservice-cb.sliding-window-size=6
* Is this window of type time or request count.    
    resilience4j.circuitbreaker.instances.paymentservice-cb.sliding-window-type=COUNT_BASED  
* How slow does a service need to be, for circuit breaker to be triggered.    
resilience4j.circuitbreaker.instances.paymentservice-cb.slow-call-duration-threshold=2500  
* How many calls (percentage) have to be slow in the sliding window to trigger circuit breaker    
resilience4j.circuitbreaker.instances.paymentservice-cb.slow-call-rate-threshold=50
* How long to be in open circuit state
  resilience4j.circuitbreaker.instances.paymentservice-cb.wait-duration-in-open-state=20000

So here, in window size of 6, if 3 calls exceed 2.5sec timelimit, then circuit breaker would be triggered.  

**API Gateway**  
In API Gateway project, we have set routes for `/hello` to go to order service endpoint.  
`/order` route will be re-written to go to order service endpoint's `/hello`.  
`/payment1` route will be re-written to go to payment service 1 endpoint's `/hello`.  
`/payment2` route will be re-written to go to payment service 2 endpoint's `/hello`.  

