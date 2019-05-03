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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox;

import java.util.Collections;
import java.util.function.Consumer;

import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.keyboard.KeyDownHandlerCommon;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager.PrioritizedCommand;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ListBoxSingletonDOMElementFactoryTest {

    @Mock
    private ListBox listBox;

    @Mock
    private Element listBoxElement;

    @Mock
    private AbsolutePanel domElementContainer;

    @Mock
    private GridLienzoPanel gridLienzoPanel;

    @Mock
    private GridLayer gridLayer;

    @Mock
    private GuidedDecisionTableView gridWidget;

    private ListBoxSingletonDOMElementFactory<String, ListBox> factory;

    @Before
    public void setup() {
        when(listBox.getElement()).thenReturn(listBoxElement);
        when(listBoxElement.getStyle()).thenReturn(mock(Style.class));
        when(domElementContainer.iterator()).thenReturn(Collections.<Widget>emptyList().iterator());
        when(gridLayer.getDomElementContainer()).thenReturn(domElementContainer);
        when(gridWidget.getModel()).thenReturn(new BaseGridData());

        doAnswer((i) -> {
            final PrioritizedCommand command = (PrioritizedCommand) i.getArguments()[0];
            command.execute();
            return null;
        }).when(gridLayer).batch(any(PrioritizedCommand.class));

        factory = spy(new ListBoxSingletonDOMElementFactoryMock(gridLienzoPanel,
                                                                gridLayer,
                                                                gridWidget));
    }

    @Test
    public void checkDOMElementCreation() {
        factory.createDomElement(gridLayer,
                                 gridWidget);

        verify(factory).createDomElementInternal(listBox, gridLayer, gridWidget);
    }

    @Test
    public void checkDOMElementInternalCreation() {
        factory.createDomElementInternal(listBox,
                                         gridLayer,
                                         gridWidget);

        verify(listBox).isMultipleSelect();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkDOMElementCreationBlurHandler() {
        final GridBodyCellRenderContext context = mock(GridBodyCellRenderContext.class);
        final Consumer<ListBoxDOMElement<String, ListBox>> onCreation = mock(Consumer.class);
        final Consumer<ListBoxDOMElement<String, ListBox>> onDisplay = mock(Consumer.class);
        when(context.getTransform()).thenReturn(mock(Transform.class));

        factory.attachDomElement(context,
                                 onCreation,
                                 onDisplay);

        final ArgumentCaptor<EventHandler> handlerCaptor = ArgumentCaptor.forClass(EventHandler.class);

        verify(listBox, times(4)).addDomHandler(handlerCaptor.capture(), any(DomEvent.Type.class));

        // KeyDownHandlerCommon
        Assertions.assertThat(handlerCaptor.getAllValues().get(0)).isInstanceOf(KeyDownHandlerCommon.class);

        // KeyDownHandler - stopPropagation
        final KeyDownEvent keyDownEventMock = mock(KeyDownEvent.class);
        Assertions.assertThat(handlerCaptor.getAllValues().get(1)).isInstanceOf(KeyDownHandler.class);
        ((KeyDownHandler) handlerCaptor.getAllValues().get(1)).onKeyDown(keyDownEventMock);
        verify(keyDownEventMock).stopPropagation();

        // MouseDownHandler - stopPropagation
        final MouseDownEvent mouseDownEventMock = mock(MouseDownEvent.class);
        Assertions.assertThat(handlerCaptor.getAllValues().get(2)).isInstanceOf(MouseDownHandler.class);
        ((MouseDownHandler) handlerCaptor.getAllValues().get(2)).onMouseDown(mouseDownEventMock);
        verify(mouseDownEventMock).stopPropagation();

        // BlurHandler
        final BlurEvent blurEventMock = mock(BlurEvent.class);
        Assertions.assertThat(handlerCaptor.getAllValues().get(3)).isInstanceOf(BlurHandler.class);
        ((BlurHandler) handlerCaptor.getAllValues().get(3)).onBlur(blurEventMock);
        verify(factory).flush();
        verify(gridLayer).batch();
        verify(gridLienzoPanel).setFocus(true);
    }

    private class ListBoxSingletonDOMElementFactoryMock extends ListBoxSingletonDOMElementFactory<String, ListBox> {

        public ListBoxSingletonDOMElementFactoryMock(final GridLienzoPanel gridPanel,
                                                     final GridLayer gridLayer,
                                                     final GuidedDecisionTableView gridWidget) {
            super(gridPanel,
                  gridLayer,
                  gridWidget);
        }

        @Override
        public String convert(final String value) {
            return "";
        }

        @Override
        public ListBox createWidget() {
            return listBox;
        }
    }
}
