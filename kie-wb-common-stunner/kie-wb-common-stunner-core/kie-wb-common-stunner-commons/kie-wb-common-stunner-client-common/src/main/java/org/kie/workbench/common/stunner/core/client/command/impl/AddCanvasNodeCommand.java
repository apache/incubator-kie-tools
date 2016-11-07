/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.command.impl;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.AddNodeCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public final class AddCanvasNodeCommand extends AddCanvasElementCommand<Node> {

    public AddCanvasNodeCommand( final Node candidate, final ShapeFactory factory ) {
        super( candidate, factory );
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> buildGraphCommand( final AbstractCanvasHandler context ) {
        return new AddNodeCommand( candidate );
    }

    @Override
    protected AbstractCanvasCommand buildUndoCommand( final AbstractCanvasHandler context ) {
        return new DeleteCanvasNodeCommand( candidate );
    }

}
