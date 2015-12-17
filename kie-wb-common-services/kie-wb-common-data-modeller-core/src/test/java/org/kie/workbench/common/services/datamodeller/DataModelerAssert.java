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

package org.kie.workbench.common.services.datamodeller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;

import static org.junit.Assert.*;

public class DataModelerAssert {

    public static void assertEqualsDataObject( DataObject obj1, DataObject obj2 ) {
        if ( obj1 != null ) {
            assertNotNull( obj2 );

            assertEquals( obj1.getPackageName(), obj2.getPackageName() );
            assertEquals( obj1.getName(), obj2.getName() );
            assertEqualsAnnotations( obj1.getAnnotations(), obj2.getAnnotations() );
            assertEqualsProperties( obj1.getProperties(), obj2.getProperties() );

        } else {
            assertNull( obj2 );
        }
    }

    public static void assertEqualsProperties( List<ObjectProperty> properties1, List<ObjectProperty> properties2 ) {
        if ( properties1 != null ) {

            assertNotNull( properties2 );
            assertEquals( properties1.size(), properties2.size() );

            for ( int i = 0 ; i < properties1.size() ; i++ ) {
                assertEqualsProperty( properties1.get( i ), properties2.get(i ) );
            }
        }
    }

    public static void assertEqualsProperty( ObjectProperty property1, ObjectProperty property2 ) {
        if ( property1 != null ) {
            assertNotNull( property2 );
            assertEquals( property1.getName(), property2.getName() );
            assertEquals( property1.getClassName(), property2.getClassName() );
            assertEquals( property1.isMultiple(), property2.isMultiple() );
            if ( property1.isMultiple() ) {
                assertEquals( property1.getBag(), property2.getBag() );
            }
            assertEqualsAnnotations( property1.getAnnotations(), property2.getAnnotations() );
        } else {
            assertNull( property2 );
        }
    }

    public static void assertEqualsAnnotations( List<Annotation> annotations1, List<Annotation> annotations2 ) {
        if ( annotations1 != null ) {
            assertNotNull( annotations2 );
            assertEquals( annotations1.size(), annotations2.size() );

            Map<String, Annotation> annotationMap = new HashMap<String, Annotation>();
            for ( Annotation annotation : annotations2 ) {
                annotationMap.put( annotation.getClassName(), annotation );
            }

            for ( Annotation annotation : annotations1 ) {
                assertEqualsAnnotation( annotation, annotationMap.get( annotation.getClassName() ) );
            }
        } else {
            assertNull( annotations2 );
        }
    }

    public static void assertEqualsAnnotation( Annotation annotation1, Annotation annotation2 ) {
        if ( annotation1 != null ) {
            assertNotNull( annotation2 );

            assertEquals( annotation1.getClassName(), annotation2.getClassName() );
            assertEquals( annotation1.getValues().size(), annotation2.getValues().size() );
            assertEqualsAnnotationDefinition( annotation1.getAnnotationDefinition(), annotation2.getAnnotationDefinition() );
            for ( String annotationKey : annotation1.getValues().keySet() ) {
                if ( (annotation1.getValue( annotationKey ) instanceof List ) && isAnnotationList((List)annotation1.getValue( annotationKey )) &&
                     (annotation2.getValue( annotationKey ) instanceof List ) && isAnnotationList((List)annotation2.getValue( annotationKey )) ) {
                    assertEqualsAnnotationList( (List) annotation1.getValue( annotationKey ), (List) annotation2.getValue( annotationKey )  );
                } else if ( annotation1.getValue( annotationKey ) instanceof Annotation &&
                            annotation2.getValue( annotationKey ) instanceof Annotation ) {
                    assertEqualsAnnotation( (Annotation) annotation1.getValue( annotationKey ), (Annotation) annotation2.getValue( annotationKey ) );
                } else {
                    assertEquals( annotation1.getValues().get( annotationKey ), annotation2.getValues().get( annotationKey ) );
                }
            }
        } else {
            assertNull( annotation2 );
        }
    }

