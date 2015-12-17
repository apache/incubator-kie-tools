/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodeller.util;

public class PortableStringUtils {

    public static String removeFirstChar( String str, char c ) {
        if ( str == null || str.length() == 0 ) return str;
        if ( str.charAt( 0 ) == c )  return str.length() == 1 ? "" : str.substring( 1, str.length() );
        return str;
    }

    public static String removeLastChar( String str, char c ) {
        if ( str == null || str.length() == 0 ) return str;
        if ( str.charAt( str.length() -1 ) == c ) return str.length() == 1 ? "" : str.substring( 0, str.length() -1 );
        return str;
    }

}
