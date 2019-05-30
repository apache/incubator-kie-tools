/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.integration.service;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.uberfire.backend.vfs.Path;

@Portable
public class MigrateRequest {

    public enum Type {
        STUNNER_TO_JBPM_DESIGNER,
        JBPM_DESIGNER_TO_STUNNER
    }

    private Type type;
    private Path path;
    private String newName;
    private String newExtension;
    private String commitMessage;
    private ProjectDiagram projectDiagram;

    private MigrateRequest(@MapsTo("type") final Type type,
                           @MapsTo("path") final Path path,
                           @MapsTo("newName") final String newName,
                           @MapsTo("newExtension") final String newExtension,
                           @MapsTo("commitMessage") final String commitMessage,
                           @MapsTo("projectDiagram") final ProjectDiagram projectDiagram) {
        this.type = type;
        this.path = path;
        this.newName = newName;
        this.newExtension = newExtension;
        this.commitMessage = commitMessage;
        this.projectDiagram = projectDiagram;
    }

    public Type getType() {
        return type;
    }

    public Path getPath() {
        return path;
    }

    public String getNewName() {
        return newName;
    }

    public String getNewExtension() {
        return newExtension;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public ProjectDiagram getProjectDiagram() {
        return projectDiagram;
    }

    public static MigrateRequest newFromStunnerToJBPMDesigner(final Path path,
                                                              final String newName,
                                                              final String newExtension,
                                                              final String commitMessage) {
        return new MigrateRequest(Type.STUNNER_TO_JBPM_DESIGNER, path, newName, newExtension, commitMessage, null);
    }

    public static MigrateRequest newFromJBPMDesignerToStunner(final Path path,
                                                              final String newName,
                                                              final String newExtension,
                                                              final String commitMessage,
                                                              final ProjectDiagram projectDiagram) {
        return new MigrateRequest(Type.JBPM_DESIGNER_TO_STUNNER, path, newName, newExtension, commitMessage, projectDiagram);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MigrateRequest request = (MigrateRequest) o;
        return type == request.type &&
                Objects.equals(path, request.path) &&
                Objects.equals(newName, request.newName) &&
                Objects.equals(newExtension, request.newExtension) &&
                Objects.equals(commitMessage, request.commitMessage) &&
                Objects.equals(projectDiagram, request.projectDiagram);
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(type),
                                         Objects.hashCode(path),
                                         Objects.hashCode(newName),
                                         Objects.hashCode(newExtension),
                                         Objects.hashCode(commitMessage),
                                         Objects.hashCode(projectDiagram));
    }
}
