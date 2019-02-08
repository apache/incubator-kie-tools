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

package org.kie.workbench.common.screens.projecteditor.backend.server;

import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.ModuleRepositoriesService;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.project.service.POMService;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.backend.builder.core.LRUPomModelCache;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

public class ProjectScreenModelSaver {

    private POMService pomService;
    private KModuleService kModuleService;
    private ProjectImportsService importsService;
    private ModuleRepositoriesService repositoriesService;
    private PackageNameWhiteListService whiteListService;

    private IOService ioService;
    private KieModuleService moduleService;
    private ModuleRepositoryResolver repositoryResolver;
    private CommentedOptionFactory commentedOptionFactory;

    private LRUPomModelCache pomModelCache;

    public ProjectScreenModelSaver() {
    }

    @Inject
    public ProjectScreenModelSaver(final POMService pomService,
                                   final KModuleService kModuleService,
                                   final ProjectImportsService importsService,
                                   final ModuleRepositoriesService repositoriesService,
                                   final PackageNameWhiteListService whiteListService,
                                   final @Named("ioStrategy") IOService ioService,
                                   final KieModuleService moduleService,
                                   final ModuleRepositoryResolver repositoryResolver,
                                   final CommentedOptionFactory commentedOptionFactory,
                                   final LRUPomModelCache pomModelCache) {
        this.pomService = pomService;
        this.kModuleService = kModuleService;
        this.importsService = importsService;
        this.repositoriesService = repositoriesService;
        this.whiteListService = whiteListService;

        this.ioService = ioService;
        this.moduleService = moduleService;
        this.repositoryResolver = repositoryResolver;
        this.commentedOptionFactory = commentedOptionFactory;

        this.pomModelCache = pomModelCache;
    }

    public void save(final Path pathToPomXML,
                     final ProjectScreenModel model,
                     final DeploymentMode mode,
                     final String comment) {
        if (DeploymentMode.VALIDATED.equals(mode)) {
            checkRepositories(pathToPomXML,
                              model);
        }

        try {
            final KieModule module = moduleService.resolveModule(pathToPomXML);
            pomModelCache.invalidateCache(module);

            ioService.startBatch(Paths.convert(pathToPomXML).getFileSystem(),
                                 commentedOptionFactory.makeCommentedOption(comment));
            pomService.save(pathToPomXML,
                            model.getPOM(),
                            model.getPOMMetaData(),
                            comment);
            kModuleService.save(model.getPathToKModule(),
                                model.getKModule(),
                                model.getKModuleMetaData(),
                                comment);
            importsService.save(model.getPathToImports(),
                                model.getProjectImports(),
                                model.getProjectImportsMetaData(),
                                comment);
            repositoriesService.save(model.getPathToRepositories(),
                                     model.getRepositories(),
                                     comment);
            whiteListService.save(model.getPathToWhiteList(),
                                  model.getWhiteList(),
                                  model.getWhiteListMetaData(),
                                  comment);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            ioService.endBatch();
        }
    }

    private void checkRepositories(final Path pathToPomXML,
                                   final ProjectScreenModel model) {
        // Check is the POM's GAV has been changed.
        final GAV gav = model.getPOM().getGav();
        final KieModule project = moduleService.resolveModule(pathToPomXML);
        if (gav.equals(project.getPom().getGav())) {
            return;
        }

        // If GAV is snapshot we can freely override the module
        if(gav.isSnapshot()) {
            return;
        }

        // Check is the Module's "proposed" GAV resolves to any pre-existing artifacts.
        // Use the Repositories in the model since the User may update the Repositories filter and save.
        final ModuleRepositories moduleRepositories = model.getRepositories();
        final Set<MavenRepositoryMetadata> repositories = repositoryResolver.getRepositoriesResolvingArtifact(gav,
                                                                                                              project,
                                                                                                              moduleRepositories.filterByIncluded());
        if (repositories.size() > 0) {
            throw new GAVAlreadyExistsException(gav,
                                                repositories);
        }
    }
}
