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

package org.guvnor.m2repo.preferences;

public class ArtifactRepositoryPreference  {

    private String globalM2RepoDir = "repositories/kie/global";

    private boolean globalM2RepoDirEnabled = true;

    private String workspaceM2RepoDir = "repositories/kie/workspaces";

    private boolean workspaceM2RepoDirEnabled = false;

    private boolean distributionManagementM2RepoDirEnabled = true;


    public String getGlobalM2RepoDir() {
        return globalM2RepoDir;
    }

    public void setGlobalM2RepoDir(final String globalM2RepoDir) {
        this.globalM2RepoDir = globalM2RepoDir.trim();
    }

    public String getWorkspaceM2RepoDir() {
        return workspaceM2RepoDir;
    }

    public void setWorkspaceM2RepoDir(String workspaceM2RepoDir) {
        this.workspaceM2RepoDir = workspaceM2RepoDir;
    }

    public boolean isGlobalM2RepoDirEnabled() {
        return globalM2RepoDirEnabled;
    }

    public void setGlobalM2RepoDirEnabled(boolean globalM2RepoDirEnabled) {
        this.globalM2RepoDirEnabled = globalM2RepoDirEnabled;
    }

    public boolean isWorkspaceM2RepoDirEnabled() {
        return workspaceM2RepoDirEnabled;
    }

    public void setWorkspaceM2RepoDirEnabled(boolean workspaceM2RepoDirEnabled) {
        this.workspaceM2RepoDirEnabled = workspaceM2RepoDirEnabled;
    }

    public boolean isDistributionManagementM2RepoDirEnabled() {
        return distributionManagementM2RepoDirEnabled;
    }

    public void setDistributionManagementM2RepoDirEnabled(boolean distributionManagementM2RepoDirEnabled) {
        this.distributionManagementM2RepoDirEnabled = distributionManagementM2RepoDirEnabled;
    }
}
