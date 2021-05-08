#!/bin/bash

TARGET_DIR="../src/main/resources/keystore"

echo "Deleting existing keys and certificates..."
rm -rf $TARGET_DIR
mkdir $TARGET_DIR
mkdir $TARGET_DIR/client

echo ""
echo "Creating server keystore..."
keytool -v -genkeypair -dname "CN=Pql,O=Pql Demo,C=IE" -keystore $TARGET_DIR/keystore.jks -storepass secret -keypass secret -keyalg RSA -keysize 2048 -alias server -validity 3650 -deststoretype pkcs12 -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth -ext SubjectAlternativeName:c=DNS:localhost,IP:127.0.0.1

echo ""
echo "Exporting server certificate to client truststore..."
keytool -v -exportcert -file $TARGET_DIR/server.cer -alias server -keystore $TARGET_DIR/keystore.jks -storepass secret -rfc
keytool -v -importcert -file $TARGET_DIR/server.cer -alias server -keystore $TARGET_DIR/client/truststore.jks -storepass secret -noprompt

echo ""
echo "Creating client keytstore..."
keytool -v -genkeypair -dname "CN=Pql Cli,O=Pql Demo,C=IE" -keystore $TARGET_DIR/client/keystore.jks -storepass secret -keypass secret -keyalg RSA -keysize 2048 -alias client -validity 3650 -deststoretype pkcs12 -ext KeyUsage=digitalSignature,dataEncipherment,keyEncipherment,keyAgreement -ext ExtendedKeyUsage=serverAuth,clientAuth

echo ""
echo "Exporting client certificate to server truststore..."
keytool -v -exportcert -file $TARGET_DIR/client/client.cer -alias client -keystore $TARGET_DIR/client/keystore.jks -storepass secret -rfc
keytool -v -importcert -file $TARGET_DIR/client/client.cer -alias client -keystore $TARGET_DIR/truststore.jks -storepass secret -noprompt

echo ""
echo "Creating client cert and key for curl..."
keytool -importkeystore -srckeystore $TARGET_DIR/client/keystore.jks -srcstorepass secret -srckeypass secret -srcalias client -destalias client -destkeystore $TARGET_DIR/client/client.p12 -deststoretype PKCS12 -deststorepass secret -destkeypass secret
openssl pkcs12 -in $TARGET_DIR/client/client.p12 -passin 'pass:secret' -nodes -nocerts -out $TARGET_DIR/client/client_key.pem

echo ""
echo "Cleaning files..."
rm $TARGET_DIR/server.cer
rm $TARGET_DIR/client/client.p12

echo ""
echo "Done!"