/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.server.util;

import org.uberfire.util.URIUtil;

public class FileServletUtil {

    public static String encodeFileNamePart( String path ) {
        //encode the file name part for a given path in the vfs uri format.
        if ( path == null ) {
            return null;
        } else {
            int index = path.lastIndexOf( "/" );
            StringBuilder builder = new StringBuilder(  );
            if ( index >= 0 ) {
                builder.append( path.substring( 0, index + 1 ) );
                if ( index < path.length() -1 ) {
                    builder.append( encodeFileName( path.substring( index+1, path.length() ) ) );
                }
            } else {
                builder.append( path );
            }
            return builder.toString();
        }
    }

    public static String encodeFileName( String fileName ) {
        return URIUtil.encode( fileName );
    }

}
