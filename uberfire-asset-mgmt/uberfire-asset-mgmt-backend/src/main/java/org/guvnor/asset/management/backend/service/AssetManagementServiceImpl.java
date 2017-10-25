/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.asset.management.model.ConfigureRepositoryEvent;
import org.guvnor.asset.management.service.AssetManagementService;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

@Service
@ApplicationScoped
public class AssetManagementServiceImpl implements AssetManagementService {

    private Instance<ProjectService<?>> projectService;

    private IOService ioService;

    private POMService pomService;

    private RepositoryService repositoryService;

    private Event<NewBranchEvent> newBranchEvent;

    private Event<ConfigureRepositoryEvent> configureRepositoryEvent;

    public AssetManagementServiceImpl() {
        //Zero-parameter constructor for CDI proxies
    }

    @Inject
    public AssetManagementServiceImpl(final Event<NewBranchEvent> newBranchEvent,
                                      final Event<ConfigureRepositoryEvent> configureRepositoryEvent,
                                      final POMService pomService,
                                      @Named("ioStrategy") final IOService ioService,
                                      final RepositoryService repositoryService,
                                      final Instance<ProjectService<?>> projectService) {
        this.ioService = ioService;
        this.newBranchEvent = newBranchEvent;
        this.configureRepositoryEvent = configureRepositoryEvent;
        this.pomService = pomService;
        this.repositoryService = repositoryService;
        this.projectService = projectService;
    }

    @Override
    public void configureRepository(final String repository,
                                    final String sourceBranch,
                                    final String devBranch,
                                    final String releaseBranch,
                                    final String version) {

        String branchName = devBranch;
        if (version != null && !version.isEmpty()) {
            branchName = branchName + "-" + version;
        }
        // create development branch
        Path branchPath = ioService.get(URI.create("default://" + branchName + "@" + repository));
        Path branchOriginPath = ioService.get(URI.create("default://" + sourceBranch + "@" + repository));

        ioService.copy(branchOriginPath,
                       branchPath);

        // update development branch project
        Repository repo = repositoryService.getRepository(Paths.convert(branchPath));

        // update all pom.xml files of projects on the dev branch
        String devVersion = null;
        if (version == null) {
            devVersion = "1.0.0";
        } else if (!version.endsWith("-SNAPSHOT")) {
            devVersion = version.concat("-SNAPSHOT");
        } else {
            devVersion = version;
        }
        Set<Project> projects = getProjects(repo);

        for (Project project : projects) {

            POM pom = pomService.load(project.getPomXMLPath());
            pom.getGav().setVersion(devVersion);
            pomService.save(project.getPomXMLPath(),
                            pom,
                            null,
                            "Update project version on development branch");
        }

        newBranchEvent.fire(new NewBranchEvent(repository,
                                               branchName,
                                               Paths.convert(branchPath),
                                               System.currentTimeMillis()));

        // create release branch
        branchName = releaseBranch;
        if (version != null && !version.isEmpty()) {
            branchName = branchName + "-" + version;
        }
        branchPath = ioService.get(URI.create("default://" + branchName + "@" + repository));
        branchOriginPath = ioService.get(URI.create("default://" + sourceBranch + "@" + repository));

        ioService.copy(branchOriginPath,
                       branchPath);

        newBranchEvent.fire(new NewBranchEvent(repository,
                                               branchName,
                                               Paths.convert(branchPath),
                                               System.currentTimeMillis()));

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("RepositoryName",
                       repository);
        parameters.put("SourceBranchName",
                       sourceBranch);
        parameters.put("DevBranchName",
                       devBranch);
        parameters.put("RelBranchName",
                       releaseBranch);
        parameters.put("Version",
                       version);
        configureRepositoryEvent.fire(new ConfigureRepositoryEvent(parameters));
    }

    @Override
    public Set<Project> getProjects(final Repository repository,
                                    final String branch) {
        return projectService.get().getProjects(repository,
                                                branch);
    }

    private Set<Project> getProjects(final Repository repository) {
        final Set<Project> authorizedProjects = new HashSet<Project>();
        if (repository == null) {
            return authorizedProjects;
        }
        final Path repositoryRoot = Paths.convert(repository.getRoot());
        final DirectoryStream<Path> nioRepositoryPaths = ioService.newDirectoryStream(repositoryRoot);
        for (Path nioRepositoryPath : nioRepositoryPaths) {
            if (Files.isDirectory(nioRepositoryPath)) {
                final org.uberfire.backend.vfs.Path projectPath = Paths.convert(nioRepositoryPath);
                final Project project = projectService.get().resolveProject(projectPath);
                if (project != null) {
                    authorizedProjects.add(project);
                }
            }
        }
        return authorizedProjects;
    }
}
