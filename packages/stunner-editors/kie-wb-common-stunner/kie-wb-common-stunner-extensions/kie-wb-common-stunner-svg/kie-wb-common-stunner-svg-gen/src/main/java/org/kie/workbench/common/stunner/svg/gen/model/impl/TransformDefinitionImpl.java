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

import org.kie.workbench.common.stunner.svg.gen.model.TransformDefinition;

public class TransformDefinitionImpl implements TransformDefinition {

    private double scaleX;
    private double scaleY;
    private double translateX;
    private double translateY;

    public TransformDefinitionImpl() {
        this(1,
             1,
             0,
             0);
    }

    public TransformDefinitionImpl(final double scaleX,
                                   final double scaleY,
                                   final double translateX,
                                   final double translateY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.translateX = translateX;
        this.translateY = translateY;
    }

    @Override
    public double getScaleX() {
        return scaleX;
    }

    @Override
    public double getScaleY() {
        return scaleY;
    }

    @Override
    public double getTranslateX() {
        return translateX;
    }

    @Override
    public double getTranslateY() {
        return translateY;
    }

    public void setScaleX(final double scaleX) {
        this.scaleX = scaleX;
    }

    public void setScaleY(final double scaleY) {
        this.scaleY = scaleY;
    }

    public void setTranslateX(final double translateX) {
        this.translateX = translateX;
    }

    public void setTranslateY(final double translateY) {
        this.translateY = translateY;
    }
}
