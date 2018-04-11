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

package org.kie.workbench.common.workbench.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface DefaultWorkbenchConstants
        extends
        Messages {

    DefaultWorkbenchConstants INSTANCE = GWT.create(DefaultWorkbenchConstants.class);

    String Role();

    String LogOut();

    String HomePage();

    String Timeline();

    String People();

    String SecurityManagement();

    String ProjectAuthoring();

    String ArtifactRepository();

    String Administration();

    String DroolsAdministration();

    String PlannerAdministration();

    String Plugins();

    String Apps();

    String DataSets();

    String DataSources();

    String ProcessDefinitions();

    String ProcessInstances();

    String Provisioning();

    String Rule_Deployments();

    String Jobs();

    String ExecutionErrors();

    String Task_Inbox();

    String Process_Reports();

    String Task_Reports();

    String Content_Management();

    String Group();

    String DocksOptaPlannerTitle();

    String DocksProjectExplorerTitle();

    String DocksDroolsJBPMTitle();

    String DocksPersistenceTitle();

    String DocksAdvancedTitle();

    String DocksStunnerPropertiesTitle();

    String DocksStunnerExplorerTitle();

    String WorkbenchRootNodeName();

    String PermissionAllow();

    String PermissionDeny();

    String DataModelerEditSources();

    String ResourcePlanner();

    String WorkbenchRootNodeHelp();

    String DataModelerEditSourcesHelp();

    String ResourcePlannerHelp();

    String Admin();

    String Settings();

    String Roles();

    String Groups();

    String Users();

    String Library();

    String MavenRepositoryPagedJarTableDownloadJar();

    String MavenRepositoryPagedJarTableDownloadJarHelp();

    String KieServerError403();

    String KieServerError401();

    String Help();

    String Artifacts();

    String EditGlobalPreferences();

    String EditGlobalPreferencesHelp();

    String GuidedDecisionTableEditColumns();

    String GuidedDecisionTableEditColumnsHelp();

    String Languages();

    String Tasks();

    String GuidedDecisionTree();

    String GuidedScoreCard();

    String XLSScoreCard();

    String StunnerDesigner();

    String InvalidBusResponseProbablySessionTimeout();

    String SessionTimeout();

    String LayoutEditorComponentPalette();

}