    private static boolean isAnnotationList( List<?> list ) {
        return list.size() > 0 && ( list.get( 0 ) instanceof Annotation );
    }

    private static void assertEqualsAnnotationList( List annotations1, List annotations2 ) {
        if ( annotations1 != null ) {
            assertNotNull( annotations2 );
            assertEquals( annotations1.size(), annotations2.size() );
            for ( int i = 0; i < annotations1.size(); i++ ) {
                assertEqualsAnnotation( ( Annotation ) annotations1.get( i ), ( Annotation ) annotations2.get( i ) );
            }
        } else {
            assertNull( annotations2 );
        }
    }

    private static void assertEqualsAnnotationDefinition( AnnotationDefinition annotationDefinition1, AnnotationDefinition annotationDefinition2 ) {
        if ( annotationDefinition1 != null ) {
            assertNotNull( annotationDefinition2 );

            assertEquals( annotationDefinition1.getClassName(),annotationDefinition2.getClassName() );
            assertEquals( annotationDefinition1.isTypeAnnotation(), annotationDefinition2.isTypeAnnotation() );
            assertEquals( annotationDefinition1.isFieldAnnotation(), annotationDefinition2.isFieldAnnotation() );

            assertEquals( annotationDefinition1.isMarker(), annotationDefinition2.isMarker() );
            assertEquals( annotationDefinition1.isNormal(), annotationDefinition2.isNormal() );
            assertEquals( annotationDefinition1.isSingleValue(), annotationDefinition2.isSingleValue() );

            assertEquals( annotationDefinition1.getRetention(), annotationDefinition2.getRetention() );
            assertArrayEquals( annotationDefinition1.getTarget().toArray(), annotationDefinition2.getTarget().toArray() );

            assertEquals( annotationDefinition1.getValuePairs().size(), annotationDefinition1.getValuePairs().size() );

            assertEquals( annotationDefinition1.getValuePairs().size(), annotationDefinition2.getValuePairs().size() );
        } else {
            assertNull( annotationDefinition2 );
        }
    }

    public static void assertEqualsAnnotationValuePair( AnnotationValuePairDefinition valuePairDefinition1, AnnotationValuePairDefinition valuePairDefinition2 ) {
        if ( valuePairDefinition1 != null ) {
            assertNotNull( valuePairDefinition2 );

            assertEquals( valuePairDefinition1.getName(), valuePairDefinition2.getName() );
            assertEquals( valuePairDefinition1.getClassName(), valuePairDefinition2.getClassName() );

            assertEquals( valuePairDefinition1.isAnnotation(), valuePairDefinition2.isAnnotation() );
            assertEquals( valuePairDefinition1.isClass(), valuePairDefinition2.isClass() );
            assertEquals( valuePairDefinition1.isEnum(), valuePairDefinition2.isEnum() );
            assertEquals( valuePairDefinition1.isPrimitiveType(), valuePairDefinition2.isPrimitiveType() );
            assertEquals( valuePairDefinition1.isString(), valuePairDefinition2.isString() );
            assertEquals( valuePairDefinition1.isArray(), valuePairDefinition2.isArray() );

            assertEquals( valuePairDefinition1.hasDefaultValue(), valuePairDefinition2.hasDefaultValue() );
            assertEquals( valuePairDefinition1.getDefaultValue(), valuePairDefinition2.getDefaultValue() );
            assertEqualsAnnotationDefinition( valuePairDefinition1.getAnnotationDefinition(), valuePairDefinition2.getAnnotationDefinition() );

        } else {
            assertNotNull( valuePairDefinition2 );
        }

    }

    public static void assertName( String name, DataObject dataObject ) {
        assertEquals( name, dataObject.getName() );
    }

    public static void assertPackageName( String packageName, DataObject dataObject ) {
        assertEquals( packageName, dataObject.getPackageName() );
    }

    public static void assertClassName( String className, DataObject dataObject ) {
        assertEquals( className, dataObject.getClassName() );
    }

}

