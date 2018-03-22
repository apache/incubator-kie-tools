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

import org.guvnor.common.services.project.model.Package;
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

    public ProjectMetadataImpl() {
    }

    private ProjectMetadataImpl(final @MapsTo("definitionSetId") String definitionSetId,
                                final @MapsTo("projectPkg") Package projectPkg,
                                final @MapsTo("moduleName") String moduleName) {
        super(definitionSetId);
        this.moduleName = moduleName;
        this.projectPkg = projectPkg;
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
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         (null != moduleName) ? moduleName.hashCode() : 0,
                                         (null != projectPkg) ? projectPkg.hashCode() : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ProjectMetadataImpl) {
            ProjectMetadataImpl other = (ProjectMetadataImpl) o;
            return super.equals(other) &&
                    (null != moduleName) ? moduleName.equals(other.moduleName) : null == other.moduleName &&
                    (null != projectPkg) ? projectPkg.equals(other.projectPkg) : null == other.projectPkg;
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
        private Path path;

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

        public ProjectMetadataBuilder forPath(final Path path) {
            this.path = path;
            return this;
        }

        public ProjectMetadataImpl build() {
            final ProjectMetadataImpl result = new ProjectMetadataImpl(defSetId,
                                                                       pPkg,
                                                                       pName);
            result.setPath(path);
            result.setRoot(pPkg.getModuleRootPath());
            result.setTitle(title);
            return result;
        }
    }
}
