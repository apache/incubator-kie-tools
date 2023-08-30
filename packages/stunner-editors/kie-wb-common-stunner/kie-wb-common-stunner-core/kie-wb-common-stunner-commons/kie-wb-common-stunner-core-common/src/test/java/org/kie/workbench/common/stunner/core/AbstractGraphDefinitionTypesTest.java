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

import java.util.Optional;

import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.validation.Violation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class AbstractGraphDefinitionTypesTest {

    public static final String DEF_ROOT_ID = RootDefinition.class.getName();
    public static final String DEF_PARENT_ID = ParentDefinition.class.getName();
    public static final String DEF_GRAND_PARENT_ID = GrandParentDefinition.class.getName();
    public static final String DEF_A_ID = DefinitionA.class.getName();
    public static final String DEF_B_ID = DefinitionB.class.getName();
    public static final String DEF_C_ID = DefinitionC.class.getName();
    public static final String ROOT_UUID = "root-uuid";
    public static final String PARENT_UUID = "parent-uuid";
    public static final String GRAND_PARENT_UUID = "grand-parent-uuid";
    public static final String A_UUID = "a-uuid";
    public static final String B_UUID = "b-uuid";
    public static final String C_UUID = "c-uuid";

    protected class RootDefinition {

    }

    protected class ParentDefinition {

    }

    protected class GrandParentDefinition {

    }

    protected class DefinitionA {

    }

    protected class DefinitionB {

    }

    protected class DefinitionC {

    }

    protected TestingGraphMockHandler graphHandler;

    protected RootDefinition rootDefinition;
    protected ParentDefinition parentDefinition;
    protected GrandParentDefinition gradParentDefinition;
    protected DefinitionA definitionA;
    protected DefinitionB definitionB;
    protected DefinitionC definitionC;
    protected Node rootNode;
    protected Node parentNode;
    protected Node grandParentNode;
    protected Node nodeA;
    protected Node nodeB;
    protected Node nodeC;

    @SuppressWarnings("unchecked")
    protected void setup() throws Exception {
        this.graphHandler = new TestingGraphMockHandler();
        this.rootDefinition = new RootDefinition();
        this.parentDefinition = new ParentDefinition();
        this.gradParentDefinition = new GrandParentDefinition();
        this.definitionA = new DefinitionA();
        this.definitionB = new DefinitionB();
        this.definitionC = new DefinitionC();
        when(graphHandler.getDefinitionAdapter().getId(eq(rootDefinition))).thenReturn(DefinitionId.build(DEF_ROOT_ID));
        when(graphHandler.getDefinitionAdapter().getId(eq(parentDefinition))).thenReturn(DefinitionId.build(DEF_PARENT_ID));
        when(graphHandler.getDefinitionAdapter().getId(eq(gradParentDefinition))).thenReturn(DefinitionId.build(DEF_GRAND_PARENT_ID));
        when(graphHandler.getDefinitionAdapter().getId(eq(definitionA))).thenReturn(DefinitionId.build(DEF_A_ID));
        when(graphHandler.getDefinitionAdapter().getId(eq(definitionB))).thenReturn(DefinitionId.build(DEF_B_ID));
        when(graphHandler.getDefinitionAdapter().getId(eq(definitionC))).thenReturn(DefinitionId.build(DEF_C_ID));
        this.rootNode = newViewNode(ROOT_UUID,
                                    rootDefinition,
                                    0,
                                    0,
                                    10000,
                                    10000);
        this.parentNode = newViewNode(PARENT_UUID,
                                      parentDefinition,
                                      0,
                                      0,
                                      1000,
                                      1000);
        this.grandParentNode = newViewNode(GRAND_PARENT_UUID,
                                           gradParentDefinition,
                                           0,
                                           0,
                                           900,
                                           900);
        this.nodeA = newViewNode(A_UUID,
                                 definitionA,
                                 0,
                                 0,
                                 10,
                                 10);
        this.nodeB = newViewNode(B_UUID,
                                 definitionB,
                                 20,
                                 20,
                                 10,
                                 10);
        this.nodeC = newViewNode(C_UUID,
                                 definitionC,
                                 40,
                                 40,
                                 10,
                                 10);
        graphHandler
                .setChild(rootNode, parentNode)
                .setChild(rootNode, grandParentNode)
                .setChild(rootNode, nodeC)
                .setChild(parentNode, nodeA)
                .setChild(parentNode, nodeB);
    }

    protected Node newViewNode(String uuid,
                               Object def,
                               final double x,
                               final double y,
                               final double w,
                               final double h) {
        return graphHandler.newViewNode(uuid,
                                        Optional.of(def),
                                        x,
                                        y,
                                        w,
                                        h);
    }

    protected void assertViolations(RuleViolations violations, boolean assertSuccess) {
        final boolean condition = violations.violations(Violation.Type.ERROR).iterator().hasNext();
        if (assertSuccess) {
            assertFalse(condition);
        } else {
            assertTrue(condition);
        }
    }
}
