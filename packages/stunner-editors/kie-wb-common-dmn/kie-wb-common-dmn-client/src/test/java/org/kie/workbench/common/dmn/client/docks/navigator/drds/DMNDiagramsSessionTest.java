/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.docks.navigator.drds;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.appformer.client.stateControl.registry.Registry;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.EventSourceMock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDiagramsSessionTest {

    @Mock
    private ManagedInstance<DMNDiagramsSessionState> dmnDiagramsSessionStates;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DMNDiagramUtils dmnDiagramUtils;

    @Mock
    private ClientSession clientSession;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private Metadata metadata;

    @Mock
    private Path path;

    @Mock
    private Diagram diagram;

    @Mock
    private EventSourceMock locker;

    @Mock
    private EventSourceMock currentRegistryChangedEvent;

    private String uri = "://cyber/v.dmn";

    private Map<String, Diagram> diagramsByDiagramElementId = new HashMap<>();

    private Map<String, DMNDiagramElement> dmnDiagramsByDiagramElementId = new HashMap<>();

    private DMNDiagramsSession dmnDiagramsSession;

    private DMNDiagramsSessionState dmnDiagramsSessionState;

    @Before
    public void setup() {

        dmnDiagramsSessionState = spy(new DMNDiagramsSessionState(dmnDiagramUtils));
        dmnDiagramsSession = spy(new DMNDiagramsSession(dmnDiagramsSessionStates, sessionManager, dmnDiagramUtils, locker, currentRegistryChangedEvent));

        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(clientSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(sessionManager.getCurrentSession()).thenReturn(clientSession);
        when(metadata.getPath()).thenReturn(path);
        when(path.toURI()).thenReturn(uri);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(dmnDiagramsSessionStates.get()).thenReturn(dmnDiagramsSessionState);

        dmnDiagramsSession.setState(metadata, diagramsByDiagramElementId, dmnDiagramsByDiagramElementId);
    }

    @Test
    public void testDestroyState() {
        assertNotNull(dmnDiagramsSession.getSessionState());
        dmnDiagramsSession.destroyState(metadata);
        assertNull(dmnDiagramsSession.getSessionState());
    }

    @Test
    public void testGetCurrentSessionKey() {
        assertEquals(uri, dmnDiagramsSession.getCurrentSessionKey());
    }

    @Test
    public void testAddAndRemove() {

        final DMNDiagramElement dmnDiagram = mock(DMNDiagramElement.class);
        final Diagram stunnerDiagram = mock(Diagram.class);
        final String diagramId = "0000";

        when(dmnDiagram.getId()).thenReturn(new Id(diagramId));

        dmnDiagramsSession.add(dmnDiagram, stunnerDiagram);

        assertEquals(dmnDiagram, dmnDiagramsSession.getDMNDiagramElement(diagramId));
        assertEquals(stunnerDiagram, dmnDiagramsSession.getDiagram(diagramId));
        assertEquals(dmnDiagram, dmnDiagramsSession.getDiagramTuple(diagramId).getDMNDiagram());
        assertEquals(stunnerDiagram, dmnDiagramsSession.getDiagramTuple(diagramId).getStunnerDiagram());

        dmnDiagramsSession.remove(dmnDiagram);

        assertNull(dmnDiagramsSession.getDMNDiagramElement(diagramId));
        assertNull(dmnDiagramsSession.getDiagram(diagramId));
        assertNull(dmnDiagramsSession.getDiagramTuple(diagramId).getDMNDiagram());
        assertNull(dmnDiagramsSession.getDiagramTuple(diagramId).getStunnerDiagram());
    }

    @Test
    public void testGetDMNDiagrams() {
        final List<DMNDiagramTuple> expected = asList(mock(DMNDiagramTuple.class), mock(DMNDiagramTuple.class));
        doReturn(expected).when(dmnDiagramsSessionState).getDMNDiagrams();

        final List<DMNDiagramTuple> actual = dmnDiagramsSession.getDMNDiagrams();

        assertEquals(expected, actual);
    }

    @Test
    public void testSetCurrentDMNDiagramElement() {
        final DMNDiagramElement diagramElement = new DMNDiagramElement(new Id(), new Name("DRG"));
        final Diagram stunnerDiagram = mock(Diagram.class);
        final DMNDiagramSelected selectedDiagram = new DMNDiagramSelected(diagramElement);

        dmnDiagramsSession.add(diagramElement, stunnerDiagram);
        dmnDiagramsSession.onDMNDiagramSelected(selectedDiagram);

        verify(dmnDiagramsSessionState).setCurrentDMNDiagramElement(diagramElement);
    }

    @Test
    public void testGetCurrentDMNDiagramElement() {

        final DMNDiagramElement diagramElement = new DMNDiagramElement();
        final Diagram stunnerDiagram = mock(Diagram.class);
        final DMNDiagramSelected selectedDiagram = new DMNDiagramSelected(diagramElement);

        dmnDiagramsSession.add(diagramElement, stunnerDiagram);
        dmnDiagramsSession.onDMNDiagramSelected(selectedDiagram);

        final Optional<DMNDiagramElement> currentDMNDiagramElement = dmnDiagramsSession.getCurrentDMNDiagramElement();

        assertTrue(currentDMNDiagramElement.isPresent());
        assertEquals(diagramElement, currentDMNDiagramElement.get());
    }

    @Test
    public void testGetCurrentDiagram() {

        final DMNDiagramElement diagramElement = new DMNDiagramElement();
        final Diagram stunnerDiagram = mock(Diagram.class);
        final DMNDiagramSelected selectedDiagram = new DMNDiagramSelected(diagramElement);

        dmnDiagramsSession.add(diagramElement, stunnerDiagram);
        dmnDiagramsSession.onDMNDiagramSelected(selectedDiagram);

        final Optional<Diagram> currentDiagram = dmnDiagramsSession.getCurrentDiagram();

        assertTrue(currentDiagram.isPresent());
        assertEquals(stunnerDiagram, currentDiagram.get());
    }

    @Test
    public void testGetDRGDiagram() {
        final Diagram expected = mock(Diagram.class);
        doReturn(expected).when(dmnDiagramsSessionState).getDRGDiagram();
        final Diagram actual = dmnDiagramsSession.getDRGDiagram();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetDRGDiagramElement() {
        final DMNDiagramElement expected = mock(DMNDiagramElement.class);
        doReturn(expected).when(dmnDiagramsSessionState).getDRGDiagramElement();
        final DMNDiagramElement actual = dmnDiagramsSession.getDRGDiagramElement();
        assertEquals(expected, actual);
    }

    @Test
    public void testClear() {

        final DMNDiagramElement dmnDiagram = mock(DMNDiagramElement.class);
        final Diagram stunnerDiagram = mock(Diagram.class);
        final String diagramId = "0000";

        when(dmnDiagram.getId()).thenReturn(new Id(diagramId));

        dmnDiagramsSession.add(dmnDiagram, stunnerDiagram);

        assertEquals(dmnDiagram, dmnDiagramsSession.getDMNDiagramElement(diagramId));
        assertEquals(stunnerDiagram, dmnDiagramsSession.getDiagram(diagramId));
        assertEquals(dmnDiagram, dmnDiagramsSession.getDiagramTuple(diagramId).getDMNDiagram());
        assertEquals(stunnerDiagram, dmnDiagramsSession.getDiagramTuple(diagramId).getStunnerDiagram());

        dmnDiagramsSession.clear();

        assertNull(dmnDiagramsSession.getDMNDiagramElement(diagramId));
        assertNull(dmnDiagramsSession.getDiagram(diagramId));
        assertNull(dmnDiagramsSession.getDiagramTuple(diagramId).getDMNDiagram());
        assertNull(dmnDiagramsSession.getDiagramTuple(diagramId).getStunnerDiagram());
    }

    @Test
    public void testGetModelDRGElements() {
        final List<DRGElement> expected = asList(mock(DRGElement.class), mock(DRGElement.class));
        doReturn(expected).when(dmnDiagramsSessionState).getModelDRGElements();
        final List<DRGElement> actual = dmnDiagramsSession.getModelDRGElements();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetModelImports() {
        final List<Import> expected = asList(mock(Import.class), mock(Import.class));
        doReturn(expected).when(dmnDiagramsSessionState).getModelImports();
        final List<Import> actual = dmnDiagramsSession.getModelImports();
        assertEquals(expected, actual);
    }

    @Test
    public void testIsGlobalGraphWhenItReturnsTrue() {
        final DMNDiagramElement diagramElement = new DMNDiagramElement(new Id(), new Name("DRG"));
        final Diagram stunnerDiagram = mock(Diagram.class);
        dmnDiagramsSession.add(diagramElement, stunnerDiagram);
        dmnDiagramsSession.onDMNDiagramSelected(new DMNDiagramSelected(diagramElement));
        assertTrue(dmnDiagramsSession.isGlobalGraphSelected());
    }

    @Test
    public void testIsGlobalGraphWhenItReturnsFalse() {
        final DMNDiagramElement diagramElement = new DMNDiagramElement(new Id(), new Name("None"));
        final Diagram stunnerDiagram = mock(Diagram.class);
        dmnDiagramsSession.add(diagramElement, stunnerDiagram);
        dmnDiagramsSession.onDMNDiagramSelected(new DMNDiagramSelected(diagramElement));
        assertFalse(dmnDiagramsSession.isGlobalGraphSelected());
    }

    @Test
    public void testGetCurrentDiagramId() {
        final DMNDiagramElement diagramElement = mock(DMNDiagramElement.class);
        final Diagram stunnerDiagram = mock(Diagram.class);
        final DMNDiagramSelected selectedDiagram = new DMNDiagramSelected(diagramElement);
        final Id id = mock(Id.class);
        final String expectedId = "value";

        when(id.getValue()).thenReturn(expectedId);
        when(diagramElement.getId()).thenReturn(id);

        dmnDiagramsSession.add(diagramElement, stunnerDiagram);
        dmnDiagramsSession.onDMNDiagramSelected(selectedDiagram);

        final String actualId = dmnDiagramsSession.getCurrentDiagramId();

        assertEquals(expectedId, actualId);
    }

    @Test
    public void testsSessionStatePresentWhenItReturnsTrue() {
        final DMNDiagramsSessionState sessionState = mock(DMNDiagramsSessionState.class);
        doReturn(sessionState).when(dmnDiagramsSession).getSessionState();
        assertTrue(dmnDiagramsSession.isSessionStatePresent());
    }

    @Test
    public void testsSessionStatePresentWhenItReturnsFalse() {
        doReturn(null).when(dmnDiagramsSession).getSessionState();
        assertFalse(dmnDiagramsSession.isSessionStatePresent());
    }

    @Test
    public void testGetNodesFromAllDiagramsWithContentId() {

        final String contentId = "contentId";
        final String anotherId1 = "anotherId1";
        final String anotherId2 = "anotherId1";

        final Node expected1 = createNodeWithContentDefinitionId(contentId);
        final Node notExpected1 = createNodeWithContentDefinitionId(anotherId1);
        final Node notExpected2 = createNodeWithContentDefinitionId(anotherId2);
        final Node expected2 = createNodeWithContentDefinitionId(contentId);
        final Node expected3 = createNodeWithContentDefinitionId(contentId);

        final List<Node> nodes = asList(expected1, notExpected1, notExpected2, expected2, expected3);

        doReturn(nodes).when(dmnDiagramsSession).getAllNodes();

        final List<Node> foundNodes = dmnDiagramsSession.getNodesFromAllDiagramsWithContentId(contentId);

        assertEquals(3, foundNodes.size());
        assertTrue(foundNodes.contains(expected1));
        assertTrue(foundNodes.contains(expected2));
        assertTrue(foundNodes.contains(expected3));
    }

    @Test
    public void testDefinitionContainsDRGElement() {

        final Node nodeWithContentDefinition = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final DRGElement drgElement = mock(DRGElement.class);

        when(nodeWithContentDefinition.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(drgElement);

        assertTrue(dmnDiagramsSession.definitionContainsDRGElement(nodeWithContentDefinition));
    }

    @Test
    public void testDefinitionContainsDRGElement_WhenDefinitionIsNotDRGElement() {

        final Node nodeWithContentDefinition = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final Object obj = mock(Object.class);

        when(nodeWithContentDefinition.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(obj);

        assertFalse(dmnDiagramsSession.definitionContainsDRGElement(nodeWithContentDefinition));
    }

    @Test
    public void testDefinitionContainsDRGElement_WhenContentIsNotDefinition() {

        final Node nodeWithContentDefinition = mock(Node.class);
        final Object obj = mock(Object.class);

        when(nodeWithContentDefinition.getContent()).thenReturn(obj);

        assertFalse(dmnDiagramsSession.definitionContainsDRGElement(nodeWithContentDefinition));
    }

    @Test
    public void testGetDRGElementFromContentDefinition() {
        final Node nodeWithContentDefinition = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final DRGElement drgElement = mock(DRGElement.class);

        when(nodeWithContentDefinition.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(drgElement);

        final DRGElement actual = dmnDiagramsSession.getDRGElementFromContentDefinition(nodeWithContentDefinition);

        assertEquals(drgElement, actual);
    }

    @Test
    public void testOnSessionDiagramOpenedEvent() {
        final SessionDiagramOpenedEvent sessionDiagramOpenedEvent = mock(SessionDiagramOpenedEvent.class);
        dmnDiagramsSession.onSessionDiagramOpenedEvent(sessionDiagramOpenedEvent);

        verify(dmnDiagramsSession).loadHistoryForTheCurrentDiagram();
    }

    @Test
    public void testLoadHistoryForTheCurrentDiagram() {

        final Map<String, List<Command<AbstractCanvasHandler, CanvasViolation>>> storedRedoHistory = mock(Map.class);
        final Map<String, List<Command<AbstractCanvasHandler, CanvasViolation>>> storedUndoHistory = mock(Map.class);
        final String diagramId = "diagramId";
        final EditorSession editorSession = mock(EditorSession.class);
        final Optional<EditorSession> optionalEditorSession = Optional.of(editorSession);
        final Registry<Command<AbstractCanvasHandler, CanvasViolation>> undoCommandRegistry = mock(Registry.class);
        final Registry<Command<AbstractCanvasHandler, CanvasViolation>> redoCommandRegistry = mock(Registry.class);
        final List<Command<AbstractCanvasHandler, CanvasViolation>> redoHistory = mock(List.class);
        final List<Command<AbstractCanvasHandler, CanvasViolation>> undoHistory = mock(List.class);

        doReturn(storedRedoHistory).when(dmnDiagramsSession).getStoredRedoHistories();
        doReturn(storedUndoHistory).when(dmnDiagramsSession).getStoredUndoHistories();
        doReturn(diagramId).when(dmnDiagramsSession).getCurrentDiagramId();
        doReturn(optionalEditorSession).when(dmnDiagramsSession).getCurrentSession();

        when(storedRedoHistory.containsKey(diagramId)).thenReturn(true);
        when(storedUndoHistory.containsKey(diagramId)).thenReturn(true);
        when(editorSession.getCommandRegistry()).thenReturn(undoCommandRegistry);
        when(editorSession.getRedoCommandRegistry()).thenReturn(redoCommandRegistry);
        when(storedRedoHistory.get(diagramId)).thenReturn(redoHistory);
        when(storedUndoHistory.get(diagramId)).thenReturn(undoHistory);

        doNothing().when(dmnDiagramsSession).loadHistoryToTheRegistry(redoHistory, redoCommandRegistry);
        doNothing().when(dmnDiagramsSession).loadHistoryToTheRegistry(undoHistory, undoCommandRegistry);

        dmnDiagramsSession.loadHistoryForTheCurrentDiagram();

        verify(dmnDiagramsSession).loadHistoryToTheRegistry(redoHistory, redoCommandRegistry);
        verify(dmnDiagramsSession).loadHistoryToTheRegistry(undoHistory, undoCommandRegistry);
        verify(dmnDiagramsSession).notifyRegistryChanged();
        verify(undoCommandRegistry, never()).clear();
        verify(redoCommandRegistry, never()).clear();
    }

    @Test
    public void testLoadHistoryForTheCurrentDiagram_WhenItIsNotEditorSession() {

        final Map<String, List<Command<AbstractCanvasHandler, CanvasViolation>>> storedRedoHistory = mock(Map.class);
        final Map<String, List<Command<AbstractCanvasHandler, CanvasViolation>>> storedUndoHistory = mock(Map.class);
        final String diagramId = "diagramId";
        final ViewerSession viewerSession = mock(ViewerSession.class);
        final Optional<ClientSession> optionalViewerSession = Optional.of(viewerSession);
        final Registry<Command<AbstractCanvasHandler, CanvasViolation>> undoCommandRegistry = mock(Registry.class);
        final Registry<Command<AbstractCanvasHandler, CanvasViolation>> redoCommandRegistry = mock(Registry.class);
        final List<Command<AbstractCanvasHandler, CanvasViolation>> redoHistory = mock(List.class);
        final List<Command<AbstractCanvasHandler, CanvasViolation>> undoHistory = mock(List.class);

        doReturn(storedRedoHistory).when(dmnDiagramsSession).getStoredRedoHistories();
        doReturn(storedUndoHistory).when(dmnDiagramsSession).getStoredUndoHistories();
        doReturn(diagramId).when(dmnDiagramsSession).getCurrentDiagramId();
        doReturn(optionalViewerSession).when(dmnDiagramsSession).getCurrentSession();

        when(storedRedoHistory.containsKey(diagramId)).thenReturn(true);
        when(storedUndoHistory.containsKey(diagramId)).thenReturn(true);
        when(storedRedoHistory.get(diagramId)).thenReturn(redoHistory);
        when(storedUndoHistory.get(diagramId)).thenReturn(undoHistory);

        doNothing().when(dmnDiagramsSession).loadHistoryToTheRegistry(redoHistory, redoCommandRegistry);
        doNothing().when(dmnDiagramsSession).loadHistoryToTheRegistry(undoHistory, undoCommandRegistry);

        dmnDiagramsSession.loadHistoryForTheCurrentDiagram();

        verify(dmnDiagramsSession, never()).loadHistoryToTheRegistry(redoHistory, redoCommandRegistry);
        verify(dmnDiagramsSession, never()).loadHistoryToTheRegistry(undoHistory, undoCommandRegistry);
        verify(dmnDiagramsSession, never()).notifyRegistryChanged();
        verify(undoCommandRegistry, never()).clear();
        verify(redoCommandRegistry, never()).clear();
    }

    @Test
    public void testLoadHistoryForTheCurrentDiagram_WhenThereIsNotHistoryStored() {

        final Map<String, List<Command<AbstractCanvasHandler, CanvasViolation>>> storedRedoHistory = mock(Map.class);
        final Map<String, List<Command<AbstractCanvasHandler, CanvasViolation>>> storedUndoHistory = mock(Map.class);
        final String diagramId = "diagramId";
        final EditorSession editorSession = mock(EditorSession.class);
        final Optional<EditorSession> optionalEditorSession = Optional.of(editorSession);
        final Registry<Command<AbstractCanvasHandler, CanvasViolation>> undoCommandRegistry = mock(Registry.class);
        final Registry<Command<AbstractCanvasHandler, CanvasViolation>> redoCommandRegistry = mock(Registry.class);
        final List<Command<AbstractCanvasHandler, CanvasViolation>> redoHistory = mock(List.class);
        final List<Command<AbstractCanvasHandler, CanvasViolation>> undoHistory = mock(List.class);

        doReturn(storedRedoHistory).when(dmnDiagramsSession).getStoredRedoHistories();
        doReturn(storedUndoHistory).when(dmnDiagramsSession).getStoredUndoHistories();
        doReturn(diagramId).when(dmnDiagramsSession).getCurrentDiagramId();
        doReturn(optionalEditorSession).when(dmnDiagramsSession).getCurrentSession();

        when(storedRedoHistory.containsKey(diagramId)).thenReturn(false);
        when(storedUndoHistory.containsKey(diagramId)).thenReturn(false);
        when(editorSession.getCommandRegistry()).thenReturn(undoCommandRegistry);
        when(editorSession.getRedoCommandRegistry()).thenReturn(redoCommandRegistry);
        when(storedRedoHistory.get(diagramId)).thenReturn(redoHistory);
        when(storedUndoHistory.get(diagramId)).thenReturn(undoHistory);

        doNothing().when(dmnDiagramsSession).loadHistoryToTheRegistry(redoHistory, redoCommandRegistry);
        doNothing().when(dmnDiagramsSession).loadHistoryToTheRegistry(undoHistory, undoCommandRegistry);

        dmnDiagramsSession.loadHistoryForTheCurrentDiagram();

        verify(dmnDiagramsSession, never()).loadHistoryToTheRegistry(redoHistory, redoCommandRegistry);
        verify(dmnDiagramsSession, never()).loadHistoryToTheRegistry(undoHistory, undoCommandRegistry);
        verify(dmnDiagramsSession).notifyRegistryChanged();
        verify(undoCommandRegistry).clear();
        verify(redoCommandRegistry).clear();
    }

    @Test
    public void testLoadHistoryToTheRegistry() {

        final Command<AbstractCanvasHandler, CanvasViolation> command1 = mock(Command.class);
        final Command<AbstractCanvasHandler, CanvasViolation> command2 = mock(Command.class);
        final Command<AbstractCanvasHandler, CanvasViolation> command3 = mock(Command.class);
        final List<Command<AbstractCanvasHandler, CanvasViolation>> history = Arrays.asList(command1, command2, command3);
        final Registry<Command<AbstractCanvasHandler, CanvasViolation>> registry = mock(Registry.class);
        final InOrder inOrder = inOrder(registry);

        dmnDiagramsSession.loadHistoryToTheRegistry(history, registry);

        inOrder.verify(registry).clear();
        inOrder.verify(registry).register(command1);
        inOrder.verify(registry).register(command2);
        inOrder.verify(registry).register(command3);
    }

    @Test
    public void testStoreCurrentRegistryHistory() {

        final Map<String, List<Command<AbstractCanvasHandler, CanvasViolation>>> storedRedoHistory = mock(Map.class);
        final Map<String, List<Command<AbstractCanvasHandler, CanvasViolation>>> storedUndoHistory = mock(Map.class);
        final String diagramId = "diagramId";
        final EditorSession editorSession = mock(EditorSession.class);
        final Optional<EditorSession> optionalEditorSession = Optional.of(editorSession);
        final List<Command<AbstractCanvasHandler, CanvasViolation>> undoHistory = mock(List.class);
        final List<Command<AbstractCanvasHandler, CanvasViolation>> redoHistory = mock(List.class);
        final Registry<Command<AbstractCanvasHandler, CanvasViolation>> undoCommandRegistry = mock(Registry.class);
        final Registry<Command<AbstractCanvasHandler, CanvasViolation>> redoCommandRegistry = mock(Registry.class);

        doReturn(storedRedoHistory).when(dmnDiagramsSession).getStoredRedoHistories();
        doReturn(storedUndoHistory).when(dmnDiagramsSession).getStoredUndoHistories();
        doReturn(diagramId).when(dmnDiagramsSession).getCurrentDiagramId();
        doReturn(optionalEditorSession).when(dmnDiagramsSession).getCurrentSession();

        when(editorSession.getCommandRegistry()).thenReturn(undoCommandRegistry);
        when(editorSession.getRedoCommandRegistry()).thenReturn(redoCommandRegistry);
        when(undoCommandRegistry.getHistory()).thenReturn(undoHistory);
        when(redoCommandRegistry.getHistory()).thenReturn(redoHistory);

        dmnDiagramsSession.storeCurrentRegistryHistory();

        verify(storedUndoHistory).put(diagramId, undoHistory);
        verify(storedRedoHistory).put(diagramId, redoHistory);
    }

    @Test
    public void testOnDMNDiagramSelected_WhenBelongsToCurrentSessionState() {

        final DMNDiagramElement selectedDiagramElement = mock(DMNDiagramElement.class);
        final DMNDiagramSelected dmnDiagramSelected = new DMNDiagramSelected(selectedDiagramElement);

        doNothing().when(dmnDiagramsSession).storeCurrentRegistryHistory();
        doReturn(true).when(dmnDiagramsSession).belongsToCurrentSessionState(selectedDiagramElement);

        dmnDiagramsSession.onDMNDiagramSelected(dmnDiagramSelected);

        verify(dmnDiagramsSessionState).setCurrentDMNDiagramElement(selectedDiagramElement);
        verify(dmnDiagramsSession).storeCurrentRegistryHistory();
    }

    @Test
    public void testOnDMNDiagramSelected_WhenDopesNotBelongsToCurrentSessionState() {

        final DMNDiagramElement selectedDiagramElement = mock(DMNDiagramElement.class);
        final DMNDiagramSelected dmnDiagramSelected = new DMNDiagramSelected(selectedDiagramElement);

        doNothing().when(dmnDiagramsSession).storeCurrentRegistryHistory();
        doReturn(false).when(dmnDiagramsSession).belongsToCurrentSessionState(selectedDiagramElement);

        dmnDiagramsSession.onDMNDiagramSelected(dmnDiagramSelected);

        verify(dmnDiagramsSessionState, never()).setCurrentDMNDiagramElement(selectedDiagramElement);
        verify(dmnDiagramsSession).storeCurrentRegistryHistory();
    }

    private Node createNodeWithContentDefinitionId(final String contentDefinitionId) {

        final Node node = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final DRGElement drgElement = mock(DRGElement.class);
        when(drgElement.getContentDefinitionId()).thenReturn(contentDefinitionId);
        when(definition.getDefinition()).thenReturn(drgElement);
        when(node.getContent()).thenReturn(definition);
        return node;
    }
}

