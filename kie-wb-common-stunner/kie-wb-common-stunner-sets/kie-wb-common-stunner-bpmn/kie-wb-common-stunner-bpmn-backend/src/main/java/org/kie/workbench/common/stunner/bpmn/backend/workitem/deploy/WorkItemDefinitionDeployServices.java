/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.workitem.deploy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.backend.vfs.Path;

/**
 * It composites all the deployment tasks into a single service.
 * It handles concurrency on each of the root deployment paths.
 */
@ApplicationScoped
public class WorkItemDefinitionDeployServices {

    private final Iterable<WorkItemDefinitionDeployService> deployServices;
    private final Map<String, Path> deployed;

    // CDI Proxy.
    protected WorkItemDefinitionDeployServices() {
        this(null);
    }

    @Inject
    public WorkItemDefinitionDeployServices(final @Any Instance<WorkItemDefinitionDeployService> deployServices) {
        this(deployServices,
             new ConcurrentHashMap<>());
    }

    WorkItemDefinitionDeployServices(final Iterable<WorkItemDefinitionDeployService> deployServices,
                                     final Map<String, Path> deployed) {
        this.deployServices = deployServices;
        this.deployed = deployed;
    }

    @SuppressWarnings("all")
    public void deploy(final Metadata metadata) {
        final Path root = metadata.getRoot();
        final Path deployedPath = getDeployedRoot(metadata);
        final Path path = null != deployedPath ? deployedPath : root;
        synchronized (path) {
            if (null == getDeployedRoot(metadata)) {
                deployed.put(root.toURI(), root);
                deployServices.forEach(s -> s.deploy(metadata));
            }
        }
    }

    private Path getDeployedRoot(final Metadata metadata) {
        final Path root = metadata.getRoot();
        return deployed
                .values()
                .stream()
                .filter(root::equals)
                .findFirst()
                .orElse(null);
    }
}
