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


package com.ait.lienzo.client.core.shape.wires.util;

import java.util.function.BiConsumer;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.wires.IControlHandle;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.event.WiresConnectorPointsChangedHandler;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.tools.client.event.HandlerRegistration;
import com.ait.lienzo.tools.client.event.HandlerRegistrationManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresConnectorLabelTest {

    @Mock
    private WiresConnector connector;

    @Mock
    private IControlHandleList iControlHandleList;

    @Mock
    private IControlHandle iControlHandle1;

    @Mock
    private IControlHandle iControlHandle2;

    @Mock
    private IControlHandle iControlHandle3;

    @Mock
    private IControlHandle iControlHandle4;

    @Mock
    private IPrimitive point1;

    @Mock
    private IPrimitive point2;

    @Mock
    private IPrimitive point3;

    @Mock
    private IPrimitive point4;

    @Mock
    private HandlerRegistrationManager registrationManager;

    @Mock
    private HandlerRegistration handlerRegistration;

    @Mock
    private Group group;

    @Mock
    private Layer layer;

    @Mock
    private BiConsumer<WiresConnector, Text> executor;

    @Mock
    private Text text;

    private WiresConnectorLabel tested;

    @Before
    public void setup() {
        when(connector.addWiresConnectorPointsChangedHandler(any(WiresConnectorPointsChangedHandler.class))).thenReturn(handlerRegistration);
        when(connector.getGroup()).thenReturn(group);
        when(group.getLayer()).thenReturn(layer);
        tested = new WiresConnectorLabel(text,
                                         connector,
                                         executor,
                                         registrationManager);
    }

    @Test
    public void testInit() {
        verify(text, times(1)).setListening(eq(false));
        verify(text, times(1)).setDraggable(eq(false));
        verify(group, times(1)).add(eq(text));
        verify(registrationManager, times(1)).register(any(HandlerRegistration.class));
        verify(executor, times(1)).accept(eq(connector), eq(text));
    }

    @Test
    public void testConfigure() {
        final boolean[] configured = new boolean[]{false};
        tested.configure(t -> {
            assertEquals(text, t);
            configured[0] = true;
        });
        assertTrue(configured[0]);
        verifyAccept();

    }

    @Test
    public void testShow() {
        tested.show();
        verify(text, times(1)).setAlpha(eq(1d));
        verifyAccept();
    }

    @Test
    public void testHide() {
        tested.hide();
        verify(text, times(1)).setAlpha(eq(0d));
        verifyAccept();
        verifyRefresh();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(text, times(1)).removeFromParent();
        verify(registrationManager, times(1)).destroy();
    }


    // P1(10,10)
    // P2(10,20)
    // P3(100, 20)
    // P4(100, 40)
    @Test
    public void testGetLargestSegment() {
        when(connector.getPointHandles()).thenReturn(iControlHandleList);
        when(iControlHandleList.size()).thenReturn(4);
        when(iControlHandleList.getHandle(0)).thenReturn(iControlHandle1);
        when(iControlHandleList.getHandle(1)).thenReturn(iControlHandle2);
        when(iControlHandleList.getHandle(2)).thenReturn(iControlHandle3);
        when(iControlHandleList.getHandle(3)).thenReturn(iControlHandle4);
        when(iControlHandle1.getControl()).thenReturn(point1);
        when(iControlHandle2.getControl()).thenReturn(point2);
        when(iControlHandle3.getControl()).thenReturn(point3);
        when(iControlHandle4.getControl()).thenReturn(point4);

        when(point1.getX()).thenReturn(10d);
        when(point1.getY()).thenReturn(10d);

        when(point2.getX()).thenReturn(10d);
        when(point2.getY()).thenReturn(20d);

        when(point3.getX()).thenReturn(100d);
        when(point3.getY()).thenReturn(20d);

        when(point4.getX()).thenReturn(100d);
        when(point4.getY()).thenReturn(40d);

        final WiresConnectorLabelFactory.Segment largestSegment = WiresConnectorLabel.getLargestSegment(connector);
        assertEquals(largestSegment.getStart().getX(), 10d, 0d);
        assertEquals(largestSegment.getStart().getY(), 20d, 0d);
        assertEquals(largestSegment.getEnd().getX(), 100d, 0d);
        assertEquals(largestSegment.getEnd().getY(), 20d, 0d);



    }

    private void verifyRefresh() {
        verify(layer, atLeastOnce()).batch();
    }

    private void verifyAccept() {
        verify(executor, atLeastOnce()).accept(eq(connector), eq(text));
    }
}
