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

import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;

public class ViewBoxDefinitionImpl implements ViewDefinition.ViewBoxDefinition {

    private final double minX;
    private final double minY;
    private final double width;
    private final double height;

    public ViewBoxDefinitionImpl(final double minX,
                                 final double minY,
                                 final double width,
                                 final double height) {
        this.minX = minX;
        this.minY = minY;
        this.width = width;
        this.height = height;
    }

    @Override
    public double getMinX() {
        return minX;
    }

    @Override
    public double getMinY() {
        return minY;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }
}
