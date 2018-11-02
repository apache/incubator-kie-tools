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
package org.kie.workbench.common.stunner.cm.client.command.graph;

import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.RemoveChildCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

import static org.kie.workbench.common.stunner.cm.client.command.CaseManagementCanvasCommandFactory.getChildIndex;

@Portable
public class CaseManagementRemoveChildCommand extends RemoveChildCommand {

    private int index;

    public CaseManagementRemoveChildCommand(@MapsTo("parentUUID") String parentUUID,
                                            @MapsTo("candidateUUID") String candidateUUID) {
        super(parentUUID, candidateUUID);
    }

    public CaseManagementRemoveChildCommand(Node<?, Edge> parent,
                                            Node<?, Edge> candidate) {
        super(parent, candidate);

        this.index = getChildIndex(parent, candidate);
    }

    @Override
    public CommandResult<RuleViolation> undo(GraphCommandExecutionContext context) {
        final Node<?, Edge> parent = getParent(context);
        final Node<?, Edge> candidate = getCandidate(context);

        final CaseManagementSetChildNodeGraphCommand undoCommand =
                new CaseManagementSetChildNodeGraphCommand(parent,
                                                           candidate,
                                                           Optional.of(index),
                                                           Optional.empty(),
                                                           Optional.empty());
        return undoCommand.execute(context);
    }
}
