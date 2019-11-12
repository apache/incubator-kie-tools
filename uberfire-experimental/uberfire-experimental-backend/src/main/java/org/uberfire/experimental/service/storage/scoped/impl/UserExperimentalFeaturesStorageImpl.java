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
import org.uberfire.experimental.service.registry.impl.ExperimentalFeatureImpl;
import org.uberfire.experimental.service.storage.scoped.ExperimentalStorageScope;
import org.uberfire.experimental.service.storage.util.ExperimentalConstants;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.rpc.SessionInfo;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Collection;

@Dependent
public class UserExperimentalFeaturesStorageImpl extends AbstractScopedExperimentalFeaturesStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserExperimentalFeaturesStorageImpl.class);

    public static final String USER_FOLDER_ROOT = ExperimentalConstants.EXPERIMENTAL_ROOT_FOLDER + "/users";
    public static final String USER_FOLDER = USER_FOLDER_ROOT + "/{0}/" + ExperimentalConstants.EXPERIMENTAL_FILENAME;

    @Inject
    public UserExperimentalFeaturesStorageImpl(final SessionInfo sessionInfo, @Named("configIO") final IOService ioService, final ExperimentalFeatureDefRegistry defRegistry) {
        super(sessionInfo, ioService, defRegistry);
    }

    @Override
    public void init(FileSystem fileSystem) {
        super.init(fileSystem);

        checkStoragePath();
    }

    private void checkStoragePath() {
        Path path = fileSystem.getPath(USER_FOLDER_ROOT);
        if (!ioService.exists(path)) {
            ioService.createDirectory(path);
        }
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
        return MessageFormat.format(USER_FOLDER, getUserId());
    }

    private String getUserId() {
        return encode(sessionInfo.getIdentity().getIdentifier());
    }

    @Override
    protected Logger log() {
        return LOGGER;
    }

    @Override
    public ExperimentalStorageScope getScope() {
        return ExperimentalStorageScope.USER;
    }

    public static String encode(String folderName) {
        return new String(Base64.getEncoder().encode(folderName.getBytes()));
    }
}
