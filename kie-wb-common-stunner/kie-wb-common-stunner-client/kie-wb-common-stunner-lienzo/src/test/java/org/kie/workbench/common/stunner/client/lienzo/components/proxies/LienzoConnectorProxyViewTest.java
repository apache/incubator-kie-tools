/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.components.proxies;

import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.proxy.WiresDragProxy;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoConnectorProxyViewTest {

    @Mock
    private WiresCanvas canvas;

    @Mock
    private WiresManager wiresManager;

    @Mock
    private EdgeShape connector;

    @Mock
    private WiresDragProxy dragProxy;

    private LienzoConnectorProxyView tested;

    @Before
    public void setup() {
        when(canvas.getWiresManager()).thenReturn(wiresManager);
        LienzoConnectorProxyView.DRAG_PROXY_BUILDER = p -> dragProxy;
        tested = new LienzoConnectorProxyView();
        tested.setCanvas(canvas);
        tested.onCreate(() -> connector);
    }

    @Test
    public void testStart() {
        tested.start(1d, 2d);
        verify(dragProxy, times(1)).enable(eq(1d), eq(2d));
    }

    @Test
    public void testDestroy() {
        tested.start(1d, 2d);
        tested.destroy();
        verify(dragProxy, times(1)).destroy();
    }
}
