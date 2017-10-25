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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;

import org.apache.maven.project.MavenProject;
import org.appformer.maven.integration.embedder.MavenProjectLoader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.guvnor.ala.build.maven.config.MavenProjectConfig;
import org.guvnor.ala.build.maven.model.PlugIn;
import org.guvnor.ala.build.maven.model.impl.MavenProjectImpl;
import org.guvnor.ala.build.maven.util.RepositoryVisitor;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.config.ProjectConfig;
import org.guvnor.ala.pipeline.BiFunctionConfigExecutor;
import org.guvnor.ala.registry.SourceRegistry;
import org.guvnor.ala.source.Source;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

public class MavenProjectConfigExecutor implements BiFunctionConfigExecutor<Source, MavenProjectConfig, ProjectConfig> {

    private final SourceRegistry sourceRegistry;

    @Inject
    public MavenProjectConfigExecutor(final SourceRegistry sourceRegistry) {
        this.sourceRegistry = sourceRegistry;
    }

    @Override
    public Optional<ProjectConfig> apply(final Source source,
                                         final MavenProjectConfig mavenProjectConfig) {
        final Path projectRoot = source.getPath().resolve(mavenProjectConfig.getProjectDir());
        final InputStream pomStream = Files.newInputStream(projectRoot.resolve("pom.xml"));
        final MavenProject project = MavenProjectLoader.parseMavenPom(pomStream);

        final Collection<PlugIn> buildPlugins = extractPlugins(project);

        final String expectedBinary = project.getArtifact().getArtifactId() + "-" + project.getArtifact().getVersion() + "." + calculateExtension(project.getArtifact().getType());
        final String _tempDir = mavenProjectConfig.getProjectTempDir().trim();

        final RepositoryVisitor repositoryVisitor;
        if (_tempDir.isEmpty()) {
            repositoryVisitor = new RepositoryVisitor(projectRoot,
                                                      project.getName());
        } else {
            repositoryVisitor = new RepositoryVisitor(projectRoot,
                                                      _tempDir,
                                                      mavenProjectConfig.recreateTempDir());
        }
        final org.guvnor.ala.build.maven.model.MavenProject mavenProject = new MavenProjectImpl(project.getId(),
                                                                                                project.getArtifact().getType(),
                                                                                                project.getName(),
                                                                                                expectedBinary,
                                                                                                source.getPath(),
                                                                                                source.getPath().resolve(mavenProjectConfig.getProjectDir()),
                                                                                                source.getPath().resolve("target").resolve(expectedBinary).toAbsolutePath(),
                                                                                                repositoryVisitor.getRoot().getAbsolutePath(),
                                                                                                buildPlugins);

        sourceRegistry.registerProject(source,
                                       mavenProject);

        return Optional.of(mavenProject);
    }

    private Collection<PlugIn> extractPlugins(final MavenProject project) {
        final Collection<PlugIn> result = new ArrayList<>(project.getBuildPlugins().size());
        for (org.apache.maven.model.Plugin plugin : project.getBuildPlugins()) {
            final Map<String, Object> config = extractConfig(plugin.getConfiguration());
            result.add(new PlugIn() {
                @Override
                public String getId() {
                    return plugin.getKey();
                }

                @Override
                public Map<String, ?> getConfiguration() {
                    return config;
                }
            });
        }
        return result;
    }

    private Map<String, Object> extractConfig(final Object configuration) {
        if (configuration instanceof Xpp3Dom) {
            final Map<String, Object> result = new HashMap<>();
            extractConfig(result,
                          (Xpp3Dom) configuration);
            if (result.containsKey("configuration")) {
                if (result.get("configuration") != null) {
                    return (Map<String, Object>) result.get("configuration");
                } else {
                    return Collections.emptyMap();
                }
            }
        }
        return Collections.emptyMap();
    }

    private void extractConfig(final Map<String, Object> content,
                               final Xpp3Dom xmlData) {
        if (xmlData.getChildCount() > 0) {
            final Map<String, Object> config = new HashMap<>(xmlData.getChildCount());
            for (final Xpp3Dom child : xmlData.getChildren()) {
                extractConfig(config,
                              child);
            }
            content.put(xmlData.getName(),
                        config);
        } else {
            content.put(xmlData.getName(),
                        xmlData.getValue());
        }
    }

    @Override
    public Class<? extends Config> executeFor() {
        return MavenProjectConfig.class;
    }

    @Override
    public String outputId() {
        return "project";
    }

    @Override
    public String inputId() {
        return "maven-config";
    }

    private String calculateExtension(String artifactType) {
        return "kjar".equals(artifactType) ? "jar" : artifactType;
    }
}
