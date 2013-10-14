<<<<<<< HEAD
//package org.kie.workbench.common.widgets.client.datamodel;
//
//import java.net.URL;
//import javax.enterprise.context.spi.CreationalContext;
//import javax.enterprise.inject.spi.Bean;
//import javax.enterprise.inject.spi.BeanManager;
//
//import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
//import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
//import org.jboss.weld.environment.se.StartMain;
//import org.junit.Before;
//import org.junit.Test;
//import org.kie.commons.java.nio.fs.file.SimpleFileSystemProvider;
//import org.kie.workbench.common.services.datamodel.service.DataModelService;
//import org.uberfire.backend.server.util.Paths;
//import org.uberfire.backend.vfs.Path;
//
//import static org.junit.Assert.*;
//
///**
//* Tests for DataModelService
//*/
//public class PackageDataModelServiceTests {
//
//    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
//    private BeanManager beanManager;
//    private Paths paths;
//
//    @Before
//    public void setUp() throws Exception {
//        //Bootstrap WELD container
//        StartMain startMain = new StartMain( new String[ 0 ] );
//        beanManager = startMain.go().getBeanManager();
//
//        //Instantiate Paths used in tests for Path conversion
//        final Bean pathsBean = (Bean) beanManager.getBeans( Paths.class ).iterator().next();
//        final CreationalContext cc = beanManager.createCreationalContext( pathsBean );
//        paths = (Paths) beanManager.getReference( pathsBean,
//                                                  Paths.class,
//                                                  cc );
//
//        //Ensure URLs use the default:// scheme
//        fs.forceAsDefault();
//    }
//
//    @Test
//    public void testPackageDataModelOracle() throws Exception {
//        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
//        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
//        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
//                                                                                               DataModelService.class,
//                                                                                               cc );
//
//        final URL packageUrl = this.getClass().getResource( "/DataModelBackendTest1/src/main/java/t3p1" );
//        final org.kie.commons.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
//        final Path packagePath = paths.convert( nioPackagePath );
//
//        final PackageDataModelOracle oracle = dataModelService.getDataModel( packagePath );
//
//        assertNotNull( oracle );
//        assertEquals( 2,
//                      oracle.getAllFactTypes().length );
//        DataModelOracleTestUtils.assertContains( "t3p1.Bean1",
//                                                 oracle.getAllFactTypes() );
//        DataModelOracleTestUtils.assertContains( "t3p2.Bean2",
//                                                 oracle.getAllFactTypes() );
//
//        assertEquals( 1,
//                      oracle.getFactTypes().length );
//        DataModelOracleTestUtils.assertContains( "Bean1",
//                                                 oracle.getFactTypes() );
//        assertEquals( 3,
//                      oracle.getFieldCompletions( "Bean1" ).length );
//        DataModelOracleTestUtils.assertContains( "this",
//                                                 oracle.getFieldCompletions( "Bean1" ) );
//        DataModelOracleTestUtils.assertContains( "field1",
//                                                 oracle.getFieldCompletions( "Bean1" ) );
//        DataModelOracleTestUtils.assertContains( "field2",
//                                                 oracle.getFieldCompletions( "Bean1" ) );
//
//        assertEquals( 1,
//                      oracle.getExternalFactTypes().length );
//        DataModelOracleTestUtils.assertContains( "t3p2.Bean2",
//                                                 oracle.getExternalFactTypes() );
//    }
//
//    @Test
//    public void testProjectDataModelOracle() throws Exception {
//        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
//        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
//        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
//                                                                                               DataModelService.class,
//                                                                                               cc );
//
//        final URL packageUrl = this.getClass().getResource( "/DataModelBackendTest1/src/main/java/t3p1" );
//        final org.kie.commons.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
//        final Path packagePath = paths.convert( nioPackagePath );
//
//        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );
//
//        assertNotNull( oracle );
//
//        assertEquals( 2,
//                      oracle.getFactTypes().length );
//        DataModelOracleTestUtils.assertContains( "t3p1.Bean1",
//                                                 oracle.getFactTypes() );
//        DataModelOracleTestUtils.assertContains( "t3p2.Bean2",
//                                                 oracle.getFactTypes() );
//
//        assertEquals( 3,
//                      oracle.getFieldCompletions( "t3p1.Bean1" ).length );
//        DataModelOracleTestUtils.assertContains( "this",
//                                                 oracle.getFieldCompletions( "t3p1.Bean1" ) );
//        DataModelOracleTestUtils.assertContains( "field1",
//                                                 oracle.getFieldCompletions( "t3p1.Bean1" ) );
//        DataModelOracleTestUtils.assertContains( "field2",
//                                                 oracle.getFieldCompletions( "t3p1.Bean1" ) );
//
//        assertEquals( 2,
//                      oracle.getFieldCompletions( "t3p2.Bean2" ).length );
//        DataModelOracleTestUtils.assertContains( "this",
//                                                 oracle.getFieldCompletions( "t3p2.Bean2" ) );
//        DataModelOracleTestUtils.assertContains( "field1",
//                                                 oracle.getFieldCompletions( "t3p2.Bean2" ) );
//    }
//
//    @Test
//    public void testProjectDataModelOracleJavaDefaultPackage() throws Exception {
//        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
//        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
//        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
//                                                                                               DataModelService.class,
//                                                                                               cc );
//
//        final URL packageUrl = this.getClass().getResource( "/DataModelBackendTest2/src/main/java" );
//        final org.kie.commons.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
//        final Path packagePath = paths.convert( nioPackagePath );
//
//        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );
//
//        assertNotNull( oracle );
//
//        assertEquals( 1,
//                      oracle.getFactTypes().length );
//        DataModelOracleTestUtils.assertContains( "Bean1",
//                                                 oracle.getFactTypes() );
//
//        assertEquals( 3,
//                      oracle.getFieldCompletions( "Bean1" ).length );
//        DataModelOracleTestUtils.assertContains( "this",
//                                                 oracle.getFieldCompletions( "Bean1" ) );
//        DataModelOracleTestUtils.assertContains( "field1",
//                                                 oracle.getFieldCompletions( "Bean1" ) );
//        DataModelOracleTestUtils.assertContains( "field2",
//                                                 oracle.getFieldCompletions( "Bean1" ) );
//    }
//
//}

