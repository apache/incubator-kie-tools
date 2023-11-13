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


package org.kie.workbench.common.stunner.svg.client.shape.impl;

import org.kie.workbench.common.stunner.client.lienzo.shape.impl.LienzoShape;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.LienzoShapeView;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.svg.client.shape.SVGShape;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;

public class SVGShapeImpl
        implements SVGShape<SVGShapeViewImpl> {

    private final SVGShapeViewImpl view;
    private final LienzoShape<LienzoShapeView> shape;

    @SuppressWarnings("unchecked")
    public SVGShapeImpl(final SVGShapeViewImpl view) {
        this(view,
             new LienzoShape<>(view,
                               view.getShapeStateHandler()));
    }

    @SuppressWarnings("unchecked")
    SVGShapeImpl(final SVGShapeViewImpl view,
                 final LienzoShape<LienzoShapeView> shape) {
        this.view = view;
        this.shape = shape;
    }

    @Override
    public void setUUID(final String uuid) {
        shape.setUUID(uuid);
    }

    @Override
    public String getUUID() {
        return shape.getUUID();
    }

    @Override
    public void beforeDraw() {
        shape.beforeDraw();
    }

    @Override
    public void afterDraw() {
        shape.afterDraw();
    }

    @Override
    public void applyState(final ShapeState shapeState) {
        shape.applyState(shapeState);
    }

    @Override
    public SVGShapeViewImpl getShapeView() {
        return view;
    }
}
