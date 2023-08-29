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


package org.kie.workbench.common.stunner.core.client.shape.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.ShapeState;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ShapeImplTest {

    @Mock
    private ShapeStateHandler shapeStateHandler;

    private ShapeViewExtStub view;
    private ShapeImpl<ShapeView> tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.view = spy(new ShapeViewExtStub());
        this.tested = new ShapeImpl<>(view, shapeStateHandler);
    }

    @Test
    public void testGetters() {
        assertEquals(view,
                     tested.getShapeView());
        assertEquals(shapeStateHandler,
                     tested.getShapeStateHandler());
    }

    @Test
    public void testUUID() {
        tested.setUUID("uuid1");
        assertEquals("uuid1",
                     tested.getUUID());
        verify(view, times(1)).setUUID(eq("uuid1"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testApplyState() {
        tested.applyState(ShapeState.NONE);
        verify(shapeStateHandler,
               never()).shapeAttributesChanged();
        verify(shapeStateHandler,
               times(1)).applyState(eq(ShapeState.NONE));
    }

    @Test
    public void testAfterDraw() {
        tested.afterDraw();
        verify(view,
               times(1)).moveTitleToTop();
    }
}
