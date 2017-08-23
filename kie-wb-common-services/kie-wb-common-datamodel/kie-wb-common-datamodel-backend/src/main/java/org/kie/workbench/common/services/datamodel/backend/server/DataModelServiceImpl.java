/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import org.appformer.project.datamodel.commons.oracle.ProjectDataModelOracleImpl;
import org.appformer.project.datamodel.oracle.ProjectDataModelOracle;
import org.drools.workbench.models.commons.backend.oracle.PackageDataModelOracleImpl;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.model.Package;
import org.kie.workbench.common.services.datamodel.backend.server.cache.LRUDataModelOracleCache;
import org.kie.workbench.common.services.datamodel.backend.server.cache.LRUProjectDataModelOracleCache;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;

@ApplicationScoped
public class DataModelServiceImpl
        implements DataModelService {

    private LRUDataModelOracleCache cachePackages;

    private LRUProjectDataModelOracleCache cacheProjects;

    private KieProjectService projectService;

    @Inject
    public DataModelServiceImpl( final @Named("PackageDataModelOracleCache") LRUDataModelOracleCache cachePackages,
                                 final @Named("ProjectDataModelOracleCache") LRUProjectDataModelOracleCache cacheProjects,
                                 final KieProjectService projectService) {
        this.cachePackages = cachePackages;
        this.cacheProjects = cacheProjects;
        this.projectService = projectService;
    }
    
    @Override
    public PackageDataModelOracle getDataModel( final Path resourcePath ) {
        try {
            PortablePreconditions.checkNotNull( "resourcePath",
                                                resourcePath );
            final KieProject project = resolveProject( resourcePath );
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
            final KieProject project = resolveProject( resourcePath );

            //Resource was not within a Project structure
            if ( project == null ) {
                return new ProjectDataModelOracleImpl();
            }

            //Retrieve (or build) oracle
            final ProjectDataModelOracle oracle = cacheProjects.assertProjectDataModelOracle( project );
            return oracle;

        } catch ( Exception e ) {
            e.printStackTrace();
            throw ExceptionUtilities.handleException( e );
        }
    }

    private KieProject resolveProject( final Path resourcePath ) {
        return projectService.resolveProject( resourcePath );
    }

    private Package resolvePackage( final Path resourcePath ) {
        return projectService.resolvePackage( resourcePath );
    }

}
