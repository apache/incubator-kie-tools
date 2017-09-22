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

import javax.enterprise.event.Event;

import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.BindableProxyFactory;
import org.jboss.errai.databinding.client.BindableProxyProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.dynamic.service.shared.adf.DynamicFormModelGenerator;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.FormPropertiesOpened;
import org.kie.workbench.common.stunner.forms.context.PathAware;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormPropertiesWidgetTest {

    private static final String DIAGRAM_NAME = "diagram1";
    private static final String ROOT_UUID = "root1";

    @Mock
    DefinitionUtils definitionUtils;
    @Mock
    CanvasCommandFactory<AbstractCanvasHandler> commandFactory;
    @Mock
    DynamicFormRenderer formRenderer;
    @Mock
    Event<FormPropertiesOpened> propertiesOpenedEvent;
    @Mock
    ClientFullSession session;
    @Mock
    SelectionControl selectionControl;
    @Mock
    AbstractCanvasHandler canvasHandler;
    @Mock
    Diagram diagram;
    @Mock
    Metadata metadata;
    @Mock
    NodeImpl node;
    @Mock
    Definition nodeContent;
    @Mock
    Object nodeDefObject;
    @Mock
    Index graphIndex;
    @Mock
    DynamicFormModelGenerator modelGenerator;
    @Mock
    Path path;
    @Mock
    BindableProxyProvider proxyProvider;
    @Mock
    BindableProxy<Object> proxy;
    Object unmockedDef = new Object();

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
        when(node.getUUID()).thenReturn(ROOT_UUID);
        when(node.getContent()).thenReturn(nodeContent);
        when(nodeContent.getDefinition()).thenReturn(nodeDefObject);
        BindableProxyFactory.addBindableProxy(Object.class,
                                              proxyProvider);
        when(proxyProvider.getBindableProxy()).thenReturn((BindableProxy) proxy);
        when(proxyProvider.getBindableProxy(unmockedDef)).thenReturn((BindableProxy) proxy);
        when(proxy.deepUnwrap()).thenReturn(unmockedDef);
        this.tested = new FormPropertiesWidget(definitionUtils,
                                               commandFactory,
                                               formRenderer,
                                               modelGenerator,
                                               propertiesOpenedEvent);
    }

    @Test
    public void testShowEmpty() {
        when(canvasHandler.getDiagram()).thenReturn(null);
        final Command callback = mock(Command.class);
        tested
                .bind(session)
                .show(callback);
        // verify( formRenderer, times( 1 ) ).unBind(); - fix on class first.
        verify(formRenderer,
               times(0)).bind(anyObject());
    }

    /**
     * Figure out how to register a mock as proxy provider used by the FormPropertiesWidget to be able
     * to verify formRenderer mock behaviors.
     * For now, let's say that the runtime exception thrown by erray as there are not proxies available on test scope implies
     * that the logic to obtain selected items from the session is correct and at least, the code gets to the point where
     * it tries to introspect the model object ( selected ).
     */
    @Test(expected = java.lang.RuntimeException.class)
    public void testShowSelectedItem() {
        final Collection<String> selectedItems = new ArrayList<String>(3) {{
            add(ROOT_UUID);
            add("item2");
            add("item3");
        }};
        when(selectionControl.getSelectedItems()).thenReturn(selectedItems);
        final Command callback = mock(Command.class);
        tested
                .bind(session)
                .show(callback);
    }

    /**
     * Same as above.
     */
    @Test(expected = java.lang.RuntimeException.class)
    public void testShowCanvasRoot() {
        when(selectionControl.getSelectedItems()).thenReturn(null);
        when(metadata.getCanvasRootUUID()).thenReturn(ROOT_UUID);
        final Command callback = mock(Command.class);
        tested
                .bind(session)
                .show(callback);
    }

    @Test
    public void testFormContextIsPathAware() throws Exception {
        final ArgumentCaptor<FormRenderingContext> contextCaptor = ArgumentCaptor.forClass(FormRenderingContext.class);
        when(metadata.getPath()).thenReturn(path);
        when(nodeContent.getDefinition()).thenReturn(unmockedDef);

        tested
                .bind(session)
                .showByUUID(ROOT_UUID,
                            RenderMode.EDIT_MODE);

        verify(formRenderer).render(contextCaptor.capture());
        assertTrue("FormRenderingContext was not PathAware.",
                   contextCaptor.getValue() instanceof PathAware);
        assertSame(path,
                   ((PathAware) contextCaptor.getValue()).getPath());
    }
}
