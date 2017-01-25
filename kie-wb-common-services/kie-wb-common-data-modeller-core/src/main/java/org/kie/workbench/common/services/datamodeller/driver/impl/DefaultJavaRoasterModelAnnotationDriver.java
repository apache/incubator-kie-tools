/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.jboss.forge.roaster.model.ValuePair;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.driver.AnnotationDriver;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverException;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;
import org.kie.workbench.common.services.datamodeller.util.PortableStringUtils;
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
            result = parsePrimitiveArrayValue( value, valuePairDefinition.getClassName(), valuePairDefinition );
        } else {
            result = parsePrimitiveValue( value, valuePairDefinition.getClassName() );
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
            result = parseEnumArrayValue( value, valuePairDefinition );
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
        String value = null;
        Object result;
        List<ValuePair> values = javaAnnotationToken.getValues();
        if ( values != null ) {
            Optional<ValuePair> valuePair = values.stream().filter(
                    vp -> valuePairDefinition.getName().equals( vp.getName() ) ).findFirst( );
            value = valuePair.map( vp -> vp.getLiteralValue() ).orElse( null );
        }
        if ( value == null ) {
            return null;
        }

        if ( valuePairDefinition.isArray() ) {
            result = parseClassArrayValue( value );
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

    private Object parsePrimitiveValue( String value, String className ) {
        if ( NamingUtils.isByteId( className ) ) {
            return parseByteValue( value, className );
        } else if ( NamingUtils.isCharId( className ) ) {
            return parseCharValue( value, className );
        } else {
            return NamingUtils.parsePrimitiveValue( className, value );
        }
    }

    private List<Object> parsePrimitiveArrayValue( String value, String className, AnnotationValuePairDefinition valuePairDefinition ) {
        if ( value == null ) return null;
        List<Object> values = new ArrayList<Object>(  );
        value = value.trim();
        if ( !value.startsWith( "{" ) || !value.endsWith( "}" ) ) {
            //mal formed array
            return values;
        } else if ( DriverUtils.isEmptyArray( value ) ) {
            return values;
        } else {
            value = PortableStringUtils.removeLastChar( PortableStringUtils.removeFirstChar( value, '{' ), '}' );
            String[] primitiveValues = value.split( "," );
            Object primitiveValue;
            for ( int i = 0; i < primitiveValues.length; i++ ) {
                primitiveValue = parsePrimitiveValue( primitiveValues[i], className );
                values.add( primitiveValue );
            }
        }
        return values;
    }


    private Object parseByteValue( String value, String className ) {
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

    private Object parseCharValue( String value, String className ) {
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

    private List<Object> parseEnumArrayValue( String value, AnnotationValuePairDefinition valuePairDefinition ) {
        if ( value == null ) return null;
        List<Object> values = new ArrayList<Object>(  );
        value = value.trim();
        if ( !value.startsWith( "{" ) || !value.endsWith( "}" ) ) {
            //mal formed array
            return values;
        } else if ( DriverUtils.isEmptyArray( value ) ) {
            return values;
        } else {
            value = PortableStringUtils.removeLastChar( PortableStringUtils.removeFirstChar( value, '{' ), '}' );
            String[] enumValues = value.split( "," );
            Object enumValue;
            for ( int i = 0; i < enumValues.length; i++ ) {
                enumValue = parseEnumValue( enumValues[i], valuePairDefinition );
                values.add( enumValue );
            }
        }
        return values;
    }

    private List<Object> parseClassArrayValue( String value ) {
        if ( value == null ) return null;
        List<Object> values = new ArrayList<Object>(  );
        value = value.trim();
        if ( !value.startsWith( "{" ) || !value.endsWith( "}" ) ) {
            //mal formed array
            return values;
        } else if ( DriverUtils.isEmptyArray( value ) ) {
            return values;
        } else {
            value = PortableStringUtils.removeLastChar( PortableStringUtils.removeFirstChar( value, '{' ), '}' );
            String[] classValues = value.split( "," );
            Object classValue;
            for ( int i = 0; i < classValues.length; i++ ) {
                classValue = parseClassValue( classValues[i] );
                if ( classValue != null ) {
                    values.add( classValue );
                }
            }
        }
        return values;
    }

    private String parseClassValue( String classValue ) {
        return classValue != null ? classValue.trim() : null;
    }

    private boolean isValidClassValue( String value ) {
        String classValue = value != null ? value.trim() : value;
        return classValue != null && classValue.length() > ".class".length() && classValue.endsWith( ".class" );
    }

    private String parseLiteralValue( String literalValue ) {
        return literalValue; //literalValue != null ? StringEscapeUtils.unquote( StringEscapeUtils.unescapeJava( literalValue ) ) : literalValue;
    }

}
