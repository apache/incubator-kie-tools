/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.wires;

import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresCompositeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectionControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectionControlImpl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectorControlImpl;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementControlFactoryTest {

    @Mock
    private WiresShape shape;

    @Mock
    private WiresConnector wiresConnector;

    @Mock
    private WiresManager wiresManager;

    private CaseManagementContainmentStateHolder state;

    private CaseManagementControlFactory factory;

    @Before
    public void setup() {
        this.state = new CaseManagementContainmentStateHolder();
        this.factory = new CaseManagementControlFactory(state);
    }

    @Test
    public void testControls() {
        WiresShapeControl shapeControl = factory.newShapeControl(shape,
                                                                 wiresManager);
        assertNotNull(shapeControl);
        assertTrue(shapeControl instanceof CaseManagementShapeControl);
        WiresConnectorControl connectorControl = factory.newConnectorControl(wiresConnector,
                                                                             wiresManager);
        assertNotNull(connectorControl);
        assertTrue(connectorControl instanceof WiresConnectorControlImpl);

        WiresConnectionControl connectionControl = factory.newConnectionControl(wiresConnector,
                                                                                true,
                                                                                wiresManager);
        assertNotNull(connectionControl);
        assertTrue(connectionControl instanceof WiresConnectionControlImpl);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCompositeControlNotSupported() {
        WiresCompositeControl.Context context = mock(WiresCompositeControl.Context.class);
        factory.newCompositeControl(context,
                                    wiresManager);
    }
}
