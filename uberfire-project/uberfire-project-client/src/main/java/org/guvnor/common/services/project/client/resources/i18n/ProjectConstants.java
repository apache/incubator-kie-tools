/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.client.resources.i18n;

import com.google.gwt.i18n.client.Messages;

public interface ProjectConstants
        extends Messages {

    String ProjectModel();

    String SaveSuccessful(String fileName);

    String GroupID();

    String EnterAGroupID();

    String GroupIdExample();

    String MoreInfo();

    String GroupIdMoreInfo();

    String ProjectGeneralSettings();

    String ProjectName();

    String ProjectNamePlaceHolder();

    String ProjectDescription();

    String ProjectDescriptionPlaceHolder();

    String ArtifactID();

    String EnterAnArtifactID();

    String ArtifactIDExample();

    String Version();

    String ArtifactIDMoreInfo();

    String EnterAVersion();

    String VersionExample();

    String VersionMoreInfo();

    String GroupArtifactVersion();

    String ParentsGroupArtifactVersion();

    String invalidGroupId();

    String invalidArtifactId();

    String invalidVersion();

    String invalidName();

    String ConflictingRepositoriesTitle();

    String ConflictingRepositoriesGAVDescription(String groupId,
                                                 String artifactId,
                                                 String version);

    String ConflictingRepositoriesOverride();

    String RepositoryId();

    String RepositoryUrl();

    String RepositorySource();

    String RepositorySourceLocal();

    String RepositorySourceProject();

    String RepositorySourceSettings();

    String RepositorySourceDistributionManagement();

    String RepositorySourceUnknown();

    String ProjectsNode();

    String ProjectsHelp();

    String ProjectResource();

    String ProjectActionRead();

    String ProjectActionUpdate();

    String ProjectActionDelete();

    String ProjectActionCreate();

    String ProjectActionBuild();
}
