/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import java.util.Arrays;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.shape.Attributes;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.json.IJSONSerializable;
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
    private Attribute attribute;
    private BoundingPoints points;

    private BoundingBox boundingBox;
    private String prop = "prop";

    private Attributes attributes;

    @Mock
    private IJSONSerializable<?> groupAttr;

    @Before
    public void setUp() {
        attributes = new Attributes(groupAttr);
        boundingBox = new BoundingBox(0, 0, 100, 100);
        points = new BoundingPoints(boundingBox);
        when(group.getComputedBoundingPoints()).thenReturn(points);
        when(attribute.getProperty()).thenReturn(prop);
        when(group.getAttributes()).thenReturn(attributes);
        when(group.uuid()).thenReturn(UUID);
        tested = new AlignAndDistributeControlImpl(group, alignAndDistribute, callback, Arrays.asList(attribute));

        when(alignAndDistribute.getControlForShape(anyString())).thenReturn(tested);
    }

    @Test
    public void refreshTest() {
        tested.setIndexed(true);
        tested.indexOff(group);
        tested.refresh();
        verify(alignAndDistribute, never()).indexOff(tested);
        verify(alignAndDistribute, times(1)).indexOn(tested);
    }
}
