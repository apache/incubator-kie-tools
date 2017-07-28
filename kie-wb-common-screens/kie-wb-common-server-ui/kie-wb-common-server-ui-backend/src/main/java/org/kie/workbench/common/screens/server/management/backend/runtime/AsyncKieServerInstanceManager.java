/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.backend.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.Message;
import org.kie.server.api.model.Severity;
import org.kie.server.controller.api.model.events.ServerInstanceUpdated;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstance;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.service.NotificationService;
import org.kie.server.controller.impl.KieServerInstanceManager;
import org.kie.workbench.common.screens.server.management.model.ContainerRuntimeOperation;
import org.kie.workbench.common.screens.server.management.model.ContainerRuntimeState;
import org.kie.workbench.common.screens.server.management.model.ContainerUpdateEvent;
import org.uberfire.commons.concurrent.Managed;

import static org.kie.workbench.common.screens.server.management.model.ContainerRuntimeOperation.SCAN;
import static org.kie.workbench.common.screens.server.management.model.ContainerRuntimeOperation.START_CONTAINER;
import static org.kie.workbench.common.screens.server.management.model.ContainerRuntimeOperation.START_SCANNER;
import static org.kie.workbench.common.screens.server.management.model.ContainerRuntimeOperation.STOP_CONTAINER;
import static org.kie.workbench.common.screens.server.management.model.ContainerRuntimeOperation.STOP_SCANNER;
import static org.kie.workbench.common.screens.server.management.model.ContainerRuntimeOperation.UPGRADE_CONTAINER;

@ApplicationScoped
public class AsyncKieServerInstanceManager extends KieServerInstanceManager {

    private ExecutorService executor;
    private NotificationService notificationService;
    private Event<ContainerUpdateEvent> containerUpdateEvent;

    protected void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public AsyncKieServerInstanceManager() {
    }

    @Inject
    public AsyncKieServerInstanceManager(NotificationService notificationService,
                                         Event<ContainerUpdateEvent> containerUpdateEvent,
                                         @Managed ExecutorService executorService) {
        this.notificationService = notificationService;
        this.containerUpdateEvent = containerUpdateEvent;
        this.executor = executorService;
    }

    @Override
    public List<Container> startScanner(final ServerTemplate serverTemplate,
                                        final ContainerSpec containerSpec,
                                        final long interval) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Container> containers = AsyncKieServerInstanceManager.super.startScanner(serverTemplate,
                                                                                              containerSpec,
                                                                                              interval);

                notificationService.notify(serverTemplate,
                                           containerSpec,
                                           containers);

