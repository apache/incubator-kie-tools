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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class ArtifactRepositoryService {

    public static final String LOCAL_M2_REPO_NAME = "local-m2-repo";
    public static final String GLOBAL_M2_REPO_NAME = "global-m2-repo";
    public static final String WORKSPACE_M2_REPO_NAME = "workspace-m2-repo";
    public static final String DISTRIBUTION_MANAGEMENT_REPO_NAME = "distribution-management-repo";
    public static final String ORG_GUVNOR_M2REPO_DIR_PROPERTY = "org.guvnor.m2repo.dir";
    public static final String GLOBAL_M2_REPO_URL = "org.appformer.m2repo.url";

    private List<ArtifactRepository> repositories;

    public ArtifactRepositoryService() {
    }

    @Inject
    public ArtifactRepositoryService(@Repository @Any Instance<ArtifactRepository> artifactRepositoryInstances) {
        this.repositories = new ArrayList<>();
        for (ArtifactRepository artifactRepository : artifactRepositoryInstances) {
            this.repositories.add(artifactRepository);
        }
    }

    public List<? extends ArtifactRepository> getRepositories() {
        return this.repositories.stream().filter(ArtifactRepository::isRepository).collect(Collectors.toList());
    }

    public List<? extends ArtifactRepository> getPomRepositories() {
        return this.repositories.stream().filter(ArtifactRepository::isPomRepository).collect(Collectors.toList());
    }
}
