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

package org.kie.workbench.common.dmn.client.commands.factory.graph;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

@Portable
public class SetChildNodeCommand extends org.kie.workbench.common.stunner.core.graph.command.impl.SetChildNodeCommand {

    public SetChildNodeCommand(final @MapsTo("parentUUID") String parentUUID,
                               final @MapsTo("candidateUUID") String candidateUUID) {
        super(parentUUID,
              candidateUUID);
    }

    public SetChildNodeCommand(final Node<?, Edge> parent,
                               final Node<?, Edge> candidate) {
        super(parent,
              candidate);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final CommandResult<RuleViolation> results = super.execute(context);
        if (!results.getType().equals(CommandResult.Type.ERROR)) {
            final Node<?, Edge> parent = getParent(context);
            final Node<?, Edge> candidate = getCandidate(context);
            if (parent.getContent() instanceof View) {
                final DMNModelInstrumentedBase parentDMNModel = (DMNModelInstrumentedBase) ((View) parent.getContent()).getDefinition();
                if (candidate.getContent() instanceof View) {
                    final DMNModelInstrumentedBase childDMNModel = (DMNModelInstrumentedBase) ((View) candidate.getContent()).getDefinition();
                    childDMNModel.setParent(parentDMNModel);
                }
            }
        }
        return results;
    }
}
