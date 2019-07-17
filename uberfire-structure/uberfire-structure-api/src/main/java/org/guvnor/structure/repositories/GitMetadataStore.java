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

package org.guvnor.structure.repositories;

import java.util.Optional;

public interface GitMetadataStore {

    /**
     * Writes the meta information about a repository without an origin.
     * @param name The name of the repository.
     */
    void write(String name);

    /**
     * Writes the meta information about a repository and its origin.
     * The origin must be in organizationalUnit/repositoryName format
     * @param name The name of the repository
     * @param origin The name of the origin in organizationalUnit/repositoryName format
     */
    void write(String name,
               String origin);

    /**
     * Writes the meta information about a repository and its origin.
     * The origin must be in organizationalUnit/repositoryName format
     * @param name The name of the repository
     * @param origin The name of the origin in organizationalUnit/repositoryName format
     * @param lock Determines if the FileSystem should be locked or not
     */
    void write(String name,
               String origin,
               boolean lock);

    /**
     * Writes the meta information about a repository.
     * @param name The name of the repository
     * @param metadata The metadata object that stores information about repository
     */
    void write(String name,
               GitMetadata metadata);

    /**
     * Writes the meta information about a repository.
     * @param name The name of the repository
     * @param metadata The metadata object that stores information about repository
     * @param lock Determines if the FileSystem should be locked or not
     */
    void write(String name,
               GitMetadata metadata,
               boolean lock);

    /**
     * Reads the git metadata from repository.
     * @param name the repository name
     * @return
     */
    Optional<GitMetadata> read(String name);

    /**
     * Deletes that repository meta information and removes its reference from the origin and forks.
     * @param name The repository name
     */
    void delete(String name);
}
