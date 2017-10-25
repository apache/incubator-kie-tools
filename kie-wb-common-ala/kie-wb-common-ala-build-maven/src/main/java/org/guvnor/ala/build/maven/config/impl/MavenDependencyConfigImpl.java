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

package org.guvnor.ala.build.maven.config.impl;

import org.guvnor.ala.build.maven.config.MavenDependencyConfig;
import org.guvnor.ala.config.CloneableConfig;

public class MavenDependencyConfigImpl implements MavenDependencyConfig,
                                                  CloneableConfig<MavenDependencyConfig> {

    private String artifact;

    public MavenDependencyConfigImpl() {
        this.artifact = MavenDependencyConfig.super.getArtifact();
    }

    public MavenDependencyConfigImpl(final String artifact) {
        this.artifact = artifact;
    }

    @Override
    public String getArtifact() {
        return artifact;
    }

    @Override
    public MavenDependencyConfig asNewClone(final MavenDependencyConfig source) {
        return new MavenDependencyConfigImpl(source.getArtifact());
    }

    @Override
    public String toString() {
        return "MavenDependencyConfigImpl{" +
                "artifact='" + artifact + '\'' +
                '}';
    }
}