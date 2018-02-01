/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.backend.websocket;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.server.controller.api.model.events.*;
import org.kie.server.controller.client.event.EventHandler;
import org.kie.workbench.common.screens.server.management.backend.utils.StandaloneController;
import org.kie.workbench.common.screens.server.management.model.ContainerSpecData;

@ApplicationScoped
@StandaloneController
public class StandaloneNotificationService implements EventHandler {

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
    public void onServerInstanceConnected(final ServerInstanceConnected serverInstanceConnected) {
        serverInstanceConnectedEvent.fire(serverInstanceConnected);
    }

    @Override
    public void onServerInstanceDeleted(final ServerInstanceDeleted serverInstanceDeleted) {
        serverInstanceDeletedEvent.fire(serverInstanceDeleted);
    }

    @Override
    public void onServerInstanceDisconnected(final ServerInstanceDisconnected serverInstanceDisconnected) {
        serverInstanceDisconnectedEvent.fire(serverInstanceDisconnected);
    }

    @Override
    public void onServerTemplateDeleted(final ServerTemplateDeleted serverTemplateDeleted) {
        serverTemplateDeletedEvent.fire(serverTemplateDeleted);
    }

    @Override
    public void onServerTemplateUpdated(final ServerTemplateUpdated serverTemplateUpdated) {
        serverTemplateUpdatedEvent.fire(serverTemplateUpdated);
    }

    @Override
    public void onServerInstanceUpdated(final ServerInstanceUpdated serverInstanceUpdated) {
        serverInstanceUpdatedEvent.fire(serverInstanceUpdated);
    }

    @Override
    public void onContainerSpecUpdated(final ContainerSpecUpdated containerSpecUpdated) {
        final ContainerSpecData containerSpecData = new ContainerSpecData(containerSpecUpdated.getContainerSpec(),
                                                                          containerSpecUpdated.getContainers());

        containerSpecDataEvent.fire(containerSpecData);
    }
}
