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

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Named;

import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.service.DefinitionSetService;
import org.kie.workbench.common.stunner.core.registry.BackendRegistryFactory;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectDiagramServiceImplTest {

    private static final String NAME = "NAME";

    private static final String PROJECT = "PROJECT";

    private static final String DEF_SET_ID = "DEF_SET_ID";

    private static final Package PACKAGE = new Package();

    private static final String RAW_CONTENT = "RAW_CONTENT";

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private Instance<DefinitionSetService> definitionSetServiceInstances;

    @Mock
    private BackendRegistryFactory registryFactory;

    @Mock
    private IOService ioService;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private MetadataServerSideService metadataService;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private EventSourceMock<ResourceOpenedEvent> resourceOpenedEvent;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    @Mock
    private ProjectDiagramServiceController diagramServiceController;

    @Mock
    private Path path;

    @Mock
    private Path resultPath;

    @Mock
    private ProjectDiagram diagram;

    private ProjectDiagramServiceImpl diagramService;

    private ArgumentCaptor<ResourceOpenedEvent> eventArgumentCaptor;

    @Before
    public void setUp() {
        eventArgumentCaptor = ArgumentCaptor.forClass(ResourceOpenedEvent.class);
        diagramService = new ProjectDiagramServiceImpl(definitionManager,
                                                       factoryManager,
                                                       definitionSetServiceInstances,
                                                       registryFactory,
                                                       ioService,
                                                       sessionInfo,
                                                       resourceOpenedEvent,
                                                       commentedOptionFactory,
                                                       moduleService) {

            {
                metadataService = ProjectDiagramServiceImplTest.this.metadataService;
            }

            @Override
            protected ProjectDiagramServiceController buildController(DefinitionManager definitionManager,
                                                                      FactoryManager factoryManager,
                                                                      Instance<DefinitionSetService> definitionSetServiceInstances,
                                                                      BackendRegistryFactory registryFactory,
                                                                      @Named("ioStrategy") IOService ioService,
                                                                      KieModuleService moduleService) {
                return diagramServiceController;
            }
        };
        diagramService.init();
        verify(diagramServiceController,
               times(1)).initialize();
    }

    @Test
    public void testGetDiagramByPath() {
        when(diagramServiceController.getDiagramByPath(path)).thenReturn(diagram);
        ProjectDiagram result = diagramService.getDiagramByPath(path);
        verify(diagramServiceController,
               times(1)).getDiagramByPath(path);
        assertEquals(result,
                     diagram);
    }

    @Test
    public void testAccepts() {
        when(diagramServiceController.accepts(path)).thenReturn(true);
        boolean result = diagramService.accepts(path);
        verify(diagramServiceController,
               times(1)).accepts(path);
        assertEquals(true,
                     result);
    }

    @Test
    public void testCreate() {
        when(diagramServiceController.create(path,
                                             NAME,
                                             DEF_SET_ID,
                                             PROJECT,
                                             PACKAGE)).thenReturn(resultPath);
        Path result = diagramService.create(path,
                                            NAME,
                                            DEF_SET_ID,
                                            PROJECT,
                                            PACKAGE);
        verify(diagramServiceController,
               times(1)).create(path,
                                NAME,
                                DEF_SET_ID,
                                PROJECT,
                                PACKAGE);
        assertEquals(resultPath,
                     result);
    }

    @Test
    public void testConstructContent() {
        Overview overview = mock(Overview.class);
        when(diagramServiceController.getDiagramByPath(path)).thenReturn(diagram);
        ProjectDiagram result = diagramService.constructContent(path,
                                                                overview);
        verify(diagramServiceController,
               times(1)).getDiagramByPath(path);
        verify(resourceOpenedEvent,
               times(1)).fire(eventArgumentCaptor.capture());
        assertEquals(sessionInfo,
                     eventArgumentCaptor.getValue().getSessionInfo());
        assertEquals(path,
                     eventArgumentCaptor.getValue().getPath());
        assertEquals(result,
                     diagram);
    }

    @Test
    public void testSave() {
        Metadata metadata = mock(Metadata.class);
        String comment = "COMMENT";
        Map<String, Object> attributes = new HashMap<>();
        CommentedOption options = mock(CommentedOption.class);
        when(metadataService.setUpAttributes(path,
                                             metadata)).thenReturn(attributes);
        when(commentedOptionFactory.makeCommentedOption(comment)).thenReturn(options);
        when(diagramServiceController.save(path,
                                           diagram,
                                           attributes,
                                           options)).thenReturn(resultPath);
        Path result = diagramService.save(path,
                                          diagram,
                                          metadata,
                                          comment);
        verify(diagramServiceController,
               times(1)).save(path,
                              diagram,
                              attributes,
                              options);
        assertEquals(resultPath,
                     result);
    }

    @Test
    public void testSaveOrUpdate() {
        ProjectMetadata projectMetadata = mock(ProjectMetadata.class);
        when(diagramServiceController.saveOrUpdate(diagram)).thenReturn(projectMetadata);
        ProjectMetadata result = diagramService.saveOrUpdate(diagram);
        verify(diagramServiceController,
               times(1)).saveOrUpdate(diagram);
        assertEquals(projectMetadata,
                     result);
    }

    @Test
    public void testDelete() {
        when(diagramServiceController.delete(diagram)).thenReturn(true);
        boolean result = diagramService.delete(diagram);
        verify(diagramServiceController,
               times(1)).delete(diagram);
        assertTrue(result);
    }

    @Test
    public void testDeleteByPath() {
        String comment = "COMMENT";
        diagramService.delete(path,
                              comment);
        verify(diagramServiceController,
               times(1)).delete(path,
                                comment);
    }

    @Test
    public void testGetRawContent() {
        when(diagramServiceController.getRawContent(diagram)).thenReturn(RAW_CONTENT);
        String result = diagramService.getRawContent(diagram);
        assertEquals(RAW_CONTENT,
                     result);
    }
}
