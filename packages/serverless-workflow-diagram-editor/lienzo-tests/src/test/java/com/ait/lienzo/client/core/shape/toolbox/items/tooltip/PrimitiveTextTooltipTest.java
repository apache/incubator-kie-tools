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
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class PrimitiveTextTooltipTest {

    @Mock
    private Tooltip tooltip;

    @Mock
    private Text tooltipText;

    @Mock
    private PrimitiveTextTooltip.BoundingLocationExecutor locationExecutor;

    private PrimitiveTextTooltip tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(locationExecutor.at(any(Direction.class))).thenReturn(locationExecutor);
        when(locationExecutor.forBoundingBox(any(Supplier.class))).thenReturn(locationExecutor);
        when(locationExecutor.offset(any(Supplier.class))).thenReturn(locationExecutor);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ((Consumer<Text>) invocationOnMock.getArguments()[0]).accept(tooltipText);
                return tooltip;
            }
        }).when(tooltip).withText(any(Consumer.class));
        this.tested = new PrimitiveTextTooltip("text1",
                                               tooltip,
                                               locationExecutor);
    }

    @Test
    public void testDefaultValuesAtInit() {
        verify(tooltipText,
               times(1)).setText(eq("text1"));
        verify(locationExecutor,
               times(1)).at(eq(PrimitiveTextTooltip.DEFAULT_AT));
        verify(tooltip,
               times(1)).setDirection(eq(PrimitiveTextTooltip.DEFAULT_TOWARDS));
    }

    @Test
    public void testSetText() {
        final PrimitiveTextTooltip cascade = tested.setText("aText");
        assertEquals(tested,
                     cascade);
        verify(tooltipText,
               times(1)).setText(eq("aText"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUseText() {
        final Consumer textConsumer = mock(Consumer.class);
        PrimitiveTextTooltip cascade = tested.withText(textConsumer);
        assertEquals(tested,
                     cascade);
        verify(tooltip,
               times(1)).withText(eq(textConsumer));
    }

    @Test
    public void testAt() {
        final PrimitiveTextTooltip cascade = tested.at(Direction.NORTH);
        assertEquals(tested,
                     cascade);
        verify(locationExecutor,
               times(1)).at(eq(Direction.NORTH));
        verify(locationExecutor,
               times(1)).accept(eq(tooltip));
    }

    @Test
    public void testTowards() {
        final PrimitiveTextTooltip cascade = tested.towards(Direction.NORTH);
        assertEquals(tested,
                     cascade);
        verify(tooltip,
               times(1)).setDirection(eq(Direction.NORTH));
    }

    @Test
    public void testSetPadding() {
        final PrimitiveTextTooltip cascade = tested.setPadding(15d);
        assertEquals(tested,
                     cascade);
        verify(tooltip,
               times(1)).setPadding(eq(15d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOffset() {
        final Supplier offsetSupplier = mock(Supplier.class);
        final PrimitiveTextTooltip cascade = tested.offset(offsetSupplier);
        assertEquals(tested,
                     cascade);
        verify(locationExecutor,
               times(1)).offset(eq(offsetSupplier));
        verify(locationExecutor,
               times(1)).accept(eq(tooltip));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBoundingBox() {
        final Supplier bbSupplier = mock(Supplier.class);
        final PrimitiveTextTooltip cascade = tested.forComputedBoundingBox(bbSupplier);
        assertEquals(tested,
                     cascade);
        verify(locationExecutor,
               times(1)).forBoundingBox(eq(bbSupplier));
        verify(locationExecutor,
               times(1)).accept(eq(tooltip));
    }

    @Test
    public void testShow() {
        final PrimitiveTextTooltip cascade = tested.show();
        assertEquals(tested,
                     cascade);
        verify(tooltip,
               times(1)).show();
        verify(tooltip,
               never()).hide();
        verify(tooltip,
               never()).destroy();
    }

    @Test
    public void testHide() {
        final PrimitiveTextTooltip cascade = tested.hide();
        assertEquals(tested,
                     cascade);
        verify(tooltip,
               times(1)).hide();
        verify(tooltip,
               never()).show();
        verify(tooltip,
               never()).destroy();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        assertNotNull(tested.getLocationExecutor());
        verify(tooltip,
               times(1)).destroy();
        verify(tooltip,
               never()).hide();
        verify(tooltip,
               never()).show();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAsPrimitive() {
        IPrimitive tp = mock(IPrimitive.class);
        when(tooltip.asPrimitive()).thenReturn(tp);
        IPrimitive<?> p = tested.asPrimitive();
        assertEquals(tp,
                     p);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBoundingLocationExecutor() {
        final PrimitiveTextTooltip.BoundingLocationExecutor executor =
                new PrimitiveTextTooltip.BoundingLocationExecutor();
        final BoundingBox boundingBox = BoundingBox.fromDoubles(0d,
                                                                0d,
                                                                100d,
                                                                200d);
        executor.at(Direction.SOUTH)
                .offset(() -> new Point2D(50, 50))
                .forBoundingBox(() -> boundingBox)
                .accept(tooltip);
        final ArgumentCaptor<Point2D> pointCaptor = ArgumentCaptor.forClass(Point2D.class);
        verify(tooltip,
               times(1)).setLocation(pointCaptor.capture());
        Point2D point = pointCaptor.getValue();
        assertEquals(100,
                     point.getX(),
                     0);
        assertEquals(250,
                     point.getY(),
                     0);
    }
}

