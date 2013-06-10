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

package org.kie.workbench.common.screens.explorer.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.Files;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.project.service.model.Package;
import org.kie.workbench.common.services.project.service.model.Project;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.group.GroupService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.security.Identity;
import org.uberfire.security.authz.AuthorizationManager;

@Service
@ApplicationScoped
public class ExplorerServiceImpl
        implements ExplorerService {

    private static String[] sourcePaths = { "src/main/java", "src/main/resources", "src/test/java", "src/test/resources" };

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private ProjectService projectService;

    @Inject
    private GroupService groupService;

    @Inject
    private AuthorizationManager authorizationManager;

    @Inject
    @SessionScoped
    private Identity identity;

    @Inject
    private Paths paths;

    public ExplorerServiceImpl() {
        // Boilerplate sacrifice for Weld
    }

    public ExplorerServiceImpl( final IOService ioService,
                                final AuthorizationManager authorizationManager,
                                final ProjectService projectService,
                                final GroupService groupService,
                                final Identity identity,
                                final Paths paths ) {
        this.ioService = ioService;
        this.authorizationManager = authorizationManager;
        this.projectService = projectService;
        this.groupService = groupService;
        this.identity = identity;
        this.paths = paths;
    }

    @Override
    public Collection<Group> getGroups() {
        final Collection<Group> groups = groupService.getGroups();
        final Collection<Group> authorizedGroups = new ArrayList<Group>();
        for ( Group group : groups ) {
            if ( authorizationManager.authorize( group,
                                                 identity ) ) {
                authorizedGroups.add( group );
            }
        }
        return authorizedGroups;
    }

    @Override
    public Collection<Repository> getRepositories( final Group group ) {
        final Collection<Repository> authorizedRepositories = new ArrayList<Repository>();
        for ( Repository repository : group.getRepositories() ) {
            if ( authorizationManager.authorize( repository,
                                                 identity ) ) {
                authorizedRepositories.add( repository );
            }
        }
        return authorizedRepositories;
    }

    @Override
    public Collection<Project> getProjects( final Repository repository ) {
        final Collection<Project> authorizedProjects = new ArrayList<Project>();
        final Path repositoryRoot = repository.getRoot();
        final DirectoryStream<org.kie.commons.java.nio.file.Path> nioRepositoryPaths = ioService.newDirectoryStream( paths.convert( repositoryRoot ) );
        for ( org.kie.commons.java.nio.file.Path nioRepositoryPath : nioRepositoryPaths ) {
            if ( Files.isDirectory( nioRepositoryPath ) ) {
                final org.uberfire.backend.vfs.Path projectPath = paths.convert( nioRepositoryPath );
                if ( projectService.resolveProject( projectPath ) != null ) {
                    final Project project = new Project( projectPath,
                                                         projectPath.getFileName() );
                    authorizedProjects.add( project );
                }
            }
        }
        return authorizedProjects;
    }

    @Override
    public Collection<Package> getPackages( final Project project ) {
        final Collection<Package> packages = new HashSet<Package>();
        final Path projectRoot = project.getPath();
        for ( String src : sourcePaths ) {
            final org.kie.commons.java.nio.file.Path nioSrcRootPath = paths.convert( projectRoot ).resolve( src );
            packages.addAll( getPackages( nioSrcRootPath,
                                          nioSrcRootPath ) );
        }
        return packages;
    }

    private Collection<Package> getPackages( final org.kie.commons.java.nio.file.Path nioSrcRootPath,
                                             final org.kie.commons.java.nio.file.Path nioSrcPath ) {
        final Collection<Package> packages = new HashSet<Package>();
        if ( !Files.exists( nioSrcPath ) ) {
            return packages;
        }
        packages.add( makePackage( nioSrcRootPath,
                                   nioSrcPath ) );
        final DirectoryStream<org.kie.commons.java.nio.file.Path> nioProjectSrcPaths = ioService.newDirectoryStream( nioSrcPath );
        for ( org.kie.commons.java.nio.file.Path nioPackageSrcPath : nioProjectSrcPaths ) {
            if ( Files.isDirectory( nioPackageSrcPath ) ) {
                packages.addAll( getPackages( nioSrcRootPath,
                                              nioPackageSrcPath ) );
                packages.add( makePackage( nioSrcRootPath,
                                           nioPackageSrcPath ) );
            }
        }
        return packages;
    }

    private Package makePackage( final org.kie.commons.java.nio.file.Path nioSrcRootPath,
                                 final org.kie.commons.java.nio.file.Path nioPackageSrcPath ) {
        final org.kie.commons.java.nio.file.Path nioRelativePath = nioSrcRootPath.relativize( nioPackageSrcPath );
        final org.uberfire.backend.vfs.Path relativePath = paths.convert( nioRelativePath,
                                                                          false );
        final Package pkg = new Package( relativePath,
                                         getPackageDisplayName( nioPackageSrcPath ) );
        return pkg;
    }

    private String getPackageDisplayName( final org.kie.commons.java.nio.file.Path nioPackageSrcPath ) {
        final org.uberfire.backend.vfs.Path packageSrcPath = paths.convert( nioPackageSrcPath );
        final String packageName = projectService.resolvePackageName( packageSrcPath );
        return packageName.isEmpty() ? "<default>" : packageName;
    }

}
