package org.kie.workbench.common.services.datamodel.backend.server;

import java.net.URL;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;
import static org.kie.workbench.common.services.datamodel.backend.server.ProjectDataModelOracleTestUtils.*;

/**
 * Tests for DataModelService
 */
public class ProjectDataModelPackageWhiteListTest {

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
    public void testPackageNameWhiteList_EmptyWhiteList() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelPackageWhiteListTest1" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 2,
                      oracle.getProjectModelFields().size() );
        assertContains( "t7p1.Bean1",
                        oracle.getProjectModelFields().keySet() );
        assertContains( "t7p2.Bean2",
                        oracle.getProjectModelFields().keySet() );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t7p1.Bean1" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t7p1.Bean1" ) );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t7p2.Bean2" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t7p2.Bean2" ) );
    }

    @Test
    public void testPackageNameWhiteList_IncludeOnePackage() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelPackageWhiteListTest2" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 1,
                      oracle.getProjectModelFields().size() );
        assertContains( "t8p1.Bean1",
                        oracle.getProjectModelFields().keySet() );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t8p1.Bean1" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t8p1.Bean1" ) );
    }

    @Test
    public void testPackageNameWhiteList_IncludeAllPackages() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelPackageWhiteListTest3" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 2,
                      oracle.getProjectModelFields().size() );
        assertContains( "t9p1.Bean1",
                        oracle.getProjectModelFields().keySet() );
        assertContains( "t9p2.Bean2",
                        oracle.getProjectModelFields().keySet() );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t9p1.Bean1" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t9p1.Bean1" ) );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t9p2.Bean2" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t9p2.Bean2" ) );
    }

    @Test
    public void testPackageNameWhiteList_NoWhiteList() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelPackageWhiteListTest4" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 2,
                      oracle.getProjectModelFields().size() );
        assertContains( "t10p1.Bean1",
                        oracle.getProjectModelFields().keySet() );
        assertContains( "t10p2.Bean2",
                        oracle.getProjectModelFields().keySet() );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t10p1.Bean1" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t10p1.Bean1" ) );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t10p2.Bean2" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t10p2.Bean2" ) );
    }

    @Test
    public void testPackageNameWhiteList_Wildcards() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelPackageWhiteListTest5" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 2,
                      oracle.getProjectModelFields().size() );
        assertContains( "t11.p1.Bean1",
                        oracle.getProjectModelFields().keySet() );
        assertContains( "t11.p2.Bean2",
                        oracle.getProjectModelFields().keySet() );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t11.p1.Bean1" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t11.p1.Bean1" ) );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t11.p2.Bean2" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t11.p2.Bean2" ) );
    }

}
