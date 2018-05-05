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

package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultToolboxControlTest {

    @Mock
    private ActionsToolboxFactory flowActionsToolboxFactoryInstance;
    private ManagedInstanceStub<ActionsToolboxFactory> flowActionsToolboxFactory;

    @Mock
    private ActionsToolboxFactory morphActionsToolboxFactoryInstance;
    private ManagedInstanceStub<ActionsToolboxFactory> morphActionsToolboxFactory;

    @Mock
    private ActionsToolboxFactory commonActionsToolboxFactoryInstance;
    private ManagedInstanceStub<ActionsToolboxFactory> commonActionsToolboxFactory;

    private DefaultToolboxControl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        flowActionsToolboxFactory = spy(new ManagedInstanceStub<>(flowActionsToolboxFactoryInstance));
        morphActionsToolboxFactory = spy(new ManagedInstanceStub<>(morphActionsToolboxFactoryInstance));
        commonActionsToolboxFactory = spy(new ManagedInstanceStub<>(commonActionsToolboxFactoryInstance));
        this.tested = new DefaultToolboxControl(flowActionsToolboxFactory,
                                                morphActionsToolboxFactory,
                                                commonActionsToolboxFactory);
    }

    @Test
    public void testGetTheRightFactories() {
        final List<ActionsToolboxFactory> factories = this.tested.getFactories();
        assertEquals(3,
                     factories.size());
        assertTrue(factories.contains(flowActionsToolboxFactoryInstance));
        assertTrue(factories.contains(morphActionsToolboxFactoryInstance));
        assertTrue(factories.contains(commonActionsToolboxFactoryInstance));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(flowActionsToolboxFactory, times(1)).destroyAll();
        verify(morphActionsToolboxFactory, times(1)).destroyAll();
        verify(commonActionsToolboxFactory, times(1)).destroyAll();
    }
}
