/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.project;

import java.net.URISyntaxException;
import java.net.URL;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.guvnor.common.services.project.backend.server.ProjectConfigurationContentHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectImportsServiceImplTest extends WeldProjectTestBase {

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    private ProjectImportsServiceImpl projectImportsService;

    @Mock
    private IOService ioService;

    private Path pathToImports;

    @Before
    public void setUp() throws Exception {
        super.startWeld();

        //Instantiate Paths used in tests for Path conversion
        final Bean pathsBean = (Bean) beanManager.getBeans( Paths.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( pathsBean );

        Paths paths = (Paths) beanManager.getReference( pathsBean,
                                                        Paths.class,
                                                        cc );

        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();

        final URL packageUrl = this.getClass().getResource( "/ProjectBackendTestProjectStructureValid/package-names-white-list" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        pathToImports = paths.convert( nioPackagePath );

        projectImportsService = new ProjectImportsServiceImpl( ioService,
                                                               new ProjectConfigurationContentHandler() );
    }

    @After
    public void cleanUp() {
        super.stopWeld();
    }

    @Test
    public void testPackageNameWhiteList() throws URISyntaxException {

        when( ioService.exists( any( org.uberfire.java.nio.file.Path.class ) ) ).thenReturn( false );

        projectImportsService.saveProjectImports( pathToImports );

        verify( ioService ).write( any( org.uberfire.java.nio.file.Path.class ),
                                   eq( "<configuration>\n" +
                                               "  <imports>\n" +
                                               "    <imports>\n" +
                                               "      <import>\n" +
                                               "        <type>java.lang.Number</type>\n" +
                                               "      </import>\n" +
                                               "    </imports>\n" +
                                               "  </imports>\n" +
                                               "  <version>1.0</version>\n" +
                                               "</configuration>" ) );
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void testPackageNameWhiteListFileExists() throws URISyntaxException {

        when( ioService.exists( any( org.uberfire.java.nio.file.Path.class ) ) ).thenReturn( true );

        projectImportsService.saveProjectImports( pathToImports );
    }

    @Test
    public void testProjectImportsLoad_Exists() throws URISyntaxException {

        when( ioService.exists( any( org.uberfire.java.nio.file.Path.class ) ) ).thenReturn( true );

        projectImportsService.load( pathToImports );

        verify( ioService,
                never() ).write( any( org.uberfire.java.nio.file.Path.class ),
                                 any( String.class ) );
        verify( ioService,
                times( 1 ) ).readAllString( any( org.uberfire.java.nio.file.Path.class ) );
    }

    @Test
    public void testProjectImportsLoad_NotExists() throws URISyntaxException {

        when( ioService.exists( any( org.uberfire.java.nio.file.Path.class ) ) ).thenReturn( false );

        projectImportsService.load( pathToImports );

        verify( ioService,
                times( 1 ) ).write( any( org.uberfire.java.nio.file.Path.class ),
                                    eq( "<configuration>\n" +
                                                "  <imports>\n" +
                                                "    <imports>\n" +
                                                "      <import>\n" +
                                                "        <type>java.lang.Number</type>\n" +
                                                "      </import>\n" +
                                                "    </imports>\n" +
                                                "  </imports>\n" +
                                                "  <version>1.0</version>\n" +
                                                "</configuration>" ) );
        verify( ioService,
                times( 1 ) ).readAllString( any( org.uberfire.java.nio.file.Path.class ) );
    }

}