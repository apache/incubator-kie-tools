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


package com.ait.lienzo.client.core.shape.toolbox.items.tooltip;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class TextTooltipItemImplTest {

    @Mock
    private TextTooltipItem tooltipItem;

    @Mock
    private Supplier<BoundingBox> boundingBox;

    private TextTooltipItemImpl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(tooltipItem.at(any(Direction.class))).thenReturn(tooltipItem);
        when(tooltipItem.towards(any(Direction.class))).thenReturn(tooltipItem);
        when(tooltipItem.setText(anyString())).thenReturn(tooltipItem);
        when(tooltipItem.forComputedBoundingBox(any(Supplier.class))).thenReturn(tooltipItem);
        this.tested = new TextTooltipItemImpl(new Supplier<TextTooltipItem>() {
            @Override
            public TextTooltipItem get() {
                return tooltipItem;
            }
        },
                                              "text1");
    }

    @Test
    public void testShow() {
        final TextTooltipItemImpl cascade = tested
                .at(Direction.SOUTH_WEST)
                .towards(Direction.NORTH_EAST)
                .forComputedBoundingBox(boundingBox)
                .show();
        assertEquals(tested,
                     cascade);
        verify(tooltipItem,
               times(1)).at(eq(Direction.SOUTH_WEST));
        verify(tooltipItem,
               times(1)).towards(eq(Direction.NORTH_EAST));
        verify(tooltipItem,
               times(1)).forComputedBoundingBox(eq(boundingBox));
        verify(tooltipItem,
               times(1)).setText(eq("text1"));
        verify(tooltipItem,
               times(1)).hide();
        verify(tooltipItem,
               times(1)).show();
    }

    @Test
    public void testHide() {
        final TextTooltipItemImpl cascade = tested.hide();
        assertEquals(tested,
                     cascade);
        verify(tooltipItem,
               times(1)).hide();
        verify(tooltipItem,
               never()).show();
    }
}
