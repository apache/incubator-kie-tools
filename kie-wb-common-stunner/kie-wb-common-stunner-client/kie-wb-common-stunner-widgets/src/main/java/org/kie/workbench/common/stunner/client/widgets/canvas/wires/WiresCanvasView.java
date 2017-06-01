/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.client.widgets.canvas.wires;

import com.ait.lienzo.client.core.shape.wires.IConnectionAcceptor;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.kie.workbench.common.stunner.client.widgets.canvas.view.CanvasView;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

/**
 * This is the root Canvas view provided by Lienzo and wires
 */
public class WiresCanvasView extends CanvasView implements WiresCanvas.View {

    protected WiresManager wiresManager;

    public void init() {
        super.init();
        wiresManager = WiresManager.get(canvasLayer);
    }

    @Override
    public WiresCanvas.View addShape(final ShapeView<?> shapeView) {
        if (WiresUtils.isWiresShape(shapeView)) {
            WiresShape wiresShape = (WiresShape) shapeView;
            wiresManager.register(wiresShape);
            wiresManager.getMagnetManager().createMagnets(wiresShape);
            WiresUtils.assertShapeGroup(wiresShape.getGroup(),
                                        WiresCanvas.WIRES_CANVAS_GROUP_ID);
        } else if (WiresUtils.isWiresConnector(shapeView)) {
            WiresConnector wiresConnector = (WiresConnector) shapeView;
            final WiresConnectorControl connectorControl = wiresManager.register(wiresConnector);
            if (shapeView instanceof WiresConnectorView) {
                ((WiresConnectorView) shapeView).setControl(connectorControl);
            }
            WiresUtils.assertShapeGroup(wiresConnector.getGroup(),
                                        WiresCanvas.WIRES_CANVAS_GROUP_ID);
        } else {
            super.addShape(shapeView);
        }
        return this;
    }

    @Override
    public WiresCanvas.View removeShape(final ShapeView<?> shapeView) {
        if (WiresUtils.isWiresShape(shapeView)) {
            WiresShape wiresShape = (WiresShape) shapeView;
            wiresManager.deregister(wiresShape);
        } else if (WiresUtils.isWiresShape(shapeView)) {
            WiresConnector wiresConnector = (WiresConnector) shapeView;
            wiresManager.deregister(wiresConnector);
        } else {
            super.removeShape(shapeView);
        }
        return this;
    }

    @Override
    public WiresCanvas.View setConnectionAcceptor(final IConnectionAcceptor connectionAcceptor) {
        wiresManager.setConnectionAcceptor(connectionAcceptor);
        return this;
    }

    @Override
    public WiresCanvas.View setContainmentAcceptor(final IContainmentAcceptor containmentAcceptor) {
        wiresManager.setContainmentAcceptor(containmentAcceptor);
        wiresManager.getLayer().setContainmentAcceptor(containmentAcceptor);
        return this;
    }

    @Override
    public WiresCanvas.View setDockingAcceptor(final IDockingAcceptor dockingAcceptor) {
        wiresManager.setDockingAcceptor(dockingAcceptor);
        return this;
    }

    @Override
    public WiresManager getWiresManager() {
        return wiresManager;
    }
}
