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

package org.kie.workbench.common.services.datamodeller.driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.drools.core.base.ClassTypeResolver;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.definition.type.ClassReactive;
import org.kie.api.definition.type.Description;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Label;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;
import org.kie.api.definition.type.TypeSafe;
import org.kie.workbench.common.services.datamodeller.DataModelerAssert;
import org.kie.workbench.common.services.datamodeller.annotations.AnnotationValuesAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.ClassAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.ENUM3;
import org.kie.workbench.common.services.datamodeller.annotations.EnumsAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.MarkerAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.PrimitivesAnnotation;
import org.kie.workbench.common.services.datamodeller.annotations.TestEnums;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataModelImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.kie.workbench.common.services.datamodeller.driver.impl.JavaRoasterModelDriver;
import org.kie.workbench.common.services.datamodeller.driver.impl.UpdateInfo;
import org.kie.workbench.common.services.datamodeller.driver.model.ModelDriverResult;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;

public class JavaRoasterModelDriverTest {

    SimpleFileSystemProvider simpleFileSystemProvider = null;
    IOService ioService = new MockIOService();

    @Before
    public void initTest() throws Exception {
        simpleFileSystemProvider = new SimpleFileSystemProvider();
        simpleFileSystemProvider.forceAsDefault();
    }

    @Test
    public void modelReadTest() {
        try {
            String uriToResource = this.getClass().getResource( "projectRoot.txt" ).toURI().toString();
            URI uriToRootPath = URI.create( uriToResource.substring( 0, uriToResource.length() - "projectRoot.txt".length() ) );
            Path rootPath = simpleFileSystemProvider.getPath( uriToRootPath );

            JavaRoasterModelDriver javaRoasterModelDriver = new JavaRoasterModelDriver( ioService, rootPath, true, getClass().getClassLoader() );
            ModelDriverResult modelDriverResult = javaRoasterModelDriver.loadModel();

            DataModel dataModelOriginal = createModel();

            assertNotNull( modelDriverResult );
            assertNotNull( modelDriverResult.getDataModel() );

            assertEquals( dataModelOriginal.getDataObjects().size(), modelDriverResult.getDataModel().getDataObjects().size() );

            for ( DataObject dataObject : dataModelOriginal.getDataObjects() ) {
                DataModelerAssert.assertEqualsDataObject( dataObject, modelDriverResult.getDataModel().getDataObject( dataObject.getClassName() ) );
            }

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Test failed: " + e.getMessage() );
        }
    }

