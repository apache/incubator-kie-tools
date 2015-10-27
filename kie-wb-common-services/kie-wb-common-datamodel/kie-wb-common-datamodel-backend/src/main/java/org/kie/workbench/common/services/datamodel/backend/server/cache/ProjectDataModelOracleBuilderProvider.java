/*
 * Copyright 2015 JBoss Inc
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
package org.kie.workbench.common.services.datamodel.backend.server.cache;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.TypeSource;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.builder.Builder;
import org.kie.workbench.common.services.backend.whitelist.PackageNameWhiteListServiceImpl;
import org.kie.workbench.common.services.backend.builder.TypeSourceResolver;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Files;

class ProjectDataModelOracleBuilderProvider {

    private static final Logger log = LoggerFactory.getLogger( ProjectDataModelOracleBuilderProvider.class );

    private ProjectImportsService importsService;
    private PackageNameWhiteListServiceImpl packageNameWhiteListService;

    public ProjectDataModelOracleBuilderProvider() {
    }

    @Inject
    public ProjectDataModelOracleBuilderProvider( final PackageNameWhiteListServiceImpl packageNameWhiteListService,
                                                  final ProjectImportsService importsService ) {
        this.packageNameWhiteListService = packageNameWhiteListService;
        this.importsService = importsService;
    }

    public InnerBuilder newBuilder( final KieProject project,
                                    final Builder builder ) {

        final KieModuleMetaData kieModuleMetaData = builder.getKieModuleMetaDataIgnoringErrors();
        final TypeSourceResolver typeSourceResolver = builder.getTypeSourceResolver( kieModuleMetaData );

        return new InnerBuilder( project,
                                 kieModuleMetaData,
                                 typeSourceResolver );
    }

    class InnerBuilder {

        private final ProjectDataModelOracleBuilder pdBuilder = ProjectDataModelOracleBuilder.newProjectOracleBuilder();

        private final KieProject project;
        private final KieModuleMetaData kieModuleMetaData;
        private final TypeSourceResolver typeSourceResolver;

        private InnerBuilder( final KieProject project,
                              final KieModuleMetaData kieModuleMetaData,
                              final TypeSourceResolver typeSourceResolver ) {
            this.project = project;
            this.kieModuleMetaData = kieModuleMetaData;
            this.typeSourceResolver = typeSourceResolver;
        }

        public ProjectDataModelOracle build() {

            addFromKieModuleMetadata();

            addExternalImports();

            return pdBuilder.build();
        }

        /**
         * The availability of these classes is checked in Builder and failed fast. Here we load them into the DMO
         */
        private void addExternalImports() {
            if ( Files.exists( Paths.convert( project.getImportsPath() ) ) ) {
                for (final Import item : getImports()) {
                    addClass( item );
                }
            }
        }

        private void addFromKieModuleMetadata() {
            for (final String packageName : getFilteredPackageNames()) {
                pdBuilder.addPackage( packageName );
                addClasses( packageName,
                            kieModuleMetaData.getClasses( packageName ) );
            }
        }

        /**
         * @return A "white list" of package names that are available for authoring
         */
        private Set<String> getFilteredPackageNames() {
            return packageNameWhiteListService.filterPackageNames( project,
                                                            kieModuleMetaData.getPackages() );
        }

        private void addClasses( final String packageName,
                                 final Collection<String> classes ) {
            for (final String className : classes) {
                addClass( packageName,
                          className );
            }
        }

        private void addClass( final Import item ) {
            try {
                Class clazz = this.getClass().getClassLoader().loadClass( item.getType() );
                pdBuilder.addClass( clazz,
                                    false,
                                    TypeSource.JAVA_DEPENDENCY );

            } catch (ClassNotFoundException cnfe) {
                //Class resolution would have happened in Builder and reported as warnings so log error here at debug level to avoid flooding logs
                log.debug( cnfe.getMessage() );

            } catch (IOException ioe) {
                log.debug( ioe.getMessage() );
            }
        }

        private void addClass( final String packageName,
                               final String className ) {
            try {
                final Class clazz = kieModuleMetaData.getClass( packageName,
                                                                className );
                pdBuilder.addClass( clazz,
                                    kieModuleMetaData.getTypeMetaInfo( clazz ).isEvent(),
                                    typeSourceResolver.getTypeSource( clazz ) );

            } catch (Throwable e) {
                //Class resolution would have happened in Builder and reported as warnings so log error here at debug level to avoid flooding logs
                log.debug( e.getMessage() );
            }
        }

        private List<Import> getImports() {
            return importsService.load( project.getImportsPath() ).getImports().getImports();
        }
    }
}
