/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.whitelist;

import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.POM;
import org.kie.workbench.common.services.backend.builder.core.NoBuilderFoundException;
import org.kie.workbench.common.services.shared.dependencies.DependencyService;

public class PackageNameSearchProvider {

    private DependencyService dependencyService;

    public PackageNameSearchProvider() {

    }

    @Inject
    public PackageNameSearchProvider( final DependencyService dependencyService ) {
        this.dependencyService = dependencyService;
    }

    /**
     * @param pom POM for the project
     * @return All the packages that are in the direct dependencies of the pom.
     */
    public PackageNameSearch newTopLevelPackageNamesSearch( final POM pom ) {
        return new PackageNameSearch( pom );
    }

    public class PackageNameSearch {

        private final POM pom;
        private final Set<String> result = new HashSet<String>();

        private PackageNameSearch( final POM pom ) {
            this.pom = pom;
        }

        public Set<String> search()
                throws NoBuilderFoundException {
            loadPackageNames();
            return result;
        }

        private void loadPackageNames()
                throws NoBuilderFoundException {

            for (Dependency dependency : dependencyService.loadDependencies( pom.getGav() )) {
                if ( isDependencyDefinedInThePOMXML( dependency ) ) {
                    result.addAll( dependencyService.loadPackageNames( dependency ) );
                }
            }
        }

        private boolean isDependencyDefinedInThePOMXML( final Dependency other ) {
            for (Dependency dependency : pom.getDependencies()) {
                if ( areEqual( dependency.getArtifactId(), other.getArtifactId() )
                        && areEqual( dependency.getGroupId(), other.getGroupId() ) ) {
                    return true;
                }
            }
            return false;
        }

        private boolean areEqual( final String value,
                                  final String other ) {
            if ( value == null || other == null ) {
                return false;
            } else {
                return value.equals( other );
            }
        }
    }
}
