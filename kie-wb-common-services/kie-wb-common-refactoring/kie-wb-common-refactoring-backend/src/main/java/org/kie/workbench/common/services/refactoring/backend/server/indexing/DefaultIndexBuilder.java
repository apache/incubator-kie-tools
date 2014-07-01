/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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
import org.kie.workbench.common.services.refactoring.model.index.IndexElementsGenerator;
import org.kie.workbench.common.services.refactoring.model.index.terms.PackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.ProjectRootPathIndexTerm;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.validation.PortablePreconditions;

public class DefaultIndexBuilder {

    private Project project;
    private Package pkg;

    private Set<IndexElementsGenerator> generators = new HashSet<IndexElementsGenerator>();

    public DefaultIndexBuilder( final Project project,
                                final Package pkg ) {
        this.project = PortablePreconditions.checkNotNull( "project",
                                                           project );
        this.pkg = PortablePreconditions.checkNotNull( "pkg",
                                                       pkg );
    }

    public DefaultIndexBuilder addGenerator( final IndexElementsGenerator generator ) {
        this.generators.add( PortablePreconditions.checkNotNull( "generator",
                                                                 generator ) );
        return this;
    }

    public Set<Pair<String, String>> build() {
        final Set<Pair<String, String>> indexElements = new HashSet<Pair<String, String>>();
        for ( IndexElementsGenerator generator : generators ) {
            addIndexElements( indexElements,
                              generator );
        }
        if ( project != null && project.getRootPath() != null ) {
            indexElements.add( new Pair<String, String>( ProjectRootPathIndexTerm.TERM,
                                                         project.getRootPath().toURI() ) );
        }
        if ( pkg != null ) {
            indexElements.add( new Pair<String, String>( PackageNameIndexTerm.TERM,
                                                         pkg.getPackageName() ) );
        }
        return indexElements;
    }

    private void addIndexElements( final Set<Pair<String, String>> indexElements,
                                   final IndexElementsGenerator generator ) {
        if ( generator == null ) {
            return;
        }
        final List<Pair<String, String>> generatorsIndexElements = generator.toIndexElements();
        indexElements.addAll( generatorsIndexElements );
    }

}
