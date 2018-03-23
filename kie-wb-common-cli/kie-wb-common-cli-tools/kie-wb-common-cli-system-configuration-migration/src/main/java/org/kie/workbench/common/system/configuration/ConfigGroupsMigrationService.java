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

package org.kie.workbench.common.system.configuration;

import java.util.Iterator;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.backend.config.ConfigGroupMarshaller;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.migration.cli.SystemAccess;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Files;
import org.uberfire.workbench.type.FileNameUtil;

@ApplicationScoped
public class ConfigGroupsMigrationService {

    private Repository systemRepository;

    private ConfigGroupMarshaller marshaller;

    private IOService ioService;

    private FileSystem fs;

    private SystemAccess system;

    private User identity;

    public ConfigGroupsMigrationService() {
    }

    @Inject
    public ConfigGroupsMigrationService(final @Named("system") Repository systemRepository,
                                        final ConfigGroupMarshaller marshaller,
                                        final @Named("configIO") IOService ioService,
                                        final @Named("systemFS") FileSystem fs,
                                        final SystemAccess system,
                                        final User identity) {
        this.systemRepository = systemRepository;
        this.marshaller = marshaller;
        this.ioService = ioService;
        this.fs = fs;
        this.system = system;
        this.identity = identity;
    }

    public void groupSystemConfigGroups() {
        try {
            startBatch();
            system.out().println("Moving existing configurations to their type directories...");
            groupConfigGroupsByType();
            system.out().println("Moving existing repositories configurations to their space directories...");
            groupRepositoryConfigGroupsBySpace();
        } finally {
            endBatch();
        }
    }

    private void startBatch() {
        ioService.startBatch(ioService.get(systemRepository.getUri()).getFileSystem());
    }

    private void endBatch() {
        ioService.endBatch();
    }

    private void groupConfigGroupsByType() {
        final org.uberfire.java.nio.file.Path systemDir = ioService.get(systemRepository.getUri());
        for (ConfigType oldType : ConfigType.values()) {
            final String oldExt = oldType.getExt();
            final DirectoryStream<org.uberfire.java.nio.file.Path> foundConfigs = getDirectoryStreamForFilesWithParticularExtension(systemDir,
                                                                                                                                    oldExt);

            final ConfigType newType = getNewType(oldType);
            final org.uberfire.java.nio.file.Path newTypeDir = systemDir.resolve(newType.getDir());
            if (!ioService.exists(newTypeDir)) {
                ioService.createDirectory(newTypeDir);
            }

            final Iterator<org.uberfire.java.nio.file.Path> it = foundConfigs.iterator();
            while (it.hasNext()) {
                final org.uberfire.java.nio.file.Path oldPath = it.next();
                final String newExt = newType.getExt();
                final String oldFileName = Paths.convert(oldPath).getFileName();
                final String newFileName = FileNameUtil.removeExtension(oldFileName,
                                                                        oldExt.substring(1)) + newExt;
                final org.uberfire.java.nio.file.Path newPath = newTypeDir.resolve(newFileName);

                ioService.move(oldPath,
                               newPath);

                if (!newType.equals(oldType)) {
                    final String content = ioService.readAllString(newPath);
                    final ConfigGroup configGroup = marshaller.unmarshall(content);
                    configGroup.setType(newType);
                    ioService.write(newPath,
                                    marshaller.marshall(configGroup),
                                    new CommentedOption(getIdentityName(),
                                                        "Updated configuration type."));
                }
            }
        }
    }

    private ConfigType getNewType(final ConfigType oldType) {
        if (ConfigType.ORGANIZATIONAL_UNIT.equals(oldType)) {
            return ConfigType.SPACE;
        }

        return oldType;
    }

    private DirectoryStream<org.uberfire.java.nio.file.Path> getDirectoryStreamForFilesWithParticularExtension(final org.uberfire.java.nio.file.Path dir,
                                                                                                               final String extension) {
        return ioService.newDirectoryStream(dir,
                                            entry -> {
                                                if (!Files.isDirectory(entry) &&
                                                        !entry.getFileName().toString().startsWith(".") &&
                                                        entry.getFileName().toString().endsWith(extension)) {
                                                    return true;
                                                }
                                                return false;
                                            });
    }

    private void groupRepositoryConfigGroupsBySpace() {
        final org.uberfire.java.nio.file.Path systemDir = ioService.get(systemRepository.getUri());
        final org.uberfire.java.nio.file.Path repositoriesDir = systemDir.resolve(ConfigType.REPOSITORY.getDir());
        final DirectoryStream<org.uberfire.java.nio.file.Path> repoConfigs = getDirectoryStreamForFilesWithParticularExtension(repositoriesDir,
                                                                                                                               ConfigType.REPOSITORY.getExt());

        final Iterator<org.uberfire.java.nio.file.Path> it = repoConfigs.iterator();
        while (it.hasNext()) {
            final org.uberfire.java.nio.file.Path oldPath = it.next();
            final String fileName = Paths.convert(oldPath).getFileName();

            final String content = ioService.readAllString(oldPath);
            final ConfigGroup repoConfig = marshaller.unmarshall(content);
            final String space = repoConfig.getConfigItemValue("space");

            final org.uberfire.java.nio.file.Path newPath = repositoriesDir.resolve(space).resolve(fileName);

            ioService.move(oldPath,
                           newPath);
        }
    }

    protected String getIdentityName() {
        try {
            return identity.getIdentifier();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
