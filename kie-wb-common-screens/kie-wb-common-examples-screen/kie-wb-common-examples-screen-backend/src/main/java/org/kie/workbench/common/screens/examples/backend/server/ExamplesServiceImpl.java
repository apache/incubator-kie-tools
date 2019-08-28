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
import java.io.InputStream;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.config.RepositoryInfo;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryCopier;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.screens.examples.model.Credentials;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ExamplesMetaData;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.kie.workbench.common.screens.examples.validation.ImportProjectValidators;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.fs.jgit.FileSystemLock;
import org.uberfire.java.nio.fs.jgit.FileSystemLockManager;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;

@Service
@ApplicationScoped
public class ExamplesServiceImpl extends BaseProjectImportService implements ExamplesService {

    private static final Logger logger = LoggerFactory.getLogger(ExamplesServiceImpl.class);

    private static final int LAST_ACCESS_THRESHOLD = 10;
    private static final TimeUnit LAST_ACCESS_TIME_UNIT = TimeUnit.SECONDS;
    private static final String LOCK_NAME = "playground.lock";
    private static final String PREFIX = ".playground-";

    private static final String KIE_WB_PLAYGROUND_ZIP = "org/kie/kie-wb-playground/kie-wb-playground.zip";
    private static final String DONE_MARKER_NAME = ".done";
    private static final String DEFAULT_GROUP_ID = "org.kie.playground";
    private static final String PLAYGROUND_DIRECTORY = ".kie-wb-playground";
    private WorkspaceProjectService projectService;
    private RepositoryFactory repositoryFactory;
    private RepositoryCopier repositoryCopier;
    private OrganizationalUnitService ouService;
    private Event<NewProjectEvent> newProjectEvent;
    private final FileSystem systemFS;
    private ExampleRepository playgroundRepository;
    protected String md5;
    protected String playgroundSpaceName;
    protected File playgroundRootDirectory;

    @Inject
    public ExamplesServiceImpl(final @Named("ioStrategy") IOService ioService,
                               final RepositoryFactory repositoryFactory,
                               final KieModuleService moduleService,
                               final RepositoryCopier repositoryCopier,
                               final OrganizationalUnitService ouService,
                               final WorkspaceProjectService projectService,
                               final MetadataService metadataService,
                               final Event<NewProjectEvent> newProjectEvent,
                               final ProjectScreenService projectScreenService,
                               final ImportProjectValidators validators,
                               final SpaceConfigStorageRegistry spaceConfigStorageRegistry,
                               final @Named("systemFS") FileSystem systemFS) {

        super(ioService,
              metadataService,
              validators,
              moduleService,
              projectService,
              projectScreenService,
              spaceConfigStorageRegistry);

        this.ioService = ioService;
        this.repositoryFactory = repositoryFactory;
        this.moduleService = moduleService;
        this.repositoryCopier = repositoryCopier;
        this.ouService = ouService;
        this.projectService = projectService;
        this.metadataService = metadataService;
        this.newProjectEvent = newProjectEvent;
        this.projectScreenService = projectScreenService;
        this.validators = validators;
        this.systemFS = systemFS;
    }

