package org.uberfire.backend.server.config;

public interface PasswordService {

    String encrypt( String plainText );

    String decrypt( String encryptedText );
}
