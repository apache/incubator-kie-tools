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

import static org.kie.workbench.common.workbench.client.PerspectiveIds.*;

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
        perspectiveTreeProvider.setPerspectiveName(HOME, i18n.HomePage());
        perspectiveTreeProvider.setPerspectiveName(ADMIN, i18n.Admin());
        perspectiveTreeProvider.setPerspectiveName(SOCIAL_HOME, i18n.Timeline());
        perspectiveTreeProvider.setPerspectiveName(SOCIAL_USER_HOME, i18n.People());
        perspectiveTreeProvider.setPerspectiveName(SECURITY_MANAGEMENT, i18n.SecurityManagement());
        perspectiveTreeProvider.setPerspectiveName(LIBRARY, i18n.ProjectAuthoring());
        perspectiveTreeProvider.setPerspectiveName(CONTRIBUTORS, i18n.Contributors());
        perspectiveTreeProvider.setPerspectiveName(GUVNOR_M2REPO, i18n.ArtifactRepository());
        perspectiveTreeProvider.setPerspectiveName(ADMINISTRATION, i18n.Administration());
        perspectiveTreeProvider.setPerspectiveName(DROOLS_ADMIN, i18n.DroolsAdministration());
        perspectiveTreeProvider.setPerspectiveName(PLANNER_ADMIN, i18n.PlannerAdministration());
        perspectiveTreeProvider.setPerspectiveName(PROCESS_DEFINITIONS, i18n.ProcessDefinitions());
        perspectiveTreeProvider.setPerspectiveName(PROCESS_INSTANCES, i18n.ProcessInstances());
        perspectiveTreeProvider.setPerspectiveName(PLUGIN_AUTHORING, i18n.Plugins());
        perspectiveTreeProvider.setPerspectiveName(APPS, i18n.Apps());
        perspectiveTreeProvider.setPerspectiveName(DATASET_AUTHORING, i18n.DataSets());
        perspectiveTreeProvider.setPerspectiveName(DATASOURCE_MANAGEMENT, i18n.DataSources());
        perspectiveTreeProvider.setPerspectiveName(DEPLOYMENTS, i18n.Process_Deployments());
        perspectiveTreeProvider.setPerspectiveName(SERVER_MANAGEMENT, i18n.Rule_Deployments());
        perspectiveTreeProvider.setPerspectiveName(JOBS, i18n.Jobs());
        perspectiveTreeProvider.setPerspectiveName(EXECUTION_ERRORS, i18n.ExecutionErrors());
        perspectiveTreeProvider.setPerspectiveName(TASKS, i18n.Tasks());
        perspectiveTreeProvider.setPerspectiveName(PROCESS_DASHBOARD, i18n.Process_Dashboard());
        perspectiveTreeProvider.setPerspectiveName(BUSINESS_DASHBOARDS, i18n.Business_Dashboards());

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

        // Set the desired display order
        workbenchTreeProvider.setRootNodePosition(0);
        perspectiveTreeProvider.setRootNodePosition(1);
        orgUnitTreeProvider.setRootNodePosition(2);
        repositoryTreeProvider.setRootNodePosition(3);
        projectTreeProvider.setRootNodePosition(4);
    }
}