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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvasView;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

@Dependent
public class WiresCanvasView extends LienzoCanvasView<WiresLayer> {

    private final WiresLayer layer;

    @Inject
    public WiresCanvasView(final WiresLayer layer) {
        this.layer = layer;
    }

    public void use(final WiresManager wiresManager) {
        layer.use(wiresManager);
    }

    @Override
    public LienzoCanvasView<WiresLayer> add(final ShapeView<?> shape) {
        if (WiresUtils.isWiresShape(shape)) {
            layer.add((WiresShape) shape);
        } else if (WiresUtils.isWiresConnector(shape)) {
            layer.add((WiresConnector) shape);
        } else {
            return super.add(shape);
        }
        return this;
    }

    public LienzoCanvasView addRoot(final ShapeView<?> shape) {
        if (WiresUtils.isWiresShape(shape)) {
            layer.add(((WiresShape) shape).getGroup());
        } else if (WiresUtils.isWiresConnector(shape)) {
            layer.add(((WiresConnector) shape).getGroup());
        } else {
            return super.add(shape);
        }
        return this;
    }

    @Override
    public LienzoCanvasView<WiresLayer> delete(final ShapeView<?> shape) {
        if (WiresUtils.isWiresShape(shape)) {
            layer.delete((WiresShape) shape);
        } else if (WiresUtils.isWiresConnector(shape)) {
            layer.delete((WiresConnector) shape);
        } else {
            return super.delete(shape);
        }
        return this;
    }

    public LienzoCanvasView deleteRoot(final ShapeView<?> shape) {
        if (WiresUtils.isWiresShape(shape)) {
            layer.delete(((WiresShape) shape).getGroup());
        } else if (WiresUtils.isWiresConnector(shape)) {
            layer.delete(((WiresConnector) shape).getGroup());
        } else {
            return super.delete(shape);
        }
        return this;
    }

    @Override
    public LienzoCanvasView addChild(final ShapeView<?> parent,
                                     final ShapeView<?> child) {
        final WiresContainer parentShape = (WiresContainer) parent;
        final WiresShape childShape = (WiresShape) child;
        layer.addChild(parentShape,
                       childShape);
        childShape.shapeMoved();
        return this;
    }

    @Override
    public LienzoCanvasView deleteChild(final ShapeView<?> parent,
                                        final ShapeView<?> child) {
        final WiresContainer parentShape = (WiresContainer) parent;
        final WiresShape childShape = (WiresShape) child;
        layer.deleteChild(parentShape,
                          childShape);
        childShape.shapeMoved();
        return this;
    }

    @Override
    public LienzoCanvasView dock(final ShapeView<?> parent,
                                 final ShapeView<?> child) {
        final WiresContainer parentShape = (WiresContainer) parent;
        final WiresShape childShape = (WiresShape) child;
        layer.dock(parentShape,
                   childShape);
        childShape.shapeMoved();
        return this;
    }

    @Override
    public LienzoCanvasView undock(final ShapeView<?> childParent,
                                   final ShapeView<?> child) {
        final WiresShape childShape = (WiresShape) child;
        layer.undock(childShape);
        childShape.shapeMoved();
        return this;
    }

    @Override
    public WiresLayer getLayer() {
        return layer;
    }
}
