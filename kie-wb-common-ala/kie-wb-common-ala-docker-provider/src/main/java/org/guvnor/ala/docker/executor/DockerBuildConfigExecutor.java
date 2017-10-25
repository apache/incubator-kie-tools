/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.docker.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.guvnor.ala.build.maven.model.MavenBuild;
import org.guvnor.ala.config.BuildConfig;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.docker.config.DockerBuildConfig;
import org.guvnor.ala.docker.model.DockerBuildImpl;
import org.guvnor.ala.pipeline.BiFunctionConfigExecutor;

public class DockerBuildConfigExecutor implements BiFunctionConfigExecutor<MavenBuild, DockerBuildConfig, BuildConfig> {

    @Override
    public Optional<BuildConfig> apply(final MavenBuild buildConfig,
                                       final DockerBuildConfig dockerBuildConfig) {
        final List<String> goals = new ArrayList<>(buildConfig.getGoals());
        final Properties properties = new Properties(buildConfig.getProperties());
        if (dockerBuildConfig.push()) {
            properties.put("docker.username",
                           dockerBuildConfig.getUsername());
            properties.put("docker.password",
                           dockerBuildConfig.getPassword());
        }
        goals.add("docker:build");
        if (dockerBuildConfig.push()) {
            goals.add("docker:push");
        }
        return Optional.of(new DockerBuildImpl(buildConfig.getProject(),
                                               goals,
                                               properties));
    }

    @Override
    public Class<? extends Config> executeFor() {
        return DockerBuildConfig.class;
    }

    @Override
    public String outputId() {
        return "maven-config";
    }

    @Override
    public String inputId() {
        return "maven-config";
    }
}
