/*
 * Copyright 2015 JBoss Inc
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

import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;

public class ValuePairEditorUtil {

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
                //TODO check if we want to manage byte as numeric in the context of value pair editors.
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
}
