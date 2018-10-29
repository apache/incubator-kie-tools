/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinition;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;
import org.uberfire.experimental.service.storage.ExperimentalFeaturesStorage;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.SpacesAPI;

public abstract class AbstractExperimentalFeaturesStorage implements ExperimentalFeaturesStorage {

    public static final String COMMENTS = "Updating experimental features registry";

    private static final String EXPERIMENTAL = "experimental";

    public static final String EXPERIMENTAL_FILE_NAME = "." + EXPERIMENTAL;

    public static final String SEPARATOR = "/";

    public static final String EXPERIMENTAL_STORAGE_FOLDER = SEPARATOR + EXPERIMENTAL;

    protected final SessionInfo sessionInfo;

    protected final SpacesAPI spaces;

    protected final IOService ioService;

    protected final ExperimentalFeatureDefRegistry defRegistry;

    protected FileSystem fileSystem;

    public AbstractExperimentalFeaturesStorage(SessionInfo sessionInfo, SpacesAPI spaces, IOService ioService, ExperimentalFeatureDefRegistry defRegistry) {
        this.sessionInfo = sessionInfo;
        this.spaces = spaces;
        this.ioService = ioService;
        this.defRegistry = defRegistry;
    }

    public abstract String getStoragePath();

    protected abstract Collection<ExperimentalFeatureDefinition> getSupportedDefinitions();

    protected abstract Logger log();

    protected List<ExperimentalFeatureImpl> readFeatures() {

        final Path fsPath = fileSystem.getPath(getStoragePath());

        final List<ExperimentalFeatureImpl> registeredFeatures = new ArrayList<>();

        boolean existsOnVFS = true;

        if (ioService.exists(fsPath)) {
            try (InputStream in = ioService.newInputStream(fsPath)) {
                Properties properties = new Properties();

                properties.load(in);

                properties.entrySet().stream()
                        .map(entry -> new ExperimentalFeatureImpl((String) entry.getKey(), Boolean.valueOf((String) entry.getValue())))
                        .forEach(registeredFeatures::add);
            } catch (Exception ex) {
                log().warn("Impossible to load registry", ex);
            }
        } else {
            existsOnVFS = false;
        }

        boolean requiresVFSSync = syncLoadedFeatures(registeredFeatures);

        if (!existsOnVFS || requiresVFSSync) {
            storeFeatures(registeredFeatures);
        }

        return registeredFeatures;
    }

    private boolean syncLoadedFeatures(final List<ExperimentalFeatureImpl> registeredFeatures) {
        final Collection<ExperimentalFeatureDefinition> expectedDefinitions = getSupportedDefinitions();

        List<String> registryFeatureIds = registeredFeatures.stream().map(ExperimentalFeatureImpl::getFeatureId).collect(Collectors.toList());

        List<String> expectedFeatureIds = expectedDefinitions.stream().map(ExperimentalFeatureDefinition::getId).collect(Collectors.toList());

        List<String> missingFeatures = expectedFeatureIds.stream()
                .filter(expectedFeatureId -> !registryFeatureIds.contains(expectedFeatureId))
                .collect(Collectors.toList());

        boolean requiresSync = false;

        if (!missingFeatures.isEmpty()) {
            requiresSync = true;
            missingFeatures.stream()
                    .forEach(expectedFeatureId -> registeredFeatures.add(new ExperimentalFeatureImpl(expectedFeatureId, false)));
        }

        List<ExperimentalFeatureImpl> extraFeatures = registryFeatureIds.stream()
                .filter(registeredFeatureId -> !expectedFeatureIds.contains(registeredFeatureId))
                .map(registryFeatureId -> registeredFeatures.stream().filter(experimentalFeature -> experimentalFeature.getFeatureId().equals(registryFeatureId)).findAny().orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!extraFeatures.isEmpty()) {
            requiresSync = true;
            registeredFeatures.removeAll(extraFeatures);
        }

        return requiresSync;
    }

    @Override
    public void storeFeatures(Collection<ExperimentalFeatureImpl> features) {

        doStoreFeatures(features, () -> {});
    }

    @Override
    public void store(final ExperimentalFeatureImpl feature) {
        List<ExperimentalFeatureImpl> registeredFeatures = (List<ExperimentalFeatureImpl>) getFeatures();

        Optional<ExperimentalFeatureImpl> optional = registeredFeatures.stream()
                .filter(registeredFeature -> registeredFeature.getFeatureId().equals(feature.getFeatureId()))
                .findAny();

        if (optional.isPresent()) {
            ExperimentalFeatureImpl registeredFeature = optional.get();

            registeredFeature.setEnabled(feature.isEnabled());

            doStoreFeatures(registeredFeatures, () -> this.maybeNotifyFeatureUpdate(feature));
        }
    }

    public void doStoreFeatures(Collection<ExperimentalFeatureImpl> features, Runnable callback) {
        final String path = getStoragePath();
        final Path fsPath = fileSystem.getPath(path);

        Properties properties = new Properties();

        features.stream()
                .filter(experimentalFeature -> defRegistry.getFeatureById(experimentalFeature.getFeatureId()) != null)
                .forEach(feature -> properties.put(feature.getFeatureId(), String.valueOf(feature.isEnabled())));

        try (OutputStream out = ioService.newOutputStream(fsPath)) {
            ioService.startBatch(fileSystem);
            properties.store(out, COMMENTS);

            if (callback != null) {
                callback.run();
            }
        } catch (Exception ex) {
            log().warn("Impossible to write experimental features registry on '{}': {}", path, ex);
        } finally {
            ioService.endBatch();
        }
    }

    protected void initializeFileSystem() {

        final URI fileSystemURI = spaces.resolveFileSystemURI(SpacesAPI.Scheme.GIT, SpacesAPI.DEFAULT_SPACE, "preferences");

        try {
            Map<String, Object> options = new HashMap<>();

            options.put("init", Boolean.TRUE);
            options.put("internal", Boolean.TRUE);

            fileSystem = ioService.newFileSystem(fileSystemURI, options);
        } catch (FileSystemAlreadyExistsException e) {
            fileSystem = ioService.getFileSystem(fileSystemURI);
        }
    }

    protected void maybeNotifyFeatureUpdate(ExperimentalFeatureImpl feature) {

    }
}
