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

package org.kie.workbench.common.stunner.core;

import java.util.Optional;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;

import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL0_EDGE1;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL0_EDGE2;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL0_EDGE3;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL0_END_NODE;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL0_NODE1;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL0_NODE2;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL0_START_NODE;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL1_EDGE1;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL1_NODE1;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL1_NODE2;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL1_SUB_PROCESS1;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL2_EDGE1;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL2_NODE1;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL2_NODE2;
import static org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder2.NODES.LEVEL2_SUB_PROCESS1;

/**
 * Utility class for having some testing graphs that can be created by giving the graph nodes contents.
 * This class is independent from the TestingGraphInstanceBuilder
 */
public class TestingGraphInstanceBuilder2 {

    public static final String ROOT_UUID = "ROOT_UUID";

    public enum NODES {
        LEVEL0_START_NODE("level0StartNode"),
        LEVEL0_END_NODE("level0EndNode"),

        LEVEL0_NODE1("level0Node1"),
        LEVEL0_NODE2("level0Node2"),

        LEVEL0_EDGE1("level0Edge1"),
        LEVEL0_EDGE2("level0Edge2"),
        LEVEL0_EDGE3("level0Edge3"),

        LEVEL1_SUB_PROCESS1("level1SubProcess1"),
        LEVEL1_NODE1("level1Node1"),
        LEVEL1_NODE2("level1Node2"),
        LEVEL1_EDGE1("level1Edge1"),

        LEVEL2_SUB_PROCESS1("level2SubProcess1"),
        LEVEL2_NODE1("level2Node1"),
        LEVEL2_NODE2("level2Node2"),
        LEVEL2_EDGE1("level2Edge1");

        private final String uuid;

        NODES(String uuid) {
            this.uuid = uuid;
        }

        public String uuid() {
            return uuid;
        }

        public String nodeName() {
            return uuid + "Name";
        }
    }

    /**
     * **********
     * * Level0Graph
     * **********
     * <p>
     * Structure:
     * -                                    parentNode
     * -----------------------------------------------------------------------------------------------------------------
     *          |                              |                               |                               |
     * level0StartNode --(level0Edge1)--> level0Node1 --(level0Edge2)--> level0Node2 ---(level0Edge3) --> level0EndNode
     * <p>
     * -----------------------------------------------------------------------------------------------------------------
     */
    public static class Level0Graph {

        public Graph graph;
        public Node parentNode;
        public Node level0StartNode;
        public Node level0Node1;
        public Node level0Node2;
        public Node level0EndNode;
        public Edge level0Edge1;
        public Edge level0Edge2;
        public Edge level0Edge3;
    }

    public static Level0Graph buildLevel0Graph(TestingGraphMockHandler graphTestHandler,
                                               Object parentNodeDef,
                                               Object level0StartNodeDef,
                                               Object level0Node1Def,
                                               Object level0Node2Def,
                                               Object level0EndNodeDef) {
        Level0Graph level0Graph = new Level0Graph();
        initializeLevel0Graph(graphTestHandler,
                              level0Graph,
                              parentNodeDef,
                              level0StartNodeDef,
                              level0Node1Def,
                              level0Node2Def,
                              level0EndNodeDef);
        return level0Graph;
    }

    private static void initializeLevel0Graph(TestingGraphMockHandler graphTestHandler,
                                              Level0Graph level0Graph,
                                              Object parentNodeDef,
                                              Object level0StartNodeDef,
                                              Object level0Node1Def,
                                              Object level0Node2Def,
                                              Object endNodeDef) {
        level0Graph.graph = graphTestHandler.graph;
        level0Graph.parentNode =
                graphTestHandler.newNode(ROOT_UUID,
                                         Optional.of(parentNodeDef));
        level0Graph.level0StartNode =
                graphTestHandler.newNode(LEVEL0_START_NODE.uuid(),
                                         Optional.of(level0StartNodeDef));
        level0Graph.level0Node1 =
                graphTestHandler.newNode(LEVEL0_NODE1.uuid(),
                                         Optional.of(level0Node1Def));
        level0Graph.level0Node2 =
                graphTestHandler.newNode(LEVEL0_NODE2.uuid(),
                                         Optional.of(level0Node2Def));
        level0Graph.level0EndNode =
                graphTestHandler.newNode(LEVEL0_END_NODE.uuid(),
                                         Optional.of(endNodeDef));
        level0Graph.level0Edge1 =
                graphTestHandler.newEdge(LEVEL0_EDGE1.uuid(),
                                         Optional.empty());
        level0Graph.level0Edge2 =
                graphTestHandler.newEdge(LEVEL0_EDGE2.uuid(),
                                         Optional.empty());
        level0Graph.level0Edge3 =
                graphTestHandler.newEdge(LEVEL0_EDGE3.uuid(),
                                         Optional.empty());

        graphTestHandler
                .setChild(level0Graph.parentNode,
                          level0Graph.level0StartNode)
                .setChild(level0Graph.parentNode,
                          level0Graph.level0Node1)
                .setChild(level0Graph.parentNode,
                          level0Graph.level0Node2)
                .setChild(level0Graph.parentNode,
                          level0Graph.level0EndNode)
                .addEdge(level0Graph.level0Edge1,
                         level0Graph.level0StartNode)
                .connectTo(level0Graph.level0Edge1,
                           level0Graph.level0Node1)
                .addEdge(level0Graph.level0Edge2,
                         level0Graph.level0Node1)
                .connectTo(level0Graph.level0Edge2,
                           level0Graph.level0Node2)
                .addEdge(level0Graph.level0Edge3,
                         level0Graph.level0Node2)
                .connectTo(level0Graph.level0Edge3,
                           level0Graph.level0EndNode);
    }

