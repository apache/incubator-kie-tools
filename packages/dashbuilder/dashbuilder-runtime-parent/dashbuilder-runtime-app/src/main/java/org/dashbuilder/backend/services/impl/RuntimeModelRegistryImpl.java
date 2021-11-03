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

package org.dashbuilder.backend.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dashbuilder.backend.RuntimeOptions;
import org.dashbuilder.shared.event.NewDataSetContentEvent;
import org.dashbuilder.shared.event.RemovedRuntimeModelEvent;
import org.dashbuilder.shared.event.UpdatedRuntimeModelEvent;
import org.dashbuilder.shared.model.DashbuilderRuntimeMode;
import org.dashbuilder.shared.model.RuntimeModel;
import org.dashbuilder.shared.service.ImportValidationService;
import org.dashbuilder.shared.service.RuntimeModelParser;
import org.dashbuilder.shared.service.RuntimeModelRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class RuntimeModelRegistryImpl implements RuntimeModelRegistry {

    Logger logger = LoggerFactory.getLogger(RuntimeModelRegistryImpl.class);

    Map<String, RuntimeModel> runtimeModels;

    DashbuilderRuntimeMode mode = DashbuilderRuntimeMode.SINGLE_IMPORT;

    @Inject
    RuntimeModelParser parser;

    @Inject
    Event<NewDataSetContentEvent> newDataSetContentEvent;

    @Inject
    ImportValidationService importValidationService;

    @Inject
    Event<UpdatedRuntimeModelEvent> runtimeModelUpdatedEvent;

    @Inject
    Event<RemovedRuntimeModelEvent> removedRuntimeModelEvent;

    @Inject
    RuntimeOptions options;

    @PostConstruct
    public void init() {
        runtimeModels = new HashMap<>();
    }

    @Override
    public Optional<RuntimeModel> single() {
        return runtimeModels.values().stream().findFirst();
    }

    @Override
    public Optional<RuntimeModel> get(String id) {
        if (mode == DashbuilderRuntimeMode.MULTIPLE_IMPORT) {
            return Optional.ofNullable(runtimeModels.get(id));
        }
        return single();
    }

    @Override
    public Optional<RuntimeModel> registerFile(String fileName) {
        // it could be possible to NOT STORE models
        if (fileName == null || fileName.trim().isEmpty()) {
            logger.error("Invalid file name: {}", fileName);
            throw new IllegalArgumentException("Invalid file name.");
        }

        var file = new File(fileName);
        if (!file.exists()) {
            logger.error("File does not exist: {}", fileName);
            throw new IllegalArgumentException("File does not exist");
        }

        if (!importValidationService.validate(fileName)) {
            logger.error("File does not have a valid structure: {}", fileName);
            throw new IllegalArgumentException("Not a valid file structure.");
        }

        try (var fis = new FileInputStream(fileName)) {
            var importId = FilenameUtils.getBaseName(file.getPath());
            return register(importId, fis);
        } catch (IOException e) {
            logger.error("Not able to load file {}", fileName, e);
            throw new IllegalArgumentException("Error loading import file: " + fileName, e);
        }
    }

    @Override
    public void setMode(DashbuilderRuntimeMode mode) {
        this.mode = mode;
    }

    @Override
    public boolean isEmpty() {
        return runtimeModels.isEmpty();
    }

    @Override
    public DashbuilderRuntimeMode getMode() {
        return mode;
    }

    @Override
    public void remove(String modelId) {
        internalRemove(modelId, true);
    }

    public Optional<RuntimeModel> register(String id, InputStream fileStream) {
        if (!acceptingNewImports()) {
            throw new IllegalArgumentException("New imports are not allowed in mode " + mode);
        }
        try {
            if (id == null) {
                id = UUID.randomUUID().toString();
            }
            var runtimeModel = parser.parse(id, fileStream);
            runtimeModels.put(id, runtimeModel);

            if (options.isDevMode()) {
                runtimeModelUpdatedEvent.fire(new UpdatedRuntimeModelEvent(id));
            }

            return Optional.of(runtimeModel);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing import model.", e);
        }
    }

    @Override
    public Collection<String> availableModels() {
        return Collections.unmodifiableCollection(runtimeModels.keySet());
    }

    @Override
    public void clear() {
        availableModels().forEach(this::remove);
        runtimeModels.clear();
    }

    @Override
    public void unregister(String id) {
        internalRemove(id, false);
    }

    public void internalRemove(String modelId, boolean deleteFile) {
        if (runtimeModels.remove(modelId) != null) {
            removedRuntimeModelEvent.fire(new RemovedRuntimeModelEvent(modelId));
            if (options.isRemoveModelFile() && deleteFile) {
                removeModelFile(modelId);
            }
        }
    }

    private void removeModelFile(String modelId) {
        String modelPath = options.buildFilePath(modelId);
        FileUtils.deleteQuietly(new File(modelPath));
    }

    void setRemovedRuntimeModelEvent(Event<RemovedRuntimeModelEvent> removedRuntimeModelEvent) {
        this.removedRuntimeModelEvent = removedRuntimeModelEvent;
    }

}
