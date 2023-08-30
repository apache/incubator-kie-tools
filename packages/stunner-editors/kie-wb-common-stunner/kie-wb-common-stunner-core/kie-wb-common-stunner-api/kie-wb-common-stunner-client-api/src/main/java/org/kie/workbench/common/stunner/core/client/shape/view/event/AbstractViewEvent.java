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

public abstract class AbstractViewEvent implements ViewEvent {

    protected boolean isShiftKeyDown;
    protected boolean isAltKeyDown;
    protected boolean isMetaKeyDown;

    public AbstractViewEvent() {
        this(false,
             false,
             false);
    }

    public AbstractViewEvent(final boolean isShiftKeyDown,
                             final boolean isAltKeyDown,
                             final boolean isMetaKeyDown) {
        this.isShiftKeyDown = isShiftKeyDown;
        this.isAltKeyDown = isAltKeyDown;
        this.isMetaKeyDown = isMetaKeyDown;
    }

    public boolean isShiftKeyDown() {
        return isShiftKeyDown;
    }

    public void setShiftKeyDown(final boolean shiftKeyDown) {
        isShiftKeyDown = shiftKeyDown;
    }

    public boolean isAltKeyDown() {
        return isAltKeyDown;
    }

    public void setAltKeyDown(final boolean altKeyDown) {
        isAltKeyDown = altKeyDown;
    }

    public boolean isMetaKeyDown() {
        return isMetaKeyDown;
    }

    public void setMetaKeyDown(final boolean metaKeyDown) {
        isMetaKeyDown = metaKeyDown;
    }
}
