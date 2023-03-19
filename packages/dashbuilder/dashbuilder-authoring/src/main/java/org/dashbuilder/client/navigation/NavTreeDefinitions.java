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
package org.dashbuilder.client.navigation;

import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.impl.NavTreeBuilder;

import static org.dashbuilder.navigation.workbench.NavWorkbenchCtx.perspective;
import static org.dashbuilder.perspectives.PerspectiveIds.CONTENT_MANAGER;
import static org.dashbuilder.perspectives.PerspectiveIds.DATA_SETS;
import static org.dashbuilder.perspectives.PerspectiveIds.DATA_TRANSFER;

/**
 * Navigation tree definitions such as the top menu bar
 */
public interface NavTreeDefinitions {

    String DASHBOARDS_GROUP = "dashboards_group";
    String GROUP_ADMIN = "app_admin";
    String ENTRY_DATASETS = "app_datasets";
    String ENTRY_CONTENT_MGR = "app_contentMgr";
    String GROUP_DASHBOARDS = "app_dashboards";

    NavTree NAV_TREE_DEFAULT = new NavTreeBuilder()
            .group(GROUP_ADMIN, "Administration", "The administration tools", false)
                .item(ENTRY_DATASETS, "Datasets", "The dataset authoring tool", false, perspective(DATA_SETS))
                .item(ENTRY_CONTENT_MGR, "Content manager", "The content manager tool", false, perspective(CONTENT_MANAGER))
                .item(ENTRY_CONTENT_MGR, "Data Transfer", "Allow content transference between installations", false, perspective(DATA_TRANSFER))
            .endGroup()
        .build();
    
    
    NavTree INITIAL_EMPTY = new NavTreeBuilder()
                                        .group(DASHBOARDS_GROUP, "Dashboards", "", true)
                                        .endGroup()
                                    .build();
}
