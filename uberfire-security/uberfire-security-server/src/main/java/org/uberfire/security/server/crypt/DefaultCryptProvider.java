/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.security.server.crypt;

import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.uberfire.security.crypt.CryptProvider;

public class DefaultCryptProvider implements CryptProvider {

    private static final Charset UTF8 = Charset.forName( "UTF-8" );
    private static final Base64 BASE64 = new Base64();
    private static final String HASH_FUNCTION = "SHA-256";
    private static final String KEY = "UFG00D3N0U6HT";

    @Override
    public String encrypt( final String content,
                           final Object salt ) {
        try {
            final Cipher cipher = buildCipher( salt, Cipher.ENCRYPT_MODE );

            final byte[] plainTextBytes = content.getBytes( UTF8 );
            final byte[] cipherText = cipher.doFinal( plainTextBytes );

            return BASE64.encodeToString( cipherText );
        } catch ( final Exception e ) {
            throw new RuntimeException( "Failed to encrypt", e );
        }
    }

    @Override
    public String decrypt( final String content,
                           final Object salt ) {
        try {
            final Cipher decipher = buildCipher( salt, Cipher.DECRYPT_MODE );

            final byte[] plainText = decipher.doFinal( BASE64.decode( content ) );

            return new String( plainText, UTF8 );
        } catch ( final Exception e ) {
            return null;
        }
    }

    private Cipher buildCipher( final Object salt,
                                final int mode )
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        final MessageDigest md = MessageDigest.getInstance( HASH_FUNCTION );
        if ( salt != null ) {
            md.update( salt.toString().getBytes( UTF8 ) );
        }
        final byte[] digestOfPassword = md.digest( KEY.getBytes( UTF8 ) );
        final byte[] keyBytes = Arrays.copyOf( digestOfPassword, 24 );
        for ( int j = 0, k = 16; j < 8; ) {
            keyBytes[ k++ ] = keyBytes[ j++ ];
        }

        final SecretKey key = new SecretKeySpec( keyBytes, "DESede" );
        final IvParameterSpec iv = new IvParameterSpec( new byte[ 8 ] );
        final Cipher cipher = Cipher.getInstance( "DESede/CBC/PKCS5Padding" );

        cipher.init( mode, key, iv );

        return cipher;
    }
}
