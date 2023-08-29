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


package org.kie.workbench.common.stunner.svg.gen.model.impl;

import org.kie.workbench.common.stunner.svg.gen.model.LayoutDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.PrimitiveDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.TransformDefinition;

public abstract class AbstractPrimitiveDefinition<V> implements PrimitiveDefinition<V> {

    private final String id;
    private double x;
    private double y;
    private double alpha;
    private boolean scalable;
    private boolean listening;
    private boolean mainShape;
    private TransformDefinition transformDefinition;
    private LayoutDefinition layoutDefinition;

    protected AbstractPrimitiveDefinition(final String id) {
        this(id,
             1,
             false,
             false,
             false);
    }

    protected AbstractPrimitiveDefinition(final String id,
                                          final double alpha,
                                          final boolean scalable,
                                          final boolean listening,
                                          final boolean mainShape) {
        this.id = id;
        this.alpha = alpha;
        this.listening = listening;
        this.scalable = scalable;
        this.mainShape = mainShape;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public double getX() {
        return x;
    }

    public void setX(final double x) {
        this.x = x;
    }

    @Override
    public double getY() {
        return y;
    }

    public void setY(final double y) {
        this.y = y;
    }

    @Override
    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(final double alpha) {
        this.alpha = alpha;
    }

    @Override
    public boolean isMain() {
        return mainShape;
    }

    public void setMainShape(final boolean mainShape) {
        this.mainShape = mainShape;
    }

    @Override
    public boolean isScalable() {
        return scalable;
    }

    public void setScalable(final boolean scalable) {
        this.scalable = scalable;
    }

    @Override
    public boolean isListening() {
        return listening;
    }

    public void setListening(final boolean listening) {
        this.listening = listening;
    }

    @Override
    public LayoutDefinition getLayoutDefinition() {
        return layoutDefinition;
    }

    public void setLayoutDefinition(final LayoutDefinition layoutDefinition) {
        this.layoutDefinition = layoutDefinition;
    }

    @Override
    public TransformDefinition getTransformDefinition() {
        return transformDefinition;
    }

    public void setTransformDefinition(final TransformDefinition transformDefinition) {
        this.transformDefinition = transformDefinition;
    }
}
