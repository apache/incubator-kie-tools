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

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasDomainObjectListener;
import org.kie.workbench.common.stunner.core.client.canvas.listener.CanvasElementListener;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNGridLayerControlImplTest {

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private ClientSession session;

    @Mock
    private Element element;

    @Mock
    private DomainObject domainObject;

    @Captor
    private ArgumentCaptor<CanvasElementListener> canvasElementListenerCaptor;

    @Captor
    private ArgumentCaptor<CanvasDomainObjectListener> domainObjectListenerCaptor;

    private DMNGridLayerControlImpl control;

    @Before
    public void setup() {
        this.control = new DMNGridLayerControlImpl() {
            @Override
            DMNGridLayer makeGridLayer() {
                return gridLayer;
            }
        };

        when(session.getCanvasHandler()).thenReturn(canvasHandler);
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
    public void testDoInitWithBoundSession() {
        control.bind(session);

        control.doInit();

        assertEquals(gridLayer,
                     control.getGridLayer());

        verify(canvasHandler).addRegistrationListener(canvasElementListenerCaptor.capture());
        final CanvasElementListener canvasElementListener = canvasElementListenerCaptor.getValue();
        canvasElementListener.update(element);
        verify(gridLayer).batch();

        reset(gridLayer);

        verify(canvasHandler).addDomainObjectListener(domainObjectListenerCaptor.capture());
        final CanvasDomainObjectListener domainObjectListener = domainObjectListenerCaptor.getValue();
        domainObjectListener.update(domainObject);
        verify(gridLayer).batch();
    }

    @Test
    public void testDoDestroyWithBoundSession() {
        control.bind(session);

        control.doInit();

        verify(canvasHandler).addRegistrationListener(canvasElementListenerCaptor.capture());
        verify(canvasHandler).addDomainObjectListener(domainObjectListenerCaptor.capture());
        final CanvasElementListener canvasElementListener = canvasElementListenerCaptor.getValue();
        final CanvasDomainObjectListener domainObjectListener = domainObjectListenerCaptor.getValue();

        control.doDestroy();

        assertNull(control.getGridLayer());

        verify(canvasHandler).removeRegistrationListener(eq(canvasElementListener));
        verify(canvasHandler).removeDomainObjectListener(eq(domainObjectListener));
    }

    @Test
    public void testGetGridLayer() {
        final DMNGridLayer gridLayer = control.getGridLayer();

        //Check same instance is re-used
        assertEquals("GridLayer instances should be identical.",
                     gridLayer,
                     control.getGridLayer());
    }
}
