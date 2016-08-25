/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.project;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Class taken from drools utility classes. (ClassUtils)
 */
public class MapClassLoader extends ClassLoader {

    private static final ProtectionDomain PROTECTION_DOMAIN;

    static {
        PROTECTION_DOMAIN = (ProtectionDomain) AccessController.doPrivileged( new PrivilegedAction() {

            public Object run() {
                return MapClassLoader.class.getProtectionDomain();
            }
        } );
    }


    private Map<String, byte[]> map;

    public MapClassLoader( Map<String, byte[]> map, ClassLoader parent ) {
        super( parent );
        this.map = map;
    }

    public Class<?> loadClass( final String name,
            final boolean resolve ) throws ClassNotFoundException {
        Class<?> cls = fastFindClass( name );

        if ( cls == null ) {
            cls = super.loadClass( name, resolve );
        }

        if ( cls == null ) {
            throw new ClassNotFoundException( "Unable to load class: " + name );
        }

        return cls;
    }

    public Class<?> fastFindClass( final String name ) {
        Class<?> cls = findLoadedClass( name );

        if ( cls == null ) {
            final byte[] clazzBytes = this.map.get( convertClassToResourcePath( name ) );
            if ( clazzBytes != null ) {
                int lastDotPos = name.lastIndexOf( '.' );
                String pkgName = lastDotPos > 0 ? name.substring( 0, lastDotPos ) : "";

                if ( getPackage( pkgName ) == null ) {
                    definePackage( pkgName,
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            null );
                }

                cls = defineClass( name,
                        clazzBytes,
                        0,
                        clazzBytes.length,
                        PROTECTION_DOMAIN );
            }

            if ( cls != null ) {
                resolveClass( cls );
            }
        }

        return cls;
    }

    public InputStream getResourceAsStream( final String name ) {
        final byte[] clsBytes = this.map.get( name );
        if ( clsBytes != null ) {
            return new ByteArrayInputStream( clsBytes );
        }
        return null;
    }

    public URL getResource( String name ) {
        return null;
    }

    public Enumeration<URL> getResources( String name ) throws IOException {
        return new Enumeration<URL>() {

            public boolean hasMoreElements() {
                return false;
            }

            public URL nextElement() {
                throw new NoSuchElementException();
            }
        };
    }

    public static String convertClassToResourcePath(final String pName) {
        return pName.replace( '.',
                '/' ) + ".class";
    }

}
