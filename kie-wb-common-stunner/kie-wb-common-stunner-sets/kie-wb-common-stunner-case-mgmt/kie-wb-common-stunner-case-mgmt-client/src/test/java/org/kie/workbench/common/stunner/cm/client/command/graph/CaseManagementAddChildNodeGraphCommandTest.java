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

package org.kie.workbench.common.stunner.cm.client.command.graph;

import java.util.OptionalInt;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndex;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementAddChildNodeGraphCommandTest extends CaseManagementAbstractGraphCommandTest {

    private int targetIndex;

    @Before
    public void setup() {
        super.setup();
        targetIndex = parent.getOutEdges().size();
        ((MapIndex) index).addNode(parent);
    }

    private void checkExecute(Supplier<CaseManagementAddChildNodeGraphCommand> addNodeSupplier) {
        addNodeSupplier.get();

        assertEquals(1,
                     parent.getOutEdges().size());
        assertEquals(1,
                     candidate.getInEdges().size());
        assertEquals(parent.getOutEdges().get(targetIndex),
                     candidate.getInEdges().get(0));

        final Edge edge = parent.getOutEdges().get(targetIndex);
        assertEquals(parent,
                     edge.getSourceNode());
        assertEquals(candidate,
                     edge.getTargetNode());
        assertTrue(edge.getContent() instanceof Child);
    }

    private void checkUndo(Supplier<CaseManagementAddChildNodeGraphCommand> addNodeSupplier) {
        //Setup the relationship to undo
        final CaseManagementAddChildNodeGraphCommand command = addNodeSupplier.get();

        //Perform test
        command.undo(context);

        assertEquals(0,
                     parent.getOutEdges().size());
        assertEquals(0,
                     candidate.getInEdges().size());
    }

    private CaseManagementAddChildNodeGraphCommand addChildNode1() {
        final CaseManagementAddChildNodeGraphCommand command = new CaseManagementAddChildNodeGraphCommand(parent,
                                                                                                          candidate,
                                                                                                          targetIndex);
        command.execute(context);
        return command;
    }

    private CaseManagementAddChildNodeGraphCommand addChildNode2() {
        final CaseManagementAddChildNodeGraphCommand command = new CaseManagementAddChildNodeGraphCommand(parent.getUUID(),
                                                                                                          candidate,
                                                                                                          OptionalInt.of(targetIndex));
        command.execute(context);
        return command;
    }

    @Test
    public void checkExecute1() {
        checkExecute(this::addChildNode1);
    }

    @Test
    public void checkUndo1() {
        checkUndo(this::addChildNode1);
    }

    @Test
    public void checkExecute2() {
        checkExecute(this::addChildNode2);
    }

    @Test
    public void checkUndo2() {
        checkUndo(this::addChildNode2);
    }
}
