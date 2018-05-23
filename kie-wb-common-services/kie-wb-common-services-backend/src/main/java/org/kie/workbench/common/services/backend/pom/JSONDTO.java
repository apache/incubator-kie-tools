/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.pom;

import java.util.List;
import java.util.Objects;

import org.apache.maven.model.Dependency;

public class JSONDTO {

    private List<Dependency> dependencies;
    private List<RepositoryKey> repositories;
    private List<RepositoryKey> pluginRepositories;

    public JSONDTO(List<Dependency> dependencies, List<RepositoryKey> respositories, List<RepositoryKey> pluginRepositories) {
        this.dependencies = dependencies;
        this.repositories = respositories;
        this.pluginRepositories = pluginRepositories;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public List<RepositoryKey> getRepositories() {
        return repositories;
    }

    public List<RepositoryKey> getPluginRepositories() {
        return pluginRepositories;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JSONDTO{");
        sb.append("dependencies=").append(dependencies);
        sb.append(", repositories=").append(repositories);
        sb.append(", pluginRepositories=").append(pluginRepositories);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof JSONDTO)) {
            return false;
        }
        JSONDTO jsondto = (JSONDTO) o;
        return Objects.equals(dependencies, jsondto.dependencies) &&
                Objects.equals(repositories, jsondto.repositories) &&
                Objects.equals(pluginRepositories, jsondto.pluginRepositories);
    }

    @Override
    public int hashCode() {

        return Objects.hash(dependencies, repositories, pluginRepositories);
    }
}
