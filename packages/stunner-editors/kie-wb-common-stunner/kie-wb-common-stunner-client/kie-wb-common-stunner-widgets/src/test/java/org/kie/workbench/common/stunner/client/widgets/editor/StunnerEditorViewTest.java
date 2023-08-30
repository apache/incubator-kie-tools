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

package org.kie.workbench.common.stunner.client.widgets.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.uberfire.client.workbench.widgets.ResizeFlowPanel;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(GwtMockitoTestRunner.class)
public class StunnerEditorViewTest {

    private Element element;

    private Element parentElement;

    private Style parentElementStyle;

    private ResizeFlowPanel editorPanel;

    private StunnerEditorView tested;

    @Before
    public void setup() {
        this.element = GWT.create(Element.class);
        this.parentElement = GWT.create(Element.class);
        this.parentElementStyle = GWT.create(Style.class);
        this.editorPanel = GWT.create(ResizeFlowPanel.class);
        this.tested = Mockito.spy(new StunnerEditorView(editorPanel));
        when(tested.getElement()).thenReturn(element);
        when(element.getParentElement()).thenReturn(parentElement);
        when(parentElement.getStyle()).thenReturn(parentElementStyle);
    }

    @Test
    public void testSetWidget() {
        final IsWidget editor = mock(IsWidget.class);
        tested.setWidget(editor);

        verify(editorPanel).clear();
        verify(editorPanel).add(eq(editor));
    }

    @Test
    public void testOnResize() {
        tested.onResize();
        verify(editorPanel).onResize();
    }

    @Test
    public void testOnAttach() {
        testOnAttach(true);
        testOnAttach(false);
    }

    public void testOnAttach(boolean parentExists) {
        tested = spy(new StunnerEditorView(editorPanel));
        final Element element = mock(Element.class);
        final Element parentElement = mock(Element.class);
        final Style style = mock(Style.class);

        when(tested.getElement()).thenReturn(element);
        when(parentElement.getStyle()).thenReturn(style);
        when(element.getStyle()).thenReturn(style);
        when(element.getParentElement()).thenReturn(parentExists ? parentElement : null);

        tested.onAttach();
        verify(tested).onAttach();
        verify(style, parentExists ? times(1) : never()).setHeight(100, Style.Unit.PCT);
        verify(style, parentExists ? times(1) : never()).setWidth(100, Style.Unit.PCT);
        verify(style, parentExists ? times(1) : never()).setDisplay(Style.Display.TABLE);
    }
}
