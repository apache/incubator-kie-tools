/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.refactoring.backend.server.indexing;

import java.net.URI;
import java.util.Set;

import org.guvnor.common.services.project.model.Package;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.KPropertyImpl;
import org.kie.workbench.common.services.refactoring.model.index.terms.ModuleNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.ModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.PackageNameIndexTerm;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.Silent.class)
public class GitKeepFileIndexerTest {

    private SimpleFileSystemProvider fileSystemProvider;
    private org.uberfire.java.nio.file.Path mainPath;

    @Mock
    private IOService ioService;

    @Mock
    private KieModuleService moduleService;

    @InjectMocks
    private GitKeepFileIndexer indexer;

    @Before
    public void setUp() throws Exception {
        fileSystemProvider = new SimpleFileSystemProvider();

        mainPath = fileSystemProvider.getPath(URI.create("default://master@myRepository/Test"));
    }

    @Test
    public void testSupportsPathFail() {
        final org.uberfire.java.nio.file.Path path = mock(org.uberfire.java.nio.file.Path.class);
        final org.uberfire.java.nio.file.Path fileName = mock(org.uberfire.java.nio.file.Path.class);
        doReturn(fileName).when(path).getFileName();
        doReturn("fail.txt").when(fileName).toString();

        assertFalse(indexer.supportsPath(path));
    }

    @Test
    public void testSupportsPathSuccess() {
        final org.uberfire.java.nio.file.Path path = mock(org.uberfire.java.nio.file.Path.class);
        final org.uberfire.java.nio.file.Path fileName = mock(org.uberfire.java.nio.file.Path.class);
        doReturn(fileName).when(path).getFileName();
        doReturn(".gitkeep").when(fileName).toString();

        assertTrue(indexer.supportsPath(path));
    }

    @Test
    public void testTheUsualBuild() throws Exception {
        final KieModule kieModule = mock(KieModule.class);
        doReturn(kieModule).when(moduleService).resolveModule(any(Path.class));
        doReturn("Module Name").when(kieModule).getModuleName();
        doReturn(getPathMock("default://master@myRepository/Test")).when(kieModule).getRootPath();
        final Package aPackage = mock(Package.class);
        doReturn("pkgName").when(aPackage).getPackageName();
        doReturn(aPackage).when(moduleService).resolvePackage(any(Path.class));

        final IndexBuilder indexBuilder = indexer.fillIndexBuilder(mainPath);
        final Set<KProperty<?>> properties = indexBuilder.build();

        assertEquals(3, properties.size());
        assertTrue(properties.contains(new KPropertyImpl<>(ModuleRootPathIndexTerm.TERM,
                                                           "default://master@myRepository/Test")));
        assertTrue(properties.contains(new KPropertyImpl<>(ModuleNameIndexTerm.TERM,
                                                           "Module Name")));
        assertTrue(properties.contains(new KPropertyImpl<>(PackageNameIndexTerm.TERM,
                                                           "pkgName")));
    }

    @Test
    public void noModuleReturnsNull() throws Exception {
        doReturn(null).when(moduleService).resolvePackage(any());

        assertNull(indexer.fillIndexBuilder(mainPath));
    }

    @Test
    public void noPackageReturnsNull() throws Exception {
        final KieModule kieModule = mock(KieModule.class);
        doReturn(kieModule).when(moduleService).resolveModule(any(Path.class));
        doReturn(null).when(moduleService).resolvePackage(any(Path.class));

        assertNull(indexer.fillIndexBuilder(mainPath));
    }

    private Path getPathMock(final String uri) {
        Path path = mock(Path.class);
        doReturn(uri).when(path).toURI();
        return path;
    }
}