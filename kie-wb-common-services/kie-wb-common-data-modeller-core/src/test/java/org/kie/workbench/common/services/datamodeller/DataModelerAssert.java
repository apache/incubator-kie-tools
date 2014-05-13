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

package org.kie.workbench.common.services.datamodeller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.workbench.common.services.datamodeller.core.Annotation;
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

    public static void assertEqualsProperties( Map<String, ObjectProperty> properties1, Map<String, ObjectProperty> properties2 ) {
        if ( properties1 != null ) {

            assertNotNull( properties2 );
            assertEquals( properties1.size(), properties2.size() );

            for ( ObjectProperty property : properties1.values() ) {
                assertEqualsProperty( property, properties2.get( property.getName() ) );
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
            for ( String annotationKey : annotation1.getValues().keySet() ) {
                assertEquals( annotation1.getValues().get( annotationKey ), annotation2.getValues().get( annotationKey ) );
            }
        } else {
            assertNull( annotation2 );
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

