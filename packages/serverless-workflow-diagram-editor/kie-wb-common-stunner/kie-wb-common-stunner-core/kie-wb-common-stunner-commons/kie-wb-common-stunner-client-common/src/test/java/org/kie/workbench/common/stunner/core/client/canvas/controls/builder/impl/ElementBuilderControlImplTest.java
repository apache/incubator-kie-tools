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


package org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddChildNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateDockNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.AbstractElementBuilderControl.ParentAssignment;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationMessages;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.context.ContainmentContext;
import org.kie.workbench.common.stunner.core.rule.context.DockingContext;
import org.kie.workbench.common.stunner.core.rule.violations.ContainmentRuleViolation;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.rule.violations.DockingRuleViolation;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ElementBuilderControlImplTest {

    protected static final String[] DEFINITION_LABELS_ARRAY = new String[]{"label"};
    protected static final Set<String> DEFINITION_LABELS = new HashSet<>(Arrays.asList(DEFINITION_LABELS_ARRAY));
    protected static final Set<String> PARENT_LABELS = Stream.of("label").collect(Collectors.toSet());
    private static final String SHAPE_SET_ID = "SHAPE_ID";
    private ElementBuilderControlImpl elementBuilderControl;

    @Mock
    private ClientDefinitionManager clientDefinitionManager;

    @Mock
    private ClientFactoryService clientFactoryServices;

    @Mock
    private RuleManager ruleManager;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private GraphBoundsIndexer graphBoundsIndexer;

    @Mock
    private Node<View<?>, Edge> parent;

    @Mock
    private Object def = "def";

    @Mock
    private AdapterManager adapterManager;

    @Mock
    protected DefinitionAdapter<Object> definitionAdapter;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private RuleSet ruleSet;

    @Mock
    private Node node;

    private static final double X = 30;
    private static final double Y = 30;

    @Mock
    private AbstractElementBuilderControl.CommandsCallback callback;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Captor
    private ArgumentCaptor<List<Command<AbstractCanvasHandler, CanvasViolation>>> commandsCapture;

    @Mock
    private UpdateDockNodeCommand dockCommand;

    @Mock
    private AddChildNodeCommand addChildCommand;

    @Mock
    private UpdateElementPositionCommand updateDockPositionCommand;

    @Mock
    private AddNodeCommand addNodeCommand;

    @Mock
    private Edge grandParentEdge;

    @Mock
    private Node<View, Edge> grandParent;

    @Mock
    private View grandParentView;

    @Before
    public void setUp() throws Exception {
        when(clientDefinitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(definitionAdapter.getLabels(def)).thenReturn(DEFINITION_LABELS_ARRAY);
        when(canvasHandler.getRuleSet()).thenReturn(ruleSet);
        when(parent.getLabels()).thenReturn(PARENT_LABELS);
        when(parent.getInEdges()).thenReturn(Arrays.asList(grandParentEdge));
        when(grandParentEdge.getContent()).thenReturn(new Child());
        when(grandParentEdge.getSourceNode()).thenReturn(grandParent);
        when(grandParent.getContent()).thenReturn(grandParentView);
        when(grandParentView.getBounds()).thenReturn(Bounds.create(10d, 10d, 100d, 100d));

        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getShapeSetId()).thenReturn(SHAPE_SET_ID);
        when(canvasCommandFactory.updateDockNode(parent, node, true)).thenReturn(dockCommand);
        when(canvasCommandFactory.updatePosition(eq(node), any())).thenReturn(updateDockPositionCommand);
        when(canvasCommandFactory.addChildNode(any(), eq(node), eq(SHAPE_SET_ID))).thenReturn(addChildCommand);
        when(canvasCommandFactory.addNode(eq(node), eq(SHAPE_SET_ID))).thenReturn(addNodeCommand);
        elementBuilderControl = new ElementBuilderControlImpl(clientDefinitionManager,
                                                              clientFactoryServices,
                                                              ruleManager,
                                                              canvasCommandFactory,
                                                              mock(ClientTranslationMessages.class),
                                                              graphBoundsIndexer);
        elementBuilderControl.init(canvasHandler);
    }

    @Test
    public void getParentAssignmentDocking() {

        //test docking
        ArgumentCaptor<DockingContext> dockingContextCaptor = forClass(DockingContext.class);
        when(ruleManager.evaluate(eq(ruleSet), dockingContextCaptor.capture())).thenReturn(new DefaultRuleViolations());

        ParentAssignment parentAssignment = elementBuilderControl.getParentAssignment(parent, def);
        assertEquals(dockingContextCaptor.getValue().getCandidateRoles(), DEFINITION_LABELS);
        assertEquals(dockingContextCaptor.getValue().getParentRoles(), PARENT_LABELS);
        assertEquals(parentAssignment, ParentAssignment.DOCKING);

        //test containment
        ArgumentCaptor<ContainmentContext> containmentContextArgumentCaptor = forClass(ContainmentContext.class);
        DefaultRuleViolations dockingRuleViolations = new DefaultRuleViolations();
        dockingRuleViolations.addViolation(new DockingRuleViolation("", ""));
        when(ruleManager.evaluate(eq(ruleSet), containmentContextArgumentCaptor.capture())).thenAnswer(arg -> {
            if (arg.getArguments()[1] instanceof ContainmentContext) {
                return new DefaultRuleViolations();
            } else {
                return dockingRuleViolations;
            }
        });

        parentAssignment = elementBuilderControl.getParentAssignment(parent, def);
        assertEquals(parentAssignment, ParentAssignment.CONTAINMENT);

        //testing none
        DefaultRuleViolations allViolations = new DefaultRuleViolations();
        allViolations.addViolation(new DockingRuleViolation("", ""));
        allViolations.addViolation(new ContainmentRuleViolation("", ""));
        when(ruleManager.evaluate(eq(ruleSet), any())).thenReturn(allViolations);
        parentAssignment = elementBuilderControl.getParentAssignment(parent, def);
        assertEquals(parentAssignment, ParentAssignment.NONE);
    }

    @Test
    public void getElementCommandsDocked() {
        ParentAssignment parentAssignment = ParentAssignment.DOCKING;
        elementBuilderControl.getElementCommands(node, parent, parentAssignment, X, Y, callback);
        verify(callback).onComplete(any(), commandsCapture.capture());
        List<Command<AbstractCanvasHandler, CanvasViolation>> commands = commandsCapture.getValue();
        assertTrue(commands.get(0) instanceof AddChildNodeCommand);
        assertTrue(commands.get(1) instanceof UpdateElementPositionCommand);
        assertTrue(commands.get(2) instanceof UpdateDockNodeCommand);

        verify(canvasCommandFactory).addChildNode(grandParent, node, SHAPE_SET_ID);
        ArgumentCaptor<Point2D> positionCaptor = ArgumentCaptor.forClass(Point2D.class);
        verify(canvasCommandFactory).updatePosition(eq(node), positionCaptor.capture());
        assertEquals(positionCaptor.getValue(), new Point2D(X, Y));
        verify(canvasCommandFactory).addChildNode(grandParent, node, SHAPE_SET_ID);
    }

    @Test
    public void getElementCommandsContainment() {
        ParentAssignment parentAssignment = ParentAssignment.CONTAINMENT;
        elementBuilderControl.getElementCommands(node, parent, parentAssignment, X, Y, callback);
        verify(callback).onComplete(any(), commandsCapture.capture());
        List<Command<AbstractCanvasHandler, CanvasViolation>> commands = commandsCapture.getValue();
        assertTrue(commands.get(0) instanceof AddChildNodeCommand);
        assertTrue(commands.get(1) instanceof UpdateElementPositionCommand);

        verify(canvasCommandFactory).addChildNode(parent, node, SHAPE_SET_ID);
        ArgumentCaptor<Point2D> positionCaptor = ArgumentCaptor.forClass(Point2D.class);
        verify(canvasCommandFactory).updatePosition(eq(node), positionCaptor.capture());
        assertEquals(positionCaptor.getValue(), new Point2D(X, Y));
    }

    @Test
    public void getElementCommandsNoParent() {
        ParentAssignment parentAssignment = ParentAssignment.NONE;
        elementBuilderControl.getElementCommands(node, parent, parentAssignment, X, Y, callback);
        verify(callback).onComplete(any(), commandsCapture.capture());
        List<Command<AbstractCanvasHandler, CanvasViolation>> commands = commandsCapture.getValue();
        assertTrue(commands.get(0) instanceof AddNodeCommand);
        assertTrue(commands.get(1) instanceof UpdateElementPositionCommand);

        verify(canvasCommandFactory).addNode(node, SHAPE_SET_ID);
        ArgumentCaptor<Point2D> positionCaptor = ArgumentCaptor.forClass(Point2D.class);
        verify(canvasCommandFactory).updatePosition(eq(node), positionCaptor.capture());
        assertEquals(positionCaptor.getValue(), new Point2D(X, Y));
    }
}