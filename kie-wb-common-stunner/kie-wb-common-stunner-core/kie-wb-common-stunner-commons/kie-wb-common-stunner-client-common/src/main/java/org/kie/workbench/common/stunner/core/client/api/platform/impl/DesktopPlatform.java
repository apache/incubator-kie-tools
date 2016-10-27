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

package org.kie.workbench.common.stunner.core.client.api.platform.impl;

import org.kie.workbench.common.stunner.core.client.api.platform.AbstractClientPlatform;
import org.kie.workbench.common.stunner.core.client.api.platform.ClientPlatform;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientReadOnlySession;
import org.kie.workbench.common.stunner.core.client.session.ClientSessionProducer;
import org.kie.workbench.common.stunner.core.client.session.impl.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class DesktopPlatform extends AbstractClientPlatform {

    private static final String[] USER_AGENTS = { "" };

    Instance<ClientReadOnlySessionImpl> readOnlySessions;
    Instance<ClientFullSessionImpl> fullSessions;

    protected DesktopPlatform() {
        this( null, null );
    }

    @Inject
    public DesktopPlatform( final Instance<ClientReadOnlySessionImpl> readOnlySessions,
                            final Instance<ClientFullSessionImpl> fullSessions ) {
        this.readOnlySessions = readOnlySessions;
        this.fullSessions = fullSessions;
    }

    @Override
    public boolean supports( final String platform ) {
        return null != platform && !isMobilePlatform( platform );
    }

    public static boolean isMobilePlatform( final String platform ) {
        return platform.contains( "arm" ) || platform.contains( "iOS" );
    }

    @Override
    public String[] getUserAgents() {
        return USER_AGENTS;
    }

    @Override
    public ClientReadOnlySession<AbstractCanvas, AbstractCanvasHandler> newReadOnlySession() {
        return readOnlySessions.get();
    }

    @Override
    public ClientFullSession<AbstractCanvas, AbstractCanvasHandler> newFullSession() {
        return fullSessions.get();
    }

}
