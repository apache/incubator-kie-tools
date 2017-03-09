/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.client.command.canvas;

import java.util.Optional;

import org.kie.workbench.common.stunner.cm.client.canvas.CaseManagementCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.RemoveCanvasChildCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Node;

public class CaseManagementSetChildNodeCanvasCommand extends org.kie.workbench.common.stunner.core.client.canvas.command.SetCanvasChildNodeCommand {

    protected final Optional<Integer> index;
    protected final Optional<Node> originalParent;
    protected final Optional<Integer> originalIndex;

    public CaseManagementSetChildNodeCanvasCommand(final Node parent,
                                                   final Node child,
                                                   final Optional<Integer> index,
                                                   final Optional<Node> originalParent,
                                                   final Optional<Integer> originalIndex) {
        super(parent,
              child);
        this.index = index;
        this.originalParent = originalParent;
        this.originalIndex = originalIndex;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        final CaseManagementCanvasHandler caseManagementCanvasHandler = (CaseManagementCanvasHandler) context;
        caseManagementCanvasHandler.addChild(getParent(),
                                             getCandidate(),
                                             index.get());
        context.updateElementProperties(getCandidate(),
                                        MutationContext.STATIC);
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        if (!(originalParent.isPresent() && originalIndex.isPresent())) {
            return new RemoveCanvasChildCommand(getParent(),
                                                getCandidate()).execute(context);
        } else {
            final CaseManagementCanvasHandler caseManagementCanvasHandler = (CaseManagementCanvasHandler) context;
            caseManagementCanvasHandler.addChild(originalParent.get(),
                                                 getCandidate(),
                                                 originalIndex.get());
            context.updateElementProperties(getCandidate(),
                                            MutationContext.STATIC);
        }
        return buildResult();
    }
}