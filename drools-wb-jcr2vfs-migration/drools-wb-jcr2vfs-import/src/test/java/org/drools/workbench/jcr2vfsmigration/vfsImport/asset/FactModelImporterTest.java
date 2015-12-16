/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.jcr2vfsmigration.vfsImport.asset;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.jcr2vfsmigration.util.MigrationPathManager;
import org.drools.workbench.jcr2vfsmigration.xml.model.Module;
import org.drools.workbench.jcr2vfsmigration.xml.model.ModuleType;
import org.drools.workbench.jcr2vfsmigration.xml.model.asset.DataModelAsset;
import org.guvnor.common.services.project.service.ProjectService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.definition.type.Role;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataModelImpl;
import org.kie.workbench.common.services.datamodeller.util.DriverUtils;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FactModelImporterTest {

    @Mock
    private IOService ioService;

    @Mock
    private MigrationPathManager migrationPathManager;

    @Mock
    private DataModelerService modelerService;

    @Mock
    private ProjectService<KieProject> projectService;

    @InjectMocks
    FactModelImporter factModelImporter = new FactModelImporter();

    private static Map<String, AnnotationDefinition> annotationDefinitions = new HashMap<String, AnnotationDefinition>(  );

    static {
        annotationDefinitions.put( Role.class.getName(), DriverUtils.buildAnnotationDefinition( Role.class ) );
    }

    @Test
    public void testFactsImportGeneratedModel() {

        Module xmlModule = buildModule();
        DataModelAsset xmlAsset = buildDataModelAsset();

        Path path = mock( Path.class );
        KieProject project = mock( KieProject.class );

        when( migrationPathManager.generatePathForAsset( xmlModule, xmlAsset, xmlAsset.getAssetType().toString() ) ).thenReturn( path );
        when( projectService.resolveProject( path ) ).thenReturn( project );
        when( modelerService.getAnnotationDefinitions() ).thenReturn( annotationDefinitions );

        factModelImporter.importAsset( xmlModule, xmlAsset, null );

        DataModel expectedDataModel = buildExpectedDataModel();

        //The importer should create the expected data model.
        verify( modelerService, times( 1 )).saveModel( eq( expectedDataModel ), eq( project ) );

    }

    /**
     * Creates the input module for the FactModelImporter.
     */
    private Module buildModule() {

        Module module = new Module( ModuleType.NORMAL,
                "uuid-test-module", //not relevant for the test
                "test-module", //not relevant for the test
                "test-user", //not relevant for the test
                "last checkin comment", //not relevant for the test
                new Date(), //nor relevant for the test
                "org.kie.test", //At import time, the package is taken from the Module.
                null, //not relevant for the test
                null, //not relevant for the test
                null, //not relevant for the test
                null  //not relevant for the test
                );
        return module;
    }

    /**
     * Creates the DataModelAsset for the FactModelImporter.
     */
    private DataModelAsset buildDataModelAsset() {

        DataModelAsset dataModelAsset = new DataModelAsset( "TestFact", "drl", "test-user", "last checkin comment",  new Date() );
        DataModelAsset.DataModelObject dataModelObject = dataModelAsset.addDataModelObject( "TestFact", "java.lang.Object" );

        dataModelObject.addObjectAnnotation( "Role", "value", Role.Type.EVENT.name() );
        dataModelObject.addObjectProperty( "field1", "java.lang.String" );

        return dataModelAsset;
    }

    /**
     * Creates the expected result.
     */
    private DataModel buildExpectedDataModel() {
        DataModel dataModel = new DataModelImpl();

        DataObject dataObject = dataModel.addDataObject( "org.kie.test", "TestFact" );
        dataObject.setSuperClassName( "java.lang.Object" );

        Annotation annotation = new AnnotationImpl( annotationDefinitions.get( Role.class.getName() ) );
        annotation.setValue( "value", Role.Type.EVENT.name() );
        dataObject.addAnnotation( annotation );

        dataObject.addProperty( "field1", "java.lang.String" );

        return dataModel;
    }

}