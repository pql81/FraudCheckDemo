package com.pql.fraudcheck.service;

import com.pql.fraudcheck.exception.CardPanException;
import lombok.extern.log4j.Log4j2;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by pasqualericupero on 09/05/2021.
 */
@Log4j2
@Service
public class SimpleEncryptionService {

    private StandardPBEStringEncryptor encryptor;


    public SimpleEncryptionService(@Value("${encryption.service.secret}") String password,
                                   @Value("${encryption.service.algorithm}") String algorithm ) {

        encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm(algorithm);
    }

    public String encrypt(String plainText) {
        try {
            return encryptor.encrypt(plainText);
        } catch (EncryptionOperationNotPossibleException e) {
            throw new CardPanException("Could not encrypt card PAN", e);
        }
    }

    public String decrypt(String encryptedText) {
        try {
            return encryptor.decrypt(encryptedText);
        } catch (EncryptionOperationNotPossibleException e) {
            throw new CardPanException("Could not decrypt card PAN", e);
        }
    }
}
