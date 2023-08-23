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

package org.kie.workbench.common.dmn.client.widgets.layer;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class InverseGridWidgetClipperTest {

    private static final double OUTER_WIDTH = 1000.0;

    private static final double OUTER_HEIGHT = 500.0;

    private static final double OUTER_ABSOLUTE_X = 0.0;

    private static final double OUTER_ABSOLUTE_Y = 0.0;

    private static final double INNER_WIDTH = 200.0;

    private static final double INNER_HEIGHT = 50.0;

    private static final double INNER_ABSOLUTE_X = 100.0;

    private static final double INNER_ABSOLUTE_Y = 75.0;

    @Mock
    private Context2D context2D;

    @Mock
    private GridWidget outer;

    @Mock
    private GridWidget inner;

    private InverseGridWidgetClipper clipper;

    @Before
    public void setUp() throws Exception {
        setupClipper(OUTER_WIDTH, OUTER_HEIGHT, OUTER_ABSOLUTE_X, OUTER_ABSOLUTE_Y,
                     INNER_WIDTH, INNER_HEIGHT, INNER_ABSOLUTE_X, INNER_ABSOLUTE_Y);
    }

    @Test
    public void testIsActive() {
        clipper.setActive(true);

        assertThat(clipper.isActive()).isTrue();

        clipper.setActive(false);

        assertThat(clipper.isActive()).isFalse();
    }

    @Test
    public void testClip() {
        clipper.clip(context2D);

        verify(context2D).beginPath();

        //Left edge
        verify(context2D).rect(eq(OUTER_ABSOLUTE_X),
                               eq(OUTER_ABSOLUTE_Y),
                               eq(INNER_ABSOLUTE_X),
                               eq(OUTER_HEIGHT + BaseExpressionGridTheme.STROKE_WIDTH));

        //Top edge
        verify(context2D).rect(eq(INNER_ABSOLUTE_X),
                               eq(OUTER_ABSOLUTE_Y),
                               eq(INNER_WIDTH + BaseExpressionGridTheme.STROKE_WIDTH),
                               eq(INNER_ABSOLUTE_Y));

        //Bottom edge
        verify(context2D).rect(eq(INNER_ABSOLUTE_X),
                               eq(INNER_ABSOLUTE_Y + INNER_HEIGHT + BaseExpressionGridTheme.STROKE_WIDTH),
                               eq(INNER_WIDTH + BaseExpressionGridTheme.STROKE_WIDTH),
                               eq(OUTER_ABSOLUTE_Y + OUTER_HEIGHT - (INNER_ABSOLUTE_Y + INNER_HEIGHT)));

        //Right edge
        verify(context2D).rect(eq(INNER_ABSOLUTE_X + INNER_WIDTH + BaseExpressionGridTheme.STROKE_WIDTH),
                               eq(OUTER_ABSOLUTE_Y),
                               eq(OUTER_ABSOLUTE_X + OUTER_WIDTH - (INNER_ABSOLUTE_X + INNER_WIDTH)),
                               eq(OUTER_HEIGHT + BaseExpressionGridTheme.STROKE_WIDTH));

        verify(context2D).clip();
    }

    private void setupClipper(final double outerWidth,
                              final double outerHeight,
                              final double outerAbsoluteX,
                              final double outerAbsoluteY,
                              final double innerWidth,
                              final double innerHeight,
                              final double innerAbsoluteX,
                              final double innerAbsoluteY) {
        when(outer.getWidth()).thenReturn(outerWidth);
        when(outer.getHeight()).thenReturn(outerHeight);
        final Point2D outerComputedLocation = new Point2D(outerAbsoluteX, outerAbsoluteY);
        when(outer.getComputedLocation()).thenReturn(outerComputedLocation);

        when(inner.getWidth()).thenReturn(innerWidth);
        when(inner.getHeight()).thenReturn(innerHeight);
        final Point2D innerComputedLocation = new Point2D(innerAbsoluteX, innerAbsoluteY);
        when(inner.getComputedLocation()).thenReturn(innerComputedLocation);

        clipper = new InverseGridWidgetClipper(outer, inner);
    }
}
