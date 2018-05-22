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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryCopier;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class ProjectScreenServiceImpl
        implements ProjectScreenService {

    private WorkspaceProjectService projectService;
    private RepositoryService repositoryService;
    private KieModuleService moduleService;
    private ProjectScreenModelLoader loader;
    private ProjectScreenModelSaver saver;
    private RepositoryCopier repositoryCopier;
    private POMService pomService;
    private MetadataService metadataService;

    public ProjectScreenServiceImpl() {
        //WELD proxy
    }

    @Inject
    public ProjectScreenServiceImpl(final WorkspaceProjectService projectService,
                                    final RepositoryService repositoryService,
                                    final KieModuleService moduleService,
                                    final ProjectScreenModelLoader loader,
                                    final ProjectScreenModelSaver saver,
                                    final RepositoryCopier repositoryCopier,
                                    final POMService pomService,
                                    final MetadataService metadataService) {
        this.projectService = projectService;
        this.repositoryService = repositoryService;
        this.moduleService = moduleService;
        this.loader = loader;
        this.saver = saver;
        this.repositoryCopier = repositoryCopier;
        this.pomService = pomService;
        this.metadataService = metadataService;
    }

    @Override
    public ProjectScreenModel load(final Path pathToPom) {
        return loader.load(pathToPom);
    }

    @Override
    public void save(final Path pathToPomXML,
                     final ProjectScreenModel model,
                     final String comment) {
        save(pathToPomXML,
             model,
             comment,
             DeploymentMode.VALIDATED);
    }

    @Override
    public WorkspaceProject save(final Path pathToPomXML,
                                 final ProjectScreenModel model,
                                 final String comment,
                                 final DeploymentMode mode) {
        saver.save(pathToPomXML,
                   model,
                   mode,
                   comment);

        return projectService.resolveProject(pathToPomXML);
    }

    @Override
    public void delete(final WorkspaceProject project) {
        repositoryService.removeRepository(project.getRepository().getSpace(), project.getRepository().getAlias());
    }

    @Override
    public void copy(final WorkspaceProject project,
                     final String newName) {

        String newUniqueName = projectService.createFreshProjectName(project.getOrganizationalUnit(),
                                                                     newName);

        final Repository copy = repositoryCopier.copy(project.getOrganizationalUnit(),
                                                      newName,
                                                      project.getRootPath());

        if (!copy.getDefaultBranch().isPresent()) {
            throw new IllegalStateException("Copy should have at least one branch.");
        }

        final Path newPomPath = Paths.convert(Paths.convert(copy.getDefaultBranch().get().getPath()).resolve("pom.xml"));
        final POM pom = pomService.load(newPomPath);

        if (pom != null) {
            pom.setName(newUniqueName);
            pom.getGav().setArtifactId(newName);

            pomService.save(newPomPath,
                            pom,
                            metadataService.getMetadata(newPomPath),
                            "Renaming the project.",
                            true);
        }
    }

    @Override
    public void reImport(final Path pathToPomXML) {
        moduleService.reImport(pathToPomXML);
    }
}

