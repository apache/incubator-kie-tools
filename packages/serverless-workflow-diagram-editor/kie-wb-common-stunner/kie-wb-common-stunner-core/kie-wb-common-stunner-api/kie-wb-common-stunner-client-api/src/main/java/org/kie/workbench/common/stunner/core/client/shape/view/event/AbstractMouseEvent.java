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


package org.kie.workbench.common.stunner.core.client.shape.view.event;

public abstract class AbstractMouseEvent extends AbstractViewEvent
        implements MouseEvent {

    private final double mouseX;
    private final double mouseY;
    private final double clientX;
    private final double clientY;

    public AbstractMouseEvent(final double mouseX,
                              final double mouseY,
                              final double clientX,
                              final double clientY) {
        this(mouseX,
             mouseY,
             clientX,
             clientY,
             false);
    }

    public AbstractMouseEvent(final double mouseX,
                              final double mouseY,
                              final double clientX,
                              final double clientY,
                              final boolean isShiftKeyDown) {
        setShiftKeyDown(isShiftKeyDown);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.clientX = clientX;
        this.clientY = clientY;
    }

    @Override
    public double getX() {
        return mouseX;
    }

    @Override
    public double getY() {
        return mouseY;
    }

    @Override
    public double getClientX() {
        return clientX;
    }

    @Override
    public double getClientY() {
        return clientY;
    }
}
