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

package com.ait.lienzo.client.core.shape.wires.handlers;

public final class MouseEvent {

    private final int x;
    private final int y;
    private final boolean isShiftKeyDown;
    private final boolean isAltKeyDown;
    private final boolean isCtrlKeyDown;

    public MouseEvent(int x,
                      int y,
                      boolean isShiftKeyDown,
                      boolean isAltKeyDown,
                      boolean isCtrlKeyDown) {
        this.x = x;
        this.y = y;
        this.isShiftKeyDown = isShiftKeyDown;
        this.isAltKeyDown = isAltKeyDown;
        this.isCtrlKeyDown = isCtrlKeyDown;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isShiftKeyDown() {
        return isShiftKeyDown;
    }

    public boolean isAltKeyDown() {
        return isAltKeyDown;
    }

    public boolean isCtrlKeyDown() {
        return isCtrlKeyDown;
    }
}
