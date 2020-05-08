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

package org.kie.workbench.common.services.backend.whitelist;

import java.io.File;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.guvnor.test.TempFiles;
import org.jboss.weld.environment.se.Weld;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;

@RunWith( MockitoJUnitRunner.class )
public class PackageNameWhiteListLoaderTest {

    @Mock
    PackageNameSearchProvider packageNameSearchProvider;

    @Mock
    IOService ioService;

    private PackageNameWhiteListLoader loader;
    private Path                       pathToWhiteList;
    private TempFiles                  tempFiles;

    private Weld weld;

    @Before
    public void setUp() throws Exception {
        final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
        // Bootstrap WELD container
        weld = new Weld();
        final BeanManager beanManager = weld.initialize().getBeanManager();

        //Instantiate Paths used in tests for Path conversion
        final Bean pathsBean = ( Bean ) beanManager.getBeans( Paths.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( pathsBean );

        Paths paths = ( Paths ) beanManager.getReference( pathsBean,
                                                          Paths.class,
                                                          cc );

        tempFiles = new TempFiles();
        final File tempFile = tempFiles.createTempFile( "white-list" );

        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( tempFile.toURI() );
        pathToWhiteList = paths.convert( nioPackagePath );

        loader = new PackageNameWhiteListLoader( packageNameSearchProvider,
                                                 ioService );

    }

    @After
    public void cleanUp() {
        if (weld != null) {
            weld.shutdown();
        }
    }

    @Test
    public void testNoFile() throws Exception {
        tempFiles.deleteFiles();
        assertTrue( loader.load( pathToWhiteList ).isEmpty() );
    }
}