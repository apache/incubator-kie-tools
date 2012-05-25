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

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.ServiceConfigurationError;

import org.drools.java.nio.file.api.FileSystemProviders;

import static java.util.Collections.*;
import static org.drools.java.nio.util.Preconditions.*;

/**
 * Back port of JSR-203 from Java Platform, Standard Edition 7.
 * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystems.html">Original JavaDoc</a>
 */
public final class FileSystems {

    private FileSystems() {
    }

    // for lazy init
    private static class DefaultFileSystemHolder {

        static final FileSystem defaultFileSystem = getDefaultFileSystem();

        private static FileSystem getDefaultFileSystem() {
            return FileSystemProviders.getDefaultProvider().getFileSystem(URI.create("default:///"));
        }
    }

    /**
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystems.html#getDefault()">Original JavaDoc</a>
     */
    public static FileSystem getDefault() {
        return DefaultFileSystemHolder.defaultFileSystem;
    }

    /**
     * @throws IllegalArgumentException
     * @throws FileSystemNotFoundException
     * @throws ProviderNotFoundException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystems.html#getFileSystem(java.net.URI)">Original JavaDoc</a>
     */
    public static FileSystem getFileSystem(final URI uri)
            throws IllegalArgumentException, FileSystemNotFoundException, ProviderNotFoundException, SecurityException {
        checkNotNull("uri", uri);

        return FileSystemProviders.resolveProvider(uri).getFileSystem(uri);
    }

    /**
     * @throws IllegalArgumentException
     * @throws FileSystemAlreadyExistsException
     * @throws ProviderNotFoundException
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystems.html#newFileSystem(java.net.URI, java.util.Map)">Original JavaDoc</a>
     */
    public static FileSystem newFileSystem(final URI uri, final Map<String, ?> env)
            throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException,
            IOException, SecurityException {
        checkNotNull("uri", uri);

        return newFileSystem(uri, env, null);
    }

    /**
     * @throws IllegalArgumentException
     * @throws FileSystemAlreadyExistsException
     * @throws ProviderNotFoundException
     * @throws ServiceConfigurationError
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystems.html#newFileSystem(java.net.URI, java.util.Map, java.lang.ClassLoader)">Original JavaDoc</a>
     */
    public static FileSystem newFileSystem(final URI uri, final Map<String, ?> env, final ClassLoader loader)
            throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException,
            ServiceConfigurationError, IOException, SecurityException {
        checkNotNull("uri", uri);

        return FileSystemProviders.resolveProvider(uri).newFileSystem(uri, env);
    }

    /**
     * @throws IllegalArgumentException
     * @throws ProviderNotFoundException
     * @throws ServiceConfigurationError
     * @throws IOException
     * @throws SecurityException
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystems.html#newFileSystem(java.nio.file.Path, java.lang.ClassLoader)">Original JavaDoc</a>
     */
    public static FileSystem newFileSystem(final Path path, final ClassLoader loader)
            throws IllegalArgumentException, ProviderNotFoundException, ServiceConfigurationError, IOException, SecurityException {
        checkNotNull("path", path);

        final Map<String, ?> env = emptyMap();
        return FileSystemProviders.resolveProvider(path.toUri()).newFileSystem(path, env);
    }
}