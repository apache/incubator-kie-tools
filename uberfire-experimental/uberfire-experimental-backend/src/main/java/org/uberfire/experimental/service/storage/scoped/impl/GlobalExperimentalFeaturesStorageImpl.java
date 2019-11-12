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
import org.slf4j.LoggerFactory;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinition;
import org.uberfire.experimental.service.events.PortableExperimentalFeatureModifiedEvent;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;
import org.uberfire.experimental.service.storage.scoped.ExperimentalStorageScope;
import org.uberfire.experimental.service.storage.util.ExperimentalConstants;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.rpc.SessionInfo;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Dependent
public class GlobalExperimentalFeaturesStorageImpl extends AbstractScopedExperimentalFeaturesStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExperimentalFeaturesStorageImpl.class);

    public static final String GLOBAL_STORAGE_PATH = ExperimentalConstants.EXPERIMENTAL_ROOT_FOLDER + "/global/" + ExperimentalConstants.EXPERIMENTAL_FILENAME;

    private Event<PortableExperimentalFeatureModifiedEvent> event;

    private List<ExperimentalFeatureImpl> globalFeatures;

    @Inject
    public GlobalExperimentalFeaturesStorageImpl(final SessionInfo sessionInfo, @Named("configIO") final IOService ioService, final ExperimentalFeatureDefRegistry defRegistry, final Event<PortableExperimentalFeatureModifiedEvent> event) {
        super(sessionInfo, ioService, defRegistry);
        this.event = event;
    }

    @Override
    public void init(FileSystem fileSystem) {
        super.init(fileSystem);
        loadGlobalFeatures();
    }

    protected void loadGlobalFeatures() {
        globalFeatures = Collections.unmodifiableList(new ArrayList<>(readFeatures()));
    }

    @Override
    protected Collection<ExperimentalFeatureDefinition> getSupportedDefinitions() {
        return defRegistry.getGlobalFeatures();
    }

    @Override
    public Collection<ExperimentalFeatureImpl> getFeatures() {
        return globalFeatures;
    }

    @Override
    public String getStoragePath() {
        return GLOBAL_STORAGE_PATH;
    }

    @Override
    protected Logger log() {
        return LOGGER;
    }

    @Override
    protected void maybeNotifyFeatureUpdate(ExperimentalFeatureImpl feature) {
        event.fire(new PortableExperimentalFeatureModifiedEvent(feature));
    }

    @Override
    public ExperimentalStorageScope getScope() {
        return ExperimentalStorageScope.GLOBAL;
    }
}
