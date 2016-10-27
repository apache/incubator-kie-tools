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

package org.kie.workbench.common.stunner.client.widgets.session.presenter.impl;

import org.kie.workbench.common.stunner.client.widgets.event.SessionDiagramOpenedEvent;
import org.kie.workbench.common.stunner.client.widgets.session.presenter.ClientSessionPresenter;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDisposedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionPausedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSessionManager;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

/**
 * Session presenter implementation that handles a session by displaying the loading and canvas views.
 */
@Dependent
public class ClientSessionPresenterImpl
        extends AbstractClientSessionPresenter<AbstractClientSession, ClientSessionPresenter.View> {

    protected ClientSessionPresenterImpl() {
        this( null, null, null );
    }

    @Inject
    public ClientSessionPresenterImpl( final AbstractClientSessionManager canvasSessionManager,
                                       final Event<SessionDiagramOpenedEvent> sessionDiagramOpenedEvent,
                                       final ClientSessionPresenter.View view ) {
        super( canvasSessionManager, sessionDiagramOpenedEvent, view );
    }

    @Override
    protected void doDisposeSession() {
    }

    @Override
    protected void doPauseSession() {
    }

    private void onCanvasSessionDisposed( @Observes SessionDisposedEvent sessionDisposedEvent ) {
        checkNotNull( "sessionDisposedEvent", sessionDisposedEvent );
        if ( null != getSession() && getSession().equals( sessionDisposedEvent.getSession() ) ) {
            disposeSession();
        }
    }

    private void onCanvasSessionPaused( @Observes SessionPausedEvent sessionPausedEvent ) {
        checkNotNull( "sessionPausedEvent", sessionPausedEvent );
        if ( null != getSession() && getSession().equals( sessionPausedEvent.getSession() ) ) {
            pauseSession();
        }
    }

}
