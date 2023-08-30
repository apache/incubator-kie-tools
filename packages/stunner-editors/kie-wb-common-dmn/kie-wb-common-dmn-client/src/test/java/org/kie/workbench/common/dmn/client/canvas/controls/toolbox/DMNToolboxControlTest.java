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

package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNToolboxControlTest {

    @Mock
    private ActionsToolboxFactory flowActionsToolboxFactoryInstance;

    private ManagedInstanceStub<ActionsToolboxFactory> flowActionsToolboxFactory;

    @Mock
    private ActionsToolboxFactory commonActionsToolboxFactoryInstance;

    private ManagedInstanceStub<ActionsToolboxFactory> commonActionsToolboxFactory;

    @Mock
    private ReadOnlyProvider readonlyProvider;

    private DMNToolboxControl tested;

    @Before
    public void setup() throws Exception {
        flowActionsToolboxFactory = spy(new ManagedInstanceStub<>(flowActionsToolboxFactoryInstance));
        commonActionsToolboxFactory = spy(new ManagedInstanceStub<>(commonActionsToolboxFactoryInstance));
        this.tested = new DMNToolboxControl(flowActionsToolboxFactory,
                                            commonActionsToolboxFactory,
                                            readonlyProvider);
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

    @Test
    public void testGetFactoriesWhenIsReadOnlyDiagram() {

        when(readonlyProvider.isReadOnlyDiagram()).thenReturn(true);

        tested.getFactories();

        verify(commonActionsToolboxFactory).get();
        verify(flowActionsToolboxFactory, never()).get();
    }

    @Test
    public void testGetFactoriesWhenIsNotReadOnlyDiagram() {

        when(readonlyProvider.isReadOnlyDiagram()).thenReturn(false);

        tested.getFactories();

        verify(commonActionsToolboxFactory).get();
        verify(flowActionsToolboxFactory).get();
    }
}
