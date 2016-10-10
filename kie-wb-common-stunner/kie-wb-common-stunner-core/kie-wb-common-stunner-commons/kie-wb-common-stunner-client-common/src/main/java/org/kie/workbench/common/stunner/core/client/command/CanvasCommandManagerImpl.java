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

package org.kie.workbench.common.stunner.core.client.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandAllowedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasUndoCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.command.*;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandManager;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandManagerListener;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandResult;
import org.kie.workbench.common.stunner.core.command.delegate.BatchDelegateCommandManager;

import javax.enterprise.event.Event;
import java.util.Collection;

class CanvasCommandManagerImpl
        extends BatchDelegateCommandManager<AbstractCanvasHandler, CanvasViolation>
        implements
        CanvasCommandManager<AbstractCanvasHandler>,
        HasCommandManagerListener<BatchCommandManagerListener<AbstractCanvasHandler, CanvasViolation>> {

    private final Event<CanvasCommandAllowedEvent> isCanvasCommandAllowedEvent;
    private final Event<CanvasCommandExecutedEvent> canvasCommandExecutedEvent;
    private final Event<CanvasUndoCommandExecutedEvent> canvasUndoCommandExecutedEvent;

    private final BatchCommandManager<AbstractCanvasHandler, CanvasViolation> commandManager;
    private BatchCommandManagerListener<AbstractCanvasHandler, CanvasViolation> listener;

    CanvasCommandManagerImpl() {
        this( null, null, null, null );
    }

    CanvasCommandManagerImpl( final Event<CanvasCommandAllowedEvent> isCanvasCommandAllowedEvent,
                              final Event<CanvasCommandExecutedEvent> canvasCommandExecutedEvent,
                              final Event<CanvasUndoCommandExecutedEvent> canvasUndoCommandExecutedEvent,
                              final CommandManagerFactory commandManagerFactory ) {
        this.isCanvasCommandAllowedEvent = isCanvasCommandAllowedEvent;
        this.canvasCommandExecutedEvent = canvasCommandExecutedEvent;
        this.canvasUndoCommandExecutedEvent = canvasUndoCommandExecutedEvent;
        this.commandManager = commandManagerFactory.newBatchCommandManager();
        this.listener = null;
    }

    @Override
    protected BatchCommandManager<AbstractCanvasHandler, CanvasViolation> getBatchDelegate() {
        return commandManager;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected void postAllow( final AbstractCanvasHandler context,
                              final Command<AbstractCanvasHandler, CanvasViolation> command,
                              final CommandResult<CanvasViolation> result ) {
        super.postAllow( context, command, result );
        if ( null != this.listener ) {
            listener.onAllow( context, command, result );

        }
        if ( null != result && null != isCanvasCommandAllowedEvent ) {
            isCanvasCommandAllowedEvent.fire( new CanvasCommandAllowedEvent( context, command, result ) );

        }

    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected void postExecute( final AbstractCanvasHandler context,
                                final Command<AbstractCanvasHandler, CanvasViolation> command,
                                final CommandResult<CanvasViolation> result ) {
        super.postExecute( context, command, result );
        if ( null != result && !CommandUtils.isError( result ) ) {
            draw( context );

        }
        if ( null != this.listener ) {
            listener.onExecute( context, command, result );

        }
        if ( null != result && null != canvasCommandExecutedEvent ) {
            canvasCommandExecutedEvent.fire( new CanvasCommandExecutedEvent( context, command, result ) );
        }

    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected void postExecuteBatch( final AbstractCanvasHandler context,
                                     final Collection<Command<AbstractCanvasHandler, CanvasViolation>> commands,
                                     final BatchCommandResult<CanvasViolation> result ) {
        super.postExecuteBatch( context, commands, result );
        if ( null != result && !CommandUtils.isError( result ) ) {
            draw( context );

        }
        if ( null != this.listener ) {
            listener.onExecuteBatch( context, commands, result );

        }
        if ( null != canvasCommandExecutedEvent ) {
            canvasCommandExecutedEvent.fire( new CanvasCommandExecutedEvent( context, commands, result ) );
        }

    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected void postUndo( final AbstractCanvasHandler context,
                             final Command<AbstractCanvasHandler, CanvasViolation> command,
                             final CommandResult<CanvasViolation> result ) {
        super.postUndo( context, command, result );
        if ( null != result && !CommandUtils.isError( result ) ) {
            draw( context );

        }
        if ( null != this.listener ) {
            listener.onUndo( context, command, result );

        }
        if ( null != canvasUndoCommandExecutedEvent ) {
            canvasUndoCommandExecutedEvent.fire( new CanvasUndoCommandExecutedEvent( context, command, result ) );
        }

    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected void postUndo( final AbstractCanvasHandler context,
                             final Collection<Command<AbstractCanvasHandler, CanvasViolation>> commands,
                             final CommandResult<CanvasViolation> result ) {
        super.postUndo( context, commands, result );
        if ( null != result && !CommandUtils.isError( result ) ) {
            draw( context );

        }
        if ( null != this.listener ) {
            listener.onUndoBatch( context, commands, result );

        }
        if ( null != canvasUndoCommandExecutedEvent ) {
            canvasUndoCommandExecutedEvent.fire( new CanvasUndoCommandExecutedEvent( context, commands, result ) );
        }

    }

    private void draw( final AbstractCanvasHandler context ) {
        context.getCanvas().draw();
    }

    @Override
    public void setCommandManagerListener( final BatchCommandManagerListener<AbstractCanvasHandler, CanvasViolation> listener ) {
        this.listener = listener;
    }
}
