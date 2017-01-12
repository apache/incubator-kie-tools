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

package org.kie.workbench.common.stunner.core.client.session.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSessionManager;
import org.kie.workbench.common.stunner.core.command.exception.CommandException;

public abstract class AbstractClientSessionManager implements ClientSessionManager<AbstractCanvas, AbstractCanvasHandler, AbstractClientSession> {

    private static Logger LOGGER = Logger.getLogger( AbstractClientSessionManager.class.getName() );

    AbstractClientSession current;

    protected abstract void postOpen();

    protected abstract void postPause();

    protected abstract void postResume();

    protected abstract void postDispose();

    @Override
    public AbstractClientSession getCurrentSession() {
        return current;
    }

    @Override
    public void open( final AbstractClientSession session ) {
        if ( null != this.current && !session.equals( this.current ) ) {
            this.pause();
        }
        if ( !session.equals( this.current ) ) {
            log( Level.FINE,
                 "Opening session [" + session.toString() + "] ..." );
            this.current = session;
            this.current.open();
            postOpen();
            log( Level.FINE,
                 "Session [" + current.toString() + "] opened" );
        }
    }

    @Override
    public void pause() {
        if ( null != current ) {
            log( Level.FINE,
                 "Pausing session [" + current.toString() + "] ..." );
            this.current.pause();
            postPause();
            log( Level.FINE,
                 "Session [" + current.toString() + "] paused" );
        }
    }

    @Override
    public void resume( final AbstractClientSession session ) {
        if ( null != current && !current.equals( session ) ) {
            pause();
        }
        if ( !session.equals( current ) ) {
            log( Level.FINE,
                 "Resuming session [" + session.toString() + "] ..." );
            this.current = session;
            this.current.resume();
            postResume();
            log( Level.FINE,
                 "Session [" + current.toString() + "] resumed" );
        }
    }

    @Override
    public void dispose() {
        if ( null != current ) {
            log( Level.FINE,
                 "Disposing session [" + current.toString() + "] ..." );
            this.current.dispose();
            postDispose();
            log( Level.FINE,
                 "Session [" + current.toString() + "] disposed" );
            this.current = null;
        }
    }

    public void handleCommandError( final CommandException ce ) {
        log( Level.SEVERE,
             "Command execution failed",
             ce );
    }

    public void handleClientError( final ClientRuntimeError error ) {
        log( Level.SEVERE,
             "An error on client side happened",
             error.getThrowable() );
    }

    private void log( final Level level,
                      final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level,
                        message );
        }
    }

    private void log( final Level level,
                      final String message,
                      final Throwable t ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level,
                        message,
                        t );
        }
    }
}
