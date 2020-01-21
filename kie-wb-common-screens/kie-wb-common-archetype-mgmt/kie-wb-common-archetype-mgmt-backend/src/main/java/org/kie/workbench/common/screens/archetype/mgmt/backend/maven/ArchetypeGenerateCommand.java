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

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.appformer.maven.integration.embedder.MavenProjectLoader;
import org.appformer.maven.integration.embedder.MavenRequest;
import org.guvnor.common.services.project.model.GAV;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class ArchetypeGenerateCommand extends AbstractMavenCommand {

    public static final List<String> GOALS = Collections.singletonList("archetype:generate");

    public static final String ARCHETYPE_GROUP_ID = "archetypeGroupId";
    public static final String ARCHETYPE_ARTIFACT_ID = "archetypeArtifactId";
    public static final String ARCHETYPE_VERSION = "archetypeVersion";
    public static final String TEMPLATE_GROUP_ID = "groupId";
    public static final String TEMPLATE_ARTIFACT_ID = "artifactId";
    public static final String TEMPLATE_VERSION = "version";
    public static final String ARCHETYPE_CATALOG = "archetypeCatalog";
    public static final String INTERNAL_CATALOG = "internal";

    private final GAV archetypeGAV;
    private final GAV templateGAV;

    public ArchetypeGenerateCommand(final String baseDirectory,
                                    final GAV archetypeGAV,
                                    final GAV templateGAV) {
        super(baseDirectory);

        this.archetypeGAV = checkNotNull("archetypeGAV", archetypeGAV);
        this.templateGAV = checkNotNull("templateGAV", templateGAV);
    }

    @Override
    public MavenRequest buildMavenRequest() {
        final MavenRequest mavenRequest = MavenProjectLoader.createMavenRequest(false);

        mavenRequest.setGoals(GOALS);
        mavenRequest.setInteractive(false);

        return mavenRequest;
    }

    @Override
    public Properties buildUserProperties() {
        final Properties properties = new Properties();

        properties.setProperty(ARCHETYPE_GROUP_ID, archetypeGAV.getGroupId());
        properties.setProperty(ARCHETYPE_ARTIFACT_ID, archetypeGAV.getArtifactId());
        properties.setProperty(ARCHETYPE_VERSION, archetypeGAV.getVersion());
        properties.setProperty(TEMPLATE_GROUP_ID, templateGAV.getGroupId());
        properties.setProperty(TEMPLATE_ARTIFACT_ID, templateGAV.getArtifactId());
        properties.setProperty(TEMPLATE_VERSION, templateGAV.getVersion());
        properties.setProperty(ARCHETYPE_CATALOG, INTERNAL_CATALOG);

        return properties;
    }
}