    @Test
    public void updateAnnotationsTest() {

        try {
            String uriToResource = this.getClass().getResource( "projectRoot.txt" ).toURI().toString();
            URI uriToRootPath = URI.create( uriToResource.substring( 0, uriToResource.length() - "projectRoot.txt".length() ) );
            Path rootPath = simpleFileSystemProvider.getPath( uriToRootPath );

            //First read the AnnotationsUpdateTest
            Path annotationsUpdateTestFilePath =  rootPath.resolve( "package3" ).resolve( "AnnotationsUpdateTest.java" );
            String source = ioService.readAllString( annotationsUpdateTestFilePath );
            JavaClassSource annotationsUpdateTestJavaClassSource = (JavaClassSource)Roaster.parse( source );

            ClassLoader classLoader = getClass().getClassLoader();
            ClassTypeResolver classTypeResolver = DriverUtils.createClassTypeResolver( annotationsUpdateTestJavaClassSource, classLoader );

            JavaRoasterModelDriver javaRoasterModelDriver = new JavaRoasterModelDriver( ioService, rootPath, true, classLoader );

            ModelDriverResult result = javaRoasterModelDriver.loadDataObject( source, annotationsUpdateTestFilePath );

            //1) read the AnnotationsUpdateTest
            DataObject annotationsUpdateTest = result.getDataModel().getDataObject( "org.kie.workbench.common.services.datamodeller.driver.package3.AnnotationsUpdateTest" );

            //2) modify the AnnotationsUpdateTest according to the expected result, basically updating all the needed annotations.

            //update the AnnotationValuesAnnotation
            Annotation annotationValuesAnnotation = annotationsUpdateTest.getAnnotation( AnnotationValuesAnnotation.class.getName() );
            //annotation.setValue( "primitivesParam", @PrimitivesAnnotation(stringParam="\"line1\" \n line2 \\ \n line3") );
            Annotation primitivesParamValue = createAnnotation( PrimitivesAnnotation.class );

            // construct the value -> "\"line1\" \n line2 \\ \n line3"
            StringBuilder stringParamBuilder = new StringBuilder( );
            stringParamBuilder.append( '"' ).append( "line1" ).append( '"' ).append( " " ).append( '\n' );
            stringParamBuilder.append( " line2 " ).append( '\\' ).append( " " ).append( '\n' );
            stringParamBuilder.append( " line3" );

            primitivesParamValue.setValue( "stringParam", stringParamBuilder.toString() );
            annotationValuesAnnotation.setValue( "primitivesParam", primitivesParamValue );

            //@PrimitivesAnnotation(intParam=1)
            Annotation primitiveValue1 = createAnnotation( PrimitivesAnnotation.class );
            primitiveValue1.setValue( "intParam", 2 );

            //@PrimitivesAnnotation(intParam=3)
            Annotation primitiveValue2 = createAnnotation( PrimitivesAnnotation.class );
            primitiveValue2.setValue( "intParam", 3 );

            List<Annotation> primitivesArrayParamValue = new ArrayList<Annotation>(  );
            primitivesArrayParamValue.add( primitiveValue1 );
            primitivesArrayParamValue.add( primitiveValue2 );

            // annotation.setValue( "primitivesArrayParam", "{@PrimitivesAnnotation(intParam=2),@PrimitivesAnnotation(intParam=3)}" );
            annotationValuesAnnotation.setValue( "primitivesArrayParam", primitivesArrayParamValue );

            //annotation.setValue( "enumsParam", "@EnumsAnnotation(enum1Param=TestEnums.ENUM1.VALUE2)" );
            Annotation enumsParamValue = createAnnotation( EnumsAnnotation.class );
            enumsParamValue.setValue( "enum1Param", TestEnums.ENUM1.VALUE2.toString() );
            annotationValuesAnnotation.setValue( "enumsParam", enumsParamValue);


            //annotation.setValue( "enumsArrayParam", "{@EnumsAnnotation(enum1Param=TestEnums.ENUM1.VALUE2),@EnumsAnnotation(enum1Param=TestEnums.ENUM1.VALUE3)}" );
            Annotation enumArrayParam1 = createAnnotation( EnumsAnnotation.class );
            enumArrayParam1.setValue( "enum1Param", TestEnums.ENUM1.VALUE2.toString() );

            Annotation enumArrayParam2 = createAnnotation( EnumsAnnotation.class );
            enumArrayParam2.setValue( "enum1Param", TestEnums.ENUM1.VALUE3.toString() );

            List<Annotation> enumVarrayValues = new ArrayList<Annotation>(  );
            enumVarrayValues.add( enumArrayParam1 );
            enumVarrayValues.add( enumArrayParam2 );
            annotationValuesAnnotation.setValue( "enumsArrayParam", enumVarrayValues );

            //annotation.setValue( "classAnnotationParam", "@ClassAnnotation(classParam=Set.class)" );
            Annotation classAnnotationParamValue = createAnnotation( ClassAnnotation.class );
            classAnnotationParamValue.setValue( "classParam", "Set.class" );
            annotationValuesAnnotation.setValue( "classAnnotationParam", classAnnotationParamValue );


            //annotation.setValue( "classAnnotationArrayParam", "{@ClassAnnotation(classParam=Set.class),@ClassAnnotation(classParam=Set.class)}" );
            Annotation classAnnotationArrayParamValue1 = createAnnotation( ClassAnnotation.class );
            classAnnotationArrayParamValue1.setValue( "classParam", "Set.class" );

            Annotation classAnnotationArrayParamValue2 = createAnnotation( ClassAnnotation.class );
            classAnnotationArrayParamValue2.setValue( "classParam", "Set.class" );

            List<Annotation> classAnnotationArrayParamValue = new ArrayList<Annotation>(  );
            classAnnotationArrayParamValue.add( classAnnotationArrayParamValue1 );
            classAnnotationArrayParamValue.add( classAnnotationArrayParamValue2 );

            annotationValuesAnnotation.setValue( "classAnnotationArrayParam", classAnnotationArrayParamValue );

            //update the ClassAnnotation
            Annotation classAnnotation = createAnnotation( ClassAnnotation.class );
            classAnnotation.setValue( "classParam", "java.util.Collection.class" );
            classAnnotation.setValue( "classArrayParam", createArrayParam( "List.class" ) );
            annotationsUpdateTest.addAnnotation( classAnnotation );


            //update the EnumsAnnotation
            Annotation enumsAnnotation = createAnnotation( EnumsAnnotation.class );
            enumsAnnotation.setValue( "enum1Param",  TestEnums.ENUM1.VALUE2.toString() );
            //enumsAnnotation.setValue( "enum1ArrayParam", "{TestEnums.ENUM1.VALUE3}" );
            enumsAnnotation.setValue( "enum1ArrayParam", createEnumArrayParam( TestEnums.ENUM1.VALUE3 ) );

            enumsAnnotation.setValue( "enum2Param", TestEnums.ENUM2.VALUE2.toString() );
            //enumsAnnotation.setValue( "enum2ArrayParam", "{TestEnums.ENUM2.VALUE3}" );
            enumsAnnotation.setValue( "enum2ArrayParam", createEnumArrayParam( TestEnums.ENUM2.VALUE3 ) );

            annotationsUpdateTest.addAnnotation( enumsAnnotation );

            //update the PrimitivesAnnotation

            Annotation primitivesAnnotation = createAnnotation( PrimitivesAnnotation.class );
            primitivesAnnotation.setValue( "byteParam", new Byte("2") );
            //primitivesAnnotation.setValue( "byteArrayParam", "{3,4}" );
            primitivesAnnotation.setValue( "byteArrayParam", createArrayParam( (byte)3, (byte)4 ) );

            primitivesAnnotation.setValue( "shortParam", new Short("2") );
            //primitivesAnnotation.setValue( "shortArrayParam", "{3,4}" );
            primitivesAnnotation.setValue( "shortArrayParam", createArrayParam( ( short ) 3, ( short ) 4 ) );

            primitivesAnnotation.setValue( "stringParam", "2" );

            primitivesAnnotation.setValue( "stringArrayParam", createArrayParam( "3", "4" ) );
            annotationsUpdateTest.addAnnotation( primitivesAnnotation );

            //add the MarkerAnnotation
            annotationsUpdateTest.addAnnotation( createMarkerAnnotation() );
            annotationsUpdateTest.setName( "AnnotationsUpdateTestResult" );

            //3) compare the modified data object with the expected data object.
            Path expectedFilePath =  rootPath.resolve( "package3" ).resolve( "AnnotationsUpdateTestResult.java" );
            String expectedSource = ioService.readAllString( expectedFilePath );
            JavaClassSource expectedJavaClassSource = (JavaClassSource)Roaster.parse( expectedSource );

            ModelDriverResult expectedResult = javaRoasterModelDriver.loadDataObject( expectedSource, expectedFilePath );
            DataObject annotationsUpdateTestResult = expectedResult.getDataModel().getDataObject( "org.kie.workbench.common.services.datamodeller.driver.package3.AnnotationsUpdateTestResult" );


            //First check, the modified data object in memory should be the same as the readed from the model.
            DataModelerAssert.assertEqualsDataObject( annotationsUpdateTestResult, annotationsUpdateTest );


            //Second check, update the JavaClassSource corresponding to the AnnotationsUpdateTest

            javaRoasterModelDriver.updateSource( annotationsUpdateTestJavaClassSource, annotationsUpdateTest, new UpdateInfo(), classTypeResolver );
            ModelDriverResult updatedResult = javaRoasterModelDriver.loadDataObject( annotationsUpdateTestJavaClassSource.toString(), annotationsUpdateTestFilePath );
            //and now compare the updatedResult with the expected value.
            DataModelerAssert.assertEqualsDataObject( annotationsUpdateTestResult, updatedResult.getDataModel().getDataObject( "org.kie.workbench.common.services.datamodeller.driver.package3.AnnotationsUpdateTestResult" ) );

            System.out.println( annotationsUpdateTestJavaClassSource );

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Test failed: " + e.getMessage() );
        }

    }

