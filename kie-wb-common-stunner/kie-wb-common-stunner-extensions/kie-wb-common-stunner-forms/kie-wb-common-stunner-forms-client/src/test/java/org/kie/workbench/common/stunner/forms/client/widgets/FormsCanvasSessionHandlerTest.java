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
package org.kie.workbench.common.stunner.forms.client.widgets;

import java.util.Arrays;
import java.util.Optional;

import org.jboss.errai.databinding.client.HasProperties;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateDomainObjectPropertyCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DataBinder.class)
public class FormsCanvasSessionHandlerTest {

    private static final String GRAPH_UUID = "graph-uuid";

    private static final String UUID = "uuid";

    private static final String FIELD_NAME = "fieldName";

    private static final String FIELD_PROPERTY_ID = "fieldPropertyId";

    private static final String FIELD_VALUE = "value";

    @Mock
    private EditorSession session;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private PropertyAdapter propertyAdapter;

    @Mock
    private AbstractCanvasHandler abstractCanvasHandler;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> commandFactory;

    @Mock
    private SelectionControl selectionControl;

    @Mock
    private FormsCanvasSessionHandler.FormRenderer formRenderer;

    @Mock
    private Index<?, ?> index;

    @Mock
    private Diagram<?, ?> diagram;

    @Mock
    private Graph graph;

    @Mock
    private Element<? extends Definition<?>> element;

    @Mock
    private DomainObject domainObject;

    @Mock
    private FormsCanvasSessionHandler.FormsDomainObjectCanvasListener domainObjectCanvasListener;

    private RefreshFormPropertiesEvent refreshFormPropertiesEvent;

    private CanvasSelectionEvent canvasSelectionEvent;

    private DomainObjectSelectionEvent domainObjectSelectionEvent;

    private FormsCanvasSessionHandler handler;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.refreshFormPropertiesEvent = new RefreshFormPropertiesEvent(session, UUID);
        this.handler = spy(new FormsCanvasSessionHandler(definitionManager, commandFactory, sessionCommandManager) {
            @Override
            protected FormsCanvasSessionHandler.FormsDomainObjectCanvasListener getFormsDomainObjectCanvasListener() {
                return domainObjectCanvasListener;
            }
        });
        this.handler.setRenderer(formRenderer);

        when(session.getCanvasHandler()).thenReturn(abstractCanvasHandler);
        when(session.getSelectionControl()).thenReturn(selectionControl);
        when(abstractCanvasHandler.getGraphIndex()).thenReturn(index);
        when(abstractCanvasHandler.getDiagram()).thenReturn(diagram);
        when(index.get(eq(UUID))).thenReturn(element);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.getUUID()).thenReturn(GRAPH_UUID);

        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(propertyAdapter.getId(any())).thenReturn(FIELD_PROPERTY_ID);
    }

    @Test
    public void testShowWithNothingSelected() {
        handler.bind(session);

        when(selectionControl.getSelectedItemDefinition()).thenReturn(Optional.empty());

        handler.show();

        verifyNoMoreInteractions(formRenderer);
    }

    @Test
    public void testShowWithElementSelected() {
        handler.bind(session);

        when(selectionControl.getSelectedItemDefinition()).thenReturn(Optional.of(element));

        handler.show();

        verify(formRenderer).render(eq(GRAPH_UUID),
                                    eq(element),
                                    any(Command.class));
    }

    @Test
    public void testShowWithDomainObjectSelected() {
        handler.bind(session);

        when(selectionControl.getSelectedItemDefinition()).thenReturn(Optional.of(domainObject));

        handler.show();

        verify(formRenderer).render(eq(GRAPH_UUID),
                                    eq(domainObject),
                                    any(Command.class));
    }

    @Test
    public void testOnRefreshFormPropertiesEventSameSession() {
        handler.bind(session);

        handler.onRefreshFormPropertiesEvent(refreshFormPropertiesEvent);

        verify(formRenderer).render(anyString(), eq(element), any(Command.class));
    }

    @Test
    public void testOnRefreshFormPropertiesEventDifferentSession() {
        handler.bind(mock(EditorSession.class));

        handler.onRefreshFormPropertiesEvent(refreshFormPropertiesEvent);

        verify(formRenderer, never()).render(anyString(), any(Element.class), any(Command.class));
    }

    @Test
    public void testOnCanvasSelectionEventSameSession() {
        handler.bind(session);

        canvasSelectionEvent = new CanvasSelectionEvent(abstractCanvasHandler, UUID);

        handler.onCanvasSelectionEvent(canvasSelectionEvent);

        verify(formRenderer).render(anyString(), eq(element), any(Command.class));
    }

    @Test
    public void testOnCanvasSelectionEventSameSessionMultipleNodes() {
        handler.bind(mock(EditorSession.class));

        canvasSelectionEvent = new CanvasSelectionEvent(abstractCanvasHandler, Arrays.asList(new String[]{UUID, UUID}));

        handler.onCanvasSelectionEvent(canvasSelectionEvent);

        verify(formRenderer, never()).render(anyString(), any(Element.class), any(Command.class));
    }

    @Test
    public void testOnCanvasSelectionEventDifferentSession() {
        handler.bind(mock(EditorSession.class));

        canvasSelectionEvent = new CanvasSelectionEvent(abstractCanvasHandler, UUID);

        handler.onCanvasSelectionEvent(canvasSelectionEvent);

        verify(formRenderer, never()).render(anyString(), any(DomainObject.class), any(Command.class));
    }

    @Test
    public void testOnDomainObjectSelectionEventSameSession() {
        handler.bind(session);

        domainObjectSelectionEvent = new DomainObjectSelectionEvent(abstractCanvasHandler, domainObject);

        handler.onDomainObjectSelectionEvent(domainObjectSelectionEvent);

        verify(formRenderer).render(anyString(), eq(domainObject), any(Command.class));
    }

    @Test
    public void testOnDomainObjectSelectionEventDifferentSession() {
        handler.bind(mock(EditorSession.class));

        domainObjectSelectionEvent = new DomainObjectSelectionEvent(abstractCanvasHandler, domainObject);

        handler.onDomainObjectSelectionEvent(domainObjectSelectionEvent);

        verify(formRenderer, never()).render(anyString(), any(Element.class), any(Command.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteUpdateDomainObjectProperty() {
        PowerMockito.mockStatic(DataBinder.class);

        final DataBinder dataBinder = mock(DataBinder.class);
        final HasProperties hasProperties = mock(HasProperties.class);
        when(DataBinder.forModel(eq(domainObject))).thenReturn(dataBinder);
        when(dataBinder.getModel()).thenReturn(hasProperties);
        when(hasProperties.get(eq(FIELD_NAME))).thenReturn(String.class);

        handler.bind(session);

        handler.executeUpdateDomainObjectProperty(domainObject,
                                                  FIELD_NAME,
                                                  FIELD_VALUE);

        final InOrder inOrder = inOrder(domainObjectCanvasListener,
                                        commandFactory,
                                        sessionCommandManager,
                                        domainObjectCanvasListener);

        inOrder.verify(domainObjectCanvasListener).startProcessing();
        inOrder.verify(commandFactory).updateDomainObjectPropertyValue(eq(domainObject),
                                                                       eq(FIELD_PROPERTY_ID),
                                                                       eq(FIELD_VALUE));
        inOrder.verify(sessionCommandManager).execute(eq(abstractCanvasHandler),
                                                      any(UpdateDomainObjectPropertyCommand.class));
        inOrder.verify(domainObjectCanvasListener).endProcessing();
    }
}
