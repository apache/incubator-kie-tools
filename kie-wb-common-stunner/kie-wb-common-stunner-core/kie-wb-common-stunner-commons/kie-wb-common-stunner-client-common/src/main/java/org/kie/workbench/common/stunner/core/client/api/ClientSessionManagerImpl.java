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

package org.kie.workbench.common.stunner.core.client.api;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSessionFactory;
import org.kie.workbench.common.stunner.core.client.session.event.OnSessionErrorEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionOpenedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionPausedEvent;
import org.kie.workbench.common.stunner.core.client.session.event.SessionResumedEvent;
import org.kie.workbench.common.stunner.core.command.exception.CommandException;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
public class ClientSessionManagerImpl extends AbstractClientSessionManager {

    private final DefinitionUtils definitionUtils;
    private final ManagedInstance<ClientSessionFactory> sessionFactoriesInstances;
    private final Event<SessionOpenedEvent> sessionOpenedEvent;
    private final Event<SessionPausedEvent> sessionPausedEvent;
    private final Event<SessionResumedEvent> sessionResumedEvent;
    private final Event<SessionDestroyedEvent> sessionDestroyedEvent;
    private final Event<OnSessionErrorEvent> sessionErrorEvent;

    protected ClientSessionManagerImpl() {
        this(null,
             null,
             null,
             null,
             null,
             null,
             null);
    }

    @Inject
    public ClientSessionManagerImpl(final DefinitionUtils definitionUtils,
                                    final @Any ManagedInstance<ClientSessionFactory> sessionFactoriesInstances,
                                    final Event<SessionOpenedEvent> sessionOpenedEvent,
                                    final Event<SessionDestroyedEvent> sessionDestroyedEvent,
                                    final Event<SessionPausedEvent> sessionPausedEvent,
                                    final Event<SessionResumedEvent> sessionResumedEvent,
                                    final Event<OnSessionErrorEvent> sessionErrorEvent) {
        this.definitionUtils = definitionUtils;
        this.sessionFactoriesInstances = sessionFactoriesInstances;
        this.sessionOpenedEvent = sessionOpenedEvent;
        this.sessionPausedEvent = sessionPausedEvent;
        this.sessionResumedEvent = sessionResumedEvent;
        this.sessionDestroyedEvent = sessionDestroyedEvent;
        this.sessionErrorEvent = sessionErrorEvent;
    }

    @Override
    protected List<ClientSessionFactory> getFactories(final Metadata metadata) {
        checkNotNull("metadata",
                     metadata);
        final String defSetId = metadata.getDefinitionSetId();
        final Annotation qualifier = definitionUtils.getQualifier(defSetId);
        final List<ClientSessionFactory> result = new LinkedList<>();
        sessionFactoriesInstances.select(qualifier).forEach(result::add);
        // If no custom session factories for this diagram, look for the default ones.
        if (result.isEmpty()) {
            sessionFactoriesInstances.select(DefinitionManager.DEFAULT_QUALIFIER).forEach(result::add);
        }
        return result;
    }

    public void postOpen() {
        this.sessionOpenedEvent.fire(new SessionOpenedEvent(current));
    }

    public void postPause() {
        this.sessionPausedEvent.fire(new SessionPausedEvent(current));
    }

    public void postResume() {
        this.sessionResumedEvent.fire(new SessionResumedEvent(current));
    }

    @Override
    public void destroy() {
        SessionDestroyedEvent destroyedEvent = null;
        if (null != current) {
            final String uuid = current.getSessionUUID();
            final Diagram diagram = current.getCanvasHandler().getDiagram();
            final String name = null != diagram ? diagram.getName() : null;
            final String graphUuid = null != diagram ? diagram.getGraph().getUUID() : null;
            final Metadata metadata = null != diagram ? diagram.getMetadata() : null;
            destroyedEvent = new SessionDestroyedEvent(uuid,
                                                       name,
                                                       graphUuid,
                                                       metadata);
        }
        super.destroy();
        if (null != destroyedEvent) {
            this.sessionDestroyedEvent.fire(destroyedEvent);
        }
    }

    @Override
    public void handleCommandError(final CommandException ce) {
        super.handleCommandError(ce);
        sessionErrorEvent.fire(new OnSessionErrorEvent(current,
                                                       new ClientRuntimeError("Error while executing command.",
                                                                              ce)));
    }

    @Override
    public void handleClientError(final ClientRuntimeError error) {
        super.handleClientError(error);
        sessionErrorEvent.fire(new OnSessionErrorEvent(current,
                                                       error));
    }
}
