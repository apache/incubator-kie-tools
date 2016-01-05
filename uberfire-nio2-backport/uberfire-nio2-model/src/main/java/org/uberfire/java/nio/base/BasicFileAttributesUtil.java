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

package org.uberfire.java.nio.base;

import java.util.HashMap;
import java.util.Map;

import static org.uberfire.java.nio.base.AbstractBasicFileAttributeView.*;

public class BasicFileAttributesUtil {

    public static Map<String, Object> cleanup( final Map<String, Object> _attrs ) {
        final Map<String, Object> attrs = new HashMap<String, Object>( _attrs );

        for ( final String key : _attrs.keySet() ) {
            if ( key.startsWith( IS_REGULAR_FILE ) || key.startsWith( IS_DIRECTORY ) ||
                    key.startsWith( IS_SYMBOLIC_LINK ) || key.startsWith( SIZE ) ||
                    key.startsWith( FILE_KEY ) || key.startsWith( IS_OTHER ) ||
                    key.startsWith( LAST_MODIFIED_TIME ) || key.startsWith( LAST_ACCESS_TIME ) ||
                    key.startsWith( CREATION_TIME ) ) {
                attrs.put( key, null );
            }
        }

        return attrs;
    }

}
