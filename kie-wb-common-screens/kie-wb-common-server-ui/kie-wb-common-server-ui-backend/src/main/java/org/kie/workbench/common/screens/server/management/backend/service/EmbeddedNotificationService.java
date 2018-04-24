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

package org.kie.workbench.common.screens.server.management.backend.service;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.server.controller.api.model.events.ServerInstanceConnected;
import org.kie.server.controller.api.model.events.ServerInstanceDeleted;
import org.kie.server.controller.api.model.events.ServerInstanceDisconnected;
import org.kie.server.controller.api.model.events.ServerInstanceUpdated;
import org.kie.server.controller.api.model.events.ServerTemplateDeleted;
import org.kie.server.controller.api.model.events.ServerTemplateUpdated;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.websocket.notification.WebSocketNotificationService;
import org.kie.workbench.common.screens.server.management.backend.utils.EmbeddedController;
import org.kie.workbench.common.screens.server.management.model.ContainerSpecData;

@ApplicationScoped
@EmbeddedController
public class EmbeddedNotificationService extends WebSocketNotificationService {

    @Inject
    private Event<ServerTemplateUpdated> serverTemplateUpdatedEvent;

    @Inject
    private Event<ServerTemplateDeleted> serverTemplateDeletedEvent;

    @Inject
    private Event<ServerInstanceUpdated> serverInstanceUpdatedEvent;

    @Inject
    private Event<ServerInstanceDeleted> serverInstanceDeletedEvent;

    @Inject
    private Event<ServerInstanceConnected> serverInstanceConnectedEvent;

    @Inject
    private Event<ServerInstanceDisconnected> serverInstanceDisconnectedEvent;

    @Inject
    private Event<ContainerSpecData> containerSpecDataEvent;

    @Override
    public void notify(final ServerTemplate serverTemplate,
                       final ContainerSpec containerSpec,
                       final List<Container> containers) {
        super.notify(serverTemplate,
                     containerSpec,
                     containers);
        ContainerSpecData containerSpecData = new ContainerSpecData(containerSpec,
                                                                    containers);

        containerSpecDataEvent.fire(containerSpecData);
    }

    @Override
    public void notify(final ServerTemplateUpdated serverTemplateUpdated) {
        super.notify(serverTemplateUpdated);
        serverTemplateUpdatedEvent.fire(serverTemplateUpdated);
    }

    @Override
    public void notify(final ServerTemplateDeleted serverTemplateDeleted) {
        super.notify(serverTemplateDeleted);
        serverTemplateDeletedEvent.fire(serverTemplateDeleted);
    }

    @Override
    public void notify(ServerInstanceUpdated serverInstanceUpdated) {
        super.notify(serverInstanceUpdated);
        serverInstanceUpdatedEvent.fire(serverInstanceUpdated);
    }

    @Override
    public void notify(ServerInstanceDeleted serverInstanceDeleted) {
        super.notify(serverInstanceDeleted);
        serverInstanceDeletedEvent.fire(serverInstanceDeleted);
    }

    @Override
    public void notify(ServerInstanceConnected serverInstanceConnected) {
        super.notify(serverInstanceConnected);
        serverInstanceConnectedEvent.fire(serverInstanceConnected);
    }

    @Override
    public void notify(ServerInstanceDisconnected serverInstanceDisconnected) {
        super.notify(serverInstanceDisconnected);
        serverInstanceDisconnectedEvent.fire(serverInstanceDisconnected);
    }
}
