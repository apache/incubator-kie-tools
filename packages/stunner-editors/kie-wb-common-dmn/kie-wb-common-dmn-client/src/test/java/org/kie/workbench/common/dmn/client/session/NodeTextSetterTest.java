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

package org.kie.workbench.common.dmn.client.session;

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.NamedElement;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NodeTextSetterTest {

    @Mock
    private TextPropertyProviderFactory textPropertyProviderFactory;

    @Mock
    private EventSourceMock<CanvasElementUpdatedEvent> canvasElementUpdatedEvent;

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Captor
    private ArgumentCaptor<CanvasElementUpdatedEvent> canvasElementUpdatedEventArgumentCaptor;

    private NodeTextSetter nodeTextSetter;

    @Before
    public void setup() {
        nodeTextSetter = spy(new NodeTextSetter(textPropertyProviderFactory,
                                                canvasElementUpdatedEvent,
                                                dmnGraphUtils));
    }

    @Test
    public void testSetText() {

        final Node node = mock(Node.class);
        final String oldName = "old name";
        final String newName = "new name";
        final DMNEditorSession dmnEditorSession = mock(DMNEditorSession.class);
        final Optional<DMNEditorSession> editorSession = Optional.of(dmnEditorSession);
        final TextPropertyProvider textPropertyProvider = mock(TextPropertyProvider.class);
        final CanvasCommandManager<AbstractCanvasHandler> commandManager = mock(CanvasCommandManager.class);
        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);
        final Optional<AbstractCanvasHandler> optionalCanvasHanlder = Optional.of(canvasHandler);

        when(dmnEditorSession.getCommandManager()).thenReturn(commandManager);
        when(textPropertyProviderFactory.getProvider(node)).thenReturn(textPropertyProvider);

        doReturn(oldName).when(nodeTextSetter).getName(node);
        doReturn(editorSession).when(nodeTextSetter).getCurrentSession();
        doReturn(optionalCanvasHanlder).when(nodeTextSetter).getCanvasHandler(dmnEditorSession);

        nodeTextSetter.setText(newName, node);

        verify(textPropertyProvider).setText(canvasHandler,
                                             commandManager,
                                             node,
                                             newName);

        verify(nodeTextSetter).fireCanvasElementUpdated(canvasHandler, node);
    }

    @Test
    public void testFireCanvasElementUpdated() {

        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);
        final Node node = mock(Node.class);

        nodeTextSetter.fireCanvasElementUpdated(canvasHandler, node);

        verify(canvasElementUpdatedEvent).fire(canvasElementUpdatedEventArgumentCaptor.capture());

        final CanvasElementUpdatedEvent arg = canvasElementUpdatedEventArgumentCaptor.getValue();

        assertEquals(canvasHandler, arg.getCanvasHandler());
        assertEquals(node, arg.getElement());
    }

    @Test
    public void testGetName() {

        final NamedElement named = mock(NamedElement.class);
        final Node node = mock(Node.class);
        final Name name = mock(Name.class);
        final String nodeName = "node name";

        when(name.getValue()).thenReturn(nodeName);
        when(named.getName()).thenReturn(name);
        doReturn(Optional.of(named)).when(nodeTextSetter).getNamedElement(node);

        final String actualName = nodeTextSetter.getName(node);

        assertEquals(nodeName, actualName);
    }

    @Test
    public void testGetNameWhenNamedElementIsNotPresent() {

        final Node node = mock(Node.class);
        final Name name = mock(Name.class);
        final String nodeName = "node name";

        when(name.getValue()).thenReturn(nodeName);

        doReturn(Optional.empty()).when(nodeTextSetter).getNamedElement(node);

        final String actualName = nodeTextSetter.getName(node);

        assertEquals("", actualName);
    }

    @Test
    public void testGetNamedElement() {

        final Node node = mock(Node.class);
        final View content = mock(View.class);
        final NamedElement namedElement = mock(NamedElement.class);

        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(namedElement);

        final Optional<NamedElement> actualNamed = nodeTextSetter.getNamedElement(node);

        assertTrue(actualNamed.isPresent());
        assertEquals(namedElement, actualNamed.get());
    }

    @Test
    public void testGetNamedElementWhenNamedIsNotPresent() {

        final Node node = mock(Node.class);
        final View content = mock(View.class);
        final Object namedElement = mock(Object.class);

        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(namedElement);

        final Optional<NamedElement> actualNamed = nodeTextSetter.getNamedElement(node);

        assertFalse(actualNamed.isPresent());
    }

    @Test
    public void testGetNamedElementWhenViewIsNotPresent() {

        final Node node = mock(Node.class);
        final Object content = mock(Object.class);

        when(node.getContent()).thenReturn(content);

        final Optional<NamedElement> actualNamed = nodeTextSetter.getNamedElement(node);

        assertFalse(actualNamed.isPresent());
    }
}