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
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.structure.server.organizationalunit.OrganizationalUnitFactory;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

public class OrganizationalUnitFactoryImpl implements OrganizationalUnitFactory {

    private RepositoryService repositoryService;

    private BackwardCompatibleUtil backward;

    private SpacesAPI spacesAPI;

    private ConfigurationService configurationService;

    private ConfigurationFactory configurationFactory;

    @Inject
    public OrganizationalUnitFactoryImpl(final RepositoryService repositoryService,
                                         final BackwardCompatibleUtil backward,
                                         final SpacesAPI spacesAPI,
                                         final ConfigurationService configurationService,
                                         final ConfigurationFactory configurationFactory) {
        this.repositoryService = repositoryService;
        this.backward = backward;
        this.spacesAPI = spacesAPI;
        this.configurationService = configurationService;
        this.configurationFactory = configurationFactory;
    }

    @Override
    public OrganizationalUnit newOrganizationalUnit(ConfigGroup groupConfig) {

        OrganizationalUnitImpl organizationalUnit = new OrganizationalUnitImpl(groupConfig.getName(),
                                                                               groupConfig.getConfigItemValue("defaultGroupId"));

        ConfigItem<List<String>> repositories = groupConfig.getConfigItem("repositories");
        if (repositories != null) {
            for (String alias : repositories.getValue()) {
                Space space = spacesAPI.getSpace(organizationalUnit.getName());
                final Repository repo = repositoryService.getRepositoryFromSpace(space,
                                                                                 alias);
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

        fillOrganizationalUnitContributors(groupConfig, organizationalUnit);

        return organizationalUnit;
    }

    private void fillOrganizationalUnitContributors(final ConfigGroup configGroup,
                                                    final OrganizationalUnit organizationalUnit) {
        boolean shouldUpdateConfigGroup = false;

        final String oldOwner = configGroup.getConfigItemValue("owner");
        if (oldOwner != null) {
            shouldUpdateConfigGroup = true;
            organizationalUnit.getContributors().add(new Contributor(oldOwner, ContributorType.OWNER));
            configGroup.removeConfigItem("owner");
        }

        ConfigItem<List<String>> oldContributors = configGroup.getConfigItem("contributors");
        if (oldContributors != null) {
            shouldUpdateConfigGroup = true;

            for (String userName : oldContributors.getValue()) {
                if (!userName.equals(oldOwner)) {
                    organizationalUnit.getContributors().add(new Contributor(userName, ContributorType.CONTRIBUTOR));
                }
            }

            configGroup.removeConfigItem("contributors");
        }

        if (!shouldUpdateConfigGroup) {
            ConfigItem<List<Contributor>> newContributorsConfigItem = configGroup.getConfigItem("space-contributors");
            newContributorsConfigItem.getValue().forEach(c -> organizationalUnit.getContributors().add(c));
        } else {
            configGroup.setConfigItem(configurationFactory.newConfigItem("space-contributors", organizationalUnit.getContributors()));
            configurationService.updateConfiguration(configGroup);
        }
    }
}
