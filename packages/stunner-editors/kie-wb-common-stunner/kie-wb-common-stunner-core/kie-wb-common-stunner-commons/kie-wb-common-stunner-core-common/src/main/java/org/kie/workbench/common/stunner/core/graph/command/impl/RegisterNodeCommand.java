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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;

/**
 * A Command to register and node into the graph storage.
 * Cardinality rule evaluations for the graph required.
 * <p>
 * This command should be used as aggregate for composite commands,
 * but it's recommended to provide on the factory only public commands
 * that do really update the graph structure:
 * - <a>org.kie.workbench.common.stunner.core.graph.command.impl.AddNodeCommand</a>
 * - <a>org.kie.workbench.common.stunner.core.graph.command.impl.AddChildNodeCommand</a>
 * - <a>org.kie.workbench.common.stunner.core.graph.command.impl.AddDockedNodeCommand</a>
 */
@Portable
public class RegisterNodeCommand extends AbstractGraphCommand {

    private final Node candidate;

    public RegisterNodeCommand(final @MapsTo("candidate") Node candidate) {
        this.candidate = Objects.requireNonNull(candidate, "Parameter named 'candidate' should be not null!");
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final CommandResult<RuleViolation> results = allow(context);
        if (!results.getType().equals(CommandResult.Type.ERROR)) {
            final Graph graph = getGraph(context);
            graph.addNode(candidate);
            getMutableIndex(context).addNode(candidate);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        final Collection<RuleViolation> cardinalityRuleViolations =
                evaluate(context,
                         contextBuilder -> contextBuilder.cardinality(Collections.singleton(getCandidate()),
                                                                      CardinalityContext.Operation.ADD));
        return new GraphCommandResultBuilder(cardinalityRuleViolations).build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final DeregisterNodeCommand undoCommand = new DeregisterNodeCommand(candidate);
        return undoCommand.execute(context);
    }

    public Node getCandidate() {
        return candidate;
    }

    @Override
    public String toString() {
        return "RegisterNodeCommand [candidate=" + candidate.getUUID() + "]";
    }
}
