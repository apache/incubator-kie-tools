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
import elemental2.dom.HTMLElement;
import org.kie.j2cl.tools.di.core.IsElement;
import org.kie.workbench.common.stunner.lienzo.flowpanel.FloatingWidgetView;

import static elemental2.dom.CSSProperties.ZIndexUnionType;

public class PrimitivePopup {

    private FloatingWidgetView floatingWidgetView = new FloatingWidgetView();

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
        floatingWidgetView.add(new IsElement() {
            @Override
            public HTMLElement getElement() {
                return lienzoPanel.getElement();
            }
        });
        this.lienzoPanel.add(canvasLayer);
        if (null != _primitive) {
            canvasLayer.add(_primitive);
        }
        floatingWidgetView.getPanel().getElement().style.left = x + "px";
        floatingWidgetView.getPanel().getElement().style.top = y + "px";
        floatingWidgetView.getPanel().getElement().style.zIndex = ZIndexUnionType.of(zIndex);
        floatingWidgetView.getPanel().getElement().style.display = "inline";
        floatingWidgetView.show();
    }

    public PrimitivePopup hide() {
        reset();
        floatingWidgetView.hide();
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
        floatingWidgetView.getPanel().getElement().style.position = "fixed";
        floatingWidgetView.getPanel().getElement().style.zIndex = ZIndexUnionType.of(zIndex);
        floatingWidgetView.getPanel().getElement().style.display = "none";
    }

    protected void deattach() {
        floatingWidgetView.destroy();
    }

    protected void reset() {
        floatingWidgetView.clear();
        canvasLayer = new Layer();
        lienzoPanel = null;
    }
}
