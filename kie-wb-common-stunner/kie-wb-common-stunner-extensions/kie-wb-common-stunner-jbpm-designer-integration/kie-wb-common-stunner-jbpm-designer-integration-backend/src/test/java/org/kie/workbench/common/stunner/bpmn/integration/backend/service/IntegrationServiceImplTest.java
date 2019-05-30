/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.integration.backend.service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.backend.service.KieServiceOverviewLoader;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNBackendService;
import org.kie.workbench.common.stunner.bpmn.integration.service.IntegrationService;
import org.kie.workbench.common.stunner.bpmn.integration.service.MigrateRequest;
import org.kie.workbench.common.stunner.bpmn.integration.service.MigrateResult;
import org.kie.workbench.common.stunner.bpmn.resource.BPMNDefinitionSetResourceType;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingMessage;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingRequest;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingResponse;
import org.kie.workbench.common.stunner.core.registry.factory.FactoryRegistry;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectMetadataImpl;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileSystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IntegrationServiceImplTest {

    private static final String NAME = "TestProcess";
    private static final String FILE_NAME = NAME + ".bpmn";
    private static final String PATH_URI = "default:///test/src/main/resources/com/myspace/test/" + FILE_NAME;
    private static final String COMMIT_MESSAGE = "COMMIT_MESSAGE";
    private static final String NEW_FILE_NAME = "NewFileName";
    private static final String NEW_EXTENSION = ".NewExtension";
    private static final String EXPECTED_FILE_URI = "default:///test/src/main/resources/com/myspace/test/" + NEW_FILE_NAME + NEW_EXTENSION;

    @Mock
    private ProjectDiagramService diagramService;

    @Mock
    private BPMNBackendService definitionService;

    @Mock
    private DiagramMarshaller diagramMarshaller;

    @Mock
    private BPMNDefinitionSetResourceType resourceType;

    private String defSetId;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private FactoryRegistry factoryRegistry;

    @Mock
    private DiagramFactory diagramFactory;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private KieServiceOverviewLoader overviewLoader;

    @Mock
    private IOService ioService;

    @Mock
    private CommentedOptionFactory optionFactory;

    @Mock
    private CommentedOption commentedOption;

    private IntegrationService service;

    @Mock
    private Path path;

    @Mock
    private Package modulePackage;

    @Mock
    private KieModule kieModule;

    @Mock
    private Overview overview;

    private byte[] bytes = "emulate the file content".getBytes();

    @Captor
    private ArgumentCaptor<MarshallingRequest> marshallingRequestCaptor;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Captor
    private ArgumentCaptor<org.uberfire.java.nio.file.Path> sourcePathCaptor;

    @Captor
    private ArgumentCaptor<org.uberfire.java.nio.file.Path> targetPathCaptor;

    @Captor
    private ArgumentCaptor<Path> vfsTargetPathCaptor;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(path.getFileName()).thenReturn(FILE_NAME);
        when(path.toURI()).thenReturn(PATH_URI);
        when(path.toString()).thenReturn(PATH_URI);
        when(optionFactory.makeCommentedOption(COMMIT_MESSAGE)).thenReturn(commentedOption);

        when(factoryManager.registry()).thenReturn(factoryRegistry);
        resourceType = new BPMNDefinitionSetResourceType(mock(org.guvnor.common.services.project.categories.Process.class));
        defSetId = resourceType.getDefinitionSetType().getName();
        when(definitionService.getResourceType()).thenReturn(resourceType);
        when(definitionService.getDiagramMarshaller()).thenReturn(diagramMarshaller);
        service = new IntegrationServiceImpl(diagramService,
                                             definitionService,
                                             factoryManager,
                                             moduleService,
                                             overviewLoader,
                                             ioService,
                                             optionFactory);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetDiagramByPathSuccessful() {
        Graph<DefinitionSet, ?> graph = mock(Graph.class);
        DefinitionSet definitionSet = mock(DefinitionSet.class);
        String definitionValue = "theDefinitionValue";
        when(graph.getContent()).thenReturn(definitionSet);
        when(definitionSet.getDefinition()).thenReturn(definitionValue);
        when(factoryRegistry.getDiagramFactory(definitionValue, ProjectMetadata.class)).thenReturn(diagramFactory);
        ProjectDiagram diagram = mock(ProjectDiagram.class);
        when(diagramFactory.build(eq(NAME), any(ProjectMetadata.class), eq(graph))).thenReturn(diagram);

        List<MarshallingMessage> messages = new ArrayList<>();
        MarshallingResponse response = mockResponse(MarshallingResponse.State.SUCCESS, messages, Optional.of(graph));
        MarshallingResponse result = prepareTestGetDiagramByPath(response, null);

        assertEquals(MarshallingResponse.State.SUCCESS, result.getState());
        assertEquals(messages, result.getMessages());
        assertTrue(result.getResult().isPresent());
        assertEquals(diagram, result.getResult().get());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetDiagramByPathWithError() {
        List<MarshallingMessage> messages = new ArrayList<>();
        MarshallingResponse response = mockResponse(MarshallingResponse.State.ERROR, messages, Optional.empty());
        MarshallingResponse result = prepareTestGetDiagramByPath(response, null);

        assertEquals(MarshallingResponse.State.ERROR, result.getState());
        assertEquals(messages, result.getMessages());
        assertFalse(result.getResult().isPresent());
    }

    @Test
    public void testGetDiagramByPathWithUnexpectedError() {
        List<MarshallingMessage> messages = new ArrayList<>();
        MarshallingResponse response = mockResponse(MarshallingResponse.State.ERROR, messages, Optional.empty());
        RuntimeException unexpectedError = new RuntimeException("Unexpected error");
        expectedException.expectMessage("An error was produced while diagram loading from file " + PATH_URI);
        prepareTestGetDiagramByPath(response, unexpectedError);
    }

    @Test
    public void testMigrateFromStunnerToJBPMDesignerSuccessful() {
        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(false);
        MigrateRequest request = MigrateRequest.newFromStunnerToJBPMDesigner(path, NEW_FILE_NAME, NEW_EXTENSION, COMMIT_MESSAGE);
        MigrateResult result = service.migrateDiagram(request);

        verify(ioService).move(sourcePathCaptor.capture(), targetPathCaptor.capture(), eq(commentedOption));
        assertEquals(PATH_URI, sourcePathCaptor.getValue().toUri().toString());
        assertEquals(EXPECTED_FILE_URI, targetPathCaptor.getValue().toUri().toString());
        verify(ioService).startBatch(any(FileSystem.class));
        verify(ioService).endBatch();

        assertFalse(result.hasError());
        assertEquals(EXPECTED_FILE_URI, result.getPath().toURI());
    }

    @Test
    public void testMigrateFromStunnerToJBPMDesignerFileAlreadyExists() {
        MigrateRequest request = MigrateRequest.newFromStunnerToJBPMDesigner(path, NEW_FILE_NAME, NEW_EXTENSION, COMMIT_MESSAGE);
        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(true);
        MigrateResult result = service.migrateDiagram(request);

        verify(ioService, never()).move(any(org.uberfire.java.nio.file.Path.class),
                                        any(org.uberfire.java.nio.file.Path.class),
                                        any(CommentedOption.class));
        verify(ioService, never()).startBatch(any(FileSystem.class));
        verify(ioService, never()).endBatch();

        assertTrue(result.hasError());
        assertEquals(IntegrationService.ServiceError.JBPM_DESIGNER_PROCESS_ALREADY_EXIST, result.getError());
        assertEquals(IntegrationService.ServiceError.JBPM_DESIGNER_PROCESS_ALREADY_EXIST.i18nKey(), result.getMessageKey());
        assertEquals(1, result.getMessageArguments().size());
        assertEquals(path.toString(), result.getMessageArguments().get(0).toString());
    }

    @Test
    public void testMigrateFromStunnerToJBPMDesignerUnexpectedError() {
        MigrateRequest request = MigrateRequest.newFromStunnerToJBPMDesigner(path, NEW_FILE_NAME, NEW_EXTENSION, COMMIT_MESSAGE);
        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(false);
        when(ioService.move(any(org.uberfire.java.nio.file.Path.class),
                            any(org.uberfire.java.nio.file.Path.class),
                            any(CommentedOption.class))).thenThrow(new IOException("Unexpected error"));

        expectedException.expectMessage("An error was produced during diagram migration from Stunner to jBPMDesigner for diagram: " + PATH_URI);
        service.migrateDiagram(request);

        verify(ioService).move(any(org.uberfire.java.nio.file.Path.class),
                               any(org.uberfire.java.nio.file.Path.class),
                               any(CommentedOption.class));
        verify(ioService).startBatch(any(FileSystem.class));
        verify(ioService).endBatch();
    }

    @Test
    public void testMigrateFromJBPMDesignerToStunnerSuccessful() {
        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(false);
        ProjectDiagram projectDiagram = mock(ProjectDiagram.class);
        ProjectMetadata metadata = mock(ProjectMetadata.class);
        when(projectDiagram.getMetadata()).thenReturn(metadata);
        MigrateRequest request = MigrateRequest.newFromJBPMDesignerToStunner(path, NEW_FILE_NAME, NEW_EXTENSION, COMMIT_MESSAGE, projectDiagram);
        MigrateResult result = service.migrateDiagram(request);

        verify(diagramService).saveOrUpdate(projectDiagram);
        verify(metadata).setTitle(NEW_FILE_NAME);
        verify(metadata).setPath(vfsTargetPathCaptor.capture());
        assertEquals(EXPECTED_FILE_URI, vfsTargetPathCaptor.getValue().toURI());
        assertFalse(result.hasError());
        assertEquals(EXPECTED_FILE_URI, result.getPath().toURI());
    }

    @Test
    public void testMigrateFromJBPMDesignerToStunnerFileAlreadyExists() {
        ProjectDiagram projectDiagram = mock(ProjectDiagram.class);
        ProjectMetadata metadata = mock(ProjectMetadata.class);
        when(projectDiagram.getMetadata()).thenReturn(metadata);
        MigrateRequest request = MigrateRequest.newFromJBPMDesignerToStunner(path, NEW_FILE_NAME, NEW_EXTENSION, COMMIT_MESSAGE, projectDiagram);
        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(true);
        MigrateResult result = service.migrateDiagram(request);

        verify(diagramService, never()).saveOrUpdate(projectDiagram);
        verify(metadata, never()).setTitle(anyString());
        verify(metadata, never()).setPath(anyObject());

        assertTrue(result.hasError());
        assertEquals(IntegrationService.ServiceError.STUNNER_PROCESS_ALREADY_EXIST, result.getError());
        assertEquals(IntegrationService.ServiceError.STUNNER_PROCESS_ALREADY_EXIST.i18nKey(), result.getMessageKey());
        assertEquals(1, result.getMessageArguments().size());
        assertEquals(path.toString(), result.getMessageArguments().get(0).toString());
    }

    @SuppressWarnings("unchecked")
    private MarshallingResponse prepareTestGetDiagramByPath(MarshallingResponse response, RuntimeException unexpectedError) {
        MarshallingRequest.Mode mode = MarshallingRequest.Mode.AUTO;

        when(moduleService.resolvePackage(path)).thenReturn(modulePackage);
        when(moduleService.resolveModule(path)).thenReturn(kieModule);
        when(overviewLoader.loadOverview(path)).thenReturn(overview);
        when(ioService.readAllBytes(Paths.convert(path))).thenReturn(bytes);

        ProjectMetadata metadata = new ProjectMetadataImpl.ProjectMetadataBuilder()
                .forDefinitionSetId(defSetId)
                .forModuleName(kieModule.getModuleName())
                .forProjectPackage(modulePackage)
                .forOverview(overviewLoader.loadOverview(path))
                .forTitle(NAME)
                .forPath(path)
                .build();

        MarshallingRequest expectedRequest = MarshallingRequest.builder()
                .metadata(metadata)
                .input(new ByteArrayInputStream(bytes))
                .mode(mode)
                .build();

        if (unexpectedError == null) {
            when(diagramMarshaller.unmarshallWithValidation(any(MarshallingRequest.class))).thenReturn(response);
        } else {
            when(diagramMarshaller.unmarshallWithValidation(any(MarshallingRequest.class))).thenThrow(unexpectedError);
        }

        MarshallingResponse result = service.getDiagramByPath(path, MarshallingRequest.Mode.AUTO);

        verify(diagramMarshaller).unmarshallWithValidation(marshallingRequestCaptor.capture());
        assertEquals(expectedRequest.getMetadata(), metadata);
        assertEquals(expectedRequest.getMode(), mode);
        return result;
    }

    private static MarshallingResponse mockResponse(MarshallingResponse.State state, List<MarshallingMessage> messages, Optional result) {
        MarshallingResponse response = mock(MarshallingResponse.class);
        when(response.getState()).thenReturn(state);
        when(response.getMessages()).thenReturn(messages);
        when(response.getResult()).thenReturn(result);
        return response;
    }
}
