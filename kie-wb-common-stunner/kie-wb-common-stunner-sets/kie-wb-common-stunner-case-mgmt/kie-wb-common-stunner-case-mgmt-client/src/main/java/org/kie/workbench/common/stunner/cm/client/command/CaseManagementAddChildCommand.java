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

package org.kie.workbench.common.stunner.cm.client.command;

import org.kie.workbench.common.stunner.cm.client.command.canvas.CaseManagementAddChildNodeCanvasCommand;
import org.kie.workbench.common.stunner.cm.client.command.graph.CaseManagementAddChildNodeGraphCommand;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

import static org.kie.workbench.common.stunner.cm.client.command.util.CaseManagementCommandUtil.getNewChildCanvasIndex;
import static org.kie.workbench.common.stunner.cm.client.command.util.CaseManagementCommandUtil.getNewChildGraphIndex;

public class CaseManagementAddChildCommand extends org.kie.workbench.common.stunner.core.client.canvas.command.AddChildNodeCommand {

    public CaseManagementAddChildCommand(final Node parent,
                                         final Node candidate,
                                         final String shapeSetId) {
        super(parent,
              candidate,
              shapeSetId);
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        //This registers the Candidate in the Graph and forms the Child Relationship between Parent and Candidate
        return new CaseManagementAddChildNodeGraphCommand(parent,
                                                          candidate,
                                                          getNewChildGraphIndex(parent));
    }

    @Override
    protected AbstractCanvasCommand newCanvasCommand(final AbstractCanvasHandler context) {
        //This needs to add Candidate to Parent which may adjust it's position and then update Graph entry bounds
        return new CaseManagementAddChildNodeCanvasCommand(parent,
                                                           candidate,
                                                           shapeSetId,
                                                           getNewChildCanvasIndex(parent));
    }
}
