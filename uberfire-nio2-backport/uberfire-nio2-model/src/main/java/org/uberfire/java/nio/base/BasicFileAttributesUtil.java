package org.uberfire.java.nio.base;

import java.util.HashMap;
import java.util.Map;

import static org.uberfire.java.nio.base.AbstractBasicFileAttributeView.*;

public class BasicFileAttributesUtil {

    public static Map<String, Object> cleanup( final Map<String, Object> _attrs ) {
        final Map<String, Object> attrs = new HashMap<String, Object>( _attrs );

        for ( final String key : _attrs.keySet() ) {
            if ( key.startsWith( IS_REGULAR_FILE ) || key.startsWith( IS_DIRECTORY ) ||
                    key.startsWith( IS_SYMBOLIC_LINK ) || key.startsWith( SIZE ) ||
                    key.startsWith( FILE_KEY ) || key.startsWith( IS_OTHER ) ||
                    key.startsWith( LAST_MODIFIED_TIME ) || key.startsWith( LAST_ACCESS_TIME ) ||
                    key.startsWith( CREATION_TIME ) ) {
                attrs.put( key, null );
            }
        }

        return attrs;
    }

}
