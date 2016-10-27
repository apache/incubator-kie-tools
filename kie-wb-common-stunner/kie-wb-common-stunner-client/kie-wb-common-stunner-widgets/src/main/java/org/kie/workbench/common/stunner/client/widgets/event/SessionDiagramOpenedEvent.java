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

package org.kie.workbench.common.stunner.client.widgets.event;

import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.uberfire.workbench.events.UberFireEvent;

/**
 * <p>Event when a ldiagram has been loaded and drawn into the session's canvas.</p>
 */
public final class SessionDiagramOpenedEvent implements UberFireEvent {

    private final ClientSession session;

    public SessionDiagramOpenedEvent( final ClientSession session ) {
        this.session = session;
    }

    public ClientSession getSession() {
        return session;
    }

    @Override
    public String toString() {
        return "SessionDiagramOpenedEvent [session=" + session.toString() + "]";
    }

}
