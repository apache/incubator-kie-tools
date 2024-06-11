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

package org.kie.workbench.common.stunner.sw.client.shapes;

import org.junit.Test;
import org.kie.workbench.common.stunner.sw.definition.ActionNode;
import org.kie.workbench.common.stunner.sw.definition.ParallelStateBranch;
import org.kie.workbench.common.stunner.sw.definition.SubFlowRef;

import static org.assertj.core.util.Arrays.array;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HasBranchesTest extends HasTranslationGeneralTest {

    private final HasBranches hasBranches = HasBranchesTest.super::getTranslation;

    private final String NAME = "Test Name";

    @Test
    public void branchesAreNullTest() {
        assertTranslations(TEST_STRING,
                           hasBranches.getBranchesString(null),
                           "Branches.null");
    }

    @Test
    public void branchesAreEmptyTest() {
        assertTranslations(TEST_STRING,
                           hasBranches.getBranchesString(new ParallelStateBranch[0]),
                           "Branches.null");
    }

    @Test
    public void oneBranchNoNameNoActionsTest() {
        assertTranslations(TEST_STRING + ": null\r\n" + TEST_STRING + "\r\n\r\n",
                           hasBranches.getBranchesString(array(new ParallelStateBranch())),
                           "Branch.title", "Actions.null");
    }

    @Test
    public void oneBranchWithNameNoActionsTest() {
        ParallelStateBranch branch = new ParallelStateBranch();
        branch.setName(NAME);
        assertTranslations(TEST_STRING + ": " + NAME + "\r\n" + TEST_STRING + "\r\n\r\n",
                           hasBranches.getBranchesString(array(branch)),
                           "Branch.title", "Actions.null");
    }

    @Test
    public void oneBranchWithNameOneActionTest() {
        ParallelStateBranch branch = new ParallelStateBranch();
        branch.setName(NAME);
        ActionNode actionNode = new ActionNode();
        actionNode.setName(NAME);
        branch.setActions(array(actionNode));
        assertTranslations(TEST_STRING + ": " + NAME + "\r\n" + TEST_STRING + ": " + NAME + "\r\n\r\n",
                           hasBranches.getBranchesString(array(branch)),
                           "Branch.title", "Action.name");
    }

    @Test
    public void twoBranchesWithNameNoActionTest() {
        ParallelStateBranch branch = new ParallelStateBranch();
        branch.setName(NAME);
        assertTranslations(TEST_STRING + ": " + NAME + "\r\n" + TEST_STRING +
                                   "\r\n\r\n" + TEST_STRING + ": " + NAME + "\r\n" + TEST_STRING + "\r\n\r\n",
                           hasBranches.getBranchesString(array(branch, branch)),
                           "Branch.title", "Actions.null", "Branch.title", "Actions.null");
    }

    @Test
    public void hasSubflowsTrueTest() {
        ActionNode[] actions = new ActionNode[2];

        ActionNode action1 = new ActionNode();
        SubFlowRef subFlowRef = new SubFlowRef();
        String workflowId = "PROPER WORKFLOW ID";
        subFlowRef.setWorkflowId(workflowId);
        action1.setSubFlowRef(subFlowRef);
        actions[0] = action1;

        ActionNode action2 = new ActionNode();
        String funcRef = "FUNC REF NAME";
        action2.setFunctionRef(funcRef);
        actions[1] = action2;

        ParallelStateBranch branch = new ParallelStateBranch();
        branch.setName(NAME);
        branch.setActions(actions);

        assertTrue(hasBranches.hasSubflows(array(branch)));
    }

    @Test
    public void hasSubflowsFalseTest() {
        ActionNode[] actions = new ActionNode[1];
        ActionNode action = new ActionNode();
        String funcRef = "FUNC REF NAME";
        action.setFunctionRef(funcRef);
        actions[0] = action;

        ParallelStateBranch branch = new ParallelStateBranch();
        branch.setName(NAME);
        branch.setActions(actions);

        assertFalse(hasBranches.hasSubflows(array(branch)));
    }

}
