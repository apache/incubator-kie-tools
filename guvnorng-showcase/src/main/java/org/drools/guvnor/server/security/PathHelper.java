/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.security;

public class PathHelper {
    public static boolean isSubPath(String parentPath,
                                    String subPath) {
        parentPath = (parentPath.startsWith( "/" )) ? parentPath.substring( 1 ) : parentPath;
        subPath = (subPath.startsWith( "/" )) ? subPath.substring( 1 ) : subPath;
        String[] parentTags = parentPath.split( "/" );
        String[] subTags = subPath.split( "/" );
        if ( parentTags.length > subTags.length ) {
            return false;
        }
        for ( int i = 0; i < parentTags.length; i++ ) {
            if ( !parentTags[i].equals( subTags[i] ) ) {
                return false;
            }
        }

        return true;
    }
}
