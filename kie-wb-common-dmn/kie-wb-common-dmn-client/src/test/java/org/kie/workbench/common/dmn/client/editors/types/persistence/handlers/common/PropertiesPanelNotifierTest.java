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

package org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.common;

import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.IsInformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class PropertiesPanelNotifierTest {

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private SessionManager sessionManager;

    @Captor
    private ArgumentCaptor<RefreshFormPropertiesEvent> propertiesEventArgumentCaptor;

    private PropertiesPanelNotifier notifier;

    @Before
    public void setup() {
        notifier = spy(new PropertiesPanelNotifier(refreshFormPropertiesEvent, sessionManager));
    }

    @Test
    public void testNotifyPanel() {

        final Node node1 = mock(Node.class);
        final Node node2 = mock(Node.class);
        final Object definition1 = mock(Object.class);
        final Object definition2 = mock(Object.class);

        doReturn(asList(node1, node2)).when(notifier).getNodes();
        doReturn(definition1).when(notifier).getDefinition(node1);
        doReturn(definition2).when(notifier).getDefinition(node2);

        notifier.notifyPanel();

        verify(notifier).notifyVariables(node1, definition1);
        verify(notifier).notifyExpressions(node2, definition2);
    }

    @Test
    public void testOnCanvasSelectionEvent() {

        final CanvasSelectionEvent selectionEvent = mock(CanvasSelectionEvent.class);
        final String uuid1 = "uuid1";
        final String uuid2 = "uuid2";
        final List<String> t = asList(uuid1, uuid2);

        when(selectionEvent.getIdentifiers()).thenReturn(t);

        notifier.onCanvasSelectionEvent(selectionEvent);

        verify(notifier).setSelectedElementUUID(uuid1);
    }

    @Test
    public void testOnDomainObjectSelectionEvent() {

        final DomainObjectSelectionEvent selectionEvent = mock(DomainObjectSelectionEvent.class);
        final DomainObject domainObject = mock(DomainObject.class);
        final String uuid = "uuid";

        when(selectionEvent.getDomainObject()).thenReturn(domainObject);
        when(domainObject.getDomainObjectUUID()).thenReturn(uuid);

        notifier.onDomainObjectSelectionEvent(selectionEvent);

        verify(notifier).setSelectedElementUUID(uuid);
    }

    @Test
    public void testNotifyExpressions() {

        final Node node = mock(Node.class);
        final HasExpression hasExpression = mock(HasExpression.class);
        final Expression expression = mock(Expression.class);
        final HasTypeRef hasTypeRef1 = mock(HasTypeRef.class);
        final HasTypeRef hasTypeRef2 = mock(HasTypeRef.class);

        when(expression.getHasTypeRefs()).thenReturn(asList(hasTypeRef1, hasTypeRef2));
        when(hasExpression.getExpression()).thenReturn(expression);
        doNothing().when(notifier).notifyOutdatedElement(any(), any());

        notifier.notifyExpressions(node, hasExpression);

        verify(notifier).notifyOutdatedElement(node, hasTypeRef1);
        verify(notifier).notifyOutdatedElement(node, hasTypeRef2);
    }

    @Test
    public void testNotifyExpressionsWhenDefinitionIsDoesNotHaveExpression() {

        final Node node = mock(Node.class);
        final Object definition = mock(Object.class);

        notifier.notifyExpressions(node, definition);

        verify(notifier, never()).notifyOutdatedElement(any(), any());
    }

    @Test
    public void testNotifyVariables() {

        final Node node = mock(Node.class);
        final HasVariable hasVariable = mock(HasVariable.class);
        final IsInformationItem informationItem = mock(IsInformationItem.class);

        when(hasVariable.getVariable()).thenReturn(informationItem);
        doNothing().when(notifier).notifyOutdatedElement(any(), any());

        notifier.notifyVariables(node, hasVariable);

        verify(notifier).notifyOutdatedElement(node, informationItem);
    }

    @Test
    public void testNotifyVariablesWhenDefinitionIsDoesNotHaveVariables() {

        final Node node = mock(Node.class);
        final Object definition = mock(Object.class);

        notifier.notifyVariables(node, definition);

        verify(notifier, never()).notifyOutdatedElement(any(), any());
    }

    @Test
    public void testNotifyOutdatedNodeWhenNodeIsOutdated() {

        final Node node = mock(Node.class);
        final HasTypeRef elementTypeRef = mock(HasTypeRef.class);
        final QName newQName = mock(QName.class);
        final QName typeRef = mock(QName.class);
        final String oldLocalPart = "tPerson";
        final String elementLocalPart = "tPerson";

        when(elementTypeRef.getTypeRef()).thenReturn(typeRef);
        when(typeRef.getLocalPart()).thenReturn(elementLocalPart);
        doNothing().when(notifier).refreshFormProperties(any());

        notifier.withOldLocalPart(oldLocalPart)
                .withNewQName(newQName)
                .notifyOutdatedElement(node, elementTypeRef);

        verify(elementTypeRef).setTypeRef(newQName);
        verify(notifier).refreshFormProperties(node);
    }

    @Test
    public void testNotifyOutdatedNodeWhenNodeIsNotOutdated() {

        final Node node = mock(Node.class);
        final HasTypeRef elementTypeRef = mock(HasTypeRef.class);
        final QName newQName = mock(QName.class);
        final QName typeRef = mock(QName.class);
        final String oldLocalPart = "tPerson";
        final String elementLocalPart = "";

        when(elementTypeRef.getTypeRef()).thenReturn(typeRef);
        when(typeRef.getLocalPart()).thenReturn(elementLocalPart);

        notifier.withOldLocalPart(oldLocalPart)
                .withNewQName(newQName)
                .notifyOutdatedElement(node, elementTypeRef);

        verify(elementTypeRef, never()).setTypeRef(any());
        verify(notifier, never()).refreshFormProperties(any());
    }

    @Test
    public void testGetDefinition() {

        final Node node = mock(Node.class);
        final ViewImpl content = mock(ViewImpl.class);
        final Object expected = mock(Object.class);

        when(content.getDefinition()).thenReturn(expected);
        when(node.getContent()).thenReturn(content);

        final Object actual = notifier.getDefinition(node);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetNodes() {

        final Graph graph = mock(Graph.class);
        final Node node1 = mock(Node.class);
        final Node node2 = mock(Node.class);
        final Iterable<Node> expectedNodes = asList(node1, node2);

        when(graph.nodes()).thenReturn(expectedNodes);
        doReturn(Optional.of(graph)).when(notifier).getGraph();

        final List<Node> actualNodes = notifier.getNodes();

        assertEquals(expectedNodes, actualNodes);
    }

    @Test
    public void testGetGraph() {

        final ClientSession clientSession = mock(ClientSession.class);
        final CanvasHandler canvasHandler = mock(CanvasHandler.class);
        final Diagram diagram = mock(Diagram.class);
        final Optional<Graph> expected = Optional.of(mock(Graph.class));

        when(diagram.getGraph()).thenReturn(expected.get());
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(clientSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(sessionManager.getCurrentSession()).thenReturn(clientSession);

        final Optional<Graph<?, Node>> actual = notifier.getGraph();

        assertEquals(expected, actual);
    }

    @Test
    public void testRefreshFormPropertiesWhenPropertiesPanelIsUpdated() {

        final Node node = mock(Node.class);
        final ClientSession clientSession = mock(ClientSession.class);
        final String uuid = "uuid";

        when(node.getUUID()).thenReturn(uuid);
        when(sessionManager.getCurrentSession()).thenReturn(clientSession);
        when(notifier.getSelectedElementUUID()).thenReturn(Optional.of(uuid));

        notifier.refreshFormProperties(node);

        verify(refreshFormPropertiesEvent).fire(propertiesEventArgumentCaptor.capture());

        final RefreshFormPropertiesEvent value = propertiesEventArgumentCaptor.getValue();

        assertEquals(uuid, value.getUuid());
        assertEquals(clientSession, value.getSession());
    }

    @Test
    public void testRefreshFormPropertiesWhenPropertiesPanelIsNotUpdated() {

        final Node node = mock(Node.class);
        final ClientSession clientSession = mock(ClientSession.class);

        when(node.getUUID()).thenReturn("uuid1");
        when(sessionManager.getCurrentSession()).thenReturn(clientSession);
        when(notifier.getSelectedElementUUID()).thenReturn(Optional.of("uuid2"));

        notifier.refreshFormProperties(node);

        verify(refreshFormPropertiesEvent, never()).fire(any());
    }
}
