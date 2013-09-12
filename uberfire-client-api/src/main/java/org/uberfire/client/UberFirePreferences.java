package org.uberfire.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class UberFirePreferences {

    private static Map<String, Object> preferences = new HashMap<String, Object>();

    public static Map<String, Object> getProperties() {
        return Collections.unmodifiableMap( preferences );
    }

    public static Object getProperty( final String o ) {
        return preferences.get( o );
    }

    public static Object getProperty( final String o,
                                      final Object def ) {
        final Object value = preferences.get( o );
        return value != null ? value : def;
    }

    public static void setProperties( Map<? extends String, ?> map ) {
        preferences.putAll( map );
    }

    public static Object setProperty( final String s,
                                      final Object o ) {
        return preferences.put( s, o );
    }
}
