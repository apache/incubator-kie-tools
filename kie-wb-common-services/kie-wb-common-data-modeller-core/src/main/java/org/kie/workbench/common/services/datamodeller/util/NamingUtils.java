/**
 * Copyright 2012 JBoss Inc
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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class NamingUtils {

    public static final String BYTE = "byte";
    public static final String SHORT = "short";
    public static final String INT = "int";
    public static final String LONG = "long";
    public static final String FLOAT = "float";
    public static final String DOUBLE = "double";
    public static final String CHAR = "char";
    public static final String BOOLEAN = "boolean";

    public static String extractClassName( final String fullClassName ) {

        if ( fullClassName == null ) {
            return null;
        }
        int index = fullClassName.lastIndexOf( "." );
        if ( index > 0 ) {
            return fullClassName.substring( index + 1, fullClassName.length() );

        } else {
            return fullClassName;
        }
    }

    public static String extractPackageName( final String fullClassName ) {
        if ( fullClassName == null ) {
            return null;
        }
        int index = fullClassName.lastIndexOf( "." );
        if ( index > 0 ) {
            return fullClassName.substring( 0, index );

        } else {
            return null;
        }
    }

    public static List<String> tokenizePackageName( final String packageName ) {
        List<String> tokens = new ArrayList<String>();

        if ( packageName != null ) {
            StringTokenizer st = new StringTokenizer( packageName, "." );
            while ( st.hasMoreTokens() ) {
                tokens.add( st.nextToken() );
            }
        }
        return tokens;
    }

    public static boolean isPrimitiveTypeClass( final String className ) {
        //returns true for: byte, short, int, long, float, double, char, boolean

        return
                Byte.class.getName().equals( className ) ||
                        Short.class.getName().equals( className ) ||
                        Integer.class.getName().equals( className ) ||
                        Long.class.getName().equals( className ) ||
                        Float.class.getName().equals( className ) ||
                        Double.class.getName().equals( className ) ||
                        Character.class.getName().equals( className ) ||
                        Boolean.class.getName().equals( className );
    }

    public static boolean isPrimitiveTypeId( final String type ) {
        //returns true for: byte, short, int, long, float, double, char, boolean
        return
                BYTE.equals( type ) ||
                        SHORT.equals( type ) ||
                        INT.equals( type ) ||
                        LONG.equals( type ) ||
                        FLOAT.equals( type ) ||
                        DOUBLE.equals( type ) ||
                        CHAR.equals( type ) ||
                        BOOLEAN.equals( type );
    }

    public static String getClassForPrimitiveTypeId( final String type ) {

        if ( BYTE.equals( type ) ) {
            return Byte.class.getName();
        }
        if ( SHORT.equals( type ) ) {
            return Short.class.getName();
        }
        if ( INT.equals( type ) ) {
            return Integer.class.getName();
        }
        if ( LONG.equals( type ) ) {
            return Long.class.getName();
        }
        if ( FLOAT.equals( type ) ) {
            return Float.class.getName();
        }
        if ( DOUBLE.equals( type ) ) {
            return Double.class.getName();
        }
        if ( CHAR.equals( type ) ) {
            return Character.class.getName();
        }
        if ( BOOLEAN.equals( type ) ) {
            return Boolean.class.getName();
        }

        return null;
    }

    public static boolean isQualified( final String type ) {
        String[] tokens = tokenizeClassName( type );
        return ( tokens != null ) && ( tokens.length > 1 );
    }

    public static String[] tokenizeClassName( final String className ) {
        String[] result = null;
        if ( className != null ) {
            result = className.split( "\\." );
        }
        return result;
    }

}