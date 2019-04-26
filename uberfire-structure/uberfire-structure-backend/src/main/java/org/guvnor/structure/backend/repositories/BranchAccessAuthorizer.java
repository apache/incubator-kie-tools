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

package org.guvnor.structure.backend.repositories;

import java.util.Optional;
import javax.inject.Inject;

import org.eclipse.jgit.transport.ReceiveCommand;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.config.BranchPermissions;
import org.guvnor.structure.organizationalunit.config.RolePermissions;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.repositories.Repository;

public class BranchAccessAuthorizer {

    public enum AccessType {
        READ, WRITE, DELETE;

        public static AccessType valueOf(final ReceiveCommand.Type commandType) {
            if (commandType == null) {
                return null;
            } else if (commandType.equals(ReceiveCommand.Type.DELETE)) {
                return DELETE;
            } else {
                return WRITE;
            }
        }
    }

    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    private ConfiguredRepositories configuredRepositories;

    private OrganizationalUnitService organizationalUnitService;

    public BranchAccessAuthorizer() {
    }

    @Inject
    public BranchAccessAuthorizer(final SpaceConfigStorageRegistry spaceConfigStorageRegistry,
                                  final ConfiguredRepositories configuredRepositories,
                                  final OrganizationalUnitService organizationalUnitService) {
        this.spaceConfigStorageRegistry = spaceConfigStorageRegistry;
        this.configuredRepositories = configuredRepositories;
        this.organizationalUnitService = organizationalUnitService;
    }

    public boolean authorize(final String user,
                             final String spaceName,
                             final String repositoryIdentifier,
                             final String repositoryAlias,
                             final String branchName,
                             final AccessType accessType) {
        final BranchPermissions branchPermissions = spaceConfigStorageRegistry.get(spaceName).loadBranchPermissions(branchName,
                                                                                                                    repositoryIdentifier);
        final OrganizationalUnit organizationalUnit = organizationalUnitService.getOrganizationalUnit(spaceName);
        final Repository repository = configuredRepositories.getRepositoryByRepositoryAlias(organizationalUnit.getSpace(),
                                                                                            repositoryAlias);
        final Optional<Contributor> userContributor = repository.getContributors().stream().filter(c -> c.getUsername().equals(user)).findFirst();

        if (userContributor.isPresent()) {
            final String userRole = userContributor.get().getType().name();
            final RolePermissions rolePermissions = branchPermissions.getPermissionsByRole().get(userRole);

            if (AccessType.READ.equals(accessType)) {
                return rolePermissions.canRead();
            } else if (AccessType.WRITE.equals(accessType)) {
                return rolePermissions.canWrite();
            } else if (AccessType.DELETE.equals(accessType)) {
                return rolePermissions.canDelete();
            }
        }

        return true;
    }
}
