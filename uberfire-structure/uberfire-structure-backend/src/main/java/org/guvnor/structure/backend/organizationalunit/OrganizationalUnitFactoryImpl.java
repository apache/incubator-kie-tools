/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.structure.backend.organizationalunit;

import java.util.Collection;
import java.util.List;
import javax.inject.Inject;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.config.RepositoryInfo;
import org.guvnor.structure.organizationalunit.config.SpaceInfo;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.organizationalunit.OrganizationalUnitFactory;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

public class OrganizationalUnitFactoryImpl implements OrganizationalUnitFactory {

    private RepositoryService repositoryService;

    private SpacesAPI spacesAPI;

    @Inject
    public OrganizationalUnitFactoryImpl(final RepositoryService repositoryService,
                                         final SpacesAPI spacesAPI) {
        this.repositoryService = repositoryService;
        this.spacesAPI = spacesAPI;
    }

    @Override
    public OrganizationalUnit newOrganizationalUnit(final SpaceInfo spaceInfo) {

        OrganizationalUnitImpl organizationalUnit = new OrganizationalUnitImpl(spaceInfo.getName(),
                                                                               spaceInfo.getDefaultGroupId());
        organizationalUnit.setDescription(spaceInfo.getDescription());

        final List<RepositoryInfo> repositories = spaceInfo.getRepositories();
        if (repositories != null) {
            for (RepositoryInfo alias : repositories) {
                Space space = spacesAPI.getSpace(organizationalUnit.getName());
                final Repository repo = repositoryService.getRepositoryFromSpace(space,
                                                                                 alias.getName());
                if (repo != null) {
                    organizationalUnit.getRepositories().add(repo);
                }
            }
        }

        final List<String> securityGroups = spaceInfo.getSecurityGroups();
        if (securityGroups != null) {
            organizationalUnit.getGroups().addAll(securityGroups);
        }

        final Collection<Contributor> contributors = spaceInfo.getContributors();
        if (contributors != null) {
            organizationalUnit.getContributors().addAll(contributors);
        }

        return organizationalUnit;
    }
}
