# FraudCheckDemo
Demo project for a fraud check service

The purpose of the project is to implement a microservice to check fraud given payment terminal, card, amount and threat score.

#### Prerequisites

Application runs with Java8, SpringBoot v2.4.5 and Maven.

#### Running the application with Maven

```shell
$ git clone https://github.com/pql81/FraudCheckDemo.git
$ cd FraudCheckDemo
$ git checkout master
$ mvn spring-boot:run
```

#### OpenAPI documetation and UI test

OpenAPI description can be found at http://localhost:8080/v3/api-docs/

Testing API is possible at http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
The URL above works as API client to test applcition functionalities

#### Running test for required use case

To run all test:
```shell
$ cd FraudCheckDemo
$ mvn test
```
