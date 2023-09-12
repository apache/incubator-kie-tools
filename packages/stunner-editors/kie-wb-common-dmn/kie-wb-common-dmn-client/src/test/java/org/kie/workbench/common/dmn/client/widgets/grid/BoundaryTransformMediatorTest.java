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

package org.kie.workbench.common.dmn.client.widgets.grid;

import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class BoundaryTransformMediatorTest {

    @Mock
    private GridWidget gridWidget;

    private Bounds visibleBounds = new BaseBounds(0,
                                                  0,
                                                  1000,
                                                  1000);

    private BoundaryTransformMediator restriction;

    @Before
    public void setup() {
        this.restriction = new BoundaryTransformMediator(gridWidget);
    }

    private void setGridWidgetBounds(final double x,
                                     final double y,
                                     final double width,
                                     final double height) {
        when(gridWidget.getX()).thenReturn(x);
        when(gridWidget.getY()).thenReturn(y);
        when(gridWidget.getWidth()).thenReturn(width);
        when(gridWidget.getHeight()).thenReturn(height);
    }

    private void testTransformation(final double txActual,
                                    final double tyActual,
                                    final double txExpected,
                                    final double tyExpected) {
        final Transform test = new Transform().translate(txActual,
                                                         tyActual);
        final Transform result = restriction.adjust(test,
                                                    visibleBounds);

        assertNotNull(result);
        assertEquals(txExpected,
                     result.getTranslateX(),
                     0.0);
        assertEquals(tyExpected,
                     result.getTranslateY(),
                     0.0);
    }

    @Test
    public void testLeftEdgeWhenGridIsSmallerThanVisibleBounds() {
        setGridWidgetBounds(0,
                            0,
                            500,
                            500);
        testTransformation(1200,
                           0,
                           0,
                           0);
    }

    @Test
    public void testRightEdgeWhenGridIsSmallerThanVisibleBounds() {
        setGridWidgetBounds(0,
                            0,
                            500,
                            500);
        testTransformation(-200,
                           0,
                           0,
                           0);
    }

    @Test
    public void testTopEdgeWhenGridIsSmallerThanVisibleBounds() {
        setGridWidgetBounds(0,
                            0,
                            500,
                            500);
        testTransformation(0,
                           1200,
                           0,
                           0);
    }

    @Test
    public void testBottomEdgeWhenGridIsSmallerThanVisibleBounds() {
        setGridWidgetBounds(0,
                            0,
                            500,
                            500);
        testTransformation(0,
                           -200,
                           0,
                           0);
    }

    @Test
    public void testLeftEdgeWhenGridIsLargerThanVisibleBounds() {
        setGridWidgetBounds(0,
                            0,
                            5000,
                            5000);
        testTransformation(1200,
                           0,
                           0,
                           0);
    }

    @Test
    public void testRightEdgeWhenGridIsLargerThanVisibleBounds() {
        setGridWidgetBounds(0,
                            0,
                            5000,
                            5000);
        testTransformation(-200,
                           0,
                           -200,
                           0);
    }

    @Test
    public void testTopEdgeWhenGridIsLargerThanVisibleBounds() {
        setGridWidgetBounds(0,
                            0,
                            5000,
                            5000);
        testTransformation(0,
                           1200,
                           0,
                           0);
    }

    @Test
    public void testBottomEdgeWhenGridIsLargerThanVisibleBounds() {
        setGridWidgetBounds(0,
                            0,
                            5000,
                            5000);
        testTransformation(0,
                           -200,
                           0,
                           -200);
    }
}