    class MockIOService extends IOServiceMock {

        @Override
        public String readAllString( org.uberfire.java.nio.file.Path path )
                throws IllegalArgumentException, NoSuchFileException, org.uberfire.java.nio.IOException {
            return readFile( path );
        }
    }

    private DataModel createModel() {
        DataModel dataModel = new DataModelImpl();

        dataModel.addDataObject( createPojo1() );
        dataModel.addDataObject( createPojo2() );
        dataModel.addDataObject( createAnnotationValuesAnnotationTest() );
        dataModel.addDataObject( createClassAnnotationTest() );
        dataModel.addDataObject( createEnumsAnnotationTest() );
        dataModel.addDataObject( createMarkerAnnotationTest() );
        dataModel.addDataObject( createPrimitivesAnnotationTest() );
        dataModel.addDataObject( createAnnotationsUpdateTest() );
        dataModel.addDataObject( createAnnotationsUpdateTestResult() );

        return dataModel;
    }

    private DataObject createPojo1( ) {

        DataObject pojo1 = createDataObject( "org.kie.workbench.common.services.datamodeller.driver.package1", "Pojo1", "org.kie.workbench.common.services.datamodeller.driver.package2.Pojo2" );

        Annotation annotation;

        annotation = createAnnotation( Entity.class, "name", "Pojo1Entity" );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( Table.class, "name", "POJO1" );
        annotation.setValue( "catalog", "CATALOG_NAME" );
        annotation.setValue( "schema", "SCHEMA_NAME" );

        // @UniqueConstraint( name = "constraint1", columnNames = {"column1", "column2"} )
        Annotation constraint1 = createAnnotation( UniqueConstraint.class );
        constraint1.setValue( "name", "constraint1" );
        constraint1.setValue( "columnNames", createArrayParam( "column1", "column2" ) );

        // @UniqueConstraint( name = "constraint2", columnNames = {"column3","column4"} )
        Annotation constraint2 = createAnnotation( UniqueConstraint.class );
        constraint2.setValue( "name", "constraint2" );
        constraint2.setValue( "columnNames", createArrayParam( "column3", "column4" ) );

        List<Annotation> uniqueConstraints = new ArrayList<Annotation>(  );
        uniqueConstraints.add( constraint1 );
        uniqueConstraints.add( constraint2 );

        //annotation.setValue( "uniqueConstraints", "{@UniqueConstraint(name=\"constraint1\",columnNames={\"column1\",\"column2\"}),@UniqueConstraint(name=\"constraint2\",columnNames={\"column3\",\"column4\"})}" );
        annotation.setValue( "uniqueConstraints", uniqueConstraints );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( SequenceGenerator.class, "name", "pojo1IdSeq" );
        annotation.setValue( "sequenceName", "POJO_ID_SEQ" );
        annotation.setValue( "allocationSize", new Integer("1") );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( TypeSafe.class, "value", true );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( Role.class, "value", Role.Type.EVENT.toString() );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( Label.class, "value", "Pojo1Label" );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( Description.class, "value", "Pojo1Description" );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( Duration.class, "value", "duration" );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( Timestamp.class, "value", "timestamp" );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( ClassReactive.class );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( Expires.class, "value", "1h25m" );
        pojo1.addAnnotation( annotation );

        ObjectProperty property = pojo1.addProperty( "serialVersionUID", "long" );

        property = pojo1.addProperty( "field1", "java.lang.Character" );

        annotation = createAnnotation( Position.class, "value", new Integer("0") );
        property.addAnnotation( annotation );

        annotation = createAnnotation( Key.class );
        property.addAnnotation( annotation );

        annotation = createAnnotation( Label.class, "value", "field1Label" );
        property.addAnnotation( annotation );

        annotation = createAnnotation( Description.class, "value", "field1Description" );
        property.addAnnotation( annotation );

        property = pojo1.addProperty( "duration", "java.lang.Integer" );
        annotation = createAnnotation( Position.class, "value", new Integer("1") );
        property.addAnnotation( annotation );

        property = pojo1.addProperty( "timestamp", "java.util.Date" );
        annotation = createAnnotation( Position.class, "value", new Integer("2") );
        property.addAnnotation( annotation );

        property = pojo1.addProperty( "field2", "char" );
        annotation = createAnnotation( Position.class, "value", new Integer("3") );
        property.addAnnotation( annotation );

        annotation = createAnnotation( Key.class );
        property.addAnnotation( annotation );

        annotation = createAnnotation( Label.class, "value", "field2Label" );
        property.addAnnotation( annotation );

        annotation = createAnnotation( Description.class, "value", "field2Description" );
        property.addAnnotation( annotation );

        return pojo1;
    }

