/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.asset.management.client.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * This uses GWT to provide client side compile time resolving of locales. See:
 * http://code.google.com/docreader/#p=google-web-toolkit-doc-1-5&s=google-web- toolkit-doc-1-5&t=DevGuideInternationalization
 * (for more information).
 * <p/>
 * Each method name matches up with a key in Constants.properties (the properties file can still be used on the server). To use
 * this, use <code>GWT.create(Constants.class)</code>.
 */
public interface Constants extends Messages {

    Constants INSTANCE = GWT.create(Constants.class);

    String Release_Branch();

    String Dev_Branch();

    String Configure_Repository();

    String Choose_Repository();

    String Repository_Configuration();

    String Choose_Branch();

    String Choose_Project();

    String User_Name();

    String Password();

    String Server_URL();

    String Build_Project();

    String Deploy_To_Maven();

    String Deploy_To_Runtime();

    String Build_Configuration();

    String Promote_Assets();

    String Choose_Source_Branch();

    String Choose_Destination_Branch();

    String Version();

    String ReleaseVersion();

    String ABuildIsAlreadyRunning();

    String Loading();

    String RepositoryStructureWithName(String string);

    String UnmanagedRepository(String repository);

    String UnInitializedStructure(String repository);

    String Save();

    String Saving();

    String Deleting();

    String RepositoryStructure();

    String VersionHolder();

    String ArtifactIdHolder();

    String GroupIdHolder();

    String CreatingRepositoryStructure();

    String ConvertingToMultiModuleProject();

    String AddModule();

    String DeleteModule();

    String EditModule();

    String Modules();

    String Module();

    String NewProject();

    String Projects();

    String Project();

    String RepositoryNotSelected();

    String ConfirmModuleDeletion(String module);

    String ConfirmProjectDeletion(String project);

    String ConfirmSaveRepositoryStructure();

    String ConfirmConvertToMultiModuleStructure();

    //Repository structure data widget constants

    String InitRepositoryStructure();

    String EditProject();

    String SaveChanges();

    String ConvertToMultiModule();

    //create
    String Repository_structure_view_create_projectTypeLabel();

    String Repository_structure_view_create_isSingleModuleRadioButton();

    String Repository_structure_view_create_isSingleModuleRadioButtonHelpInline();

    String Repository_structure_view_create_isMultiModuleRadioButton();

    String Repository_structure_view_create_isMultiModuleRadioButtonHelpInline();

    String Repository_structure_view_create_groupIdTextBoxHelpInline();

    String Repository_structure_view_create_artifactIdTextBoxHelpInline();

    String Repository_structure_view_create_versionTextBoxHelpInline();

    String Repository_structure_view_create_isUnmanagedRepositoryRadioButton();

    String Repository_structure_view_create_isUnmanagedRepositoryButtonHelpInline();

    //single module
    String Repository_structure_view_edit_single_projectTypeLabel();

    String Repository_structure_view_edit_single_isSingleModuleRadioButton();

    String Repository_structure_view_edit_single_isSingleModuleRadioButtonHelpInline();

    String Repository_structure_view_edit_single_isMultiModuleRadioButton();

    String Repository_structure_view_edit_single_isMultiModuleRadioButtonHelpInline();

    String Repository_structure_view_edit_single_groupIdTextBoxHelpInline();

    String Repository_structure_view_edit_single_artifactIdTextBoxHelpInline();

    String Repository_structure_view_edit_single_versionTextBoxHelpInline();

    //multi module
    String Repository_structure_view_edit_multi_projectTypeLabel();

    String Repository_structure_view_edit_multi_isMultiModuleRadioButton();

    String Repository_structure_view_edit_multi_isMultiModuleRadioButtonHelpInline();

    String Repository_structure_view_edit_multi_groupIdTextBoxHelpInline();

    String Repository_structure_view_edit_multi_artifactIdTextBoxHelpInline();

    String Repository_structure_view_edit_multi_versionTextBoxHelpInline();

    //unmanaged repo
    String Repository_structure_view_edit_unmanaged_projectTypeLabel();

    String Repository_structure_view_edit_unmanaged_isUnmanagedRepositoryRadioButton();

    String Repository_structure_view_edit_unmanaged_isUnmanagedRepositoryButtonHelpInline();

    //End of Project structure data widget constants

    String Current_Version();

    String Select_Repository();

    String No_Project_Structure_Available();

    String Select_A_Branch();

    String Commits_To_Promote();

    String Requires_Review();

    String Source_Branch();

    String Target_Branch();

    String Files_In_The_Branch();

    String Files_To_Promote();

    String Promote_All();

    String Promote_Selected();

    String Release_Project();

    String Release_Configuration();

    String AssetManagementLog();

    String UnexpectedError();

    String ProcessName();

    String Select_Project();

    String ApproveOperation();

    String Requestor();

    String Operation();

    String Repository();

    String IsApproved();

    String RequiresRework();

    String Release();

    String FieldMandatory0(final String fieldName);

    String SnapshotNotAvailableForRelease(final String version);

    String Configure();

    String Promote();

    //create repository wizard

    String NewRepository();

    String RepoName();

    String RepoNameHolder();

    String OrganizationalUnit();

    String RepositoryType();

    String ManagedRepository();

    String ManagedRepositoryHelpInline();

    String RepositoryInfoPage();

    String RepositoryStructurePage();

    String StructureTypeDescription();

    String RepoInformation();

    String ProjectGroup();

    String ProjectGroupHolder();

    String ProjectArtifact();

    String ProjectArtifactHolder();

    String ProjectVersion();

    String ProjectVersionHolder();

    String ProjectSettings();

    String ProjectName();

    String ProjectNameHolder();

    String ProjectDescription();

    String ProjectDescriptionHolder();

    String SingleModule();

    String SingleModuleHelpInline();

    String MultiModule();

    String MultiModuleHelpInline();

    String ProjectBranches();

    String ConfigureBranches();

    String IsRequired();

    String ValidatingProjectGAV();

    String CreatingRepository();

    String RepoCreationSuccess();

    String InitializingRepository();

    String RepoInitializationSuccess();

    String RepoInitializationFail();

    String RepoConfigurationStarted();

    String RepoConfigurationStartFailed();

    String InvalidRepositoryName();

    String InvalidProjectName();

    String InvalidGroupId();

    String InvalidArtifactId();

    String InvalidVersion();

    //end of create repository wizard.
}
