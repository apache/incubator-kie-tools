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

package org.kie.workbench.common.screens.server.management.backend.storage.migration;

import java.util.HashMap;
import java.util.Set;

import com.thoughtworks.xstream.XStream;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.controller.api.ModelFactory;
import org.kie.server.controller.api.model.KieServerInstance;
import org.kie.server.controller.api.model.KieServerInstanceInfo;
import org.kie.server.controller.api.model.KieServerSetup;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.storage.KieServerTemplateStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.Path;

/**
 * Allows automatic migration from 6.3 stored kie server templates to new 6.4.x onwards server templates
 */
public class ServerTemplateMigration {

    private static final Logger logger = LoggerFactory.getLogger(ServerTemplateMigration.class);

    public void migrate(Path dir, IOService ioService, XStream xs, KieServerTemplateStorage templateStorage) {

        logger.debug("Attempting to find and migrate 6.2 type kie server templates inside directory '{}'...", dir);
        try {
            ioService.startBatch(dir.getFileSystem());
            for (final Path path : ioService.newDirectoryStream(dir, new DirectoryStream.Filter<Path>() {
                @Override
                public boolean accept(Path entry) throws IOException {
                    return entry.toString().endsWith("-info.xml");
                }
            })) {

                logger.debug("Found 6.2 type kie server template file '{}', migrating it...", path);
                try {
                    final KieServerInstance kieServerInstance = (KieServerInstance) xs.fromXML(ioService.readAllString(path));

                    logger.debug("Loaded KieServerInstance {}", kieServerInstance);
                    ServerTemplate serverTemplate = new ServerTemplate();
                    serverTemplate.setId(kieServerInstance.getIdentifier());
                    serverTemplate.setName(kieServerInstance.getName());

                    KieServerSetup serverSetup = kieServerInstance.getKieServerSetup();
                    if (serverSetup != null) {
                        Set<KieContainerResource> containerResources = kieServerInstance.getKieServerSetup().getContainers();
                        logger.debug("Server with id {} has containers {}", kieServerInstance.getIdentifier(), containerResources);
                        if (containerResources != null) {
                            for (KieContainerResource containerRef : containerResources) {

                                ContainerSpec containerSpec = new ContainerSpec(containerRef.getContainerId(),
                                        containerRef.getContainerId(),
                                        serverTemplate,
                                        containerRef.getReleaseId(),
                                        containerRef.getStatus(),
                                        new HashMap<Capability, ContainerConfig>());

                                logger.debug("Migrating container '{}' to container spec '{}'", containerRef, containerSpec);
                                serverTemplate.addContainerSpec(containerSpec);
                            }
                        }

                    }
                    Set<KieServerInstanceInfo> instanceInfos = kieServerInstance.getManagedInstances();
                    if (instanceInfos != null) {
                        logger.debug("Server with id {} has server instances {}", kieServerInstance.getIdentifier(), instanceInfos);
                        for (KieServerInstanceInfo instanceInfo : instanceInfos) {

                            logger.debug("Migrating server instance '{}'", instanceInfo);
                            serverTemplate.addServerInstance(ModelFactory.newServerInstanceKey(serverTemplate.getId(), instanceInfo.getLocation()));

                            serverTemplate.setCapabilities(instanceInfo.getCapabilities());
                        }
                    }
                    logger.debug("About to store migrated server template {}", serverTemplate);
                    // store migrated information
                    templateStorage.store(serverTemplate);
                    logger.info("Server template {} migrated successfully, removing old version...", serverTemplate);
                    // delete old to do not attempt second time migration
                    try {
                        ioService.startBatch(path.getFileSystem());
                        ioService.delete(path);
                    } finally {
                        ioService.endBatch();
                    }
                    logger.debug("Old version of server template '{}' has been removed", kieServerInstance);
                } catch (Exception ex) {
                    logger.error("Error while migrating old version (6.2.) of kie server instance from path {}", path, ex);
                }
            }

        } catch ( final NotDirectoryException ignore ) {
            logger.debug("No directory found, ignoring migration of kie server templates");
        } finally {
            ioService.endBatch();
        }
    }
}
