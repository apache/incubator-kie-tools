/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.examples.backend.server;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.SimpleFileVisitor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.EnvironmentParameters;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryCopier;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ExamplesMetaData;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.spaces.SpacesAPI;

import static org.guvnor.structure.repositories.EnvironmentParameters.SCHEME;
import static org.guvnor.structure.repositories.EnvironmentParameters.MIRROR;
import static org.guvnor.structure.server.config.ConfigType.REPOSITORY;

@Service
@ApplicationScoped
public class ExamplesServiceImpl implements ExamplesService {

    private static final Logger logger = LoggerFactory.getLogger(ExamplesServiceImpl.class);

    private static final String PROJECT_DESCRIPTON = "project.description";

    private static final String KIE_WB_PLAYGROUND_ZIP = "org/kie/kie-wb-playground/kie-wb-playground.zip";
    private final Set<Repository> clonedRepositories = new HashSet<>();
    private WorkspaceProjectService projectService;
    private IOService ioService;
    private ConfigurationFactory configurationFactory;
    private RepositoryFactory repositoryFactory;
    private KieModuleService moduleService;
    private RepositoryService repositoryService;
    private RepositoryCopier repositoryCopier;
    private OrganizationalUnitService ouService;
    private Event<NewProjectEvent> newProjectEvent;
    private MetadataService metadataService;
    private SpacesAPI spaces;
    private ExampleRepository playgroundRepository;
    private ProjectScreenService projectScreenService;

    public ExamplesServiceImpl() {
        //Zero-parameter Constructor for CDI proxies
    }

    @Inject
    public ExamplesServiceImpl(final @Named("ioStrategy") IOService ioService,
                               final ConfigurationFactory configurationFactory,
                               final RepositoryFactory repositoryFactory,
                               final KieModuleService moduleService,
                               final RepositoryService repositoryService,
                               final RepositoryCopier repositoryCopier,
                               final OrganizationalUnitService ouService,
                               final WorkspaceProjectService projectService,
                               final MetadataService metadataService,
                               final SpacesAPI spaces,
                               final Event<NewProjectEvent> newProjectEvent,
                               final ProjectScreenService projectScreenService) {
        this.ioService = ioService;
        this.configurationFactory = configurationFactory;
        this.repositoryFactory = repositoryFactory;
        this.moduleService = moduleService;
        this.repositoryService = repositoryService;
        this.repositoryCopier = repositoryCopier;
        this.ouService = ouService;
        this.projectService = projectService;
        this.metadataService = metadataService;
        this.spaces = spaces;
        this.newProjectEvent = newProjectEvent;
        this.projectScreenService = projectScreenService;
    }

