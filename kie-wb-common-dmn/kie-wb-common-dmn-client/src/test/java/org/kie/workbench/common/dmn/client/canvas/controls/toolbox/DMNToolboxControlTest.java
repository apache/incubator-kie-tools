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

package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class DMNToolboxControlTest {

    @Mock
    private ActionsToolboxFactory flowActionsToolboxFactoryInstance;
    private ManagedInstanceStub<ActionsToolboxFactory> flowActionsToolboxFactory;

    @Mock
    private ActionsToolboxFactory commonActionsToolboxFactoryInstance;
    private ManagedInstanceStub<ActionsToolboxFactory> commonActionsToolboxFactory;

    private DMNToolboxControl tested;

    @Before
    public void setup() throws Exception {
        flowActionsToolboxFactory = new ManagedInstanceStub<>(flowActionsToolboxFactoryInstance);
        commonActionsToolboxFactory = new ManagedInstanceStub<>(commonActionsToolboxFactoryInstance);
        this.tested = new DMNToolboxControl(flowActionsToolboxFactory,
                                            commonActionsToolboxFactory);
    }

    @Test
    public void testRegisterTheRightFactories() {
        final List<ActionsToolboxFactory> factories = tested.getFactories();
        assertNotNull(factories);
        assertEquals(2,
                     factories.size());
        assertEquals(flowActionsToolboxFactoryInstance,
                     factories.get(0));
        assertEquals(commonActionsToolboxFactoryInstance,
                     factories.get(1));
    }
}
