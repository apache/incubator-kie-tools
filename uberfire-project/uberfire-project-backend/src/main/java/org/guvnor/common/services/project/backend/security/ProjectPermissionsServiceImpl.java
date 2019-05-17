/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.backend.security;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.guvnor.common.services.project.security.ProjectPermissionsService;
import org.guvnor.structure.organizationalunit.config.BranchPermissions;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;

public class ProjectPermissionsServiceImpl implements ProjectPermissionsService {

    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    public ProjectPermissionsServiceImpl() {
    }

    @Inject
    public ProjectPermissionsServiceImpl(final SpaceConfigStorageRegistry spaceConfigStorageRegistry) {
        this.spaceConfigStorageRegistry = spaceConfigStorageRegistry;
    }

    @Override
    public BranchPermissions loadBranchPermissions(final String spaceName,
                                                   final String projectIdentifier,
                                                   final String branchName) {
        return spaceConfigStorageRegistry.get(spaceName).loadBranchPermissions(branchName, projectIdentifier);
    }

    @Override
    public Map<String, BranchPermissions> loadBranchPermissions(final String spaceName,
                                                                final String projectIdentifier,
                                                                final List<String> branches) {
        return branches.stream().collect(Collectors.toMap(Function.identity(), branch -> loadBranchPermissions(spaceName, projectIdentifier, branch)));
    }
}
