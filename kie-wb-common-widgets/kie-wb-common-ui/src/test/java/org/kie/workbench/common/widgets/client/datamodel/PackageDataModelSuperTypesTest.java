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

package org.kie.workbench.common.widgets.client.datamodel;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;
import static org.kie.workbench.common.widgets.client.datamodel.PackageDataModelOracleTestUtils.*;
import static org.mockito.Mockito.*;

/**
 * Tests for DataModelService
 */
public class PackageDataModelSuperTypesTest {

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    private BeanManager beanManager;
    private Paths paths;

    @Before
    public void setUp() throws Exception {
        //Bootstrap WELD container
        StartMain startMain = new StartMain( new String[ 0 ] );
        beanManager = startMain.go().getBeanManager();

        //Instantiate Paths used in tests for Path conversion
        final Bean pathsBean = (Bean) beanManager.getBeans( Paths.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( pathsBean );
        paths = (Paths) beanManager.getReference( pathsBean,
                                                  Paths.class,
                                                  cc );

        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();
    }

    @Test
    public void testPackageSuperTypes() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendSuperTypesTest1/src/main/java/t2p1" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final PackageDataModelOracle packageLoader = dataModelService.getDataModel( packagePath );

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( "t2p1" );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setSuperTypes( new HashMap<String, List<String>>() {{
            put( "t2p1.Bean1", null );
            put( "t2p1.Bean2", new ArrayList<String>() {{
                add( "t2p1.Bean1" );
            }} );
            put( "t2p2.Bean3", new ArrayList<String>() {{
                add( "t2p1.Bean1" );
            }} );
            put( "t2p1.Bean4", new ArrayList<String>() {{
                add( "t2p2.Bean3" );
            }} );
        }} );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertNotNull( oracle );

        assertEquals( 3,
                      oracle.getFactTypes().length );
        assertContains( "Bean1",
                        oracle.getFactTypes() );
        assertContains( "Bean2",
                        oracle.getFactTypes() );
        assertContains( "Bean4",
                        oracle.getFactTypes() );

        oracle.getSuperType( "Bean1",
                             new Callback<String>() {
                                 @Override
                                 public void callback( final String result ) {
                                     assertEquals( "java.lang.Object",
                                                   result );
                                 }
                             } );
        oracle.getSuperType( "Bean2",
                             new Callback<String>() {
                                 @Override
                                 public void callback( final String result ) {
                                     assertEquals( "Bean1",
                                                   result );
                                 }
                             } );
        oracle.getSuperType( "Bean4",
                             new Callback<String>() {
                                 @Override
                                 public void callback( final String result ) {
                                     assertEquals( "t2p2.Bean3",
                                                   result );
                                 }
                             } );
    }

    @Test
    public void testProjectSuperTypes() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendSuperTypesTest1/src/main/java/t2p1" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final PackageDataModelOracle packageLoader = dataModelService.getDataModel( packagePath );
        final ProjectDataModelOracle projectLoader = dataModelService.getProjectDataModel( packagePath );

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller( packageLoader );
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( "t2p1" );
        dataModel.setModelFields( projectLoader.getProjectModelFields() );
        dataModel.setSuperTypes( new HashMap<String, List<String>>() {{
            put( "t2p1.Bean1", null );
            put( "t2p1.Bean2", new ArrayList<String>() {{
                add( "t2p1.Bean1" );
            }} );
            put( "t2p2.Bean3", new ArrayList<String>() {{
                add( "t2p1.Bean1" );
            }} );
            put( "t2p1.Bean4", new ArrayList<String>() {{
                add( "t2p2.Bean3" );
            }} );
        }} );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertNotNull( oracle );

        assertEquals( 5,
                      oracle.getAllFactTypes().length );
        assertContains( "t2p1.Bean1",
                        oracle.getAllFactTypes() );
        assertContains( "t2p1.Bean2",
                        oracle.getAllFactTypes() );
        assertContains( "t2p2.Bean3",
                        oracle.getAllFactTypes() );
        assertContains( "t2p1.Bean4",
                        oracle.getAllFactTypes() );
        assertContains( "java.lang.String",
                        oracle.getAllFactTypes() );

        oracle.getSuperType( "Bean1",
                             new Callback<String>() {
                                 @Override
                                 public void callback( final String result ) {
                                     assertEquals( "java.lang.Object",
                                                   result );
                                 }
                             } );
        oracle.getSuperType( "Bean2",
                             new Callback<String>() {
                                 @Override
                                 public void callback( final String result ) {
                                     assertEquals( "Bean1",
                                                   result );
                                 }
                             } );
        oracle.getSuperType( "Bean4",
                             new Callback<String>() {
                                 @Override
                                 public void callback( final String result ) {
                                     assertEquals( "t2p2.Bean3",
                                                   result );
                                 }
                             } );
    }

}
