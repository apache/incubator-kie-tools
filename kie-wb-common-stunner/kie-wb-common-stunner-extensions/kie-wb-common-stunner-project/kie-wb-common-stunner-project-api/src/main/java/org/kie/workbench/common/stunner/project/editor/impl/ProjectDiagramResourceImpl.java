/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.project.editor.impl;

import java.util.Objects;
import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.editor.ProjectDiagramResource;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.kie.workbench.common.stunner.project.editor.ProjectDiagramResource.Type.PROJECT_DIAGRAM;
import static org.kie.workbench.common.stunner.project.editor.ProjectDiagramResource.Type.XML_DIAGRAM;

@Portable
public class ProjectDiagramResourceImpl implements ProjectDiagramResource {

    private ProjectDiagram projectDiagram = null;

    private String xmlDiagram = "";

    private Type type = XML_DIAGRAM;

    public ProjectDiagramResourceImpl(final @MapsTo("projectDiagram") ProjectDiagram projectDiagram,
                                      final @MapsTo("xmlDiagram") String xmlDiagram,
                                      final @MapsTo("type") Type type) {
        checkNotNull("type", type);

        this.projectDiagram = projectDiagram;
        this.xmlDiagram = xmlDiagram;
        this.type = type;
    }

    public ProjectDiagramResourceImpl(final ProjectDiagram projectDiagram) {
        this(projectDiagram, null, PROJECT_DIAGRAM);
    }

    public ProjectDiagramResourceImpl(final String xmlDiagram) {
        this(null, xmlDiagram, XML_DIAGRAM);
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ProjectDiagramResourceImpl that = (ProjectDiagramResourceImpl) o;

        if (projectDiagram != null ? !projectDiagram.equals(that.projectDiagram) : that.projectDiagram != null) {
            return false;
        }
        if (xmlDiagram != null ? !xmlDiagram.equals(that.xmlDiagram) : that.xmlDiagram != null) {
            return false;
        }

        return type == that.type;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(projectDiagram),
                                         Objects.hashCode(xmlDiagram),
                                         Objects.hashCode(type));
    }

    @Override
    public Optional<ProjectDiagram> projectDiagram() {
        return Optional.ofNullable(projectDiagram);
    }

    @Override
    public Optional<String> xmlDiagram() {
        return Optional.ofNullable(xmlDiagram);
    }

    @Override
    public Type getType() {
        return type;
    }
}
