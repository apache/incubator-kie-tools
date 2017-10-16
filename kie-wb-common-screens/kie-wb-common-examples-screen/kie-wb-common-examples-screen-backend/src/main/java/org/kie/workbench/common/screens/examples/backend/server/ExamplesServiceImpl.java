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
import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
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
import org.kie.workbench.common.screens.examples.model.ExampleTargetRepository;
import org.kie.workbench.common.screens.examples.model.ExamplesMetaData;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.FileVisitor;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.StandardCopyOption;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.rpc.SessionInfo;

import static org.guvnor.structure.repositories.EnvironmentParameters.SCHEME;
import static org.guvnor.structure.server.config.ConfigType.REPOSITORY;

@Service
@ApplicationScoped
public class ExamplesServiceImpl implements ExamplesService {

    private static final Logger logger = LoggerFactory.getLogger(ExamplesServiceImpl.class);

    private static final String PROJECT_DESCRIPTON = "project.description";

    private static final String KIE_WB_PLAYGROUND_ZIP = "org/kie/kie-wb-playground/kie-wb-playground.zip";
    private final Set<Repository> clonedRepositories = new HashSet<Repository>();
    private IOService ioService;
    private ConfigurationFactory configurationFactory;
    private RepositoryFactory repositoryFactory;
    private KieProjectService projectService;
    private RepositoryService repositoryService;
    private OrganizationalUnitService ouService;
    private Event<NewProjectEvent> newProjectEvent;
    private SafeSessionInfo sessionInfo;
    private MetadataService metadataService;
    private ExampleRepository playgroundRepository;

    public ExamplesServiceImpl() {
        //Zero-parameter Constructor for CDI proxies
    }

    @Inject
    public ExamplesServiceImpl(final @Named("ioStrategy") IOService ioService,
                               final ConfigurationFactory configurationFactory,
                               final RepositoryFactory repositoryFactory,
                               final KieProjectService projectService,
                               final RepositoryService repositoryService,
                               final OrganizationalUnitService ouService,
                               final MetadataService metadataService,
                               final Event<NewProjectEvent> newProjectEvent,
                               final SessionInfo sessionInfo) {
        this.ioService = ioService;
        this.configurationFactory = configurationFactory;
        this.repositoryFactory = repositoryFactory;
        this.projectService = projectService;
        this.repositoryService = repositoryService;
        this.ouService = ouService;
        this.metadataService = metadataService;
        this.newProjectEvent = newProjectEvent;
        this.sessionInfo = new SafeSessionInfo(sessionInfo);
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
        return new ExamplesMetaData(getPlaygroundRepository(),
                                    getExampleOrganizationalUnits());
    }

    @Override
    public ExampleRepository getPlaygroundRepository() {
        return playgroundRepository;
    }

    Set<ExampleOrganizationalUnit> getExampleOrganizationalUnits() {
        final Collection<OrganizationalUnit> organizationalUnits = ouService.getOrganizationalUnits();
        final Set<ExampleOrganizationalUnit> exampleOrganizationalUnits = new HashSet<ExampleOrganizationalUnit>();
        for (OrganizationalUnit ou : organizationalUnits) {
            exampleOrganizationalUnits.add(new ExampleOrganizationalUnit(ou.getName()));
        }
        return exampleOrganizationalUnits;
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

        final Set<Project> projects = projectService.getProjects(gitRepository,
                                                                 "master");
        return convert(projects);
    }

    Repository resolveGitRepository(final ExampleRepository exampleRepository) {
        if (exampleRepository.equals(playgroundRepository)) {
            return clonedRepositories.stream().filter(r -> exampleRepository.getUrl().equals(r.getEnvironment().get("origin"))).findFirst().orElseGet(() -> cloneRepository(exampleRepository.getUrl()));
        } else {
            return cloneRepository(exampleRepository.getUrl());
        }
    }

