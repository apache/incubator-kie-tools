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

package org.kie.workbench.common.dmn.client.commands.factory.canvas;

import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNGraphsProvider;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DeleteCanvasConnectorCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Edge;

import static org.kie.workbench.common.dmn.client.commands.util.ContentDefinitionIdUtils.belongsToCurrentGraph;

public class DMNDeleteCanvasConnectorNodeCommand extends DeleteCanvasConnectorCommand {

    private final GraphsProvider graphsProvider;

    public DMNDeleteCanvasConnectorNodeCommand(final Edge candidate,
                                               final DMNGraphsProvider graphsProvider) {
        super(candidate);
        this.graphsProvider = graphsProvider;
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        if (candidateNodeBelongsToCurrentGraph()) {
            return superUndo(context);
        } else {
            return CanvasCommandResultBuilder.SUCCESS;
        }
    }

    CommandResult<CanvasViolation> superUndo(final AbstractCanvasHandler context) {
        return super.undo(context);
    }

    boolean candidateNodeBelongsToCurrentGraph() {
        return belongsToCurrentGraph(getCandidate(), graphsProvider);
    }
}
