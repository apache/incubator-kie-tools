package org.uberfire.backend.server;

import java.util.HashMap;
import java.util.Map;

public class UserRegistry {

    private UserRegistry() {

    }

    private static final Map<String, String> mailUser = new HashMap<String, String>() {{
        put( "alexandre.porcelli@gmail.com", "porcelli" );
        put( "conan@example.com", "conan" );
        put( "manstis@example.com", "manstis" );
        put( "rikkola@example.com", "rikkola" );
        put( "jliu@example.com", "jliu" );

    }};

    private static final Map<String, String> userMail = new HashMap<String, String>() {{
        put( "porcelli", "alexandre.porcelli@gmail.com" );
        put( "conan", "me@example.com" );
        put( "manstis", "manstis@example.com" );
        put( "rikkola", "rikkola@example.com" );
        put( "jliu", "jliu@example.com" );
    }};

    private static final Map<String, String> userFullName = new HashMap<String, String>() {{
        put( "porcelli", "Alexandre Porcelli" );
    }};

    public static String getUser( final String email ) {
        return mailUser.get( email );
    }

    public static String getEmail( final String user ) {
        return userMail.get( user );
    }

    public static String getFullName( final String user ) {
        return userFullName.get( user );
    }

    public static boolean hasUser( final String userName ) {
        return userMail.containsKey( userName );
    }

    public static String getWebsite( String userName ) {
        return null;
    }
}
