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
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.collection.NFastArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class JsWiresMagnetTest {

    JsWiresMagnet jsWiresMagnet;

    @Mock
    WiresMagnet magnet;

    @Mock
    IPrimitive primitive;

    @Mock
    WiresConnection connection1;

    @Mock
    WiresConnection connection2;

    @Test
    public void testGetHeadConnection() {
        when(magnet.getControl()).thenReturn(primitive);

        jsWiresMagnet = new JsWiresMagnet(magnet);
        final IPrimitive control = jsWiresMagnet.getControl();
        assertEquals("Controls should be the same", primitive, control);
    }

    @Test
    public void testGetConnectionsSize() {
        NFastArrayList<WiresConnection> connections = new NFastArrayList<>();
        connections.add(connection1);
        connections.add(connection2);
        when(magnet.getConnections()).thenReturn(connections);
        jsWiresMagnet = new JsWiresMagnet(magnet);
        final int connectionSize = jsWiresMagnet.getConnectionSize();
        assertEquals("Connections should have same size", connections.size(), connectionSize);
    }

    @Test
    public void testGetConnection() {
        NFastArrayList<WiresConnection> connections = new NFastArrayList<>();
        connections.add(connection1);
        connections.add(connection2);
        when(magnet.getConnections()).thenReturn(connections);
        jsWiresMagnet = new JsWiresMagnet(magnet);
        final JsWiresConnection connection = jsWiresMagnet.getConnection(0);
        verify(magnet, times(1)).getConnections();
    }
}
