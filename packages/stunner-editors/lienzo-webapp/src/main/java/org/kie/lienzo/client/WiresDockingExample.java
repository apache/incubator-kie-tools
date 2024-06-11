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

package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.IContainmentAcceptor;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.shared.core.types.ColorName;
import elemental2.dom.HTMLDivElement;

public class WiresDockingExample extends BaseExample implements Example {

    public WiresDockingExample(final String title) {
        super(title);
    }

    @Override
    public void init(final LienzoPanel panel, final HTMLDivElement topDiv) {
        super.init(panel, topDiv);
    }

    @Override
    public void run() {

//        getToolBarContainer().add(m_label);

        final WiresManager wires_manager = WiresManager.get(layer);

        wires_manager.setContainmentAcceptor(IContainmentAcceptor.ALL);

        wires_manager.setDockingAcceptor(new IDockingAcceptor() {
            @Override
            public boolean dockingAllowed(final WiresContainer parent, final WiresShape child) {
                return acceptDocking(parent, child);
            }

            @Override
            public boolean acceptDocking(final WiresContainer parent, final WiresShape child) {
                final String pd = getUserData(parent);
                final String cd = getUserData(child);
                return "parent".equals(pd) && "dock".equals(cd);
            }

            @Override
            public int getHotspotSize() {
                return IDockingAcceptor.HOTSPOT_SIZE;
            }

            private String getUserData(final WiresContainer shape) {
                return ((null != shape) && (null != shape.getContainer()) && (null != shape.getContainer().getUserData())) ? shape.getContainer().getUserData().toString() : null;
            }
        });

        final MultiPath parentMultiPath = new MultiPath().rect(0, 0, 400, 400).setStrokeColor("#000000").setFillColor(ColorName.WHITESMOKE);
        final WiresShape parentShape = new WiresShape(parentMultiPath);
        parentShape.getContainer().setUserData("parent");
        parentShape.setLocation(new Point2D(500, 200)).setDraggable(true);
        wires_manager.register(parentShape);
        wires_manager.getMagnetManager().createMagnets(parentShape);

        final MultiPath childMultiPath = new MultiPath().rect(0, 0, 100, 100).setStrokeColor(ColorName.RED).setFillColor(ColorName.RED);
        final WiresShape childShape = new WiresShape(childMultiPath);
        childShape.getContainer().setUserData("child");
        childShape.setLocation(new Point2D(50, 200)).setDraggable(true);
        wires_manager.register(childShape);

        wires_manager.getMagnetManager().createMagnets(childShape);

        final MultiPath dockMultiPath = new MultiPath().rect(0, 0, 100, 100).setStrokeColor(ColorName.BLUE).setFillColor(ColorName.BLUE);
        final WiresShape dockShape = new WiresShape(dockMultiPath);
        dockShape.getContainer().setUserData("dock");
        dockShape.setLocation(new Point2D(50, 400)).setDraggable(true);
        wires_manager.register(dockShape);
        wires_manager.getMagnetManager().createMagnets(dockShape);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
