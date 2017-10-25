/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.project.backend.server.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;

public class DependencyContentHandler {

    public org.guvnor.common.services.project.model.Dependency fromPomModelToClientModel(final Dependency from) {
        org.guvnor.common.services.project.model.Dependency dependency = new org.guvnor.common.services.project.model.Dependency();

        dependency.setArtifactId(from.getArtifactId());
        dependency.setGroupId(from.getGroupId());
        dependency.setVersion(from.getVersion());

        dependency.setScope(from.getScope());

        return dependency;
    }

    public List<org.guvnor.common.services.project.model.Dependency> fromPomModelToClientModel(List<Dependency> dependencies) {
        List<org.guvnor.common.services.project.model.Dependency> result = new ArrayList<org.guvnor.common.services.project.model.Dependency>();
        for (Dependency dependency : dependencies) {
            result.add(fromPomModelToClientModel(dependency));
        }
        return result;
    }
}
