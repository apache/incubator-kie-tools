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

package org.guvnor.m2repo.backend.server.repositories;

import java.io.File;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.guvnor.m2repo.preferences.ArtifactRepositoryPreference;
import org.uberfire.apache.commons.io.FilenameUtils;
import org.uberfire.backend.server.cdi.workspace.WorkspaceNameResolver;
import org.uberfire.backend.server.cdi.workspace.WorkspaceScoped;

@ApplicationScoped
public class ArtifactRepositoryProducer {

    private ArtifactRepositoryPreference preferences;
    private WorkspaceNameResolver workspaceNameResolver;

    public ArtifactRepositoryProducer() {
    }

    @Inject
    public ArtifactRepositoryProducer(ArtifactRepositoryPreference preferences,
                                      WorkspaceNameResolver resolver) {
        this.preferences = preferences;
        this.workspaceNameResolver = resolver;
    }

    @PostConstruct
    public void initialize() {
        this.preferences.load();
    }

    @Produces
    @Repository
    @ApplicationScoped
    public ArtifactRepository produceLocalRepository() {
        return new LocalArtifactRepository(ArtifactRepositoryService.LOCAL_M2_REPO_NAME);
    }

    @Produces
    @Repository
    @ApplicationScoped
    public ArtifactRepository produceGlobalRepository() {
        if (!this.preferences.isGlobalM2RepoDirEnabled()) {
            return new NullArtifactRepository();
        }
        return new FileSystemArtifactRepository(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME,
                                                this.getGlobalM2RepoDir());
    }

    @Produces
    @Repository
    @WorkspaceScoped
    public ArtifactRepository produceWorkspaceRepository() {
        if (!this.preferences.isWorkspaceM2RepoDirEnabled()) {
            return new NullArtifactRepository();
        }
        String repoDir = getWorkspaceRepoDir();
        return new FileSystemArtifactRepository(ArtifactRepositoryService.WORKSPACE_M2_REPO_NAME,
                                                repoDir);
    }

    @Produces
    @Repository
    @ApplicationScoped
    public ArtifactRepository produceDistributionManagementRepository() {
        if (!this.preferences.isDistributionManagementM2RepoDirEnabled()) {
            return new NullArtifactRepository();
        }
        return new DistributionManagementArtifactRepository(ArtifactRepositoryService.DISTRIBUTION_MANAGEMENT_REPO_NAME);
    }

    private String getGlobalM2RepoDir() {
        final String repoRoot = FilenameUtils.separatorsToSystem(preferences.getGlobalM2RepoDir());

        final String meReposDir = System.getProperty(ArtifactRepositoryService.ORG_GUVNOR_M2REPO_DIR_PROPERTY);

        String repoDir;
        if (meReposDir == null || meReposDir.trim().isEmpty()) {
            repoDir = repoRoot;
        } else {
            repoDir = meReposDir.trim();
        }
        return repoDir;
    }

    private String getWorkspaceRepoDir() {
        String workspace = this.getWorkspaceName();
        final String repoRoot = FilenameUtils.separatorsToSystem(preferences.getWorkspaceM2RepoDir());
        String repoDir;
        if (repoRoot == null || repoRoot.trim().isEmpty()) {
            repoDir = this.getGlobalM2RepoDir() + File.separator + "workspaces";
        } else {
            repoDir = repoRoot;
        }
        return repoDir + File.separator + workspace;
    }

    private String getWorkspaceName() {
        return workspaceNameResolver.getWorkspaceName();
    }
}
