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

package com.ait.lienzo.client.widget.panel.mediators;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;

public abstract class AbstractPanelMediator<T> {

    private final Supplier<LienzoBoundsPanel> panelSupplier;

    public AbstractPanelMediator(final Supplier<LienzoBoundsPanel> panelSupplier) {
        this.panelSupplier = panelSupplier;
    }

    public T enable() {
        if (isEnabled()) {
            return cast();
        }
        getLayer().setListening(false);
        onEnable();
        return cast();
    }

    protected abstract void onEnable();

    public T disable() {
        if (!isEnabled()) {
            return cast();
        }
        getLayer().setListening(true);
        onDisable();
        return cast();
    }

    protected abstract void onDisable();

    public abstract boolean isEnabled();

    public void removeHandler() {
        disable();
        onRemoveHandler();
    }

    protected void onRemoveHandler() {
    }

    @SuppressWarnings("unchecked")
    private T cast() {
        return (T) this;
    }

    protected LienzoBoundsPanel getPanel() {
        return panelSupplier.get();
    }

    protected Layer getLayer() {
        return getPanel().getLayer();
    }
}
