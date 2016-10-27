/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasUndoCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.factory.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Iterator;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class ClearSessionCommand extends AbstractClientSessionCommand<AbstractClientFullSession> {

    private CanvasCommandFactory canvasCommandFactory;

    @Inject
    public ClearSessionCommand( final CanvasCommandFactory canvasCommandFactory ) {
        super( false );
        this.canvasCommandFactory = canvasCommandFactory;
    }

    @Override
    public AbstractClientSessionCommand<AbstractClientFullSession> bind( AbstractClientFullSession session ) {
        super.bind( session );
        checkState();
        return this;
    }

    @Override
    public <T> void execute( final Callback<T> callback ) {
        final CommandResult<CanvasViolation> result =
                getSession().getCanvasCommandManager()
                        .execute( getSession().getCanvasHandler(),
                                canvasCommandFactory.CLEAR_CANVAS() );
        if ( null != callback ) {
            callback.onSuccess( ( T ) result );
        }
    }

    void onCommandExecuted( @Observes CanvasCommandExecutedEvent commandExecutedEvent ) {
        checkNotNull( "commandExecutedEvent", commandExecutedEvent );
        checkState();
    }

    void onCommandUndoExecuted( @Observes CanvasUndoCommandExecutedEvent commandUndoExecutedEvent ) {
        checkNotNull( "commandUndoExecutedEvent", commandUndoExecutedEvent );
        checkState();
    }

    private void checkState() {
        setEnabled( getState() );
        fire();
    }

    @SuppressWarnings( "unchecked" )
    private boolean getState() {
        boolean doEnable = false;
        final Diagram diagram = null != getSession() ? getSession().getCanvasHandler().getDiagram() : null;
        if ( null != diagram ) {
            final Graph graph = diagram.getGraph();
            if ( null != graph ) {
                final String rootUUID = diagram.getMetadata().getCanvasRootUUID();
                Iterable<Node> nodes = graph.nodes();
                final boolean hasNodes = null != nodes && nodes.iterator().hasNext();
                if ( hasNodes ) {
                    final Iterator<Node> nodesIt = nodes.iterator();
                    final Node node = nodesIt.next();
                    if ( nodesIt.hasNext() ) {
                        doEnable = true;

                    } else {
                        doEnable = null == rootUUID || !rootUUID.equals( node.getUUID() );
                    }

                }

            }

        }
        return doEnable;

    }

}
