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

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.Toolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.DeleteNodeAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNCommonActionsToolboxFactoryTest {

    private static final String E_UUID = "e1";

    @Mock
    private ActionsToolboxFactory commonActionsToolboxFactory;

    @Mock
    private DeleteNodeAction deleteNodeAction;

    @Mock
    private DMNEditDecisionToolboxAction editDecisionToolboxActionInstance;
    private ManagedInstanceStub<DMNEditDecisionToolboxAction> editDecisionToolboxAction;

    @Mock
    private DMNEditBusinessKnowledgeModelToolboxAction editBusinessKnowledgeModelToolboxActionInstance;
    private ManagedInstanceStub<DMNEditBusinessKnowledgeModelToolboxAction> editBusinessKnowledgeModelToolboxAction;

    @Mock
    private ActionsToolboxView viewInstance;
    private ManagedInstanceStub<ActionsToolboxView> view;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Node element;

    private DMNCommonActionsToolboxFactory tested;

    @Before
    public void setup() throws Exception {
        when(element.getUUID()).thenReturn(E_UUID);
        when(element.asNode()).thenReturn(element);
        when(commonActionsToolboxFactory.getActions(eq(canvasHandler),
                                                    any(Element.class)))
                .thenReturn(Collections.singleton(deleteNodeAction));
        editDecisionToolboxAction = new ManagedInstanceStub<>(editDecisionToolboxActionInstance);
        editBusinessKnowledgeModelToolboxAction = new ManagedInstanceStub<>(editBusinessKnowledgeModelToolboxActionInstance);
        view = new ManagedInstanceStub<>(viewInstance);
        this.tested = new DMNCommonActionsToolboxFactory(commonActionsToolboxFactory,
                                                         editDecisionToolboxAction,
                                                         editBusinessKnowledgeModelToolboxAction,
                                                         view);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildToolboxForNoDecisionType() {
        final Optional<Toolbox<?>> _toolbox =
                tested.build(canvasHandler,
                             element);
        assertTrue(_toolbox.isPresent());
        Toolbox<?> toolbox = _toolbox.get();
        assertTrue(toolbox instanceof ActionsToolbox);
        final ActionsToolbox actionsToolbox = (ActionsToolbox) toolbox;
        assertEquals(E_UUID,
                     actionsToolbox.getElementUUID());
        assertEquals(1,
                     actionsToolbox.size());
        assertEquals(deleteNodeAction,
                     actionsToolbox.iterator().next());
        verify(viewInstance,
               times(1)).init(eq(actionsToolbox));
        verify(viewInstance,
               times(1)).addButton(any(Glyph.class),
                                   anyString(),
                                   any(Consumer.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildToolboxForDecisionType() {
        final Node<View<Decision>, Edge> decisionNode =
                new NodeImpl<>("decisionNode1");
        final Decision decision = new Decision();
        final Bounds bounds = Bounds.create(0d, 0d, 100d, 150d);
        final View<Decision> nodeContent = new ViewImpl<>(decision,
                                                          bounds);
        decisionNode.setContent(nodeContent);
        final Optional<Toolbox<?>> _toolbox = tested.build(canvasHandler,
                                                           decisionNode);
        assertTrue(_toolbox.isPresent());
        Toolbox<?> toolbox = _toolbox.get();
        assertTrue(toolbox instanceof ActionsToolbox);
        final ActionsToolbox actionsToolbox = (ActionsToolbox) toolbox;
        assertEquals("decisionNode1",
                     actionsToolbox.getElementUUID());
        assertEquals(2,
                     actionsToolbox.size());
        final Iterator<ToolboxAction> actionsIt = actionsToolbox.iterator();
        assertEquals(deleteNodeAction,
                     actionsIt.next());
        assertEquals(editDecisionToolboxActionInstance,
                     actionsIt.next());
        assertFalse(actionsIt.hasNext());
        verify(viewInstance,
               times(1)).init(eq(actionsToolbox));
        verify(viewInstance,
               times(2)).addButton(any(Glyph.class),
                                   anyString(),
                                   any(Consumer.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildToolboxForBusinessKnowledgeModelType() {
        final Node<View<BusinessKnowledgeModel>, Edge> bkmNode =
                new NodeImpl<>("bkmNode1");
        final BusinessKnowledgeModel bkm = new BusinessKnowledgeModel();
        final Bounds bounds = Bounds.create(0d, 0d, 100d, 150d);
        final View<BusinessKnowledgeModel> nodeContent = new ViewImpl<>(bkm,
                                                                        bounds);
        bkmNode.setContent(nodeContent);
        final Optional<Toolbox<?>> _toolbox = tested.build(canvasHandler,
                                                           bkmNode);
        assertTrue(_toolbox.isPresent());
        Toolbox<?> toolbox = _toolbox.get();
        assertTrue(toolbox instanceof ActionsToolbox);
        final ActionsToolbox actionsToolbox = (ActionsToolbox) toolbox;
        assertEquals("bkmNode1",
                     actionsToolbox.getElementUUID());
        assertEquals(2,
                     actionsToolbox.size());
        final Iterator<ToolboxAction> actionsIt = actionsToolbox.iterator();
        assertEquals(deleteNodeAction,
                     actionsIt.next());
        assertEquals(editBusinessKnowledgeModelToolboxActionInstance,
                     actionsIt.next());
        assertFalse(actionsIt.hasNext());
        verify(viewInstance,
               times(1)).init(eq(actionsToolbox));
        verify(viewInstance,
               times(2)).addButton(any(Glyph.class),
                                   anyString(),
                                   any(Consumer.class));
    }
}
