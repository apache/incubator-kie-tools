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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.util;

import com.google.gwt.regexp.shared.RegExp;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;

public class ValuePairEditorUtil {

    private static final RegExp hexaCharsExp = RegExp.compile( "[0-9a-fA-F]" );

    public static String buildValuePairLabel( AnnotationValuePairDefinition valuePairDefinition ) {
        return valuePairDefinition.getName();
    }

    public static boolean validate( String value, NumberType numberType ) {
        try {
            parseNumberValue( value, numberType );
        } catch ( Exception e ) {
            return false;
        }
        return true;
    }

    public static NumberType getNumberType( AnnotationValuePairDefinition valuePairDefinition ) {

        String clazz = valuePairDefinition.getClassName();

        if ( valuePairDefinition.isPrimitiveType() ) {
            if ( "byte".equals( clazz ) || Byte.class.getName().equals( clazz ) ) {
                return NumberType.BYTE;
            } else if ( "short".equals( clazz ) || Short.class.getName().equals( clazz ) ) {
                return NumberType.SHORT;
            } else if ( "int".equals( clazz ) || Integer.class.getName().equals( clazz ) ) {
                return NumberType.INT;
            } else if ( "long".equals( clazz ) || Long.class.getName().equals( clazz ) ) {
                return NumberType.LONG;
            } else if ( "float".equals( clazz ) || Float.class.getName().equals( clazz ) ) {
                return NumberType.FLOAT;
            } else if ( "double".equals( clazz ) || Double.class.getName().equals( clazz ) ) {
                return NumberType.DOUBLE;
            }
        }
        return null;
    }

    public static boolean isNumberType( AnnotationValuePairDefinition valuePairDefinition ) {
        return getNumberType( valuePairDefinition ) != null;
    }

    public static Object parseNumberValue( String value, NumberType numberType ) throws NumberFormatException {

        try {
            switch ( numberType ) {
                case BYTE:
                    return Byte.parseByte( value );
                case SHORT:
                    return Short.parseShort( value );
                case INT:
                    return Integer.parseInt( value );
                case LONG:
                    return Long.parseLong( value );
                case FLOAT:
                    return Float.parseFloat( value );
                case DOUBLE:
                    return Double.parseDouble( value );

            }
        } catch ( Exception e ) {
            throw new NumberFormatException( "Invalid " + numberType + " value." );
        }
        throw new NumberFormatException( "Unknown NumberType: " + numberType );
    }

    public static boolean isValidCharacterLiteral( String charLiteralStr ) {

        if ( charLiteralStr == null || charLiteralStr.length() == 0 ) {
            //Window.alert("caso1");
            return false;
        } else if ( charLiteralStr.length() == 1 && charLiteralStr.charAt( 0 ) == ' ' ) {
            //Window.alert("caso2");
            return true;
        }
        charLiteralStr = charLiteralStr.trim();
        if ( charLiteralStr.length() == 0 ) {
            //Window.alert("caso3");
            return false;
        }

        if ( charLiteralStr.length() == 1 && charLiteralStr.charAt( 0 ) != '\\' &&
                charLiteralStr.charAt( 0 ) != '\"' &&
                charLiteralStr.charAt( 0 ) != '\'' ) {
            //Window.alert("caso4");
            return true;
        }

        if ( charLiteralStr.length() == 2 &&
                charLiteralStr.charAt( 0 ) == '\\' &&
                ( charLiteralStr.charAt( 1 ) == 't' ||
                    charLiteralStr.charAt( 1 ) == 'b' ||
                    charLiteralStr.charAt( 1 ) == 'n' ||
                    charLiteralStr.charAt( 1 ) == 'r' ||
                    charLiteralStr.charAt( 1 ) == 'f' ||
                    charLiteralStr.charAt( 1 ) == '\'' ||
                    charLiteralStr.charAt( 1 ) == '"' ||
                    charLiteralStr.charAt( 1 ) == '\\' )
                ) {
            //Window.alert("caso5");
            return true;
        }

        if ( charLiteralStr.length() == 6 &&
                charLiteralStr.charAt( 0 ) == '\\' &&
                charLiteralStr.charAt( 1 ) == 'u' && hasValidHexaDecimalChars( charLiteralStr, 2 ) ) {
            //Window.alert("caso6");
            return true;
        }
        //Window.alert("caso7");
        return false;
    }

    public static String unquoteCharacterLiteral( String charLiteralStr ) {
        if ( charLiteralStr != null &&
                charLiteralStr.length() >= 3 &&
                charLiteralStr.charAt( 0 ) == '\'' &&
                charLiteralStr.charAt( charLiteralStr.length() -1 ) == '\'' ) {
            return charLiteralStr.substring( 1, charLiteralStr.length() -1 );
        } else {
            return charLiteralStr;
        }
    }

    public static boolean hasValidHexaDecimalChars( String charLiteralStr, int start ) {
        for ( int i = start+1; i < charLiteralStr.length(); i++ ) {
            if ( !isValidHexaDecimalChar( charLiteralStr.charAt( i ) ) ) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidHexaDecimalChar( char hexaDecimalChar ) {
        return hexaCharsExp.test( new String( new char[]{hexaDecimalChar} ) );
    }

    public static boolean isBlankCharaterSequence( String charSequence ) {
        if ( charSequence == null || charSequence.length() == 0 ) {
            return false;
        }
        for ( int i = 0; i < charSequence.length(); i++ ) {
            if ( ' ' != charSequence.charAt( i ) ) {
                return false;
            }
        }
        return true;
    }
}
