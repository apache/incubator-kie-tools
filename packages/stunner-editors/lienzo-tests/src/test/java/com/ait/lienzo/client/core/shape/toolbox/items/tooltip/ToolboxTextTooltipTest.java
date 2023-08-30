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

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.toolbox.items.LayerToolbox;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ToolboxTextTooltipTest {

    @Mock
    private Layer layer;

    @Mock
    private LayerToolbox toolbox;

    @Mock
    private PrimitiveTextTooltip tooltip;

    @Mock
    private IPrimitive tooltipPrim;

    @Mock
    private TextTooltipItemImpl delegate;

    private ToolboxTextTooltip tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(toolbox.getLayer()).thenReturn(layer);
        when(tooltip.asPrimitive()).thenReturn(tooltipPrim);
        this.tested = new ToolboxTextTooltip(toolbox,
                                             tooltip,
                                             delegate);
    }

    @Test
    public void testAttachAtInit() {
        verify(layer,
               times(1)).add(eq(tooltipPrim));
    }

    @Test
    public void testAt() {
        ToolboxTextTooltip cascade = tested.at(Direction.NORTH);
        assertEquals(tested,
                     cascade);
        verify(delegate,
               times(1)).at(eq(Direction.NORTH));
    }

    @Test
    public void testTowards() {
        ToolboxTextTooltip cascade = tested.towards(Direction.NORTH);
        assertEquals(tested,
                     cascade);
        verify(delegate,
               times(1)).towards(eq(Direction.NORTH));
    }

    @Test
    public void testSetText() {
        ToolboxTextTooltip cascade = tested.setText("text1");
        assertEquals(tested,
                     cascade);
        verify(delegate,
               times(1)).setText(eq("text1"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUseText() {
        final Consumer textConsumer = mock(Consumer.class);
        ToolboxTextTooltip cascade = tested.withText(textConsumer);
        assertEquals(tested,
                     cascade);
        verify(tooltip,
               times(1)).withText(eq(textConsumer));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetBoundingBox() {
        final Supplier bbSupplier = mock(Supplier.class);
        ToolboxTextTooltip cascade = tested.forComputedBoundingBox(bbSupplier);
        assertEquals(tested,
                     cascade);
        verify(delegate,
               times(1)).forComputedBoundingBox(eq(bbSupplier));
    }

    @Test
    public void testShow() {
        ToolboxTextTooltip cascade = tested.show();
        assertEquals(tested,
                     cascade);
        verify(delegate,
               times(1)).show();
    }

    @Test
    public void testHide() {
        ToolboxTextTooltip cascade = tested.hide();
        assertEquals(tested,
                     cascade);
        verify(delegate,
               times(1)).hide();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(delegate,
               times(1)).destroy();
    }
}
