/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.services.datamodeller.driver.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.jboss.forge.roaster.model.ValuePair;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.driver.AnnotationDriver;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverException;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;
import org.kie.workbench.common.services.datamodeller.util.StringEscapeUtils;

public class DefaultJavaRoasterModelAnnotationDriver implements AnnotationDriver {

    @Override
    public Annotation buildAnnotation( AnnotationDefinition annotationDefinition, Object annotationToken ) throws ModelDriverException {

        AnnotationSource javaAnnotationToken = ( AnnotationSource ) annotationToken;
        AnnotationImpl annotation = new AnnotationImpl( annotationDefinition );
        if ( annotationDefinition.isMarker() ) {
            return annotation;
        } else {
            if ( javaAnnotationToken.getValues() != null ) {
                List<ValuePair> values = javaAnnotationToken.getValues();

                if ( values != null && values.size() > 0 ) {
                    for ( AnnotationValuePairDefinition valuePairDefinition : annotationDefinition.getValuePairs() ) {
                        Object annotationValue = buildAnnotationValue( javaAnnotationToken, valuePairDefinition );
                        if ( annotationValue != null ) {
                            annotation.setValue( valuePairDefinition.getName(), annotationValue );
                        }
                    }
                }
            }
        }
        return annotation;
    }

    private Object buildAnnotationValue( AnnotationSource javaAnnotationToken, AnnotationValuePairDefinition valuePairDefinition ) throws ModelDriverException {
        Object result = null;
        if ( javaAnnotationToken.getLiteralValue( valuePairDefinition.getName() ) != null ) {
            //there's a value
            if ( valuePairDefinition.isPrimitiveType() ) {
                result = parsePrimitiveValue( javaAnnotationToken, valuePairDefinition );
            } else if ( valuePairDefinition.isEnum() ) {
                result = parseEnumValue( javaAnnotationToken, valuePairDefinition );
            } else if ( valuePairDefinition.isString() ) {
                result = parseStringValue( javaAnnotationToken, valuePairDefinition );
            } else if ( valuePairDefinition.isClass() ) {
                result = parseClassValue( javaAnnotationToken, valuePairDefinition );
            } else if ( valuePairDefinition.isAnnotation() ) {
                result = parseAnnotationValue( javaAnnotationToken, valuePairDefinition );
            }
        }
        return result;
    }

    private Object parsePrimitiveValue( AnnotationSource javaAnnotationToken, AnnotationValuePairDefinition valuePairDefinition ) {
        String value = parseLiteralValue( javaAnnotationToken.getLiteralValue( valuePairDefinition.getName() ) );
        Object result;

        if ( value == null ) {
            return null;
        }

        if ( valuePairDefinition.isArray() ) {
            //TODO parse primitive arrays. Not implemented in Roaster.
            result = value;
        } else {
            result = parsePrimitiveValue( value, valuePairDefinition.getClassName(), valuePairDefinition.isArray() );
        }
        return result;
    }

    private Object parseEnumValue( AnnotationSource javaAnnotationToken, AnnotationValuePairDefinition valuePairDefinition ) {
        String value = parseLiteralValue( javaAnnotationToken.getLiteralValue( valuePairDefinition.getName() ) );
        Object result;

        if ( value == null ) {
            return null;
        }

        if ( valuePairDefinition.isArray() ) {
            //TODO parse enum arrays. If we assume the Enum.class is accessible to current classloader then Roaster can do it.
            result = value;
        } else {
            result = parseEnumValue( value, valuePairDefinition );
        }

        return result;
    }

    private Object parseStringValue( AnnotationSource javaAnnotationToken, AnnotationValuePairDefinition valuePairDefinition ) {
        Object result = null;

        if ( valuePairDefinition.isArray() ) {
            String[] arrayValue = javaAnnotationToken.getStringArrayValue( valuePairDefinition.getName() );
            if ( arrayValue != null ) {
                result = Arrays.asList( arrayValue );
            }
        } else {
            result = javaAnnotationToken.getStringValue( valuePairDefinition.getName() );
        }

        return result;
    }

