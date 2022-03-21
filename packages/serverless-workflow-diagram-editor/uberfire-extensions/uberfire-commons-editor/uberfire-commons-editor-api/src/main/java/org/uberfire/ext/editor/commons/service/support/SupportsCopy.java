/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.editor.commons.service.support;

import org.uberfire.backend.vfs.Path;

/**
 * Describes a service that supports the copy operation.
 */
public interface SupportsCopy {

    /**
     * Copies a file or directory to the same parent directory, with a new name.
     * @param path Original file or directory
     * @param newName Name of the new file or directory
     * @param comment Comment for the copy operation
     * @return The path to the new file.
     */
    Path copy(final Path path,
              final String newName,
              final String comment);

    /**
     * Copies a file or directory to a specific target directory, with a new name.
     * @param path Original file or directory
     * @param newName Name of the new file or directory
     * @param targetDirectory Directory in which the new file will be written
     * @param comment Comment for the copy operation
     * @return The path to the new file.
     */
    Path copy(final Path path,
              final String newName,
              final Path targetDirectory,
              final String comment);
}
