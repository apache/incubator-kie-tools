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

package org.uberfire.experimental.service.storage.scoped.impl;

import org.slf4j.Logger;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinition;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;
import org.uberfire.experimental.service.storage.scoped.ScopedExperimentalFeaturesStorage;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.rpc.SessionInfo;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static org.uberfire.experimental.service.storage.util.ExperimentalConstants.COMMENTS;

public abstract class AbstractScopedExperimentalFeaturesStorage implements ScopedExperimentalFeaturesStorage {

    protected final SessionInfo sessionInfo;
    protected final ExperimentalFeatureDefRegistry defRegistry;
    protected final IOService ioService;

    protected FileSystem fileSystem;

    public AbstractScopedExperimentalFeaturesStorage(final SessionInfo sessionInfo, final IOService ioService, final ExperimentalFeatureDefRegistry defRegistry) {
        this.sessionInfo = sessionInfo;
        this.ioService = ioService;
        this.defRegistry = defRegistry;
    }

    @Override
    public void init(final FileSystem fileSystem) {
        this.fileSystem = fileSystem;
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
                        .map(entry -> new ExperimentalFeatureImpl((String) entry.getKey(), Boolean.parseBoolean((String) entry.getValue())))
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


    protected void maybeNotifyFeatureUpdate(ExperimentalFeatureImpl feature) {

    }
}
