/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.DataModelImpl;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;

/**
 * Tests for DataModelService
 */
public class DataModelerServiceTest extends DataModelerServiceBaseTest {

    @Test
    public void testDataModelerService() throws Exception {

        final URL packageUrl = this.getClass().getResource( "/DataModelerTest1" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        KieProject project = projectService.resolveProject( packagePath );

        DataModel dataModelOriginal = createModel();

        org.kie.workbench.common.services.datamodeller.core.DataModel dataModel = dataModelService.loadModel( project );
        Map<String, DataObject> objectsMap = new HashMap<String, DataObject>();

        assertNotNull( dataModel );

        assertEquals( dataModelOriginal.getDataObjects().size(), dataModel.getDataObjects().size() );

        for ( DataObject dataObject : dataModel.getDataObjects() ) {
            objectsMap.put( dataObject.getClassName(), dataObject );
        }

        for ( DataObject dataObject : dataModelOriginal.getDataObjects() ) {
            org.kie.workbench.common.services.datamodeller.DataModelerAssert.assertEqualsDataObject( dataObject, objectsMap.get( dataObject.getClassName() ) );
        }

    }

    private DataModel createModel() {
        DataModel dataModel = new DataModelImpl();

        DataObject pojo1 = createDataObject( "t1p1",  "Pojo1", "t1p2.Pojo2" );
        dataModel.addDataObject( pojo1 );

        Annotation annotation = createAnnotation( systemAnnotations, null, TypeSafe.class.getName(), "value", Boolean.TRUE );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( systemAnnotations, null, Role.class.getName(), "value", "EVENT" );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( systemAnnotations, null, Label.class.getName(), "value", "Pojo1Label" );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( systemAnnotations, null, Description.class.getName(), "value", "Pojo1Description" );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( systemAnnotations, null, Duration.class.getName(), "value", "duration" );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( systemAnnotations, null, Timestamp.class.getName(), "value", "timestamp" );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( systemAnnotations, null, ClassReactive.class.getName(), null, null );
        pojo1.addAnnotation( annotation );

        annotation = createAnnotation( systemAnnotations, null, Expires.class.getName(), "value", "1h25m" );
        pojo1.addAnnotation( annotation );

        addProperty( pojo1, "serialVersionUID", "long", true, false, null );
        ObjectProperty property = addProperty( pojo1, "field1", "java.lang.Character", true, false, null );

        annotation = createAnnotation( systemAnnotations, null, Position.class.getName(), "value", 0 );
        property.addAnnotation( annotation );

        annotation = createAnnotation( systemAnnotations, null, Key.class.getName(), null, null );
        property.addAnnotation( annotation );

        annotation = createAnnotation( systemAnnotations, null, Label.class.getName(), "value", "field1Label" );
        property.addAnnotation( annotation );

        annotation = createAnnotation( systemAnnotations, null, Description.class.getName(), "value", "field1Description" );
        property.addAnnotation( annotation );

        property = addProperty( pojo1, "duration", "java.lang.Integer", true, false, null );
        annotation = createAnnotation( systemAnnotations, null, Position.class.getName(), "value", 1 );
        property.addAnnotation( annotation );

        property = addProperty( pojo1, "timestamp", "java.util.Date", true, false, null );
        annotation = createAnnotation( systemAnnotations, null, Position.class.getName(), "value", 2 );
        property.addAnnotation( annotation );

        property = addProperty( pojo1, "field2", "char", true, false, null );

        annotation = createAnnotation( systemAnnotations, null, Position.class.getName(), "value", 3 );
        property.addAnnotation( annotation );

        annotation = createAnnotation( systemAnnotations, null, Key.class.getName(), null, null );
        property.addAnnotation( annotation );

        annotation = createAnnotation( systemAnnotations, null, Label.class.getName(), "value", "field2Label" );
        property.addAnnotation( annotation );

        annotation = createAnnotation( systemAnnotations, null, Description.class.getName(), "value", "field2Description" );
        property.addAnnotation( annotation );

        dataModel.getDataObjects().add( pojo1 );

        DataObject pojo2 = createDataObject( "t1p2", "Pojo2", null );
        dataModel.addDataObject( pojo2 );

        return dataModel;

    }

}
