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

package org.kie.workbench.common.screens.projecteditor.backend.server;

import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.ProjectRepositories;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.backend.project.KieRepositoryResolver;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class ProjectScreenServiceImpl
        implements ProjectScreenService {

    private KieProjectService projectService;
    private KieRepositoryResolver repositoryResolver;
    private ProjectScreenModelLoader loader;
    private ProjectScreenModelSaver saver;

    public ProjectScreenServiceImpl() {
        //WELD proxy
    }

    @Inject
    public ProjectScreenServiceImpl( final KieProjectService projectService,
                                     final KieRepositoryResolver repositoryResolver,
                                     final ProjectScreenModelLoader loader,
                                     final ProjectScreenModelSaver saver ) {
        this.projectService = projectService;
        this.repositoryResolver = repositoryResolver;
        this.loader = loader;
        this.saver = saver;
    }

    @Override
    public ProjectScreenModel load( final Path pathToPom ) {
        return loader.load( pathToPom );
    }

    @Override
    public void save( final Path pathToPomXML,
                      final ProjectScreenModel model,
                      final String comment ) {
        save( pathToPomXML,
              model,
              comment,
              DeploymentMode.VALIDATED );
    }

    @Override
    public void save( final Path pathToPomXML,
                      final ProjectScreenModel model,
                      final String comment,
                      final DeploymentMode mode ) {
        if ( DeploymentMode.VALIDATED.equals( mode ) ) {
            checkRepositories( pathToPomXML,
                               model );
        }
        saver.save( pathToPomXML,
                    model,
                    comment );
    }

    private void checkRepositories( final Path pathToPomXML,
                                    final ProjectScreenModel model ) {
        final KieProject project = projectService.resolveProject( pathToPomXML );

        // Check is the Project's "proposed" GAV resolves to any pre-existing artifacts.
        // Use the Repositories in the model since the User may update the Repositories filter and save.
        final Set<MavenRepositoryMetadata> filter = new HashSet<MavenRepositoryMetadata>();
        final ProjectRepositories projectRepositories = model.getRepositories();
        for ( ProjectRepositories.ProjectRepository pr : projectRepositories.getRepositories() ) {
            if ( pr.isIncluded() ) {
                filter.add( pr.getMetadata() );
            }
        }
        final MavenRepositoryMetadata[] aFilter = new MavenRepositoryMetadata[ filter.size() ];
        filter.toArray( aFilter );
        final GAV gav = model.getPOM().getGav();
        final Set<MavenRepositoryMetadata> repositories = repositoryResolver.getRepositoriesResolvingArtifact( gav,
                                                                                                               project,
                                                                                                               aFilter );
        if ( repositories.size() > 0 ) {
            throw new GAVAlreadyExistsException( gav,
                                                 repositories );
        }
    }

    @Override
    public ProjectScreenModel rename( final Path pathToPomXML,
                                      final String newName,
                                      final String comment ) {
        return load( projectService.rename( pathToPomXML,
                                            newName,
                                            comment ) );
    }

    @Override
    public void delete( final Path pathToPomXML,
                        final String comment ) {
        projectService.delete( pathToPomXML,
                               comment );
    }

    @Override
    public void copy( final Path pathToPomXML,
                      final String newName,
                      final String comment ) {
        projectService.copy( pathToPomXML,
                             newName,
                             comment );
    }

}
