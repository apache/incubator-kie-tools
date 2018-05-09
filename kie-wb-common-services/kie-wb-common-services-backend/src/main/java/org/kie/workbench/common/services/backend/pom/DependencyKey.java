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

import java.util.Objects;

import org.apache.maven.model.Dependency;
import org.kie.soup.commons.validation.PortablePreconditions;

public class DependencyKey {

    private Dependency dependency;

    public DependencyKey(Dependency dep) {
        PortablePreconditions.checkNotNull("DependencyKey", dep);
        this.dependency = dep;
    }

    public Dependency getDependency() {
        return dependency;
    }

    @Override
    public boolean equals(Object o) {
        PortablePreconditions.checkNotNull("DependencyKey", o);
        if (this == o) {
            return true;
        }
        if (!(o instanceof DependencyKey)) {
            return false;
        }
        DependencyKey that = (DependencyKey) o;
        return Objects.equals(getDependency().getGroupId(), that.getDependency().getGroupId())
                && Objects.equals(getDependency().getArtifactId(), that.getDependency().getArtifactId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependency.getGroupId(), dependency.getArtifactId());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DependencyKey{");
        sb.append("dependency=").append(dependency);
        sb.append('}');
        return sb.toString();
    }
}
