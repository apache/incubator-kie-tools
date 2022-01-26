/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.components.monaco_editor;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.DOMRect;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLCollection;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.Node;
import elemental2.dom.NodeList;
import org.gwtproject.dom.client.NativeEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoStandaloneCodeEditor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MonacoEditorViewTest {

    @Mock
    private MonacoEditorPresenter presenter;

    @Mock
    private MonacoStandaloneCodeEditor editor;

    @Mock
    private HTMLDivElement rootContainer;

    @Mock
    private HTMLDivElement level1;

    @Mock
    private HTMLDivElement level2;

    @Mock
    private HTMLDivElement level3;

    @Mock
    private Node node;

    @Mock
    private HTMLSelectElement languageSelector;

    @Mock
    private HTMLDivElement monacoEditor;

    @Mock
    private DOMRect resizeRect;

    @Mock
    private HTMLDivElement loadingEditor;

    @Mock
    private CSSStyleDeclaration monacoStyle;

    @Mock
    private CSSStyleDeclaration loadingStyle;

    private MonacoEditorView tested;

    @Before
    public void setUp() {

        rootContainer.childNodes = mock(NodeList.class);
        monacoEditor.childNodes = mock(NodeList.class);
        monacoEditor.style = monacoStyle;
        loadingEditor.style = loadingStyle;

        tested = spy(new MonacoEditorView());
        tested.rootContainer = rootContainer;
        tested.languageSelector = languageSelector;
        tested.monacoEditor = monacoEditor;
        tested.loadingEditor = loadingEditor;
        tested.editor = editor;
        tested.init(presenter);
        //verify(languageSelector, times(1)).setTitle(anyString());
    }

    @Test
    public void testOnLanguageChanged() {
        languageSelector.value = "lang";
        //when(languageSelector.getValue()).thenReturn("lang");
        tested.onLanguageChanged(mock(Event.class));
        verify(presenter, times(1)).onLanguageChanged(eq("lang"));
    }

    @Test
    public void testSetLanguage() {
        //tested.setLanguage("lang1");
        //verify(languageSelector, times(1)).setValue(eq("lang1"));
    }

    @Test
    public void testSetLanguageReadOnly() {
       //tested.setLanguageReadOnly(true);
       // verify(languageSelector, times(1)).setDisabled(eq(true));
    }

    @Test
    public void testGetLanguage() {
        //when(languageSelector.getValue()).thenReturn("langValue");
        //assertEquals("langValue", tested.getLanguage());
    }

    @Test
    public void testSetValue() {
        tested.setValue("editorValue");
        verify(editor, times(1)).setValue(eq("editorValue"));
    }

    @Test
    public void testGetValue() {
        when(editor.getValue()).thenReturn("editorValue");
        assertEquals("editorValue", tested.getValue());
    }

    //@Test
    public void testLoadStandaloneEditor() {
        MonacoStandaloneCodeEditor editor = mock(MonacoStandaloneCodeEditor.class);
        tested.load(editor, 300, 100);
        ArgumentCaptor<MonacoStandaloneCodeEditor.CallbackFunction> captor =
                ArgumentCaptor.forClass(MonacoStandaloneCodeEditor.CallbackFunction.class);
        verify(editor, times(1)).onDidBlurEditorWidget(captor.capture());
        verify(editor, times(1)).layout(any());
        captor.getValue().call(mock(NativeEvent.class));
        verify(presenter, times(1)).onValueChanged();
    }

    @Test
    public void testLoadingStarts() {
        tested.loadingStarts();
        verify(monacoStyle, times(1)).setProperty(eq(MonacoEditorView.DISPLAY), eq(MonacoEditorView.NONE));
        verify(monacoStyle, never()).removeProperty(any());
        verify(loadingStyle, times(1)).removeProperty(eq(MonacoEditorView.DISPLAY));
        verify(loadingStyle, never()).setProperty(any(), any());
    }

    @Test
    public void testLoadingEnds() {
        tested.loadingEnds();
        verify(loadingStyle, times(1)).setProperty(eq(MonacoEditorView.DISPLAY), eq(MonacoEditorView.NONE));
        verify(loadingStyle, never()).removeProperty(any());
        verify(monacoStyle, times(1)).removeProperty(eq(MonacoEditorView.DISPLAY));
        verify(monacoStyle, never()).setProperty(any(), any());
    }

    @Test
    public void testDispose() {
        tested.dispose();
        verify(editor, times(1)).dispose();
        //verify(monacoEditor, times(1)).getChildNodes();
        assertNull(tested.editor);
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(editor, times(1)).dispose();
        //verify(monacoEditor, times(1)).getChildNodes();
        //verify(rootContainer, times(1)).getChildNodes();
        assertNull(tested.editor);
    }

    @Test
    public void testGetParentInDepth() {
        setElementChain();
        Element parentElementRecovered = tested.getParentInDepth(rootContainer, 3);
        assertEquals(level1, parentElementRecovered);
    }

    @Test
    public void testGetParentInDepthOutOfBounds() {
        setElementChain();
        Element parentElementRecovered = tested.getParentInDepth(rootContainer, 4);
        assertEquals(level1, parentElementRecovered);
    }

    @Test
    public void testGetParentInDepthNullElement() {
        setElementChain();
        Element parentElementRecovered = tested.getParentInDepth(null, 4);
        assertNull(parentElementRecovered);
    }

    //@Test
    public void testAttachListenerToPanelTitle() {
        setElementChain();
        when(level1.getElementsByClassName(anyString())).thenReturn(getNodeList());
        assertTrue(tested.resizeObserver == null);
        tested.observeCommand = () -> System.out.println("Nothing");
        tested.attachListenerToPanelTitle();
        verify(node, times(1)).addEventListener(anyString(), anyObject(), anyBoolean());
        assertTrue(tested.resizeObserver != null);
    }

    //@Test(expected = NullPointerException.class)
    public void testAttachListenerToPanelTitleError() {
        setElementChain();
        when(level1.getElementsByClassName(anyString())).thenReturn(getNodeList());
        assertTrue(tested.resizeObserver == null);
        tested.attachListenerToPanelTitle();
        verify(node, times(1)).addEventListener(anyString(), anyObject(), anyBoolean());
        assertTrue(tested.resizeObserver != null);
    }

    @Test
    public void testOnResize() {
        when(monacoEditor.getBoundingClientRect()).thenReturn(resizeRect);
        resizeRect.width = 100.0;
        //when(resizeRect.getWidth()).thenReturn(100.0);
        tested.onResize();
        verify(presenter, times(1)).requestRefresh();
        resizeRect.width = 150.0;

        //when(resizeRect.getWidth()).thenReturn(150.0);
        tested.onResize();
        verify(presenter, times(2)).requestRefresh();
        // Should not call resize
        resizeRect.width = 148.0;
        //when(resizeRect.getWidth()).thenReturn(148.0);
        tested.onResize();
        verify(presenter, times(2)).requestRefresh();
    }

    private void setElementChain() {
        rootContainer.parentElement = level3;
        level3.parentElement = level2;
        level2.parentElement = level1;
    }

    private HTMLCollection<Element> getNodeList() {
        return new HTMLCollection() {
            @Override
            public Node item(int i) {
                return node;
            }

            @Override
            public int getLength() {
                return 1;
            }
        };
    }
}
