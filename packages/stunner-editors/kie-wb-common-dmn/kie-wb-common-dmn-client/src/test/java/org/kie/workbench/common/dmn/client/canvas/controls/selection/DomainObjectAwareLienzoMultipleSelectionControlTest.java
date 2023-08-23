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

package org.kie.workbench.common.dmn.client.canvas.controls.selection;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.ait.lienzo.client.core.event.OnEventHandlers;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.SelectionManager;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenu;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DomainObjectAwareLienzoMultipleSelectionControlTest {

    private static final String LAYER_UUID = "layer-uuid";

    private static final String DIAGRAM_UUID = "diagram-uuid";

    private static final String ELEMENT_UUID = "element-uuid";

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata diagramMetadata;

    @Mock
    private Index graphIndex;

    @Mock
    private WiresCanvas canvas;

    @Mock
    private com.ait.lienzo.client.core.shape.Layer lienzoLayer;

    @Mock
    private Viewport lienzoViewport;

    @Mock
    private OnEventHandlers lienzoOnEventHandlers;

    @Mock
    private Object definition;

    @Mock
    private Element element;

    @Mock
    private DomainObject domainObject;

    @Mock
    private SelectionManager selectionManager;

    @Mock
    private EventSourceMock<CanvasSelectionEvent> canvasSelectionEvent;

    @Mock
    private EventSourceMock<CanvasClearSelectionEvent> clearSelectionEvent;

    @Mock
    private DRDContextMenu drdContextMenu;

    @Mock
    private WiresCanvasView wiresCanvasView;

    @Mock
    private Widget canvasWidget;

    @Mock
    private HandlerRegistration handlerRegistration;

    private DomainObjectAwareLienzoMultipleSelectionControl control;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(lienzoLayer.uuid()).thenReturn(LAYER_UUID);
        when(lienzoLayer.getViewport()).thenReturn(lienzoViewport);
        when(lienzoViewport.getOnEventHandlers()).thenReturn(lienzoOnEventHandlers);

        final WiresManager wiresManager = spy(WiresManager.get(lienzoLayer));

        this.control = new DomainObjectAwareLienzoMultipleSelectionControl(canvasSelectionEvent,
                                                                           clearSelectionEvent,
                                                                           drdContextMenu);

        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
        when(canvas.getView()).thenReturn(wiresCanvasView);
        when(wiresCanvasView.asWidget()).thenReturn(canvasWidget);
        when(canvasWidget.addDomHandler(any(), any())).thenReturn(handlerRegistration);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(diagram.getMetadata()).thenReturn(diagramMetadata);
        when(diagramMetadata.getCanvasRootUUID()).thenReturn(DIAGRAM_UUID);
        when(graphIndex.get(ELEMENT_UUID)).thenReturn(element);
        when(canvas.getWiresManager()).thenReturn(wiresManager);
        when(element.getUUID()).thenReturn(ELEMENT_UUID);
        when(element.getContent()).thenReturn(new ViewImpl<>(definition,
                                                             Bounds.create(0, 0, 10, 10)));

        doReturn(selectionManager).when(wiresManager).getSelectionManager();

        control.init(canvasHandler);
        control.register(element);
    }

    @Test
    public void testSelectElement() {
        control.select(ELEMENT_UUID);

        assertElementSelected();
    }

    @Test
    public void testSelectElementWithEvent() {
        final CanvasSelectionEvent event = new CanvasSelectionEvent(canvasHandler, ELEMENT_UUID);
        control.handleCanvasElementSelectedEvent(event);

        assertElementSelected();
    }

    @Test
    public void testSelectDomainObjectWithEvent() {
        final DomainObjectSelectionEvent event = new DomainObjectSelectionEvent(canvasHandler, domainObject);
        control.handleDomainObjectSelectedEvent(event);

        assertDomainObjectSelected();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectNullDomainObjectWithEvent() {
        final DomainObjectSelectionEvent event = new DomainObjectSelectionEvent(canvasHandler, null);
        control.handleDomainObjectSelectedEvent(event);

        final Optional<Object> selectedItemDefinition = control.getSelectedItemDefinition();
        assertThat(selectedItemDefinition).isNotPresent();
    }

    @SuppressWarnings("unchecked")
    private void assertElementSelected() {
        final Optional<Object> selectedItemDefinition = control.getSelectedItemDefinition();
        assertThat(selectedItemDefinition).isPresent();
        assertThat(selectedItemDefinition.get()).isEqualTo(element);
    }

    @SuppressWarnings("unchecked")
    private void assertDomainObjectSelected() {
        final Optional<Object> selectedItemDefinition = control.getSelectedItemDefinition();
        assertThat(selectedItemDefinition).isPresent();
        assertThat(selectedItemDefinition.get()).isEqualTo(domainObject);
    }

    @Test
    public void testSelectDomainObjectThenElement() {
        final DomainObjectSelectionEvent domainObjectEvent = new DomainObjectSelectionEvent(canvasHandler, domainObject);
        control.handleDomainObjectSelectedEvent(domainObjectEvent);

        assertDomainObjectSelected();

        final CanvasSelectionEvent elementEvent = new CanvasSelectionEvent(canvasHandler, ELEMENT_UUID);
        control.handleCanvasElementSelectedEvent(elementEvent);

        assertElementSelected();
    }

    @Test
    public void testSelectDomainObjectThenClear() {
        final DomainObjectSelectionEvent domainObjectEvent = new DomainObjectSelectionEvent(canvasHandler, domainObject);
        control.handleDomainObjectSelectedEvent(domainObjectEvent);

        assertDomainObjectSelected();

        control.clear();

        assertThat(control.getSelectedItemDefinition()).isNotPresent();
    }

    @Test
    public void testSelectDomainObjectThenClearWithEvent() {
        final DomainObjectSelectionEvent domainObjectEvent = new DomainObjectSelectionEvent(canvasHandler, domainObject);
        control.handleDomainObjectSelectedEvent(domainObjectEvent);

        assertDomainObjectSelected();

        final CanvasClearSelectionEvent event = new CanvasClearSelectionEvent(canvasHandler);
        control.handleCanvasClearSelectionEvent(event);

        assertThat(control.getSelectedItemDefinition()).isNotPresent();
        verify(selectionManager).clearSelection();
    }

    @Test
    public void testSelectDomainObjectThenDestroy() {
        final DomainObjectSelectionEvent domainObjectEvent = new DomainObjectSelectionEvent(canvasHandler, domainObject);
        control.handleDomainObjectSelectedEvent(domainObjectEvent);

        assertDomainObjectSelected();

        control.destroy();

        assertThat(control.getSelectedItemDefinition()).isNotPresent();
    }

    @Test
    public void testIsClickedOnShapeWhenShapeIsClicked() {
        final DomainObjectAwareLienzoMultipleSelectionControl partiallyMockedControl = mock(DomainObjectAwareLienzoMultipleSelectionControl.class);
        final View view = mock(View.class);

        when(partiallyMockedControl.getSelectedNodesStream(canvasHandler)).thenReturn(Stream.of(element));
        when(element.getContent()).thenReturn(view);
        when(view.getBounds()).thenReturn(new Bounds(
                new Bound(10d, 5d),
                new Bound(30d, 20d)
        ));
        when(partiallyMockedControl.isClickedOnShape(canvasHandler, 25, 15)).thenCallRealMethod();

        assertThat(partiallyMockedControl.isClickedOnShape(canvasHandler, 25, 15)).isTrue();
    }

    @Test
    public void testIsClickedOnShapeWhenShapeIsNotClicked() {
        assertThat(control.isClickedOnShape(canvasHandler, 0, 0)).isFalse();
    }

    @Test
    public void testWhenGettingSelectedNodes() {
        final DomainObjectAwareLienzoMultipleSelectionControl partiallyMockedControl = mock(DomainObjectAwareLienzoMultipleSelectionControl.class);

        when(partiallyMockedControl.getSelectedNodesStream(canvasHandler)).thenReturn(Stream.of(element));
        when(partiallyMockedControl.getSelectedNodes(canvasHandler)).thenCallRealMethod();

        final List selectedNodes = partiallyMockedControl.getSelectedNodes(canvasHandler);
        assertThat(selectedNodes).isNotEmpty();
        assertThat(selectedNodes).hasSize(1);
    }
}
