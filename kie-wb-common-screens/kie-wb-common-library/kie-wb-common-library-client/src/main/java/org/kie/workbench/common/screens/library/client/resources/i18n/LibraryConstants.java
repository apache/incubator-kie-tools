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

package org.kie.workbench.common.screens.library.client.resources.i18n;

import org.jboss.errai.ui.shared.api.annotations.TranslationKey;

public class LibraryConstants {

    @TranslationKey(defaultValue = "Welcome")
    public static final String EmptyLibraryView_Welcome = "EmptyLibraryView.Welcome";

    @TranslationKey(defaultValue = "Creating project")
    public static final String NewProjectScreen_Saving = "NewProjectScreen.Saving";

    @TranslationKey(defaultValue = "Error while creating the project.")
    public static final String NewProjectScreen_Error = "NewProjectScreen.Error";

    @TranslationKey(defaultValue = "New project")
    public static final String NewProject = "NewProject";

    @TranslationKey(defaultValue = "Project successfully created!")
    public static final String ProjectCreated = "ProjectCreated";

    @TranslationKey(defaultValue = "Settings")
    public static final String Settings = "Settings";

    @TranslationKey(defaultValue = "Project successfully imported!")
    public static final String ProjectImportedSuccessfully = "ProjectImportedSuccessfully";

    @TranslationKey(defaultValue = "Error while importing the project.")
    public static final String ProjectImportError = "ProjectImportError";

    @TranslationKey(defaultValue = "Importing project")
    public static final String Importing = "Importing";

    @TranslationKey(defaultValue = "Loading assets")
    public static final String LoadingAssets = "LoadingAssets";

    @TranslationKey(defaultValue = "Last modified")
    public static final String LastModified = "LastModified";

    @TranslationKey(defaultValue = "Created")
    public static final String Created = "Created";

    @TranslationKey(defaultValue = "Default project")
    public static final String DefaultProject = "DefaultProject";

    @TranslationKey(defaultValue = "Quick setup")
    public static final String QuickSetup = "QuickSetup";

    @TranslationKey(defaultValue = "Advanced setup")
    public static final String AdvancedSetup = "AdvancedSetup";

    @TranslationKey(defaultValue = "Other projects")
    public static final String OtherProjects = "OtherProjects";

    @TranslationKey(defaultValue = "Project deleted.")
    public static final String ProjectDeleted = "ProjectDeleted";

    @TranslationKey(defaultValue = "A build is already running.")
    public static final String ABuildIsAlreadyRunning = "ABuildIsAlreadyRunning";

    @TranslationKey(defaultValue = "Filter By Name")
    public static final String FilterByName = "FilterByName";

    @TranslationKey(defaultValue = "Create {0}")
    public static final String CreateOrganizationalUnit = "CreateOrganizationalUnit";

    @TranslationKey(defaultValue = "Save")
    public static final String Save = "Save";

    @TranslationKey(defaultValue = "Cancel")
    public static final String Cancel = "Cancel";

    @TranslationKey(defaultValue = "Removing")
    public static final String Removing = "Removing";

    @TranslationKey(defaultValue = "Are you sure you want to remove the \"{0}\" {1}?")
    public static final String RemoveOrganizationalUnitWarningMessage = "RemoveOrganizationalUnitWarningMessage";

    @TranslationKey(defaultValue = "{0} removed successfully!")
    public static final String RemoveOrganizationalUnitSuccess = "RemoveOrganizationalUnitSuccess";

    @TranslationKey(defaultValue = "The field \"{0}\" should not be empty.")
    public static final String EmptyFieldValidation = "EmptyFieldValidation";

    @TranslationKey(defaultValue = "A {0} with the same name already exists.")
    public static final String DuplicatedOrganizationalUnitValidation = "DuplicatedOrganizationalUnitValidation";

    @TranslationKey(defaultValue = "The field \"{0}\" is invalid.")
    public static final String InvalidFieldValidation = "InvalidFieldValidation";

    @TranslationKey(defaultValue = "Saving")
    public static final String Saving = "Saving";

    @TranslationKey(defaultValue = "The {0} was saved successfully.")
    public static final String OrganizationalUnitSaveSuccess = "OrganizationalUnitSaveSuccess";

    @TranslationKey(defaultValue = "Name")
    public static final String Name = "Name";

    @TranslationKey(defaultValue = "Default Group ID")
    public static final String DefaultGroupId = "DefaultGroupId";

    @TranslationKey(defaultValue = "repositories")
    public static final String Repositories = "Repositories";

    @TranslationKey(defaultValue = "Team")
    public static final String OrganizationalUnitDefaultAliasInSingular = "OrganizationalUnitDefaultAliasInSingular";

    @TranslationKey(defaultValue = "Teams")
    public static final String OrganizationalUnitDefaultAliasInPlural = "OrganizationalUnitDefaultAliasInPlural";

    @TranslationKey(defaultValue = "Indexing has not finished")
    public static final String IndexingHasNotFinished = "IndexingHasNotFinished";

    @TranslationKey(defaultValue = "Please wait while the project content is being indexed")
    public static final String PleaseWaitWhileTheProjectContentIsBeingIndexed = "PleaseWaitWhileTheProjectContentIsBeingIndexed";

    @TranslationKey(defaultValue = "Empty search")
    public static final String EmptySearch = "EmptySearch";

    @TranslationKey(defaultValue = "No files where found with the given search criteria.")
    public static final String NoFilesWhereFoundWithTheGivenSearchCriteria = "NoFilesWhereFoundWithTheGivenSearchCriteria";

    @TranslationKey(defaultValue = "End of file list")
    public static final String EndOfFileList = "EndOfFileList";

    @TranslationKey(defaultValue = "No more files. Please, press previous.")
    public static final String NoMoreFilesPleasePressPrevious = "NoMoreFilesPleasePressPrevious";

}
