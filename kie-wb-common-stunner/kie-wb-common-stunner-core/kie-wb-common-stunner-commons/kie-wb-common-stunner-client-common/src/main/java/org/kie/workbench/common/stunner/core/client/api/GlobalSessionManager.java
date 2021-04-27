/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.api;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.OnSessionErrorEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils;
import org.kie.workbench.common.stunner.core.command.exception.CommandException;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * Manages a single session by Window
 */
@ApplicationScoped
public class GlobalSessionManager implements SessionManager {

    private final Function<Metadata, Annotation> qualifierProvider;
    private final ManagedInstance<ClientSession> sessionInstances;
    private final Event<SessionOpenedEvent> sessionOpenedEvent;
    private final Event<SessionDestroyedEvent> sessionDestroyedEvent;
    private final Event<OnSessionErrorEvent> sessionErrorEvent;

    private AbstractSession current;

    protected GlobalSessionManager() {
        this(null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public GlobalSessionManager(final DefinitionUtils definitionUtils,
                                final @Any ManagedInstance<ClientSession> sessionInstances,
                                final Event<SessionOpenedEvent> sessionOpenedEvent,
                                final Event<SessionDestroyedEvent> sessionDestroyedEvent,
                                final Event<OnSessionErrorEvent> sessionErrorEvent) {
        this.qualifierProvider = metadata -> definitionUtils.getQualifier(metadata.getDefinitionSetId());
        this.sessionInstances = sessionInstances;
        this.sessionOpenedEvent = sessionOpenedEvent;
        this.sessionDestroyedEvent = sessionDestroyedEvent;
        this.sessionErrorEvent = sessionErrorEvent;
    }

    @Override
    public <S extends ClientSession> void newSession(final Metadata metadata,
                                                     final Class<S> sessionType,
                                                     final Consumer<S> sessionConsumer) {
        final S session = InstanceUtils.lookup(sessionInstances,
                                               sessionType,
                                               qualifierProvider.apply(metadata));
        ((AbstractSession) session).init(metadata,
                                         () -> sessionConsumer.accept(session));
    }

    @Override
    public <S extends ClientSession> void open(final S session) {
        checkNotNull("session",
                     session);
        if (!session.equals(current)) {
            current = (AbstractSession) session;
            current.open();
            sessionOpenedEvent.fire(new SessionOpenedEvent(current));
        }
    }

    @Override
    public <S extends ClientSession> void destroy(final S session) {
        final boolean isCurrent = session.equals(current);
        final String uuid = session.getSessionUUID();
        final Diagram diagram = session.getCanvasHandler().getDiagram();
        final String name = null != diagram ? diagram.getName() : null;
        final String graphUuid = null != diagram ? diagram.getGraph().getUUID() : null;
        final Metadata metadata = null != diagram ? diagram.getMetadata() : null;
        ((AbstractSession) session).destroy();
        sessionInstances.destroy(session);
        if (isCurrent) {
            current = null;
        }
        sessionDestroyedEvent.fire(new SessionDestroyedEvent(uuid,
                                                             name,
                                                             graphUuid,
                                                             metadata));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S extends ClientSession> S getCurrentSession() {
        return (S) current;
    }

    @Override
    public void handleCommandError(final CommandException ce) {
        sessionErrorEvent.fire(new OnSessionErrorEvent(current,
                                                       new ClientRuntimeError("Error while executing command.",
                                                                              ce)));
    }

    @Override
    public void handleClientError(final ClientRuntimeError error) {
        sessionErrorEvent.fire(new OnSessionErrorEvent(current,
                                                       error));
    }
}
