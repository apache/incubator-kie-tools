/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.project.diagram.impl;

import java.util.Objects;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.diagram.AbstractMetadata;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.uberfire.backend.vfs.Path;

@Portable
public class ProjectMetadataImpl extends AbstractMetadata implements ProjectMetadata {

    private String moduleName;
    private Package projectPkg;
    private Overview overview;
    private String projectType;
    private SVGGenerator diagramSVGGenerator;
    private Path diagramSVGPath;

    public ProjectMetadataImpl() {
    }

    private ProjectMetadataImpl(final @MapsTo("definitionSetId") String definitionSetId,
                                final @MapsTo("projectPkg") Package projectPkg,
                                final @MapsTo("overview") Overview overview,
                                final @MapsTo("moduleName") String moduleName,
                                final @MapsTo("projectType") String projectType,
                                final @MapsTo("diagramSVGGenerator") SVGGenerator diagramSVGGenerator,
                                final @MapsTo("diagramSVGPath") Path diagramSVGPath) {
        super(definitionSetId);
        this.moduleName = moduleName;
        this.projectPkg = projectPkg;
        this.overview = overview;
        this.projectType = projectType;
        this.diagramSVGGenerator = diagramSVGGenerator;
        this.diagramSVGPath = diagramSVGPath;
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }

    @Override
    public Package getProjectPackage() {
        return projectPkg;
    }

    @Override
    public Overview getOverview() {
        return overview;
    }

    @Override
    public String getProjectType() {
        return projectType;
    }

    @Override
    public SVGGenerator getDiagramSVGGenerator() {
        return diagramSVGGenerator;
    }

    @Override
    public void setDiagramSVGGenerator(SVGGenerator diagramSVGGenerator) {
        this.diagramSVGGenerator = diagramSVGGenerator;
    }

    @Override
    public Path getDiagramSVGPath() {
        return diagramSVGPath;
    }

    @Override
    public void setDiagramSVGPath(Path diagramSVGPath) {
        this.diagramSVGPath = diagramSVGPath;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(moduleName),
                                         Objects.hashCode(projectPkg),
                                         Objects.hashCode(overview),
                                         Objects.hashCode(projectType),
                                         Objects.hashCode(diagramSVGGenerator),
                                         Objects.hashCode(diagramSVGPath));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ProjectMetadataImpl) {
            ProjectMetadataImpl other = (ProjectMetadataImpl) o;
            return super.equals(other) &&
                    Objects.equals(moduleName, other.moduleName) &&
                    Objects.equals(projectPkg, other.projectPkg) &&
                    Objects.equals(overview, other.overview) &&
                    Objects.equals(projectType, other.projectType) &&
                    Objects.equals(diagramSVGGenerator, other.diagramSVGGenerator) &&
                    Objects.equals(diagramSVGPath, other.diagramSVGPath);
        }
        return false;
    }

    @Override
    public Class<? extends Metadata> getMetadataType() {
        return ProjectMetadata.class;
    }

    @NonPortable
    public static class ProjectMetadataBuilder {

        private String defSetId;
        private String title;
        private String pName;
        private Package pPkg;
        private Overview overview;
        private Path path;
        private String projectType;
        private SVGGenerator diagramSVGGenerator;
        private Path diagramSVGPath;

        public ProjectMetadataBuilder forDefinitionSetId(final String s) {
            this.defSetId = s;
            return this;
        }

        public ProjectMetadataBuilder forTitle(final String t) {
            this.title = t;
            return this;
        }

        public ProjectMetadataBuilder forModuleName(final String pName) {
            this.pName = pName;
            return this;
        }

        public ProjectMetadataBuilder forProjectPackage(final Package pPkg) {
            this.pPkg = pPkg;
            return this;
        }

        public ProjectMetadataBuilder forOverview(final Overview overview) {
            this.overview = overview;
            return this;
        }

        public ProjectMetadataBuilder forPath(final Path path) {
            this.path = path;
            return this;
        }

        public ProjectMetadataBuilder forProjectType(final String projectType) {
            this.projectType = projectType;
            return this;
        }

        public ProjectMetadataBuilder forDiagramSVGGenerator(SVGGenerator diagramSVGGenerator) {
            this.diagramSVGGenerator = diagramSVGGenerator;
            return this;
        }

        public ProjectMetadataBuilder forDiagramSVGPAth(final Path diagramSVGPath) {
            this.diagramSVGPath = diagramSVGPath;
            return this;
        }

        public ProjectMetadataImpl build() {
            final ProjectMetadataImpl result = new ProjectMetadataImpl(defSetId,
                                                                       pPkg,
                                                                       overview,
                                                                       pName,
                                                                       projectType,
                                                                       diagramSVGGenerator,
                                                                       diagramSVGPath);
            result.setPath(path);
            result.setRoot(pPkg.getModuleRootPath());
            result.setTitle(title);
            return result;
        }
    }
}
