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

#### Testing the service

In order to test the service, as it is a demo using mocked external services (card geo-location, terminal info), there are some particular request values that trigger a particular fraud:

- Card number ending in 31 or 32: amount exceeded the limit for this terminal (respectively 30 and 80 transactions for the card)
- Terminal Id not starting with 'T': terminal not found error
- Terminal Id ending with 02 or 03: too many transactions for this terminal (respectively 697 and 980 transactions)
- Adjusting amount, currency and threatScore triggers or not the amount limit rule
