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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataModelImpl;
import org.kie.workbench.common.services.datamodeller.driver.impl.JavaRoasterModelDriver;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

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

    class MockIOService extends IOServiceMock {

        @Override
        public String readAllString( org.uberfire.java.nio.file.Path path )
                throws IllegalArgumentException, NoSuchFileException, org.uberfire.java.nio.IOException {
            return readFile( path );
        }
    }

    private DataModel createModel() {
        DataModel dataModel = new DataModelImpl();

        DataObject pojo1 = createDataObject( dataModel, "org.kie.workbench.common.services.datamodeller.driver.package1", "Pojo1", "org.kie.workbench.common.services.datamodeller.driver.package2.Pojo2" );

        Annotation annotation = createAnnotation( TypeSafe.class.getName(), "value", "true" );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( Role.class.getName(), "value", "EVENT" );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( Label.class.getName(), "value", "Pojo1Label" );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( Description.class.getName(), "value", "Pojo1Description" );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( Duration.class.getName(), "value", "duration" );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( Timestamp.class.getName(), "value", "timestamp" );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( ClassReactive.class.getName(), null, null );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( Expires.class.getName(), "value", "1h25m" );
        pojo1.addAnnotation( annotation );

        ObjectProperty property = pojo1.addProperty( "field1", "java.lang.Character" );

        annotation = createAnnotation( Position.class.getName(), "value", "0" );
        property.addAnnotation( annotation );

        annotation = createAnnotation( Key.class.getName(), null, null );
        property.addAnnotation( annotation );

        annotation = createAnnotation( Label.class.getName(), "value", "field1Label" );
        property.addAnnotation( annotation );

        annotation = createAnnotation( Description.class.getName(), "value", "field1Description" );
        property.addAnnotation( annotation );

        property = pojo1.addProperty( "duration", "java.lang.Integer" );
        annotation = createAnnotation( Position.class.getName(), "value", "1" );
        property.addAnnotation( annotation );

        property = pojo1.addProperty( "timestamp", "java.util.Date" );
        annotation = createAnnotation( Position.class.getName(), "value", "2" );
        property.addAnnotation( annotation );

        property = pojo1.addProperty( "field2", "char" );
        annotation = createAnnotation( Position.class.getName(), "value", "3" );
        property.addAnnotation( annotation );

        annotation = createAnnotation( Key.class.getName(), null, null );
        property.addAnnotation( annotation );

        annotation = createAnnotation( Label.class.getName(), "value", "field2Label" );
        property.addAnnotation( annotation );

        annotation = createAnnotation( Description.class.getName(), "value", "field2Description" );
        property.addAnnotation( annotation );

        property = pojo1.addProperty( "serialVersionUID", "long" );

        DataObject pojo2 = createDataObject( dataModel, "org.kie.workbench.common.services.datamodeller.driver.package2", "Pojo2", null );

        return dataModel;
    }

    private DataObject createDataObject( DataModel dataModel, String packageName, String name, String superClassName ) {
        DataObject dataObject = dataModel.addDataObject( packageName, name );
        dataObject.setSuperClassName( superClassName );
        return dataObject;
    }

    private Annotation createAnnotation( String className, String memberName, String value ) {
        JavaRoasterModelDriver javaRoasterModelDriver = new JavaRoasterModelDriver();

        Annotation annotation = new AnnotationImpl( javaRoasterModelDriver.getConfiguredAnnotation( className ) );

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

}
