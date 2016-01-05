/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

    public static boolean buildDotFile( final Path path,
                                        final OutputStream out,
                                        final FileAttribute<?>... attrs ) {
        boolean hasContent = false;
        if ( attrs != null && attrs.length > 0 ) {
            final Properties properties = new Properties();

            for ( final FileAttribute<?> attr : attrs ) {
                if ( attr.value() instanceof Serializable ) {
                    hasContent = true;
                    properties.put( attr.name(), attr.value() );
                }
            }

            if ( hasContent ) {
                try {
                    properties.store( out );
                } catch ( final Exception e ) {
                    throw new IOException( e );
                }
            }

            if ( path instanceof AttrHolder ) {
                ( (AttrHolder) path ).getAttrStorage().loadContent( properties );
            }
        } else {
            path.getFileSystem().provider().deleteIfExists( dot( path ) );
        }

        if ( !hasContent ) {
            try {
                out.close();
            } catch ( java.io.IOException e ) {
            }
        }

        return hasContent;
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
