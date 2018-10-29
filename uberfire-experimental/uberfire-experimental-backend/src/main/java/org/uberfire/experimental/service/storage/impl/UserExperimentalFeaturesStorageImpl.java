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

import java.text.MessageFormat;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinition;
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.SpacesAPI;

@Dependent
@Named("user")
public class UserExperimentalFeaturesStorageImpl extends AbstractExperimentalFeaturesStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserExperimentalFeaturesStorageImpl.class);

    public static final String USER_FOLDER = "/experimental/users/{0}/.experimental";

    @Inject
    public UserExperimentalFeaturesStorageImpl(final SessionInfo sessionInfo, final SpacesAPI spaces, @Named("configIO") final IOService ioService, final ExperimentalFeatureDefRegistry defRegistry) {
        super(sessionInfo, spaces, ioService, defRegistry);
    }

    @PostConstruct
    public void init() {
        initializeFileSystem();
    }

    @Override
    protected Collection<ExperimentalFeatureDefinition> getSupportedDefinitions() {
        return defRegistry.getUserFeatures();
    }

    @Override
    public Collection<ExperimentalFeatureImpl> getFeatures() {
        return readFeatures();
    }

    @Override
    public String getStoragePath() {
        return MessageFormat.format(USER_FOLDER, sessionInfo.getIdentity().getIdentifier());
    }

    @Override
    protected Logger log() {
        return LOGGER;
    }
}