    private Repository cloneRepository(final String repositoryURL) {
        Repository repository = null;
        try {
            final String alias = getExampleAlias(repositoryURL);
            final Map<String, Object> env = new HashMap<String, Object>() {{
                put("origin",
                    repositoryURL);
                put(SCHEME,
                    "git");
                put("replaceIfExists",
                    true);
            }};

            final ConfigGroup repositoryConfig = configurationFactory.newConfigGroup(REPOSITORY,
                                                                                     alias,
                                                                                     "");
            for (final Map.Entry<String, Object> entry : env.entrySet()) {
                repositoryConfig.addConfigItem(configurationFactory.newConfigItem(entry.getKey(),
                                                                                  entry.getValue()));
            }

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

    private Set<ExampleProject> convert(final Set<Project> projects) {
        return projects.stream()
                .map(p -> new ExampleProject(p.getRootPath(),
                                             p.getProjectName(),
                                             readDescription(p),
                                             getTags(p)))
                .collect(Collectors.toSet());
    }

    private String readDescription(final Project project) {
        final Path root = project.getRootPath();
        final POM pom = project.getPom();
        final org.uberfire.java.nio.file.Path nioRoot = Paths.convert(root);
        final org.uberfire.java.nio.file.Path nioDescription = nioRoot.resolve(PROJECT_DESCRIPTON);
        String description = "Example '" + project.getProjectName() + "' project";

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

    private List<String> getTags(final Project project) {
        List<String> tags = metadataService.getTags(project.getPomXMLPath());
        tags.sort((t1, t2) -> t1.compareTo(t2));
        return tags;
    }

    @Override
    public boolean validateRepositoryName(final String name) {
        return repositoryService.validateRepositoryName(name);
    }

    @Override
    public ProjectContextChangeEvent setupExamples(final ExampleOrganizationalUnit exampleTargetOU,
                                                   final ExampleTargetRepository exampleTarget,
                                                   final String branch,
                                                   final List<ExampleProject> exampleProjects) {
        PortablePreconditions.checkNotNull("exampleTargetOU",
                                           exampleTargetOU);
        PortablePreconditions.checkNotNull("exampleTarget",
                                           exampleTarget);
        PortablePreconditions.checkNotNull("branch",
                                           branch);
        PortablePreconditions.checkNotNull("exampleProjects",
                                           exampleProjects);
        PortablePreconditions.checkCondition("Must have at least one ExampleProject",
                                             exampleProjects.size() > 0);

        //Retrieve or create Organizational Unit
        final String targetOUName = exampleTargetOU.getName();
        OrganizationalUnit targetOU = ouService.getOrganizationalUnit(targetOUName);
        if (targetOU == null) {
            targetOU = createOrganizationalUnit(targetOUName);
        }

        //Retrieve or create target Repository
        final String targetRepositoryAlias = exampleTarget.getAlias();
        Repository targetRepository = repositoryService.getRepository(targetRepositoryAlias);
        if (targetRepository == null) {
            targetRepository = createTargetRepository(targetOU,
                                                      targetRepositoryAlias);
        }

        final Path targetRepositoryRoot = targetRepository.getBranchRoot(branch);
        final org.uberfire.java.nio.file.Path nioTargetRepositoryRoot = Paths.convert(targetRepositoryRoot);
        KieProject firstExampleProject = null;

        try {
            ioService.startBatch(nioTargetRepositoryRoot.getFileSystem());
            for (ExampleProject exampleProject : exampleProjects) {
                final Path exampleProjectRoot = exampleProject.getRoot();
                final org.uberfire.java.nio.file.Path nioExampleProjectRoot = Paths.convert(exampleProjectRoot);
                final org.uberfire.java.nio.file.Path nioTargetProjectRoot = nioTargetRepositoryRoot.resolve(exampleProject.getName());

                final RecursiveCopier copier = new RecursiveCopier(nioExampleProjectRoot,
                                                                   nioTargetProjectRoot);
                Files.walkFileTree(nioExampleProjectRoot,
                                   copier);

                // Signal creation of new Project (Creation of OU and Repository, if applicable,
                // are already handled in the corresponding services).
                final Path targetProjectRoot = Paths.convert(nioTargetProjectRoot);
                final KieProject project = projectService.resolveProject(targetProjectRoot);
                newProjectEvent.fire(new NewProjectEvent(project,
                                                         sessionInfo.getId(),
                                                         sessionInfo.getIdentity().getIdentifier()));

                //Store first new example project
                if (firstExampleProject == null) {
                    firstExampleProject = project;
                }
            }
        } catch (IOException ioe) {
            logger.error("Unable to create Example(s).",
                         ioe);
        } finally {
            ioService.endBatch();
        }
        return new ProjectContextChangeEvent(targetOU,
                                             targetRepository,
                                             targetRepository.getDefaultBranch(),
                                             firstExampleProject);
    }

    private OrganizationalUnit createOrganizationalUnit(final String name) {
        final OrganizationalUnit ou = ouService.createOrganizationalUnit(name,
                                                                         "",
                                                                         "");
        return ou;
    }

    private Repository createTargetRepository(final OrganizationalUnit ou,
                                              final String alias) {
        final RepositoryEnvironmentConfigurations configuration = new RepositoryEnvironmentConfigurations();
        configuration.setManaged(false);
        final Repository repository = repositoryService.createRepository(ou,
                                                                         GitRepository.SCHEME,
                                                                         alias,
                                                                         configuration);
        return repository;
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public void dispose() {
        for (Repository repository : clonedRepositories) {
            try {
                ioService.delete(Paths.convert(repository.getRoot()).getFileSystem().getPath(null));
            } catch (Exception e) {
                logger.warn("Unable to remove transient Repository '" + repository.getAlias() + "'.",
                            e);
            }
        }
    }

    static class RecursiveCopier implements FileVisitor<org.uberfire.java.nio.file.Path> {

        private final org.uberfire.java.nio.file.Path source;
        private final org.uberfire.java.nio.file.Path target;

        RecursiveCopier(final org.uberfire.java.nio.file.Path source,
                        final org.uberfire.java.nio.file.Path target) {
            this.source = source;
            this.target = target;
        }

        @Override
        public FileVisitResult preVisitDirectory(final org.uberfire.java.nio.file.Path src,
                                                 final BasicFileAttributes attrs) {
            final org.uberfire.java.nio.file.Path tgt = target.resolve(source.relativize(src));
            try {
                Files.copy(src,
                           tgt,
                           StandardCopyOption.REPLACE_EXISTING);
            } catch (FileAlreadyExistsException x) {
                //Swallow
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final org.uberfire.java.nio.file.Path file,
                                         final BasicFileAttributes attrs) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final org.uberfire.java.nio.file.Path dir,
                                                  final IOException exc) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(final org.uberfire.java.nio.file.Path file,
                                               final IOException exc) {
            return FileVisitResult.CONTINUE;
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
