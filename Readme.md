
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
