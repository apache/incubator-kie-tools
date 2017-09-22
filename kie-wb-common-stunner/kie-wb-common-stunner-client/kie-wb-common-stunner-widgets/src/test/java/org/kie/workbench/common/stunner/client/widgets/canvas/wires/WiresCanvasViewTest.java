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

package org.kie.workbench.common.stunner.client.widgets.canvas.wires;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.MultiPathDecorator;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresShapeView;

import static org.junit.Assert.assertEquals;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresCanvasViewTest {

    private WiresCanvasView canvas;

    private com.ait.lienzo.client.widget.LienzoPanel panel;

    private org.kie.workbench.common.stunner.core.client.canvas.Layer layer;

    @Before
    public void setup() {
        this.canvas = new WiresCanvasView();
        this.panel = new LienzoPanel(100,
                                     100);
        this.layer = new LienzoLayer();
        this.canvas.init();
        this.canvas.show(panel,
                         100,
                         100,
                         layer);
    }

    @Test
    public void addShapeWithWiresShape() {
        final WiresShapeView view = new WiresShapeView(new MultiPath());
        canvas.addShape(view);

        assertEquals(WiresCanvas.WIRES_CANVAS_GROUP_ID,
                     WiresUtils.getShapeGroup(view.getGroup()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void addShapeWithWiresConnector() {
        final WiresConnectorView view = new WiresConnectorView(new OrthogonalPolyLine(new Point2D(0,
                                                                                                  0)),
                                                               new MultiPathDecorator(new MultiPath()),
                                                               new MultiPathDecorator(new MultiPath()));
        canvas.addShape(view);

        assertEquals(WiresCanvas.WIRES_CANVAS_GROUP_ID,
                     WiresUtils.getShapeGroup(view.getGroup()));
    }
}