    private Object parseClassValue( AnnotationSource javaAnnotationToken, AnnotationValuePairDefinition valuePairDefinition ) {
        String value = parseLiteralValue( javaAnnotationToken.getLiteralValue( valuePairDefinition.getName() ) );
        Object result;

        if ( value == null ) {
            return null;
        }

        if ( valuePairDefinition.isArray() ) {
            //TODO parse class arrays. If we assume the Class.class is accessible to current classloader then Roaster can do it.
            result = value;
        } else {
            result = value;
        }
        return result;
    }

    private Object parseAnnotationValue( AnnotationSource javaAnnotationToken, AnnotationValuePairDefinition valuePairDefinition ) throws ModelDriverException {
        String value = javaAnnotationToken.getLiteralValue( valuePairDefinition.getName() );
        AnnotationDefinition annotationDefinition = valuePairDefinition.getAnnotationDefinition();
        Object result = null;

        if ( value == null ) return null;

        if ( annotationDefinition == null ) {
            return value;
        }

        if ( valuePairDefinition.isArray() ) {
            AnnotationSource[] javaAnnotationTokenValueArray = javaAnnotationToken.getAnnotationArrayValue( valuePairDefinition.getName() );
            List<Annotation> annotationList = new ArrayList<Annotation>();
            Annotation annotation;
            if ( javaAnnotationTokenValueArray != null ) {
                for ( int i = 0; i < javaAnnotationTokenValueArray.length; i++ ) {
                    annotation = buildAnnotation( annotationDefinition, javaAnnotationTokenValueArray[ i ] );
                    if ( annotation != null ) {
                        annotationList.add( annotation );
                    }
                }
            }
            result = annotationList.size() > 0 ? annotationList : null;
        } else {
            AnnotationSource javaAnnotationTokenValue = javaAnnotationToken.getAnnotationValue( valuePairDefinition.getName() );
            if ( javaAnnotationTokenValue != null ) {
                result = buildAnnotation( annotationDefinition, javaAnnotationTokenValue );
            }
        }

        return result;
    }

    private Object parsePrimitiveValue( String value, String className, boolean isArray ) {
        if ( NamingUtils.isByteId( className ) ) {
            return parseByteValue( value, className, isArray );
        } else if ( NamingUtils.isCharId( className ) ) {
            return parseCharValue( value, className, isArray );
        } else {
            return NamingUtils.parsePrimitiveValue( className, value );
        }
    }

    private Object parseByteValue( String value, String className, boolean isArray ) {
        //remove the word (byte) in case the value is something like (byte)222"
        String regex = "(\\s)*\\((\\s)*byte(\\s)*\\)(\\s)*";
        Pattern pattern = Pattern.compile( regex );
        String[] splits = pattern.split( value );
        Object result = null;
        try {
            if ( splits.length == 0 ) {
                result = NamingUtils.parsePrimitiveValue( className, value );
            } else if ( splits.length == 1 ) {
                result = NamingUtils.parsePrimitiveValue( className, splits[ 0 ] );
            } else if ( splits.length == 2 ) {
                result = NamingUtils.parsePrimitiveValue( className, splits[ 1 ] );
            } else {
                result = NamingUtils.parsePrimitiveValue( className, value );
            }
        } catch ( NumberFormatException e ) {
            result = value;
        }

        return result;
    }

    private Object parseCharValue( String value, String className, boolean isArray ) {
        String unquotedValue = StringEscapeUtils.unquoteSingle( value );
        return NamingUtils.parsePrimitiveValue( className, unquotedValue );
    }

    private Object parseEnumValue( String value, AnnotationValuePairDefinition valuePairDefinition ) {
        String[] enumValues = valuePairDefinition.enumValues();
        String result = value;
        if ( value != null && enumValues != null ) {
            for ( int i = 0; i < enumValues.length; i++ ) {
                if ( value.endsWith( enumValues[ i ] ) ) {
                    result = enumValues[ i ];
                    break;
                }
            }
        }
        return result;
    }

    private String parseLiteralValue( String literalValue ) {
        return literalValue != null ? StringEscapeUtils.unquote( StringEscapeUtils.unescapeJava( literalValue ) ) : literalValue;
    }

}
