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
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.mockito.Mock;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
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
    public ProjectDiagram mockDiagram() {
        return mock(ProjectDiagram.class);
    }

    public ProjectMetadata mockMetadata() {
        return mock(ProjectMetadata.class);
    }

    @Test
    @Override
    @SuppressWarnings("unchecked")
    public void testGetDiagramByPath() throws IOException {
        Path path = mock(Path.class);
        when(path.toURI()).thenReturn(FILE_URI);
        String fileName = FILE_NAME + "." + RESOURCE_TYPE_SUFFIX;
        when(path.getFileName()).thenReturn(fileName);
        when(resourceType.accept(path)).thenReturn(true);
        final org.uberfire.java.nio.file.Path expectedNioPath = Paths.convert(path);

        byte[] content = DIAGRAM_MARSHALLED.getBytes();
        when(ioService.readAllBytes(expectedNioPath)).thenReturn(content);

        Graph<DefinitionSet, ?> graph = mock(Graph.class);
        DefinitionSet graphContent = mock(DefinitionSet.class);
        when(graph.getContent()).thenReturn(graphContent);
        when(graphContent.getDefinition()).thenReturn("DefinitionSet");

        when(diagramMarshaller.unmarshall(anyObject(),
                                          anyObject())).thenReturn(graph);

        DiagramFactory diagramFactory = mock(DiagramFactory.class);
        when(factoryRegistry.getDiagramFactory("DefinitionSet",
                                               getMetadataType())).thenReturn(diagramFactory);

        when(diagramFactory.build(eq(FILE_NAME),
                                  any(Metadata.class),
                                  eq(graph)))
                .thenAnswer(i -> {
                    final ProjectMetadata metadata = (ProjectMetadata) i.getArguments()[1];
                    when(diagram.getMetadata()).thenReturn(metadata);
                    return diagram;
                });

        WorkspaceProject project = mock(WorkspaceProject.class);
        when(projectService.resolveProject(any(Path.class))).thenReturn(project);
        when(project.getName()).thenReturn(PROJECT_NAME);

        Diagram result = diagramService.getDiagramByPath(path);
        assertEquals(diagram,
                     result);

        verify(metadataService).getMetadata(eq(path));
        verify(projectService).resolveProject(eq(rootModulePath));

        assertTrue(result.getMetadata() instanceof ProjectMetadata);
        ProjectMetadata metadata = (ProjectMetadata) result.getMetadata();
        assertNotNull(metadata.getOverview());
        assertEquals(PROJECT_NAME,
                     metadata.getOverview().getProjectName());
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
}
