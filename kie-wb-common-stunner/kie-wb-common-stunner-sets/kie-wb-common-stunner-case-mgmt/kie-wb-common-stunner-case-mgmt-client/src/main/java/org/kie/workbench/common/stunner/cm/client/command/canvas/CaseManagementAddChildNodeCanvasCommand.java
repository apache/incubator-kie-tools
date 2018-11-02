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

import java.util.Optional;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddCanvasNodeCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Node;

public class CaseManagementAddChildNodeCanvasCommand extends AbstractCanvasCommand {

    private final Node parent;
    private final Node child;
    private final String shapeSetId;
    private final int index;

    public CaseManagementAddChildNodeCanvasCommand(final Node parent,
                                                   final Node child,
                                                   final String shapeSetId,
                                                   final int index) {
        this.parent = parent;
        this.child = child;
        this.shapeSetId = shapeSetId;
        this.index = index;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        return new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>()
                .addCommand(new AddCanvasNodeCommand(child,
                                                     shapeSetId))
                .addCommand(new CaseManagementSetChildNodeCanvasCommand(parent,
                                                                        child,
                                                                        Optional.of(index),
                                                                        Optional.empty(),
                                                                        Optional.empty()))
                .build()
                .execute(context);
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>()
                .addCommand(new CaseManagementDeleteCanvasNodeCommand(child,
                                                                      parent,
                                                                      index))
                .build()
                .execute(context);
    }
}
