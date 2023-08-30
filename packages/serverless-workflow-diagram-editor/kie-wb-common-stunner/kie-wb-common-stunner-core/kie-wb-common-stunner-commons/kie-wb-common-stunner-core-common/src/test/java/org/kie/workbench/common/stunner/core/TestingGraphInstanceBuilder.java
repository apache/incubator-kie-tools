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


package org.kie.workbench.common.stunner.core;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;
import org.kie.workbench.common.stunner.core.rule.impl.EdgeOccurrences;
import org.kie.workbench.common.stunner.core.rule.impl.Occurrences;

/**
 * An utility class for testing scope that provides some "real" (not mocked) graph's initial
 * structure, with different nodes and connectors.
 */
public class TestingGraphInstanceBuilder {

    public static final String DEF0_ID = "def0";
    public static final String[] DEF0_LABELS = new String[]{"label0", "all"};
    //public static final Set<String> DEF0_LABELS = Collections.singleton("label0");
    public static final String DEF1_ID = "def1";
    public static final String[] DEF1_LABELS = new String[]{"label1", "all"};
    public static final String DEF2_ID = "def2";
    public static final String[] DEF2_LABELS = new String[]{"label2", "dockLabel", "all"};
    public static final String DEF3_ID = "def3";
    public static final String[] DEF3_LABELS = new String[]{"label3", "all"};
    public static final String DEF4_ID = "def4";
    public static final String[] DEF4_LABELS = new String[]{"label4", "all"};
    public static final String DEF5_ID = "def5";
    public static final String[] DEF5_LABELS = new String[]{"label4", "dockLabel", "all"};

    public static final String EDGE1_ID = "edge11d";
    public static final String EDGE2_ID = "edge21d";
    public static final String EDGE3_ID = "edge24d";
    public static final String PARENT_NODE_UUID = "parent1";
    public static final String START_NODE_UUID = "node1";
    public static final String INTERM_NODE_UUID = "node2";
    public static final String END_NODE_UUID = "node3";
    public static final String DOCKED_NODE_UUID = "node4";
    public static final String EDGE1_UUID = "edge1";
    public static final String EDGE2_UUID = "edge2";
    public static final String EDGE3_UUID = "edge3";
    public static final String CONTAINER_NODE_UUID = "container1";

    public static class TestGraph {

        public Graph graph;
    }

    /**
     * **********
     * * Graph1 *
     * **********
     * <p>
     * Structure:
     * startNode --(edge1)--> intermNode --(edge2)--> endNode
     */
    public static class TestGraph1 extends TestGraph {

        public Object startNodeBean;
        public Node startNode;
        public Object intermNodeBean;
        public Node intermNode;
        public Object endNodeBean;
        public Node endNode;
        public Object edge1Bean;
        public Edge edge1;
        public Object edge2Bean;
        public Edge edge2;
        public int evaluationsCount;
    }

    public static TestGraph1 newGraph1(final TestingGraphMockHandler handler) {
        return buildTestGraph1(handler);
    }

    /**
     * **********
     * * Graph2 *
     * **********
     * <p>
     * Structure:
     * -                 parentNode
     * --------------------------------------------
     * |                     |                     |
     * startNode --(edge1)--> intermNode --(edge2)--> endNode
     */
    public static class TestGraph2 extends TestGraph {

        public Node parentNode;
        public Node startNode;
        public Node intermNode;
        public Node endNode;
        public Edge edge1;
        public Edge edge2;
        public int evaluationsCount;
    }

    public static TestGraph2 newGraph2(final TestingGraphMockHandler handler) {
        return buildTestGraph2(handler);
    }

    /**
     * **********
     * * Graph3 *
     * **********
     * <p>
     * Structure:
     * -                       parentNode
     * ----------------------------------------------------------------------
     * |                       containerNode                                 |
     * |   -----------------------------------------------------------       |
     * |   | startNode --(edge1)--> intermNode --(edge2)--> endNode  |       |
     * |   -----------------------------------------------------------       |
     * |                                                                     |
     * ----------------------------------------------------------------------
     */

    public static class TestGraph3 extends TestGraph {

        public Node parentNode;
        public Node containerNode;
        public Node startNode;
        public Node intermNode;
        public Node endNode;
        public Edge edge1;
        public Edge edge2;
    }

    public static TestGraph3 newGraph3(final TestingGraphMockHandler handler) {
        return buildTestGraph3(handler);
    }

    /**
     * **********
     * * Graph4 *
     * **********
     * <p>
     * Structure:
     * -                 parentNode
     * --------------------------------------------
     * |                     |                     |
     * startNode --(edge1)--> intermNode --(edge2)--> endNode
     * |
     * (edge3)
     * |
     * dockedNode
     */
    public static class TestGraph4 extends TestGraph2 {

        public Node dockedNode;

        public Edge edge3;

