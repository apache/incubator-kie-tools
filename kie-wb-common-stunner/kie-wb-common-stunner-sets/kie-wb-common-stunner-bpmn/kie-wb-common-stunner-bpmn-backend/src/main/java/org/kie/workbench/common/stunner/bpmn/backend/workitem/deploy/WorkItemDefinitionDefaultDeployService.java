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

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.backend.workitem.WorkItemDefinitionResources;
import org.kie.workbench.common.stunner.core.backend.service.BackendFileSystemManager;
import org.kie.workbench.common.stunner.core.backend.service.BackendFileSystemManager.Asset;
import org.kie.workbench.common.stunner.core.backend.service.BackendFileSystemManager.AssetBuilder;
import org.kie.workbench.common.stunner.core.backend.service.BackendFileSystemManager.Assets;
import org.kie.workbench.common.stunner.core.diagram.Metadata;

/**
 * - It deploys the default work item definitions that the workbench provides.
 * - It does the deployment in same way as jbpm-designer does, in order to guarantee the
 * compatibility between both designers. This way it provides the same artifacts (and same file names) and
 * uses same FS structure as well for the deployment operation, as the jbpm-designer's ones.
 */
@ApplicationScoped
public class WorkItemDefinitionDefaultDeployService implements WorkItemDefinitionDeployService {

    static final String DEPLOY_MESSAGE = "Deployment of the default work item definitions";
    static final String ASSETS_ROOT = "/META-INF/org/kie/workbench/common/stunner/bpmn/backend/workitem/";
    static final String WID_FILE = "WorkDefinitions.wid";
    static final String EMAIL_ICON = "defaultemailicon.gif";
    static final String BR_ICON = "defaultbusinessrulesicon.png";
    static final String DECISION_ICON = "defaultdecisionicon.png";
    static final String LOG_ICON = "defaultlogicon.gif";
    static final String SERVICE_NODE_ICON = "defaultservicenodeicon.png";
    static final String[] ASSETS = new String[]{
            WID_FILE,
            EMAIL_ICON,
            BR_ICON,
            DECISION_ICON,
            LOG_ICON,
            SERVICE_NODE_ICON
    };

    private final WorkItemDefinitionResources resources;
    private final BackendFileSystemManager backendFileSystemManager;
    private final BiFunction<String, String, Asset> assetBuilder;

    // CDI proxy.
    protected WorkItemDefinitionDefaultDeployService() {
        this(null, null);
    }

    @Inject
    public WorkItemDefinitionDefaultDeployService(final WorkItemDefinitionResources resources,
                                                  final BackendFileSystemManager backendFileSystemManager) {
        this.resources = resources;
        this.backendFileSystemManager = backendFileSystemManager;
        this.assetBuilder = (fileName, cp) ->
                new AssetBuilder()
                        .setFileName(fileName)
                        .fromClasspathResouce(cp)
                        .build();
    }

    WorkItemDefinitionDefaultDeployService(final WorkItemDefinitionResources resources,
                                           final BackendFileSystemManager backendFileSystemManager,
                                           final BiFunction<String, String, Asset> assetBuilder) {
        this.resources = resources;
        this.backendFileSystemManager = backendFileSystemManager;
        this.assetBuilder = assetBuilder;
    }

    @Override
    public void deploy(final Metadata metadata) {
        backendFileSystemManager
                .deploy(resources.resolveGlobalPath(metadata),
                        new Assets(Arrays.stream(ASSETS)
                                           .map(asset -> assetBuilder.apply(asset,
                                                                            ASSETS_ROOT + asset))
                                           .collect(Collectors.toList())),
                        DEPLOY_MESSAGE);
    }
}
