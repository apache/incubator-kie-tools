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

package org.kie.workbench.common.stunner.cm.client.command;

import java.util.Collections;
import java.util.Optional;
import java.util.OptionalInt;

import org.kie.workbench.common.stunner.cm.client.command.canvas.CaseManagementSetChildNodeCanvasCommand;
import org.kie.workbench.common.stunner.cm.client.command.graph.CaseManagementSetChildNodeGraphCommand;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class CaseManagementSetChildCommand extends org.kie.workbench.common.stunner.core.client.canvas.command.SetChildrenCommand {

    protected final OptionalInt index;
    protected final Optional<Node> originalParent;
    protected final OptionalInt originalIndex;

    public CaseManagementSetChildCommand(final Node parent,
                                         final Node child) {
        this(parent,
             child,
             OptionalInt.of(parent.getOutEdges().size()),
             Optional.empty(),
             OptionalInt.empty());
    }

    public CaseManagementSetChildCommand(final Node parent,
                                         final Node child,
                                         final OptionalInt index,
                                         final Optional<Node> originalParent,
                                         final OptionalInt originalIndex) {
        super(parent,
              Collections.singleton(child));
        this.index = index;
        this.originalParent = originalParent;
        this.originalIndex = originalIndex;
    }

    public Node getCandidate() {
        return getCandidates().iterator().next();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new CaseManagementSetChildNodeGraphCommand(parent,
                                                          getCandidate(),
                                                          index,
                                                          originalParent,
                                                          originalIndex);
    }

    @Override
    protected AbstractCanvasCommand newCanvasCommand(final AbstractCanvasHandler context) {
        return new CaseManagementSetChildNodeCanvasCommand(parent,
                                                           getCandidate(),
                                                           index,
                                                           originalParent,
                                                           originalIndex);
    }
}