=======
package org.kie.workbench.common.widgets.client.datamodel;

import java.net.URL;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Test;
import org.kie.commons.java.nio.fs.file.SimpleFileSystemProvider;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.widgets.client.callbacks.Callback;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.kie.workbench.common.widgets.client.datamodel.PackageDataModelOracleTestUtils.*;
import static org.mockito.Mockito.*;

/**
 * Tests for DataModelService
 */
public class PackageDataModelServiceTests {

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
    public void testPackageDataModelOracle() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendTest1/src/main/java/t3p1" );
        final org.kie.commons.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final PackageDataModelOracle packageLoader = dataModelService.getDataModel( packagePath );

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller();
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertNotNull( oracle );
        assertEquals( 2,
                      oracle.getAllFactTypes().length );
        assertContains( "t3p1.Bean1",
                        oracle.getAllFactTypes() );
        assertContains( "t3p2.Bean2",
                        oracle.getAllFactTypes() );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertContains( "Bean1",
                        oracle.getFactTypes() );

        oracle.getFieldCompletions( "Bean1",
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            assertEquals( 3,
                                                          fields.length );
                                            assertContains( "this",
                                                            fields );
                                            assertContains( "field1",
                                                            fields );
                                            assertContains( "field2",
                                                            fields );
                                        }
                                    } );

        assertEquals( 1,
                      oracle.getExternalFactTypes().length );
        assertContains( "t3p2.Bean2",
                        oracle.getExternalFactTypes() );
    }

    @Test
    public void testProjectDataModelOracle() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendTest1/src/main/java/t3p1" );
        final org.kie.commons.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle packageLoader = dataModelService.getProjectDataModel( packagePath );

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller();
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertNotNull( oracle );

        assertEquals( 2,
                      oracle.getFactTypes().length );
        assertContains( "t3p1.Bean1",
                        oracle.getFactTypes() );
        assertContains( "t3p2.Bean2",
                        oracle.getFactTypes() );

        oracle.getFieldCompletions( "t3p1.Bean1",
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            assertEquals( 3,
                                                          fields.length );
                                            assertContains( "this",
                                                            fields );
                                            assertContains( "field1",
                                                            fields );
                                            assertContains( "field2",
                                                            fields );
                                        }
                                    } );

        oracle.getFieldCompletions( "t3p2.Bean2",
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            assertEquals( 2,
                                                          fields.length );
                                            assertContains( "this",
                                                            fields );
                                            assertContains( "field1",
                                                            fields );
                                        }
                                    } );
    }

    @Test
    public void testProjectDataModelOracleJavaDefaultPackage() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendTest2/src/main/java" );
        final org.kie.commons.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle packageLoader = dataModelService.getProjectDataModel( packagePath );

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller();
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        assertNotNull( oracle );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertContains( "Bean1",
                        oracle.getFactTypes() );

        oracle.getFieldCompletions( "Bean1",
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            assertEquals( 3,
                                                          fields.length );
                                            assertContains( "this",
                                                            fields );
                                            assertContains( "field1",
                                                            fields );
                                            assertContains( "field2",
                                                            fields );
                                        }
                                    } );
    }

}
>>>>>>> BZ1014071 - DataModelOracle: Performance issues when model is large. Tests.