    /**
     * **********
     * * Level1Graph
     * **********
     * <p>
     * Structure:
     * -                                                  parentNode
     * ----------------------------------------------------------------------------------------------------------------------
     *          |                                 |      |                           |                                |
     * level0StartNode --(level0Edge1)--> level0Node1 -- | ---(level0Edge2)--> level0Node2 ---(level0Edge3) --> level0EndNode
     * <p>                                               |
     *                                                   |
     *                                            level1SubProcess1
     *                               --------------------------------------------------
     *                               |   level1Node1 --(level1Edge1)--> level1Node2   |
     *                               --------------------------------------------------
     * <p>
     * ---------------------------------------------------------------------------------------------------------------------
     */
    public static class Level1Graph extends Level0Graph {

        public Node level1SubProcess1;
        public Node level1Node1;
        public Node level1Node2;
        public Edge level1Edge1;
    }

    public static Level1Graph buildLevel1Graph(TestingGraphMockHandler graphTestHandler,
                                               Object parentNodeDef,
                                               Object level0StartNodeDef,
                                               Object level0Node1Def,
                                               Object level0Node2Def,
                                               Object level0EndNodeDef,
                                               Object level1SubProcess1Def,
                                               Object level1Node1Def,
                                               Object level1Node2Def) {
        Level1Graph level1Graph = new Level1Graph();
        initializeLevel1Graph(graphTestHandler,
                              level1Graph,
                              parentNodeDef,
                              level0StartNodeDef,
                              level0Node1Def,
                              level0Node2Def,
                              level0EndNodeDef,
                              level1SubProcess1Def,
                              level1Node1Def,
                              level1Node2Def);
        return level1Graph;
    }

    private static void initializeLevel1Graph(TestingGraphMockHandler graphTestHandler,
                                              Level1Graph level1Graph,
                                              Object parentNodeDef,
                                              Object level0StartNodeDef,
                                              Object level0Node1Def,
                                              Object level0Node2Def,
                                              Object level0EndNodeDef,
                                              Object level1SubProcess1Def,
                                              Object level1Node1Def,
                                              Object level1Node2Def) {

        initializeLevel0Graph(graphTestHandler,
                              level1Graph,
                              parentNodeDef,
                              level0StartNodeDef,
                              level0Node1Def,
                              level0Node2Def,
                              level0EndNodeDef);

        level1Graph.level1SubProcess1 =
                graphTestHandler.newNode(LEVEL1_SUB_PROCESS1.uuid(),
                                         Optional.of(level1SubProcess1Def));
        level1Graph.level1Node1 =
                graphTestHandler.newNode(LEVEL1_NODE1.uuid(),
                                         Optional.of(level1Node1Def));
        level1Graph.level1Node2 =
                graphTestHandler.newNode(LEVEL1_NODE2.uuid(),
                                         Optional.of(level1Node2Def));
        level1Graph.level1Edge1 =
                graphTestHandler.newEdge(LEVEL1_EDGE1.uuid(),
                                         Optional.empty());
        graphTestHandler
                .setChild(level1Graph.parentNode,
                          level1Graph.level1SubProcess1)
                .setChild(level1Graph.level1SubProcess1,
                          level1Graph.level1Node1)
                .setChild(level1Graph.level1SubProcess1,
                          level1Graph.level1Node2)
                .addEdge(level1Graph.level1Edge1,
                         level1Graph.level1Node1)
                .connectTo(level1Graph.level1Edge1,
                           level1Graph.level1Node2);
    }

