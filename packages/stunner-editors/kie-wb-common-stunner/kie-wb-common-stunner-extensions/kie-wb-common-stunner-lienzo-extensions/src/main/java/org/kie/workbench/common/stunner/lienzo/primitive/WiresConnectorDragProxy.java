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

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;

public class WiresConnectorDragProxy extends AbstractDragProxy<WiresConnector> {

    public WiresConnectorDragProxy(final Layer layer,
                                   final WiresConnector shape,
                                   final int x,
                                   final int y,
                                   final int timeout,
                                   final Callback callback) {
        super(layer,
              shape,
              x,
              y,
              timeout,
              callback);
    }

    @Override
    protected void addToLayer(final Layer layer,
                              final WiresConnector shape) {
        getWiresManager(layer).register(shape);
    }

    @Override
    protected void removeFromLayer(final Layer layer,
                                   final WiresConnector shape) {
        getWiresManager(layer).deregister(shape);
    }

    @Override
    protected void setLocation(WiresConnector shape,
                               int x,
                               int y) {
        shape.getGroup().setX(x).setY(y);
    }

    protected WiresManager getWiresManager(final Layer layer) {
        return WiresManager.get(layer);
    }
}
