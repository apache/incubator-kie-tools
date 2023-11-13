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

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * A Command to add a node as a child for the main graph instance.
 * It check parent cardinality rules and containment rules as we..
 */
public class AddNodeCommand extends RegisterNodeCommand {

    public AddNodeCommand(final Node candidate) {
        super(candidate);
    }

    @SuppressWarnings("unchecked")
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        final CommandResult<RuleViolation> parentResult = super.check(context);
        final GraphCommandResultBuilder builder = new GraphCommandResultBuilder();
        parentResult.getViolations().forEach(builder::addViolation);
        final Element<? extends Definition<?>> graph = (Element<? extends Definition<?>>) getGraph(context);
        final Collection<RuleViolation> containmentRuleViolations =
                evaluate(context,
                         contextBuilder -> contextBuilder.containment(graph,
                                                                      getCandidate()));
        builder.addViolations(containmentRuleViolations);
        return builder.build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final SafeDeleteNodeCommand undoCommand = new SafeDeleteNodeCommand(getCandidate());
        return undoCommand.execute(context);
    }

    @Override
    public String toString() {
        return "AddNodeCommand [candidate=" + getCandidate().getUUID() + "]";
    }
}