    private DataObject createPojo2( ) {
        DataObject pojo2 = createDataObject( "org.kie.workbench.common.services.datamodeller.driver.package2", "Pojo2", null );

        return pojo2;
    }

    private DataObject createAnnotationValuesAnnotationTest( ) {
        DataObject dataObject = createDataObject( "org.kie.workbench.common.services.datamodeller.driver.package3", "AnnotationValuesAnnotationTest", null );
        ObjectProperty field1 = dataObject.addProperty( "field1", String.class.getName() );
        Annotation annotation = createAnnotationValuesAnnotation( );
        dataObject.addAnnotation( annotation );
        field1.addAnnotation( annotation );
        return dataObject;
    }

    private Annotation createAnnotationValuesAnnotation() {

        Annotation annotation = createAnnotation( AnnotationValuesAnnotation.class );

        //annotation.setValue( "primitivesParam", "@PrimitivesAnnotation(stringParam=\"1\")" );
        Annotation primitivesParamValue = createAnnotation( PrimitivesAnnotation.class );
        primitivesParamValue.setValue( "stringParam", "1" );
        annotation.setValue( "primitivesParam", primitivesParamValue );

        //@PrimitivesAnnotation(intParam=1)
        Annotation primitiveValue1 = createAnnotation( PrimitivesAnnotation.class );
        primitiveValue1.setValue( "intParam", 1 );

        //@PrimitivesAnnotation(intParam=2)
        Annotation primitiveValue2 = createAnnotation( PrimitivesAnnotation.class );
        primitiveValue2.setValue( "intParam", 2 );

        List<Annotation> primitivesArrayParamValue = new ArrayList<Annotation>(  );
        primitivesArrayParamValue.add( primitiveValue1 );
        primitivesArrayParamValue.add( primitiveValue2 );

        // annotation.setValue( "primitivesArrayParam", "{@PrimitivesAnnotation(intParam=1),@PrimitivesAnnotation(intParam=2)}" );
        annotation.setValue( "primitivesArrayParam", primitivesArrayParamValue );

        //annotation.setValue( "enumsParam", "@EnumsAnnotation(enum1Param=TestEnums.ENUM1.VALUE1)" );
        Annotation enumsParamValue = createAnnotation( EnumsAnnotation.class );
        enumsParamValue.setValue( "enum1Param", TestEnums.ENUM1.VALUE1.toString() );
        annotation.setValue( "enumsParam", enumsParamValue);


        //annotation.setValue( "enumsArrayParam", "{@EnumsAnnotation(enum1Param=TestEnums.ENUM1.VALUE1),@EnumsAnnotation(enum1Param=TestEnums.ENUM1.VALUE2)}" );
        Annotation enumArrayParam1 = createAnnotation( EnumsAnnotation.class );
        enumArrayParam1.setValue( "enum1Param", TestEnums.ENUM1.VALUE1.toString() );

        Annotation enumArrayParam2 = createAnnotation( EnumsAnnotation.class );
        enumArrayParam2.setValue( "enum1Param", TestEnums.ENUM1.VALUE2.toString() );

        List<Annotation> enumVarrayValues = new ArrayList<Annotation>(  );
        enumVarrayValues.add( enumArrayParam1 );
        enumVarrayValues.add( enumArrayParam2 );
        annotation.setValue( "enumsArrayParam", enumVarrayValues );

        //annotation.setValue( "classAnnotationParam", "@ClassAnnotation(classParam=Map.class)" );
        Annotation classAnnotationParamValue = createAnnotation( ClassAnnotation.class );
        classAnnotationParamValue.setValue( "classParam", "Map.class" );
        annotation.setValue( "classAnnotationParam", classAnnotationParamValue );


        //annotation.setValue( "classAnnotationArrayParam", "{@ClassAnnotation(classParam=Map.class),@ClassAnnotation(classParam=Set.class)}" );
        Annotation classAnnotationArrayParamValue1 = createAnnotation( ClassAnnotation.class );
        classAnnotationArrayParamValue1.setValue( "classParam", "Map.class" );

        Annotation classAnnotationArrayParamValue2 = createAnnotation( ClassAnnotation.class );
        classAnnotationArrayParamValue2.setValue( "classParam", "Set.class" );

        List<Annotation> classAnnotationArrayParamValue = new ArrayList<Annotation>(  );
        classAnnotationArrayParamValue.add( classAnnotationArrayParamValue1 );
        classAnnotationArrayParamValue.add( classAnnotationArrayParamValue2 );

        annotation.setValue( "classAnnotationArrayParam", classAnnotationArrayParamValue );

        return annotation;
    }

