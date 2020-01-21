/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.archetype.mgmt.backend.maven;

import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.appformer.maven.integration.embedder.MavenProjectLoader;
import org.appformer.maven.integration.embedder.MavenRequest;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;

public class ExecuteGoalsCommand extends AbstractMavenCommand {

    public static final String POM_XML = "pom.xml";
    protected static final List<String> DEFAULT_GOALS = Arrays.asList("clean",
                                                                      "install");
    private final List<String> goals;

    public ExecuteGoalsCommand(final String baseDirectory) {
        this(baseDirectory,
             DEFAULT_GOALS);
    }

    public ExecuteGoalsCommand(final String baseDirectory,
                               final List<String> goals) {
        super(baseDirectory);

        this.goals = checkNotEmpty("goals", goals);
    }

    @Override
    public MavenRequest buildMavenRequest() {
        final String pomPath = baseDirectory + FileSystems.getDefault().getSeparator() + POM_XML;

        final MavenRequest mavenRequest = MavenProjectLoader.createMavenRequest(false);

        mavenRequest.setGoals(goals);
        mavenRequest.setPom(pomPath);

        return mavenRequest;
    }

    @Override
    public Properties buildUserProperties() {
        return new Properties();
    }
}
