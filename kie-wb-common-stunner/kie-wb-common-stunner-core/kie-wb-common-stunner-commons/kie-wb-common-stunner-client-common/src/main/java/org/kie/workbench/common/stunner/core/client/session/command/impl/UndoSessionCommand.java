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
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientFullSession;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.stack.StackCommandManager;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class UndoSessionCommand extends AbstractClientSessionCommand<AbstractClientFullSession> {

    public UndoSessionCommand() {
        super( false );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> void execute( final Callback<T> callback ) {
        final StackCommandManager<AbstractCanvasHandler, CanvasViolation> scm = getStackCommandManager();
        if ( null != scm ) {
            final CommandResult<CanvasViolation> result =
                    getStackCommandManager().undo( getSession().getCanvasHandler() );
            if ( null != callback ) {
                callback.onSuccess( ( T ) result );
            }
            checkState();
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
        if ( null != getSession() ) {
            final StackCommandManager<AbstractCanvasHandler, CanvasViolation> canvasCommManager = getStackCommandManager();
            final boolean isHistoryEmpty = canvasCommManager == null || canvasCommManager.getRegistry().getCommandHistorySize() == 0;
            setEnabled( !isHistoryEmpty );
        } else {
            setEnabled( false );
        }
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