        public TestGraph4(TestGraph2 testGraph2) {

            graph = testGraph2.graph;
            parentNode = testGraph2.parentNode;
            startNode = testGraph2.startNode;
            intermNode = testGraph2.intermNode;
            endNode = testGraph2.endNode;
            edge1 = testGraph2.edge1;
            edge2 = testGraph2.edge2;
            evaluationsCount = testGraph2.evaluationsCount;
        }
    }

    public static TestGraph4 newGraph4(final TestingGraphMockHandler handler) {
        return buildTestGraph4(handler);
    }

    /**
     * **********
     * * Graph5 *
     * **********
     * <p>
     * Structure:
     * -                 parentNode
     * ----------------------------------------------------------------
     * |                     |                                        |
     * startNode --(edge1)--> intermNode --(edge2)--> intermNode   endNode
     */
    public static class TestGraph5 extends TestGraph {

        public Node parentNode;
        public Node startNode;
        public Node intermNode;
        public Node endNode;
        public Edge edge1;
        public Edge edge2;
        public int evaluationsCount;
    }

    public static TestGraph5 newGraph5(final TestingGraphMockHandler handler) {
        return buildTestGraph5(handler);
    }

    /*
     ************ PRIVATE BUILDER METHODS ******************
     */

    private static TestGraph1 buildTestGraph1(final TestingGraphMockHandler graphTestHandler) {
        TestGraph1 result = new TestGraph1();
        result.graph = graphTestHandler.graph;
        result.startNodeBean = graphTestHandler.newDef(DEF1_ID,
                                                       Optional.of(DEF1_LABELS));
        result.startNode =
                graphTestHandler.newNode(START_NODE_UUID,
                                         Optional.of(result.startNodeBean));
        result.intermNodeBean = graphTestHandler.newDef(DEF2_ID,
                                                        Optional.of(DEF2_LABELS));
        result.intermNode =
                graphTestHandler.newNode(INTERM_NODE_UUID,
                                         Optional.of(result.intermNodeBean));
        result.endNodeBean = graphTestHandler.newDef(DEF3_ID,
                                                     Optional.of(DEF3_LABELS));
        result.endNode =
                graphTestHandler.newNode(END_NODE_UUID,
                                         Optional.of(result.endNodeBean));
        result.edge1Bean = graphTestHandler.newDef(EDGE1_ID,
                                                   Optional.empty());
        result.edge1 =
                graphTestHandler.newEdge(EDGE1_UUID,
                                         Optional.of(result.edge1Bean));
        result.edge2Bean = graphTestHandler.newDef(EDGE2_UUID,
                                                   Optional.empty());
        result.edge2 =
                graphTestHandler.newEdge(EDGE2_UUID,
                                         Optional.of(result.edge2Bean));
        graphTestHandler
                .addEdge(result.edge1,
                         result.startNode)
                .connectTo(result.edge1,
                           result.intermNode)
                .addEdge(result.edge2,
                         result.intermNode)
                .connectTo(result.edge2,
                           result.endNode);
        result.evaluationsCount = 14;
        return result;
    }

    @SuppressWarnings("unchecked")
    public static void createDefaultRulesForGraph1(RuleSet ruleSet) {
        List rules = (List) ruleSet.getRules();
        String def1Label = "label1";
        String def2Label = "label2";
        rules.add(new CanConnect("connectionFromStartToIntermediateForEdge1",
                                 EDGE1_ID,
                                 Collections.singletonList(new CanConnect.PermittedConnection(def1Label,
                                                                                              def2Label))));
        rules.add(new EdgeOccurrences("Edge1Cardinality",
                                      EDGE1_ID,
                                      def1Label,
                                      EdgeCardinalityContext.Direction.OUTGOING,
                                      -1,
                                      -1));
        rules.add(new Occurrences("graphCardinalityForDef1",
                                  def1Label,
                                  -1,
                                  -1));
        rules.add(new Occurrences("graphCardinalityForDef2",
                                  def2Label,
                                  -1,
                                  -1));
    }

    private static TestGraph2 buildTestGraph2(final TestingGraphMockHandler graphTestHandler) {
        TestGraph2 result = new TestGraph2();
        result.graph = graphTestHandler.graph;
        result.parentNode =
                graphTestHandler.newNode(PARENT_NODE_UUID,
                                         DEF0_ID,
                                         Optional.of(DEF0_LABELS));
        result.startNode =
                graphTestHandler.newNode(START_NODE_UUID,
                                         DEF1_ID,
                                         Optional.of(DEF1_LABELS));
        result.intermNode =
                graphTestHandler.newNode(INTERM_NODE_UUID,
                                         DEF2_ID,
                                         Optional.of(DEF2_LABELS));
        result.endNode =
                graphTestHandler.newNode(END_NODE_UUID,
                                         DEF3_ID,
                                         Optional.of(DEF3_LABELS));
        result.edge1 =
                graphTestHandler.newEdge(EDGE1_UUID,
                                         EDGE1_ID,
                                         Optional.empty());
        result.edge2 =
                graphTestHandler.newEdge(EDGE2_UUID,
                                         EDGE2_ID,
                                         Optional.empty());
        graphTestHandler
                .setChild(result.parentNode,
                          result.startNode)
                .setChild(result.parentNode,
                          result.intermNode)
                .setChild(result.parentNode,
                          result.endNode)
                .addEdge(result.edge1,
                         result.startNode)
                .connectTo(result.edge1,
                           result.intermNode)
                .addEdge(result.edge2,
                         result.intermNode)
                .connectTo(result.edge2,
                           result.endNode);
        result.evaluationsCount = 19;
        return result;
    }

