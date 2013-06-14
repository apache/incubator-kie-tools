package org.uberfire.client.widgets.gravatar;

import java.security.NoSuchAlgorithmException;

import com.google.gwt.core.client.impl.Md5Digest;
import com.googlecode.gwt.crypto.bouncycastle.util.encoders.Hex;

/**
 * Url Builder
 * @author francois wauquier
 */
public class GravatarUrlBuilder {

    private static GravatarUrlBuilder instance;

    private GravatarUrlBuilder() {
    }

    /**
     * Get unique instance
     * @return
     */
    public static GravatarUrlBuilder get() {
        if ( instance == null ) {
            instance = new GravatarUrlBuilder();
        }
        return instance;
    }

    /**
     * Build the url
     * @param email
     * @return
     */
    public String build( final String email,
                         final int size ) {
        return "http://www.gravatar.com/avatar/" + hash( email ) + "?s=" + size + "&d=mm";
    }

    private String hash( String email ) {
        try {
            String cleanEmail = email.trim().toLowerCase();
            return new String( Hex.encode( Md5Digest.getInstance( "MD5" ).digest( cleanEmail.getBytes() ) ) );
        } catch ( NoSuchAlgorithmException e ) {
            throw new RuntimeException( "MD5 implementation not found", e );
        }
    }

}