    private DataObject createClassAnnotationTest( ) {
        DataObject dataObject = createDataObject( "org.kie.workbench.common.services.datamodeller.driver.package3", "ClassAnnotationTest", null );
        ObjectProperty field1 = dataObject.addProperty( "field1", String.class.getName() );
        Annotation annotation = createClassAnnotation();
        dataObject.addAnnotation( annotation );
        field1.addAnnotation( annotation );
        return dataObject;
    }

    private Annotation createClassAnnotation() {
        Annotation annotation = createAnnotation( ClassAnnotation.class );
        annotation.setValue( "classParam", "java.util.List.class" );
        annotation.setValue( "classArrayParam", createArrayParam( "List.class", "Collection.class", "Map.class", "Set.class" ) );
        return annotation;
    }

    private DataObject createEnumsAnnotationTest( ) {
        DataObject dataObject = createDataObject( "org.kie.workbench.common.services.datamodeller.driver.package3", "EnumsAnnotationTest", null );
        ObjectProperty field1 = dataObject.addProperty( "field1", String.class.getName() );
        Annotation annotation = createEnumsAnnotation();
        dataObject.addAnnotation( annotation );
        field1.addAnnotation( annotation );
        return dataObject;
    }

    private Annotation createEnumsAnnotation() {
        Annotation annotation = createAnnotation( EnumsAnnotation.class );
        annotation.setValue( "enum1Param",  TestEnums.ENUM1.VALUE1.toString() );
        //annotation.setValue( "enum1ArrayParam", "{TestEnums.ENUM1.VALUE1,TestEnums.ENUM1.VALUE2}" );
        annotation.setValue( "enum1ArrayParam", createEnumArrayParam( TestEnums.ENUM1.VALUE1, TestEnums.ENUM1.VALUE2 ) );
        annotation.setValue( "enum2Param", TestEnums.ENUM2.VALUE1.toString() );
        //annotation.setValue( "enum2ArrayParam","{TestEnums.ENUM2.VALUE1,TestEnums.ENUM2.VALUE2}" );
        annotation.setValue( "enum2ArrayParam", createEnumArrayParam( TestEnums.ENUM2.VALUE1, TestEnums.ENUM2.VALUE2 ) );

        annotation.setValue( "enum3Param", ENUM3.VALUE1.toString() );
        //annotation.setValue( "enum3ArrayParam", "{ENUM3.VALUE1,ENUM3.VALUE2}" );
        annotation.setValue( "enum3ArrayParam", createEnumArrayParam( ENUM3.VALUE1, ENUM3.VALUE2 ) );

        return annotation;
    }

