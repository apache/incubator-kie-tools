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

package com.ait.lienzo.client.widget.panel.impl;

import com.ait.lienzo.client.widget.panel.IsResizable;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.client.widget.panel.ResizeCallback;
import com.ait.lienzo.client.widget.panel.ResizeObserver;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;

/**
 * Automatically fits its size to the parent's one.
 */
public class LienzoResizablePanel
        extends LienzoPanelDelegate<LienzoResizablePanel>
        implements IsResizable {

    private final LienzoFixedPanel panel;
    private ResizeObserver resizeObserver;
    private final ResizeCallback m_resizeCallback;

    public static LienzoResizablePanel newPanel() {
        LienzoFixedPanel panel = LienzoFixedPanel.newPanel();
        return new LienzoResizablePanel(panel);
    }

    public LienzoResizablePanel(LienzoFixedPanel panel) {
        this.panel = panel;
        this.m_resizeCallback = e -> fitToParentSize();
    }

    public void initResizeObserver() {
        if (null == resizeObserver &&
                null != panel.getElement().parentNode &&
                null != panel.getElement().parentNode.parentNode) {
            resizeObserver = new ResizeObserver(m_resizeCallback);
            resizeObserver.observe((Element) panel.getElement().parentNode.parentNode);
        }
    }

    @Override
    public void onResize() {
        initResizeObserver();
    }

    @Override
    protected LienzoPanel getPanel() {
        return panel;
    }

    @Override
    public void destroy() {
        resizeObserver.disconnect();
        resizeObserver = null;
        super.destroy();
    }

    private void fitToParentSize() {
        if (null != panel.getElement().parentNode &&
                null != panel.getElement().parentNode.parentNode) {
            HTMLDivElement parent = (HTMLDivElement) panel.getElement().parentNode.parentNode;
            int offsetWidth = parent.offsetWidth;
            int offsetHeight = parent.offsetHeight;
            if (offsetWidth > 0 && offsetHeight > 0) {
                panel.setPixelSize(offsetWidth, offsetHeight);
            }
        }
    }
}
