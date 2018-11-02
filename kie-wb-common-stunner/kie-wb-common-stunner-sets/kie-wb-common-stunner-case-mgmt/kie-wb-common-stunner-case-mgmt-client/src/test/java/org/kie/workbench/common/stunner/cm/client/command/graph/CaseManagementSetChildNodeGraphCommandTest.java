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

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.cm.client.command.CommandTestUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementSetChildNodeGraphCommandTest extends CaseManagementAbstractGraphCommandTest {

    private Optional<Integer> index;
    private Optional<Node> originalParent;
    private Optional<Integer> originalIndex;

    @Before
    public void setup() {
        super.setup();
        this.index = Optional.empty();
        this.originalParent = Optional.empty();
        this.originalIndex = Optional.empty();
    }

    @Test
    public void checkExecute() {
        setChildNode(parent,
                     candidate,
                     index,
                     originalParent,
                     originalIndex);

        assertEquals(1,
                     parent.getOutEdges().size());
        assertEquals(1,
                     candidate.getInEdges().size());
        assertEquals(parent.getOutEdges().get(0),
                     candidate.getInEdges().get(0));

        final Edge edge = parent.getOutEdges().get(0);
        assertEquals(parent,
                     edge.getSourceNode());
        assertEquals(candidate,
                     edge.getTargetNode());
        assertTrue(edge.getContent() instanceof Child);
    }

    private CaseManagementSetChildNodeGraphCommand setChildNode(final Node parent,
                                                                final Node candidate,
                                                                final Optional<Integer> index,
                                                                final Optional<Node> originalParent,
                                                                final Optional<Integer> originalIndex) {
        final CaseManagementSetChildNodeGraphCommand command = new CaseManagementSetChildNodeGraphCommand(parent,
                                                                                                          candidate,
                                                                                                          index,
                                                                                                          originalParent,
                                                                                                          originalIndex);
        command.execute(context);
        return command;
    }

    @Test
    public void checkUndo() {
        //Setup the relationship to undo
        final CaseManagementSetChildNodeGraphCommand command = setChildNode(parent,
                                                                            candidate,
                                                                            index,
                                                                            originalParent,
                                                                            originalIndex);

        //Perform test
        command.undo(context);

        assertEquals(0,
                     parent.getOutEdges().size());
        assertEquals(0,
                     candidate.getInEdges().size());
    }

    @Test
    public void checkExecuteWhenChildHasExistingParent() {
        //Setup an existing relationship
        setChildNode(parent,
                     candidate,
                     index,
                     originalParent,
                     originalIndex);

        //Perform test
        final Node<View<?>, Edge> newParent = CommandTestUtils.makeNode("uuid3",
                                                                        "existingParent",
                                                                        10.0,
                                                                        20.0,
                                                                        50.0,
                                                                        50.0);
        setChildNode(newParent,
                     candidate,
                     index,
                     Optional.of(parent),
                     Optional.of(0));

        assertEquals(0,
                     parent.getOutEdges().size());
        assertEquals(1,
                     newParent.getOutEdges().size());
        assertEquals(1,
                     candidate.getInEdges().size());
        assertEquals(newParent.getOutEdges().get(0),
                     candidate.getInEdges().get(0));

        final Edge edge = newParent.getOutEdges().get(0);
        assertEquals(newParent,
                     edge.getSourceNode());
        assertEquals(candidate,
                     edge.getTargetNode());
        assertTrue(edge.getContent() instanceof Child);
    }

    @Test
    public void checkUndoWhenChildHasExistingParent() {
        //Setup an existing relationship
        setChildNode(parent,
                     candidate,
                     index,
                     originalParent,
                     originalIndex);

        final Node<View<?>, Edge> newParent = CommandTestUtils.makeNode("uuid3",
                                                                        "existingParent",
                                                                        10.0,
                                                                        20.0,
                                                                        50.0,
                                                                        50.0);
        final CaseManagementSetChildNodeGraphCommand addToNewParentCommand = setChildNode(newParent,
                                                                                          candidate,
                                                                                          index,
                                                                                          Optional.of(parent),
                                                                                          Optional.of(0));

        //Perform test
        addToNewParentCommand.undo(context);

        assertEquals(0,
                     newParent.getOutEdges().size());
        assertEquals(1,
                     parent.getOutEdges().size());
        assertEquals(1,
                     candidate.getInEdges().size());
        assertEquals(parent.getOutEdges().get(0),
                     candidate.getInEdges().get(0));

        final Edge edge = parent.getOutEdges().get(0);
        assertEquals(parent,
                     edge.getSourceNode());
        assertEquals(candidate,
                     edge.getTargetNode());
        assertTrue(edge.getContent() instanceof Child);
    }
}
