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

package org.kie.workbench.common.stunner.client.lienzo.wires;

import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.mockito.Mock;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class StunnerWiresConnectorHandlerTest {

    private StunnerWiresConnectorHandler stunnerWiresConnectorHandler;

    @Mock
    private WiresConnectorView connector;

    @Mock
    private NodeMouseDoubleClickEvent doubleClickEvent;

    @Before
    public void setUp() throws Exception {
        stunnerWiresConnectorHandler = new StunnerWiresConnectorHandler(connector, WiresManager.get(new Layer()));
    }

    @Test
    public void onNodeMouseDoubleClick() {
        stunnerWiresConnectorHandler.onNodeMouseDoubleClick(doubleClickEvent);
        //test it is not executing any action
        verify(connector, never()).getLine();
        verify(connector, never()).getPointHandles();
    }
}