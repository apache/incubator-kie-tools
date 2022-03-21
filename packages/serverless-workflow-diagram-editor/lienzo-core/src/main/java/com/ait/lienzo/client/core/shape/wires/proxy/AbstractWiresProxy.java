/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ait.lienzo.client.core.shape.wires.proxy;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;

public abstract class AbstractWiresProxy implements WiresProxy {

    private final WiresManager wiresManager;

    protected AbstractWiresProxy(final WiresManager wiresManager) {
        this.wiresManager = wiresManager;
    }

    public WiresManager getWiresManager() {
        return wiresManager;
    }

    protected WiresLayer getWiresLayer() {
        return wiresManager.getLayer();
    }

    protected Layer getLayer() {
        return getWiresLayer().getLayer();
    }

    protected void batch() {
        getLayer().batch();
    }
}
