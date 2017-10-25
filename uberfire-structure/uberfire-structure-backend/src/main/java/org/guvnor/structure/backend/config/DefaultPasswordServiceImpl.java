/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.backend.config;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.structure.server.config.PasswordService;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DefaultPasswordServiceImpl implements PasswordService {

    private static final Logger log = LoggerFactory.getLogger(DefaultPasswordServiceImpl.class);

    private static final String SECURE_STRING = System.getProperty("org.uberfire.secure.key",
                                                                   "org.uberfire.admin");
    private static final String SECURE_ALGORITHM = System.getProperty("org.uberfire.secure.alg",
                                                                      "PBEWithMD5AndDES");

    @Override
    public String encrypt(final String plainText) {
        final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(SECURE_STRING);
        encryptor.setAlgorithm(SECURE_ALGORITHM);

        String result = plainText;
        try {
            result = encryptor.encrypt(plainText);
        } catch (EncryptionOperationNotPossibleException e) {
            log.error("Unable to encrypt",
                      e);
        }
        return result;
    }

    @Override
    public String decrypt(final String encryptedText) {
        final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(SECURE_STRING);
        encryptor.setAlgorithm(SECURE_ALGORITHM);

        String result = encryptedText;
        try {
            result = encryptor.decrypt(encryptedText);
        } catch (EncryptionOperationNotPossibleException e) {
            log.error("Unable to decrypt",
                      e);
        }
        return result;
    }
}