    private DataObject createPrimitivesAnnotationTest( ) {
        DataObject dataObject = createDataObject( "org.kie.workbench.common.services.datamodeller.driver.package3", "PrimitivesAnnotationTest", null );
        ObjectProperty field1 = dataObject.addProperty( "field1", String.class.getName() );
        Annotation annotation = createPrimitivesAnnotation();
        dataObject.addAnnotation( annotation );
        field1.addAnnotation( annotation );
        return dataObject;
    }

    private Annotation createPrimitivesAnnotation() {
        Annotation annotation = createAnnotation( PrimitivesAnnotation.class );
        annotation.setValue( "byteParam", new Byte("1") );
        //annotation.setValue( "byteArrayParam", "{1,2}" );
        annotation.setValue( "byteArrayParam", createArrayParam( (byte)1, (byte)2 ) );

        annotation.setValue( "shortParam", new Short("1") );
        //annotation.setValue( "shortArrayParam", "{1,2}" );
        annotation.setValue( "shortArrayParam", createArrayParam( (short)1, (short)2 ) );

        annotation.setValue( "intParam", new Integer("1") );
        //annotation.setValue( "intArrayParam", "{1,2}" );
        annotation.setValue( "intArrayParam", createArrayParam( (int)1, (int)2 ) );


        annotation.setValue( "longParam", new Long("1") );
        //annotation.setValue( "longArrayParam", "{1,2}" );
        annotation.setValue( "longArrayParam", createArrayParam( (long)1, (long)2 ) );


        annotation.setValue( "floatParam", new Float("1") );
        //annotation.setValue( "floatArrayParam", "{1,2}" );
        annotation.setValue( "floatArrayParam", createArrayParam( (float)1, (float)2 ) );

        annotation.setValue( "doubleParam", new Double("1") );
        //annotation.setValue( "doubleArrayParam", "{1,2}" );
        annotation.setValue( "doubleArrayParam", createArrayParam( (double)1, (double)2 ) );

        annotation.setValue( "booleanParam", true );
        //annotation.setValue( "booleanArrayParam", "{true,true}");
        annotation.setValue( "booleanArrayParam", createArrayParam( true, true ) );

        annotation.setValue( "charParam", "1");
        //annotation.setValue( "charArrayParam", "{'1','2'}" );
        annotation.setValue( "charArrayParam", createArrayParam( "1","2" ) );

        annotation.setValue( "stringParam", "1" );

        annotation.setValue( "stringArrayParam", createArrayParam( "1", "2" ) );
        return annotation;
    }

    private DataObject createMarkerAnnotationTest( ) {
        DataObject dataObject = createDataObject( "org.kie.workbench.common.services.datamodeller.driver.package3", "MarkerAnnotationTest", null );
        ObjectProperty field1 = dataObject.addProperty( "field1", String.class.getName() );
        Annotation annotation = createMarkerAnnotation();
        dataObject.addAnnotation( annotation );
        field1.addAnnotation( annotation );
        return dataObject;
    }

    private Annotation createMarkerAnnotation() {
        return createAnnotation( MarkerAnnotation.class );
    }

    private DataObject createAnnotationsUpdateTest() {
        DataObject dataObject = createDataObject( "org.kie.workbench.common.services.datamodeller.driver.package3", "AnnotationsUpdateTest", null );
        dataObject.addAnnotation( createAnnotationValuesAnnotation() );
        dataObject.addAnnotation( createClassAnnotation() );
        dataObject.addAnnotation( createEnumsAnnotation() );
        dataObject.addAnnotation( createPrimitivesAnnotation() );
        return dataObject;
    }

