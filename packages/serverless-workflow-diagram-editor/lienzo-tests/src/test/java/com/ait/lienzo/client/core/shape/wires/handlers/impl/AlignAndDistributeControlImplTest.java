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


package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.wires.AlignAndDistribute;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class AlignAndDistributeControlImplTest extends AbstractWiresControlTest {

    public static final String UUID = "uuid";
    private AlignAndDistributeControlImpl tested;

    @Mock
    private IPrimitive<?> group;

    @Mock
    private AlignAndDistribute alignAndDistribute;

    @Mock
    private AlignAndDistribute.AlignAndDistributeMatchesCallback callback;

    @Mock
    private BoundingPoints points;

    private BoundingBox boundingBox;

    @Mock
    private Node node;

    @Before
    public void setUp() {
        boundingBox = BoundingBox.fromDoubles(0, 0, 100, 100);
        points = new BoundingPoints(boundingBox);
        when(group.getComputedBoundingPoints()).thenReturn(points);
        when(group.uuid()).thenReturn(UUID);

        tested = new AlignAndDistributeControlImpl(group, alignAndDistribute, callback);
        tested.setIndexed(true);

        when(alignAndDistribute.getControlForShape(anyString())).thenReturn(tested);
    }

    @Test
    public void refreshTest() {
        tested.indexOff(group);
        tested.refresh();

        verify(alignAndDistribute, never()).indexOff(tested);
        verify(alignAndDistribute, times(1)).indexOn(tested);
    }

    @Test
    public void indexOnTest() {
        when(group.asNode()).thenReturn(node);
        tested.indexOn(group);
        AlignAndDistributeControlImpl spied = spy(tested);
        spied.refresh();
        verify(spied, times(1)).updateIndex();
    }

    @Test
    public void indexOffTest() {
        tested.indexOff(group);
        AlignAndDistributeControlImpl spied = spy(tested);
        spied.refresh();
        verify(spied, never()).updateIndex();
    }
}
