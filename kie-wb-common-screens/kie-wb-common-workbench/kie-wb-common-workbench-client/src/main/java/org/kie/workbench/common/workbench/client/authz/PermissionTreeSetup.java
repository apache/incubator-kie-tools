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

package org.kie.workbench.common.workbench.client.authz;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.security.ProjectTreeProvider;
import org.guvnor.structure.client.security.OrganizationalUnitTreeProvider;
import org.guvnor.structure.client.security.RepositoryTreeProvider;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.authz.PerspectiveTreeProvider;

import static org.kie.workbench.common.workbench.client.PerspectiveIds.ADMIN;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.ADMINISTRATION;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.APPS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.AUTHORING;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.BUSINESS_DASHBOARDS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.DATASET_AUTHORING;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.DATASOURCE_MANAGEMENT;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.DEPLOYMENTS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.EXECUTION_ERRORS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.GUVNOR_M2REPO;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.HOME;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.JOBS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.LIBRARY;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PLUGIN_AUTHORING;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PROCESS_DASHBOARD;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PROCESS_DEFINITIONS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PROCESS_INSTANCES;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.SECURITY_MANAGEMENT;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.SERVER_MANAGEMENT;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.SOCIAL_HOME;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.SOCIAL_USER_HOME;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.TASKS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.TASKS_ADMIN;

/**
 * This is an example of how to customize some of the permission tree nodes.
 */
@ApplicationScoped
public class PermissionTreeSetup {

    private WorkbenchTreeProvider workbenchTreeProvider;
    private PerspectiveTreeProvider perspectiveTreeProvider;
    private OrganizationalUnitTreeProvider orgUnitTreeProvider;
    private RepositoryTreeProvider repositoryTreeProvider;
    private ProjectTreeProvider projectTreeProvider;
    private DefaultWorkbenchConstants i18n = DefaultWorkbenchConstants.INSTANCE;

    public PermissionTreeSetup() {
    }

    @Inject
    public PermissionTreeSetup(WorkbenchTreeProvider workbenchTreeProvider,
                               PerspectiveTreeProvider perspectiveTreeProvider,
                               OrganizationalUnitTreeProvider orgUnitTreeProvider,
                               RepositoryTreeProvider repositoryTreeProvider,
                               ProjectTreeProvider projectTreeProvider) {
        this.workbenchTreeProvider = workbenchTreeProvider;
        this.perspectiveTreeProvider = perspectiveTreeProvider;
        this.orgUnitTreeProvider = orgUnitTreeProvider;
        this.repositoryTreeProvider = repositoryTreeProvider;
        this.projectTreeProvider = projectTreeProvider;
    }

    public void configureTree() {
        perspectiveTreeProvider.setPerspectiveName(HOME,
                                                   i18n.HomePage());
        perspectiveTreeProvider.setPerspectiveName(ADMIN,
                                                   i18n.Admin());
        perspectiveTreeProvider.setPerspectiveName(SECURITY_MANAGEMENT,
                                                   i18n.SecurityManagement());
        perspectiveTreeProvider.setPerspectiveName(GUVNOR_M2REPO,
                                                   i18n.ArtifactRepository());
        perspectiveTreeProvider.setPerspectiveName(DATASET_AUTHORING,
                                                   i18n.DataSets());
        perspectiveTreeProvider.setPerspectiveName(DATASOURCE_MANAGEMENT,
                                                   i18n.DataSources());
        perspectiveTreeProvider.setPerspectiveName(LIBRARY,
                                                   i18n.ProjectAuthoring());
        perspectiveTreeProvider.setPerspectiveName(BUSINESS_DASHBOARDS,
                                                   i18n.Business_Dashboards());
        perspectiveTreeProvider.setPerspectiveName(DEPLOYMENTS,
                                                   i18n.Process_Deployments());
        perspectiveTreeProvider.setPerspectiveName(SERVER_MANAGEMENT,
                                                   i18n.Rule_Deployments());
        perspectiveTreeProvider.setPerspectiveName(PROCESS_DEFINITIONS,
                                                   i18n.ProcessDefinitions());
        perspectiveTreeProvider.setPerspectiveName(PROCESS_INSTANCES,
                                                   i18n.ProcessInstances());
        perspectiveTreeProvider.setPerspectiveName(TASKS_ADMIN,
                                                   i18n.Tasks_Admin());
        perspectiveTreeProvider.setPerspectiveName(JOBS,
                                                   i18n.Jobs());
        perspectiveTreeProvider.setPerspectiveName(EXECUTION_ERRORS,
                                                   i18n.ExecutionErrors());
        perspectiveTreeProvider.setPerspectiveName(TASKS,
                                                   i18n.Tasks());
        perspectiveTreeProvider.setPerspectiveName(PROCESS_DASHBOARD,
                                                   i18n.Process_Dashboard());
        perspectiveTreeProvider.setPerspectiveName(APPS,
                                                   i18n.Apps());

        // Exclude some perspectives
        perspectiveTreeProvider.excludePerspectiveId(AUTHORING); /* kie-wb-distributions */
        perspectiveTreeProvider.excludePerspectiveId("AuthoringPerspectiveNoContext"); /* kie-wb-distributions */
        perspectiveTreeProvider.excludePerspectiveId("FormDisplayPerspective"); /* jbpm-console-ng */
        perspectiveTreeProvider.excludePerspectiveId("Drools Tasks"); /* jbpm-console-ng */
        perspectiveTreeProvider.excludePerspectiveId("Experimental Paging"); /* jbpm-console-ng */
        perspectiveTreeProvider.excludePerspectiveId("StandaloneEditorPerspective"); /* uberfire */
        perspectiveTreeProvider.excludePerspectiveId("WiresTreesPerspective"); /* uberfire */
        perspectiveTreeProvider.excludePerspectiveId("WiresGridsDemoPerspective"); /* uberfire */
        perspectiveTreeProvider.excludePerspectiveId("PreferencesCentralPerspective"); /* uberfire */
        perspectiveTreeProvider.excludePerspectiveId(ADMINISTRATION); /* kie-wb-distributions */
        perspectiveTreeProvider.excludePerspectiveId(PLUGIN_AUTHORING); /* uberfire */
        perspectiveTreeProvider.excludePerspectiveId(SOCIAL_HOME); /* uberfire */
        perspectiveTreeProvider.excludePerspectiveId(SOCIAL_USER_HOME); /* uberfire */
        perspectiveTreeProvider.excludePerspectiveId("Asset Management"); /* guvnor */
        perspectiveTreeProvider.excludePerspectiveId(SOCIAL_USER_HOME); /* uberfire */

        // Set the desired display order
        workbenchTreeProvider.setRootNodePosition(0);
        perspectiveTreeProvider.setRootNodePosition(1);
        orgUnitTreeProvider.setRootNodePosition(2);
        repositoryTreeProvider.setRootNodePosition(3);
        projectTreeProvider.setRootNodePosition(4);
    }
}