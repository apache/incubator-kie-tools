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
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Import;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectImportsServiceImplTest extends WeldModuleTestBase {

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    private ProjectImportsServiceImpl projectImportsService;

    @Mock
    private RenameService renameService;

    @Mock
    private SaveAndRenameServiceImpl<ProjectImports, Metadata> saveAndRenameService;

    @Mock
    private IOService ioService;

    @Captor
    private ArgumentCaptor<String> importsArgumentCaptor;

    private Path pathToImports;

    @Before
    public void setUp() throws Exception {
        super.startWeld();

        //Instantiate Paths used in tests for Path conversion
        final Bean pathsBean = beanManager.getBeans(Paths.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(pathsBean);

        Paths paths = (Paths) beanManager.getReference(pathsBean,
                                                       Paths.class,
                                                       cc);

        final URL packageUrl = this.getClass().getResource("/ModuleBackendTestModuleStructureValid/package-names-white-list");
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath(packageUrl.toURI());
        final ProjectConfigurationContentHandler contentHandler = new ProjectConfigurationContentHandler();

        pathToImports = paths.convert(nioPackagePath);

        projectImportsService = new ProjectImportsServiceImpl(ioService,
                                                              contentHandler,
                                                              renameService,
                                                              saveAndRenameService);
    }

    @After
    public void cleanUp() {
        super.stopWeld();
    }

    @Test
    public void testPackageNameWhiteList() throws URISyntaxException {

        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(false);

        projectImportsService.saveProjectImports(pathToImports);

        verify(ioService).write(any(org.uberfire.java.nio.file.Path.class),
                                importsArgumentCaptor.capture());

        assertExternalDataObjects(importsArgumentCaptor.getValue());
    }

    private void assertExternalDataObjects(final String xml) {
        assertNotNull(xml);
        assertTrue(xml.contains(java.lang.Number.class.getName()));
        assertTrue(xml.contains(java.lang.Boolean.class.getName()));
        assertTrue(xml.contains(java.lang.String.class.getName()));
        assertTrue(xml.contains(java.lang.Integer.class.getName()));
        assertTrue(xml.contains(java.lang.Double.class.getName()));
        assertTrue(xml.contains(java.util.List.class.getName()));
        assertTrue(xml.contains(java.util.Collection.class.getName()));
        assertTrue(xml.contains(java.util.ArrayList.class.getName()));
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void testPackageNameWhiteListFileExists() throws URISyntaxException {

        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(true);

        projectImportsService.saveProjectImports(pathToImports);
    }

    @Test
    public void testProjectImportsLoad_Exists() throws URISyntaxException {

        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(true);

        final ProjectImports projectImports = projectImportsService.load(pathToImports);

        verify(ioService,
               never()).write(any(org.uberfire.java.nio.file.Path.class),
                              any(String.class));
        verify(ioService,
               times(1)).readAllString(any(org.uberfire.java.nio.file.Path.class));

        // projects imports need always to contain java.lang.Number
        // because of the guided rule editor
        assertTrue(projectImports.getImports().contains(new Import(Number.class)));
    }

    @Test
    public void testProjectImportsLoad_NotExists() throws URISyntaxException {

        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(false);

        final ProjectImports projectImports = projectImportsService.load(pathToImports);

        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               importsArgumentCaptor.capture());

        assertExternalDataObjects(importsArgumentCaptor.getValue());

        // projects imports need always to contain java.lang.Number
        // because of the guided rule editor
        assertTrue(projectImports.getImports().contains(new Import(Number.class)));
    }

    @Test
    public void testInit() {
        projectImportsService.init();

        verify(saveAndRenameService).init(projectImportsService);
    }

    @Test
    public void testRename() {

        final Path path = mock(Path.class);
        final String newName = "newName";
        final String comment = "comment";

        projectImportsService.rename(path, newName, comment);

        verify(renameService).rename(path, newName, comment);
    }

    @Test
    public void testSaveAndRename() {

        final Path path = mock(Path.class);
        final String newName = "newName";
        final Metadata metadata = mock(Metadata.class);
        final ProjectImports content = mock(ProjectImports.class);
        final String comment = "comment";

        projectImportsService.saveAndRename(path, newName, metadata, content, comment);

        verify(saveAndRenameService).saveAndRename(path, newName, metadata, content, comment);
    }
}
