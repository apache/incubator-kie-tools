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

import java.util.Collection;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.DeleteElementsCommand;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class CaseManagementDeleteElementsCommand extends DeleteElementsCommand {

    public CaseManagementDeleteElementsCommand(Collection<Element> elements) {
        super(elements);
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(AbstractCanvasHandler context) {
        return new org.kie.workbench.common.stunner.cm.client.command.graph.CaseManagementDeleteElementsCommand(this::getElements,
                                                                                                                new CaseManagementCanvasMultipleDeleteProcessor());
    }

    private class CaseManagementCanvasMultipleDeleteProcessor extends CanvasMultipleDeleteProcessor {

        @Override
        protected CaseManagementDeleteNodeCommand.CaseManagementCanvasDeleteProcessor createProcessor(SafeDeleteNodeCommand.Options options) {
            return new CaseManagementDeleteNodeCommand.CaseManagementCanvasDeleteProcessor(options);
        }
    }
}
