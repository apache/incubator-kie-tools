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

import org.kie.workbench.common.dmn.client.commands.util.ContentDefinitionIdUtils;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNGraphsProvider;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.DeleteCanvasNodeCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Node;

public class DMNDeleteCanvasNodeCommand extends DeleteCanvasNodeCommand {

    private final GraphsProvider graphsProvider;

    public DMNDeleteCanvasNodeCommand(final Node candidate,
                                      final DMNGraphsProvider graphsProvider) {
        super(candidate);
        this.graphsProvider = graphsProvider;
    }

    @Override
    protected AbstractCanvasCommand createUndoCommand(final Node parent,
                                                      final Node candidate,
                                                      final String ssid) {

        if (belongsToCurrentGraph(candidate)) {
            return superCreateUndoCommand(parent, candidate, ssid);
        } else {
            return createEmptyCommand();
        }
    }

    AbstractCanvasCommand createEmptyCommand() {
        return new AbstractCanvasCommand() {
            @Override
            public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
                return CanvasCommandResultBuilder.SUCCESS;
            }

            @Override
            public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
                return CanvasCommandResultBuilder.SUCCESS;
            }
        };
    }

    boolean belongsToCurrentGraph(final Node candidate) {
        return ContentDefinitionIdUtils.belongsToCurrentGraph(candidate, graphsProvider);
    }

    AbstractCanvasCommand superCreateUndoCommand(final Node parent,
                                                 final Node candidate,
                                                 final String ssid) {
        return super.createUndoCommand(parent, candidate, ssid);
    }
}
