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


package org.kie.workbench.common.stunner.sw.autolayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ait.lienzo.client.core.layout.Layout;
import com.ait.lienzo.client.core.layout.graph.Vertex;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.DirectGraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.sw.autolayout.lienzo.LienzoAutoLayout;
import org.kie.workbench.common.stunner.sw.definition.DataConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.DefaultConditionTransition;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.OperationState;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.Workflow;
import org.kie.workbench.common.stunner.sw.marshall.BaseMarshallingTest;
import org.kie.workbench.common.stunner.sw.marshall.Marshaller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(LienzoMockitoTestRunner.class)
public class AutoLayoutTest extends BaseMarshallingTest {

    private static final String WORKFLOW_ID = "workflow1";
    private static final String WORKFLOW_NAME = "Workflow1";

    @Override
    protected Workflow createWorkflow() {
        // States
        SwitchState chooseOnLanguage = new SwitchState();
        chooseOnLanguage.setName("ChooseOnLanguage");

        InjectState greetInEnglish = new InjectState();
        greetInEnglish.setName("GreetInEnglish");

        InjectState greetInSpanish = new InjectState();
        greetInSpanish.setName("GreetInSpanish");

        InjectState greetInGerman = new InjectState();
        greetInGerman.setName("GreetInGerman");
        greetInGerman.setEnd(true);

        InjectState greetUnknown = new InjectState();
        greetUnknown.setName("GreetUnknown");

        InjectState figureOut = new InjectState();
        figureOut.setName("FigureOut");

        OperationState greetPerson = new OperationState();
        greetPerson.setName("GreetPerson");
        greetInGerman.setEnd(true);

        // Switch state transitions
        DefaultConditionTransition ChooseOnLanguageDefaultTransition = new DefaultConditionTransition();
        ChooseOnLanguageDefaultTransition.setTransition("GreetInEnglish");
        DataConditionTransition chooseOnLanguageEnglishTransition = new DataConditionTransition();
        chooseOnLanguageEnglishTransition.setTransition("GreetInEnglish");
        chooseOnLanguageEnglishTransition.setCondition("${ .language == \\\"English\\\" }\"");
        DataConditionTransition chooseOnLanguageSpanishTransition = new DataConditionTransition();
        chooseOnLanguageSpanishTransition.setTransition("GreetInSpanish");
        chooseOnLanguageSpanishTransition.setCondition("${ .language == \"Spanish\" }");
        DataConditionTransition chooseOnLanguageGermanTransition = new DataConditionTransition();
        chooseOnLanguageGermanTransition.setTransition("GreetInGerman");
        chooseOnLanguageGermanTransition.setCondition("${ .language == \"German\" }");
        DataConditionTransition chooseOnLanguageInformalTransition = new DataConditionTransition();
        chooseOnLanguageInformalTransition.setTransition("GreetPerson");
        chooseOnLanguageInformalTransition.setCondition("${ .language == \"Informal\" }");
        DataConditionTransition chooseOnLanguageUnknownTransition = new DataConditionTransition();
        chooseOnLanguageUnknownTransition.setTransition("GreetUnknown");
        chooseOnLanguageUnknownTransition.setCondition("${ .language == \"Unknown\" }");
        DataConditionTransition[] ChooseOnLanguageDataConditionTransitions = new DataConditionTransition[]{
                chooseOnLanguageEnglishTransition,
                chooseOnLanguageSpanishTransition,
                chooseOnLanguageGermanTransition,
                chooseOnLanguageInformalTransition,
                chooseOnLanguageUnknownTransition
        };

        // Set transitions
        chooseOnLanguage.setDefaultCondition(ChooseOnLanguageDefaultTransition);
        chooseOnLanguage.setDataConditions(ChooseOnLanguageDataConditionTransitions);
        greetInEnglish.setTransition("GreetPerson");
        greetInSpanish.setTransition("GreetPerson");
        greetUnknown.setTransition("FigureOut");
        // Backward connection
        figureOut.setTransition("ChooseOnLanguage");

        return new Workflow()
                .setId(WORKFLOW_ID)
                .setName(WORKFLOW_NAME)
                .setStart("ChooseOnLanguage")
                .setStates(new State[]{
                        chooseOnLanguage,
                        greetInEnglish,
                        greetInSpanish,
                        greetInGerman,
                        greetUnknown,
                        figureOut,
                        greetPerson
                });
    }

