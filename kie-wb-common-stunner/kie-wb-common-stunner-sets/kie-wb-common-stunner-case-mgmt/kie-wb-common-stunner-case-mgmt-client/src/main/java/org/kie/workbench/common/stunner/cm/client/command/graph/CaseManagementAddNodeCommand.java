/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm.client.command.graph;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddNodeCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

@Portable
public class CaseManagementAddNodeCommand extends AddNodeCommand {

    public CaseManagementAddNodeCommand(@MapsTo("candidate") Node candidate) {
        super(candidate);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> undo(GraphCommandExecutionContext context) {
        final CaseManagementSafeDeleteNodeCommand undoCommand = new CaseManagementSafeDeleteNodeCommand(getCandidate());
        return undoCommand.execute(context);
    }
}
