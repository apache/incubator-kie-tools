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


package org.kie.workbench.common.stunner.svg.client.shape.impl;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.impl.LienzoShape;
import org.kie.workbench.common.stunner.client.lienzo.shape.impl.ShapeStateDefaultHandler;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.LienzoShapeView;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class SVGShapeImplTest {

    @Mock
    private SVGShapeViewImpl view;

    @Mock
    private ShapeStateDefaultHandler svgShapeStateHandler;

    @Mock
    private LienzoShape<LienzoShapeView> lienzoShape;

    private SVGShapeImpl tested;
    private SVGShapeImpl mocked;

    @Before
    public void setup() throws Exception {
        when(view.getShapeStateHandler()).thenReturn(svgShapeStateHandler);
        this.tested = new SVGShapeImpl(view);
        this.mocked = new SVGShapeImpl(view,
                                       lienzoShape);
    }

    @Test
    public void testView() {
        assertEquals(view,
                     tested.getShapeView());
    }

    @Test
    public void testUUID() {
        final String uuid = "uuid1";
        tested.setUUID(uuid);
        assertEquals(uuid,
                     tested.getUUID());
    }

    @Test
    public void testBeforeDraw() {
        mocked.beforeDraw();
        verify(lienzoShape,
               times(1)).beforeDraw();
        verify(lienzoShape,
               times(0)).afterDraw();
        verify(lienzoShape,
               times(0)).applyState(any(ShapeState.class));
    }

    @Test
    public void testAfterDraw() {
        mocked.afterDraw();
        verify(lienzoShape,
               times(1)).afterDraw();
        verify(lienzoShape,
               times(0)).beforeDraw();
        verify(lienzoShape,
               times(0)).applyState(any(ShapeState.class));
    }

    @Test
    public void testApplyState() {
        final ShapeState state = ShapeState.SELECTED;
        mocked.applyState(state);
        verify(lienzoShape,
               times(1)).applyState(eq(state));
        verify(lienzoShape,
               times(0)).beforeDraw();
        verify(lienzoShape,
               times(0)).afterDraw();
    }
}
