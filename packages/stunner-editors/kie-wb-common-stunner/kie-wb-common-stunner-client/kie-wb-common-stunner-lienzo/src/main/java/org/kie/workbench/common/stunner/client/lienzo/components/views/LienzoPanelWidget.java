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

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.style.Style;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.impl.LienzoFixedPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;

public class LienzoPanelWidget extends LienzoPanel implements IsWidget {

    private final LienzoFixedPanel panel;
    private final Widget widget;

    public static LienzoPanelWidget create(int wide,
                                           int high) {
        return new LienzoPanelWidget(LienzoFixedPanel.newPanel(wide, high));
    }

    LienzoPanelWidget(LienzoFixedPanel panel) {
        this.panel = panel;
        this.widget = ElementWrapperWidget.getWidget(panel.getElement());
    }

    @Override
    public LienzoPanel add(Layer layer) {
        panel.add(layer);
        return this;
    }

    @Override
    public LienzoPanel setBackgroundLayer(Layer layer) {
        panel.setBackgroundLayer(layer);
        return this;
    }

    @Override
    public LienzoPanel setCursor(Style.Cursor cursor) {
        panel.setCursor(cursor);
        return this;
    }

    @Override
    public int getWidePx() {
        return panel.getWidePx();
    }

    @Override
    public int getHighPx() {
        return panel.getHighPx();
    }

    @Override
    public Viewport getViewport() {
        return panel.getViewport();
    }

    @Override
    public HTMLDivElement getElement() {
        return panel.getElement();
    }

    @Override
    public void destroy() {
        panel.destroy();
    }

    @Override
    public Widget asWidget() {
        return widget;
    }
}
