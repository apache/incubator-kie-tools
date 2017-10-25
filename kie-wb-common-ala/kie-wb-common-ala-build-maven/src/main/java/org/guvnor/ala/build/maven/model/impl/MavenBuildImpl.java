/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

package org.guvnor.ala.build.maven.model.impl;

import java.util.List;
import java.util.Properties;

import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.model.MavenBuild;
import org.guvnor.ala.config.CloneableConfig;

/**
 * The Build services implementation using Maven Invoker
 */
public class MavenBuildImpl implements MavenBuild,
                                       CloneableConfig<MavenBuild> {

    public final Project project;
    public final List<String> goals;
    private final Properties properties;

    public MavenBuildImpl(final Project project,
                          final List<String> goals,
                          final Properties properties) {
        this.project = project;
        this.goals = goals;
        this.properties = properties;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public List<String> getGoals() {
        return goals;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public MavenBuild asNewClone(final MavenBuild source) {
        return new MavenBuildImpl(source.getProject(),
                                  source.getGoals(),
                                  source.getProperties());
    }
}