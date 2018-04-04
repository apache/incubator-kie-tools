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
package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;

/**
 * Removes a connector between two nodes from the canvas.
 */
public class DeleteCanvasConnectorCommand extends AbstractCanvasCommand {

    private final Edge candidate;
    private final Node source;
    private final Node target;

    public DeleteCanvasConnectorCommand(final Edge candidate) {
        this.candidate = candidate;
        this.source = candidate.getSourceNode();
        this.target = candidate.getTargetNode();
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        if (!checkShapeNotNull(context, candidate.getUUID())) {
            //it is already not present on canvas
            return buildResult();
        }

        context.deregister(candidate);
        if (null != source) {
            context.notifyCanvasElementUpdated(source);
        }
        if (null != target) {
            context.notifyCanvasElementUpdated(target);
        }
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        final String ssid = context.getDiagram().getMetadata().getShapeSetId();
        return new AddCanvasConnectorCommand(candidate,
                                             ssid).execute(context);
    }

    public Edge getCandidate() {
        return candidate;
    }

    @Override
    public String toString() {
        return getClass().getName() +
                " [candidate=" + getUUID(candidate) + "," +
                " sourceNode=" + getUUID(source) + "," +
                " targetNode=" + getUUID(target) + "]";
    }
}
