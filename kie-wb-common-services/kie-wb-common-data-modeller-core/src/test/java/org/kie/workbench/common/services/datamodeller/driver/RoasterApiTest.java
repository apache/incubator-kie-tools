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
package org.kie.workbench.common.services.datamodeller.driver;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.datamodeller.annotations.AnnotationValuesAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.ClassAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.ENUM3;
import org.kie.workbench.common.services.datamodeller.annotations.EnumsAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.PrimitivesAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.TestEnums;

import static org.junit.Assert.*;

public class RoasterApiTest {

    private JavaClassSource javaClass;

    @Before
    public void createBaseClass() {
        javaClass = createClass( "org.kie.workbench.common.services.datamodeller.driver", "TestAnnotations" );
    }


    public JavaClassSource createClass(String pkg, String name) {
        JavaClassSource javaClass = Roaster.create( JavaClassSource.class );

        javaClass.setPackage(pkg);
        javaClass.setName( name );
        return javaClass;
    }


    @Test
    public void testAnnotations() {
        AnnotationSource annotationSource = javaClass.addAnnotation( ClassAnnotation.class.getName() );

        //Class value pair
        annotationSource.setClassValue( "classParam", java.util.List.class );
        Class cls = annotationSource.getClassValue( "classParam" );
        assertEquals( java.util.List.class, cls);

        //Class array value pair
        annotationSource.setClassArrayValue( "classArrayParam", new Class[] { Map.class, List.class, Serializable.class} );
        Class[] clss = annotationSource.getClassArrayValue( "classArrayParam" );
        assertArrayEquals(  new Class[] { Map.class, List.class, Serializable.class}, clss );


        annotationSource = javaClass.addAnnotation( PrimitivesAnnotation.class.getName() );
        //String value pair
        annotationSource.setStringValue( "stringParam", "TheValue" );
        String stringResult = annotationSource.getStringValue( "stringParam" );
        assertEquals( "TheValue", stringResult );

        //String array value pair
        String[] stringArray =  {"value1", "value2", "value3"};
        annotationSource.setStringArrayValue( "stringArrayParam", new String[] {"value1", "value2", "value3"} );
        String[] stringArrayResult = annotationSource.getStringArrayValue( "stringArrayParam" );
        assertArrayEquals( stringArray, stringArrayResult );


        annotationSource = javaClass.addAnnotation( EnumsAnnotation.class.getName() );
        //Enum value pair
        annotationSource.setEnumValue( "enum3Param", ENUM3.VALUE1 );
        annotationSource.setEnumValue( "enum1Param", TestEnums.ENUM1.VALUE2 );

        Object enumValue1Result = annotationSource.getEnumValue( ENUM3.class, "enum3Param" );
        Object enumValue2Result = annotationSource.getEnumValue( TestEnums.ENUM1.class, "enum1Param" );

        assertEquals( ENUM3.VALUE1, enumValue1Result );
        assertEquals( TestEnums.ENUM1.VALUE2, enumValue2Result );

        //Enum array value pair
        TestEnums.ENUM2[] enumArrayValues = { TestEnums.ENUM2.VALUE1, TestEnums.ENUM2.VALUE2, TestEnums.ENUM2.VALUE3 };
        annotationSource.setEnumArrayValue( "enum2ArrayParam", enumArrayValues );

        Object[] enumArrayValuesResult = annotationSource.getEnumArrayValue( TestEnums.ENUM2.class, "enum2ArrayParam" );
        assertArrayEquals( enumArrayValues, enumArrayValuesResult  );


        //Annotation value pair
        annotationSource = javaClass.addAnnotation(AnnotationValuesAnnotation.class.getName());

        AnnotationSource primitivesAnnotation = annotationSource.setAnnotationValue( "primitivesParam" );
        primitivesAnnotation.setName( PrimitivesAnnotation.class.getName() );
        primitivesAnnotation.setStringValue( "stringParam", "The Value" );

        AnnotationSource arrayAnnotationValue1 = annotationSource.addAnnotationValue( "primitivesArrayParam" );
        arrayAnnotationValue1.setName( PrimitivesAnnotation.class.getName() );
        arrayAnnotationValue1.setStringValue( "stringParam", "The Value1" );


        AnnotationSource arrayAnnotationValue2 = annotationSource.addAnnotationValue( "primitivesArrayParam" );
        arrayAnnotationValue2.setName( PrimitivesAnnotation.class.getName() );
        arrayAnnotationValue2.setStringValue( "stringParam", "The Value2" );

        AnnotationSource primitivesParamValue = annotationSource.getAnnotationValue("primitivesParam");

        AnnotationSource[] primitivesArrayParamValue = annotationSource.getAnnotationArrayValue("primitivesArrayParam");

        System.out.println( javaClass.toString() );
    }
}