    @PostConstruct
    public void initPlaygroundRepository() {
        try {
            String userDir = System.getProperty("user.dir");

            File playgroundDirectory = new File(userDir,
                                                ".kie-wb-playground");
            if (playgroundDirectory.exists()) {
                cleanPlaygroundDirectory(playgroundDirectory.toPath());
            }
            playgroundDirectory.mkdirs();

            URL resource = getClass().getClassLoader().getResource(KIE_WB_PLAYGROUND_ZIP);
            if (resource == null) {
                logger.warn("Playground repository jar not found on classpath.");
                return;
            }

            try (ZipInputStream inputStream = new ZipInputStream(resource.openStream())) {
                ZipEntry zipEntry = null;
                while ((zipEntry = inputStream.getNextEntry()) != null) {
                    byte[] buffer = new byte[1024];
                    File file = new File(playgroundDirectory,
                                         zipEntry.getName());
                    if (zipEntry.isDirectory()) {
                        file.mkdirs();
                    } else {
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            int read = -1;
                            while ((read = inputStream.read(buffer)) != -1) {
                                fos.write(buffer,
                                          0,
                                          read);
                            }
                        }
                    }
                }

                final Git git = Git.init().setBare(false).setDirectory(playgroundDirectory).call();
                git.add().addFilepattern(".").call();
                git.commit().setMessage("Initial commit").call();

                String repositoryUrl = resolveRepositoryUrl(playgroundDirectory.getAbsolutePath());
                playgroundRepository = new ExampleRepository(repositoryUrl);
            }
        } catch (java.io.IOException | GitAPIException e) {
            logger.error("Unable to initialize playground git repository. Only custom repository definition will be available in the Workbench.",
                         e);
        }
    }

    private void cleanPlaygroundDirectory(java.nio.file.Path playgroundDirectoryPath) throws java.io.IOException {
        java.nio.file.Files.walkFileTree(playgroundDirectoryPath,
                                         new SimpleFileVisitor<java.nio.file.Path>() {
                                             @Override
                                             public java.nio.file.FileVisitResult visitFile(java.nio.file.Path file,
                                                                                            java.nio.file.attribute.BasicFileAttributes attrs) throws java.io.IOException {
                                                 file.toFile().delete();
                                                 return java.nio.file.FileVisitResult.CONTINUE;
                                             }

                                             @Override
                                             public java.nio.file.FileVisitResult postVisitDirectory(java.nio.file.Path dir,
                                                                                                     java.io.IOException exc) throws java.io.IOException {
                                                 dir.toFile().delete();
                                                 return java.nio.file.FileVisitResult.CONTINUE;
                                             }
                                         });
    }

    String resolveRepositoryUrl(final String playgroundDirectoryPath) {
        if ("\\".equals(getFileSeparator())) {
            return "file:///" + playgroundDirectoryPath.replaceAll("\\\\",
                                                                   "/");
        } else {
            return "file://" + playgroundDirectoryPath;
        }
    }

    @Override
    public ExamplesMetaData getMetaData() {
        return new ExamplesMetaData(getPlaygroundRepository());
    }

    @Override
    public ExampleRepository getPlaygroundRepository() {
        return playgroundRepository;
    }

    @Override
    public Set<ExampleProject> getProjects(final ExampleRepository repository) {
        if (repository == null) {
            return Collections.emptySet();
        }
        final String repositoryURL = repository.getUrl();
        if (repositoryURL == null || repositoryURL.trim().isEmpty()) {
            return Collections.emptySet();
        }

        // Avoid cloning playground repository multiple times
        Repository gitRepository = resolveGitRepository(repository);

        if (gitRepository == null) {
            return Collections.emptySet();
        }

        final Set<Module> modules = moduleService.getAllModules(gitRepository.getBranch("master").get());
        return convert(modules);
    }

    Repository resolveGitRepository(final ExampleRepository exampleRepository) {
        if (exampleRepository.equals(playgroundRepository)) {
            return clonedRepositories.stream().filter(r -> exampleRepository.getUrl().equals(r.getEnvironment().get("origin"))).findFirst().orElseGet(() -> cloneRepository(exampleRepository.getUrl()));
        } else {
            return cloneRepository(exampleRepository.getUrl(),
                                   exampleRepository.getUserName(),
                                   exampleRepository.getPassword());
        }
    }

    private Repository cloneRepository(final String repositoryURL) {
        return cloneRepository(repositoryURL,
                               null,
                               null);
    }

    private Repository cloneRepository(final String repositoryURL,
                                       final String userName,
                                       final String password) {
        Repository repository = null;
        try {
            final String alias = getExampleAlias(repositoryURL);
            final Map<String, Object> env = new HashMap<String, Object>() {{
                put("origin",
                    repositoryURL);
                put(SCHEME,
                    GitRepository.SCHEME.toString());
                put("replaceIfExists",
                    true);
                put("username",
                    userName);
                put("password",
                    password);
                put(MIRROR,
                    false);
            }};

            final ConfigGroup repositoryConfig = configurationFactory.newConfigGroup(REPOSITORY,
                                                                                     "system",
                                                                                     alias,
                                                                                     "");
            for (final Map.Entry<String, Object> entry : env.entrySet()) {
                repositoryConfig.addConfigItem(configurationFactory.newConfigItem(entry.getKey(),
                                                                                  entry.getValue()));
            }

            repositoryConfig.addConfigItem(configurationFactory.newConfigItem(EnvironmentParameters.AVOID_INDEX,
                                                                              "true"));

            repositoryConfig.addConfigItem(configurationFactory.newConfigItem(EnvironmentParameters.SPACE,
                                                                              "system"));

            repository = repositoryFactory.newRepository(repositoryConfig);
            clonedRepositories.add(repository);
            return repository;
        } catch (final Exception e) {
            logger.error("Error during create repository",
                         e);
            throw new RuntimeException(e);
        }
    }

    String getExampleAlias(final String repositoryURL) {
        String alias = repositoryURL;
        alias = alias.substring(alias.lastIndexOf("/") + 1);
        final int lastDotIndex = alias.lastIndexOf('.');
        if (lastDotIndex > 0) {
            alias = alias.substring(0,
                                    lastDotIndex);
        }
        return "examples-" + alias;
    }

    String getFileSeparator() {
        return FileSystems.getDefault().getSeparator();
    }

    private Set<ExampleProject> convert(final Set<Module> modules) {
        return modules.stream()
                .map(p -> makeExampleProject(p))
                .collect(Collectors.toSet());
    }

    private ExampleProject makeExampleProject(final Module module) {
        final String description = readDescription(module);
        final List<String> tags = getTags(module);

        return new ExampleProject(module.getRootPath(),
                                  module.getModuleName(),
                                  description,
                                  tags);
    }

    private String readDescription(final Module module) {
        final Path root = module.getRootPath();
        final POM pom = module.getPom();
        final org.uberfire.java.nio.file.Path nioRoot = Paths.convert(root);
        final org.uberfire.java.nio.file.Path nioDescription = nioRoot.resolve(PROJECT_DESCRIPTON);
        String description = "Example '" + module.getModuleName() + "' module";

        if (ioService.exists(nioDescription)) {
            description = ioService.readAllString(nioDescription);
        } else if (pom != null
                && pom.getDescription() != null
                && !pom.getDescription().isEmpty()) {
            description = pom.getDescription();
        }

        if (description != null) {
            return description.replaceAll("[\\s]+",
                                          " ");
        }

        return description;
    }

    private List<String> getTags(final Module module) {
        List<String> tags = metadataService.getTags(module.getPomXMLPath());
        tags.sort(String::compareTo);
        return tags;
    }

    @Override
    public WorkspaceProjectContextChangeEvent setupExamples(final ExampleOrganizationalUnit exampleTargetOU,
                                                            final List<ExampleProject> exampleProjects) {
        PortablePreconditions.checkNotNull("exampleTargetOU",
                                           exampleTargetOU);
        PortablePreconditions.checkNotNull("exampleProjects",
                                           exampleProjects);
        PortablePreconditions.checkCondition("Must have at least one ExampleProject",
                                             exampleProjects.size() > 0);

        //Retrieve or create Organizational Unit
        final String targetOUName = exampleTargetOU.getName();
        final OrganizationalUnit targetOU = getOrganizationalUnit(targetOUName);

        WorkspaceProject firstExampleProject = null;

        for (final ExampleProject exampleProject : exampleProjects) {
            try {
                final Repository targetRepository = repositoryCopier.copy(targetOU,
                                                                          exampleProject.getName(),
                                                                          exampleProject.getRoot());

                // Signal creation of new Project (Creation of OU and Repository, if applicable,
                // are already handled in the corresponding services).
                WorkspaceProject project = projectService.resolveProject(targetRepository);
                project = renameIfNecessary(targetOU,
                                            project);
                newProjectEvent.fire(new NewProjectEvent(project));

                //Store first new example project
                if (firstExampleProject == null) {
                    firstExampleProject = project;
                }
            } catch (IOException ioe) {
                logger.error("Unable to create Example(s).",
                             ioe);
            }
        }

        return new WorkspaceProjectContextChangeEvent(firstExampleProject,
                                                      firstExampleProject.getMainModule());
    }

    private WorkspaceProject renameIfNecessary(final OrganizationalUnit ou,
                                               final WorkspaceProject project) {

        String name = project.getName();
        Collection<WorkspaceProject> projectsWithSameName = projectService.getAllWorkspaceProjectsByName(ou,
                                                                                                         name);

        if (projectsWithSameName.size() > 1) {
            name = this.projectService.createFreshProjectName(ou,
                                                              project.getName());
        }

        if (!name.equals(project.getName())) {
            final Path pomXMLPath = project.getMainModule().getPomXMLPath();
            final ProjectScreenModel model = projectScreenService.load(pomXMLPath);
            model.getPOM().setName(name);
            projectScreenService.save(pomXMLPath,
                                      model,
                                      "");
            return projectService.resolveProject(pomXMLPath);
        }

        return project;
    }

    private OrganizationalUnit getOrganizationalUnit(String targetOUName) {
        OrganizationalUnit targetOU = ouService.getOrganizationalUnit(targetOUName);
        if (targetOU == null) {
            targetOU = createOrganizationalUnit(targetOUName);
        }
        return targetOU;
    }

    private OrganizationalUnit createOrganizationalUnit(final String name) {
        final OrganizationalUnit ou = ouService.createOrganizationalUnit(name,
                                                                         "",
                                                                         "");
        return ou;
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

    // Test getters and setters
    Set<Repository> getClonedRepositories() {
        return clonedRepositories;
    }

    void setPlaygroundRepository(final ExampleRepository playgroundRepository) {
        this.playgroundRepository = playgroundRepository;
    }
}
