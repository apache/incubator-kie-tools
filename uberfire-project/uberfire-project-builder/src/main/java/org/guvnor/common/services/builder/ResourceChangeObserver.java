/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.common.services.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.events.InvalidateDMOModuleCacheEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.ModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceChangeType;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

/**
 * Server side component that observes for the different resource add/delete/update events related to
 * a given project and that causes the ModuleDataModelOracle to be invalidated. Typically .java, .class and pom.xml
 * files. When such a resource is modified an InvalidateDMOModuleCacheEvent event is fired.
 */
@ApplicationScoped
public class ResourceChangeObserver {

    private static final Logger logger = LoggerFactory.getLogger(ResourceChangeObserver.class);

    @Inject
    private ModuleService<? extends Module> projectService;

    @Inject
    private ResourceChangeIncrementalBuilder incrementalBuilder;

    @Inject
    private Event<InvalidateDMOModuleCacheEvent> invalidateDMOProjectCacheEvent;

    @Inject
    @Any
    private Instance<ResourceChangeObservableFile> observableFiles;

    @Inject
    private ObservablePOMFile observablePomFile;

    public void processResourceAdd(@Observes final ResourceAddedEvent resourceAddedEvent) {
        processResourceChange(resourceAddedEvent.getSessionInfo(),
                              resourceAddedEvent.getPath(),
                              ResourceChangeType.ADD);
        incrementalBuilder.addResource(resourceAddedEvent.getPath());
    }

    public void processResourceDelete(@Observes final ResourceDeletedEvent resourceDeletedEvent) {
        processResourceChange(resourceDeletedEvent.getSessionInfo(),
                              resourceDeletedEvent.getPath(),
                              ResourceChangeType.DELETE);
        incrementalBuilder.deleteResource(resourceDeletedEvent.getPath());
    }

    public void processResourceUpdate(@Observes final ResourceUpdatedEvent resourceUpdatedEvent) {
        processResourceChange(resourceUpdatedEvent.getSessionInfo(),
                              resourceUpdatedEvent.getPath(),
                              ResourceChangeType.UPDATE);
        incrementalBuilder.updateResource(resourceUpdatedEvent.getPath());
    }

    public void processResourceCopied(@Observes final ResourceCopiedEvent resourceCopiedEvent) {
        processResourceChange(resourceCopiedEvent.getSessionInfo(),
                              resourceCopiedEvent.getPath(),
                              ResourceChangeType.COPY);
        incrementalBuilder.addResource(resourceCopiedEvent.getPath()); //¿?
    }

    public void processResourceRenamed(@Observes final ResourceRenamedEvent resourceRenamedEvent) {
        processResourceChange(resourceRenamedEvent.getSessionInfo(),
                              resourceRenamedEvent.getPath(),
                              ResourceChangeType.RENAME);
        incrementalBuilder.deleteResource(resourceRenamedEvent.getPath());
        incrementalBuilder.addResource(resourceRenamedEvent.getDestinationPath());
    }

    public void processBatchChanges(@Observes final ResourceBatchChangesEvent resourceBatchChangesEvent) {
        final Map<Path, Collection<ResourceChange>> batchChanges = resourceBatchChangesEvent.getBatch();
        if (batchChanges == null) {
            //un expected case
            logger.warn("No batchChanges was present for the given resourceBatchChangesEvent: " + resourceBatchChangesEvent);
        } else {
            processBatchResourceChanges(resourceBatchChangesEvent.getSessionInfo(),
                                        batchChanges);
            incrementalBuilder.batchResourceChanges(resourceBatchChangesEvent.getBatch());
        }
    }

    private void processResourceChange(final SessionInfo sessionInfo,
                                       final Path path,
                                       final ResourceChangeType changeType) {
        //Only process Project resources
        final Module module = projectService.resolveModule(path);
        if (module == null) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Processing resource change for sessionInfo: " + sessionInfo
                                 + ", project: " + module
                                 + ", path: " + path
                                 + ", changeType: " + changeType);
        }

        if (isObservableResource(path)) {
            invalidateDMOProjectCacheEvent.fire(new InvalidateDMOModuleCacheEvent(sessionInfo,
                                                                                  module,
                                                                                  path));
        }
    }

    private void processBatchResourceChanges(final SessionInfo sessionInfo,
                                             final Map<Path, Collection<ResourceChange>> resourceChanges) {

        Module module;
        final Map<Module, Path> pendingNotifications = new HashMap<Module, Path>();
        for (final Map.Entry<Path, Collection<ResourceChange>> pathCollectionEntry : resourceChanges.entrySet()) {

            //Only process Project resources
            module = projectService.resolveModule(pathCollectionEntry.getKey());
            if (module == null) {
                continue;
            }

            if (!pendingNotifications.containsKey(module) && isObservableResource(pathCollectionEntry.getKey())) {
                pendingNotifications.put(module,
                                         pathCollectionEntry.getKey());
            } else if (isPomFile(pathCollectionEntry.getKey())) {
                //if the pom.xml comes in the batch events set then use the pom.xml path for the cache invalidation event
                pendingNotifications.put(module,
                                         pathCollectionEntry.getKey());
            }
        }

        for (final Map.Entry<Module, Path> pendingNotification : pendingNotifications.entrySet()) {
            invalidateDMOProjectCacheEvent.fire(new InvalidateDMOModuleCacheEvent(sessionInfo,
                                                                                  pendingNotification.getKey(),
                                                                                  pendingNotification.getValue()));
        }
    }

    //Check if the changed file should invalidate the DMO cache
    private boolean isObservableResource(final Path path) {
        if (path == null) {
            return false;
        }
        for (ResourceChangeObservableFile observableFile : observableFiles) {
            if (observableFile.accept(path)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPomFile(final Path path) {
        if (path == null) {
            return false;
        }
        return observablePomFile.accept(path);
    }
}
