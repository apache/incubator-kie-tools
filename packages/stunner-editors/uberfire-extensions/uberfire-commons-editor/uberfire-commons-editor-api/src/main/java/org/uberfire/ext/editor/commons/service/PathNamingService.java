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

package org.uberfire.ext.editor.commons.service;

import org.uberfire.backend.vfs.Path;

/**
 * Utilities service for directories and files naming methods.
 */
public interface PathNamingService {

    /**
     * Creates a path by renaming a file or directory, keeping the original file extension (if applicable).
     * @param originalPath Original file path
     * @param targetFileName New file name
     * @return Path for the new file
     */
    Path buildTargetPath(Path originalPath,
                         String targetFileName);

    /**
     * Creates a path to a file or directory in another parent directory, keeping the original file extension (if applicable).
     * @param originalPath Original file path
     * @param targetParentDirectory Parent directory of the created file/directory path
     * @param targetFileName New file name
     * @return Path for the new file
     */
    Path buildTargetPath(Path originalPath,
                         Path targetParentDirectory,
                         String targetFileName);

    /**
     * Returns the extension of the passed file name.
     * @param fileName File name with extension
     * @return The file name extension
     */
    String getExtension(final String fileName);
}