    /**
     * **********
     * * Level2Graph
     * **********
     * <p>
     * Structure:
     * -                           parentNode
     * ----------------------------------------------------------------------------------------------------------------------
     *           |                             |         |                          |                                 |
     * level0StartNode --(level0Edge1)--> level0Node1 -- | ---(level0Edge2)--> level0Node2 ---(level0Edge3) --> level0EndNode
     * <p>                                               |
     *                                                   |
     *                                              level1SubProcess1
     *                                ------------------------------------------------------------
     *                                |                                                          |
     *                                |     level1Node1 --(level1Edge1)--> level1Node2           |
     *                                |                                                          |
     *                                |                                                          |
     *                                |                   level2SubProcess1                      |
     *                                |     --------------------------------------------------   |
     *                                |     |   level2Node1 --(level2Edge1)--> level2Node2   |   |
     *                                |     --------------------------------------------------   |
     *                                |                                                          |
     *                                ------------------------------------------------------------
     *
     * <p>
     * -----------------------------------------------------------------------------------------------------------------------
     */
    public static class Level2Graph extends Level1Graph {

        public Node level2SubProcess1;
        public Node level2Node1;
        public Node level2Node2;
        public Edge level2Edge1;
    }

    public static Level2Graph buildLevel2Graph(TestingGraphMockHandler graphTestHandler,
                                               Object parentNodeDef,
                                               Object level0StartNodeDef,
                                               Object level0Node1Def,
                                               Object level0Node2Def,
                                               Object level0EndNodeDef,
                                               Object level1SubProcess1Def,
                                               Object level1Node1Def,
                                               Object level1Node2Def,
                                               Object level2SubProcess1Def,
                                               Object level2Node1Def,
                                               Object level2Node2Def) {
        Level2Graph level2Graph = new Level2Graph();
        initializeLevel2Graph(graphTestHandler,
                              level2Graph,
                              parentNodeDef,
                              level0StartNodeDef,
                              level0Node1Def,
                              level0Node2Def,
                              level0EndNodeDef,
                              level1SubProcess1Def,
                              level1Node1Def,
                              level1Node2Def,
                              level2SubProcess1Def,
                              level2Node1Def,
                              level2Node2Def);

        return level2Graph;
    }

    private static void initializeLevel2Graph(TestingGraphMockHandler graphTestHandler,
                                              Level2Graph level2Graph,
                                              Object parentNodeDef,
                                              Object level0StartNodeDef,
                                              Object level0Node1Def,
                                              Object level0Node2Def,
                                              Object level0EndNodeDef,
                                              Object level1SubProcess1Def,
                                              Object level1Node1Def,
                                              Object level1Node2Def,
                                              Object level2SubProcess1Def,
                                              Object level2Node1Def,
                                              Object level2Node2Def) {
        initializeLevel1Graph(graphTestHandler,
                              level2Graph,
                              parentNodeDef,
                              level0StartNodeDef,
                              level0Node1Def,
                              level0Node2Def,
                              level0EndNodeDef,
                              level1SubProcess1Def,
                              level1Node1Def,
                              level1Node2Def);

        level2Graph.level2SubProcess1 =
                graphTestHandler.newNode(LEVEL2_SUB_PROCESS1.uuid(),
                                         Optional.of(level2SubProcess1Def));
        level2Graph.level2Node1 =
                graphTestHandler.newNode(LEVEL2_NODE1.uuid(),
                                         Optional.of(level2Node1Def));
        level2Graph.level2Node2 =
                graphTestHandler.newNode(LEVEL2_NODE2.uuid(),
                                         Optional.of(level2Node2Def));
        level2Graph.level2Edge1 =
                graphTestHandler.newEdge(LEVEL2_EDGE1.uuid(),
                                         Optional.empty());
        graphTestHandler
                .setChild(level2Graph.level1SubProcess1,
                          level2Graph.level2SubProcess1)
                .setChild(level2Graph.level2SubProcess1,
                          level2Graph.level2Node1)
                .setChild(level2Graph.level2SubProcess1,
                          level2Graph.level2Node2)
                .addEdge(level2Graph.level2Edge1,
                         level2Graph.level2Node1)
                .connectTo(level2Graph.level2Edge1,
                           level2Graph.level2Node2);
    }
}
