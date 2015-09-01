/*
 * Copyright 2015 JBoss Inc
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

import org.guvnor.common.services.project.model.GAV;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.controller.api.model.KieServerInstance;
import org.kie.server.controller.api.model.KieServerInstanceInfo;
import org.kie.server.controller.api.model.KieServerStatus;
import org.kie.workbench.common.screens.server.management.model.ConnectionType;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;
import org.kie.workbench.common.screens.server.management.model.Server;
import org.kie.workbench.common.screens.server.management.model.ServerInstanceRef;
import org.kie.workbench.common.screens.server.management.model.impl.ContainerRefImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerInstanceRefImpl;

public class ServerUtility {

    public static Server buildServer(final KieServerInstance kieServerInstance) {
        Server serverRef = new ServerImpl(
                kieServerInstance.getIdentifier(),
                "",
                kieServerInstance.getName(),
                "user",
                "password",
                kieServerInstance.getStatus().equals(KieServerStatus.DOWN)? ContainerStatus.STOPPED :  ContainerStatus.STARTED,
                ConnectionType.REMOTE,
                null,
                new HashMap<String, String>() {{
                    put( "version", kieServerInstance.getVersion() );
                }},
                null
        );
        // prepare containers
        if (kieServerInstance.getKieServerSetup() != null && kieServerInstance.getKieServerSetup().getContainers() != null) {

            Set<KieContainerResource> containerResources = kieServerInstance.getKieServerSetup().getContainers();
            for (KieContainerResource containerResource : containerResources) {

                GAV gav = new GAV(containerResource.getReleaseId().getGroupId(), containerResource.getReleaseId().getArtifactId(), containerResource.getReleaseId().getVersion());
                ContainerRef containerRef = new ContainerRefImpl(serverRef.getId(),
                        containerResource.getContainerId(),
                        KieContainerStatus.STARTED.equals(containerResource.getStatus())?ContainerStatus.STARTED:ContainerStatus.STOPPED,
                        gav,
                        containerResource.getScanner() == null ? null : ScannerStatus.valueOf(containerResource.getScanner().getStatus().toString()),
                        containerResource.getScanner() == null ? null : containerResource.getScanner().getPollInterval());

                serverRef.addContainerRef(containerRef);
            }
        }

        // prepare managed instances
        if (kieServerInstance.getManagedInstances() != null) {
            for (KieServerInstanceInfo instanceInfo : kieServerInstance.getManagedInstances()) {
                ServerInstanceRef instanceRef = new ServerInstanceRefImpl(instanceInfo.getStatus().toString(), instanceInfo.getLocation());
                serverRef.addManagedServer(instanceRef);
            }
        }
        return serverRef;
    }
}
