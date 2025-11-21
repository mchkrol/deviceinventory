# Device Inventory

A Spring Boot application with a device inventory and network deployment operations REST API

## Features
- REST operations for device inventory and network deployment
- Validation of the device data and network connectivity
- H2 in-memory database
- Swagger UI documentation

## Technologies
- Java 21
- Spring Boot 3.3.4
- Spring Data JPA
- Lombok
- H2 Database
- Swagger (SpringDoc OpenAPI)

## Description
- The REST API allows to register devices to network deployment and retrieve device data and network structure
- There are three types of devices: Gateway, Switch and Access Point
- If a device is attached to another device in the same network, it is represented via uplink reference
- The entire set of devices may form a tree structure or a forrest (collection of trees) if there is more than one root network device.

## How to run (Windows)
1. Clone the repository:<br>
   ```git clone https://github.com/mchkrol/deviceinventory.git```<br>
   ```cd .\deviceinventory\```
2. Run the application:<br>
   ```./mvnw spring-boot:run```<br>
   or using run configurations in IDE (the DeviceinventoryApplication class)
4. After startup, you can access:<br>
   Swagger UI → http://localhost:8080/swagger-ui/index.html<br>
   OpenAPI JSON → http://localhost:8080/v3/api-docs<br>
   H2 Console → http://localhost:8080/h2-console<br>

## Unit Tests
Run all unit tests:<br>
```./mvnw test```<br>
Unit tests are located in:<br>
```src/test/java/com/michalkrol/deviceinventory/```
