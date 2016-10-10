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

package org.kie.workbench.common.stunner.core.client.session.impl;

import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.core.client.api.platform.ClientPlatform;
import org.kie.workbench.common.stunner.core.client.api.platform.PlatformManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.CanvasSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDisposedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionPausedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionResumedEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: Multiple sessions support.
@ApplicationScoped
public class CanvasSessionManagerImpl implements DefaultCanvasSessionManager {

    private static Logger LOGGER = Logger.getLogger( CanvasSessionManagerImpl.class.getName() );

    PlatformManager platformManager;
    Event<SessionOpenedEvent> sessionOpenedEvent;
    Event<SessionPausedEvent> sessionPausedEvent;
    Event<SessionResumedEvent> sessionResumedEvent;
    Event<SessionDisposedEvent> sessionDisposedEvent;

    private CanvasSession<AbstractCanvas, AbstractCanvasHandler> current;

    protected CanvasSessionManagerImpl() {
    }

    @Inject
    public CanvasSessionManagerImpl( final PlatformManager platformManager,
                                     final Event<SessionOpenedEvent> sessionOpenedEvent,
                                     final Event<SessionDisposedEvent> sessionDisposedEvent,
                                     final Event<SessionPausedEvent> sessionPausedEvent,
                                     final Event<SessionResumedEvent> sessionResumedEvent ) {
        this.platformManager = platformManager;
        this.sessionOpenedEvent = sessionOpenedEvent;
        this.sessionPausedEvent = sessionPausedEvent;
        this.sessionResumedEvent = sessionResumedEvent;
        this.sessionDisposedEvent = sessionDisposedEvent;
    }

    @Override
    public CanvasSession<AbstractCanvas, AbstractCanvasHandler> getCurrentSession() {
        return current;
    }

    @Override
    public void open( final CanvasSession<AbstractCanvas, AbstractCanvasHandler> session ) {
        if ( null != this.current && !session.equals( this.current ) ) {
            this.dispose();
        }
        if ( !session.equals( this.current ) ) {
            this.current = session;
            this.current.onOpen();
            this.sessionOpenedEvent.fire( new SessionOpenedEvent( current ) );
            log( Level.FINE, "Session [" + current.toString() + "] opened" );
        }

    }

    @Override
    public void pause() {
        // TODO
        if ( null != current ) {
            this.sessionPausedEvent.fire( new SessionPausedEvent( current ) );
            log( Level.FINE, "Session [" + current.toString() + "] paused" );
        }

    }

    @Override
    public void resume( final CanvasSession<AbstractCanvas, AbstractCanvasHandler> session ) {
        // TODO
        if ( null != current && !current.equals( session ) ) {
            pause();
        }
        if ( !session.equals( current ) ) {
            this.current = session;
            this.sessionResumedEvent.fire( new SessionResumedEvent( session ) );
            log( Level.FINE, "Session [" + current.toString() + "] resumed" );
        }

    }

    @Override
    public void dispose() {
        if ( null != current ) {
            this.current.onDispose();
            this.sessionDisposedEvent.fire( new SessionDisposedEvent( current ) );
            log( Level.FINE, "Session [" + current.toString() + "] disposed" );
            this.current = null;
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public DefaultCanvasReadOnlySession newReadOnlySession() {
        final ClientPlatform platform = getPlatform();
        DefaultCanvasReadOnlySession session = null;
        if ( platform instanceof DefaultCanvasSessionProducer ) {
            final DefaultCanvasSessionProducer sessionProducer = ( DefaultCanvasSessionProducer ) platform;
            session = sessionProducer.newReadOnlySession();

        }
        return session;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public DefaultCanvasFullSession newFullSession() {
        final ClientPlatform platform = getPlatform();
        DefaultCanvasFullSession session = null;
        if ( platform instanceof DefaultCanvasSessionProducer ) {
            final DefaultCanvasSessionProducer sessionProducer = ( DefaultCanvasSessionProducer ) platform;
            session = sessionProducer.newFullSession();

        }
        return session;
    }

    protected ClientPlatform getPlatform() {
        return platformManager.getCurrentPlatform();

    }

    private void log( final Level level, final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level, message );
        }
    }

}
