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


package org.kie.workbench.common.stunner.lienzo.primitive;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.impl.LienzoFixedPanel;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;

public class PrimitivePopup extends FlowPanel {

    protected Layer canvasLayer = new Layer();
    protected LienzoFixedPanel lienzoPanel;
    protected int zIndex = 20;

    public PrimitivePopup() {
        attach();
    }

    public PrimitivePopup show(final double width,
                               final double height,
                               final double x,
                               final double y) {
        doShow(null,
               width,
               height,
               x,
               y);
        draw();
        return this;
    }

    private void draw() {
        lienzoPanel.getViewport().draw();
    }

    public PrimitivePopup show(final IPrimitive<?> _primitive,
                               final double width,
                               final double height,
                               final double x,
                               final double y) {
        doShow(_primitive,
               width,
               height,
               x,
               y);
        draw();
        return this;
    }

    protected void doShow(final IPrimitive<?> _primitive,
                          final double width,
                          final double height,
                          final double x,
                          final double y) {
        reset();
        lienzoPanel = LienzoFixedPanel.newPanel((int) width, (int) height);
        this.add(ElementWrapperWidget.getWidget(lienzoPanel.getElement()));
        this.lienzoPanel.add(canvasLayer);
        if (null != _primitive) {
            canvasLayer.add(_primitive);
        }
        this.getElement().getStyle().setLeft(x,
                                             Style.Unit.PX);
        this.getElement().getStyle().setTop(y,
                                            Style.Unit.PX);
        this.getElement().getStyle().setZIndex(zIndex);
        this.getElement().getStyle().setDisplay(Style.Display.INLINE);
    }

    public PrimitivePopup hide() {
        reset();
        this.getElement().getStyle().setDisplay(Style.Display.NONE);
        return this;
    }

    public PrimitivePopup remove() {
        reset();
        deattach();
        return this;
    }

    public void setzIndex(final int zIndex) {
        this.zIndex = zIndex;
    }

    public LienzoPanel getLienzoPanel() {
        return lienzoPanel;
    }

    protected void attach() {
        RootPanel.get().add(this);
        this.getElement().getStyle().setPosition(Style.Position.FIXED);
        this.getElement().getStyle().setZIndex(zIndex);
        this.getElement().getStyle().setDisplay(Style.Display.NONE);
    }

    protected void deattach() {
        RootPanel.get().remove(this);
    }

    protected void reset() {
        this.clear();
        canvasLayer = new Layer();
        lienzoPanel = null;
    }
}
