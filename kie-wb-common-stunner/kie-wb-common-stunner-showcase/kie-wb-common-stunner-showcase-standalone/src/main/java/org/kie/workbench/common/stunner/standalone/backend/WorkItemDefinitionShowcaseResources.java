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

import java.io.File;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.workbench.common.stunner.backend.service.BackendFileSystemManagerImpl;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.WorkItemDefinitionResources;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

/**
 * Work Item Definition resources specialization class for the standalone showcase.
 * It provides a different global root for work item defintions rather than the defaults.
 * It also deploys (on demand) the work item definitions found at WEB-INF/wid, if any.
 */
@ApplicationScoped
@Specializes
public class WorkItemDefinitionShowcaseResources extends WorkItemDefinitionResources {

    private static final String WID_PATH = "wid";

    private final BackendFileSystemManagerImpl backendFileSystemManager;

    // CDI proxy.
    protected WorkItemDefinitionShowcaseResources() {
        this(null, null);
    }

    @Inject
    public WorkItemDefinitionShowcaseResources(final @Named("ioStrategy") IOService ioService,
                                               final BackendFileSystemManagerImpl backendFileSystemManager) {
        super(ioService,
              () -> WID_PATH);
        this.backendFileSystemManager = backendFileSystemManager;
    }

    @PostConstruct
    public void init() {
        // Register the work item definitions found in the webapp directory.
        registerAppDefinitions();
    }

    @Override
    public Path resolveSearchPath(org.uberfire.backend.vfs.Path path) {
        return super.resolveSearchPath(null != path ? path : Paths.convert(getWorkItemDefinitionsRootPath()));
    }

    private void registerAppDefinitions() {
        final String widAppPath = backendFileSystemManager.getPathRelativeToApp(WID_PATH);
        backendFileSystemManager.findAndDeployFiles(new File(widAppPath),
                                                    getWorkItemDefinitionsRootPath());
    }

    private Path getWorkItemDefinitionsRootPath() {
        return backendFileSystemManager.getRootPath().resolve(WID_PATH);
    }
}
