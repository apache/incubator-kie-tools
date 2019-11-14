/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.kie.workbench.common.screens.examples.backend.server;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.structure.organizationalunit.config.RepositoryInfo;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.examples.model.Credentials;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.service.ImportService;
import org.kie.workbench.common.screens.examples.service.ProjectImportService;
import org.kie.workbench.common.screens.examples.validation.ImportProjectValidators;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;

@Service
@ApplicationScoped
public class ProjectImportServiceImpl extends BaseProjectImportService implements ProjectImportService {

    private Logger logger = LoggerFactory.getLogger(ProjectImportServiceImpl.class);
    private RepositoryFactory repositoryFactory;

    private final Set<Repository> clonedRepositories = new HashSet<>();

    @Inject
    public ProjectImportServiceImpl(final @Named("ioStrategy") IOService ioService,
                                    final MetadataService metadataService,
                                    final RepositoryFactory repositoryFactory,
                                    final KieModuleService moduleService,
                                    final ImportProjectValidators validators,
                                    final PathUtil pathUtil,
                                    final WorkspaceProjectService projectService,
                                    final ProjectScreenService projectScreenService,
                                    final Event<NewProjectEvent> newProjectEvent,
                                    final RepositoryService repoService,
                                    final SpaceConfigStorageRegistry spaceConfigStorageRegistry) {

        super(ioService,
              metadataService,
              validators,
              moduleService,
              projectService,
              projectScreenService,
              spaceConfigStorageRegistry,
              pathUtil,
              repoService,
              newProjectEvent);

        this.repositoryFactory = repositoryFactory;
    }

    @Override
    protected Repository resolveGitRepository(ExampleRepository repository) {

        try {
            String url = repository.getUrl();
            final String alias = getRepositoryAlias(url);
            Credentials credentials = repository.getCredentials();
            String username = null;
            String password = null;
            if (credentials != null) {
                username = credentials.getUsername();
                password = credentials.getPassword();
            }
            final Map<String, Object> env = this.buildGitEnv(url,
                                                             username,
                                                             password,
                                                             true);

            final RepositoryInfo repositoryConfig = createConfigGroup(alias,
                                                                      env);

            Repository repo = repositoryFactory.newRepository(repositoryConfig);
            clonedRepositories.add(repo);
            return repo;
        } catch (final Exception e) {
            logger.error("Error during create repository",
                         e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public void dispose() {
        for (final Repository repository : clonedRepositories) {
            try {
                if (repository.getDefaultBranch().isPresent()) {
                    ioService.delete(Paths.convert(repository.getDefaultBranch().get().getPath()).getFileSystem().getPath(null));
                }
            } catch (Exception e) {
                logger.warn("Unable to remove transient Repository '" + repository.getAlias() + "'.",
                            e);
            }
        }
    }

    @Override
    protected ImportProject makeExampleProject(final Module module,
                                               ExampleRepository repository) {
        final String description = readDescription(module);
        final List<String> tags = getTags(module);

        return new ImportProject(module.getRootPath(),
                                 module.getModuleName(),
                                 description,
                                 repository.getUrl(),
                                 tags,
                                 repository.getCredentials(),
                                 getBranches(getProjectRoot(module.getRootPath()),
                                             module.getRootPath()),
                                 true);
    }
}

