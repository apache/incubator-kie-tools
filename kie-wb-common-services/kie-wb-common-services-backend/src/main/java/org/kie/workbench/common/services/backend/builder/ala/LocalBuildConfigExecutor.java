/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.builder.ala;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;

import org.guvnor.ala.config.BuildConfig;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.pipeline.BiFunctionConfigExecutor;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceChangeType;

/**
 * Executor for a LocalBuildConfig configuration.
 */
@ApplicationScoped
public class LocalBuildConfigExecutor
        implements BiFunctionConfigExecutor<LocalModule, LocalBuildConfig, BuildConfig> {

    public LocalBuildConfigExecutor() {
        //Empty constructor for Weld proxying
    }

    /**
     * This executor mainly translates the local project configuration and the build configuration parameters provided
     * by the pipeline input into an internal format convenient for building the project in the local build system.
     * @param localModule the internal representation of the project in the local build system.
     * @param localBuildConfig the build configuration.
     * @return the internal build configuration for building the project in the local build system.
     */
    @Override
    public Optional<BuildConfig> apply(LocalModule localModule, LocalBuildConfig localBuildConfig) {
        Optional<BuildConfig> result = Optional.empty();
        LocalBuildConfig.BuildType buildType = decodeBuildType(localBuildConfig.getBuildType());
        switch (buildType) {
            case FULL_BUILD:
                result = Optional.of(new LocalBuildConfigInternal(localModule.getModule()));
                break;
            case INCREMENTAL_ADD_RESOURCE:
            case INCREMENTAL_DELETE_RESOURCE:
            case INCREMENTAL_UPDATE_RESOURCE:
                result = Optional.of(new LocalBuildConfigInternal(localModule.getModule(), buildType,
                                                                  decodePath(localBuildConfig.getResource())));
                break;
            case INCREMENTAL_BATCH_CHANGES:
                result = Optional.of(new LocalBuildConfigInternal(localModule.getModule(),
                                                                  getResourceChanges(localBuildConfig.getResourceChanges())));
                break;
            case FULL_BUILD_AND_DEPLOY:
                result = Optional.of(new LocalBuildConfigInternal(localModule.getModule(),
                                                                  decodeDeploymentType(localBuildConfig.getDeploymentType()),
                                                                  decodeSuppressHandlers(localBuildConfig.getSuppressHandlers())));
        }
        return result;
    }

    @Override
    public Class<? extends Config> executeFor() {
        return LocalBuildConfig.class;
    }

    @Override
    public String outputId() {
        return "local-build";
    }

    private LocalBuildConfig.BuildType decodeBuildType(String value) {
        return LocalBuildConfig.BuildType.valueOf(value);
    }

    private Map<Path, Collection<ResourceChange>> getResourceChanges(Map<String, String> input) {
        Map<Path, Collection<ResourceChange>> resourceChanges = new HashMap<>();
        input.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(LocalBuildConfig.RESOURCE_CHANGE))
                .forEach(entry -> {
                    resourceChanges.put(decodePath(entry.getKey(), LocalBuildConfig.RESOURCE_CHANGE), decodeChanges(entry.getValue()));
                });
        return resourceChanges;
    }

    private Collection<ResourceChange> decodeChanges(String value) {
        return Arrays.stream(value.split(","))
                .map(s -> new ResourceChange() {
                    @Override
                    public ResourceChangeType getType() {
                        return ResourceChangeType.valueOf(s.trim());
                    }

                    @Override
                    public String getMessage() {
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    private Path decodePath(String uri) {
        return decodePath(uri, "");
    }

    private Path decodePath(String uri, String prefix) {
        final String name = uri.substring(uri.lastIndexOf('/') + 1, uri.length());
        final String decodedURI = uri.substring(prefix.length(), uri.length());
        return PathFactory.newPath(name, decodedURI);
    }

    private LocalBuildConfig.DeploymentType decodeDeploymentType(String value) {
        return LocalBuildConfig.DeploymentType.valueOf(value);
    }

    private boolean decodeSuppressHandlers(String suppressHandlers) {
        return Boolean.parseBoolean(suppressHandlers);
    }
}