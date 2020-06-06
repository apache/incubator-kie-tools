/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.project.cli.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.config.RepositoryConfiguration;
import org.guvnor.structure.organizationalunit.config.RepositoryInfo;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.organizationalunit.config.SpaceInfo;
import org.guvnor.structure.repositories.RepositoryUtils;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationService;

@Dependent
public class ConfigGroupToSpaceInfoConverter {

    private static final String PROPOSED_GROUP_ID_REGEX = "[^A-Za-z0-9_\\-.]";
    private static final String DEFAULT_GROUP_ID = "defaultGroupId";
    private static final String OWNER = "owner";
    private static final String CONTRIBUTORS = "contributors";
    private static final String SPACE_CONTRIBUTORS = "space-contributors";
    private static final String SECURITY_GROUPS = "security:groups";

    private ConfigurationService configurationService;
    private BackwardCompatibleUtil backwardCompatibleUtil;
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    @Inject
    public ConfigGroupToSpaceInfoConverter(final ConfigurationService configurationService,
                                           final BackwardCompatibleUtil backwardCompatibleUtil,
                                           final SpaceConfigStorageRegistry spaceConfigStorageRegistry) {
        this.configurationService = configurationService;
        this.backwardCompatibleUtil = backwardCompatibleUtil;
        this.spaceConfigStorageRegistry = spaceConfigStorageRegistry;
    }

    public SpaceInfo toSpaceInfo(ConfigGroup configGroup) {
        final String spaceName = extractName(configGroup);

        Optional<SpaceInfo> optional = Optional.ofNullable(spaceConfigStorageRegistry.get(spaceName).loadSpaceInfo());

        if (optional.isPresent()) {
            return optional.get();
        }

        final String defaultGroupId = extractDefaultGroupId(configGroup);
        final String description = extractDescription(configGroup);
        final Collection<Contributor> contributors = extractContributors(configGroup);
        final List<RepositoryInfo> repositories = extractRepositories(spaceName);
        final List<String> securityGroups = extractSecurityGroups(configGroup);

        return new SpaceInfo(spaceName,
                             description,
                             defaultGroupId,
                             contributors,
                             repositories,
                             securityGroups);
    }

    private String extractName(final ConfigGroup groupConfig) {
        return groupConfig.getName();
    }

    private String extractDescription(final ConfigGroup groupConfig) {
        return groupConfig.getDescription();
    }

    private String extractDefaultGroupId(final ConfigGroup groupConfig) {
        String defaultGroupId = groupConfig.getConfigItemValue(DEFAULT_GROUP_ID);

        if (defaultGroupId == null || defaultGroupId.trim().isEmpty()) {
            defaultGroupId = getSanitizedDefaultGroupId(extractName(groupConfig));
        }

        return defaultGroupId;
    }

    private String getSanitizedDefaultGroupId(final String proposedGroupId) {
        //Only [A-Za-z0-9_\-.] are valid so strip everything else out
        return proposedGroupId != null ? proposedGroupId.replaceAll(PROPOSED_GROUP_ID_REGEX,
                                                                    "") : proposedGroupId;
    }

    private Collection<Contributor> extractContributors(final ConfigGroup configGroup) {
        final List<Contributor> contributors = new ArrayList<>();
        boolean oldConfigGroup = false;

        final String oldOwner = configGroup.getConfigItemValue(OWNER);
        if (oldOwner != null) {
            oldConfigGroup = true;
            contributors.add(new Contributor(oldOwner,
                                             ContributorType.OWNER));
        }

        ConfigItem<List<String>> oldContributors = configGroup.getConfigItem(CONTRIBUTORS);
        if (oldContributors != null) {
            oldConfigGroup = true;

            for (String userName : oldContributors.getValue()) {
                if (!userName.equals(oldOwner)) {
                    contributors.add(new Contributor(userName,
                                                     ContributorType.CONTRIBUTOR));
                }
            }
        }

        if (!oldConfigGroup) {
            ConfigItem<List<Contributor>> newContributorsConfigItem = configGroup.getConfigItem(SPACE_CONTRIBUTORS);
            contributors.addAll(newContributorsConfigItem.getValue());
        }

        return contributors;
    }

    private List<RepositoryInfo> extractRepositories(final String spaceName) {
        List<ConfigGroup> repos = configurationService.getConfiguration(ConfigType.REPOSITORY,
                                                                        spaceName);

        return repos.stream().map(this::toRepositoryInfo)
                .collect(Collectors.toList());
    }

    private RepositoryInfo toRepositoryInfo(ConfigGroup configGroup) {
        final Map<String, Object> envMap = extractEnvMap(configGroup);

        RepositoryUtils.cleanUpCredentialsFromEnvMap(envMap);

        return new RepositoryInfo(configGroup.getName(),
                                  false,
                                  new RepositoryConfiguration(envMap));
    }

    private List<String> extractSecurityGroups(final ConfigGroup groupConfig) {
        ConfigItem<List<String>> securityGroups = backwardCompatibleUtil.compat(groupConfig).getConfigItem(SECURITY_GROUPS);
        return securityGroups.getValue();
    }

    public void cleanUpRepositories(final ConfigGroup configGroup) {
        final String spaceName = extractName(configGroup);

        configurationService.getConfiguration(ConfigType.REPOSITORY, spaceName)
                .forEach(cg -> {
                    final Map<String, Object> envMap = extractEnvMap(cg);

                    final List<String> keysRemoved = RepositoryUtils.cleanUpCredentialsFromEnvMap(envMap);
                    keysRemoved.forEach(cg::removeConfigItem);

                    configurationService.updateConfiguration(cg);
                });
    }

    private Map<String, Object> extractEnvMap(final ConfigGroup configGroup) {
        return backwardCompatibleUtil.compat(configGroup)
                .getItems()
                .stream()
                .collect(Collectors.toMap(ConfigItem::getName,
                                          ConfigItem::getValue));
    }
}
