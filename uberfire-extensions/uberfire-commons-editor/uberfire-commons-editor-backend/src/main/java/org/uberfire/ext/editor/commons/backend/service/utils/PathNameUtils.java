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

package org.uberfire.ext.editor.commons.backend.service.utils;

import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

/**
 * Utilities class for directories and files naming methods.
 */
public class PathNameUtils {

    /**
     * Renames a file or directory, keeping the original file extension.
     * @param originalPath Original file path
     * @param targetFileName New file name
     * @return Path for the new file
     */
    public static Path buildTargetPath( Path originalPath,
                                        String targetFileName ) {
        if ( Files.isDirectory( originalPath ) ) {
            return originalPath.resolveSibling( targetFileName );
        }

        final String originalFileName = originalPath.getFileName().toString();
        final int extensionIndex = originalFileName.indexOf( "." );
        String extension = "";

        if ( extensionIndex >= 0 ) {
            extension = originalFileName.substring( extensionIndex );
        }

        return originalPath.resolveSibling( targetFileName + extension );
    }
}
