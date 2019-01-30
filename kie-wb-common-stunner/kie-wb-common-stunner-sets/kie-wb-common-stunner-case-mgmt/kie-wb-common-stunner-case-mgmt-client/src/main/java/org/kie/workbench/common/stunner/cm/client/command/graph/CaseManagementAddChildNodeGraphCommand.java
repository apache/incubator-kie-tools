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

package org.kie.workbench.common.stunner.cm.client.command.graph;

import java.util.Optional;
import java.util.OptionalInt;

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.RegisterNodeCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

@SuppressWarnings("unchecked")
public class CaseManagementAddChildNodeGraphCommand extends org.kie.workbench.common.stunner.core.graph.command.impl.AddChildNodeCommand {

    private final OptionalInt index;

    public CaseManagementAddChildNodeGraphCommand(final Node parent,
                                                  final Node child,
                                                  final int index) {
        super(parent,
              child);

        this.index = OptionalInt.of(index);
    }

    public CaseManagementAddChildNodeGraphCommand(final String parentUUID,
                                                  final Node child,
                                                  final OptionalInt index) {
        super(parentUUID,
              child,
              null);

        this.index = index;
    }

    @Override
    protected CaseManagementAddChildNodeGraphCommand initialize(final GraphCommandExecutionContext context) {
        final Node parent = getParent(context);
        final Node child = getCandidate();
        this.addCommand(new RegisterNodeCommand(child));
        this.addCommand(new CaseManagementSetChildNodeGraphCommand(parent,
                                                                   child,
                                                                   index,
                                                                   Optional.empty(),
                                                                   OptionalInt.empty()));
        return this;
    }

    @Override
    public CommandResult<RuleViolation> undo(GraphCommandExecutionContext context) {
        return new CaseManagementSafeDeleteNodeCommand(getCandidate()).execute(context);
    }
}