    @PostConstruct
    public void initPlaygroundRepository() {

        URL resource = getClass().getClassLoader().getResource(KIE_WB_PLAYGROUND_ZIP);
        if (resource == null) {
            logger.warn("Playground repository jar not found on classpath.");
            return;
        }
        String userDir = System.getProperty("user.dir");
        md5 = calculateMD5(resource);

        playgroundRootDirectory = new File(userDir,
                                           PLAYGROUND_DIRECTORY);

        // .kie-wb-playground/md5number
        File playgroundDirectory = new File(playgroundRootDirectory, md5);
        File doneMarker = new File(playgroundDirectory, DONE_MARKER_NAME);

        this.playgroundSpaceName = PREFIX + md5;

        String repositoryUrl = resolveRepositoryUrl(playgroundDirectory.getAbsolutePath());

        if (!playgroundDirectory.exists()) {
            playgroundDirectory.mkdirs();
        }

        FileSystemLock physicalLock = createLock();

        try {

            physicalLock.lock();

            if (!doneMarker.exists()) {
                // unzip folder if is not uncompressed
                unzipPlayground(resource, playgroundDirectory);
                doneMarker.createNewFile();
            }

            if (!this.existSpace(playgroundSpaceName)) {

                // create space
                this.createPlaygroundHiddenSpace(md5);

                spaceConfigStorageRegistry.getBatch(playgroundSpaceName).run(spaceConfigStorageBatchContext -> {
                    // Delete old folders;
                    this.deleteOldPlaygrounds(md5);

                    // Mark for deletion old playground spaces
                    this.deleteOldHiddenSpaces(md5);

                    this.cloneRepository(repositoryUrl);
                    return null;
                });
            }
        } catch (Exception e) {
            String message = "Can't create examples playground";
            logger.error(message);
            throw new ImportExamplesException(message, e);
        } finally {
            physicalLock.unlock();
        }

        playgroundRepository = new ExampleRepository(repositoryUrl);
    }

    private FileSystemLock createLock() {
        return FileSystemLockManager.getInstance()
                .getFileSystemLock(playgroundRootDirectory, LOCK_NAME, LAST_ACCESS_TIME_UNIT, LAST_ACCESS_THRESHOLD);
    }

    private ExampleRepository unzipPlayground(URL resource, File playgroundDirectory) {

        try (ZipInputStream inputStream = new ZipInputStream(resource.openStream())) {
            ZipEntry zipEntry;
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
            return new ExampleRepository(repositoryUrl);
        } catch (java.io.IOException |
                GitAPIException e) {
            String message = "Unable to initialize playground git repository. Only custom repository definition will be available in the Workbench.";
            logger.error(message,
                         e);
            throw new ImportExamplesException(message, e);
        }
    }

    protected void deleteOldHiddenSpaces(String md5) {
        this.ouService.getAllOrganizationalUnits(false, this::isOldPlayground)
                .forEach(space -> this.ouService.removeOrganizationalUnit(space.getName()));
    }

    protected boolean isOldPlayground(OrganizationalUnit ou) {
        return ou.getName().startsWith(PREFIX) && !ou.getName().endsWith(md5);
    }

    protected void createPlaygroundHiddenSpace(String md5) {
        String spaceName = PREFIX + md5;
        this.ouService.createOrganizationalUnit(spaceName, DEFAULT_GROUP_ID);
    }

    protected void deleteOldPlaygrounds(String md5) {
        try {
            Files.list(this.playgroundRootDirectory.toPath())
                    .filter(p -> !p.toFile().getAbsolutePath().endsWith(md5))
                    .forEach(this::cleanPlaygroundDirectory);
        } catch (Exception e) {
            throw new ImportExamplesException("Can't delete old playgrounds", e);
        }
    }

    protected String calculateMD5(URL resource) {
        try (InputStream is = resource.openStream()) {
            return DigestUtils.md5Hex(is);
        } catch (Exception e) {
            throw new ImportExamplesException("Can't calculate md5 for playground.zip", e);
        }
    }

    private void cleanPlaygroundDirectory(java.nio.file.Path playgroundDirectoryPath) {
        try {
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
        } catch (Exception e) {
            throw new ImportExamplesException("Can't delete playground directory: " + playgroundDirectoryPath.toFile().getName(), e);
        }
    }

