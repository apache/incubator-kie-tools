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


package org.kie.workbench.common.stunner.client.lienzo.components.views;

import elemental2.dom.EventListener;
import elemental2.dom.HTMLDivElement;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.uberfire.mvp.Command;

public class LienzoPanelFocusHandler {

    EventListener mouseOverListener;
    EventListener mouseLeaveListener;

    HTMLDivElement panel;
    static final String ON_MOUSE_OVER = "mouseover";
    static final String ON_MOUSE_LEAVE = "mouseleave";

    public LienzoPanelFocusHandler listen(final LienzoPanel panel,
                                          final Command onFocus,
                                          final Command onLostFocus) {
        clear();

        this.panel = panel.getView().getElement();
        this.mouseOverListener = mouseOverEvent -> onFocus.execute();
        this.mouseLeaveListener = mouseLeaveEvent -> onLostFocus.execute();
        this.panel.addEventListener(ON_MOUSE_OVER, mouseOverListener);
        this.panel.addEventListener(ON_MOUSE_LEAVE, mouseLeaveListener);

        return this;
    }

    public LienzoPanelFocusHandler clear() {
        if (null != panel) {
            if (null != mouseOverListener) {
                panel.removeEventListener(ON_MOUSE_OVER, mouseOverListener);
            }
            if (null != mouseLeaveListener) {
                panel.removeEventListener(ON_MOUSE_LEAVE, mouseLeaveListener);
            }
        }

        mouseOverListener = null;
        mouseLeaveListener = null;
        panel = null;

        return this;
    }
}
