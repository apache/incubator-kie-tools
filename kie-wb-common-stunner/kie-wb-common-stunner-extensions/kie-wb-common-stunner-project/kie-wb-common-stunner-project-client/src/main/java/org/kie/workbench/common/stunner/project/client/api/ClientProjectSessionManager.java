/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.project.client.api;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.api.ClientSessionManagerImpl;
import org.kie.workbench.common.stunner.core.client.session.ClientSessionFactory;
import org.kie.workbench.common.stunner.core.client.session.event.OnSessionErrorEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionPausedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionResumedEvent;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@ApplicationScoped
@Specializes
public class ClientProjectSessionManager extends ClientSessionManagerImpl {

    protected ClientProjectSessionManager() {
        this(null,
             null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public ClientProjectSessionManager(final DefinitionUtils definitionUtils,
                                       final @Any ManagedInstance<ClientSessionFactory> sessionFactoriesInstances,
                                       final Event<SessionOpenedEvent> sessionOpenedEvent,
                                       final Event<SessionDestroyedEvent> sessionDestroyedEvent,
                                       final Event<SessionPausedEvent> sessionPausedEvent,
                                       final Event<SessionResumedEvent> sessionResumedEvent,
                                       final Event<OnSessionErrorEvent> sessionErrorEvent) {
        super(definitionUtils,
              sessionFactoriesInstances,
              sessionOpenedEvent,
              sessionDestroyedEvent,
              sessionPausedEvent,
              sessionResumedEvent,
              sessionErrorEvent);
    }
}
