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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.services.cdi.ApplicationStarted;
import org.uberfire.workbench.events.ResourceChange;

/**
 * Listener for changes to project resources to handle incremental builds
 */
@ApplicationScoped
public class ResourceChangeIncrementalBuilder {

    private static final String INCREMENTAL_BUILD_PROPERTY_NAME = "build.enable-incremental";

    protected static final Logger logger = LoggerFactory.getLogger(ResourceChangeIncrementalBuilder.class);

    @Inject
    protected ProjectService<? extends Project> projectService;

    @Inject
    private AppConfigService appConfigService;

    @Inject
    private IncrementalBuilderExecutorManagerFactory executorManagerProducer;

    private IncrementalBuilderExecutorManager executorManager = null;

    protected boolean isIncrementalEnabled = false;

    @PostConstruct
    private void setup() {
        isIncrementalEnabled = isIncrementalBuildEnabled();
    }

    public void configureOnEvent(@Observes ApplicationStarted applicationStartedEvent) {
        isIncrementalEnabled = isIncrementalBuildEnabled();
        getExecutor();
    }

    private boolean isIncrementalBuildEnabled() {
        final String value = appConfigService.loadPreferences().get(INCREMENTAL_BUILD_PROPERTY_NAME);
        return Boolean.parseBoolean(value);
    }

    @PreDestroy
    private void destroyExecutorService() {
        if (executorManager != null && !isEjb(executorManager,
                                              IncrementalBuilderExecutorManager.class)) {
            executorManager.shutdown();
        }
    }

    private boolean isEjb(Object o,
                          Class<?> expected) {
        if (o.getClass() != expected) {
            return true;
        }

        return false;
    }

    private synchronized IncrementalBuilderExecutorManager getExecutor() {
        if (executorManager == null) {
            executorManager = executorManagerProducer.getExecutorManager();
        }
        return executorManager;
    }

    public void addResource(final Path resource) {
        //Do nothing if incremental builds are disabled
        if (!isIncrementalEnabled) {
            return;
        }

        logger.info("Incremental build request received for: " + resource.toURI() + " (added).");

        //If resource is not within a Package it cannot be used for an incremental build
        final Package pkg = projectService.resolvePackage(resource);
        if (pkg == null) {
            return;
        }

        //Schedule an incremental build
        getExecutor().execute(new AsyncIncrementalBuilder() {

            @Override
            public void execute(final ProjectService projectService,
                                final BuildService buildService,
                                final Event<IncrementalBuildResults> incrementalBuildResultsEvent,
                                final Event<BuildResults> buildResultsEvent) {
                try {
                    logger.info("Incremental build request being processed: " + resource.toURI() + " (added).");
                    final Project project = projectService.resolveProject(resource);

                    //Fall back to a Full Build in lieu of an Incremental Build if the Project has not been previously built
                    if (buildService.isBuilt(project)) {
                        final IncrementalBuildResults results = buildService.addPackageResource(resource);
                        incrementalBuildResultsEvent.fire(results);
                    } else {
                        final BuildResults results = buildService.build(project);
                        buildResultsEvent.fire(results);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(),
                                 e);
                }
            }

            @Override
            public String getDescription() {
                return "Incremental Build [" + resource.toURI() + " (added)]";
            }
        });
    }

    public void deleteResource(final Path resource) {
        //Do nothing if incremental builds are disabled
        if (!isIncrementalEnabled) {
            return;
        }

        logger.info("Incremental build request received for: " + resource.toURI() + " (deleted).");

        //If resource is not within a Package it cannot be used for an incremental build
        final Package pkg = projectService.resolvePackage(resource);
        if (pkg == null) {
            return;
        }

        //Schedule an incremental build
        getExecutor().execute(new AsyncIncrementalBuilder() {

            @Override
            public void execute(final ProjectService projectService,
                                final BuildService buildService,
                                final Event<IncrementalBuildResults> incrementalBuildResultsEvent,
                                final Event<BuildResults> buildResultsEvent) {
                try {
                    logger.info("Incremental build request being processed: " + resource.toURI() + " (deleted).");
                    final Project project = projectService.resolveProject(resource);

                    //Fall back to a Full Build in lieu of an Incremental Build if the Project has not been previously built
                    if (buildService.isBuilt(project)) {
                        final IncrementalBuildResults results = buildService.deletePackageResource(resource);
                        incrementalBuildResultsEvent.fire(results);
                    } else {
                        final BuildResults results = buildService.build(project);
                        buildResultsEvent.fire(results);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(),
                                 e);
                }
            }

            @Override
            public String getDescription() {
                return "Incremental Build [" + resource.toURI() + " (deleted)]";
            }
        });
    }

    public void updateResource(final Path resource) {
        //Do nothing if incremental builds are disabled
        if (!isIncrementalEnabled) {
            return;
        }

        logger.info("Incremental build request received for: " + resource.toURI() + " (updated).");

        //The pom.xml cannot be processed incrementally
        if (isProjectResourceUpdateNeeded(resource)) {
            scheduleProjectResourceUpdate(resource);
        } else {
            //If resource is not within a Package it cannot be used for an incremental build
            final Package pkg = projectService.resolvePackage(resource);
            if (pkg == null) {
                return;
            }
            schedulePackageResourceUpdate(resource);
        }
    }

