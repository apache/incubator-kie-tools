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

package org.kie.workbench.common.stunner.project.client.session.impl;

import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.core.client.api.platform.PlatformManager;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDisposedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionPausedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionResumedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.ClientSessionManagerImpl;
import org.kie.workbench.common.stunner.project.client.screens.ProjectDiagramWorkbenchDocks;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Specializes
public class ClientProjectSessionManager extends ClientSessionManagerImpl {

    private static Logger LOGGER = Logger.getLogger( ClientProjectSessionManager.class.getName() );
    private final ProjectDiagramWorkbenchDocks editorDocks;

    protected ClientProjectSessionManager() {
        this( null, null, null, null, null, null );
    }

    @Inject
    public ClientProjectSessionManager( final ProjectDiagramWorkbenchDocks editorDocks,
                                        final PlatformManager platformManager,
                                        final Event<SessionOpenedEvent> sessionOpenedEvent,
                                        final Event<SessionDisposedEvent> sessionDisposedEvent,
                                        final Event<SessionPausedEvent> sessionPausedEvent,
                                        final Event<SessionResumedEvent> sessionResumedEvent ) {
        super( platformManager, sessionOpenedEvent, sessionDisposedEvent, sessionPausedEvent, sessionResumedEvent );
        this.editorDocks = editorDocks;
    }

    @Override
    protected void postOpen() {
        // Ensure docks are enabled before firing session events, so inner dock components, if opened, could be able to observe those.
        editorDocks.enableDocks();
        super.postOpen();
    }

    @Override
    protected void postResume() {
        // Ensure docks are enabled before firing session events, so inner dock components, if opened, could be able to observe those.
        editorDocks.enableDocks();
        super.postResume();
    }

    @Override
    protected void postDispose() {
        super.postDispose();
        // Once session is disposed, disable the docks area for the Stunner's editor.
        editorDocks.disableDocks();
    }

    private void log( final Level level, final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level, message );
        }
    }

}
