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

package org.guvnor.ala.build.maven.model.impl;

import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.model.MavenBinary;
import org.guvnor.ala.config.CloneableConfig;
import org.uberfire.java.nio.file.Path;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class MavenProjectBinaryImpl implements MavenBinary,
                                               CloneableConfig<MavenBinary> {

    private final Path path;

    private final Project sourceProject;

    private final String artifactId;

    private final String version;

    private final String groupId;

    public MavenProjectBinaryImpl(final Path path,
                                  final Project sourceProject,
                                  final String groupId,
                                  final String artifactId,
                                  final String version) {
        this.path = checkNotNull("path",
                                 path);
        this.sourceProject = checkNotNull("sourceProject",
                                          sourceProject);
        this.artifactId = checkNotNull("artifactId",
                                       artifactId);
        this.version = checkNotNull("version",
                                    version);
        this.groupId = checkNotNull("groupId",
                                    groupId);
    }

    @Override
    public Project getProject() {
        return sourceProject;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public String getName() {
        return sourceProject.getExpectedBinary();
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public MavenBinary asNewClone(final MavenBinary source) {
        return new MavenProjectBinaryImpl(path,
                                          source.getProject(),
                                          groupId,
                                          artifactId,
                                          version);
    }

    @Override
    public String toString() {
        return "MavenProjectBinaryImpl{" +
                "path=" + path +
                ", sourceProject=" + sourceProject +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", groupId='" + groupId + '\'' +
                '}';
    }
}