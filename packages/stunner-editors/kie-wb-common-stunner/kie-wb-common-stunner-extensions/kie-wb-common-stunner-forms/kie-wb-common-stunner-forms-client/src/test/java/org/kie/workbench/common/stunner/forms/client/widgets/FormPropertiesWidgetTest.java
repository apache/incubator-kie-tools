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


package org.kie.workbench.common.stunner.forms.client.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.logging.Level;

import javax.enterprise.event.Event;

import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.BindableProxyFactory;
import org.jboss.errai.databinding.client.BindableProxyProvider;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.FormPropertiesOpened;
import org.kie.workbench.common.stunner.forms.client.formFilters.FormFiltersProviderFactory;
import org.kie.workbench.common.stunner.forms.client.formFilters.StunnerFormElementFilterProvider;
import org.kie.workbench.common.stunner.forms.client.widgets.container.FormsContainer;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FormPropertiesWidgetTest {

    private static final String GRAPH_UUID = "graph1";
    private static final String DIAGRAM_NAME = "diagram1";
    private static final String ROOT_UUID = "root1";
    private static final String NODE2_UUID = "node2";
    private static final String DOMAIN_OBJECT_UUID = "domainObject1";
    private static final String DOMAIN_OBJECT_TRANSLATION_KEY = "domainObjectTranslationKey";
    private static final String RANDOM_FIELD = "randomField";
    private static final String CUSTOM_DEFINITION = "MyCustomDefinition";

    @Mock
    private FormPropertiesWidgetView view;
    @Mock
    private DefinitionUtils definitionUtils;
    @Mock
    private FormsCanvasSessionHandler formsCanvasSessionHandler;
    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> commandFactory;
    @Mock
    private Event<FormPropertiesOpened> propertiesOpenedEvent;
    @Mock
    private FormsContainer formsContainer;
    @Mock
    private FormsFlushManager formsFlushManager;
    @Mock
    private TranslationService translationService;
    @Mock
    private EditorSession session;
    @Mock
    private SelectionControl selectionControl;
    @Mock
    private AbstractCanvasHandler canvasHandler;
    @Mock
    private Diagram diagram;
    @Mock
    private Graph graph;
    @Mock
    private Metadata metadata;
    @Mock
    private NodeImpl node;
    @Mock
    private NodeImpl filterednode;
    @Mock
    private NodeImpl node2;
    @Mock
    private GraphImpl graphImpl;

    private EdgeImpl edge;
    @Mock
    private Definition nodeContent;
    @Mock
    private Definition filteredContent;
    @Mock
    private Object nodeDefObject;
    @Mock
    private Index graphIndex;
    @Mock
    private Path path;
    @Mock
    private BindableProxyProvider proxyProvider;
    @Mock
    private BindableProxy<Object> proxy;
    @Mock
    private DomainObject domainObject;

    @Captor
    private ArgumentCaptor<FormsCanvasSessionHandler.FormRenderer> formRendererArgumentCaptor;

    @Captor
    private ArgumentCaptor<FieldChangeHandler> fieldChangeHandlerArgumentCaptor;

    @Captor
    private ArgumentCaptor<FormPropertiesOpened> formPropertiesOpenedArgumentCaptor;

    private Object unmockedDef = new Object();

    private FormPropertiesWidget tested;

    @Before
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void setup() throws Exception {
        when(session.getSelectionControl()).thenReturn(selectionControl);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(graphIndex.get(eq(ROOT_UUID))).thenReturn(node);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(diagram.getName()).thenReturn(DIAGRAM_NAME);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.getUUID()).thenReturn(GRAPH_UUID);
        when(node.getUUID()).thenReturn(ROOT_UUID);
        when(node.getContent()).thenReturn(nodeContent);
        when(filterednode.getUUID()).thenReturn(ROOT_UUID);
        when(filterednode.getContent()).thenReturn(filteredContent);
        when(filteredContent.getDefinition()).thenReturn(CUSTOM_DEFINITION);
        FormFiltersProviderFactory.registerProvider(new StunnerFormElementFilterProvider() {
            @Override
            public Class<?> getDefinitionType() {
                return String.class;
            }

            @Override
            public Collection<FormElementFilter> provideFilters(String elementUUID, Object definition) {
                final Predicate predicate = t -> false;
                final FormElementFilter filter = new FormElementFilter(RANDOM_FIELD, predicate);
                return Arrays.asList(filter);
            }
        });
        when(nodeContent.getDefinition()).thenReturn(nodeDefObject);
        when(node2.getUUID()).thenReturn(NODE2_UUID);
        when(node2.getContent()).thenReturn(nodeContent);
        BindableProxyFactory.addBindableProxy(Object.class,
                                              proxyProvider);
        when(proxyProvider.getBindableProxy()).thenReturn((BindableProxy) proxy);
        when(proxyProvider.getBindableProxy(unmockedDef)).thenReturn((BindableProxy) proxy);
        when(proxy.deepUnwrap()).thenReturn(unmockedDef);
        this.tested = new FormPropertiesWidget(view,
                                               definitionUtils,
                                               formsCanvasSessionHandler,
                                               propertiesOpenedEvent,
                                               formsContainer,
                                               formsFlushManager,
                                               translationService) {
            @Override
            protected void log(final Level level, final String message) {
                //Logging not supported in Unit Test
            }
        };

        doAnswer((i) -> i.getArguments()[0]).when(translationService).getTranslation(anyString());
    }

    @Test
    public void testInit() {
        tested.init();
        verify(formsFlushManager, times(1)).setCurrentContainer(formsContainer);
    }

    @Test
    public void testShowEmpty() {
        when(canvasHandler.getDiagram()).thenReturn(null);
        final Command callback = mock(Command.class);

        tested.bind(session).show(callback);
        verify(formsCanvasSessionHandler).bind(session);
        verify(formsCanvasSessionHandler).show(callback);

        verify(formsContainer, never()).render(anyString(), any(), any(), any(), any(), any());
        verify(propertiesOpenedEvent, never()).fire(formPropertiesOpenedArgumentCaptor.capture());
    }

    /**
     * Figure out how to register a mock as proxy provider used by the FormPropertiesWidget to be able
     * to verify formRenderer mock behaviors.
     * For now, let's say that the runtime exception thrown by erray as there are not proxies available on test scope implies
     * that the logic to obtain selected items from the session is correct and at least, the code gets to the point where
     * it tries to introspect the model object ( selected ).
     */
    @Test
    public void testShowSelectedItem() {
        final Collection<String> selectedItems = new ArrayList<String>(3) {{
            add(ROOT_UUID);
            add("item2");
            add("item3");
        }};
        when(selectionControl.getSelectedItems()).thenReturn(selectedItems);

        final Command callback = mock(Command.class);

        tested.bind(session).show(callback);

        verify(formsCanvasSessionHandler).bind(session);
        verify(formsCanvasSessionHandler).show(callback);
    }

    /**
     * Same as above.
     */
    @Test
    public void testShowCanvasRoot() {
        when(selectionControl.getSelectedItems()).thenReturn(null);
        when(metadata.getCanvasRootUUID()).thenReturn(ROOT_UUID);

        final Command callback = mock(Command.class);

        tested.bind(session).show(callback);

        verify(formsCanvasSessionHandler).bind(session);
        verify(formsCanvasSessionHandler).show(callback);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShowDomainObject() {
        tested.init();

        final String fieldName = "fieldName";
        final String fieldValue = "fieldValue";
        final Command callback = mock(Command.class);

        when(formsCanvasSessionHandler.getDiagram()).thenReturn(diagram);
        when(domainObject.getDomainObjectUUID()).thenReturn(DOMAIN_OBJECT_UUID);
        when(domainObject.getDomainObjectNameTranslationKey()).thenReturn(DOMAIN_OBJECT_TRANSLATION_KEY);
        when(formsCanvasSessionHandler.getSession()).thenReturn(session);

        verify(formsCanvasSessionHandler).setRenderer(formRendererArgumentCaptor.capture());

        final FormsCanvasSessionHandler.FormRenderer formRenderer = formRendererArgumentCaptor.getValue();
        formRenderer.render(GRAPH_UUID, domainObject, callback);

        verify(formsContainer).render(eq(GRAPH_UUID),
                                      eq(DOMAIN_OBJECT_UUID),
                                      eq(domainObject),
                                      Mockito.<Path>any(),
                                      fieldChangeHandlerArgumentCaptor.capture(),
                                      eq(RenderMode.EDIT_MODE));
        final FieldChangeHandler fieldChangeHandler = fieldChangeHandlerArgumentCaptor.getValue();
        fieldChangeHandler.onFieldChange(fieldName, fieldValue);
        verify(formsCanvasSessionHandler).executeUpdateDomainObjectProperty(eq(domainObject),
                                                                            eq(fieldName),
                                                                            eq(fieldValue));

        verify(propertiesOpenedEvent).fire(formPropertiesOpenedArgumentCaptor.capture());

        final FormPropertiesOpened formPropertiesOpened = formPropertiesOpenedArgumentCaptor.getValue();
        assertThat(formPropertiesOpened.getUuid()).isEqualTo(DOMAIN_OBJECT_UUID);
        assertThat(formPropertiesOpened.getName()).isEqualTo(DOMAIN_OBJECT_TRANSLATION_KEY);
        assertThat(formPropertiesOpened.getSession()).isEqualTo(session);

        verify(callback).execute();
    }

    @Test
    public void testShowNullElement() {
        tested.init();

        verify(formsCanvasSessionHandler).setRenderer(formRendererArgumentCaptor.capture());
        final FormsCanvasSessionHandler.FormRenderer formRenderer = formRendererArgumentCaptor.getValue();

        final Command command = mock(Command.class);
        formRenderer.render(GRAPH_UUID, (Element) null, command);

        verify(formsContainer, never()).render(any(), any(), any(), any(), any(), any());
        verify(propertiesOpenedEvent, never()).fire(formPropertiesOpenedArgumentCaptor.capture());
        verify(command, never()).execute();
    }

    @Test
    public void testShowElement() {
        tested.init();

        verify(formsCanvasSessionHandler).setRenderer(formRendererArgumentCaptor.capture());
        final FormsCanvasSessionHandler.FormRenderer formRenderer = formRendererArgumentCaptor.getValue();

        final Command command = mock(Command.class);
        when(formsCanvasSessionHandler.getDiagram()).thenReturn(diagram);

        formRenderer.render(GRAPH_UUID, node, command);
        formRenderer.render(GRAPH_UUID, node, command);
        formRenderer.render(GRAPH_UUID, graphImpl, command);

        verify(formsCanvasSessionHandler, never()).executeUpdateProperty(any(), any(), any());
        verify(formsContainer, atMost(1)).render(any(), any(), any(), any(), any(), any());

        formRenderer.render(GRAPH_UUID, filterednode, command);
        formRenderer.render(GRAPH_UUID, filterednode, command);
        verify(formsContainer, atMost(3)).render(any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testFireFormsPropertiesOpenedEvent() {
        tested.fireFormsPropertiesOpenedEvent("", "");
        verify(propertiesOpenedEvent, times(1)).fire(formPropertiesOpenedArgumentCaptor.capture());
    }

    @Test
    public void testAreElementsPositionSame() {
        tested.init();

        verify(formsCanvasSessionHandler).setRenderer(formRendererArgumentCaptor.capture());
        final FormsCanvasSessionHandler.FormRenderer formRenderer = formRendererArgumentCaptor.getValue();

        final Command command = mock(Command.class);
        when(formsCanvasSessionHandler.getDiagram()).thenReturn(diagram);

        formRenderer.render(GRAPH_UUID, node, command);

        assertEquals("Value is not the same ", tested.areLastPositionsForSameElementSame(node), true);

        formRenderer.render(GRAPH_UUID, node2, command);
        assertEquals("Value is not the same ", tested.areLastPositionsForSameElementSame(node), false);
        assertEquals("Value is not the same ", tested.areLastPositionsForSameElementSame(node2), true);
    }

    @Test
    public void testShowSwitchingBetweenElementAndDomainObject() {
        tested.init();

        verify(formsCanvasSessionHandler).setRenderer(formRendererArgumentCaptor.capture());
        final FormsCanvasSessionHandler.FormRenderer formRenderer = formRendererArgumentCaptor.getValue();

        final Command command = mock(Command.class);
        when(formsCanvasSessionHandler.getDiagram()).thenReturn(diagram);
        when(domainObject.getDomainObjectUUID()).thenReturn(DOMAIN_OBJECT_UUID);
        when(domainObject.getDomainObjectNameTranslationKey()).thenReturn(DOMAIN_OBJECT_TRANSLATION_KEY);

        formRenderer.render(GRAPH_UUID, node, command);
        verify(formsContainer, times(1)).render(eq(GRAPH_UUID), eq(ROOT_UUID), eq(nodeDefObject), any(), any(), any());

        formRenderer.render(GRAPH_UUID, domainObject, command);
        verify(formsContainer, times(1)).render(eq(GRAPH_UUID), eq(DOMAIN_OBJECT_UUID), eq(domainObject), any(), any(), any());

        formRenderer.render(GRAPH_UUID, node, command);
        verify(formsContainer, times(2)).render(eq(GRAPH_UUID), eq(ROOT_UUID), eq(nodeDefObject), any(), any(), any());

        formRenderer.render(GRAPH_UUID, domainObject, command);
        verify(formsContainer, times(2)).render(eq(GRAPH_UUID), eq(DOMAIN_OBJECT_UUID), eq(domainObject), any(), any(), any());
    }

    @Test
    public void testAreElementsPositionSameNotNodes() {
        tested.init();

        verify(formsCanvasSessionHandler).setRenderer(formRendererArgumentCaptor.capture());
        final FormsCanvasSessionHandler.FormRenderer formRenderer = formRendererArgumentCaptor.getValue();

        final Command command = mock(Command.class);
        when(formsCanvasSessionHandler.getDiagram()).thenReturn(diagram);
        edge = new EdgeImpl("edge1");
        formRenderer.render(GRAPH_UUID, edge, command);

        assertEquals("Value is not the same ", tested.areLastPositionsForSameElementSame(edge), false);

        formRenderer.render(GRAPH_UUID, edge, command);
        assertEquals("Value is not the same ", tested.areLastPositionsForSameElementSame(node), false);
    }

    @Test
    public void testIsNode() {
        NodeImpl node = mock(NodeImpl.class);
        assertTrue(FormPropertiesWidget.isNode(node));

        GraphImpl notNode = mock(GraphImpl.class);
        assertFalse(FormPropertiesWidget.isNode(notNode));
    }

    @Test
    public void testIsFiltered() {
        NodeImpl nodeNullContent = mock(NodeImpl.class);
        when(nodeNullContent.getContent()).thenReturn(null);
        assertFalse(FormPropertiesWidget.isFiltered(nodeNullContent));

        Definition content = mock(Definition.class);

        NodeImpl nodeUnfiltered = mock(NodeImpl.class);
        Object unfilteredDefinition = mock(Object.class);
        when(nodeUnfiltered.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(unfilteredDefinition);
        assertFalse(FormPropertiesWidget.isFiltered(nodeUnfiltered));

        NodeImpl nodeFiltered = mock(NodeImpl.class);
        when(nodeFiltered.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(CUSTOM_DEFINITION);
        assertTrue(FormPropertiesWidget.isFiltered(nodeFiltered));
    }
}
