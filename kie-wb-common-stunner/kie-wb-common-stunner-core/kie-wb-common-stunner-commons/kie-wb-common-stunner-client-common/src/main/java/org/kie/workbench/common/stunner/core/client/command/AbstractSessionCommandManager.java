/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSessionManager;
import org.kie.workbench.common.stunner.core.command.*;
import org.kie.workbench.common.stunner.core.command.exception.CommandException;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Command manager used in a client session context. It delegates to each session's command manager and keeps
 * each session's command registry synchronized as well.
 */
public abstract class AbstractSessionCommandManager
        extends DelegateCommandManager<AbstractCanvasHandler, CanvasViolation>
        implements SessionCommandManager<AbstractCanvasHandler> {

    private static Logger LOGGER = Logger.getLogger( AbstractSessionCommandManager.class.getName() );

    protected abstract AbstractClientSessionManager getClientSessionManager();

    protected abstract CommandListener<AbstractCanvasHandler, CanvasViolation> getRegistryListener();

    @Override
    public CommandResult<CanvasViolation> execute( final AbstractCanvasHandler context,
                                                   final Command<AbstractCanvasHandler, CanvasViolation> command ) {
        try {
            return super.execute( context, command );
        } catch ( final CommandException ce ) {
            getClientSessionManager().handleCommandError( ce );
        } catch ( final RuntimeException e ) {
            getClientSessionManager().handleClientError( new ClientRuntimeError( e ) );
        }
        return CanvasCommandResultBuilder.FAILED;
    }

    @Override
    public CommandResult<CanvasViolation> undo( final AbstractCanvasHandler context ) {
        final Command<AbstractCanvasHandler, CanvasViolation> lastEntry = getRegistry().peek();
        if ( null != lastEntry ) {
            try {
                return getDelegate().undo( context, lastEntry );
            } catch ( final CommandException ce ) {
                getClientSessionManager().handleCommandError( ce );
            } catch ( final RuntimeException e ) {
                getClientSessionManager().handleClientError( new ClientRuntimeError( e ) );
            }
            return CanvasCommandResultBuilder.FAILED;
        }
        return null;

    }

    public ClientSession<AbstractCanvas, AbstractCanvasHandler> getCurrentSession() {
        return getClientSessionManager().getCurrentSession();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected CommandManager<AbstractCanvasHandler, CanvasViolation> getDelegate() {
        final ClientFullSession<AbstractCanvas, AbstractCanvasHandler> session = getFullSession();
        if ( null != session ) {
            final CanvasCommandManager<AbstractCanvasHandler> commandManager = session.getCommandManager();
            try {
                final HasCommandListener<CommandListener<AbstractCanvasHandler, CanvasViolation>> hasCommandListener =
                        ( HasCommandListener<CommandListener<AbstractCanvasHandler, CanvasViolation>> ) commandManager;
                hasCommandListener.setCommandListener( getRegistryListener() );
            } catch ( final ClassCastException e ) {
                LOGGER.log( Level.WARNING, "Current command manager for canvas does not support" +
                        "command listeners. Session's registry cannot be updated." );
            }
            return commandManager;
        }
        return null;
    }

    @Override
    public CommandRegistry<Command<AbstractCanvasHandler, CanvasViolation>> getRegistry() {
        final ClientFullSession<AbstractCanvas, AbstractCanvasHandler> session = getFullSession();
        if ( null != session ) {
            return session.getCommandRegistry();
        }
        return null;
    }

    private ClientFullSession<AbstractCanvas, AbstractCanvasHandler> getFullSession() {
        final ClientSession<AbstractCanvas, AbstractCanvasHandler> session = getCurrentSession();
        try {
            return ( ClientFullSession<AbstractCanvas, AbstractCanvasHandler> ) session;
        } catch ( final ClassCastException e ) {
            LOGGER.log( Level.WARNING, "Session is not type of client full session." );
            return null;
        }
    }

    @Override
    public String toString() {
        return "[" + getClass().getName() + "] - Current session = ["
                + ( null != getCurrentSession() ? getCurrentSession().toString() : "null" ) + "]";
    }

}