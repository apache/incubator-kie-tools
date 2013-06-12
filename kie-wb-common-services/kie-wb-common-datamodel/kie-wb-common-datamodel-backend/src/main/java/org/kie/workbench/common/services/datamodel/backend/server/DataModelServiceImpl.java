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

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.backend.exceptions.ExceptionUtilities;
import org.kie.workbench.common.services.datamodel.backend.server.cache.LRUDataModelOracleCache;
import org.kie.workbench.common.services.datamodel.backend.server.cache.LRUProjectDataModelOracleCache;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracleImpl;
import org.kie.workbench.common.services.datamodel.oracle.ProjectDataModelOracle;
import org.kie.workbench.common.services.datamodel.oracle.ProjectDataModelOracleImpl;
import org.kie.workbench.common.services.datamodel.service.DataModelService;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.shared.context.Package;
import org.kie.workbench.common.services.shared.context.Project;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class DataModelServiceImpl
        implements DataModelService {

    @Inject
    @Named("PackageDataModelOracleCache")
    private LRUDataModelOracleCache cachePackages;

    @Inject
    @Named("ProjectDataModelOracleCache")
    private LRUProjectDataModelOracleCache cacheProjects;

    @Inject
    private ProjectService projectService;

    @Override
    public PackageDataModelOracle getDataModel( final Path resourcePath ) {
        try {
            PortablePreconditions.checkNotNull( "resourcePath",
                                                resourcePath );
            final Project project = resolveProject( resourcePath );
            final Package pkg = resolvePackage( resourcePath );

            //Resource was not within a Project structure
            if ( project == null ) {
                return new PackageDataModelOracleImpl();
            }

            //Retrieve (or build) oracle
            final PackageDataModelOracle oracle = cachePackages.assertPackageDataModelOracle( project,
                                                                                              pkg );
            return oracle;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public ProjectDataModelOracle getProjectDataModel( final Path resourcePath ) {
        try {
            PortablePreconditions.checkNotNull( "resourcePath",
                                                resourcePath );
            final Project project = resolveProject( resourcePath );

            //Resource was not within a Project structure
            if ( project == null ) {
                return new ProjectDataModelOracleImpl();
            }

            //Retrieve (or build) oracle
            final ProjectDataModelOracle oracle = cacheProjects.assertProjectDataModelOracle( project );
            return oracle;

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
