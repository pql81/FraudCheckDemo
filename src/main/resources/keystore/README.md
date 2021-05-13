#### Certificates and keys

This folder contains server truststore and keystore. Subfolder `client` contains client truststore and keystore as well as client key and cert for Insomnia Rest client and curl to authenticate.

Please notice that certificates and keys are included in this repo **for test purpose only and they shouldn't be used in a production environment**.

The purpose of this folder is to provide a quick setup for MTLS. To test it run the command below:
```shell
$ mvn spring-boot:run -Dspring-boot.run.profiles=mtls
```
And send an http request:
```shell
$ cd FraudCheckDemo/src/main/resources/keystore/client
$ curl -X POST "https://localhost:8443/fraud-check" -H  "accept: */*" -H  "Content-Type: application/json" -d "{\"amount\":20,\"currency\":\"EUR\",\"terminalId\":\"T-002\",\"threatScore\":10,\"cardNumber\":\"KCybCt7X9r2kk83zSl0w5j+EkCkLySNxf5Jhier8Cz4=\"}" --cert ./client.cer --key ./client_key.pem --insecure
```
