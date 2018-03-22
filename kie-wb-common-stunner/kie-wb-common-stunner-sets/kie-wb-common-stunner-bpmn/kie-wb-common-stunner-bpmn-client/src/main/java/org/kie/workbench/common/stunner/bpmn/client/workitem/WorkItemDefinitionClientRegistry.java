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

package org.kie.workbench.common.stunner.bpmn.client.workitem;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNImageResources;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionCacheRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionMetadataRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionService;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.mvp.Command;

/**
 * The default Work Item Definition Registry for client side.
 * <p>
 * - It manages the registries relying on the client session lifecycle.
 * - It produces the @Default WorkItemDefinitionRegistry based on current session
 * - It performs calls to server side to populate the current session's registry
 * - It destroy the registry, if any, when a session is being destroyed
 */
@ApplicationScoped
@Typed(WorkItemDefinitionClientRegistry.class)
public class WorkItemDefinitionClientRegistry
        implements WorkItemDefinitionRegistry {

    public static String DEFAULT_ICON_DATA = BPMNImageResources.INSTANCE.serviceNodeIcon().getSafeUri().asString();

    private final SessionManager sessionManager;
    private final Caller<WorkItemDefinitionService> service;
    private final Supplier<WorkItemDefinitionCacheRegistry> registryInstanceSupplier;
    private final Consumer<WorkItemDefinitionCacheRegistry> registryInstanceDestroyer;
    private final WorkItemDefinitionMetadataRegistry metadataRegistry;
    private final Map<String, WorkItemDefinitionCacheRegistry> sessionRegistries;

    @Inject
    public WorkItemDefinitionClientRegistry(final SessionManager sessionManager,
                                            final Caller<WorkItemDefinitionService> service,
                                            final ManagedInstance<WorkItemDefinitionCacheRegistry> registryInstances,
                                            final WorkItemDefinitionMetadataRegistry metadataRegistry) {
        this(sessionManager,
             service,
             registryInstances::get,
             registryInstances::destroy,
             metadataRegistry);
    }

    WorkItemDefinitionClientRegistry(final SessionManager sessionManager,
                                     final Caller<WorkItemDefinitionService> service,
                                     final Supplier<WorkItemDefinitionCacheRegistry> registryInstanceSupplier,
                                     final Consumer<WorkItemDefinitionCacheRegistry> registryInstanceDestroyer,
                                     final WorkItemDefinitionMetadataRegistry metadataRegistry) {
        this.sessionManager = sessionManager;
        this.service = service;
        this.registryInstanceSupplier = registryInstanceSupplier;
        this.registryInstanceDestroyer = registryInstanceDestroyer;
        this.metadataRegistry = metadataRegistry;
        this.sessionRegistries = new LinkedHashMap<>();
    }

    @PostConstruct
    public void init() {
        metadataRegistry
                .setRegistrySupplier(this::getCurrentSessionRegistry)
                .setWorkItemsByPathSupplier((path, collectionConsumer) ->
                                                    service.call((RemoteCallback<Collection<WorkItemDefinition>>) collectionConsumer::accept).search(path));
    }

    public void load(final ClientSession session,
                     final Metadata metadata,
                     final Command callback) {
        metadataRegistry
                .setRegistrySupplier(() -> getRegistry(session))
                .load(metadata,
                      () -> {
                          metadataRegistry.setRegistrySupplier(this::getCurrentSessionRegistry);
                          callback.execute();
                      });
    }

    @Produces
    @Default
    public WorkItemDefinitionRegistry getRegistry() {
        return getCurrentSessionRegistry();
    }

    @Override
    public Collection<WorkItemDefinition> items() {
        return metadataRegistry.items();
    }

    @Override
    public WorkItemDefinition get(final String name) {
        return metadataRegistry.get(name);
    }

    @PreDestroy
    public void destroy() {
        sessionRegistries.keySet().stream()
                .forEach(this::removeRegistry);
    }

    void onSessionDestroyed(final @Observes SessionDestroyedEvent sessionDestroyedEvent) {
        removeRegistry(sessionDestroyedEvent.getSessionUUID());
    }

    private WorkItemDefinitionCacheRegistry getCurrentSessionRegistry() {
        return getRegistry(sessionManager.getCurrentSession());
    }

    private WorkItemDefinitionCacheRegistry getRegistry(final ClientSession session) {
        return obtainRegistry(session.getSessionUUID());
    }

    private WorkItemDefinitionCacheRegistry obtainRegistry(final String sessionUUID) {
        WorkItemDefinitionCacheRegistry registry = sessionRegistries.get(sessionUUID);
        if (null == registry) {
            registry = registryInstanceSupplier.get();
            sessionRegistries.put(sessionUUID,
                                  registry);
        }
        return registry;
    }

    private void removeRegistry(final String sessionUUID) {
        final WorkItemDefinitionCacheRegistry registry = sessionRegistries.remove(sessionUUID);
        if (null != registry) {
            registry.clear();
            registryInstanceDestroyer.accept(registry);
        }
    }
}