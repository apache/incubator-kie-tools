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
 *
 */

package org.guvnor.m2repo.client.resources.i18n;

import org.jboss.errai.ui.shared.api.annotations.TranslationKey;

public class M2Constants {

    @TranslationKey(defaultValue = "Artifact Repository")
    public static final String ArtifactRepositoryPreference_Label = "ArtifactRepositoryPreference.Label";

    @TranslationKey(defaultValue = "Global M2 repository directory")
    public static final String ArtifactRepositoryPreference_GlobalM2RepoDir = "ArtifactRepositoryPreference.GlobalM2RepoDir";

    @TranslationKey(defaultValue = "Is Global M2 repository directory enabled?")
    public static final String ArtifactRepositoryPreference_GlobalM2RepoDirEnabled = "ArtifactRepositoryPreference.GlobalM2RepoDirEnabled";

    @TranslationKey(defaultValue = "Workspace M2 repository directory")
    public static final String ArtifactRepositoryPreference_WorkspaceM2RepoDir = "ArtifactRepositoryPreference.WorkspaceM2RepoDir";

    @TranslationKey(defaultValue = "Is Workspace M2 repository directory enabled?")
    public static final String ArtifactRepositoryPreference_WorkspaceM2RepoDirEnabled = "ArtifactRepositoryPreference.WorkspaceM2RepoDirEnabled";

    @TranslationKey(defaultValue = "Is Distribution Management repository enabled?")
    public static final String ArtifactRepositoryPreference_DistributionManagementM2RepoDirEnabled = "ArtifactRepositoryPreference.DistributionManagementM2RepoDirEnabled";
}
