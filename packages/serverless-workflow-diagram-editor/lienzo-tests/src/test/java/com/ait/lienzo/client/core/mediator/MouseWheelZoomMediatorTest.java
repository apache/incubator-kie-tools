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

package com.ait.lienzo.client.core.mediator;

import com.ait.lienzo.client.core.shape.Scene;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.WheelEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class MouseWheelZoomMediatorTest
{
    @Mock
    private WheelEvent mouseWheelEvent;

    private MouseWheelZoomMediator tested;

    @Mock
    private Viewport               viewport;

    @Mock
    private HTMLDivElement         element;

    @Mock
    private Scene scene;


    private Transform transform = new Transform();

    @Before
    public void setUp()
    {

        this.tested = spy(new MouseWheelZoomMediator());
        this.tested.setZoomFactor(0.3);
        this.tested.setViewport(viewport);

        when(viewport.getScene()).thenReturn(scene);
        when(viewport.getElement()).thenReturn(element);
        when(tested.getViewport()).thenReturn(viewport);

        doCallRealMethod().when(viewport).setTransform(any(Transform.class));
        doCallRealMethod().when(viewport).getTransform();
        viewport.setTransform(transform);
    }

    @Test
    public void testOnMouseWheelAboutPoint()
    {
        tested.setScaleAboutPoint(true);
        mouseWheelEvent.deltaY = -1;
        tested.onMouseWheel(mouseWheelEvent, 110, 323);

        assertEquals(0.7692307692307692d, viewport.getTransform().getScaleX(), 0d);
        assertEquals(0.7692307692307692d, viewport.getTransform().getScaleY(), 0d);
        assertEquals(25.384615384615387d, viewport.getTransform().getTranslateX(), 0d);
        assertEquals(74.53846153846155d, viewport.getTransform().getTranslateY(), 0d);


    }

    @Test
    public void testOnMouseWheelRelativeToCenter()
    {
        tested.setScaleAboutPoint(false);
        mouseWheelEvent.deltaY = -1;
        tested.onMouseWheel(mouseWheelEvent, 110, 323);
        assertEquals(transform, viewport.getTransform());
        assertEquals(0.7692307692307692d, viewport.getTransform().getScaleX(), 0d);
        assertEquals(0.7692307692307692d, viewport.getTransform().getScaleY(), 0d);
        assertEquals(0d, viewport.getTransform().getTranslateX(), 0d);
        assertEquals(0d, viewport.getTransform().getTranslateY(), 0d);
    }
}
