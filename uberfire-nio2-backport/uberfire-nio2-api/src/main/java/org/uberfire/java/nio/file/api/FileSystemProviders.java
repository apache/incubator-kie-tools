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

package org.uberfire.java.nio.file.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static java.util.Collections.*;
import static org.uberfire.commons.validation.Preconditions.*;

/**
 * Back port of JSR-203 from Java Platform, Standard Edition 7.
 * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/spi/FileSystemProvider.html">Original JavaDoc</a>
 */
public final class FileSystemProviders {

    private static final Logger LOGGER = LoggerFactory.getLogger( FileSystemProviders.class );

    private static List<FileSystemProvider> installedProviders;
    private static Map<String, FileSystemProvider> mapOfinstalledProviders;

    static {
        try {
            installedProviders = buildProviders();
            mapOfinstalledProviders = buildProvidersMap();
        } catch ( final Throwable ex ) {
            LOGGER.error( "Can't initialize FileSystemProviders", ex );
        }
    }

    private static synchronized List<FileSystemProvider> buildProviders() {
        final ServiceLoader<FileSystemProvider> providers = ServiceLoader.load( FileSystemProvider.class );
        if ( providers == null ) {
            return emptyList();
        }
        final List<FileSystemProvider> result = new ArrayList<FileSystemProvider>();

        for ( final FileSystemProvider provider : providers ) {
            result.add( provider );
        }
        return unmodifiableList( result );
    }

    private static synchronized Map<String, FileSystemProvider> buildProvidersMap() {
        final Map<String, FileSystemProvider> result = new HashMap<String, FileSystemProvider>( installedProviders.size() + 1 );
        for ( int i = 0; i < installedProviders.size(); i++ ) {
            final FileSystemProvider provider = installedProviders.get( i );
            if ( i == 0 ) {
                provider.forceAsDefault();
                result.put( "default", provider );
            }
            result.put( provider.getScheme(), provider );
        }
        return unmodifiableMap( result );
    }

    /**
     * Non standard method that provides access to default provider (default:// scheme).
     * @return the default file system provider
     * @throws ServiceConfigurationError
     */
    public static FileSystemProvider getDefaultProvider() throws ServiceConfigurationError {
        return installedProviders.get( 0 );
    }

    /**
     * Non standard method to resolve a provider based on uri's scheme
     * @param uri the uri
     * @return the file system provider
     */
    public static FileSystemProvider resolveProvider( final URI uri ) {
        checkNotNull( "uri", uri );

        return getProvider( uri.getScheme() );
    }

    private static FileSystemProvider getProvider( final String scheme )
            throws FileSystemNotFoundException, ServiceConfigurationError {
        checkNotEmpty( "scheme", scheme );

        final FileSystemProvider fileSystemProvider = mapOfinstalledProviders.get( scheme );

        if ( fileSystemProvider == null ) {
            throw new FileSystemNotFoundException( "Provider '" + scheme + "' not found" );
        }

        return fileSystemProvider;
    }

    /**
     * @throws ServiceConfigurationError
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/spi/FileSystemProvider.html#installedProviders()">Original JavaDoc</a>
     */
    public static List<FileSystemProvider> installedProviders() throws ServiceConfigurationError {
        return installedProviders;
    }

}