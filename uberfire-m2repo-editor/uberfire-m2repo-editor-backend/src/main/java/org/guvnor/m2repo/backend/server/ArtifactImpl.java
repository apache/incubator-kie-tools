/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.guvnor.m2repo.backend.server;

import java.io.File;
import java.util.Map;

import org.eclipse.aether.artifact.Artifact;

public class ArtifactImpl implements Artifact {

    private String groupId;
    private String artifactId;
    private String version;
    private String baseVersion;
    private boolean snapshot;
    private String classifier;
    private String extension;
    private File file;
    private Map<String, String> properties;

    public ArtifactImpl(final File file) {
        this.setFile(file);
    }

    @Override
    public String getGroupId() {
        return this.groupId;
    }

    @Override
    public String getArtifactId() {
        return this.artifactId;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public Artifact setVersion(final String version) {
        this.version = version;
        return this;
    }

    @Override
    public String getBaseVersion() {
        return this.baseVersion;
    }

    @Override
    public boolean isSnapshot() {
        return this.snapshot;
    }

    @Override
    public String getClassifier() {
        return this.classifier;
    }

    @Override
    public String getExtension() {
        return this.extension;
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public Artifact setFile(final File file) {
        this.file = file;
        return this;
    }

    @Override
    public String getProperty(final String s,
                              final String s1) {
        return this.properties.getOrDefault(s,
                                            s1);
    }

    @Override
    public Map<String, String> getProperties() {
        return this.properties;
    }

    @Override
    public Artifact setProperties(final Map<String, String> map) {
        this.properties = map;
        return this;
    }
}
