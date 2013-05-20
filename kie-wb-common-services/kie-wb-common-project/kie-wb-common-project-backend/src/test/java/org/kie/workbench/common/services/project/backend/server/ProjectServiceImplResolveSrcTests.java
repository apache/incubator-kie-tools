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
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;

/**
 * Tests for ProjectServiceImpl resolveSrcPackage
 */
public class ProjectServiceImplResolveSrcTests {

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
    public void testResolveSrcPackageWithNonProjectPath() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL testUrl = this.getClass().getResource( "/" );
        final org.kie.commons.java.nio.file.Path testNioPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( testNioPath );

        //Test a non-Project Path resolves to null
        final Path result = projectService.resolveSrcPackage( testPath );
        assertNull( result );
    }

    @Test
    public void testResolveSrcPackageWithRootPath() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource("/ProjectBackendTestProject1");
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        //Test a root resolves to null
        final Path result = projectService.resolveSrcPackage( rootPath );
        assertNull( result );
    }

    @Test
    public void testResolveSrcPackageWithSrcPath() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource("/ProjectBackendTestProject1/src");
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        //Test a root/src resolves to null
        final Path result = projectService.resolveSrcPackage( rootPath );
        assertNull( result );
    }

    @Test
    public void testResolveSrcPackageWithMainPath() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource("/ProjectBackendTestProject1/src/main");
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        //Test a root/src/main resolves to null
        final Path result = projectService.resolveSrcPackage( rootPath );
        assertNull( result );
    }

    @Test
    public void testResolveSrcPackageDefaultJava() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource("/ProjectBackendTestProject1/src/main/java");
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource("/ProjectBackendTestProject1/src/main/java");
        final org.kie.commons.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test /src/main/java resolves as the default package
        final Path result = projectService.resolveSrcPackage( testPath );
        assertEquals( rootPath.toURI(),
                      result.toURI() );
    }

    @Test
    public void testResolveSrcPackageDefaultResources() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource("/ProjectBackendTestProject1/src/main/resources");
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource("/ProjectBackendTestProject1/src/main/resources");
        final org.kie.commons.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test /src/main/resources resolves as the default package
        final Path result = projectService.resolveSrcPackage( testPath );
        assertEquals( rootPath.toURI(),
                      result.toURI() );
    }

    @Test
    public void testResolveSrcPackageWithJavaFileInPackage() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource("/ProjectBackendTestProject1/src/main/java/org/kie/test/project/backend");
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource("/ProjectBackendTestProject1/src/main/java/org/kie/test/project/backend/Bean.java");
        final org.kie.commons.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test a Java file resolves to the containing package
        final Path result = projectService.resolveSrcPackage( testPath );
        assertEquals( rootPath.toURI(),
                      result.toURI() );
    }

    @Test
    public void testResolveSrcPackageWithResourcesFileInPackage() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL rootUrl = this.getClass().getResource("/ProjectBackendTestProject1/src/main/resources/org/kie/test/project/backend");
        final org.kie.commons.java.nio.file.Path nioRootPath = fs.getPath( rootUrl.toURI() );
        final Path rootPath = paths.convert( nioRootPath );

        final URL testUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/main/resources//org/kie/test/project/backend/rule1.drl" );
        final org.kie.commons.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test a Resources file resolves to the containing package
        final Path result = projectService.resolveSrcPackage( testPath );
        assertEquals( rootPath.toURI(),
                      result.toURI() );
    }

    @Test
    public void testResolveTestPackageDefaultJava() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL testUrl = this.getClass().getResource("/ProjectBackendTestProject1/src/test/java");
        final org.kie.commons.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test /src/test/java resolves to null
        final Path result = projectService.resolveSrcPackage( testPath );
        assertNull( result );
    }

    @Test
    public void testResolveTestPackageDefaultResources() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL testUrl = this.getClass().getResource("/ProjectBackendTestProject1/src/test/resources");
        final org.kie.commons.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test /src/test/resources resolves to null
        final Path result = projectService.resolveSrcPackage( testPath );
        assertNull( result );
    }

    @Test
    public void testResolveTestPackageWithJavaFileInPackage() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL testUrl = this.getClass().getResource("/ProjectBackendTestProject1/src/test/java/org/kie/test/project/backend/BeanTest.java");
        final org.kie.commons.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test a Test Java file resolves to null
        final Path result = projectService.resolveSrcPackage( testPath );
        assertNull( result );
    }

    @Test
    public void testResolveTestPackageWithResourcesFileInPackage() throws Exception {

        final Bean projectServiceBean = (Bean) beanManager.getBeans( ProjectService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( projectServiceBean );
        final ProjectService projectService = (ProjectService) beanManager.getReference( projectServiceBean,
                                                                                         ProjectService.class,
                                                                                         cc );

        final URL testUrl = this.getClass().getResource( "/ProjectBackendTestProject1/src/test/resources//org/kie/test/project/backend/test.scenario" );
        final org.kie.commons.java.nio.file.Path nioTestPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( nioTestPath );

        //Test a Test Resources file resolves to null
        final Path result = projectService.resolveSrcPackage( testPath );
        assertNull( result );
    }

}