    private DataObject createAnnotationsUpdateTestResult() {
        DataObject dataObject = createDataObject( "org.kie.workbench.common.services.datamodeller.driver.package3", "AnnotationsUpdateTestResult", null );

        dataObject.addAnnotation( createMarkerAnnotation() );
        /*
        @PrimitivesAnnotation( byteParam = ( byte ) 2, byteArrayParam = { 3, 4 },
        shortParam = 2, shortArrayParam = { 3, 4 },
        stringParam = "2", stringArrayParam = { "3", "4" }
         */
        Annotation primitivesAnnotation = createAnnotation( PrimitivesAnnotation.class );
        primitivesAnnotation.setValue( "byteParam", new Byte("2") );
        //primitivesAnnotation.setValue( "byteArrayParam", "{3,4}" );
        primitivesAnnotation.setValue( "byteArrayParam", createArrayParam( ( byte ) 3, ( byte ) 4 ) );

        primitivesAnnotation.setValue( "shortParam", new Short( "2" ) );
        primitivesAnnotation.setValue( "shortArrayParam", createArrayParam( ( short ) 3, ( short ) 4 ) );
        primitivesAnnotation.setValue( "stringParam", "2" );

        primitivesAnnotation.setValue( "stringArrayParam", createArrayParam( "3", "4" ) );
        dataObject.addAnnotation( primitivesAnnotation );

        /*
        @EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE2, enum1ArrayParam = { TestEnums.ENUM1.VALUE3 },
        enum2Param = TestEnums.ENUM2.VALUE2, enum2ArrayParam = { TestEnums.ENUM2.VALUE3 }
        )
        */
        Annotation enumsAnnotation = createAnnotation( EnumsAnnotation.class );
        enumsAnnotation.setValue( "enum1Param",  TestEnums.ENUM1.VALUE2.toString() );
        //enumsAnnotation.setValue( "enum1ArrayParam", "{TestEnums.ENUM1.VALUE3}" );
        enumsAnnotation.setValue( "enum1ArrayParam", createEnumArrayParam( TestEnums.ENUM1.VALUE3 ) );

        enumsAnnotation.setValue( "enum2Param", TestEnums.ENUM2.VALUE2.toString() );
        //enumsAnnotation.setValue( "enum2ArrayParam", "{TestEnums.ENUM2.VALUE3}" );
        enumsAnnotation.setValue( "enum2ArrayParam", createEnumArrayParam( TestEnums.ENUM2.VALUE3 ) );

        dataObject.addAnnotation( enumsAnnotation );


        /*
        @ClassAnnotation( classParam = java.util.Collection.class,
                classArrayParam = { List.class }
        )
        */
        Annotation classAnnotation = createAnnotation( ClassAnnotation.class );
        classAnnotation.setValue( "classParam", "java.util.Collection.class" );
        classAnnotation.setValue( "classArrayParam", createArrayParam( "List.class" ) );
        dataObject.addAnnotation( classAnnotation );


        /*
        @AnnotationValuesAnnotation( primitivesParam = @PrimitivesAnnotation( stringParam = "\"line1\" \n line2 \\ \n line3" ),
            primitivesArrayParam = { @PrimitivesAnnotation( intParam = 2 ), @PrimitivesAnnotation( intParam = 3 ) },
            enumsParam = @EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE2 ),
            enumsArrayParam = { @EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE2 ), @EnumsAnnotation( enum1Param = TestEnums.ENUM1.VALUE3 ) },
            classAnnotationParam = @ClassAnnotation( classParam = Set.class ),
            classAnnotationArrayParam = { @ClassAnnotation( classParam = Set.class ), @ClassAnnotation( classParam = Set.class ) }
        )
        */

        Annotation annotationValuesAnnotation = createAnnotation( AnnotationValuesAnnotation.class );

        //annotation.setValue( "primitivesParam", "@PrimitivesAnnotation(stringParam="\"line1\" \n line2 \\ \n line3")" );
        Annotation primitivesParamValue = createAnnotation( PrimitivesAnnotation.class );
        // construct the value -> "\"line1\" \n line2 \\ \n line3"
        StringBuilder stringParamBuilder = new StringBuilder( );
        stringParamBuilder.append( '"' ).append( "line1" ).append( '"' ).append( " " ).append( '\n' );
        stringParamBuilder.append( " line2 " ).append( '\\' ).append( " " ).append( '\n' );
        stringParamBuilder.append( " line3" );
        primitivesParamValue.setValue( "stringParam", stringParamBuilder.toString() );

        annotationValuesAnnotation.setValue( "primitivesParam", primitivesParamValue );

        //@PrimitivesAnnotation(intParam=1)
        Annotation primitiveValue1 = createAnnotation( PrimitivesAnnotation.class );
        primitiveValue1.setValue( "intParam", 2 );

        //@PrimitivesAnnotation(intParam=3)
        Annotation primitiveValue2 = createAnnotation( PrimitivesAnnotation.class );
        primitiveValue2.setValue( "intParam", 3 );

        List<Annotation> primitivesArrayParamValue = new ArrayList<Annotation>(  );
        primitivesArrayParamValue.add( primitiveValue1 );
        primitivesArrayParamValue.add( primitiveValue2 );

        // annotation.setValue( "primitivesArrayParam", "{@PrimitivesAnnotation(intParam=2),@PrimitivesAnnotation(intParam=3)}" );
        annotationValuesAnnotation.setValue( "primitivesArrayParam", primitivesArrayParamValue );

        //annotation.setValue( "enumsParam", "@EnumsAnnotation(enum1Param=TestEnums.ENUM1.VALUE2)" );
        Annotation enumsParamValue = createAnnotation( EnumsAnnotation.class );
        enumsParamValue.setValue( "enum1Param", TestEnums.ENUM1.VALUE2.toString() );
        annotationValuesAnnotation.setValue( "enumsParam", enumsParamValue);


        //annotation.setValue( "enumsArrayParam", "{@EnumsAnnotation(enum1Param=TestEnums.ENUM1.VALUE2),@EnumsAnnotation(enum1Param=TestEnums.ENUM1.VALUE3)}" );
        Annotation enumArrayParam1 = createAnnotation( EnumsAnnotation.class );
        enumArrayParam1.setValue( "enum1Param", TestEnums.ENUM1.VALUE2.toString() );

        Annotation enumArrayParam2 = createAnnotation( EnumsAnnotation.class );
        enumArrayParam2.setValue( "enum1Param", TestEnums.ENUM1.VALUE3.toString() );

        List<Annotation> enumVarrayValues = new ArrayList<Annotation>(  );
        enumVarrayValues.add( enumArrayParam1 );
        enumVarrayValues.add( enumArrayParam2 );
        annotationValuesAnnotation.setValue( "enumsArrayParam", enumVarrayValues );

        //annotation.setValue( "classAnnotationParam", "@ClassAnnotation(classParam=Set.class)" );
        Annotation classAnnotationParamValue = createAnnotation( ClassAnnotation.class );
        classAnnotationParamValue.setValue( "classParam", "Set.class" );
        annotationValuesAnnotation.setValue( "classAnnotationParam", classAnnotationParamValue );


        //annotation.setValue( "classAnnotationArrayParam", "{@ClassAnnotation(classParam=Set.class),@ClassAnnotation(classParam=Set.class)}" );
        Annotation classAnnotationArrayParamValue1 = createAnnotation( ClassAnnotation.class );
        classAnnotationArrayParamValue1.setValue( "classParam", "Set.class" );

        Annotation classAnnotationArrayParamValue2 = createAnnotation( ClassAnnotation.class );
        classAnnotationArrayParamValue2.setValue( "classParam", "Set.class" );

        List<Annotation> classAnnotationArrayParamValue = new ArrayList<Annotation>(  );
        classAnnotationArrayParamValue.add( classAnnotationArrayParamValue1 );
        classAnnotationArrayParamValue.add( classAnnotationArrayParamValue2 );

        annotationValuesAnnotation.setValue( "classAnnotationArrayParam", classAnnotationArrayParamValue );
        dataObject.addAnnotation( annotationValuesAnnotation );

        return dataObject;
    }

