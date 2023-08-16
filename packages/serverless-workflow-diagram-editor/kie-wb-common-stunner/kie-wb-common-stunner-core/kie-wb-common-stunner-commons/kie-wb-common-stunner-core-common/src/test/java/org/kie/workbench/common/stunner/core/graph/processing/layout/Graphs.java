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


package org.kie.workbench.common.stunner.core.graph.processing.layout;

public final class Graphs {

    /*
     * 2 crossings
     *       A           B
     *      /\\         /
     *     /  \ -------+ --
     *    / /-- +-----/    \
     *   / /     \          \
     *  D         E          F
     * */

    public static final String[][] SIMPLE_ACYCLIC = {
            {"A", "B"},
            {"A", "C"},
            {"C", "B"}};

    public static final String[][] SIMPLE_CYCLIC = {
            {"A", "B"},
            {"B", "C"},
            {"C", "A"}};

    public static final String[][] CYCLIC_GRAPH_1 = {
            {"A", "B"},
            {"A", "C"},
            {"C", "D"},
            {"D", "B"},
            {"G", "B"},
            {"G", "A"},
            {"G", "H"},
            {"H", "I"},
            {"I", "G"},
            {"G", "J"},
            {"G", "F"},
            {"F", "E"},
            {"E", "A"},
            {"A", "I"}
    };

    static final String[][] REAL_CASE_1 = {
            {"L", "D"},
            {"D", "A"},
            {"D", "B"},
            {"E", "B"},
            {"Y", "T"},
            {"T", "E"},
            {"T", "U"},
            {"Z", "U"},
            {"A1", "U"},
            {"B1", "U"},
            {"C1", "U"},
            {"U", "N"},
            {"U", "O"},
            {"U", "P"},
            {"U", "Q"},
            {"D1", "V"},
            {"V", "Q"},
            {"Q", "E"},
            {"Q", "F"},
            {"Q", "G"},
            {"Q", "H"},
            {"Q", "I"},
            {"Q", "J"},
            {"Q", "K"},
            {"T", "F"},
            {"F", "C"}
    };

    public static final String[][] TwoSeparateTreesFromRoots = {
            {"A1", "B1"},
            {"A1", "C1"},
            {"C1", "D1"},
            {"C1", "E1"},
            {"A2", "B2"},
            {"A2", "C2"},
            {"A2", "D2"},
            {"B2", "E2"},
            {"D2", "F2"},
    };

    public static final String[][] TwoSeparateTreesToRoots = {
            {"B1", "A1"},
            {"C1", "A1"},
            {"D1", "C1"},
            {"E1", "C1"},
            {"B2", "A2"},
            {"C2", "A2"},
            {"D2", "A2"},
            {"E2", "B2"},
            {"F2", "D2"},
    };

    public static final String[][] Full = {
            {"A", "B"},
            {"A", "C"},
            {"A", "D"},
            {"B", "A"},
            {"B", "C"},
            {"B", "D"},
            {"C", "A"},
            {"C", "B"},
            {"C", "D"},
            {"D", "A"},
            {"D", "B"},
            {"D", "C"}
    };
}