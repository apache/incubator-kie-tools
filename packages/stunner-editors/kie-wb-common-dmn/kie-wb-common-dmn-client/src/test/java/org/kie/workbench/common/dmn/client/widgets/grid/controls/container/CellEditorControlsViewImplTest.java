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
package org.kie.workbench.common.dmn.client.widgets.grid.controls.container;

import java.util.Optional;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Body;
import org.jboss.errai.common.client.dom.CSSStyleDeclaration;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.PopupEditorControls;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsViewImpl.LEFT;
import static org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsViewImpl.PX;
import static org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsViewImpl.TOP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@RunWith(GwtMockitoTestRunner.class)
public class CellEditorControlsViewImplTest {

    @Mock
    private Document document;

    @Mock
    private Div cellEditorControls;

    @Mock
    private Div cellEditorControlsContainer;

    @Mock
    private Body body;

    @Mock
    private ElementWrapperWidget<?> elementWrapperWidget;

    @Captor
    private ArgumentCaptor<EventListener> mouseDownListenerCaptor;

    @Captor
    private ArgumentCaptor<EventListener> mouseWheelListenerCaptor;

    private CellEditorControlsViewImpl view;

    @Before
    public void setup() {
        when(document.getBody()).thenReturn(body);

        this.view = spy(new CellEditorControlsViewImpl(document,
                                                       cellEditorControls,
                                                       cellEditorControlsContainer));

        doReturn(elementWrapperWidget).when(view).getWidget();
        doNothing().when(view).addWidgetToRootPanel(any());
        doNothing().when(view).removeWidgetFromRootPanel(any());
    }

    @Test
    public void testPostConstruct() {
        view.setup();

        verify(view).addWidgetToRootPanel(elementWrapperWidget);
        verify(body).addEventListener(eq(BrowserEvents.MOUSEDOWN),
                                      mouseDownListenerCaptor.capture(),
                                      eq(false));
        verify(body).addEventListener(eq(BrowserEvents.MOUSEWHEEL),
                                      mouseWheelListenerCaptor.capture(),
                                      eq(false));
    }

    @Test
    public void testPreDestroy() {
        testPostConstruct();

        view.destroy();

        verify(view).removeWidgetFromRootPanel(elementWrapperWidget);
        verify(body).removeEventListener(eq(BrowserEvents.MOUSEDOWN),
                                         eq(mouseDownListenerCaptor.getValue()),
                                         eq(false));
        verify(body).removeEventListener(eq(BrowserEvents.MOUSEWHEEL),
                                         eq(mouseWheelListenerCaptor.getValue()),
                                         eq(false));
    }

    @Test
    public void testShow() {
        final PopupEditorControls editor = mock(PopupEditorControls.class);
        final int x = 10;
        final int y = 20;

        final HTMLElement element = mock(HTMLElement.class);
        doReturn(element).when(view).getElement();
        final CSSStyleDeclaration style = mock(CSSStyleDeclaration.class);
        when(element.getStyle()).thenReturn(style);

        view.show(editor, x, y);

        verify(style).setProperty(LEFT, x + PX);
        verify(style).setProperty(TOP, y + PX);
        verify(view).setOnClosedByKeyboardCallback(editor);
        verify(editor).show();
    }

    @Test
    public void testSetOnClosedByKeyboardCallback() {
        final PopupEditorControls editor = mock(PopupEditorControls.class, withSettings().extraInterfaces(CanBeClosedByKeyboard.class));

        view.setOnClosedByKeyboardCallback(editor);

        verify((CanBeClosedByKeyboard) editor).setOnClosedByKeyboardCallback(any());
    }

    @Test
    public void testRemoveOnClosedByKeyboardCallback() {
        final PopupEditorControls editor = mock(PopupEditorControls.class, withSettings().extraInterfaces(CanBeClosedByKeyboard.class));

        view.removeOnClosedByKeyboardCallback(editor);

        verify((CanBeClosedByKeyboard) editor).setOnClosedByKeyboardCallback(null);
    }

    @Test
    public void testFocusOnDMNContainer() {
        final elemental2.dom.Element dmnContainerElement = mock(elemental2.dom.Element.class);

        doReturn(dmnContainerElement).when(view).getDMNContainer();

        view.focusOnDMNContainer(null);

        verify(dmnContainerElement).focus();
    }

    @Test
    public void testHide() {
        final PopupEditorControls editor = mock(PopupEditorControls.class);
        final Optional<PopupEditorControls> activeEditor = Optional.of(editor);

        doReturn(activeEditor).when(view).getActiveEditor();

        view.hide();

        verify(editor).hide();
        verify(view).removeOnClosedByKeyboardCallback(editor);
        verify(view).setActiveEditor(Optional.empty());
    }
}