    @SuppressWarnings("all")
    @Override
    public void setUp() {
        super.setUp();

        unmarshallWorkflow();

        // Apply auto-layout post processing
        final String startNodeUuid = Marshaller.getStartNodeUuid((GraphImpl) getGraph());
        final String endNodeUuid = Marshaller.getEndNodeUuid((GraphImpl) getGraph());
        final Map<String, Vertex> vertices = AutoLayout.loadVertices(getGraph(),
                                                                     context.getWorkflowRootNode(),
                                                                     false);
        final LienzoAutoLayout autoLayout = new LienzoAutoLayout();
        final Layout layout = autoLayout.processGraph(getGraph(), vertices, startNodeUuid, endNodeUuid);
        final CompositeCommand.Builder layoutCommands = new CompositeCommand.Builder();
        AutoLayout.updateEdgesDirection(layout, layoutCommands);
        AutoLayout.createControlPoints(layout, layoutCommands);
        AutoLayout.hideNodeIfIsNotConnected(layout, startNodeUuid, getGraph(), context.getWorkflowRootNode());
        AutoLayout.hideNodeIfIsNotConnected(layout, endNodeUuid, getGraph(), context.getWorkflowRootNode());
        final CompositeCommand<GraphCommandExecutionContext, RuleViolation> all =
                new CompositeCommand.Builder<>()
                        .addCommand(layoutCommands.build())
                        .build();

        final DirectGraphCommandExecutionContext graphContext = builderContext.buildExecutionContext();
        all.execute(graphContext);
    }

    @Test
    public void testMoveEndNodesX() {
        Iterable<Node> nodes = (Iterable<Node>) getGraph().nodes();
        Map<String, Bounds> endBoundsBefore = new HashMap<>();
        nodes.forEach(node -> {
            if (node.getContent() instanceof View) {
                final View content = (View) node.getContent();
                if (content.getDefinition() instanceof End) {
                    End end = (End) content.getDefinition();
                    endBoundsBefore.put(end.getName(), content.getBounds());
                }
            }
        });

        // Move End nodes X
        AutoLayout.moveEndNodesX(getGraph(), AutoLayout.X_DEVIATION);

        nodes.forEach(node -> {
            if (node.getContent() instanceof View) {
                final View content = (View) node.getContent();
                if (content.getDefinition() instanceof End) {
                    End end = (End) content.getDefinition();
                    final Bounds bounds = content.getBounds();
                    final Bounds boundsBefore = endBoundsBefore.get(end.getName());

                    assertNotSame(boundsBefore, bounds);
                    assertNotEquals(boundsBefore.getX(), bounds.getX());
                }
            }
        });
    }

    @SuppressWarnings("all")
    @Test
    public void testAdjustIncomingConnectionsNonBackwardsNoControlPoints() {
        Iterable<Node> nodes = ((GraphImpl) getGraph()).nodes();
        nodes.forEach(node -> {
            if (node.getContent() instanceof View) {
                final List<Edge> inEdges = (List<Edge>) node.getInEdges().stream()
                        .filter(e -> ((Edge) e).getContent() instanceof ViewConnector)
                        .collect(Collectors.toList());

                // fix Incoming connections
                AutoLayout.adjustIncomingConnections(node, inEdges);

                for (int i = 0; i < inEdges.size(); i++) {
                    Edge edge = inEdges.get(i);
                    ViewConnector content = (ViewConnector) edge.getContent();
                    MagnetConnection targetConnection = (MagnetConnection) content.getTargetConnection().get();
                    ControlPoint[] controlPoints = content.getControlPoints();
                    Node sourceNode = edge.getSourceNode();
                    View sourceContent = (View) sourceNode.getContent();
                    Bounds sourceBounds = sourceContent.getBounds();
                    Node targetNode = edge.getTargetNode();
                    View targetContent = (View) targetNode.getContent();
                    Bounds targetBounds = targetContent.getBounds();

                    if (!AutoLayout.isBackwards(sourceBounds.getY(), targetBounds.getY())
                            && controlPoints.length == 0) {
                        checkAdjustConnectionWithoutControlPoints(i,
                                                                  sourceNode,
                                                                  targetConnection,
                                                                  inEdges);
                    }
                }
            }
        });
    }

