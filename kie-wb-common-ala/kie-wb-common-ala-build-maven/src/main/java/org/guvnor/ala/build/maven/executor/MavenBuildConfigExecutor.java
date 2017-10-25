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
package org.guvnor.ala.build.maven.executor;

import java.util.Optional;

import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.config.MavenBuildConfig;
import org.guvnor.ala.build.maven.model.impl.MavenBuildImpl;
import org.guvnor.ala.config.BuildConfig;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.pipeline.BiFunctionConfigExecutor;

public class MavenBuildConfigExecutor implements BiFunctionConfigExecutor<Project, MavenBuildConfig, BuildConfig> {

    @Override
    public Optional<BuildConfig> apply(final Project project,
                                       final MavenBuildConfig mavenBuildConfig) {
        return Optional.of(new MavenBuildImpl(project,
                                              mavenBuildConfig.getGoals(),
                                              mavenBuildConfig.getProperties()));
    }

    @Override
    public Class<? extends Config> executeFor() {
        return MavenBuildConfig.class;
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