                produceContainerUpdateEvent(serverTemplate,
                                            containerSpec,
                                            containers,
                                            START_SCANNER);
            }
        });
        return Collections.emptyList();
    }

    @Override
    public List<Container> stopScanner(final ServerTemplate serverTemplate,
                                       final ContainerSpec containerSpec) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Container> containers = AsyncKieServerInstanceManager.super.stopScanner(serverTemplate,
                                                                                             containerSpec);
                notificationService.notify(serverTemplate,
                                           containerSpec,
                                           containers);

                produceContainerUpdateEvent(serverTemplate,
                                            containerSpec,
                                            containers,
                                            STOP_SCANNER);
            }
        });
        return Collections.emptyList();
    }

    @Override
    public List<Container> scanNow(final ServerTemplate serverTemplate,
                                   final ContainerSpec containerSpec) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Container> containers = AsyncKieServerInstanceManager.super.scanNow(serverTemplate,
                                                                                         containerSpec);
                notificationService.notify(serverTemplate,
                                           containerSpec,
                                           containers);

                produceContainerUpdateEvent(serverTemplate,
                                            containerSpec,
                                            containers,
                                            SCAN);
            }
        });
        return Collections.emptyList();
    }

    @Override
    public synchronized List<Container> startContainer(final ServerTemplate serverTemplate,
                                                       final ContainerSpec containerSpec) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Container> containers = AsyncKieServerInstanceManager.super.startContainer(serverTemplate,
                                                                                                containerSpec);
                notificationService.notify(serverTemplate,
                                           containerSpec,
                                           containers);

                produceContainerUpdateEvent(serverTemplate,
                                            containerSpec,
                                            containers,
                                            START_CONTAINER);
            }
        });
        return Collections.emptyList();
    }

    @Override
    public synchronized List<Container> stopContainer(final ServerTemplate serverTemplate,
                                                      final ContainerSpec containerSpec) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Container> containers = AsyncKieServerInstanceManager.super.stopContainer(serverTemplate,
                                                                                               containerSpec);
                notificationService.notify(serverTemplate,
                                           containerSpec,
                                           containers);

                produceContainerUpdateEvent(serverTemplate,
                                            containerSpec,
                                            containers,
                                            STOP_CONTAINER);
            }
        });
        return Collections.emptyList();
    }

    @Override
    public List<Container> upgradeContainer(final ServerTemplate serverTemplate,
                                            final ContainerSpec containerSpec) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Container> containers = AsyncKieServerInstanceManager.super.upgradeContainer(serverTemplate,
                                                                                                  containerSpec);
                notificationService.notify(serverTemplate,
                                           containerSpec,
                                           containers);

                produceContainerUpdateEvent(serverTemplate,
                                            containerSpec,
                                            containers,
                                            UPGRADE_CONTAINER);
            }
        });
        return Collections.emptyList();
    }

    @Override
    public List<Container> getContainers(final ServerInstanceKey serverInstanceKey) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Container> containers = AsyncKieServerInstanceManager.super.getContainers(serverInstanceKey);

                ServerInstance serverInstance = new ServerInstance();
                serverInstance.setServerName(serverInstanceKey.getServerName());
                serverInstance.setServerTemplateId(serverInstanceKey.getServerTemplateId());
                serverInstance.setServerInstanceId(serverInstanceKey.getServerInstanceId());
                serverInstance.setUrl(serverInstanceKey.getUrl());

                serverInstance.setContainers(containers);

                notificationService.notify(new ServerInstanceUpdated(serverInstance));
            }
        });
        return Collections.emptyList();
    }

    @Override
    public List<Container> getContainers(final ServerTemplate serverTemplate,
                                         final ContainerSpec containerSpec) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Container> containers = AsyncKieServerInstanceManager.super.getContainers(serverTemplate,
                                                                                               containerSpec);

                notificationService.notify(serverTemplate,
                                           containerSpec,
                                           containers);
            }
        });
        return Collections.emptyList();
    }

    protected void produceContainerUpdateEvent(ServerTemplate serverTemplate,
                                               ContainerSpec containerSpec,
                                               List<Container> containers,
                                               ContainerRuntimeOperation containerRuntimeOperation) {
        List<ServerInstanceKey> failedServerInstances = new ArrayList<ServerInstanceKey>();
        for (Container container : containers) {
            if (hasIssues(container)) {
                failedServerInstances.add(container.getServerInstanceKey());
            }
        }

        ContainerRuntimeState containerRuntimeState = ContainerRuntimeState.ONLINE;
        if (failedServerInstances.size() == containers.size()) {
            containerRuntimeState = ContainerRuntimeState.OFFLINE;
        } else if (!failedServerInstances.isEmpty()) {
            containerRuntimeState = ContainerRuntimeState.PARTIAL_ONLINE;
        }

        ContainerUpdateEvent updateEvent = new ContainerUpdateEvent(
                serverTemplate,
                containerSpec,
                failedServerInstances,
                containerRuntimeState,
                containerRuntimeOperation
        );

        containerUpdateEvent.fire(updateEvent);
    }

    protected boolean hasIssues(Container container) {
        if (container.getStatus().equals(KieContainerStatus.FAILED)) {
            return true;
        } else if (container.getMessages() != null) {

            for (Message message : container.getMessages()) {
                if (message.getSeverity().equals(Severity.ERROR) || message.getSeverity().equals(Severity.WARN)) {
                    return true;
                }
            }
        }

        return false;
    }
}
