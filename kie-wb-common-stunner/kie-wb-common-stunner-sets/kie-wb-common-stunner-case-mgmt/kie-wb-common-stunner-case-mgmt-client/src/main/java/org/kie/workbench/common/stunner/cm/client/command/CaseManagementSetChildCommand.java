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
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.cm.client.command.canvas.CaseManagementSetChildNodeCanvasCommand;
import org.kie.workbench.common.stunner.cm.client.command.graph.CaseManagementSetChildNodeGraphCommand;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

import static org.kie.workbench.common.stunner.cm.client.command.util.CaseManagementCommandUtil.getChildGraphIndex;
import static org.kie.workbench.common.stunner.cm.client.command.util.CaseManagementCommandUtil.getNewChildCanvasIndex;
import static org.kie.workbench.common.stunner.cm.client.command.util.CaseManagementCommandUtil.isStage;

public class CaseManagementSetChildCommand extends org.kie.workbench.common.stunner.core.client.canvas.command.SetChildrenCommand {

    protected final OptionalInt canvasIndex;
    protected final Optional<Node<View<?>, Edge>> last;
    protected final Optional<Node<View<?>, Edge>> originalParent;
    protected final OptionalInt originaCanvaslIndex;

    public CaseManagementSetChildCommand(final Node<View<?>, Edge> parent,
                                         final Node<View<?>, Edge> child) {
        this(parent,
             child,
             Optional.empty(),
             OptionalInt.of(getNewChildCanvasIndex(parent)),
             Optional.empty(),
             OptionalInt.empty());
    }

    public CaseManagementSetChildCommand(final Node<View<?>, Edge> parent,
                                         final Node<View<?>, Edge> child,
                                         final Optional<Node<View<?>, Edge>> last,
                                         final OptionalInt canvasIndex,
                                         final Optional<Node<View<?>, Edge>> originalParent,
                                         final OptionalInt originaCanvaslIndex) {
        super(parent,
              Collections.singleton(child));
        this.last = last;
        this.canvasIndex = canvasIndex;
        this.originalParent = originalParent;
        this.originaCanvaslIndex = originaCanvaslIndex;
    }

    public Node getCandidate() {
        return getCandidates().iterator().next();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        // Get the original graph index
        OptionalInt originalIndex = originalParent
                .map(p -> OptionalInt.of(getChildGraphIndex(p, getCandidate())))
                .orElseGet(OptionalInt::empty);

        OptionalInt index = last.map(s -> {
            // Get the index from the last node
            int i = getChildGraphIndex(parent, s);
            if (originalIndex.isPresent() && originalIndex.getAsInt() < i) {
                // If move the node forward
                return OptionalInt.of(i);
            } else {
                return OptionalInt.of(i + 1);
            }
        }).orElseGet(() -> {
            if (canvasIndex.isPresent() && canvasIndex.getAsInt() == 0 && isStage(parent, getCandidate())) {
                // If add a Stage to the start, find the StartNoneEvent
                List<Node<View<?>, Edge>> childNodes = ((Node<View<?>, Edge>) parent).getOutEdges().stream()
                        .map(e -> (Node<View<?>, Edge>) e.getTargetNode()).collect(Collectors.toList());
                for (int i = 0, n = childNodes.size(); i < n; i++) {
                    if (childNodes.get(i).getContent().getDefinition() instanceof StartNoneEvent) {
                        return OptionalInt.of(++i);
                    }
                }
                return OptionalInt.of(0);
            } else {
                return OptionalInt.empty();
            }
        });

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
                                                           canvasIndex,
                                                           originalParent,
                                                           originaCanvaslIndex);
    }
}
