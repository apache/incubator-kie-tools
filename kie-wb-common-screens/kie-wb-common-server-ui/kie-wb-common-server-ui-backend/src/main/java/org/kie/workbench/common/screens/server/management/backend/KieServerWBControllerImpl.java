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

package org.kie.workbench.common.screens.server.management.backend;

import java.util.HashMap;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.GAV;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.controller.api.KieServerController;
import org.kie.server.controller.api.KieServerControllerAdmin;
import org.kie.server.controller.api.model.KieServerInstance;
import org.kie.server.controller.api.model.KieServerInstanceInfo;
import org.kie.server.controller.api.model.KieServerSetup;
import org.kie.server.controller.api.model.KieServerStatus;
import org.kie.server.controller.api.storage.KieServerControllerStorage;
import org.kie.server.controller.rest.RestKieServerControllerImpl;
import org.kie.workbench.common.screens.server.management.events.ServerConnected;
import org.kie.workbench.common.screens.server.management.events.ServerDisconnected;
import org.kie.workbench.common.screens.server.management.events.ServerOnError;
import org.kie.workbench.common.screens.server.management.model.ConnectionType;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;
import org.kie.workbench.common.screens.server.management.model.Server;
import org.kie.workbench.common.screens.server.management.model.ServerInstanceRef;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.model.impl.ContainerRefImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerInstanceRefImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl;

@ApplicationScoped
public class KieServerWBControllerImpl extends RestKieServerControllerImpl {

    @Inject
    private Event<ServerConnected> serverConnectedEvent;
    @Inject
    private Event<ServerDisconnected> serverDisconnectedEvent;
    @Inject
    private KieServerControllerAdmin controllerAdmin;

    @Inject
    @Override
    public void setStorage(KieServerControllerStorage storage) {
        super.setStorage(storage);
    }

    @Override
    public KieServerSetup connect(KieServerInfo serverInfo) {
        KieServerSetup kieServerSetup = super.connect(serverInfo);

        KieServerInstance kieServerInstance = controllerAdmin.getKieServerInstance(serverInfo.getServerId());

        serverConnectedEvent.fire( new ServerConnected( ServerUtility.buildServer(kieServerInstance) ) );
        return kieServerSetup;
    }

    @Override
    public void disconnect(KieServerInfo serverInfo) {
        super.disconnect(serverInfo);

        KieServerInstance kieServerInstance = controllerAdmin.getKieServerInstance(serverInfo.getServerId());
        serverDisconnectedEvent.fire( new ServerDisconnected( ServerUtility.buildServer(kieServerInstance)) );
    }


}
