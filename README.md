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

#### Running the application with MTLS

In order to enable mutual TLS it is possible to select mtls profile. Client cert and key is available in resources/keystore/client folder in the project

```shell
$ git clone https://github.com/pql81/FraudCheckDemo.git
$ cd FraudCheckDemo
$ git checkout master
$ mvn spring-boot:run -Dspring-boot.run.profiles=mtls
```

Using Insomnia client is possible to load client cert and key and test the service. Please uncheck 'Validate Certificates' in settings as the provided certificate is self-signed.

Alternatively curl can be used to send a request:

```shell
$ cd FraudCheckDemo/src/main/resources/keystore/client
$ curl -X POST "https://localhost:8443/fraud-check" -H  "accept: */*" -H  "Content-Type: application/json" -d "{\"amount\":20,\"currency\":\"EUR\",\"terminalId\":\"T-002\",\"threatScore\":10,\"cardNumber\":\"5555444455554444\"}" --cert ./client/client.cer --key ./client/client_key.pem --insecure
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
