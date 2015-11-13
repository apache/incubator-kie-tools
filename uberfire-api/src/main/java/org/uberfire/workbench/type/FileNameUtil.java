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
package org.uberfire.workbench.type;

import org.uberfire.backend.vfs.Path;

public final class FileNameUtil {

    public static String removeExtension( final Path path,
                                          final ResourceTypeDefinition type ) {
        if ( path == null ) {
            return null;
        }
        final String fileName = path.getFileName();
        if ( type == null ) {
            return fileName;
        }
        final int index = indexOfExtension( type,
                                            fileName );
        if ( index == -1 ) {
            return fileName;
        } else {
            return fileName.substring( 0,
                                       index );
        }
    }

    private static int indexOfExtension( final ResourceTypeDefinition type,
                                         final String fileName ) {
        if ( fileName == null ) {
            return -1;
        }
        final String suffix = ( type.getSuffix() == null ? "" : type.getSuffix() );
        final int extensionPos = fileName.lastIndexOf( "." + suffix );
        return extensionPos;
    }

}
