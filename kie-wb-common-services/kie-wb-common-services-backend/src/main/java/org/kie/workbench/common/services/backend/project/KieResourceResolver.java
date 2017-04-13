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

package org.kie.workbench.common.services.backend.project;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.ProjectResourcePathResolver;
import org.guvnor.common.services.project.backend.server.ResourceResolver;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.server.config.ConfigurationService;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Files;

import static org.guvnor.common.services.project.utils.ProjectResourcePaths.*;
import static org.kie.workbench.common.services.backend.project.KieProjectResourcePaths.*;

public class KieResourceResolver
        extends ResourceResolver<KieProject> {

    private KModuleService kModuleService;

    public KieResourceResolver() {

    }

    @Inject
    public KieResourceResolver( final @Named("ioStrategy") IOService ioService,
                                final POMService pomService,
                                final ConfigurationService configurationService,
                                final CommentedOptionFactory commentedOptionFactory,
                                final BackwardCompatibleUtil backward,
                                final KModuleService kModuleService,
                                final Instance<ProjectResourcePathResolver> resourcePathResolversInstance ) {
        super( ioService,
               pomService,
               configurationService,
               commentedOptionFactory,
               backward,
               resourcePathResolversInstance );
        this.kModuleService = kModuleService;
    }

    @Override
    public KieProject resolveProject( final Path resource ) {
        try {
            //Null resource paths cannot resolve to a Project
            if ( resource == null ) {
                return null;
            }

            //Check if resource is the project root
            org.uberfire.java.nio.file.Path path = Paths.convert( resource ).normalize();

            //A project root is the folder containing the pom.xml file. This will be the parent of the "src" folder
            if ( Files.isRegularFile( path ) ) {
                path = path.getParent();
            }
            while ( path.getNameCount() > 0 && !path.getFileName().toString().equals( SOURCE_FILENAME ) ) {
                if ( hasPom( path ) && hasKModule( path ) ) {
                    return makeProject( path );
                }
                path = path.getParent();
            }
            if ( path.getNameCount() == 0 ) {
                return null;
            }
            path = path.getParent();
            if ( path.getNameCount() == 0 || path == null ) {
                return null;
            }
            if ( !hasPom( path ) ) {
                return null;
            }
            if ( !hasKModule( path ) ) {
                return null;
            }
            return makeProject( path );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    protected KieProject makeProject( final org.uberfire.java.nio.file.Path nioProjectRootPath ) {
        final KieProject project = simpleProjectInstance( nioProjectRootPath );
        final POM pom = pomService.load( project.getPomXMLPath() );
        project.setPom( pom );

        addSecurityGroups( project );

        return project;
    }

    @Override
    public org.guvnor.common.services.project.model.Package resolvePackage( final Path resource ) {
        try {
            //Null resource paths cannot resolve to a Project
            if ( resource == null ) {
                return null;
            }

            //If Path is not within a Project we cannot resolve a package
            final Project project = resolveProject( resource );
            if ( project == null ) {
                return null;
            }

            //pom.xml and kmodule.xml are not inside packages
            if ( isPom( resource ) || kModuleService.isKModule( resource ) ) {
                return null;
            }

            return makePackage( project,
                                resource );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public KieProject simpleProjectInstance( final org.uberfire.java.nio.file.Path nioProjectRootPath ) {
        final Path projectRootPath = Paths.convert( nioProjectRootPath );
        return new KieProject( projectRootPath,
                               Paths.convert( nioProjectRootPath.resolve( POM_PATH ) ),
                               Paths.convert( nioProjectRootPath.resolve( KMODULE_PATH ) ),
                               Paths.convert( nioProjectRootPath.resolve( PROJECT_IMPORTS_PATH ) ),
                               Paths.convert( nioProjectRootPath.resolve( PROJECT_REPOSITORIES_PATH ) ),
                               Paths.convert( nioProjectRootPath.resolve( PACKAGE_NAME_WHITE_LIST ) ),
                               projectRootPath.getFileName() );
    }

    protected boolean hasKModule( final org.uberfire.java.nio.file.Path path ) {
        final org.uberfire.java.nio.file.Path kmodulePath = path.resolve( KMODULE_PATH );
        return Files.exists( kmodulePath );
    }
}
