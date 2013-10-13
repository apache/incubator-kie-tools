package org.uberfire.java.nio.base.dotfiles;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.AttrHolder;
import org.uberfire.java.nio.base.Properties;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

/**
 *
 */
public final class DotFileUtils {

    private DotFileUtils() {

    }

    public static void buildDotFile( final Path path,
                                     final OutputStream out,
                                     final FileAttribute<?>... attrs ) {
        if ( attrs != null && attrs.length > 0 ) {
            final Properties properties = new Properties();

            for ( final FileAttribute<?> attr : attrs ) {
                if ( attr.value() instanceof Serializable ) {
                    properties.put( attr.name(), attr.value() );
                }
            }

            try {
                properties.store( out );
            } catch ( final Exception e ) {
                throw new IOException( e );
            }

            if ( path instanceof AttrHolder ) {
                ( (AttrHolder) path ).getAttrStorage().loadContent( properties );
            }
        } else {
            path.getFileSystem().provider().deleteIfExists( dot( path ) );
        }
    }

    public static Path dot( final Path path ) {
        if ( path.getFileName() == null ) {
            return path.resolve( ".root" );
        }
        return path.resolveSibling( "." + path.getFileName() );
    }

    public static FileAttribute<?>[] consolidate( final Map<String, Object> props,
                                                  final FileAttribute<?>... attrs ) {
        if ( props == null || props.size() == 0 ) {
            return attrs;
        }

        final Map<String, Object> temp = new HashMap<String, Object>( props );

        for ( final FileAttribute<?> attr : attrs ) {
            temp.put( attr.name(), attr.value() );
        }

        final FileAttribute<?>[] result = new FileAttribute<?>[ temp.size() ];
        int i = -1;
        for ( final Map.Entry<String, Object> attr : temp.entrySet() ) {
            result[ ++i ] = new FileAttribute<Object>() {
                @Override
                public String name() {
                    return attr.getKey();
                }

                @Override
                public Object value() {
                    return attr.getValue();
                }
            };
        }

        return result;
    }
}
