/*
 * Copyright 2012 JBoss Inc
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
package org.kie.workbench.common.services.datamodel.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.datamodel.backend.server.cache.LRUDataModelOracleCache;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleIncrementalPayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 *
 */
@Service
@ApplicationScoped
public class IncrementalDataModelServiceImpl implements IncrementalDataModelService {

    @Inject
    @Named("PackageDataModelOracleCache")
    private LRUDataModelOracleCache cachePackages;

    @Inject
    private ProjectService projectService;

    @Override
    public PackageDataModelOracleIncrementalPayload getUpdates( final Path resourcePath,
                                                                final Imports imports,
                                                                final String factType ) {
        PortablePreconditions.checkNotNull( "resourcePath",
                                            resourcePath );
        PortablePreconditions.checkNotNull( "imports",
                                            imports );
        PortablePreconditions.checkNotNull( "factType",
                                            factType );

        final PackageDataModelOracleIncrementalPayload dataModel = new PackageDataModelOracleIncrementalPayload();

        try {
            //Check resource was within a Project structure
            final Project project = resolveProject( resourcePath );
            if ( project == null ) {
                return dataModel;
            }
            //Check resource was within a Package structure
            final Package pkg = resolvePackage( resourcePath );
            if ( pkg == null ) {
                return dataModel;
            }

            //Get the fully qualified class name of the fact type
            String fullyQualifiedClassName = null;

            // Check if the factType is already the FQCN
            if (factType.contains(".")) {
                fullyQualifiedClassName = factType;
            }

            if (fullyQualifiedClassName == null) {
                for (Import imp : imports.getImports()) {
                    if (imp.getType().endsWith(factType)) {
                        fullyQualifiedClassName = imp.getType();
                        break;
                    }
                }
            }
            if ( fullyQualifiedClassName == null ) {
                fullyQualifiedClassName = pkg.getPackageName() + "." + factType;
            }

            //Retrieve (or build) oracle and populate incremental content
            final PackageDataModelOracle oracle = cachePackages.assertPackageDataModelOracle( project,
                                                                                              pkg );
            DataModelOracleUtilities.populateDataModel( oracle,
                                                        dataModel,
                                                        fullyQualifiedClassName );
            return dataModel;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }

    }

    private Project resolveProject( final Path resourcePath ) {
        return projectService.resolveProject( resourcePath );
    }

    private Package resolvePackage( final Path resourcePath ) {
        return projectService.resolvePackage( resourcePath );
    }

}