    @SuppressWarnings("all")
    @Test
    public void testAdjustIncomingConnectionsBackwards() {
        Iterable<Node> nodes = ((GraphImpl) getGraph()).nodes();
        nodes.forEach(node -> {
            if (node.getContent() instanceof View) {
                final List<Edge> inEdges = (List<Edge>) node.getInEdges().stream()
                        .filter(e -> ((Edge) e).getContent() instanceof ViewConnector)
                        .collect(Collectors.toList());

                // fix Incoming connections
                AutoLayout.adjustIncomingConnections(node, inEdges);

                for (int i = 0; i < inEdges.size(); i++) {
                    Edge edge = inEdges.get(i);
                    ViewConnector content = (ViewConnector) edge.getContent();
                    MagnetConnection sourceConnection = (MagnetConnection) content.getSourceConnection().get();
                    MagnetConnection targetConnection = (MagnetConnection) content.getTargetConnection().get();
                    ControlPoint[] controlPoints = content.getControlPoints();
                    Node sourceNode = edge.getSourceNode();
                    View sourceContent = (View) sourceNode.getContent();
                    Bounds sourceBounds = sourceContent.getBounds();
                    Node targetNode = edge.getTargetNode();
                    View targetContent = (View) targetNode.getContent();
                    Bounds targetBounds = targetContent.getBounds();

                    if (AutoLayout.isBackwards(sourceBounds.getY(), targetBounds.getY())) {
                        if (inEdges.size() > 0) {
                            if (controlPoints.length == 0) {
                                assertFalse(sourceConnection.isAuto());
                                assertEquals(MagnetConnection.MAGNET_LEFT, sourceConnection.getMagnetIndex().getAsInt(), 0);
                            }
                            if (inEdges.size() == 1) {
                                assertFalse(targetConnection.isAuto());
                                assertEquals(MagnetConnection.MAGNET_TOP, targetConnection.getMagnetIndex().getAsInt(), 0);
                            } else {
                                assertFalse(targetConnection.isAuto());
                                assertEquals(MagnetConnection.MAGNET_CENTER, targetConnection.getMagnetIndex().getAsInt(), 0);
                            }
                        }
                    }
                }
            }
        });
    }

    @SuppressWarnings("all")
    @Test
    public void testAdjustOutgoingConnectionsNonBackwardsNoControlPoints() {
        Iterable<Node> nodes = ((GraphImpl) getGraph()).nodes();
        nodes.forEach(node -> {
            if (node.getContent() instanceof View) {
                final List<Edge> inEdges = (List<Edge>) node.getInEdges().stream()
                        .filter(e -> ((Edge) e).getContent() instanceof ViewConnector)
                        .collect(Collectors.toList());
                final List<Edge> outEdges = (List<Edge>) node.getOutEdges().stream()
                        .filter(e -> ((Edge) e).getContent() instanceof ViewConnector)
                        .collect(Collectors.toList());

                // fix Incoming connections
                AutoLayout.adjustIncomingConnections(node, inEdges);

                // fix Outgoing connections
                AutoLayout.adjustOutgoingConnections(node, inEdges, outEdges);

                if (node.getContent() instanceof View) {
                    if (outEdges.size() >= 1) {
                        for (int i = 0; i < outEdges.size(); i++) {
                            Edge edge = outEdges.get(i);
                            ViewConnector content = (ViewConnector) edge.getContent();
                            MagnetConnection targetConnection = (MagnetConnection) content.getTargetConnection().get();
                            ControlPoint[] controlPoints = content.getControlPoints();
                            Node sourceNode = edge.getSourceNode();
                            View sourceContent = (View) sourceNode.getContent();
                            Bounds sourceBounds = sourceContent.getBounds();
                            Node targetNode = edge.getTargetNode();
                            View targetContent = (View) targetNode.getContent();
                            Bounds targetBounds = targetContent.getBounds();

                            if (AutoLayout.isBackwards(sourceBounds.getY(), targetBounds.getY()) &&
                                    controlPoints.length == 0) {
                                checkAdjustConnectionWithoutControlPoints(i,
                                                                          sourceNode,
                                                                          targetConnection,
                                                                          inEdges);
                            }
                        }
                    }
                }
            }
        });
    }

