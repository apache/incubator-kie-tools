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

package org.guvnor.asset.management.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.asset.management.service.RepositoryStructureService;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.validation.ValidationUtils;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Files;

@Service
@ApplicationScoped
public class RepositoryStructureServiceImpl
        implements RepositoryStructureService {

    private IOService ioService;
    private POMService pomService;
    private ProjectService<? extends Project> projectService;
    private GuvnorM2Repository m2service;
    private CommentedOptionFactory optionsFactory;
    private ProjectRepositoryResolver repositoryResolver;
    private RepositoryStructureModelLoader modelLoader;
    private ManagedStatusUpdater managedStatusUpdater;

    public RepositoryStructureServiceImpl() {
        //Zero-parameter constructor for CDI proxies
    }

    @Inject
    public RepositoryStructureServiceImpl(final @Named("ioStrategy") IOService ioService,
                                          final POMService pomService,
                                          final ProjectService<? extends Project> projectService,
                                          final GuvnorM2Repository m2service,
                                          final CommentedOptionFactory optionsFactory,
                                          final ProjectRepositoryResolver repositoryResolver,
                                          final RepositoryStructureModelLoader modelLoader,
                                          final ManagedStatusUpdater managedStatusUpdater) {
        this.ioService = ioService;
        this.pomService = pomService;
        this.projectService = projectService;
        this.m2service = m2service;
        this.optionsFactory = optionsFactory;
        this.repositoryResolver = repositoryResolver;
        this.modelLoader = modelLoader;
        this.managedStatusUpdater = managedStatusUpdater;
    }

    @Override
    public Path initRepositoryStructure(final GAV gav,
                                        final Repository repo,
                                        final DeploymentMode mode) {
        final POM pom = new POM(repo.getAlias(),
                                repo.getAlias(),
                                gav,
                                true);

        if (DeploymentMode.VALIDATED.equals(mode)) {
            checkRepositories(pom);
        }

        //Creating the parent pom
        final Path fsRoot = repo.getRoot();
        final Path pathToPom = pomService.create(fsRoot,
                                                 "",
                                                 pom);
        //Deploying the parent pom artifact,
        // it needs to be deployed before the first child is created
        m2service.deployParentPom(gav);

        managedStatusUpdater.updateManagedStatus(repo,
                                                 true);

        return pathToPom;
    }

    @Override
    public Path initRepositoryStructure(final POM pom,
                                        final String baseUrl,
                                        final Repository repository,
                                        final boolean multiProject,
                                        final DeploymentMode mode) {
        if (pom == null || baseUrl == null || repository == null) {
            return null;
        }

        if (DeploymentMode.VALIDATED.equals(mode)) {
            checkRepositories(pom);
        }

        if (multiProject) {

            pom.setPackaging("pom");

            //Creating the parent pom
            final Path fsRoot = repository.getRoot();
            final Path pathToPom = pomService.create(fsRoot,
                                                     baseUrl,
                                                     pom);
            //Deploying the parent pom artifact,
            // it needs to be deployed before the first child is created
            m2service.deployParentPom(pom.getGav());

            managedStatusUpdater.updateManagedStatus(repository,
                                                     true);

            return pathToPom;
        } else {
            final Project project = projectService.newProject(repository.getBranchRoot(repository.getDefaultBranch()),
                                                              pom,
                                                              baseUrl,
                                                              mode);
            return project.getPomXMLPath();
        }
    }

    private void checkRepositories(final POM pom) {
        // Check is the POM's GAV resolves to any pre-existing artifacts. We don't need to filter
        // resolved Repositories by those enabled for the Project since this is a new Project.
        final Set<MavenRepositoryMetadata> repositories = repositoryResolver.getRepositoriesResolvingArtifact(pom.getGav());
        if (repositories.size() > 0) {
            throw new GAVAlreadyExistsException(pom.getGav(),
                                                repositories);
        }
    }

    @Override
    public Repository updateManagedStatus(final Repository repo,
                                          final boolean managed) {
        return managedStatusUpdater.updateManagedStatus(repo,
                                                        managed);
    }

    @Override
    public Path convertToMultiProjectStructure(final List<Project> projects,
                                               final GAV parentGav,
                                               final Repository repo,
                                               final boolean updateChildrenGav,
                                               final String comment) {

        if (projects == null || parentGav == null || repo == null) {
            return null;
        }

        try {
            final Path path = initRepositoryStructure(parentGav,
                                                      repo,
                                                      DeploymentMode.FORCED);

            final POM parentPom = pomService.load(path);
            if (parentPom == null) {
                //uncommon case, the pom was just created.
                return null;
            }

            ioService.startBatch(new FileSystem[]{Paths.convert(path).getFileSystem()},
                                 optionsFactory.makeCommentedOption(comment != null ? comment : ""));

            boolean saveParentPom = false;
            for (Project project : projects) {
                final POM pom = pomService.load(project.getPomXMLPath());
                pom.setParent(parentGav);
                if (updateChildrenGav) {
                    pom.getGav().setGroupId(parentGav.getGroupId());
                    pom.getGav().setVersion(parentGav.getVersion());
                }
                pomService.save(project.getPomXMLPath(),
                                pom,
                                null,
                                comment);

                parentPom.setPackaging("pom");
                parentPom.getModules().add(pom.getName() != null ? pom.getName() : pom.getGav().getArtifactId());
                saveParentPom = true;
            }

            if (saveParentPom) {
                pomService.save(path,
                                parentPom,
                                null,
                                comment);
            }

            return path;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        } finally {
            ioService.endBatch();
        }
    }

    @Override
    public RepositoryStructureModel load(final Repository repository,
                                         final String branch) {
        return modelLoader.load(repository,
                                branch,
                                true);
    }

    @Override
    public RepositoryStructureModel load(final Repository repository,
                                         final String branch,
                                         final boolean includeModules) {
        return modelLoader.load(repository,
                                branch,
                                includeModules);
    }

    @Override
    @SuppressWarnings("unused")
    public void save(final Path pathToPomXML,
                     final RepositoryStructureModel model,
                     final String comment) {
        final FileSystem fs = Paths.convert(pathToPomXML).getFileSystem();
        try {
            pomService.save(pathToPomXML,
                            model.getPOM(),
                            model.getPOMMetaData(),
                            comment,
                            true);
        } catch (final Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public boolean isValidProjectName(final String name) {
        return ValidationUtils.isFileName(name);
    }

    @Override
    public boolean isValidGroupId(final String groupId) {
        if (groupId == null || "".equals(groupId.trim())) {
            return false;
        }
        final String[] groupIdComponents = groupId.split("\\.",
                                                         -1);
        for (String s : groupIdComponents) {
            if (!ValidationUtils.isArtifactIdentifier(s)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isValidArtifactId(final String artifactId) {
        if (artifactId == null || "".equals(artifactId.trim())) {
            return false;
        }
        final String[] artifactIdComponents = artifactId.split("\\.",
                                                               -1);
        for (String s : artifactIdComponents) {
            if (!ValidationUtils.isArtifactIdentifier(s)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isValidVersion(final String version) {
        if (version == null || "".equals(version.trim())) {
            return false;
        }
        return version.matches("^[a-zA-Z0-9\\.\\-_]+$");
    }

    @Override
    public void delete(final Path pathToPomXML,
                       final String comment) {
        projectService.delete(pathToPomXML,
                              comment);
    }

    private List<Project> getProjects(final Path branchRoot) {
        final List<Project> repositoryProjects = new ArrayList<Project>();
        if (branchRoot == null) {
            return repositoryProjects;
        } else {
            final DirectoryStream<org.uberfire.java.nio.file.Path> nioRepositoryPaths = ioService.newDirectoryStream(Paths.convert(branchRoot));
            for (org.uberfire.java.nio.file.Path nioRepositoryPath : nioRepositoryPaths) {
                if (Files.isDirectory(nioRepositoryPath)) {
                    final org.uberfire.backend.vfs.Path projectPath = Paths.convert(nioRepositoryPath);
                    final Project project = projectService.resolveProject(projectPath);
                    if (project != null) {
                        repositoryProjects.add(project);
                    }
                }
            }
            return repositoryProjects;
        }
    }
}
