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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.components.toolbox.Toolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.DeleteNodeToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.Mock;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNCommonActionsToolboxFactoryTest {

    private static final String E_UUID = "e1";

    @Mock
    private DeleteNodeToolboxAction deleteNodeAction;

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

    @Mock
    private CanvasCommandManager<AbstractCanvasHandler> commandManager;

    @Mock
    private DefaultCanvasCommandFactory commandFactory;

    @Mock
    private ManagedInstance<DeleteNodeToolboxAction> deleteNodeActions;

    @Mock
    private ReadOnlyProvider readonlyProvider;

    @Mock
    private ManagedInstance<DMNEditDRDToolboxAction> editDRDToolboxActions;

    private DMNCommonActionsToolboxFactory tested;

    @Before
    public void setup() {
        when(element.getUUID()).thenReturn(E_UUID);
        when(element.asNode()).thenReturn(element);
        editDecisionToolboxAction = new ManagedInstanceStub<>(editDecisionToolboxActionInstance);
        editBusinessKnowledgeModelToolboxAction = new ManagedInstanceStub<>(editBusinessKnowledgeModelToolboxActionInstance);
        view = new ManagedInstanceStub<>(viewInstance);
        this.tested = spy(new DMNCommonActionsToolboxFactory(editDecisionToolboxAction,
                                                             editBusinessKnowledgeModelToolboxAction,
                                                             editDRDToolboxActions,
                                                             view,
                                                             commandManager,
                                                             commandFactory,
                                                             deleteNodeActions,
                                                             readonlyProvider));

        doReturn(Collections.singleton(deleteNodeAction)).
                when(tested).superGetActions(eq(canvasHandler),
                                             any(Element.class));
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
        assertEquals(2,
                     actionsToolbox.size());
        assertEquals(deleteNodeAction,
                     actionsToolbox.iterator().next());
        verify(viewInstance,
               times(1)).init(eq(actionsToolbox));
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
        assertEquals(3,
                     actionsToolbox.size());
        final Iterator<ToolboxAction> actionsIt = actionsToolbox.iterator();
        assertEquals(deleteNodeAction,
                     actionsIt.next());
        assertEquals(editDecisionToolboxActionInstance,
                     actionsIt.next());
        assertTrue(actionsIt.hasNext());
        verify(viewInstance,
               times(1)).init(eq(actionsToolbox));
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
        assertEquals(3,
                     actionsToolbox.size());
        final Iterator<ToolboxAction> actionsIt = actionsToolbox.iterator();
        assertEquals(deleteNodeAction,
                     actionsIt.next());
        assertEquals(editBusinessKnowledgeModelToolboxActionInstance,
                     actionsIt.next());
        assertTrue(actionsIt.hasNext());
        verify(viewInstance,
               times(1)).init(eq(actionsToolbox));
    }

    @Test
    public void testIsAllowed() {

        final Node node = mock(Node.class);
        final Definition content = mock(Definition.class);
        final DecisionService decisionService = mock(DecisionService.class);
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(decisionService);

        final boolean actual = tested.isAllowed(canvasHandler, node);

        assertTrue(actual);
        verify(tested, never()).superIsAllowed(canvasHandler, node);
    }

    @Test
    public void testIsAllowedWhenIsNotDecisionService() {

        final Node node = mock(Node.class);
        final Definition content = mock(Definition.class);
        final Object def = mock(Object.class);
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(def);
        doReturn(false).when(tested).superIsAllowed(canvasHandler, node);

        final boolean actual = tested.isAllowed(canvasHandler, node);

        assertFalse(actual);
    }

    @Test
    public void testAddEditDecisionAction() {

        final List<ToolboxAction<AbstractCanvasHandler>> actions = new ArrayList<>();

        final Element element = mock(Element.class);
        final Node node = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final Decision decision = mock(Decision.class);
        when(element.asNode()).thenReturn(node);
        when(element.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(decision);

        tested.addEditAction(element, actions);

        assertEquals(1, actions.size());
        assertTrue(DMNEditDecisionToolboxAction.class.isInstance(actions.get(0)));
    }

    @Test
    public void testAddEditBusinessKnowledgeModelAction() {

        final List<ToolboxAction<AbstractCanvasHandler>> actions = new ArrayList<>();

        final Element element = mock(Element.class);
        final Node node = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final BusinessKnowledgeModel bkm = mock(BusinessKnowledgeModel.class);
        when(element.asNode()).thenReturn(node);
        when(element.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(bkm);

        tested.addEditAction(element, actions);

        assertEquals(1, actions.size());
        assertTrue(DMNEditBusinessKnowledgeModelToolboxAction.class.isInstance(actions.get(0)));
    }

    @Test
    public void testAddEditWhenIsNotDecisionOrBusinessKnowledgeModelAction() {

        final List<ToolboxAction<AbstractCanvasHandler>> actions = new ArrayList<>();

        final Element element = mock(Element.class);
        final Node node = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final Object someObject = mock(Object.class);
        when(element.asNode()).thenReturn(node);
        when(element.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(someObject);

        tested.addEditAction(element, actions);

        assertEquals(0, actions.size());
    }
}
