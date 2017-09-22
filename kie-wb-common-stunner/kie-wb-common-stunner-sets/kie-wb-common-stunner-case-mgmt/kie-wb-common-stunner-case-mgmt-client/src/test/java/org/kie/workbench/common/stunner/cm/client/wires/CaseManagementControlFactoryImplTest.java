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

import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingAndContainmentControl;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementControlFactoryImplTest {

    @Mock
    private WiresShape shape;

    @Mock
    private WiresManager wiresManager;

    private CaseManagementContainmentStateHolder state;

    private CaseManagementControlFactoryImpl factory;

    @Before
    public void setup() {
        this.state = new CaseManagementContainmentStateHolder();
        this.factory = new CaseManagementControlFactoryImpl(state);
    }

    @Test
    public void assertNewDockingAndContainmentControl() {
        final WiresDockingAndContainmentControl control = factory.newDockingAndContainmentControl(shape,
                                                                                                  wiresManager);
        assertTrue(control instanceof CaseManagementDockingAndContainmentControlImpl);
    }
}
