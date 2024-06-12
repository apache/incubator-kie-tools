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

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.style.Style.Cursor;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import elemental2.dom.HTMLDivElement;

public abstract class LienzoPanelDelegate<T extends LienzoPanelDelegate> extends LienzoPanel<T> {

    protected abstract LienzoPanel getPanel();

    @Override
    public T add(Layer layer) {
        getPanel().add(layer);
        return cast();
    }

    @Override
    public T setBackgroundLayer(Layer layer) {
        getPanel().setBackgroundLayer(layer);
        return cast();
    }

    @Override
    public T setCursor(Cursor cursor) {
        getPanel().setCursor(cursor);
        return cast();
    }

    @Override
    public int getWidePx() {
        return getPanel().getWidePx();
    }

    @Override
    public int getHighPx() {
        return getPanel().getHighPx();
    }

    @Override
    public Viewport getViewport() {
        return getPanel().getViewport();
    }

    @Override
    public HTMLDivElement getElement() {
        return getPanel().getElement();
    }

    @Override
    public void destroy() {
        getPanel().destroy();
    }

    @SuppressWarnings("unchecked")
    private T cast() {
        return (T) this;
    }
}
