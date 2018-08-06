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

package org.kie.workbench.common.dmn.client.widgets.layer;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNGridLayerControlImplTest {

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private Element element;

    private DMNGridLayerControlImpl control;

    @Before
    public void setup() {
        this.control = new DMNGridLayerControlImpl() {
            @Override
            DMNGridLayer makeGridLayer() {
                return gridLayer;
            }
        };
    }

    @Test
    public void testDoInit() {
        final DMNGridLayer gridLayer = control.getGridLayer();
        assertNotNull(gridLayer);

        control.doInit();

        assertEquals(gridLayer,
                     control.getGridLayer());
    }

    @Test
    public void testDoDestroy() {
        control.doDestroy();

        assertNull(control.getGridLayer());
    }

    @Test
    public void testGetGridLayer() {
        final DMNGridLayer gridLayer = control.getGridLayer();

        //Check same instance is re-used
        assertEquals(gridLayer,
                     control.getGridLayer());
    }

    @Test
    public void testOnCanvasElementUpdatedEvent() {
        final CanvasElementUpdatedEvent event = new CanvasElementUpdatedEvent(canvasHandler, element);

        control.onCanvasElementUpdatedEvent(event);

        verify(gridLayer).batch();
    }
}
