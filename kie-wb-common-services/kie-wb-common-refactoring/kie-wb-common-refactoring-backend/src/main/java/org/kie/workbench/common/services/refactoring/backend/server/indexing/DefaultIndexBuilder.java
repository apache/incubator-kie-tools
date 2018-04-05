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

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.kie.workbench.common.services.refactoring.IndexElementsGenerator;
import org.kie.workbench.common.services.refactoring.KPropertyImpl;
import org.kie.workbench.common.services.refactoring.model.index.terms.ModuleNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.ModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.PackageNameIndexTerm;
import org.uberfire.ext.metadata.model.KProperty;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class DefaultIndexBuilder implements IndexBuilder {

    protected final String fileName;
    protected final Module module;
    protected final Package pkg;
    protected String pkgName;

    private Set<IndexElementsGenerator> generators = new HashSet<>();

    public DefaultIndexBuilder(final String fileName,
                               final Module module,
                               final Package pkg) {
        this.fileName = checkNotNull("fileName",
                                     fileName);
        this.module = checkNotNull("module",
                                   module);
        this.pkg = checkNotNull("pkg",
                                pkg);
    }

    @Override
    public DefaultIndexBuilder addGenerator(final IndexElementsGenerator generator) {
        this.generators.add(checkNotNull("generator",
                                         generator));
        return this;
    }

    @Override
    public Set<KProperty<?>> build() {
        final Set<KProperty<?>> indexElements = new HashSet<>();
        generators.forEach((generator) -> addIndexElements(indexElements,
                                                           generator));

        if (module != null && module.getRootPath() != null) {
            final String projectRootUri = module.getRootPath().toURI();
            indexElements.add(new KPropertyImpl<>(ModuleRootPathIndexTerm.TERM,
                                                  projectRootUri));
            final String projectName = module.getModuleName();
            if (projectName != null) {
                indexElements.add(new KPropertyImpl<>(ModuleNameIndexTerm.TERM,
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
