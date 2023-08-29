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


package org.kie.workbench.common.stunner.client.lienzo.wires;

import java.lang.reflect.Field;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.decorator.MagnetDecorator;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresHandlerFactoryImpl;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.wires.decorator.StunnerMagnetDecorator;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresManagerFactoryImplTest {

    private WiresManagerFactoryImpl wiresManagerFactory;

    @Mock
    private StunnerWiresControlFactory wiresControlFactory;

    @Mock
    private WiresHandlerFactoryImpl wiresHandlerFactory;

    @Mock
    private Viewport viewport;

    @Mock
    private HTMLDivElement element;

    private Layer layer;

    @Before
    public void setUp() throws Exception {
        layer = spy(new Layer());
        when(layer.getViewport()).thenReturn(viewport);
        when(viewport.getElement()).thenReturn(element);
        wiresManagerFactory = new WiresManagerFactoryImpl(wiresControlFactory, wiresHandlerFactory);
    }

    @Test
    public void newWiresManager() throws NoSuchFieldException, IllegalAccessException {
        WiresManager wiresManager = wiresManagerFactory.newWiresManager(layer);
        assertEquals(wiresManager.getControlFactory(), wiresControlFactory);
        assertEquals(wiresManager.getWiresHandlerFactory(), wiresHandlerFactory);

        final Field m_magnetDecoratorField = wiresManager.getMagnetManager().getClass().getDeclaredField("m_magnetDecorator");
        m_magnetDecoratorField.setAccessible(true);
        MagnetDecorator magnetDecorator = (MagnetDecorator) m_magnetDecoratorField.get(wiresManager.getMagnetManager());

        assertTrue(magnetDecorator instanceof StunnerMagnetDecorator);
    }
}