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

package org.kie.workbench.common.dmn.client.commands.factory.graph;

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.DeregisterNodeCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class DMNDeregisterNodeCommand extends DeregisterNodeCommand {

    private final Graph graph;

    public DMNDeregisterNodeCommand(final Graph graph,
                                    final String uuid) {
        super(uuid);

        this.graph = graph;
    }

    @Override
    protected Graph<?, Node> getGraph(final GraphCommandExecutionContext context) {
        return graph;
    }

    @Override
    protected Node<?, Edge> getCandidate(final GraphCommandExecutionContext context) {
        return getGraph(context).getNode(uuid);
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final DMNRegisterNodeCommand undoCommand = new DMNRegisterNodeCommand(getGraph(context), getRemoved());
        return undoCommand.execute(context);
    }
}
