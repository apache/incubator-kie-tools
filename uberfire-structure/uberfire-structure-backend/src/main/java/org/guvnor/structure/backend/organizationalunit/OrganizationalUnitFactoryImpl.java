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

import java.util.List;
import javax.inject.Inject;

import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.organizationalunit.OrganizationalUnitFactory;

public class OrganizationalUnitFactoryImpl implements OrganizationalUnitFactory {

    private RepositoryService repositoryService;

    private BackwardCompatibleUtil backward;

    @Inject
    public OrganizationalUnitFactoryImpl(final RepositoryService repositoryService,
                                         final BackwardCompatibleUtil backward) {
        this.repositoryService = repositoryService;
        this.backward = backward;
    }

    @Override
    public OrganizationalUnit newOrganizationalUnit(ConfigGroup groupConfig) {

        OrganizationalUnitImpl organizationalUnit = new OrganizationalUnitImpl(groupConfig.getName(),
                                                                               groupConfig.getConfigItemValue("owner"),
                                                                               groupConfig.getConfigItemValue("defaultGroupId"));
        ConfigItem<List<String>> repositories = groupConfig.getConfigItem("repositories");
        if (repositories != null) {
            for (String alias : repositories.getValue()) {

                final Repository repo = repositoryService.getRepository(alias);
                if (repo != null) {
                    organizationalUnit.getRepositories().add(repo);
                }
            }
        }

        //Copy in Security Roles required to access this resource
        ConfigItem<List<String>> groups = backward.compat(groupConfig).getConfigItem("security:groups");
        if (groups != null) {
            for (String group : groups.getValue()) {
                organizationalUnit.getGroups().add(group);
            }
        }

        ConfigItem<List<String>> contributors = groupConfig.getConfigItem("contributors");
        if (contributors != null) {
            for (String userName : contributors.getValue()) {
                organizationalUnit.getContributors().add(userName);
            }
        }

        return organizationalUnit;
    }
}
