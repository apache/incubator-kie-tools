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

package org.kie.workbench.common.stunner.core.client.api.platform;

import org.kie.workbench.common.stunner.core.client.session.impl.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class DesktopPlatform implements ClientPlatform, DefaultCanvasSessionProducer {

    private static final String[] USER_AGENTS = { "" };

    Instance<CanvasReadOnlySessionImpl> readOnlySessions;
    Instance<CanvasFullSessionImpl> fullSessions;

    protected DesktopPlatform() {
        this( null, null );
    }

    @Inject
    public DesktopPlatform( final Instance<CanvasReadOnlySessionImpl> readOnlySessions,
                            final Instance<CanvasFullSessionImpl> fullSessions ) {
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
    public DefaultCanvasReadOnlySession newReadOnlySession() {
        return readOnlySessions.get();
    }

    @Override
    public DefaultCanvasFullSession newFullSession() {
        return fullSessions.get();
    }

}
