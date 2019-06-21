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

package org.uberfire.backend.server.io.object;

import java.net.URI;

import org.uberfire.java.nio.file.Path;

/**
 * Object Storage definition
 */
public interface ObjectStorage {

    /**
     * Initialize the object storage.
     * @param rootURI The root URI where is going to create the file system
     */
    void init(URI rootURI);

    /**
     * Checks if a path exists into the object storage.
     * @param path the path to the stored object.
     * @return true if exists that path into the storage else false.
     */
    boolean exists(String path);

    /**
     * Read the content of the path given
     * @param path The path where the content is stored
     * @return The content of the file unmarshalled
     */
    <T> T read(String path);

    /**
     * Write an object into the object storage. That object is marshalled into JSON. Lock will be enabled before write
     * and disabled after it.
     * @param path The path where the object is going to be written
     * @param value The object itself
     */
    <T> void write(String path,
                   T value);

    /**
     * Write an object into the object storage. That object is marshalled into JSON. You can avoid FS lock
     * @param path The path where the object is going to be written
     * @param value The object itself
     * @param lock If the object storage should be locked while writing
     */
    <T> void write(String path,
                   T value,
                   boolean lock);

    /**
     * Delete a path from the object storage if exists. That path contains the object stored.
     * @param path The path to delete
     */
    void delete(String path);

    /**
     * Return a path based on the filesystem that is implemented for storage
     * @param first the path string or initial part of the path string
     * @param path additional strings to be joined to form the path string
     * @return the path object that represents the path.
     */
    Path getPath(String first,
                 String... path);

    void close();
}
