/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.project.backend.service;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.service.KieServiceOverviewLoader;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.core.backend.service.AbstractVFSDiagramService;
import org.kie.workbench.common.stunner.core.backend.service.AbstractVFSDiagramServiceTest;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectDiagramImpl;
import org.mockito.Mock;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.DeleteOption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectDiagramServiceControllerTest
        extends AbstractVFSDiagramServiceTest<ProjectMetadata, ProjectDiagram> {

    private static final String JBPM_DIAGRAM_SVG = "arbitrary content " + "xmlns:oryx=\"http://oryx-editor.org\"" + "etc...";

    private final static String PROJECT_NAME = "project";

    @Mock
    private KieModuleService moduleService;

    @Mock
    private MetadataServerSideService metadataService;

    @Mock
    private WorkspaceProjectService projectService;

    @Mock
    private KieModule kieModule;

    @Mock
    private Package kiePackage;

    @Mock
    private Path rootModulePath;

    @Before
    public void setUp() throws IOException {
        super.setUp();
        when(moduleService.resolveModule(any(Path.class))).thenReturn(kieModule);
        when(moduleService.resolvePackage(any(Path.class))).thenReturn(kiePackage);
        when(kieModule.getModuleName()).thenReturn("kieModule1");
        when(kieModule.getRootPath()).thenReturn(rootModulePath);
        when(kiePackage.getModuleRootPath()).thenReturn(rootModulePath);
    }

    @Override
    public AbstractVFSDiagramService<ProjectMetadata, ProjectDiagram> createVFSDiagramService() {
        ProjectDiagramServiceController service = new ProjectDiagramServiceController(definitionManager,
                                                                                      factoryManager,
                                                                                      definitionSetServiceInstances,
                                                                                      ioService,
                                                                                      registryFactory,
                                                                                      moduleService,
                                                                                      new KieServiceOverviewLoader(metadataService,
                                                                                                                   moduleService,
                                                                                                                   projectService));
        service.initialize();
        return service;
    }

    @Override
    public Class<? extends Metadata> getMetadataType() {
        return ((ProjectDiagramServiceController) diagramService).getMetadataType();
    }

    @Override
    public ProjectDiagramImpl mockDiagram() {
        return mock(ProjectDiagramImpl.class);
    }

    public ProjectMetadata mockMetadata() {
        return mock(ProjectMetadata.class);
    }

    @Test
    @Override
    public void testGetDiagramByPath() throws IOException {
        final Path path = mockGetDiagramByPathObjects();
        prepareLoadDiagramByPath(path);

        final Diagram result = diagramService.getDiagramByPath(path);
        assertEquals(diagram, result);
        verifyExpectedResult(path, result, null, null);
    }

    @Test
    public void testGetDiagramByPathWhenStunnerSVGFileExists() throws IOException {
        final Path path = mockGetDiagramByPathObjects();
        final Path expectedSVGPath = prepareSVGFile(path, DIAGRAM_SVG);
        prepareLoadDiagramByPath(path);

        final Diagram result = diagramService.getDiagramByPath(path);
        assertEquals(diagram, result);
        verifyExpectedResult(path, result, expectedSVGPath, ProjectMetadata.SVGGenerator.STUNNER);
    }

    @Test
    public void testGetDiagramByPathWhenStunnerJBPMFileExists() throws IOException {
        final Path path = mockGetDiagramByPathObjects();
        final Path expectedSVGPath = prepareSVGFile(path, JBPM_DIAGRAM_SVG);
        prepareLoadDiagramByPath(path);

        final Diagram result = diagramService.getDiagramByPath(path);
        assertEquals(diagram, result);
        verifyExpectedResult(path, result, expectedSVGPath, ProjectMetadata.SVGGenerator.JBPM_DESIGNER);
    }

    private Path prepareSVGFile(Path diagramPath, String content) {
        final org.uberfire.java.nio.file.Path expectedSVGPath = Paths.convert(diagramPath).getParent().resolve(DIAGRAM_FILE_ID + AbstractVFSDiagramService.SVG_SUFFIX);
        when(ioService.exists(expectedSVGPath)).thenReturn(true);
        when(ioService.readAllString(expectedSVGPath)).thenReturn(content);
        return Paths.convert(expectedSVGPath);
    }

    private void verifyExpectedResult(Path path, Diagram result, Path expectedSVGPath, ProjectMetadata.SVGGenerator generator) {
        verify(metadataService).getMetadata(eq(path));
        verify(projectService).resolveProject(eq(rootModulePath));

        assertTrue(result.getMetadata() instanceof ProjectMetadata);
        final ProjectMetadata metadata = (ProjectMetadata) result.getMetadata();
        assertNotNull(metadata.getOverview());
        assertEquals(PROJECT_NAME, metadata.getOverview().getProjectName());
        assertEquals(expectedSVGPath, metadata.getDiagramSVGPath());
        assertEquals(generator, metadata.getDiagramSVGGenerator());
    }

    @Test
    public void testSaveOrUpdate() {
        Path path = mock(Path.class);
        when(path.toURI()).thenReturn(FILE_URI);
        when(metadata.getPath()).thenReturn(path);
        org.uberfire.java.nio.file.Path expectedNioPath = Paths.convert(path);

        diagramService.saveOrUpdate(diagram);

        verify(ioService,
               times(1)).write(expectedNioPath,
                               DIAGRAM_MARSHALLED);
    }

    @Test
    public void testSaveAsXml() {
        final Path path = mock(Path.class);
        final String xml = "xml";
        final Map<String, ?> attributes = Collections.singletonMap("key", "value");
        final CommentedOption option = mock(CommentedOption.class);
        when(path.toURI()).thenReturn(FILE_URI);
        final org.uberfire.java.nio.file.Path expectedNioPath = Paths.convert(path);

        ((ProjectDiagramServiceController) diagramService).saveAsXml(path,
                                                                     xml,
                                                                     attributes,
                                                                     option);

        verify(ioService,
               times(1)).write(eq(expectedNioPath),
                               eq(xml),
                               eq(attributes),
                               eq(option));
    }

    @Test
    public void testDelete() {
        Path path = mock(Path.class);
        when(path.toURI()).thenReturn(FILE_URI);
        when(metadata.getPath()).thenReturn(path);
        org.uberfire.java.nio.file.Path expectedNioPath = Paths.convert(path);

        diagramService.delete(diagram);

        verify(ioService,
               times(1)).deleteIfExists(eq(expectedNioPath),
                                        any(DeleteOption.class));
    }

    @Test
    public void testSaveOrUpdateSvg() throws IOException {
        final Path path = mockGetDiagramByPathObjects();
        prepareLoadDiagramByPath(path);
        super.testBaseSaveOrUpdateSvg();
    }

    private void prepareLoadDiagramByPath(Path path) throws IOException {
        when(metadata.getPath()).thenReturn(path);

        when(diagramMarshaller.unmarshall(anyObject(),
                                          anyObject())).thenReturn(graph);

        when(diagramFactory.build(eq(FILE_NAME),
                                  any(Metadata.class),
                                  eq(graph)))
                .thenAnswer(i -> {
                    final ProjectMetadata metadata = (ProjectMetadata) i.getArguments()[1];
                    metadata.setCanvasRootUUID(ProjectDiagramServiceControllerTest.this.metadata.getCanvasRootUUID());
                    metadata.setPath(ProjectDiagramServiceControllerTest.this.metadata.getPath());
                    when(diagram.getMetadata()).thenReturn(metadata);
                    return diagram;
                });
        WorkspaceProject project = mock(WorkspaceProject.class);
        when(projectService.resolveProject(any(Path.class))).thenReturn(project);
        when(project.getName()).thenReturn(PROJECT_NAME);
    }
}
