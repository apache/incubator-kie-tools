package org.kie.workbench.common.services.datamodel.backend.server;

import java.net.URL;
import java.util.HashSet;
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
public class ProjectDataModelSuperTypesTest {

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
    public void testProjectSuperTypes() throws Exception {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelService dataModelService = (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                                                               DataModelService.class,
                                                                                               cc );

        final URL packageUrl = this.getClass().getResource( "/DataModelBackendSuperTypesTest1/src/main/java/t2p1" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 6,
                      oracle.getProjectModelFields().size() );
        assertContains( "t2p1.Bean1",
                        oracle.getProjectModelFields().keySet() );
        assertContains( "t2p1.Bean2",
                        oracle.getProjectModelFields().keySet() );
        assertContains( "t2p2.Bean3",
                        oracle.getProjectModelFields().keySet() );
        assertContains( "t2p1.Bean4",
                        oracle.getProjectModelFields().keySet() );

        assertContains( "java.lang.Object",
                        new HashSet<String>( oracle.getProjectSuperTypes().get( "t2p1.Bean1" ) ) );
        assertContains( "t2p1.Bean1",
                        new HashSet<String>( oracle.getProjectSuperTypes().get( "t2p1.Bean2" ) ) );
        assertContains( "t2p1.Bean1",
                        new HashSet<String>( oracle.getProjectSuperTypes().get( "t2p2.Bean3" ) ) );
        assertContains( "t2p2.Bean3",
                        new HashSet<String>( oracle.getProjectSuperTypes().get( "t2p1.Bean4" ) ) );
    }

}
