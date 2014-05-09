package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.workbench.common.screens.datamodeller.model.AnnotationTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DataModelerAssert {

    public static void assertEqualsDataObject( DataObjectTO obj1, DataObjectTO obj2 ) {
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

    public static void assertEqualsProperties( List<ObjectPropertyTO> properties1, List<ObjectPropertyTO> properties2 ) {
        if ( properties1 != null ) {
            assertNotNull( properties2 );
            assertEquals( properties1.size(), properties2.size() );

            Map<String, ObjectPropertyTO> propertyMap = new HashMap<String, ObjectPropertyTO>();
            for ( ObjectPropertyTO propertyTO : properties2 ) {
                propertyMap.put( propertyTO.getName(), propertyTO );
            }

            for ( ObjectPropertyTO propertyTO : properties1 ) {
                assertEqualsProperty( propertyTO, propertyMap.get( propertyTO.getName() ) );
            }
        }
    }

    public static void assertEqualsProperty( ObjectPropertyTO property1, ObjectPropertyTO property2 ) {
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

    public static void assertEqualsAnnotations( List<AnnotationTO> annotations1, List<AnnotationTO> annotations2 ) {
        if ( annotations1 != null ) {
            assertNotNull( annotations2 );
            assertEquals( annotations1.size(), annotations2.size() );

            Map<String, AnnotationTO> annotationMap = new HashMap<String, AnnotationTO>();
            for ( AnnotationTO annotationTO : annotations2 ) {
                annotationMap.put( annotationTO.getClassName(), annotationTO );
            }

            for ( AnnotationTO annotationTO : annotations1 ) {
                assertEqualsAnnotation( annotationTO, annotationMap.get( annotationTO.getClassName() ) );
            }
        } else {
            assertNull( annotations2 );
        }
    }

    public static void assertEqualsAnnotation( AnnotationTO annotation1, AnnotationTO annotation2 ) {
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

    public static void assertName( String name, DataObjectTO dataObjectTO ) {
        assertEquals( name, dataObjectTO.getName() );
    }

    public static void assertPackageName( String packageName, DataObjectTO dataObjectTO ) {
        assertEquals( packageName, dataObjectTO.getPackageName() );
    }

    public static void assertClassName( String className, DataObjectTO dataObjectTO ) {
        assertEquals( className, dataObjectTO.getClassName() );
    }

}
