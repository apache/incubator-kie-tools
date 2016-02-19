/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.backend.runtime;

import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.server.controller.api.model.events.ServerInstanceUpdated;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstance;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.service.NotificationService;
import org.kie.server.controller.impl.KieServerInstanceManager;
import org.uberfire.commons.async.DisposableExecutor;
import org.uberfire.commons.async.SimpleAsyncExecutorService;

@ApplicationScoped
public class AsyncKieServerInstanceManager extends KieServerInstanceManager {

    private DisposableExecutor executor;

    @Inject
    private NotificationService notificationService;

    @PostConstruct
    public void configure() {
        executor = SimpleAsyncExecutorService.getDefaultInstance();
    }

    @Override
    public List<Container> startScanner( final ServerTemplate serverTemplate,
                                         final ContainerSpec containerSpec,
                                         final long interval ) {
        executor.execute( new Runnable() {
            @Override
            public void run() {
                List<Container> containers = AsyncKieServerInstanceManager.super.startScanner( serverTemplate, containerSpec, interval );

                notificationService.notify( serverTemplate, containerSpec, containers );
            }
        } );
        return Collections.emptyList();
    }

    @Override
    public List<Container> stopScanner( final ServerTemplate serverTemplate,
                                        final ContainerSpec containerSpec ) {
        executor.execute( new Runnable() {
            @Override
            public void run() {
                List<Container> containers = AsyncKieServerInstanceManager.super.stopScanner( serverTemplate, containerSpec );
                notificationService.notify( serverTemplate, containerSpec, containers );
            }
        } );
        return Collections.emptyList();
    }

    @Override
    public List<Container> scanNow( final ServerTemplate serverTemplate,
                                    final ContainerSpec containerSpec ) {
        executor.execute( new Runnable() {
            @Override
            public void run() {
                List<Container> containers = AsyncKieServerInstanceManager.super.scanNow( serverTemplate, containerSpec );
                notificationService.notify( serverTemplate, containerSpec, containers );
            }
        } );
        return Collections.emptyList();
    }

    @Override
    public List<Container> startContainer( final ServerTemplate serverTemplate,
                                           final ContainerSpec containerSpec ) {
        executor.execute( new Runnable() {
            @Override
            public void run() {
                List<Container> containers = AsyncKieServerInstanceManager.super.startContainer( serverTemplate, containerSpec );
                notificationService.notify( serverTemplate, containerSpec, containers );
            }
        } );
        return Collections.emptyList();
    }

    @Override
    public List<Container> stopContainer( final ServerTemplate serverTemplate,
                                          final ContainerSpec containerSpec ) {
        executor.execute( new Runnable() {
            @Override
            public void run() {
                List<Container> containers = AsyncKieServerInstanceManager.super.stopContainer( serverTemplate, containerSpec );
                notificationService.notify( serverTemplate, containerSpec, containers );
            }
        } );
        return Collections.emptyList();
    }

    @Override
    public List<Container> upgradeContainer( final ServerTemplate serverTemplate,
                                             final ContainerSpec containerSpec ) {
        executor.execute( new Runnable() {
            @Override
            public void run() {
                List<Container> containers = AsyncKieServerInstanceManager.super.upgradeContainer( serverTemplate, containerSpec );
                notificationService.notify( serverTemplate, containerSpec, containers );
            }
        } );
        return Collections.emptyList();
    }

    @Override
    public List<Container> getContainers( final ServerInstanceKey serverInstanceKey ) {
        executor.execute( new Runnable() {
            @Override
            public void run() {
                List<Container> containers = AsyncKieServerInstanceManager.super.getContainers( serverInstanceKey );

                ServerInstance serverInstance = new ServerInstance();
                serverInstance.setServerName(serverInstanceKey.getServerName());
                serverInstance.setServerTemplateId(serverInstanceKey.getServerTemplateId());
                serverInstance.setServerInstanceId(serverInstanceKey.getServerInstanceId());
                serverInstance.setUrl(serverInstanceKey.getUrl());

                serverInstance.setContainers(containers);

                notificationService.notify( new ServerInstanceUpdated(serverInstance));
            }
        } );
        return Collections.emptyList();
    }

    @Override
    public List<Container> getContainers(final ServerTemplate serverTemplate, final ContainerSpec containerSpec) {
        executor.execute( new Runnable() {
            @Override
            public void run() {
                List<Container> containers = AsyncKieServerInstanceManager.super.getContainers(serverTemplate, containerSpec);

                notificationService.notify(serverTemplate, containerSpec, containers);
            }
        } );
        return Collections.emptyList();
    }
}
