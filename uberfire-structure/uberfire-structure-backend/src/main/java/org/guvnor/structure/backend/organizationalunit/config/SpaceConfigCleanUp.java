/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.organizationalunit.config;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.organizationalunit.config.SpaceInfo;
import org.guvnor.structure.repositories.RepositoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.java.nio.file.api.FileSystemUtils;
import org.uberfire.java.nio.fs.jgit.FileSystemLock;
import org.uberfire.java.nio.fs.jgit.FileSystemLockManager;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;

@Startup
@ApplicationScoped
public class SpaceConfigCleanUp {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpaceConfigCleanUp.class);
    private static final String LOCK_NAME = ".config.lock";
    private static final String MARKER_NAME = ".config-cleanup.done";
    private static final String CLEAN_UP_MESSAGE = "Space config clean up.";
    private static final int LAST_ACCESS_THRESHOLD = 1;

    private OrganizationalUnitService orgUnitService;
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    public SpaceConfigCleanUp() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public SpaceConfigCleanUp(final OrganizationalUnitService orgUnitService,
                              final SpaceConfigStorageRegistry spaceConfigStorageRegistry) {
        this.orgUnitService = orgUnitService;
        this.spaceConfigStorageRegistry = spaceConfigStorageRegistry;
    }

    @PostConstruct
    public void postConstruct() {
        executeCleanUp();
    }

    private void executeCleanUp() {
        try {
            if (this.isGitDefaultFileSystem()) {
                orgUnitService.getAllOrganizationalUnits()
                        .stream()
                        .map(OrganizationalUnit::getName)
                        .forEach(this::cleanUpSpaceConfigStorage);
            }
        } catch (Exception e) {
            LOGGER.error("Error when executing clean up.", e);
        }
    }

    protected boolean isGitDefaultFileSystem() {
        return FileSystemUtils.isGitDefaultFileSystem();
    }

    private void cleanUpSpaceConfigStorage(final String spaceName) {
        final SpaceConfigStorageImpl configStorage = (SpaceConfigStorageImpl) spaceConfigStorageRegistry.get(spaceName);

        final JGitFileSystem fs = (JGitFileSystem) configStorage.getPath().getFileSystem();
        final File configDirectory = fs.getGit()
                .getRepository()
                .getDirectory()
                .getParentFile();

        final File marker = createMarker(configDirectory);

        if (marker.exists()) {
            return;
        }

        final FileSystemLock physicalLock = createLock(configDirectory);

        try {
            physicalLock.lock();

            final SpaceInfo spaceInfo = configStorage.loadSpaceInfo();

            final boolean updateNeeded = spaceInfo.getRepositories()
                    .stream()
                    .map(repositoryInfo -> repositoryInfo.getConfiguration().getEnvironment())
                    .filter(envMap -> !RepositoryUtils.cleanUpCredentialsFromEnvMap(envMap).isEmpty())
                    .count() != 0;

            if (updateNeeded) {
                configStorage.saveSpaceInfo(spaceInfo);
                fs.getGit().resetWithSquash(CLEAN_UP_MESSAGE);
            }

            if (!marker.createNewFile()) {
                LOGGER.warn("Cannot create marker file {}.", MARKER_NAME);
            }
        } catch (IOException e) {
            LOGGER.error("Error when cleaning up space config storage.", e);
        } finally {
            physicalLock.unlock();
        }
    }

    FileSystemLock createLock(final File directory) {
        return FileSystemLockManager.getInstance().getFileSystemLock(directory,
                                                                     LOCK_NAME,
                                                                     TimeUnit.SECONDS,
                                                                     LAST_ACCESS_THRESHOLD);
    }

    File createMarker(final File directory) {
        return new File(directory, MARKER_NAME);
    }
}
