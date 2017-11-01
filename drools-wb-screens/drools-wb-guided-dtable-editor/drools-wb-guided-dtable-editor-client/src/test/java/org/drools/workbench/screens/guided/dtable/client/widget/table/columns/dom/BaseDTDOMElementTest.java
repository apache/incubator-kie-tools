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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BaseDTDOMElementTest {

    @Mock
    private TextBox textBox;

    @Mock
    private GridLayer gridLayer;

    @Mock
    private GridWidget gridWidget;

    @GwtMock
    @SuppressWarnings("unused")
    private SimplePanel widgetContainer;

    @Mock
    private Element widgetContainerElement;

    private BaseDTDOMElement<String, TextBox> element;

    @Before
    public void setup() {
        when(widgetContainer.getElement()).thenReturn(widgetContainerElement);
        when(widgetContainerElement.getStyle()).thenReturn(mock(Style.class));
        element = new BaseDTDOMElementMock(textBox,
                                           gridLayer,
                                           gridWidget);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void setupDelegatingMouseDownHandlerDoesNotAttachHandler() {
        element.setupDelegatingMouseDownHandler();

        verify(widgetContainer,
               never()).addDomHandler(any(EventHandler.class),
                                      any(DomEvent.Type.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void setupDelegatingMouseMoveHandlerDoesNotAttachHandler() {
        element.setupDelegatingMouseMoveHandler();

        verify(widgetContainer,
               never()).addDomHandler(any(EventHandler.class),
                                      any(DomEvent.Type.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void setupDelegatingMouseUpHandlerDoesNotAttachHandler() {
        element.setupDelegatingMouseUpHandler();

        verify(widgetContainer,
               never()).addDomHandler(any(EventHandler.class),
                                      any(DomEvent.Type.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void setupDelegatingClickHandlerDoesNotAttachHandler() {
        element.setupDelegatingClickHandler();

        verify(widgetContainer,
               never()).addDomHandler(any(EventHandler.class),
                                      any(DomEvent.Type.class));
    }

    private class BaseDTDOMElementMock extends BaseDTDOMElement<String, TextBox> {

        public BaseDTDOMElementMock(final TextBox widget,
                                    final GridLayer gridLayer,
                                    final GridWidget gridWidget) {
            super(widget,
                  gridLayer,
                  gridWidget);
        }

        @Override
        public void initialise(final GridBodyCellRenderContext context) {
            //Nothing to test in this implementation
        }

        @Override
        public void flush(final String value) {
            //Nothing to test in this implementation
        }
    }
}
