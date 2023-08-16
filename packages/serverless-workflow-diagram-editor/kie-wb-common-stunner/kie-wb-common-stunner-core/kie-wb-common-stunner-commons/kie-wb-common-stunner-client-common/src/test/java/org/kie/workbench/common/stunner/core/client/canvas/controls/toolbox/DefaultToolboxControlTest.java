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


package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    ToolboxControlImpl<ActionsToolboxFactory> toolboxControl;

    private static final String ELEMENT_UUID = "element-uuid1";
    private static final String ELEMENT_UUID_2 = "element-uuid2";
    private static final String ELEMENT_UUID_3 = "element-uuid3";
    private static final String ELEMENT_UUID_4 = "element-uuid4";
    private static final String ELEMENT_UUID_5 = "element-uuid5";

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(toolboxControl.getCanvasHandler()).thenReturn(canvasHandler);
        flowActionsToolboxFactory = spy(new ManagedInstanceStub<>(flowActionsToolboxFactoryInstance));
        morphActionsToolboxFactory = spy(new ManagedInstanceStub<>(morphActionsToolboxFactoryInstance));
        commonActionsToolboxFactory = spy(new ManagedInstanceStub<>(commonActionsToolboxFactoryInstance));
        this.tested = new DefaultToolboxControl(flowActionsToolboxFactory,
                                                morphActionsToolboxFactory,
                                                commonActionsToolboxFactory,
                                                toolboxControl);
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

    @Test
    public void testHandleSelectionEvent() {
        CanvasSelectionEvent canvasSelectionEvent = new CanvasSelectionEvent(canvasHandler,
                                                                             Arrays.asList(ELEMENT_UUID,
                                                                                           ELEMENT_UUID_2,
                                                                                           ELEMENT_UUID_3,
                                                                                           ELEMENT_UUID_4,
                                                                                           ELEMENT_UUID_5));
        tested.handleCanvasSelectionEvent(canvasSelectionEvent);
        assertEquals(5, canvasSelectionEvent.getIdentifiers().size());
        assertSame(canvasHandler, canvasSelectionEvent.getCanvasHandler());
        assertSame(canvasHandler, toolboxControl.getCanvasHandler());

        canvasSelectionEvent = new CanvasSelectionEvent(canvasHandler, Arrays.asList(ELEMENT_UUID));
        tested.handleCanvasSelectionEvent(canvasSelectionEvent);
        assertEquals(1, canvasSelectionEvent.getIdentifiers().size());
        assertSame(canvasHandler, canvasSelectionEvent.getCanvasHandler());
        assertSame(canvasHandler, toolboxControl.getCanvasHandler());
        verify(toolboxControl,times(1)).show(ELEMENT_UUID);
    }
}