    private static TestGraph3 buildTestGraph3(final TestingGraphMockHandler graphTestHandler) {
        TestGraph3 result = new TestGraph3();
        result.graph = graphTestHandler.graph;
        result.parentNode =
                graphTestHandler.newNode(PARENT_NODE_UUID,
                                         DEF0_ID,
                                         Optional.of(DEF0_LABELS));
        result.containerNode =
                graphTestHandler.newNode(CONTAINER_NODE_UUID,
                                         DEF4_ID,
                                         Optional.of(DEF4_LABELS));
        result.startNode =
                graphTestHandler.newNode(START_NODE_UUID,
                                         DEF1_ID,
                                         Optional.of(DEF1_LABELS));
        result.intermNode =
                graphTestHandler.newNode(INTERM_NODE_UUID,
                                         DEF2_ID,
                                         Optional.of(DEF2_LABELS));
        result.endNode =
                graphTestHandler.newNode(END_NODE_UUID,
                                         DEF3_ID,
                                         Optional.of(DEF3_LABELS));
        result.edge1 =
                graphTestHandler.newEdge(EDGE1_UUID,
                                         EDGE1_ID,
                                         Optional.empty());
        result.edge2 =
                graphTestHandler.newEdge(EDGE2_UUID,
                                         EDGE2_ID,
                                         Optional.empty());
        graphTestHandler
                .setChild(result.parentNode,
                          result.containerNode)
                .setChild(result.containerNode,
                          result.startNode)
                .setChild(result.containerNode,
                          result.intermNode)
                .setChild(result.containerNode,
                          result.endNode)
                .addEdge(result.edge1,
                         result.startNode)
                .connectTo(result.edge1,
                           result.intermNode)
                .addEdge(result.edge2,
                         result.intermNode)
                .connectTo(result.edge2,
                           result.endNode);

        return result;
    }

    private static TestGraph4 buildTestGraph4(final TestingGraphMockHandler graphTestHandler) {
        TestGraph4 result = new TestGraph4(buildTestGraph2(graphTestHandler));

        result.dockedNode = graphTestHandler.newNode(DOCKED_NODE_UUID,
                                                     DEF5_ID,
                                                     Optional.of(DEF5_LABELS));

        result.edge3 = graphTestHandler.newEdge(EDGE3_UUID, EDGE3_ID, Optional.of(DEF4_LABELS));
        graphTestHandler.dockTo(result.intermNode, result.dockedNode);
        graphTestHandler.setChild(result.parentNode, result.dockedNode);

        return result;
    }

    private static TestGraph5 buildTestGraph5(final TestingGraphMockHandler graphTestHandler) {
        TestGraph5 result = new TestGraph5();
        result.graph = graphTestHandler.graph;
        result.parentNode =
                graphTestHandler.newNode(PARENT_NODE_UUID,
                                         DEF0_ID,
                                         Optional.of(DEF0_LABELS));
        result.startNode =
                graphTestHandler.newNode(START_NODE_UUID,
                                         DEF1_ID,
                                         Optional.of(DEF1_LABELS));
        result.intermNode =
                graphTestHandler.newNode(INTERM_NODE_UUID,
                                         DEF2_ID,
                                         Optional.of(DEF2_LABELS));
        result.endNode =
                graphTestHandler.newNode(END_NODE_UUID,
                                         DEF3_ID,
                                         Optional.of(DEF3_LABELS));
        result.edge1 =
                graphTestHandler.newEdge(EDGE1_UUID,
                                         EDGE1_ID,
                                         Optional.empty());
        result.edge2 =
                graphTestHandler.newEdge(EDGE2_UUID,
                                         EDGE2_ID,
                                         Optional.empty());
        graphTestHandler
                .setChild(result.parentNode,
                          result.startNode)
                .setChild(result.parentNode,
                          result.intermNode)
                .setChild(result.parentNode,
                          result.endNode)
                .addEdge(result.edge1,
                         result.startNode)
                .connectTo(result.edge1,
                           result.intermNode)
                .addEdge(result.edge2,
                         result.intermNode)
                .connectTo(result.edge2,
                           result.intermNode);
        result.evaluationsCount = 19;
        return result;
    }
}
