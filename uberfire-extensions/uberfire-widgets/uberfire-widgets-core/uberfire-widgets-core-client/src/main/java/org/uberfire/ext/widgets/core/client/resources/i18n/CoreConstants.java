/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.widgets.core.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * Core Widgets I18N constants
 */
public interface CoreConstants
        extends
        Messages {

    CoreConstants INSTANCE = GWT.create( CoreConstants.class );

    String multipleMatchingActivitiesFound();

    String activityNotFound();

    String cancel();

    String Previous();

    String Next();

    String Finish();

    String Delete();

    String RepositoryViewUriLabel();

    String RepositoryViewRootLabel();

    String ConfirmDeleteRepository0( String repositoryAlias );

    String Downloading();

    String SelectFileToUpload();

    String UploadSuccess();

    String UploadFail();

    String Uploading();

    String DefaultEditor();

    String Download();

    String FileExplorer();

    String Navigator();

    String EmptyEntry();

    String MetaFileEditor();

    String CantLoadOrganizationalUnits();

    String SelectEntry();

    String URLMandatory();

    String InvalidUrlFormat();

    String OrganizationalUnitMandatory();

    String RepositoryNaneMandatory();

    String RepositoryNameInvalid();

    String DoYouAgree();

    String RepoCloneSuccess();

    String RepoAlreadyExists();

    String RepoCloneFail();

    String Cloning();

    String IndexClonedRepositoryWarning();

    String RepoInformation();

    String IsRequired();

    String RepoName();

    String RepoNameHolder();

    String OrganizationalUnit();

    String GitUrl();

    String GitUrlHolder();

    String UserName();

    String UserNameHolder();

    String Password();

    String PasswordHolder();

    String Cancel();

    String Clone();

    String RepoCreationSuccess();

    String RepoCreationFail();

    String Create();

    String RepositoryEditor();

    String AvailableProtocols();

    String Reverting();

    String GeneralInformation();

    String CommitHistory();

    String LoadMore();

    String TextEditor();

    String ConfirmStateRevert();

    String ActivityNotFound();

    String CloneRepository();

    String CreateRepository();

    String Empty();

    String RevertToThis();

    String textResourceTypeDescription();

    String Branches();

    String Update();
    
    String GroupId();
    
    String ArtifactId();
    
    String Version();
    
    String GroupIdHolder();
    
    String ArtifactIdHolder();
    
    String VersionHolder();
}
