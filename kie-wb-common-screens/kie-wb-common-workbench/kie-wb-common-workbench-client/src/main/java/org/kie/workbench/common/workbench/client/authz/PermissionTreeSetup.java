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
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.structure.client.security.OrganizationalUnitTreeProvider;
import org.guvnor.structure.client.security.RepositoryTreeProvider;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.uberfire.client.authz.EditorTreeProvider;
import org.uberfire.client.authz.PerspectiveTreeProvider;

import static org.kie.workbench.common.workbench.client.EditorIds.DMN_DESIGNER;
import static org.kie.workbench.common.workbench.client.EditorIds.CASE_MODELLER;
import static org.kie.workbench.common.workbench.client.EditorIds.GUIDED_DECISION_TREE;
import static org.kie.workbench.common.workbench.client.EditorIds.GUIDED_SCORE_CARD;
import static org.kie.workbench.common.workbench.client.EditorIds.SCENARIO_SIMULATION_DESIGNER;
import static org.kie.workbench.common.workbench.client.EditorIds.STUNNER_DESIGNER;
import static org.kie.workbench.common.workbench.client.EditorIds.XLS_SCORE_CARD;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.*;

/**
 * This is an example of how to customize some of the permission tree nodes.
 */
@ApplicationScoped
public class PermissionTreeSetup {

    private WorkbenchTreeProvider workbenchTreeProvider;
    private PerspectiveTreeProvider perspectiveTreeProvider;
    private EditorTreeProvider editorTreeProvider;
    private Instance<OrganizationalUnitTreeProvider> orgUnitTreeProvider;
    private Instance<RepositoryTreeProvider> repositoryTreeProvider;

    private DefaultWorkbenchConstants i18n = DefaultWorkbenchConstants.INSTANCE;

    public PermissionTreeSetup() {
        //CDI proxy
    }

    @Inject
    public PermissionTreeSetup(final WorkbenchTreeProvider workbenchTreeProvider,
                               final PerspectiveTreeProvider perspectiveTreeProvider,
                               final EditorTreeProvider editorTreeProvider,
                               final Instance<OrganizationalUnitTreeProvider> orgUnitTreeProvider,
                               final Instance<RepositoryTreeProvider> repositoryTreeProvider) {
        this.workbenchTreeProvider = workbenchTreeProvider;
        this.perspectiveTreeProvider = perspectiveTreeProvider;
        this.editorTreeProvider = editorTreeProvider;
        this.orgUnitTreeProvider = orgUnitTreeProvider;
        this.repositoryTreeProvider = repositoryTreeProvider;
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
        perspectiveTreeProvider.setPerspectiveName(CONTENT_MANAGEMENT,
                                                   i18n.Content_Management());
        perspectiveTreeProvider.setPerspectiveName(PROVISIONING,
                                                   i18n.Provisioning());
        perspectiveTreeProvider.setPerspectiveName(SERVER_MANAGEMENT,
                                                   i18n.Rule_Deployments());
        perspectiveTreeProvider.setPerspectiveName(PROCESS_DEFINITIONS,
                                                   i18n.ProcessDefinitions());
        perspectiveTreeProvider.setPerspectiveName(PROCESS_INSTANCES,
                                                   i18n.ProcessInstances());
        perspectiveTreeProvider.setPerspectiveName(TASKS_ADMIN,
                                                   i18n.Tasks());
        perspectiveTreeProvider.setPerspectiveName(JOBS,
                                                   i18n.Jobs());
        perspectiveTreeProvider.setPerspectiveName(EXECUTION_ERRORS,
                                                   i18n.ExecutionErrors());
        perspectiveTreeProvider.setPerspectiveName(TASKS,
                                                   i18n.Task_Inbox());
        perspectiveTreeProvider.setPerspectiveName(PROCESS_DASHBOARD,
                                                   i18n.Process_Reports());
        perspectiveTreeProvider.setPerspectiveName(TASK_DASHBOARD,
                                                   i18n.Task_Reports());
        perspectiveTreeProvider.setPerspectiveName(APPS,
                                                   i18n.Apps());

        // Exclude some perspectives
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
        perspectiveTreeProvider.excludePerspectiveId(APPS); /* uberfire */
        perspectiveTreeProvider.excludePerspectiveId(SOCIAL_HOME); /* uberfire */
        perspectiveTreeProvider.excludePerspectiveId(SOCIAL_USER_HOME); /* uberfire */
        perspectiveTreeProvider.excludePerspectiveId(SSH_KEYS_EDITOR);

        // Include optional editors
        editorTreeProvider.registerEditor(GUIDED_DECISION_TREE,
                                          i18n.GuidedDecisionTree());
        editorTreeProvider.registerEditor(GUIDED_SCORE_CARD,
                                          i18n.GuidedScoreCard());
        editorTreeProvider.registerEditor(XLS_SCORE_CARD,
                                          i18n.XLSScoreCard());
        editorTreeProvider.registerEditor(STUNNER_DESIGNER,
                                          i18n.StunnerDesigner());
        editorTreeProvider.registerEditor(DMN_DESIGNER,
                                          i18n.DMNDesigner());
        editorTreeProvider.registerEditor(CASE_MODELLER,
                                          i18n.CaseModeller());
        editorTreeProvider.registerEditor(SCENARIO_SIMULATION_DESIGNER,
                                          i18n.ScenarioSimulationEditor());

        // Set the desired display order
        workbenchTreeProvider.setRootNodePosition(0);
        perspectiveTreeProvider.setRootNodePosition(1);
        editorTreeProvider.setRootNodePosition(2);
        if (!orgUnitTreeProvider.isUnsatisfied()) {
            orgUnitTreeProvider.get().setRootNodePosition(3);
        }
        if (!repositoryTreeProvider.isUnsatisfied()) {
            repositoryTreeProvider.get().setRootNodePosition(4);
        }
    }
}
