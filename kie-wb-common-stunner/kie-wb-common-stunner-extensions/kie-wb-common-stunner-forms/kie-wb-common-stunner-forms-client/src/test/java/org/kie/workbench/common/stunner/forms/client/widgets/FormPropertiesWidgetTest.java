/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import javax.enterprise.event.Event;

import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.BindableProxyFactory;
import org.jboss.errai.databinding.client.BindableProxyProvider;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.FormPropertiesOpened;
import org.kie.workbench.common.stunner.forms.client.widgets.container.FormsContainer;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormPropertiesWidgetTest {

    private static final String GRAPH_UUID = "graph1";
    private static final String DIAGRAM_NAME = "diagram1";
    private static final String ROOT_UUID = "root1";
    private static final String DOMAIN_OBJECT_UUID = "domainObject1";
    private static final String DOMAIN_OBJECT_TRANSLATION_KEY = "domainObjectTranslationKey";

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
    private Definition nodeContent;
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
        when(nodeContent.getDefinition()).thenReturn(nodeDefObject);
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
                                               translationService) {
            @Override
            protected void log(final Level level, final String message) {
                //Logging not supported in Unit Test
            }
        };

        doAnswer((i) -> i.getArguments()[0]).when(translationService).getTranslation(anyString());
    }

    @Test
    public void testShowEmpty() {
        when(canvasHandler.getDiagram()).thenReturn(null);
        final Command callback = mock(Command.class);

        tested.bind(session).show(callback);
        verify(formsCanvasSessionHandler).bind(session);
        verify(formsCanvasSessionHandler).show(callback);

        verify(formsContainer, never()).render(anyString(), any(), any(), any(), any(), any());
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
                                      any(Path.class),
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
        verify(command, never()).execute();
    }
}
