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

import java.net.URI;
import java.util.Optional;

import javax.inject.Inject;

import org.appformer.maven.integration.MavenRepository;
import org.eclipse.aether.artifact.Artifact;
import org.guvnor.ala.build.maven.config.MavenDependencyConfig;
import org.guvnor.ala.build.maven.model.MavenBinary;
import org.guvnor.ala.build.maven.model.impl.MavenBinaryImpl;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.registry.BuildRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.FileSystems;
import org.uberfire.java.nio.file.Path;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;

public class MavenDependencyConfigExecutor implements FunctionConfigExecutor<MavenDependencyConfig, MavenBinary> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MavenDependencyConfigExecutor.class);

    private final BuildRegistry buildRegistry;

    @Inject
    public MavenDependencyConfigExecutor(final BuildRegistry buildRegistry) {
        this.buildRegistry = buildRegistry;
    }

    @Override
    public Optional<MavenBinary> apply(final MavenDependencyConfig config) {
        final String artifactId = config.getArtifact();
        checkNotEmpty("artifact parameter is mandatory",
                      artifactId);
        LOGGER.debug("Resolving Artifact: {}",
                     artifactId);
        final Artifact artifact = resolveArtifact(artifactId);
        if (artifact == null) {
            throw new RuntimeException("Cannot resolve Maven artifact. Look at the previous logs for more information.");
        }
        final String absolutePath = artifact.getFile().getAbsolutePath();
        LOGGER.debug("Resolved Artifact path: {}",
                     absolutePath);
        final Path path = FileSystems.getFileSystem(URI.create("file://default")).getPath(absolutePath);
        final MavenBinary binary = new MavenBinaryImpl(path,
                                                       artifact.getArtifactId(),
                                                       artifact.getGroupId(),
                                                       artifact.getArtifactId(),
                                                       artifact.getVersion());
        buildRegistry.registerBinary(binary);
        return Optional.of(binary);
    }

    @Override
    public Class<? extends Config> executeFor() {
        return MavenDependencyConfig.class;
    }

    @Override
    public String outputId() {
        return "binary";
    }

    @Override
    public String inputId() {
        return "maven-dependency-config";
    }

    protected Artifact resolveArtifact(final String artifactId) {
        return MavenRepository.getMavenRepository().resolveArtifact(artifactId);
    }
}