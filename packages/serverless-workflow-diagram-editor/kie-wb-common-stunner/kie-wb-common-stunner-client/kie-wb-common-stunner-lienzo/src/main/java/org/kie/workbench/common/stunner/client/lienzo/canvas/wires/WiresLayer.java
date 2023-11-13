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


package org.kie.workbench.common.stunner.client.lienzo.canvas.wires;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingControl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;
import jakarta.enterprise.context.Dependent;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;

@Dependent
public class WiresLayer extends LienzoLayer {

    private static Logger LOGGER = Logger.getLogger(WiresLayer.class.getName());

    static final Direction[] MAGNET_CARDINALS = MagnetManager.FOUR_CARDINALS;

    private WiresManager wiresManager;

    public WiresLayer use(final WiresManager wiresManager) {
        this.wiresManager = wiresManager;
        return this;
    }

    public LienzoLayer add(final WiresShape wiresShape) {
        if (contains(wiresShape)) {
            LOGGER.log(Level.WARNING, "Cannot add a WiresShape into the WiresLayer twice!");
        } else {
            wiresManager.register(wiresShape);
            wiresManager.getMagnetManager().createMagnets(wiresShape,
                                                          MAGNET_CARDINALS);
            WiresUtils.assertShapeGroup(wiresShape.getGroup(),
                                        WiresCanvas.WIRES_CANVAS_GROUP_ID);
        }
        return this;
    }

    private boolean contains(final WiresShape wiresShape) {
        return null != wiresManager.getShape(wiresShape.uuid());
    }

    public LienzoLayer add(final WiresConnector wiresConnector) {
        wiresManager.register(wiresConnector);
        WiresUtils.assertShapeGroup(wiresConnector.getGroup(),
                                    WiresCanvas.WIRES_CANVAS_GROUP_ID);
        return this;
    }

    public LienzoLayer delete(final WiresShape wiresShape) {
        wiresManager.deregister(wiresShape);
        return this;
    }

    public LienzoLayer delete(final WiresConnector wiresConnector) {
        wiresManager.deregister(wiresConnector);
        return this;
    }

    public WiresLayer addChild(final WiresContainer parent,
                               final WiresShape child) {
        parent.add(child);
        return this;
    }

    public WiresLayer deleteChild(final WiresContainer parent,
                                  final WiresShape child) {
        parent.remove(child);
        return this;
    }

    public WiresLayer dock(final WiresContainer parent,
                           final WiresShape child) {
        final WiresDockingControl dockingControl = child.getControl().getDockingControl();
        dockingControl.dock(parent);
        final Point2D candidateLocation = dockingControl.getCandidateLocation();
        if (null != candidateLocation) {
            child.setLocation(candidateLocation);
        }
        return this;
    }

    public WiresLayer undock(final WiresShape child) {
        child.getControl().getDockingControl().undock();
        return this;
    }

    public WiresManager getWiresManager() {
        return wiresManager;
    }

    @Override
    public void destroy() {
        WiresManager.remove(wiresManager);
        wiresManager = null;
        super.destroy();
    }
}
