/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.structure.client.security.OrganizationalUnitTreeProvider;
import org.guvnor.structure.client.security.RepositoryTreeProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.authz.EditorTreeProvider;
import org.uberfire.client.authz.PerspectiveTreeProvider;
import org.uberfire.mocks.MockInstanceImpl;

import static org.kie.workbench.common.workbench.client.EditorIds.CASE_MODELLER;
import static org.kie.workbench.common.workbench.client.EditorIds.GUIDED_DECISION_TREE;
import static org.kie.workbench.common.workbench.client.EditorIds.GUIDED_SCORE_CARD;
import static org.kie.workbench.common.workbench.client.EditorIds.SCENARIO_SIMULATION_DESIGNER;
import static org.kie.workbench.common.workbench.client.EditorIds.STUNNER_DESIGNER;
import static org.kie.workbench.common.workbench.client.EditorIds.XLS_SCORE_CARD;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.ADMIN;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.ADMINISTRATION;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.APPS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.CONTENT_MANAGEMENT;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.DATASET_AUTHORING;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.DATASOURCE_MANAGEMENT;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.EXECUTION_ERRORS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.GUVNOR_M2REPO;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.HOME;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.JOBS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.LIBRARY;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PLUGIN_AUTHORING;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PROCESS_DASHBOARD;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PROCESS_DEFINITIONS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PROCESS_INSTANCES;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PROVISIONING;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.SECURITY_MANAGEMENT;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.SERVER_MANAGEMENT;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.SSH_KEYS_EDITOR;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.TASKS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.TASKS_ADMIN;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.TASK_DASHBOARD;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class PermissionTreeSetupTest {

    @Mock
    private WorkbenchTreeProvider workbenchTreeProvider;

    @Mock
    private PerspectiveTreeProvider perspectiveTreeProvider;

    @Mock
    private EditorTreeProvider editorTreeProvider;

    @Mock
    private MockInstanceImpl<OrganizationalUnitTreeProvider> orgUnitTreeProvider;

    @Mock
    private MockInstanceImpl<RepositoryTreeProvider> repositoryTreeProvider;

    private PermissionTreeSetup tree;

    @Before
    public void setup() {
        when(orgUnitTreeProvider.isUnsatisfied()).thenReturn(true);
        when(repositoryTreeProvider.isUnsatisfied()).thenReturn(true);

        this.tree = new PermissionTreeSetup(workbenchTreeProvider,
                                            perspectiveTreeProvider,
                                            editorTreeProvider,
                                            orgUnitTreeProvider,
                                            repositoryTreeProvider);
    }

    @Test
    public void testConfigureTree_Perspectives() {
        tree.configureTree();

        verify(perspectiveTreeProvider).setPerspectiveName(eq(HOME),
                                                           eq("HomePage"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(ADMIN),
                                                           eq("Admin"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(SECURITY_MANAGEMENT),
                                                           eq("SecurityManagement"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(GUVNOR_M2REPO),
                                                           eq("ArtifactRepository"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(DATASET_AUTHORING),
                                                           eq("DataSets"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(DATASOURCE_MANAGEMENT),
                                                           eq("DataSources"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(LIBRARY),
                                                           eq("ProjectAuthoring"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(CONTENT_MANAGEMENT),
                                                           eq("Content_Management"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(PROVISIONING),
                                                           eq("Provisioning"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(SERVER_MANAGEMENT),
                                                           eq("Rule_Deployments"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(PROCESS_DEFINITIONS),
                                                           eq("ProcessDefinitions"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(PROCESS_INSTANCES),
                                                           eq("ProcessInstances"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(TASKS_ADMIN),
                                                           eq("Tasks"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(JOBS),
                                                           eq("Jobs"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(EXECUTION_ERRORS),
                                                           eq("ExecutionErrors"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(TASKS),
                                                           eq("Task_Inbox"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(PROCESS_DASHBOARD),
                                                           eq("Process_Reports"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(TASK_DASHBOARD),
                                                           eq("Task_Reports"));
        verify(perspectiveTreeProvider).setPerspectiveName(eq(APPS),
                                                           eq("Apps"));
    }

    @Test
    public void testConfigureTree_ExcludedPerspectives() {
        tree.configureTree();

        verify(perspectiveTreeProvider).excludePerspectiveId(eq("AuthoringPerspectiveNoContext"));
        verify(perspectiveTreeProvider).excludePerspectiveId(eq("FormDisplayPerspective"));
        verify(perspectiveTreeProvider).excludePerspectiveId(eq("Drools Tasks"));
        verify(perspectiveTreeProvider).excludePerspectiveId(eq("Experimental Paging"));
        verify(perspectiveTreeProvider).excludePerspectiveId(eq("StandaloneEditorPerspective"));
        verify(perspectiveTreeProvider).excludePerspectiveId(eq("WiresTreesPerspective"));
        verify(perspectiveTreeProvider).excludePerspectiveId(eq("WiresGridsDemoPerspective"));
        verify(perspectiveTreeProvider).excludePerspectiveId(eq("PreferencesCentralPerspective"));
        verify(perspectiveTreeProvider).excludePerspectiveId(eq(ADMINISTRATION));
        verify(perspectiveTreeProvider).excludePerspectiveId(eq(PLUGIN_AUTHORING));
        verify(perspectiveTreeProvider).excludePerspectiveId(eq(SSH_KEYS_EDITOR));
    }

    @Test
    public void testConfigureTree_RegisteredEditors() {
        tree.configureTree();

        verify(editorTreeProvider).registerEditor(eq(GUIDED_DECISION_TREE),
                                                  eq("GuidedDecisionTree"));
        verify(editorTreeProvider).registerEditor(eq(GUIDED_SCORE_CARD),
                                                  eq("GuidedScoreCard"));
        verify(editorTreeProvider).registerEditor(eq(XLS_SCORE_CARD),
                                                  eq("XLSScoreCard"));
        verify(editorTreeProvider).registerEditor(eq(STUNNER_DESIGNER),
                                                  eq("StunnerDesigner"));
        verify(editorTreeProvider).registerEditor(eq(CASE_MODELLER),
                                                  eq("CaseModeller"));
        verify(editorTreeProvider).registerEditor(eq(SCENARIO_SIMULATION_DESIGNER),
                                                  eq("ScenarioSimulationEditor"));
    }

    @Test
    public void testConfigureTree_ProviderOrders() {
        final OrganizationalUnitTreeProvider orgUnitTree = mock(OrganizationalUnitTreeProvider.class);
        final RepositoryTreeProvider repositoryTree = mock(RepositoryTreeProvider.class);
        when(orgUnitTreeProvider.isUnsatisfied()).thenReturn(false);
        when(repositoryTreeProvider.isUnsatisfied()).thenReturn(false);
        when(orgUnitTreeProvider.get()).thenReturn(orgUnitTree);
        when(repositoryTreeProvider.get()).thenReturn(repositoryTree);

        tree.configureTree();

        verify(workbenchTreeProvider).setRootNodePosition(eq(0));
        verify(perspectiveTreeProvider).setRootNodePosition(eq(1));
        verify(editorTreeProvider).setRootNodePosition(eq(2));
        verify(orgUnitTree).setRootNodePosition(eq(3));
        verify(repositoryTree).setRootNodePosition(eq(4));
    }
}
