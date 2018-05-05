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
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionCacheRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistries;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.service.WorkItemDefinitionLookupService;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.Command;

/**
 * - It manages the registries relying on the client session lifecycle
 * - It produces the @Default WorkItemDefinitionRegistry based on current session
 * - It performs calls to server side to populate the current registry
 * - It destroy the registry, if any, when a session is being destroyed
 */
@ApplicationScoped
@Typed(WorkItemDefinitionClientRegistry.class)
public class WorkItemDefinitionClientRegistry implements WorkItemDefinitionRegistry {

    private final Caller<WorkItemDefinitionLookupService> service;
    private final WorkItemDefinitionRegistries<Metadata> index;
    private final SessionManager sessionManager;
    private final Supplier<WorkItemDefinitionCacheRegistry> registryInstanceSupplier;
    private final Consumer<Throwable> errorPresenter;

    @Inject
    public WorkItemDefinitionClientRegistry(final Caller<WorkItemDefinitionLookupService> service,
                                            final SessionManager sessionManager,
                                            final ManagedInstance<WorkItemDefinitionCacheRegistry> registryInstances,
                                            final ErrorPopupPresenter errorPopupPresenter) {
        this(service,
             sessionManager,
             registryInstances::get,
             exception -> errorPopupPresenter.showMessage(getExceptionMessage(exception)),
             new WorkItemDefinitionRegistries<>(metadata -> metadata.getRoot().toURI(),
                                                new HashMap<>(),
                                                registryInstances::destroy));
    }

    WorkItemDefinitionClientRegistry(final Caller<WorkItemDefinitionLookupService> service,
                                     final SessionManager sessionManager,
                                     final Supplier<WorkItemDefinitionCacheRegistry> registryInstances,
                                     final Consumer<Throwable> errorPresenter,
                                     final WorkItemDefinitionRegistries<Metadata> index) {
        this.service = service;
        this.sessionManager = sessionManager;
        this.registryInstanceSupplier = registryInstances;
        this.errorPresenter = errorPresenter;
        this.index = index;
    }

    @Override
    public Collection<WorkItemDefinition> items() {
        return getCurrentSessionRegistry().items();
    }

    @Override
    public WorkItemDefinition get(final String name) {
        return getCurrentSessionRegistry().get(name);
    }

    @Produces
    @Default
    public WorkItemDefinitionRegistry getRegistry() {
        return getCurrentSessionRegistry();
    }

    public void load(final Metadata metadata,
                     final Command callback) {
        service.call((RemoteCallback<Collection<WorkItemDefinition>>) workItemDefinitions -> {
            final WorkItemDefinitionCacheRegistry registry = getRegistryForModule(metadata);
            workItemDefinitions.forEach(registry::register);
            callback.execute();
        }, (message, throwable) -> {
            errorPresenter.accept(throwable);
            callback.execute();
            return false;
        }).execute(metadata);
    }

    @PreDestroy
    public void destroy() {
        index.clear();
    }

    void onSessionDestroyed(final @Observes SessionDestroyedEvent sessionDestroyedEvent) {
        removeRegistry(sessionDestroyedEvent.getMetadata());
    }

    WorkItemDefinitionCacheRegistry getCurrentSessionRegistry() {
        return getRegistryForSession(sessionManager.getCurrentSession());
    }

    WorkItemDefinitionCacheRegistry getRegistryForSession(final ClientSession session) {
        return getRegistryForModule(session.getCanvasHandler().getDiagram().getMetadata());
    }

    WorkItemDefinitionCacheRegistry getRegistryForModule(final Metadata metadata) {
        return obtainRegistry(metadata);
    }

    private WorkItemDefinitionCacheRegistry obtainRegistry(final Metadata metadata) {
        if (!index.contains(metadata)) {
            index.put(metadata,
                      registryInstanceSupplier.get());
        }
        return index.registries().apply(metadata);
    }

    private void removeRegistry(final Metadata metadata) {
        index.remove(metadata);
    }

    @SuppressWarnings("all")
    private static String getExceptionMessage(final Throwable throwable) {
        Throwable root = throwable;
        while (null != root) {
            if (null != root.getCause()) {
                root = root.getCause();
            } else {
                break;
            }
        }
        return root.getMessage();
    }
}
