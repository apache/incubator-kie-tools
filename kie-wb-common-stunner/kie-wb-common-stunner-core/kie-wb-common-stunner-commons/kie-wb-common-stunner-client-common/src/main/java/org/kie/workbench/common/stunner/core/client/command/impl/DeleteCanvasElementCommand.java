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
import org.kie.workbench.common.stunner.core.client.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;

public abstract class DeleteCanvasElementCommand<E extends Element> extends AbstractCanvasGraphCommand {

    protected E candidate;
    protected Node parent;
    protected ShapeFactory factory;

    public DeleteCanvasElementCommand( final E candidate ) {
        this.candidate = candidate;
        this.parent = getParent();
    }

    public DeleteCanvasElementCommand( final E candidate, final Node parent ) {
        this.candidate = candidate;
        this.parent = parent;
    }

    @Override
    public CommandResult<CanvasViolation> doExecute( final AbstractCanvasHandler context ) {
        this.factory = ShapeUtils.getDefaultShapeFactory( context, candidate );
        doDeregister( context );
        return buildResult();
    }

    protected void doDeregister( final AbstractCanvasHandler context ) {
        context.deregister( candidate );
    }

    protected Node getParent() {
        return null;
    }

    ;

}
