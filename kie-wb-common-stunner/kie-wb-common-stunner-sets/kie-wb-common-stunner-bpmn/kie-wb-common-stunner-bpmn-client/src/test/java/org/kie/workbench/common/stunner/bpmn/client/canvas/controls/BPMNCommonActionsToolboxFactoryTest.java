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

package org.kie.workbench.common.stunner.bpmn.client.canvas.controls;

import java.util.Collection;
import java.util.Collections;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.forms.client.components.toolbox.FormGenerationToolboxAction;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BPMNCommonActionsToolboxFactoryTest {

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private ActionsToolboxFactory commonActionToolbox;

    @Mock
    private ToolboxAction<AbstractCanvasHandler> action1;

    private ManagedInstance<FormGenerationToolboxAction> generateFormsActions;

    @Mock
    private FormGenerationToolboxAction formGenerationToolboxAction;

    private ManagedInstance<ActionsToolboxView> views;

    @Mock
    private ActionsToolboxView view;

    private BPMNCommonActionsToolboxFactory tested;
    private Node element;

    @Before
    public void init() {
        element = new NodeImpl<>("node1");
        when(commonActionToolbox.getActions(eq(canvasHandler),
                                            eq(element))).thenReturn(Collections.singletonList(action1));
        generateFormsActions = spy(new ManagedInstanceStub<>(formGenerationToolboxAction));
        views = spy(new ManagedInstanceStub<>(view));
        tested = new BPMNCommonActionsToolboxFactory(commonActionToolbox,
                                                     generateFormsActions,
                                                     views);
    }

    @Test
    public void testNewViewInstance() {
        assertEquals(view, tested.newViewInstance());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetActionsForSupportedNode() {
        element.setContent(new ViewImpl<>(new UserTask(),
                                          BoundsImpl.build()));
        final Collection<ToolboxAction<AbstractCanvasHandler>> actions = tested.getActions(canvasHandler,
                                                                                           element);
        assertEquals(2, actions.size());
        assertTrue(actions.contains(action1));
        assertTrue(actions.contains(formGenerationToolboxAction));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetActionsForNotSupportedNode() {
        element.setContent(new ViewImpl<>(new ScriptTask(),
                                          BoundsImpl.build()));
        final Collection<ToolboxAction<AbstractCanvasHandler>> actions = tested.getActions(canvasHandler,
                                                                                           element);
        assertEquals(1, actions.size());
        assertTrue(actions.contains(action1));
        assertFalse(actions.contains(formGenerationToolboxAction));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(generateFormsActions, times(1)).destroyAll();
        verify(views, times(1)).destroyAll();
    }
}
