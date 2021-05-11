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

#### Enable MTLS

In order to enable mutual TLS it is possible to select mtls profile. Client cert and key is available in resources/keystore/client folder in the project

```shell
$ mvn spring-boot:run -Dspring-boot.run.profiles=mtls
```

API can be tested using Insomnia client. Client cert and key need to be loaded to Insomnia in order to invoke the service. Please uncheck 'Validate Certificates' in settings as the provided server certificate is self-signed.

Alternatively curl can be used to send a request:

```shell
$ cd FraudCheckDemo/src/main/resources/keystore/client
$ curl -X POST "https://localhost:8443/fraud-check" -H  "accept: */*" -H  "Content-Type: application/json" -d "{\"amount\":20,\"currency\":\"EUR\",\"terminalId\":\"T-002\",\"threatScore\":10,\"cardNumber\":\"KCybCt7X9r2kk83zSl0w5j+EkCkLySNxf5Jhier8Cz4=\"}" --cert ./client/client.cer --key ./client/client_key.pem --insecure
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

- Card number ending in `31` or `32`: amount exceeded the limit for this terminal (respectively 30 and 80 transactions for the card)
- Terminal Id not starting with `T`: terminal not found error
- Terminal Id ending with `02` or `03`: too many transactions for this terminal (respectively 697 and 980 transactions)
- Adjusting amount, currency and threatScore triggers or not the amount limit rule

As card numbers are expected to be send encrypted, here is a list of valid values to use in the request:
- `KCybCt7X9r2kk83zSl0w5j+EkCkLySNxf5Jhier8Cz4=` (valid card, low transaction number)
- `tkVaENbCuVcZEf4th6bBpvzDnfblo2rxTHXOSEEaDqw=` (card ending in 31, amount exceeded the limit for this terminal - score 15)
- `ERIhdVg9zZKjBX5nTrZmhBwf1/F1OqWBGxFU3nRJx2g=` (card ending in 32, amount exceeded the limit for this terminal - score 50)
- `M52w7xu90R8MeWLrgaBIIg==`                     (invalid card, pan too short)

Running test SimpleEncryptionServiceTest will display a list of card and their encrypted values.

#### Adding a new fraud rule

Adding a new fraud rule to fraud check service is pretty straight forward, simply follow the steps:
- Create a new class in ```com.pql.fraudcheck.rules``` package implementing ```IFraudDetection```
- Implement both methods ```FraudRuleScore checkFraud(IncomingTransactionInfo transInfo)``` and ```boolean isEnabled()```
- Place the rule logic in `checkFraud` method and return a `FraudRuleScore` object accordingly
- Annotate the new class with ```@Component("<NEW_RULE_NAME>")```. Name can be arbitrary but it has to be unique within other fraud rule components

This is how the new rule class looks like:
```java
@Component("MY_NEW_RULE")
public class MyNewRule implements IFraudDetection {

    @Override
    public FraudRuleScore checkFraud(IncomingTransactionInfo transInfo) throws CurrencyException, CorruptedDataException {
        FraudRuleScore fraudRuleScore;
        // rule logic here

        return fraudRuleScore;
    }

    @Override
    public boolean isEnabled() {
        boolean enabled;
        // logic here

        return enabled;
    }
}
```

Done! Spring Boot will instantiate the new component automatically and put it in the fraud rule `Map` for constructor injection in ```FraudRulesHandler```. Then the new rule will be added to the `applicableFraudRuleList` and run only if its `isEnabled()` method returns `true`.
