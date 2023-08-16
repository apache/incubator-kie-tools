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


package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMagnetsControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import com.ait.lienzo.client.core.types.Point2D;
import elemental2.dom.HTMLDivElement;
import org.mockito.Mock;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public abstract class AbstractWiresControlTest {

    protected static final double SHAPE_SIZE = 10;

    protected static final double PARENT_SIZE = 100;

    protected WiresShape shape;

    protected WiresShape parent;

    protected Layer layer;

    protected WiresManager manager;

    protected ColorMapBackedPicker.PickerOptions pickerOptions;

    @Mock
    protected WiresParentPickerControlImpl parentPicker;

    @Mock
    protected WiresColorMapIndex index;

    @Mock
    protected IDockingAcceptor dockingAcceptor;

    @Mock
    protected WiresShapeControl shapeControl;

    @Mock
    protected WiresMagnetsControl shapeMagnetsControl;

    @Mock
    protected WiresShapeControl parentControl;

    @Mock
    protected WiresMagnetsControl parentMagnetsControl;

    @Mock
    protected HTMLDivElement div;

    @Mock
    protected Viewport viewport;

    public void setUp() {
        layer = spy(new Layer());
        pickerOptions = new ColorMapBackedPicker.PickerOptions(false, 0);
        when(layer.getViewport()).thenReturn(viewport);
        when(viewport.getElement()).thenReturn(div);

        manager = WiresManager.get(layer);
        shape = spy(new WiresShape(new MultiPath().rect(0, 0, SHAPE_SIZE, SHAPE_SIZE)));
        shape.setWiresManager(manager);
        shape.setControl(shapeControl);
        parent = new WiresShape(new MultiPath().rect(0, 0, PARENT_SIZE, PARENT_SIZE));
        parent.setWiresManager(manager);
        parent.setControl(parentControl);
        manager.getMagnetManager().createMagnets(parent);
        manager.setDockingAcceptor(dockingAcceptor);

        when(shapeControl.getMagnetsControl()).thenReturn(shapeMagnetsControl);
        when(parentControl.getMagnetsControl()).thenReturn(parentMagnetsControl);

        when(parentPicker.getParent()).thenReturn(parent);
        when(dockingAcceptor.dockingAllowed(parent, shape)).thenReturn(true);
        when(parentPicker.getParentShapePart()).thenReturn(PickerPart.ShapePart.BORDER);
        when(parentPicker.getShape()).thenReturn(shape);
        when(parentPicker.getCurrentLocation()).thenReturn(parent.getLocation());
        when(parentPicker.onMove(anyDouble(), anyDouble())).thenReturn(true);
        when(parentPicker.getIndex()).thenReturn(index);
        when(parentPicker.getIndex()).thenReturn(index);

        shape.setLocation(new Point2D(0, 0));
        parent.setLocation(new Point2D(0, 0));
    }
}