    protected String resolveRepositoryUrl(final String playgroundDirectoryPath) {
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
    protected Repository resolveGitRepository(final ExampleRepository exampleRepository) {

        return spaceConfigStorageRegistry.getBatch(this.playgroundSpaceName).run(spaceConfigStorageBatchContext -> {
            if (exampleRepository.equals(playgroundRepository)) {
                return this.ouService.getOrganizationalUnit(this.playgroundSpaceName)
                        .getRepositories()
                        .stream()
                        .findFirst()
                        .get();
            } else {
                Credentials credentials = exampleRepository.getCredentials();
                String username = null;
                String password = null;
                if (credentials != null) {
                    username = credentials.getUsername();
                    password = credentials.getPassword();
                }
                return cloneRepository(exampleRepository.getUrl(),
                                       username,
                                       password);
            }
        });
    }

    private Repository cloneRepository(final String repositoryURL) {
        return cloneRepository(repositoryURL,
                               null,
                               null);
    }

    private Repository cloneRepository(final String repositoryURL,
                                       final String userName,
                                       final String password) {
        try {
            final String alias = getRepositoryAlias(repositoryURL);
            final Map<String, Object> env = this.buildGitEnv(repositoryURL,
                                                             userName,
                                                             password,
                                                             false);

            final RepositoryInfo repositoryConfig = this.createConfigGroup(alias,
                                                                           env);

            Repository repository = repositoryFactory.newRepository(repositoryConfig);
            this.ouService.addRepository(this.ouService.getOrganizationalUnit(this.playgroundSpaceName), repository);
            return repository;
        } catch (final Exception e) {
            logger.error("Error during create repository",
                         e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getDefaultSpace() {
        return this.playgroundSpaceName;
    }

    @Override
    protected String getRepositoryAlias(final String repositoryURL) {
        return "examples-" + super.getRepositoryAlias(repositoryURL);
    }

    String getFileSeparator() {
        return FileSystems.getDefault().getSeparator();
    }

    @Override
    public WorkspaceProjectContextChangeEvent setupExamples(final ExampleOrganizationalUnit exampleTargetOU,
                                                            final List<ImportProject> importProjects) {
        PortablePreconditions.checkNotNull("exampleTargetOU",
                                           exampleTargetOU);
        PortablePreconditions.checkNotNull("exampleProjects",
                                           importProjects);
        PortablePreconditions.checkCondition("Must have at least one ExampleProject",
                                             importProjects.size() > 0);

        //Retrieve or create Organizational Unit
        final String targetOUName = exampleTargetOU.getName();
        final OrganizationalUnit targetOU = getOrganizationalUnit(targetOUName);
        return this.importProjects(targetOU,
                                   importProjects);
    }

    @Override
    public Set<ImportProject> getExampleProjects() {
        ExampleRepository repos = this.getPlaygroundRepository();
        return this.getProjects(repos);
    }

    protected OrganizationalUnit getOrganizationalUnit(String targetOUName) {
        OrganizationalUnit targetOU = ouService.getOrganizationalUnit(targetOUName);
        if (targetOU == null) {
            targetOU = createOrganizationalUnit(targetOUName);
        }
        return targetOU;
    }

    protected OrganizationalUnit createOrganizationalUnit(final String name) {
        final OrganizationalUnit ou = ouService.createOrganizationalUnit(name,
                                                                         "");
        return ou;
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public void dispose() {

    }

    void setPlaygroundRepository(final ExampleRepository playgroundRepository) {
        this.playgroundRepository = playgroundRepository;
    }

    protected WorkspaceProjectContextChangeEvent importProjects(OrganizationalUnit targetOU,
                                                                List<ImportProject> projects) {

        return spaceConfigStorageRegistry.getBatch(targetOU.getSpace().getName())
                .run(context -> {

                    WorkspaceProject firstExampleProject = null;

                    for (final ImportProject importProject : projects) {
                        try {
                            final Repository targetRepository = repositoryCopier.copy(targetOU,
                                                                                      "example-" + importProject.getName(),
                                                                                      importProject.getRoot());

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
                });
    }

    protected boolean existSpace(String space) {
        try {
            DirectoryStream<Path> spaces = Files.newDirectoryStream(getNiogitPath());
            return StreamSupport.stream(spaces.spliterator(), false)
                    .filter(s -> s.getFileName().toString().equalsIgnoreCase(space))
                    .findFirst()
                    .isPresent();
        } catch (Exception e) {
            throw new ImportExamplesException("Can't read spaces directory", e);
        }
    }

    protected java.nio.file.Path getNiogitPath() {
        final JGitPathImpl systemGitPath = (JGitPathImpl) systemFS.getPath("system");
        return systemGitPath.getFileSystem().getGit().getRepository().getDirectory().getParentFile().getParentFile().toPath();
    }
}
