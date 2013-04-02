package org.uberfire.backend.server.config;

import javax.enterprise.context.ApplicationScoped;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DefaultPasswordServiceImpl implements PasswordService {

    private static final Logger log = LoggerFactory.getLogger( DefaultPasswordServiceImpl.class );

    private static final String SECURE_STRING = System.getProperty( "org.kie.secure.key",
                                                                    "org.kie.admin" );
    private static final String SECURE_ALGORITHM = System.getProperty( "org.kie.secure.alg",
                                                                       "PBEWithMD5AndTripleDES" );

    @Override
    public String encrypt( final String plainText ) {
        final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword( SECURE_STRING );
        encryptor.setAlgorithm( SECURE_ALGORITHM );

        String result = plainText;
        try {
            result = encryptor.encrypt( plainText );
        } catch ( EncryptionOperationNotPossibleException e ) {
            log.error( "Unable to encrypt",
                       e );
        }
        return result;
    }

    @Override
    public String decrypt( final String encryptedText ) {
        final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword( SECURE_STRING );
        encryptor.setAlgorithm( SECURE_ALGORITHM );

        String result = encryptedText;
        try {
            result = encryptor.decrypt( encryptedText );
        } catch ( EncryptionOperationNotPossibleException e ) {
            log.error( "Unable to decrypt",
                       e );
        }
        return result;
    }
}