    protected boolean isProjectResourceUpdateNeeded(Path resource) {
        return projectService.isPom(resource);
    }

    //Schedule a re-build of a Project (changes to pom.xml or kmodule.xml require a full build)
    protected void scheduleProjectResourceUpdate(final Path resource) {
        final Project project = projectService.resolveProject(resource);
        getExecutor().execute(new AsyncIncrementalBuilder() {

            @Override
            public void execute(final ProjectService projectService,
                                final BuildService buildService,
                                final Event<IncrementalBuildResults> incrementalBuildResultsEvent,
                                final Event<BuildResults> buildResultsEvent) {
                try {
                    logger.info("Incremental build request being processed: " + project.getRootPath() + " (updated).");
                    final BuildResults results = buildService.build(project);
                    buildResultsEvent.fire(results);
                } catch (Exception e) {
                    logger.error(e.getMessage(),
                                 e);
                }
            }

            @Override
            public String getDescription() {
                return "Incremental Build [" + resource.toURI() + " (updated)]";
            }
        });
    }

    //Schedule an incremental build for a package resource
    protected void schedulePackageResourceUpdate(final Path resource) {
        getExecutor().execute(new AsyncIncrementalBuilder() {

            @Override
            public void execute(final ProjectService projectService,
                                final BuildService buildService,
                                final Event<IncrementalBuildResults> incrementalBuildResultsEvent,
                                final Event<BuildResults> buildResultsEvent) {
                try {
                    logger.info("Incremental build request being processed: " + resource.toURI() + " (updated).");
                    final Project project = projectService.resolveProject(resource);

                    //Fall back to a Full Build in lieu of an Incremental Build if the Project has not been previously built
                    if (buildService.isBuilt(project)) {
                        final IncrementalBuildResults results = buildService.updatePackageResource(resource);
                        incrementalBuildResultsEvent.fire(results);
                    } else {
                        final BuildResults results = buildService.build(project);
                        buildResultsEvent.fire(results);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(),
                                 e);
                }
            }

            @Override
            public String getDescription() {
                return "Incremental Build [" + resource.toURI() + " (updated)]";
            }
        });
    }

    public void batchResourceChanges(final Map<Path, Collection<ResourceChange>> batch) {
        //Do nothing if incremental builds are disabled
        if (!isIncrementalEnabled) {
            return;
        }

        logger.info("Batch incremental build request received.");

        //Block changes together with their respective project as Builder operates at the Project level
        final Map<Project, Map<Path, Collection<ResourceChange>>> projectBatchChanges = new HashMap<Project, Map<Path, Collection<ResourceChange>>>();

        for (Map.Entry<Path, Collection<ResourceChange>> pathCollectionEntry : batch.entrySet()) {
            for (final ResourceChange change : pathCollectionEntry.getValue()) {
                final Path resource = pathCollectionEntry.getKey();

                //If resource is not within a Package it cannot be used for an incremental build
                final Project project = projectService.resolveProject(resource);
                final Package pkg = projectService.resolvePackage(resource);
                if (project != null && pkg != null) {
                    if (!projectBatchChanges.containsKey(project)) {
                        projectBatchChanges.put(project,
                                                new HashMap<Path, Collection<ResourceChange>>());
                    }
                    final Map<Path, Collection<ResourceChange>> projectChanges = projectBatchChanges.get(project);
                    if (!projectChanges.containsKey(pathCollectionEntry.getKey())) {
                        projectChanges.put(pathCollectionEntry.getKey(),
                                           new ArrayList<ResourceChange>());
                    }
                    projectChanges.get(pathCollectionEntry.getKey()).add(change);
                    logger.info("- Batch content: " + pathCollectionEntry.getKey().toURI() + " (" + change.getType().toString() + ").");
                }
            }
        }

        //Schedule an incremental build for each Project
        for (final Map.Entry<Project, Map<Path, Collection<ResourceChange>>> e : projectBatchChanges.entrySet()) {
            getExecutor().execute(new AsyncIncrementalBuilder() {

                @Override
                public void execute(final ProjectService projectService,
                                    final BuildService buildService,
                                    final Event<IncrementalBuildResults> incrementalBuildResultsEvent,
                                    final Event<BuildResults> buildResultsEvent) {
                    try {
                        logger.info("Batch incremental build request being processed.");
                        final Project project = e.getKey();
                        final Map<Path, Collection<ResourceChange>> changes = e.getValue();

                        //Fall back to a Full Build in lieu of an Incremental Build if the Project has not been previously built
                        if (buildService.isBuilt(project)) {
                            final IncrementalBuildResults results = buildService.applyBatchResourceChanges(project,
                                                                                                           changes);
                            incrementalBuildResultsEvent.fire(results);
                        } else {
                            final BuildResults results = buildService.build(project);
                            buildResultsEvent.fire(results);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(),
                                     e);
                    }
                }

                @Override
                public String getDescription() {
                    return "Batch incremental build [" + e.getKey().getProjectName() + "]";
                }
            });
        }
    }
}
