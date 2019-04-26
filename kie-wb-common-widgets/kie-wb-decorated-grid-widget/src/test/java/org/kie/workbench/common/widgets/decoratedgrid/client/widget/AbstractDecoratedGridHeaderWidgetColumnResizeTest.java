/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.widgets.decoratedgrid.client.widget;

import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SetInternalModelEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractDecoratedGridHeaderWidgetColumnResizeTest {

    @Mock
    private ResourcesProvider resources;

    @Mock
    private EventBus eventBus;

    @Mock
    private ImageResource imageResource;

    @Mock
    private Element body;

    @Mock
    private com.google.gwt.dom.client.Element bodyParent;

    @Mock
    private TableElement bodyParentAsTableElement;

    @Mock
    private Widget headerWidget;

    @Mock
    private RootPanel rootPanel;

    @Mock
    private HandlerRegistration mouseMoveHandlerRegistration;

    @Mock
    private HandlerRegistration mouseDownHandlerRegistration;

    @Mock
    private HandlerRegistration mouseUpHandlerRegistration;

    @Mock
    private HandlerRegistration mouseOutHandlerRegistration;

    @Mock
    private AbstractDecoratedGridHeaderWidget.ResizerInformation resizerInformation;

    @Captor
    private ArgumentCaptor<MouseMoveHandler> mouseMoveEventHandlerCaptor;

    @Captor
    private ArgumentCaptor<MouseDownHandler> mouseDownEventHandlerCaptor;

    @Captor
    private ArgumentCaptor<MouseUpHandler> mouseUpEventHandlerCaptor;

    @Captor
    private ArgumentCaptor<MouseOutHandler> mouseOutEventHandlerCaptor;

    private AbstractDecoratedGridHeaderWidget header;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(resources.collapseCellsIcon()).thenReturn(imageResource);
        when(resources.expandCellsIcon()).thenReturn(imageResource);
        when(body.getParentElement()).thenReturn(bodyParent);
        when(bodyParent.cast()).thenReturn(bodyParentAsTableElement);
        when(rootPanel.addDomHandler(any(MouseMoveHandler.class),
                                     eq(MouseMoveEvent.getType()))).thenReturn(mouseMoveHandlerRegistration);
        when(rootPanel.addDomHandler(any(MouseDownHandler.class),
                                     eq(MouseDownEvent.getType()))).thenReturn(mouseDownHandlerRegistration);
        when(rootPanel.addDomHandler(any(MouseUpHandler.class),
                                     eq(MouseUpEvent.getType()))).thenReturn(mouseUpHandlerRegistration);
        when(rootPanel.addDomHandler(any(MouseOutHandler.class),
                                     eq(MouseOutEvent.getType()))).thenReturn(mouseOutHandlerRegistration);

        this.header = spy(new AbstractDecoratedGridHeaderWidget(resources,
                                                                true,
                                                                eventBus) {

            @Override
            public void redraw() {
                //NOP for Column Resize tests
            }

            @Override
            public void setScrollPosition(final int position) {
                //NOP for Column Resize tests
            }

            @Override
            protected void resizeColumn(final DynamicColumn resizeColumn,
                                        final int resizeColumnWidth) {
                //NOP for Column Resize tests
            }

            @Override
            public void onSetInternalModel(final SetInternalModelEvent event) {
                //NOP for Column Resize tests
            }

            @Override
            void setResizerDimensions(final int position) {
                //NOP for Column Resize tests
            }

            @Override
            protected Widget getHeaderWidget() {
                return headerWidget;
            }

            @Override
            protected Element getBody() {
                return AbstractDecoratedGridHeaderWidgetColumnResizeTest.this.body;
            }

            @Override
            RootPanel rootPanel() {
                return AbstractDecoratedGridHeaderWidgetColumnResizeTest.this.rootPanel;
            }

            @Override
            protected ResizerInformation getResizerInformation(final int mx) {
                return AbstractDecoratedGridHeaderWidgetColumnResizeTest.this.resizerInformation;
            }
        });
    }

    @Test
    public void testOnLoad() {
        header.onLoad();

        verify(rootPanel).addDomHandler(any(MouseMoveHandler.class),
                                        eq(MouseMoveEvent.getType()));
        verify(rootPanel).addDomHandler(any(MouseDownHandler.class),
                                        eq(MouseDownEvent.getType()));
        verify(rootPanel).addDomHandler(any(MouseUpHandler.class),
                                        eq(MouseUpEvent.getType()));
        verify(rootPanel).addDomHandler(any(MouseOutHandler.class),
                                        eq(MouseOutEvent.getType()));
    }

    @Test
    public void testOnUnload() {
        header.onLoad();

        header.onUnload();

        verify(mouseMoveHandlerRegistration).removeHandler();
        verify(mouseDownHandlerRegistration).removeHandler();
        verify(mouseUpHandlerRegistration).removeHandler();
        verify(mouseOutHandlerRegistration).removeHandler();
    }

    @Test
    public void testMouseMoveHandler() {
        header.onLoad();

        verify(rootPanel).addDomHandler(mouseMoveEventHandlerCaptor.capture(),
                                        eq(MouseMoveEvent.getType()));

        final MouseMoveHandler mouseMoveHandler = mouseMoveEventHandlerCaptor.getValue();
        assertNotNull(mouseMoveHandler);

        final int cx = 32;
        final MouseMoveEvent event = mock(MouseMoveEvent.class);
        when(event.getClientX()).thenReturn(cx);

        mouseMoveHandler.onMouseMove(event);

        verify(header).getResizerInformation(eq(cx));
    }

    @Test
    public void testMouseDownHandler() {
        header.onLoad();

        //Prime a resize operation following a MouseMoveEvent to identify a column
        primeResizeOperation();

        //Test MouseDownHandler following a primed resize
        verify(rootPanel).addDomHandler(mouseDownEventHandlerCaptor.capture(),
                                        eq(MouseDownEvent.getType()));

        final MouseDownHandler mouseDownHandler = mouseDownEventHandlerCaptor.getValue();
        assertNotNull(mouseDownHandler);

        final int x = 32;
        final MouseDownEvent event = mock(MouseDownEvent.class);
        when(resizerInformation.isResizePrimed()).thenReturn(true);
        when(event.getX()).thenReturn(x);

        mouseDownHandler.onMouseDown(event);

        verify(resizerInformation).setResizing(eq(true));
        verify(header).setResizerDimensions(eq(x));
        verify(event).preventDefault();
    }

    @Test
    public void testMouseUpHandler() {
        header.onLoad();

        //Mock a resize operation following a MouseMoveEvent and MouseDownEvent
        when(resizerInformation.isResizing()).thenReturn(true);
        primeResizeOperation();

        //Test MouseUpHandler following a resize operation
        verify(rootPanel).addDomHandler(mouseUpEventHandlerCaptor.capture(),
                                        eq(MouseUpEvent.getType()));

        final MouseUpHandler mouseUpHandler = mouseUpEventHandlerCaptor.getValue();
        assertNotNull(mouseUpHandler);

        final MouseUpEvent event = mock(MouseUpEvent.class);
        mouseUpHandler.onMouseUp(event);

        verify(resizerInformation).setResizing(eq(false));
        verify(resizerInformation).setResizePrimed(eq(false));
        verify(event).preventDefault();
    }

    @Test
    public void testMouseOutHandler() {
        header.onLoad();

        //Mock a resize operation following a MouseMoveEvent and MouseDownEvent
        when(resizerInformation.isResizing()).thenReturn(true);
        primeResizeOperation();

        //Test MouseOutHandler following a resize operation
        verify(rootPanel).addDomHandler(mouseOutEventHandlerCaptor.capture(),
                                        eq(MouseOutEvent.getType()));

        final MouseOutHandler mouseOutHandler = mouseOutEventHandlerCaptor.getValue();
        assertNotNull(mouseOutHandler);

        final MouseOutEvent event = mock(MouseOutEvent.class);
        mouseOutHandler.onMouseOut(event);

        verify(resizerInformation).setResizing(eq(false));
        verify(resizerInformation).setResizePrimed(eq(false));
        verify(event).preventDefault();
    }

    private void primeResizeOperation() {
        verify(rootPanel).addDomHandler(mouseMoveEventHandlerCaptor.capture(),
                                        eq(MouseMoveEvent.getType()));
        final MouseMoveHandler mouseMoveHandler = mouseMoveEventHandlerCaptor.getValue();
        mouseMoveHandler.onMouseMove(mock(MouseMoveEvent.class));
    }
}
