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

package com.ait.lienzo.client.core.shape.wires.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.common.api.java.util.function.BiConsumer;
import com.ait.tooling.common.api.java.util.function.Consumer;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.HandlerRegistration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresConnectorLabelTest {

    @Mock
    private WiresConnector connector;

    @Mock
    private HandlerRegistrationManager registrationManager;

    @Mock
    private Group group;

    @Mock
    private Layer layer;

    @Mock
    private BiConsumer<WiresConnector, Text> executor;

    @Mock
    private Text text;

    private WiresConnectorLabel tested;

    @Before
    public void setup() {
        when(connector.getGroup()).thenReturn(group);
        when(group.getLayer()).thenReturn(layer);
        tested = new WiresConnectorLabel(text,
                                         connector,
                                         executor,
                                         registrationManager);
    }

    @Test
    public void testInit() {
        verify(text, times(1)).setListening(eq(false));
        verify(text, times(1)).setDraggable(eq(false));
        verify(group, times(1)).add(eq(text));
        verify(registrationManager, times(1)).register(any(HandlerRegistration.class));
        verify(executor, times(1)).accept(eq(connector), eq(text));
    }

    @Test
    public void testConfigure() {
        final boolean[] configured = new boolean[]{false};
        tested.configure(new Consumer<Text>() {
            @Override
            public void accept(Text t) {
                assertEquals(text, t);
                configured[0] = true;
            }
        });
        assertTrue(configured[0]);
        verifyRefresh();
    }

    @Test
    public void testShow() {
        tested.show();
        verify(text, times(1)).setAlpha(eq(1d));
        verifyRefresh();
    }

    @Test
    public void testHide() {
        tested.hide();
        verify(text, times(1)).setAlpha(eq(0d));
        verifyRefresh();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(text, times(1)).removeFromParent();
        verify(registrationManager, times(1)).destroy();
    }

    private void verifyRefresh() {
        verify(executor, atLeastOnce()).accept(eq(connector), eq(text));
        verify(layer, atLeastOnce()).batch();
    }

    @Test
    public void testBatchNullLayer(){
        reset(group, layer);
        when(group.getLayer()).thenReturn(null);
        tested.show();
        verify(layer, never()).batch();
    }
}