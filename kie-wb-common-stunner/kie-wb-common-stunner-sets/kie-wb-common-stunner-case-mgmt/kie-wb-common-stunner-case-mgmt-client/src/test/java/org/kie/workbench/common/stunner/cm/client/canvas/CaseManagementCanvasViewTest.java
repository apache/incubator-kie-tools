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

package org.kie.workbench.common.stunner.cm.client.canvas;

import java.util.Optional;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresLayer;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasSettings;
import org.kie.workbench.common.stunner.shapes.client.view.AbstractConnectorView;
import org.kie.workbench.common.stunner.shapes.client.view.PolylineConnectorView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementCanvasViewTest {

    @Mock
    private WiresLayer wiresLayer;

    private CaseManagementCanvasView view;
    private Layer layer;
    private WiresManager wiresManager;

    @Before
    public void setup() {
        this.layer = new Layer();
        this.wiresManager = WiresManager.get(layer);
        when(wiresLayer.getWiresManager()).thenReturn(wiresManager);
        this.view = new CaseManagementCanvasView(wiresLayer);
        this.view.init();
    }

    @Test
    public void addWiresShape() {
        final CaseManagementShapeView shape = new CaseManagementShapeView("mockCaseMgmtShapeView",
                                                                          new SVGPrimitiveShape(new Rectangle(0d, 0d)),
                                                                          0d,
                                                                          0d,
                                                                          false);
        final String uuid = shape.uuid();
        shape.setUUID(uuid);

        view.add(shape);

        final WiresShape registeredShape = wiresManager.getShape(uuid);
        assertNotNull(registeredShape);
        assertEquals(shape,
                     registeredShape);
    }

    @Test
    public void addWiresConnector() {
        final AbstractConnectorView connector = new PolylineConnectorView(0.0,
                                                                          0.0);
        final String uuid = connector.uuid();
        connector.setUUID(uuid);

        view.add(connector);

        final WiresShape registeredConnector = wiresManager.getShape(uuid);
        assertNull(registeredConnector);
    }

    @Test
    public void addChildShape() {
        final CaseManagementShapeView parent = new CaseManagementShapeView("mockCaseMgmtShapeViewParent",
                                                                           new SVGPrimitiveShape(new Rectangle(0d, 0d)),
                                                                           0d,
                                                                           0d,
                                                                           false);
        final CaseManagementShapeView child = new CaseManagementShapeView("mockCaseMgmtShapeViewChild",
                                                                          new SVGPrimitiveShape(new Rectangle(0d, 0d)),
                                                                          0d,
                                                                          0d,
                                                                          false);

        view.addChildShape(parent,
                           child,
                           0);

        assertEquals(1,
                     parent.getChildShapes().size());
        assertEquals(child,
                     parent.getChildShapes().get(0));
    }

    @Test
    public void testGetPanelBounds() throws Exception {
        final Bounds bounds = Bounds.build(1.0d, 2.0d, 3.0d, 4.0d);

        final LienzoBoundsPanel boundsPanel = mock(LienzoBoundsPanel.class);
        when(boundsPanel.getBounds()).thenReturn(bounds);

        final Widget widget = mock(Widget.class);
        final Element element = mock(Element.class);
        final Style style = mock(Style.class);
        when(widget.getElement()).thenReturn(element);
        when(element.getStyle()).thenReturn(style);
        final Layer toplayer = mock(Layer.class);
        when(wiresLayer.getTopLayer()).thenReturn(toplayer);

        final LienzoPanel lienzoPanel = mock(LienzoPanel.class);
        when(lienzoPanel.getView()).thenReturn(boundsPanel);
        when(lienzoPanel.asWidget()).thenReturn(widget);

        view.initialize(lienzoPanel, new CanvasSettings(false));

        final Optional<Bounds> result = view.getPanelBounds();

        assertTrue(result.isPresent());
        assertEquals(bounds.getHeight(), result.get().getHeight(), 0.00001);
        assertEquals(bounds, result.get());
    }
}