    private DataObject createDataObject( String packageName, String name, String superClassName ) {
        DataObject dataObject = new DataObjectImpl( packageName, name );
        dataObject.setSuperClassName( superClassName );
        return dataObject;
    }

    private Annotation createAnnotation( Class cls ) {
        return createAnnotation( cls, null, null );
    }

    private Annotation createAnnotation( Class cls, String memberName, Object value ) {

        AnnotationDefinition annotationDefinition = DriverUtils.buildAnnotationDefinition( cls );
        Annotation annotation = new AnnotationImpl( annotationDefinition );

        if ( memberName != null ) {
            annotation.setValue( memberName, value );
        }

        return annotation;
    }

    private String readFile( org.uberfire.java.nio.file.Path path ) {
        String substring = path.toString().substring( path.toString().indexOf( "test-classes" ) + "test-classes".length() );
        InputStream resourceAsStream = getClass().getResourceAsStream( substring );

        StringBuilder drl = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader( new InputStreamReader( resourceAsStream ) );
            String line;
            while ( ( line = reader.readLine() ) != null ) {
                drl.append( line ).append( "\n" );
            }
            resourceAsStream.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        return drl.toString();
    }

    private List<String> createEnumArrayParam( Enum<?>...params ) {
        List<String> values = new ArrayList<String>(  );
        for ( Enum<?> param : params ) {
            values.add( param.name() );
        }
        return values;
    }

    private List<Object> createArrayParam( Object... params ) {
        List<Object> values = new ArrayList<Object>(  );
        for ( Object param : params ) {
            values.add( param);
        }
        return values;
    }

}
