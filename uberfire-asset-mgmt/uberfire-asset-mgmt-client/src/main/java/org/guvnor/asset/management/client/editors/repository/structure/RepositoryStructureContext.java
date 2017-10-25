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

package org.guvnor.asset.management.client.editors.repository.structure;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.repositories.Repository;

public class RepositoryStructureContext {

    private Repository currentRepository;
    private String currentBranch;
    private Project currentProject;

    public void reset(final Repository currentRepository,
                      final String currentBranch,
                      final Project currentProject) {
        this.currentRepository = currentRepository;
        this.currentBranch = currentBranch;
        this.currentProject = currentProject;
    }

    public boolean activeProjectChanged(final Project activeProject) {
        return activeProject != null && !activeProject.equals(this.currentProject);
    }

    public boolean repositoryOrBranchChanged(final Repository activeRepository,
                                             final String activeBranch) {
        return activeRepository != null
                && (!activeRepository.equals(this.currentRepository)
                || !activeBranch.equals(this.currentBranch));
    }
}
