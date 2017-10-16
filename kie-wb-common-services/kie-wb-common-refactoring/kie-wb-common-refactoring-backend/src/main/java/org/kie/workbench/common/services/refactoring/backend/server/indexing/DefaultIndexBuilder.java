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
package org.kie.workbench.common.services.refactoring.backend.server.indexing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.refactoring.IndexElementsGenerator;
import org.kie.workbench.common.services.refactoring.KPropertyImpl;
import org.kie.workbench.common.services.refactoring.model.index.terms.PackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.ProjectNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.ProjectRootPathIndexTerm;
import org.uberfire.ext.metadata.model.KProperty;

public class DefaultIndexBuilder {

    protected final String fileName;
    protected final Project project;
    protected final Package pkg;
    protected String pkgName;

    private Set<IndexElementsGenerator> generators = new HashSet<IndexElementsGenerator>();

    public DefaultIndexBuilder(final String fileName,
                               final Project project,
                               final Package pkg) {
        this.fileName = PortablePreconditions.checkNotNull("fileName",
                                                           fileName);
        this.project = PortablePreconditions.checkNotNull("project",
                                                          project);
        this.pkg = PortablePreconditions.checkNotNull("pkg",
                                                      pkg);
    }

    public DefaultIndexBuilder addGenerator(final IndexElementsGenerator generator) {
        this.generators.add(PortablePreconditions.checkNotNull("generator",
                                                               generator));
        return this;
    }

    public Set<KProperty<?>> build() {
        final Set<KProperty<?>> indexElements = new HashSet<>();
        generators.forEach((generator) -> addIndexElements(indexElements,
                                                           generator));

        if (project != null && project.getRootPath() != null) {
            final String projectRootUri = project.getRootPath().toURI();
            indexElements.add(new KPropertyImpl<>(ProjectRootPathIndexTerm.TERM,
                                                  projectRootUri));
            final String projectName = project.getProjectName();
            if (projectName != null) {
                indexElements.add(new KPropertyImpl<>(ProjectNameIndexTerm.TERM,
                                                      projectName));
            }
        }

        if (pkgName == null) {
            pkgName = pkg.getPackageName();
        }
        if (pkgName != null) {
            indexElements.add(new KPropertyImpl<>(PackageNameIndexTerm.TERM,
                                                  pkgName));
        }
        return indexElements;
    }

    private void addIndexElements(final Set<KProperty<?>> indexElements,
                                  final IndexElementsGenerator generator) {
        if (generator == null) {
            return;
        }
        final List<KProperty<?>> generatorsIndexElements = generator.toIndexElements();
        indexElements.addAll(generatorsIndexElements);
    }

    public void setPackageName(String pkgName) {
        this.pkgName = pkgName;
    }
}
