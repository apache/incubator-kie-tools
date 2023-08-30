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


package com.ait.lienzo.client.core.shape.wires.types;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.WiresConnection;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import jsinterop.annotations.JsType;

@JsType
public class JsWiresMagnet {

    WiresMagnet magnet;

    public JsWiresMagnet(WiresMagnet magnet) {
        this.magnet = magnet;
    }

    public IPrimitive<?> getControl() {
        return magnet.getControl();
    }

    public int getConnectionSize() {
        int size = 0;
        NFastArrayList<WiresConnection> connections = magnet.getConnections();
        if (null != connections) {
            size = connections.size();
        }
        return size;
    }

    public JsWiresConnection getConnection(int index) {
        WiresConnection wiresConnection = magnet.getConnections().get(index);
        return new JsWiresConnection(wiresConnection);
    }
}
