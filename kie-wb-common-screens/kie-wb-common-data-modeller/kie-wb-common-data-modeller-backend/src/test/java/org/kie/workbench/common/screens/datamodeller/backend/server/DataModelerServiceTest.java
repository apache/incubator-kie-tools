package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.net.URL;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;

/**
 * Tests for DataModelService
 */
public class DataModelerServiceTest {

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
    public void testDataModelerService() throws Exception {

        //Create DataModelerService bean
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelerService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        final DataModelerService dataModelService = (DataModelerService) beanManager.getReference( dataModelServiceBean,
                DataModelerService.class,
                cc );

        //Create ProjectServiceBean
        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext pscc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                ProjectService.class,
                pscc );

        final URL packageUrl = this.getClass().getResource( "/DataModelerTest1" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        Project project = projectService.resolveProject( packagePath );



        /*
        final URL testUrl = this.getClass().getResource( "/" );
        final org.uberfire.java.nio.file.Path testNioPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( testNioPath );
        */

        //Test a non-Project Path resolves to null
        //final Package result = projectService.resolvePackage( testPath );


        DataModelTO dataModelTO = dataModelService.loadModel( project );

        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXX: " + dataModelTO);
        for ( DataObjectTO dataObjectTO : dataModelTO.getDataObjects()) {
            System.out.println("dataObjectTO: " + dataObjectTO.getClassName() );
        }

        DataObjectTO pojo1 = dataModelTO.getDataObjectByClassName( "t1p1.Pojo1" );
        assertNotNull( pojo1 );
        assertName( "Pojo1", pojo1 );
        assertPackageName( "t1p1", pojo1 );
        assertClassName( "t1p1.Pojo1", pojo1 );



        //assertNotNull( oracle );

        /*
        assertEquals( 4,
                      oracle.getProjectModelFields().size() );
        assertContains( "t3p1.Bean1",
                        oracle.getProjectModelFields().keySet() );
        assertContains( "t3p2.Bean2",
                        oracle.getProjectModelFields().keySet() );

        assertTrue( oracle.getProjectEventTypes().get( "t3p1.Bean1" ) );
        assertFalse( oracle.getProjectEventTypes().get( "t3p2.Bean2" ) );

        assertEquals( 3,
                      oracle.getProjectModelFields().get( "t3p1.Bean1" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t3p1.Bean1" ) );
        assertContains( "field1",
                        oracle.getProjectModelFields().get( "t3p1.Bean1" ) );
        assertContains( "field2",
                        oracle.getProjectModelFields().get( "t3p1.Bean1" ) );

        assertEquals( 2,
                      oracle.getProjectModelFields().get( "t3p2.Bean2" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t3p2.Bean2" ) );
        assertContains( "field1",
                        oracle.getProjectModelFields().get( "t3p2.Bean2" ) );
                        */
    }


    void assertName(String name, DataObjectTO dataObjectTO) {
        assertEquals( name, dataObjectTO.getName() );
    }

    void assertPackageName(String packageName, DataObjectTO dataObjectTO) {
        assertEquals( packageName, dataObjectTO.getPackageName() );
    }

    void assertClassName(String className, DataObjectTO dataObjectTO) {
        assertEquals( className, dataObjectTO.getClassName() );
    }



}
