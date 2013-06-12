package org.kie.workbench.common.services.project.backend.server;

import java.net.URL;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Test;
import org.kie.commons.java.nio.fs.file.SimpleFileSystemProvider;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.shared.context.Package;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;

/**
 * Tests for ProjectServiceImpl resolveTestPackage
 */
public class ProjectServiceImplResolveTestTests {

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
    public void testResolveTestPackageWithNonProjectPath() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL testUrl = this.getClass().getResource( "/" );
        final org.kie.commons.java.nio.file.Path testNioPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( testNioPath );

        //Test a non-Project Path resolves to null
        final Package result = projectService.resolvePackage( testPath );
        assertNull( result );
    }

    @Test
    public void testResolveTestPackageWithRootPath() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1" );
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        //Test a root resolves to null
        final Package result = projectService.resolvePackage( rootPath );
        assertNull( result );
    }

    @Test
    public void testResolveTestPackageWithSrcPath() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src" );
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        //Test a root/src resolves to null
        final Package result = projectService.resolvePackage( rootPath );
        assertNull( result );
    }

    @Test
    public void testResolveTestPackageWithMainPath() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test" );
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        //Test a root/src/test resolves to null
        final Package result = projectService.resolvePackage( rootPath );
        assertNull( result );
    }

    @Test
    public void testResolveTestPackageDefaultJava() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test/java" );
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test/java" );
        final org.kie.commons.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test /src/test/java resolves as the default package
        final Package result = projectService.resolvePackage( testPath );
        assertEquals( rootPath.toURI(),
                      result.getPackageTestSrcPath().toURI() );
    }

    @Test
    public void testResolveTestPackageDefaultResources() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test/resources" );
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test/resources" );
        final org.kie.commons.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test /src/test/resources resolves as the default package
        final Package result = projectService.resolvePackage( testPath );
        assertEquals( rootPath.toURI(),
                      result.getPackageTestResourcesPath().toURI() );
    }

    @Test
    public void testResolveTestPackageWithJavaFileInPackage() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test/java/org/kie/test/project/backend" );
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test/java/org/kie/test/project/backend/BeanTest.java" );
        final org.kie.commons.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test a Java file resolves to the containing package
        final Package result = projectService.resolvePackage( testPath );
        assertEquals( rootPath.toURI(),
                      result.getPackageTestSrcPath().toURI() );
    }

    @Test
    public void testResolveTestPackageWithResourcesFileInPackage() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test/resources/org/kie/test/project/backend" );
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test/resources/org/kie/test/project/backend/test.scenario" );
        final org.kie.commons.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test a Resources file resolves to the containing package
        final Package result = projectService.resolvePackage( testPath );
        assertEquals( rootPath.toURI(),
                      result.getPackageTestResourcesPath().toURI() );
    }

}
