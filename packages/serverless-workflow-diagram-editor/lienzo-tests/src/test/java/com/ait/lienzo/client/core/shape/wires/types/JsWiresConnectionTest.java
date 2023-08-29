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
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class JsWiresConnectionTest {

    JsWiresConnection jsWiresConnection;

    @Mock
    WiresConnection connection;

    @Mock
    WiresConnector connector;

    @Mock
    WiresMagnet magnet;

    @Mock
    IPrimitive control;

    @Test
    public void testGetConnector() {
        when(connection.getConnector()).thenReturn(connector);
        jsWiresConnection = new JsWiresConnection(connection);
        final JsWiresConnector connector = jsWiresConnection.getConnector();
        verify(connection, Mockito.times(1)).getConnector();
    }

    @Test
    public void testGetMagnet() {
        when(connection.getMagnet()).thenReturn(magnet);
        jsWiresConnection = new JsWiresConnection(connection);
        final JsWiresMagnet magnet = jsWiresConnection.getMagnet();
        verify(connection, Mockito.times(1)).getMagnet();
    }

    @Test
    public void testControl() {
        when(connection.getControl()).thenReturn(control);
        jsWiresConnection = new JsWiresConnection(connection);
        final IPrimitive iPrimitive = jsWiresConnection.getControl();
        verify(connection, Mockito.times(1)).getControl();
    }

}
