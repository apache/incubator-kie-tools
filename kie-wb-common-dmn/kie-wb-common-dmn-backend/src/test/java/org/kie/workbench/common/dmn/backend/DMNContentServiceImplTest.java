/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.backend;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.DMNContentResource;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.marshalling.DMNPathsHelper;
import org.kie.workbench.common.dmn.backend.common.DMNIOHelper;
import org.kie.workbench.common.dmn.backend.editors.common.PMMLIncludedDocumentFactory;
import org.kie.workbench.common.services.backend.service.KieServiceOverviewLoader;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectMetadataImpl;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNContentServiceImplTest {

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    @Mock
    private DMNIOHelper dmnIOHelper;

    @Mock
    private DMNPathsHelper pathsHelper;

    @Mock
    private PMMLIncludedDocumentFactory pmmlIncludedDocumentFactory;

    @Mock
    private Path path;

    @Mock
    private org.uberfire.java.nio.file.Path convertedPath;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private KieServiceOverviewLoader overviewLoader;

    @Mock
    private IOService ioService;

    @Mock
    private MetadataServerSideService metadataService;

    @Mock
    private WorkspaceProject workspaceProject;

    private String fileName = "fileName.dmn";

    private DMNContentServiceImpl service;

    @Before
    public void setup() {

        service = spy(new DMNContentServiceImpl(commentedOptionFactory, dmnIOHelper, pathsHelper, pmmlIncludedDocumentFactory) {{
            this.moduleService = DMNContentServiceImplTest.this.moduleService;
            this.overviewLoader = DMNContentServiceImplTest.this.overviewLoader;
            this.ioService = DMNContentServiceImplTest.this.ioService;
            this.metadataService = DMNContentServiceImplTest.this.metadataService;
        }});

        when(path.getFileName()).thenReturn(fileName);
        doReturn(convertedPath).when(service).convertPath(path);
    }

    @Test
    public void testGetContent() {

        final String actual = "<xml/>";
        doReturn(actual).when(service).getSource(path);

        final String expected = service.getContent(path);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetProjectContent() {

        final String defSetId = "defSetId";
        final String expectedContent = "<xml/>";
        final String moduleName = "moduleName";
        final Package aPackage = mock(Package.class);
        final KieModule kieModule = mock(KieModule.class);
        final Overview overview = mock(Overview.class);

        doReturn(expectedContent).when(service).getSource(path);
        when(moduleService.resolvePackage(path)).thenReturn(aPackage);
        when(moduleService.resolveModule(path)).thenReturn(kieModule);
        when(overviewLoader.loadOverview(path)).thenReturn(overview);
        when(kieModule.getModuleName()).thenReturn(moduleName);

        final DMNContentResource contentResource = service.getProjectContent(path, defSetId);
        final String actualContent = contentResource.getContent();
        final ProjectMetadataImpl metadata = (ProjectMetadataImpl) contentResource.getMetadata();

        assertEquals(expectedContent, actualContent);
        assertEquals(defSetId, metadata.getDefinitionSetId());
        assertEquals(moduleName, metadata.getModuleName());
        assertEquals(aPackage, metadata.getProjectPackage());
        assertEquals(overview, metadata.getOverview());
        assertEquals(fileName, metadata.getTitle());
        assertEquals(path, metadata.getPath());
    }

    @Test
    public void saveContent() {

        final String content = "<xml/>";
        final Metadata metadata = mock(Metadata.class);
        final String comment = "Commit message.";
        final Map<String, Object> attributes = new HashMap<>();
        final CommentedOption commentedOption = mock(CommentedOption.class);

        when(metadataService.setUpAttributes(path, metadata)).thenReturn(attributes);
        when(commentedOptionFactory.makeCommentedOption(comment)).thenReturn(commentedOption);

        service.saveContent(path, content, metadata, comment);

        verify(ioService).write(convertedPath, content, attributes, commentedOption);
    }

    @Test
    public void testGetModelsPaths() {

        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);
        final List<Path> expectedPaths = asList(path1, path2);

        when(pathsHelper.getModelsPaths(workspaceProject)).thenReturn(expectedPaths);

        final List<Path> actualPaths = service.getModelsPaths(workspaceProject);

        assertEquals(expectedPaths, actualPaths);
    }

    @Test
    public void testGetDMNModelsPaths() {

        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);
        final List<Path> expectedPaths = asList(path1, path2);

        when(pathsHelper.getDMNModelsPaths(workspaceProject)).thenReturn(expectedPaths);

        final List<Path> actualPaths = service.getDMNModelsPaths(workspaceProject);

        assertEquals(expectedPaths, actualPaths);
    }

    @Test
    public void testGetPMMLModelsPaths() {

        final Path path1 = mock(Path.class);
        final Path path2 = mock(Path.class);
        final List<Path> expectedPaths = asList(path1, path2);

        when(pathsHelper.getPMMLModelsPaths(workspaceProject)).thenReturn(expectedPaths);

        final List<Path> actualPaths = service.getPMMLModelsPaths(workspaceProject);

        assertEquals(expectedPaths, actualPaths);
    }

    @Test
    public void constructContent() {
        service.constructContent(path, null);

        final String actual = "<xml/>";
        doReturn(actual).when(service).getSource(path);

        final String expected = service.constructContent(path, null);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetSource() {

        final InputStream inputStream = mock(InputStream.class);
        final String expectedSource = "<xml/>";

        when(ioService.newInputStream(convertedPath)).thenReturn(inputStream);
        when(dmnIOHelper.isAsString(inputStream)).thenReturn(expectedSource);

        final String actualSource = service.getSource(path);

        assertEquals(expectedSource, actualSource);
    }

    @Test
    public void testLoadPMMLDocumentMetadata() {

        final PMMLDocumentMetadata expected = mock(PMMLDocumentMetadata.class);

        when(pmmlIncludedDocumentFactory.getDocumentByPath(path)).thenReturn(expected);

        final PMMLDocumentMetadata actual = service.loadPMMLDocumentMetadata(path);

        assertEquals(expected, actual);
    }
}
