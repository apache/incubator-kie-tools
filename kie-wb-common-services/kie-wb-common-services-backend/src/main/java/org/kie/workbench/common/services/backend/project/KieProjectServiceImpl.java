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

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.AbstractProjectService;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.events.RenameProjectEvent;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

@Service
@ApplicationScoped
public class KieProjectServiceImpl
        extends AbstractProjectService<KieProject>
        implements KieProjectFactory,
                   org.kie.workbench.common.services.shared.project.KieProjectService {

    private ProjectSaver projectSaver;
    private ProjectRepositoryResolver repositoryResolver;

    public KieProjectServiceImpl() {
    }

    @Inject
    public KieProjectServiceImpl(final @Named("ioStrategy") IOService ioService,
                                 final ProjectSaver projectSaver,
                                 final POMService pomService,
                                 final ConfigurationService configurationService,
                                 final ConfigurationFactory configurationFactory,
                                 final Event<NewProjectEvent> newProjectEvent,
                                 final Event<NewPackageEvent> newPackageEvent,
                                 final Event<RenameProjectEvent> renameProjectEvent,
                                 final Event<InvalidateDMOProjectCacheEvent> invalidateDMOCache,
                                 final SessionInfo sessionInfo,
                                 final AuthorizationManager authorizationManager,
                                 final BackwardCompatibleUtil backward,
                                 final CommentedOptionFactory commentedOptionFactory,
                                 final KieResourceResolver resourceResolver,
                                 final ProjectRepositoryResolver repositoryResolver) {
        super(ioService,
              pomService,
              configurationService,
              configurationFactory,
              newProjectEvent,
              newPackageEvent,
              renameProjectEvent,
              invalidateDMOCache,
              sessionInfo,
              authorizationManager,
              backward,
              commentedOptionFactory,
              resourceResolver);
        this.projectSaver = projectSaver;
        this.repositoryResolver = repositoryResolver;
    }

    protected void setProjectSaver(final ProjectSaver projectSaver) {
        this.projectSaver = projectSaver;
    }

    @Override
    public KieProject newProject(final Path repositoryPath,
                                 final POM pom,
                                 final String baseUrl) {
        return newProject(repositoryPath,
                          pom,
                          baseUrl,
                          DeploymentMode.VALIDATED);
    }

    @Override
    public KieProject newProject(final Path repositoryPath,
                                 final POM pom,
                                 final String baseUrl,
                                 final DeploymentMode mode) {
        if (DeploymentMode.VALIDATED.equals(mode)) {
            checkRepositories(pom);
        }
        return projectSaver.save(repositoryPath,
                                 pom,
                                 baseUrl);
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
    public KieProject simpleProjectInstance(final org.uberfire.java.nio.file.Path nioProjectRootPath) {
        return (KieProject) resourceResolver.simpleProjectInstance(nioProjectRootPath);
    }

    @Override
    public KieProject resolveProject(final Path resource) {
        return (KieProject) resourceResolver.resolveProject(resource);
    }

    @Override
    public Project resolveParentProject(final Path resource) {
        return resourceResolver.resolveParentProject(resource);
    }

    @Override
    public Project resolveToParentProject(final Path resource) {
        return resourceResolver.resolveToParentProject(resource);
    }

    @Override
    public Set<Package> resolvePackages(final Project project) {
        return resourceResolver.resolvePackages(project);
    }

    @Override
    public Set<Package> resolvePackages(final Package pkg) {
        return resourceResolver.resolvePackages(pkg);
    }

    @Override
    public Package resolveDefaultPackage(final Project project) {
        return resourceResolver.resolveDefaultPackage(project);
    }

    @Override
    public Package resolveDefaultWorkspacePackage(final Project project) {
        return resourceResolver.resolveDefaultWorkspacePackage(project);
    }

    @Override
    public Path resolveDefaultPath(Package pkg,
                                   String resourceType) {
        return resourceResolver.resolveDefaultPath(pkg,
                                                   resourceType);
    }

    @Override
    public Package resolveParentPackage(final Package pkg) {
        return resourceResolver.resolveParentPackage(pkg);
    }

    @Override
    public boolean isPom(final Path resource) {
        return resourceResolver.isPom(resource);
    }

    @Override
    public Package resolvePackage(final Path resource) {
        return resourceResolver.resolvePackage(resource);
    }
}
