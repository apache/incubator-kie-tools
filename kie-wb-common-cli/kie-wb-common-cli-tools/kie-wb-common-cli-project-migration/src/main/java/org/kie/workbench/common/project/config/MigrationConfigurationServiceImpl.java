/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.project.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.backend.config.ConfigGroupMarshaller;
import org.guvnor.structure.backend.config.ConfigurationServiceImpl;
import org.guvnor.structure.backend.config.OrgUnit;
import org.guvnor.structure.backend.config.Repository;
import org.guvnor.structure.backend.config.watch.AsyncWatchServiceCallback;
import org.guvnor.structure.config.SystemRepositoryChangedEvent;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

@Migration
@ApplicationScoped
public class MigrationConfigurationServiceImpl extends ConfigurationServiceImpl
                                               implements ConfigurationService,
                                                          AsyncWatchServiceCallback {

    public MigrationConfigurationServiceImpl() {
    }

    @Inject
    public MigrationConfigurationServiceImpl(final @Named("system") org.guvnor.structure.repositories.Repository systemRepository,
                                             final ConfigGroupMarshaller marshaller,
                                             final User identity,
                                             final @Named("configIO") IOService ioService,
                                             final @Repository Event<SystemRepositoryChangedEvent> repoChangedEvent,
                                             final @OrgUnit Event<SystemRepositoryChangedEvent> orgUnitChangedEvent,
                                             final Event<SystemRepositoryChangedEvent> changedEvent,
                                             final @Named("systemFS") FileSystem fs) {
        super(systemRepository,
              marshaller,
              identity,
              ioService,
              repoChangedEvent,
              orgUnitChangedEvent,
              changedEvent,
              fs);
    }

    @Override
    public List<ConfigGroup> getConfiguration(ConfigType configType) {
        if (ConfigType.SPACE.equals(configType)) {
            configType = ConfigType.ORGANIZATIONAL_UNIT;
        }

        final ConfigType type = configType;

        final List<ConfigGroup> configGroups = new ArrayList<>();
        final DirectoryStream<Path> foundConfigs = ioService.newDirectoryStream(ioService.get(systemRepository.getUri()),
                                                                                entry -> {
                                                                                    if (!Files.isDirectory(entry) &&
                                                                                            !entry.getFileName().toString().startsWith(".") &&
                                                                                            entry.getFileName().toString().endsWith(type.getExt())) {
                                                                                        return true;
                                                                                    }
                                                                                    return false;
                                                                                }
        );

        //Only load and cache if a file was found!
        final Iterator<Path> it = foundConfigs.iterator();
        if (it.hasNext()) {
            while (it.hasNext()) {
                final String content = ioService.readAllString(it.next());
                final ConfigGroup configGroup = marshaller.unmarshall(content);
                configGroups.add(configGroup);
            }
            configGroupsByTypeWithoutNamespace.put(type,
                                                   configGroups);
        }
        return configGroups;
    }

    @Override
    public List<ConfigGroup> getConfiguration(ConfigType type,
                                              final String namespace) {
        if (ConfigType.SPACE.equals(type)) {
            type = ConfigType.ORGANIZATIONAL_UNIT;
        }

        if (!ConfigType.REPOSITORY.equals(type)) {
            return Collections.emptyList();
        }

        return getConfiguration(type).stream()
                .filter(repoConfig -> namespace.equals(repoConfig.getConfigItemValue("space")))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<ConfigGroup>> getConfigurationByNamespace(ConfigType type) {
        if (ConfigType.SPACE.equals(type)) {
            type = ConfigType.ORGANIZATIONAL_UNIT;
        }

        if (!ConfigType.REPOSITORY.equals(type)) {
            return Collections.emptyMap();
        }

        final Map<String, List<ConfigGroup>> repoConfigsBySpace = new HashMap<>();
        final List<ConfigGroup> repoConfigs = getConfiguration(type);
        for (ConfigGroup repoConfig : repoConfigs) {
            final String space = repoConfig.getConfigItemValue("space");

            if (space != null) {
                if (!repoConfigsBySpace.containsKey(space)) {
                    repoConfigsBySpace.put(space,
                                           new ArrayList<>());
                }

                repoConfigsBySpace.get(space).add(repoConfig);
            }
        }

        return repoConfigsBySpace;
    }

    @Override
    public boolean addConfiguration(final ConfigGroup configGroup) {
        if (ConfigType.SPACE.equals(configGroup.getType())) {
            configGroup.setType(ConfigType.ORGANIZATIONAL_UNIT);
        }

        String filename = configGroup.getName().replaceAll(INVALID_FILENAME_CHARS,
                                                           "_");

        final Path filePath = ioService.get(systemRepository.getUri()).resolve(filename + configGroup.getType().getExt());
        // avoid duplicated writes to not cause cyclic cluster sync
        if (ioService.exists(filePath)) {
            return true;
        }

        final CommentedOption commentedOption = new CommentedOption(getIdentityName(),
                                                                    "Created config " + filePath.getFileName());
        saveConfiguration(configGroup,
                          filePath,
                          commentedOption);

        configGroupsByTypeWithoutNamespace.remove(configGroup.getType());

        return true;
    }

    @Override
    public boolean updateConfiguration(final ConfigGroup configGroup) {
        if (ConfigType.SPACE.equals(configGroup.getType())) {
            configGroup.setType(ConfigType.ORGANIZATIONAL_UNIT);
        }

        String filename = configGroup.getName().replaceAll(INVALID_FILENAME_CHARS,
                                                           "_");

        final Path filePath = ioService.get(systemRepository.getUri()).resolve(filename + configGroup.getType().getExt());

        final CommentedOption commentedOption = new CommentedOption(getIdentityName(),
                                                                    "Updated config " + filePath.getFileName());
        saveConfiguration(configGroup,
                          filePath,
                          commentedOption);

        configGroupsByTypeWithoutNamespace.remove(configGroup.getType());

        return true;
    }

    @Override
    public boolean removeConfiguration(final ConfigGroup configGroup) {
        if (ConfigType.SPACE.equals(configGroup.getType())) {
            configGroup.setType(ConfigType.ORGANIZATIONAL_UNIT);
        }

        configGroupsByTypeWithoutNamespace.remove(configGroup.getType());

        String filename = configGroup.getName().replaceAll(INVALID_FILENAME_CHARS,
                                                           "_");

        final Path filePath = ioService.get(systemRepository.getUri()).resolve(filename + configGroup.getType().getExt());

        if (!ioService.exists(filePath)) {
            return true;
        }

        boolean result;
        try {
            ioService.startBatch(filePath.getFileSystem());
            result = ioService.deleteIfExists(filePath);
            if (result) {
                updateLastModified();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            ioService.endBatch();
        }

        return result;
    }

    private void saveConfiguration(final ConfigGroup configGroup,
                                   final Path filePath,
                                   final CommentedOption commentedOption) {
        try {
            ioService.startBatch(filePath.getFileSystem());
            ioService.write(filePath,
                            marshaller.marshall(configGroup),
                            commentedOption);

            updateLastModified();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            ioService.endBatch();
        }
    }
}
