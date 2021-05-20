package com.pql.fraudcheck.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by pasqualericupero on 09/05/2021.
 */
@RunWith(MockitoJUnitRunner.class)
public class SimpleEncryptionServiceTest {

    private String password = "decidelater";
    private String algorithm = "PBEWithMD5AndTripleDES";

    SimpleEncryptionService encryptionService;


    @Before
    public void setUp() {
        encryptionService = new SimpleEncryptionService(password, algorithm);
    }

    @Test
    public void testEncrypt() throws Exception {
        String[] cardArray = {"5555444455551230", "5555444455551231", "5555444455551232",
                              "4433443344331230", "4433443344331231", "4433443344331232",
                              "343455553431230", "343455553431231", "343455553431232",
                              "1234567", "12345678901234567890"};

        System.out.println("Printing encrypted cards:");
        Arrays.stream(cardArray)
                .forEach(card -> {
                    String encrypted = encryptionService.encrypt(card);
                    assertEquals(card, encryptionService.decrypt(encrypted));
                    System.out.println("[" + card + "]" + "-> " + encrypted);
                });
    }

    @Test
    public void testDecrypt() throws Exception {
        String[] cardArray = {"LjN5spFsAhBcXEkKnQHsHA29d00ZGAg53vh9bdxjbpc=", "57EKEPgEQbn0KbwI/KL07PLQG+GWBNKqrrRYXhiVeRw=",
                              "5JKaDwkzNodSV4vkE3YCFit806VQPuyfIqX3gEK2vUk=", "URogBH4GeglmHnvzyXmFqSUcsmI52u6NSCjrcVHI/mc=",
                              "bGmBrsbyL8KKSthbV2hwVMm4cecSSPiH", "5hxK9dvIt9Bk8rpOV5B5NA/wOCkHZs1P"};

        Arrays.stream(cardArray)
                .forEach(card -> {
                    String plain = encryptionService.decrypt(card);
                    assertNotEquals(card, plain);
                });
    }

    @Test
    public void testJwt() throws Exception {
        long ttlSecs = 3600*24*100;
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        long expMillis = nowMillis + 1000*ttlSecs;
        Date exp = new Date(expMillis);

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("mySuperSecret!");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        String jwtToken = "Bearer " + Jwts.builder()
                .setId("trans-service")
                .setIssuedAt(now)
                .setSubject("ME")
                .setIssuer("trans-service")
                .setId(UUID.randomUUID().toString())
                .setExpiration(exp)
                .signWith(signatureAlgorithm, signingKey)
                .compact();

        assertNotNull(jwtToken);
        System.out.print(jwtToken);
    }
}
