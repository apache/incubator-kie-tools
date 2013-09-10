package org.kie.workbench.common.services.datamodel.backend.server;

import java.net.URL;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Test;
import org.kie.commons.java.nio.fs.file.SimpleFileSystemProvider;
import org.kie.workbench.common.services.datamodel.service.DataModelService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.kie.workbench.common.services.datamodel.backend.server.DataModelOracleTestUtils.assertContains;

/**
 * Tests for DataModelService
 */
public class DataModelExtendJavaTypeTests {

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
    public void testPackageExtendJavaTypeWithQualifiedDRLBeanName() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendExtendJavaTypeTest1/src/main/java/t4p1" );
        final org.kie.commons.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final PackageDataModelOracle oracle = dataModelService.getDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertContains( "Bean1",
                        oracle.getFactTypes() );

        assertTrue( oracle.isFactTypeAnEvent( "Bean1" ) );
    }

    @Test
    public void testProjectExtendJavaTypeWithQualifiedDRLBeanName() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendExtendJavaTypeTest1" );
        final org.kie.commons.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertContains( "t4p1.Bean1",
                        oracle.getFactTypes() );

        assertTrue( oracle.isFactTypeAnEvent( "t4p1.Bean1" ) );
    }

    @Test
    public void testPackageExtendJavaTypeWithImport() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendExtendJavaTypeTest2/src/main/java/t5p1" );
        final org.kie.commons.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final PackageDataModelOracle oracle = dataModelService.getDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertContains( "Bean1",
                        oracle.getFactTypes() );

        assertTrue( oracle.isFactTypeAnEvent( "Bean1" ) );
    }

    @Test
    public void testProjectExtendJavaTypeWithImport() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendExtendJavaTypeTest2" );
        final org.kie.commons.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 1,
                      oracle.getFactTypes().length );
        assertContains( "t5p1.Bean1",
                        oracle.getFactTypes() );

        assertTrue( oracle.isFactTypeAnEvent( "t5p1.Bean1" ) );
    }

}
