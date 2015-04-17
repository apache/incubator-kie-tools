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

package org.uberfire.backend.server.util;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.uberfire.backend.vfs.FileSystem;
import org.uberfire.backend.vfs.FileSystemFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.java.nio.file.Files;

import static org.uberfire.backend.vfs.PathFactory.*;

public final class Paths {

    private static Map<org.uberfire.java.nio.file.FileSystem, FileSystem> cache = new HashMap<org.uberfire.java.nio.file.FileSystem, FileSystem>();

    public static Path convert( final org.uberfire.java.nio.file.Path path ) {
        if ( path == null ) {
            return null;
        }

        if ( path.getFileName() == null ) {
            return newPath( "/", path.toUri().toString(), new HashMap<String, Object>( 1 ) {{
                put( PathFactory.VERSION_PROPERTY, path.getFileSystem().supportedFileAttributeViews().contains( "version" ) );
            }} );
        }

        return newPath( path.getFileName().toString(), path.toUri().toString(), new HashMap<String, Object>( 1 ) {{
            put( PathFactory.VERSION_PROPERTY, path.getFileSystem().supportedFileAttributeViews().contains( "version" ) );
        }} );
    }

    public static org.uberfire.java.nio.file.Path convert( final Path path ) {
        if ( path == null ) {
            return null;
        }

        return org.uberfire.java.nio.file.Paths.get( URI.create( path.toURI() ) );
    }

    public static FileSystem convert( final org.uberfire.java.nio.file.FileSystem fs ) {
        if ( !cache.containsKey( fs ) ) {
            final Map<String, String> roots = new HashMap<String, String>();
            for ( final org.uberfire.java.nio.file.Path root : fs.getRootDirectories() ) {
                roots.put( root.toUri().toString(), root.getFileName() == null ? "/" : root.getFileName().toString() );
            }
            cache.put( fs, FileSystemFactory.newFS( roots, fs.supportedFileAttributeViews() ) );
        }

        return cache.get( fs );
    }
    
    public static String readLockedBy(final Path path) {
        org.uberfire.java.nio.file.Path lock = convert( PathFactory.newLock( path ) );
        if (!Files.exists( lock )) {
            return null;
        }
        else {
            return new String( Files.readAllBytes( lock ));
        }
    }
    
    public static boolean isLock( final Path path ) {
        return path.toURI().endsWith( PathFactory.LOCK_FILE_EXTENSION );
    }

}