    @SuppressWarnings("all")
    @Test
    public void testAdjustOutgoingConnectionsBackwardsNoCPs() {
        Iterable<Node> nodes = ((GraphImpl) getGraph()).nodes();
        nodes.forEach(node -> {
            if (node.getContent() instanceof View) {
                final List<Edge> inEdges = (List<Edge>) node.getInEdges().stream()
                        .filter(e -> ((Edge) e).getContent() instanceof ViewConnector)
                        .collect(Collectors.toList());
                final List<Edge> outEdges = (List<Edge>) node.getOutEdges().stream()
                        .filter(e -> ((Edge) e).getContent() instanceof ViewConnector)
                        .collect(Collectors.toList());

                // fix Incoming connections
                AutoLayout.adjustIncomingConnections(node, inEdges);

                // fix Outgoing connections
                AutoLayout.adjustOutgoingConnections(node, inEdges, outEdges);

                if (node.getContent() instanceof View) {
                    if (outEdges.size() >= 1) {
                        for (int i = 0; i < outEdges.size(); i++) {
                            Edge edge = outEdges.get(i);
                            ViewConnector content = (ViewConnector) edge.getContent();
                            MagnetConnection sourceConnection = (MagnetConnection) content.getSourceConnection().get();
                            MagnetConnection targetConnection = (MagnetConnection) content.getTargetConnection().get();
                            ControlPoint[] controlPoints = content.getControlPoints();
                            Node sourceNode = edge.getSourceNode();
                            View sourceContent = (View) sourceNode.getContent();
                            Bounds sourceBounds = sourceContent.getBounds();
                            Node targetNode = edge.getTargetNode();
                            View targetContent = (View) targetNode.getContent();
                            Bounds targetBounds = targetContent.getBounds();

                            if (AutoLayout.isBackwards(sourceBounds.getY(), targetBounds.getY())) {
                                if (outEdges.size() > 0) {
                                    if (controlPoints.length == 0) {
                                        assertFalse(sourceConnection.isAuto());
                                        assertEquals(MagnetConnection.MAGNET_LEFT, sourceConnection.getMagnetIndex().getAsInt(), 0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @SuppressWarnings("all")
    private void checkAdjustConnectionWithoutControlPoints(final int edgeIndex,
                                                           final Node sourceNode,
                                                           final MagnetConnection targetConnection,
                                                           final List<Edge> inEdges) {
        if (inEdges.size() == 1) {
            assertFalse(targetConnection.isAuto());
            assertEquals(MagnetConnection.MAGNET_TOP, targetConnection.getMagnetIndex().getAsInt(), 0);
        } else if (AutoLayout.isSameSource(sourceNode, inEdges, edgeIndex)) {
            assertFalse(targetConnection.isAuto());
            assertEquals(MagnetConnection.MAGNET_TOP, targetConnection.getMagnetIndex().getAsInt(), 0);
        } else {
            assertFalse(targetConnection.isAuto());
            assertEquals(MagnetConnection.MAGNET_CENTER, targetConnection.getMagnetIndex().getAsInt(), 0);
        }
    }
}
