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
import org.kie.workbench.common.stunner.core.client.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public final class UpdateCanvasElementPositionCommand extends AbstractCanvasGraphCommand {

    private final Element<?> element;
    private final Double x;
    private final Double y;

    public UpdateCanvasElementPositionCommand( final Element<?> element,
                                               final Double x,
                                               final Double y ) {
        this.element = element;
        this.x = x;
        this.y = y;
    }

    @Override
    public CommandResult<CanvasViolation> doCanvasExecute( final AbstractCanvasHandler context ) {
        context.updateElementPosition( element, MutationContext.STATIC );
        return buildResult();
    }

    @Override
    protected AbstractCanvasCommand buildUndoCommand( final AbstractCanvasHandler context ) {
        return null;
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> buildGraphCommand( final AbstractCanvasHandler context ) {
        return new UpdateElementPositionCommand( element.getUUID(), x, y );
    }

}
