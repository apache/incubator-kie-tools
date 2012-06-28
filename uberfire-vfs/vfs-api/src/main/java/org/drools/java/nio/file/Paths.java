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

package org.drools.java.nio.file;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.drools.java.nio.file.api.FileSystemProviders;

import static org.drools.java.nio.util.Preconditions.*;

/**
 * Back port of JSR-203 from Java Platform, Standard Edition 7.
 * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Paths.html">Original JavaDoc</a>
 */
public final class Paths {

    private Paths() {
    }

    /**
     * @throws IllegalArgumentException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Paths.html#get(java.lang.String, java.lang.String...)">JDK JavaDoc</a>
     */
    public static Path get(final String first, final String... more)
            throws IllegalArgumentException {
        checkNotNull("first", first);

        if (first.trim().length() == 0) {
            return FileSystems.getDefault().getPath(first);
        }

        URI uri = null;
        if (more == null || more.length == 0) {
            try {
                uri = new URI(first);
            } catch (URISyntaxException ex) {
                try {
                    uri = URI.create(first);
                } catch (IllegalArgumentException e) {
                    uri = null;
                }
            }
        }
        if (uri != null) {
            try {
                return get(uri);
            } catch (Exception ex) {
            }
        }
        return FileSystems.getDefault().getPath(first, more);
    }

    /**
     * @throws IllegalArgumentException
     * @throws FileSystemNotFoundException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/Paths.html#get(java.net.URI)">JDK JavaDoc</a>
     */
    public static Path get(final URI uri)
            throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        checkNotNull("uri", uri);

        return FileSystemProviders.resolveProvider(uri).getPath(uri);
    }
}
