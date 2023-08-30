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


package org.kie.workbench.common.stunner.client.lienzo.components.views;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.lienzo.primitive.PrimitiveTooltip;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoTextTooltipTest {

    @Mock
    private PrimitiveTooltip primitiveTooltip;

    private LienzoTextTooltip tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.tested = new LienzoTextTooltip(primitiveTooltip);
    }

    @Test
    public void testShow() {
        tested.show("content1",
                    new Point2D(200d,
                                100d));
        final ArgumentCaptor<com.ait.lienzo.client.core.types.Point2D> pointCaptor =
                ArgumentCaptor.forClass(com.ait.lienzo.client.core.types.Point2D.class);
        verify(primitiveTooltip,
               times(1))
                .show(eq("content1"),
                      pointCaptor.capture(),
                      eq(PrimitiveTooltip.Direction.WEST));
        final com.ait.lienzo.client.core.types.Point2D point = pointCaptor.getValue();
        assertEquals(200d,
                     point.getX(),
                     0);
        assertEquals(100d,
                     point.getY(),
                     0);
        verify(primitiveTooltip,
               never()).hide();
        verify(primitiveTooltip,
               never()).remove();
    }

    @Test
    public void testHide() {
        tested.hide();
        verify(primitiveTooltip,
               times(1)).hide();
        verify(primitiveTooltip,
               never()).show(anyString(),
                             any(com.ait.lienzo.client.core.types.Point2D.class),
                             anyDouble(),
                             anyDouble(),
                             any(PrimitiveTooltip.Direction.class));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(primitiveTooltip,
               times(1)).remove();
        verify(primitiveTooltip,
               never()).show(anyString(),
                             any(com.ait.lienzo.client.core.types.Point2D.class),
                             anyDouble(),
                             anyDouble(),
                             any(PrimitiveTooltip.Direction.class));
    }
}
