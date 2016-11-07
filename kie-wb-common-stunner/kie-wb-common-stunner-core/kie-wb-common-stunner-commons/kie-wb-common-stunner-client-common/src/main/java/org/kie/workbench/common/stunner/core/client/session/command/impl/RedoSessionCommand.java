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

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasUndoCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.stack.StackCommandManager;
import org.kie.workbench.common.stunner.core.command.util.RedoCommandHandler;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Collection;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class RedoSessionCommand extends AbstractClientSessionCommand<AbstractClientFullSession> {

    private final RedoCommandHandler<CanvasCommand<AbstractCanvasHandler>> redoCommandHandler;

    protected RedoSessionCommand() {
        this( null );
    }

    @Inject
    public RedoSessionCommand( final RedoCommandHandler<CanvasCommand<AbstractCanvasHandler>> redoCommandHandler ) {
        super( false );
        this.redoCommandHandler = redoCommandHandler;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> void execute( final Callback<T> callback ) {
        checkNotNull( "callback", callback );
        final StackCommandManager<AbstractCanvasHandler, CanvasViolation> scm = getStackCommandManager();
        final CommandResult<?> result = redoCommandHandler.execute( getSession().getCanvasHandler(), scm );
        checkState();
        callback.onSuccess( ( T ) result );
    }

    @Override
    public void unbind() {
        super.unbind();
        redoCommandHandler.clear();
    }

    @SuppressWarnings( "unchecked" )
    void onCommandExecuted( @Observes CanvasCommandExecutedEvent commandExecutedEvent ) {
        checkNotNull( "commandExecutedEvent", commandExecutedEvent );
        if ( null != commandExecutedEvent.getCommand() ) {
            redoCommandHandler
                    .onCommandExecuted( ( CanvasCommand<AbstractCanvasHandler> ) commandExecutedEvent.getCommand() );
        } else {
            redoCommandHandler.onCommandExecuted( commandExecutedEvent.getCommands() );
        }
        checkState();
    }

    @SuppressWarnings( "unchecked" )
    void onCommandUndoExecuted( @Observes CanvasUndoCommandExecutedEvent commandUndoExecutedEvent ) {
        checkNotNull( "commandUndoExecutedEvent", commandUndoExecutedEvent );
        final CanvasCommand<AbstractCanvasHandler> command = ( CanvasCommand<AbstractCanvasHandler> ) commandUndoExecutedEvent.getCommand();
        final Collection<CanvasCommand<AbstractCanvasHandler>> commands = commandUndoExecutedEvent.getCommands();
        if ( null != command ) {
            redoCommandHandler.onUndoCommandExecuted( command );
        } else {
            redoCommandHandler.onUndoCommandExecuted( commands );
        }
        checkState();
    }

    private void checkState() {
        setEnabled( null != getSession() && redoCommandHandler.isEnabled() );
        fire();
    }

    @SuppressWarnings( "unchecked" )
    private StackCommandManager<AbstractCanvasHandler, CanvasViolation> getStackCommandManager() {
        try {
            return ( StackCommandManager<AbstractCanvasHandler, CanvasViolation> ) getSession().getCanvasCommandManager();
        } catch ( ClassCastException e ) {
            return null;
        }
    }

}
