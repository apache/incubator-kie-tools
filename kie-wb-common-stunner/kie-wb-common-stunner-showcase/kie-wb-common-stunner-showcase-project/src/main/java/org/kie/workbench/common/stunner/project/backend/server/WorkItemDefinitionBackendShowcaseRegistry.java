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

package org.kie.workbench.common.stunner.project.backend.server;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.backend.workitem.WorkItemDefinitionBackendRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionCacheRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionMetadataRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionService;
import org.kie.workbench.common.stunner.core.backend.service.BackendFileSystemManager;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

/**
 * A Work Item Definition backend registry specialization for the project showcase.
 * It deploys (on demand) the work item definitions forund at WEB-INF/wid, if any, for each kie project.
 */
@RequestScoped
@Specializes
public class WorkItemDefinitionBackendShowcaseRegistry
        extends WorkItemDefinitionBackendRegistry {

    private static final String WID_PATH = "wid";
    // Do never use this in a serious environment xD
    private static final Set<Path> deployed = new ConcurrentSkipListSet<>();

    private final BackendFileSystemManager backendFileSystemManager;

    // CDI proxy.
    protected WorkItemDefinitionBackendShowcaseRegistry() {
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public WorkItemDefinitionBackendShowcaseRegistry(final WorkItemDefinitionCacheRegistry registry,
                                                     final WorkItemDefinitionService service,
                                                     final WorkItemDefinitionMetadataRegistry metadataRegistry,
                                                     final BackendFileSystemManager backendFileSystemManager) {
        super(registry,
              service,
              metadataRegistry);
        this.backendFileSystemManager = backendFileSystemManager;
    }

    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    public WorkItemDefinitionBackendRegistry load(final Metadata metadata) {
        final Path root = metadata.getRoot();
        if (!deployed.contains(root)) {
            deploy(root);
        }
        super.load(metadata);
        return this;
    }

    private void deploy(final Path root) {
        final String widAppPath = backendFileSystemManager.getPathRelativeToApp(WID_PATH);
        backendFileSystemManager.findAndDeployFiles(new File(widAppPath),
                                                    Paths.convert(root));
        deployed.add(root);
    }
}
