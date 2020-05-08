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

package org.uberfire.experimental.service.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinition;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;
import org.uberfire.experimental.service.storage.ExperimentalFeaturesStorage;
import org.uberfire.experimental.service.storage.migration.StorageMigrationService;
import org.uberfire.experimental.service.storage.scoped.ExperimentalStorageScope;
import org.uberfire.experimental.service.storage.scoped.ScopedExperimentalFeaturesStorage;
import org.uberfire.experimental.service.storage.util.ExperimentalConstants;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.spaces.SpacesAPI;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;

import static org.uberfire.experimental.service.storage.util.ExperimentalConstants.COMMENTS;

@Named("global")
public class ExperimentalFeaturesStorageImpl implements ExperimentalFeaturesStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentalFeaturesStorageImpl.class);

    private Properties settings;
    private FileSystem fileSystem;
    private Map<ExperimentalStorageScope, ScopedExperimentalFeaturesStorage> storages = new HashMap<>();

    private SpacesAPI spaces;
    private IOService ioService;
    private ExperimentalFeatureDefRegistry defRegistry;
    private Instance<ScopedExperimentalFeaturesStorage> instances;
    private StorageMigrationService migrationService;

    @Inject
    public ExperimentalFeaturesStorageImpl(final SpacesAPI spaces, @Named("configIO") final IOService ioService, final ExperimentalFeatureDefRegistry defRegistry, final Instance<ScopedExperimentalFeaturesStorage> instances, final StorageMigrationService migrationService) {
        this.spaces = spaces;
        this.ioService = ioService;
        this.defRegistry = defRegistry;
        this.instances = instances;
        this.migrationService = migrationService;
    }

    @PostConstruct
    public void init() {
        initializeFileSystem();

        for (ScopedExperimentalFeaturesStorage storage : instances) {
            storage.init(fileSystem);
            storages.put(storage.getScope(), storage);
        }

        readSettings();

        checkVersion();
    }

    private void readSettings() {
        Path settingsPath = fileSystem.getPath(ExperimentalConstants.EXPERIMENTAL_SETTINGS_PATH);
        if (ioService.exists(settingsPath)) {
            settings = new Properties();
            try (InputStream in = ioService.newInputStream(settingsPath)){
                settings.load(in);
            } catch (Exception e) {
                LOGGER.error("Couldn't read properties file");
            }
        } else {
            settings = new Properties();
        }
    }

    private void checkVersion() {
        int currentVersion = Integer.parseInt(settings.getOrDefault(ExperimentalConstants.EXPERIMENTAL_VERSION_KEY, "1").toString());

        if (currentVersion < ExperimentalConstants.EXPERIMENTAL_VERSION) {
            migrationService.migrate(ExperimentalConstants.EXPERIMENTAL_VERSION, fileSystem);

            settings.setProperty(ExperimentalConstants.EXPERIMENTAL_VERSION_KEY, ExperimentalConstants.EXPERIMENTAL_VERSION.toString());

            saveSettings();
        }
    }

    private void saveSettings() {
        Path settingsPath = fileSystem.getPath(ExperimentalConstants.EXPERIMENTAL_SETTINGS_PATH);

        try (OutputStream out = ioService.newOutputStream(settingsPath)) {
            ioService.startBatch(fileSystem);
            settings.store(out, COMMENTS);

        } catch (Exception ex) {
            LOGGER.warn("Impossible to write experimental features registry on '{}': {}", settingsPath, ex);
        } finally {
            ioService.endBatch();
        }
    }

    @Override
    public Collection<ExperimentalFeatureImpl> getFeatures() {
        List<ExperimentalFeatureImpl> features = new ArrayList<>();

        storages.values().forEach(storage -> features.addAll(storage.getFeatures()));

        return features;
    }

    @Override
    public void store(ExperimentalFeatureImpl experimentalFeature) {
        Optional<ExperimentalFeatureDefinition> optional = Optional.ofNullable(defRegistry.getFeatureById(experimentalFeature.getFeatureId()));
        optional.ifPresent(definition -> storages.get(ExperimentalStorageScope.getScope(definition)).store(experimentalFeature));
    }

    protected void initializeFileSystem() {
        final URI fileSystemURI = spaces.resolveFileSystemURI(SpacesAPI.Scheme.DEFAULT, SpacesAPI.DEFAULT_SPACE, "preferences");

        try {
            Map<String, Object> options = new HashMap<>();

            options.put("init", Boolean.TRUE);
            options.put("internal", Boolean.TRUE);

            fileSystem = ioService.newFileSystem(fileSystemURI, options);
        } catch (FileSystemAlreadyExistsException e) {
            fileSystem = ioService.getFileSystem(fileSystemURI);
        }
    }
}
