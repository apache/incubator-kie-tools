/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.common.services.builder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.ModuleService;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.commons.concurrent.Managed;

import static javax.ejb.TransactionAttributeType.NOT_SUPPORTED;

@Singleton
@Startup
@TransactionAttribute(NOT_SUPPORTED)
public class IncrementalBuilderExecutorManager {

    @Inject
    private ModuleService<? extends Module> moduleService;

    private BuildService buildService;

    private Event<BuildResults> buildResultsEvent;

    private Event<IncrementalBuildResults> incrementalBuildResultsEvent;

    private ExecutorService executorService;

    public IncrementalBuilderExecutorManager() {
    }

    @Inject
    public IncrementalBuilderExecutorManager(ModuleService<? extends Module> moduleService,
                                             BuildService buildService,
                                             Event<BuildResults> buildResultsEvent,
                                             Event<IncrementalBuildResults> incrementalBuildResultsEvent,
                                             @Managed ExecutorService executorService) {
        this.moduleService = moduleService;
        this.buildService = buildService;
        this.buildResultsEvent = buildResultsEvent;
        this.incrementalBuildResultsEvent = incrementalBuildResultsEvent;
        this.executorService = executorService;
    }

    private AtomicBoolean useExecService = new AtomicBoolean(false);

    @Asynchronous
    public void execute(final AsyncIncrementalBuilder incrementalBuilder) {
        if (useExecService.get()) {
            getExecutorService().execute(new DescriptiveRunnable() {
                @Override
                public void run() {
                    incrementalBuilder.execute(moduleService,
                                               buildService,
                                               incrementalBuildResultsEvent,
                                               buildResultsEvent);
                }

                @Override
                public String getDescription() {
                    return incrementalBuilder.getDescription();
                }
            });
        } else {
            incrementalBuilder.execute(moduleService,
                                       buildService,
                                       incrementalBuildResultsEvent,
                                       buildResultsEvent);
        }
    }

    //Public so we can set the ExecutorService for tests not within guvnor
    public void setExecutorService(final ExecutorService executorService) {
        this.executorService = executorService;
        this.useExecService.set(true);
    }

    private ExecutorService getExecutorService() {
        return executorService;
    }

    public void setServices(final ModuleService moduleService,
                            final BuildService buildService,
                            final Event<BuildResults> buildResultsEvent,
                            final Event<IncrementalBuildResults> incrementalBuildResultsEvent) {
        this.moduleService = moduleService;
        this.buildService = buildService;
        this.buildResultsEvent = buildResultsEvent;
        this.incrementalBuildResultsEvent = incrementalBuildResultsEvent;
        this.useExecService.set(true);
    }

    public void shutdown() {
        if (useExecService.get() && executorService != null) {
            executorService.shutdown(); // Disable new tasks from being submitted
            try {
                // Wait a while for existing tasks to terminate
                if (!executorService.awaitTermination(60,
                                                      TimeUnit.SECONDS)) {
                    executorService.shutdownNow(); // Cancel currently executing tasks
                    // Wait a while for tasks to respond to being cancelled
                    if (!executorService.awaitTermination(60,
                                                          TimeUnit.SECONDS)) {
                        System.err.println("Pool did not terminate");
                    }
                }
            } catch (InterruptedException ie) {
                // (Re-)Cancel if current thread also interrupted
                executorService.shutdownNow();
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            }
        }
    }
}
