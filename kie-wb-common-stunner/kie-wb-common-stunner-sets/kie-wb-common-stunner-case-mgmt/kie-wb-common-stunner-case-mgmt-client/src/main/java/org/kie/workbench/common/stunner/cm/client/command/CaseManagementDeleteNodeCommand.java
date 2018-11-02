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
package org.kie.workbench.common.stunner.cm.client.command;

import org.kie.workbench.common.stunner.cm.client.command.canvas.CaseManagementDeleteCanvasNodeCommand;
import org.kie.workbench.common.stunner.cm.client.command.graph.CaseManagementSafeDeleteNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DeleteNodeCommand;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class CaseManagementDeleteNodeCommand extends DeleteNodeCommand {

    public CaseManagementDeleteNodeCommand(Node candidate) {
        super(candidate);
    }

    public CaseManagementDeleteNodeCommand(Node candidate, SafeDeleteNodeCommand.Options options) {
        super(candidate, options, new CaseManagementCanvasDeleteProcessor(options));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(AbstractCanvasHandler context) {
        return new CaseManagementSafeDeleteNodeCommand(candidate,
                                                       deleteProcessor,
                                                       options);
    }

    public static class CaseManagementCanvasDeleteProcessor extends CanvasDeleteProcessor {

        public CaseManagementCanvasDeleteProcessor(SafeDeleteNodeCommand.Options options) {
            super(options);
        }

        @Override
        protected CaseManagementDeleteCanvasNodeCommand createDeleteCanvasNodeCommand(Node<?, Edge> node) {
            return new CaseManagementDeleteCanvasNodeCommand(node);
        }
    }
}
