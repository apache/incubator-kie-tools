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


package org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.enterprise.inject.Instance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.shortcut.KeyboardShortcut;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mocks.MockInstanceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractCanvasShortcutsControlImplTest {

    @Mock
    private AbstractCanvas canvas;

    @Mock
    private AbstractCanvasHandler canvasHandlerMock;

    @Mock
    private KeyboardShortcut keyboardShortcutAction;

    private Instance<KeyboardShortcut> shortcuts;

    private AbstractCanvasShortcutsControlImpl canvasShortcutsControl;

    @Before
    public void setUp() throws Exception {

        shortcuts = new MockInstanceImpl(keyboardShortcutAction);

        canvasShortcutsControl = new AbstractCanvasShortcutsControlImpl(shortcuts) {
            {
                this.canvasHandler = canvasHandlerMock;
            }
        };
    }

    @Test
    public void testRegisterCauseCanvasFocus() {
        final EditorSession session = mock(EditorSession.class);
        final KeyboardControl keyboardControl = mock(KeyboardControl.class);
        doReturn(keyboardControl).when(session).getKeyboardControl();
        canvasShortcutsControl.bind(session);

        final Element element = mock(Element.class);
        canvasShortcutsControl.register(element);

        // Ensure never focus the canvas here, as it's probably not initialized yet, at least in IE11,
        // so the focus will fail at runtime. See RHPAM-1681.
        verify(canvas, never()).focus();
    }

    @Test
    public void testBind() {
        assertThat(canvasShortcutsControl.editorSession).isNull();

        final EditorSession session = mock(EditorSession.class);
        final KeyboardControl keyboardControl = mock(KeyboardControl.class);
        doReturn(keyboardControl).when(session).getKeyboardControl();

        canvasShortcutsControl.bind(session);

        assertThat(canvasShortcutsControl.editorSession).isEqualTo(session);
        verify(keyboardControl).addKeyShortcutCallback(eq(canvasShortcutsControl));
    }

    @Test
    public void testOnKeyShortcutNothingSelected() {
        mockSelectedElements();
        mockActionReactingOnKeyAndSelectedElement(KeyboardEvent.Key.E, mock(Element.class));

        canvasShortcutsControl.onKeyShortcut(KeyboardEvent.Key.E);

        verify(keyboardShortcutAction, never()).executeAction(any(), any());
    }

    @Test
    public void testOnKeyShortcutSelectedElement() {
        final Element selectedElement = mockSelectedElements("element").get(0);

        mockActionReactingOnKeyAndSelectedElement(KeyboardEvent.Key.E, selectedElement);

        canvasShortcutsControl.onKeyShortcut(KeyboardEvent.Key.E);

        verify(keyboardShortcutAction).executeAction(canvasHandlerMock, canvasShortcutsControl.selectedNodeId());
    }

    @Test
    public void testOnKeyShortcutWrongPressedKey() {
        final Element selectedElement = mockSelectedElements("element").get(0);

        mockActionReactingOnKeyAndSelectedElement(KeyboardEvent.Key.T, selectedElement);

        canvasShortcutsControl.onKeyShortcut(KeyboardEvent.Key.E);

        verify(keyboardShortcutAction, never()).executeAction(any(), any());
    }

    @Test
    public void testOnKeyShortcutWrongElement() {
        mockSelectedElements("element");

        mockActionReactingOnKeyAndSelectedElement(KeyboardEvent.Key.E, mock(Element.class));

        canvasShortcutsControl.onKeyShortcut(KeyboardEvent.Key.E);

        verify(keyboardShortcutAction, never()).executeAction(any(), any());
    }

    @Test
    public void testOnKeyShortcutSelectedElements() {
        mockSelectedElements("element-1", "element-2");
        mockActionReactingOnKeyAndSelectedElement(KeyboardEvent.Key.E, mock(Element.class));

        canvasShortcutsControl.onKeyShortcut(KeyboardEvent.Key.E);

        verify(keyboardShortcutAction, never()).executeAction(any(), any());
    }

    @Test
    public void testSelectedNodeNothingSelected() {
        mockSelectedElements();

        assertThat(canvasShortcutsControl.selectedNodeId()).isNull();
        assertThat(canvasShortcutsControl.selectedNodeElement()).isNull();
    }

    @Test
    public void testSelectedNodeTooManySelected() {
        mockSelectedElements("node-a", "node-b");

        assertThat(canvasShortcutsControl.selectedNodeId()).isNull();
        assertThat(canvasShortcutsControl.selectedNodeElement()).isNull();
    }

    @Test
    public void testSelectedNodeExactlyOneSelected() {
        final String selectedNodeId = "node-id";
        mockSelectedElements(selectedNodeId);

        final Index index = mock(Index.class);
        doReturn(index).when(canvasHandlerMock).getGraphIndex();
        final Element selectedElement = mock(Element.class);
        doReturn(selectedElement).when(index).get(selectedNodeId);

        assertThat(canvasShortcutsControl.selectedNodeId()).isEqualTo(selectedNodeId);
        assertThat(canvasShortcutsControl.selectedNodeElement()).isEqualTo(selectedElement);
    }

    private List<Element> mockSelectedElements(final String... selectedIds) {

        final Index index = mock(Index.class);
        doReturn(index).when(canvasHandlerMock).getGraphIndex();

        final EditorSession session = mock(EditorSession.class);
        final SelectionControl selectionControl = mock(SelectionControl.class);
        final KeyboardControl keyboardControl = mock(KeyboardControl.class);
        doReturn(selectionControl).when(session).getSelectionControl();
        doReturn(keyboardControl).when(session).getKeyboardControl();
        doReturn(Arrays.asList(selectedIds)).when(selectionControl).getSelectedItems();

        canvasShortcutsControl.bind(session);

        final List<Element> selectedElements = new ArrayList<>();
        for (final String id : selectedIds) {
            final Element element = mock(Element.class);
            doReturn(element).when(index).get(id);
            selectedElements.add(element);
        }

        return selectedElements;
    }

    private void mockActionReactingOnKeyAndSelectedElement(final KeyboardEvent.Key key,
                                                           final Element element) {
        when(keyboardShortcutAction.matchesPressedKeys(key)).thenReturn(true);
        when(keyboardShortcutAction.matchesSelectedElement(element)).thenReturn(true);
    }
}
