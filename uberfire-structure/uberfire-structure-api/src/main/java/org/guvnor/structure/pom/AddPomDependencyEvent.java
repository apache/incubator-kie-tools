/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.structure.pom;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/***
 * Event to add dependency type to a pom in a project
 */
@Portable
public class AddPomDependencyEvent {

    private Path projectPath;
    private DependencyType type;

    public AddPomDependencyEvent(@MapsTo("dependencyType") final DependencyType type,
                                 @MapsTo("projectPath") final Path projectPath) {
        this.type = checkNotNull("dependencyType",
                                 type);
        this.projectPath = checkNotNull("projectPath",
                                        projectPath);
    }

    public Path getProjectPath() {
        return projectPath;
    }

    public DependencyType getDependencyType() {
        return type;
    }
}
