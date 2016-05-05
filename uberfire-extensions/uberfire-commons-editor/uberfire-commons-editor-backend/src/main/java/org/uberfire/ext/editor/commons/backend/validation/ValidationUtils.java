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

package org.uberfire.ext.editor.commons.backend.validation;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

public class ValidationUtils {

    private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };

    public static boolean isFileName( final String value ) {
        //Null check
        if ( StringUtils.isBlank( value ) ) {
            return false;
        }

        //Prefix and suffix "." causes issues
        if ( value.startsWith( "." ) || value.endsWith( "." ) ) {
            return false;
        }

        //Illegal character check
        for ( Character c : ILLEGAL_CHARACTERS ) {
            if ( value.contains( c.toString() ) ) {
                return false;
            }
        }

        final File f = new File( value );
        try {
            f.getCanonicalPath();
            return true;
        } catch ( IOException e ) {
            return false;
        }

    }
}
