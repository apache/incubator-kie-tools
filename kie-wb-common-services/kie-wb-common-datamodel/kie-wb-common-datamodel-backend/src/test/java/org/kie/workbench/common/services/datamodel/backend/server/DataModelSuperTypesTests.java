package org.kie.workbench.common.services.datamodel.backend.server;

import java.net.URL;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.drools.workbench.models.commons.shared.oracle.PackageDataModelOracle;
import org.drools.workbench.models.commons.shared.oracle.ProjectDataModelOracle;
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
public class DataModelSuperTypesTests {

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
        final org.kie.commons.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final PackageDataModelOracle oracle = dataModelService.getDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 3,
                      oracle.getFactTypes().length );
        assertContains( "Bean1",
                        oracle.getFactTypes() );
        assertContains( "Bean2",
                        oracle.getFactTypes() );
        assertContains( "Bean4",
                        oracle.getFactTypes() );

        assertNull( oracle.getSuperType( "Bean1" ) );
        assertEquals( "Bean1",
                      oracle.getSuperType( "Bean2" ) );
        assertEquals( "t2p2.Bean3",
                      oracle.getSuperType( "Bean4" ) );
    }

    @Test
    public void testProjectSuperTypes() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendSuperTypesTest1/src/main/java/t2p1" );
        final org.kie.commons.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 4,
                      oracle.getFactTypes().length );
        assertContains( "t2p1.Bean1",
                        oracle.getFactTypes() );
        assertContains( "t2p1.Bean2",
                        oracle.getFactTypes() );
        assertContains( "t2p2.Bean3",
                        oracle.getFactTypes() );
        assertContains( "t2p1.Bean4",
                        oracle.getFactTypes() );

        assertNull( oracle.getSuperType( "t2p1.Bean1" ) );
        assertEquals( "t2p1.Bean1",
                      oracle.getSuperType( "t2p1.Bean2" ) );
        assertEquals( "t2p1.Bean1",
                      oracle.getSuperType( "t2p2.Bean3" ) );
        assertEquals( "t2p2.Bean3",
                      oracle.getSuperType( "t2p1.Bean4" ) );
    }

}
