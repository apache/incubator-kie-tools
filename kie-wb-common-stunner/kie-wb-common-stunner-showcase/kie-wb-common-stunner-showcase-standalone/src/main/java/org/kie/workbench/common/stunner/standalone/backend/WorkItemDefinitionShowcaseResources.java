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

package org.kie.workbench.common.stunner.standalone.backend;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.workbench.common.stunner.backend.service.BackendFileSystemBootstrap;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.WorkItemDefinitionResources;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

import static org.uberfire.backend.server.util.Paths.convert;

/**
 * Work Item Definition resources specialization class for the standalone showcase.
 * It provides different global roots for work item defintions rather than the defaults.
 */
@ApplicationScoped
@Specializes
public class WorkItemDefinitionShowcaseResources extends WorkItemDefinitionResources {

    private final BackendFileSystemBootstrap backendFileSystemManager;

    // CDI proxy.
    protected WorkItemDefinitionShowcaseResources() {
        this(null, null);
    }

    @Inject
    public WorkItemDefinitionShowcaseResources(final @Named("ioStrategy") IOService ioService,
                                               final BackendFileSystemBootstrap backendFileSystemManager) {
        super(ioService);
        this.backendFileSystemManager = backendFileSystemManager;
    }

    @Override
    public Path resolveResourcePath(final Metadata metadata) {
        if (null != metadata.getPath()) {
            return super.resolveResourcePath(metadata);
        }
        return backendFileSystemManager.getRootPath();
    }

    @Override
    public Path resolveGlobalPath(final Metadata metadata) {
        if (null != metadata.getRoot()) {
            return super.resolveGlobalPath(metadata);
        }
        return resolveGlobalPathByRoot(convert(backendFileSystemManager.getRootPath()));
    }

    @Override
    public Path resolveResourcesPath(final Metadata metadata) {
        return resolveGlobalPath(metadata);
    }
}
