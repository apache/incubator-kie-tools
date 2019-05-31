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

package org.kie.workbench.common.stunner.cm.client.command.canvas;

import java.util.Collections;
import java.util.Optional;
import java.util.OptionalInt;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.RemoveCanvasChildrenCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class CaseManagementSetChildNodeCanvasCommand extends org.kie.workbench.common.stunner.core.client.canvas.command.SetCanvasChildrenCommand {

    protected final OptionalInt index;
    protected final Optional<Node<View<?>, Edge>> originalParent;
    protected final OptionalInt originalIndex;

    @SuppressWarnings("unchecked")
    public CaseManagementSetChildNodeCanvasCommand(final Node<View<?>, Edge> parent,
                                                   final Node<View<?>, Edge> child,
                                                   final OptionalInt index,
                                                   final Optional<Node<View<?>, Edge>> originalParent,
                                                   final OptionalInt originalIndex) {
        super(parent,
              Collections.singleton(child));
        this.index = index;
        this.originalParent = originalParent;
        this.originalIndex = originalIndex;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        context.addChild(getParent(),
                         getCandidate(),
                         index.getAsInt());
        context.updateElementProperties(getCandidate(),
                                        MutationContext.STATIC);
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        if (!(originalParent.isPresent() && originalIndex.isPresent())) {
            return new RemoveCanvasChildrenCommand(getParent(),
                                                   getCandidate()).execute(context);
        } else {
            context.addChild(originalParent.get(),
                             getCandidate(),
                             originalIndex.getAsInt());
            context.updateElementProperties(getCandidate(),
                                            MutationContext.STATIC);
        }
        return buildResult();
    }

    private Node getCandidate() {
        return getCandidates().iterator().next();
    }
}
