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
import org.guvnor.common.services.project.backend.server.AbstractModuleService;
import org.guvnor.common.services.project.backend.server.ModuleFinder;
import org.guvnor.common.services.project.builder.events.InvalidateDMOModuleCacheEvent;
import org.guvnor.common.services.project.events.NewModuleEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.RenameModuleEvent;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;

@Service
@ApplicationScoped
public class KieModuleServiceImpl
        extends AbstractModuleService<KieModule>
        implements KieModuleFactory,
                   org.kie.workbench.common.services.shared.project.KieModuleService {

    private ModuleSaver moduleSaver;
    private ModuleRepositoryResolver repositoryResolver;

    public KieModuleServiceImpl() {
    }

    @Inject
    public KieModuleServiceImpl(final @Named("ioStrategy") IOService ioService,
                                final ModuleSaver moduleSaver,
                                final POMService pomService,
                                final RepositoryService repoService,
                                final Event<NewModuleEvent> newModuleEvent,
                                final Event<NewPackageEvent> newPackageEvent,
                                final Event<RenameModuleEvent> renameModuleEvent,
                                final Event<InvalidateDMOModuleCacheEvent> invalidateDMOCache,
                                final SessionInfo sessionInfo,
                                final CommentedOptionFactory commentedOptionFactory,
                                final ModuleFinder moduleFinder,
                                final KieResourceResolver resourceResolver,
                                final ModuleRepositoryResolver repositoryResolver) {
        super(ioService,
              pomService,
              repoService,
              newModuleEvent,
              newPackageEvent,
              renameModuleEvent,
              invalidateDMOCache,
              sessionInfo,
              commentedOptionFactory,
              moduleFinder,
              resourceResolver);
        this.moduleSaver = moduleSaver;
        this.repositoryResolver = repositoryResolver;
    }

    protected void setModuleSaver(final ModuleSaver projectSaver) {
        this.moduleSaver = projectSaver;
    }

    @Override
    public KieModule newModule(final Path repositoryPath,
                               final POM pom) {
        return newModule(repositoryPath,
                         pom,
                         DeploymentMode.VALIDATED);
    }

    @Override
    public KieModule newModule(final Path repositoryPath,
                               final POM pom,
                               final DeploymentMode mode) {
        if (DeploymentMode.VALIDATED.equals(mode)) {
            checkRepositories(pom);
        }
        return moduleSaver.save(repositoryPath,
                                pom);
    }

    private void checkRepositories(final POM pom) {
        // Check is the POM's GAV resolves to any pre-existing artifacts. We don't need to filter
        // resolved Repositories by those enabled for the Module since this is a new Module.
        final Set<MavenRepositoryMetadata> repositories = repositoryResolver.getRepositoriesResolvingArtifact(pom.getGav());
        if (repositories.size() > 0) {
            throw new GAVAlreadyExistsException(pom.getGav(),
                                                repositories);
        }
    }

    @Override
    public KieModule simpleModuleInstance(final org.uberfire.java.nio.file.Path nioModuleRootPath) {
        return (KieModule) resourceResolver.simpleModuleInstance(nioModuleRootPath);
    }

    @Override
    public KieModule resolveModule(final Path resource) {
        return (KieModule) resourceResolver.resolveModule(resource);
    }

    @Override
    public KieModule resolveModule(Path resource, boolean loadPOM) {
        return (KieModule) resourceResolver.resolveModule(resource, loadPOM);
    }

    @Override
    public Module resolveParentModule(final Path resource) {
        return resourceResolver.resolveParentModule(resource);
    }

    @Override
    public Module resolveToParentModule(final Path resource) {
        return resourceResolver.resolveToParentModule(resource);
    }

    @Override
    public Set<Package> resolvePackages(final Module project) {
        return resourceResolver.resolvePackages(project);
    }

    @Override
    public Set<Package> resolvePackages(final Package pkg) {
        return resourceResolver.resolvePackages(pkg);
    }

    @Override
    public Package resolveDefaultPackage(final Module project) {
        return resourceResolver.resolveDefaultPackage(project);
    }

    @Override
    public Package resolveDefaultWorkspacePackage(final Module project) {
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
