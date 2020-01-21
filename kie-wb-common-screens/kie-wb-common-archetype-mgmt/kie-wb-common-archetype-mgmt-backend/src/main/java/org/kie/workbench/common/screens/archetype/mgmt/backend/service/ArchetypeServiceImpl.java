/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
 */

package org.kie.workbench.common.screens.archetype.mgmt.backend.service;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.execution.MavenExecutionResult;
import org.appformer.maven.integration.embedder.MavenEmbedderException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.structure.organizationalunit.NewOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.archetype.mgmt.backend.config.ArchetypeConfigStorage;
import org.kie.workbench.common.screens.archetype.mgmt.backend.config.ArchetypeConfigStorageImpl;
import org.kie.workbench.common.screens.archetype.mgmt.backend.maven.AbstractMavenCommand;
import org.kie.workbench.common.screens.archetype.mgmt.backend.maven.ArchetypeGenerateCommand;
import org.kie.workbench.common.screens.archetype.mgmt.backend.maven.ExecuteGoalsCommand;
import org.kie.workbench.common.screens.archetype.mgmt.backend.preference.ArchetypePreferencesManager;
import org.kie.workbench.common.screens.archetype.mgmt.backend.util.ArchetypeListingPredicates;
import org.kie.workbench.common.screens.archetype.mgmt.shared.events.ArchetypeListUpdatedEvent;
import org.kie.workbench.common.screens.archetype.mgmt.shared.exceptions.ArchetypeAlreadyExistsException;
import org.kie.workbench.common.screens.archetype.mgmt.shared.exceptions.InvalidArchetypeException;
import org.kie.workbench.common.screens.archetype.mgmt.shared.exceptions.MavenExecutionException;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.Archetype;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.ArchetypeListOperation;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.ArchetypeStatus;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.PaginatedArchetypeList;
import org.kie.workbench.common.screens.archetype.mgmt.shared.services.ArchetypeService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardDeleteOption;
import org.uberfire.java.nio.fs.jgit.FileSystemLock;
import org.uberfire.java.nio.fs.jgit.FileSystemLockManager;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;

import static java.lang.Integer.min;
import static java.util.stream.Collectors.toMap;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Startup
@Service
@ApplicationScoped
public class ArchetypeServiceImpl implements ArchetypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArchetypeServiceImpl.class);

    private static final String ALIAS_PARAM = "alias";

    private static final String DEFAULT_GROUP_ID = "org.kie.archetypes";
    private static final String LOCK_NAME = "archetype.lock";
    private static final int LAST_ACCESS_THRESHOLD = 1;
    private static final String TEMPLATE = "TEMPLATE";
    private static final String TEMPLATE_SUFFIX = "-" + TEMPLATE;

    private static final String GIT = "git";
    private static final String GIT_FOLDER = "." + GIT;
    private static final String REMOTE_ORIGIN_REF = "refs/remotes/origin/master";

    private static final String ORIGIN_KEY = "origin";
    private static final String SYSTEM = "system";

    private IOService ioService;
    private RepositoryService repositoryService;
    private OrganizationalUnitService ouService;
    private Event<ArchetypeListUpdatedEvent> archetypeListUpdatedEvent;
    private ArchetypeConfigStorage archetypeConfigStorage;
    private PathUtil pathUtil;
    private ArchetypePreferencesManager archetypePreferencesManager;
    private KieModuleService moduleService;

    private boolean isSpaceSetup = false;

    public ArchetypeServiceImpl() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public ArchetypeServiceImpl(final @Named("ioStrategy") IOService ioService,
                                final RepositoryService repositoryService,
                                final OrganizationalUnitService ouService,
                                final Event<ArchetypeListUpdatedEvent> archetypeListUpdatedEvent,
                                final ArchetypeConfigStorage archetypeConfigStorage,
                                final PathUtil pathUtil,
                                final ArchetypePreferencesManager archetypePreferencesManager,
                                final KieModuleService moduleService) {
        this.ioService = ioService;
        this.repositoryService = repositoryService;
        this.ouService = ouService;
        this.archetypeListUpdatedEvent = archetypeListUpdatedEvent;
        this.archetypeConfigStorage = archetypeConfigStorage;
        this.pathUtil = pathUtil;
        this.archetypePreferencesManager = archetypePreferencesManager;
        this.moduleService = moduleService;
    }

    private static String composeSearchableElement(final Archetype element) {
        return element.getAlias().toLowerCase();
    }

    @PostConstruct
    void postConstruct() {
        if (isArchetypesOUAvailable()) {
            archetypePreferencesManager.initializeCustomPreferences();

            validateAll();
        }
    }

    public void onNewOrganizationalUnitEvent(final @Observes NewOrganizationalUnitEvent newOrganizationalUnitEvent) {
        final OrganizationalUnit newOU = newOrganizationalUnitEvent.getOrganizationalUnit();

        if (isArchetypesOUAvailable() && !newOU.getName().equals(ArchetypeConfigStorageImpl.ARCHETYPES_SPACE_NAME)) {
            archetypePreferencesManager.initializeCustomPreference(newOU.getIdentifier());
        }
    }

    @Override
    public void add(final GAV archetypeGAV) {
        checkNotNull("archetypeGAV", archetypeGAV);

        final GAV templateGAV = new GAV(archetypeGAV.getGroupId(),
                                        archetypeGAV.getArtifactId(),
                                        archetypeGAV.getVersion());
        this.add(archetypeGAV,
                 templateGAV);
    }

    @Override
    public void add(final GAV archetypeGAV,
                    final GAV templateGAV) {
        checkNotNull("archetypeGAV", archetypeGAV);
        checkNotNull("templateGAV", templateGAV);

        templateGAV.setVersion(templateGAV.getVersion() + TEMPLATE_SUFFIX);

        checkArchetypeAlreadyAdded(templateGAV);

        final Path workingDirectoryPath = createTempDirectory(templateGAV.getArtifactId());

        final File workingDirectory = new File(workingDirectoryPath.toString());

        final FileSystemLock physicalLock = createLock(workingDirectory);

        try {
            physicalLock.lock();

            executeMaven(new ArchetypeGenerateCommand(workingDirectoryPath.toString(),
                                                      archetypeGAV,
                                                      templateGAV));

            checkModuleValid(workingDirectoryPath.resolve(templateGAV.getArtifactId()));

            finishAddArchetype(templateGAV,
                               workingDirectory);
        } catch (GitAPIException | MavenEmbedderException e) {
            LOGGER.error(String.format("Failed to add the archetype %s",
                                       templateGAV),
                         e);
        } finally {
            physicalLock.unlock();
        }
    }

    @Override
    public PaginatedArchetypeList list(final Integer page,
                                       final Integer pageSize,
                                       final String filter) {
        checkNotNull("page", page);
        checkNotNull("pageSize", pageSize);

        final List<Archetype> archetypes =
                listFilteredArchetypes(
                        ArchetypeListingPredicates.matchSearchFilter(filter,
                                                                     ArchetypeServiceImpl::composeSearchableElement));

        return finishListArchetypes(archetypes,
                                    page,
                                    pageSize);
    }

    @Override
    public PaginatedArchetypeList list(final Integer page,
                                       final Integer pageSize,
                                       final String filter,
                                       final ArchetypeStatus status) {
        checkNotNull("page", page);
        checkNotNull("pageSize", pageSize);
        checkNotNull("status", status);

        final List<Archetype> archetypes =
                listFilteredArchetypes(
                        ArchetypeListingPredicates.matchSearchFilterAndStatus(filter,
                                                                              ArchetypeServiceImpl::composeSearchableElement,
                                                                              status));

        return finishListArchetypes(archetypes,
                                    page,
                                    pageSize);
    }

    @Override
    public void delete(final String alias) {
        checkNotEmpty(ALIAS_PARAM, alias);

        final OrganizationalUnit archetypesOU = resolveOU();

        if (archetypesOU != null) {
            repositoryService.removeRepository(archetypesOU.getSpace(),
                                               alias);

            archetypeConfigStorage.deleteArchetype(alias);

            archetypePreferencesManager.removeArchetype(alias);

            archetypeListUpdatedEvent.fire(new ArchetypeListUpdatedEvent(ArchetypeListOperation.DELETE));

            LOGGER.info("Archetype {} successfully deleted.", alias);
        } else {
            throw new IllegalStateException("Cannot delete an archetype when there is no archetype space available.");
        }
    }

    @Override
    public void validateAll() {
        getRepositories().forEach(item -> {
            validateArchetype(item);
            archetypePreferencesManager.addArchetype(item.getAlias());
        });
    }

    @Override
    public void validate(final String alias) {
        checkNotEmpty(ALIAS_PARAM, alias);

        validateArchetype(resolveRepository(alias));

        archetypePreferencesManager.addArchetype(alias);

        archetypeListUpdatedEvent.fire(new ArchetypeListUpdatedEvent(ArchetypeListOperation.VALIDATE));
    }

    @Override
    public Repository getTemplateRepository(final String alias) {
        checkNotEmpty(ALIAS_PARAM, alias);

        return resolveRepository(alias);
    }

    private PaginatedArchetypeList finishListArchetypes(final List<Archetype> archetypes,
                                                        final int page,
                                                        final int pageSize) {
        final List<Archetype> paginatedArchetypes = paginateArchetypes(archetypes,
                                                                       page,
                                                                       pageSize);

        return new PaginatedArchetypeList(paginatedArchetypes,
                                          page,
                                          pageSize,
                                          archetypes.size());
    }

    private void checkModuleValid(final Path modulePath) {
        final boolean isValid = moduleService.resolveModule(pathUtil.convert(modulePath)) != null;

        if (!isValid) {
            throw new InvalidArchetypeException();
        }
    }

    private void validateArchetype(final Repository repository) {
        try {
            LOGGER.info("Validating the archetype: {}", repository.getAlias());

            final Path targetDirectoryPath = unpackArchetype(repository.getAlias());

            executeMaven(new ExecuteGoalsCommand(targetDirectoryPath.toString()));

            updateArchetypeStatus(repository.getAlias(),
                                  ArchetypeStatus.VALID,
                                  null);

            final boolean onlyOneAvailable = getRepositories().size() == 1;

            archetypePreferencesManager.enableArchetype(repository.getAlias(),
                                                        true,
                                                        onlyOneAvailable);

            if (onlyOneAvailable) {
                archetypePreferencesManager.setDefaultArchetype(repository.getAlias());
            }

            LOGGER.info("Archetype repository {} successfully validated.", repository.getAlias());
        } catch (Exception e) {
            updateArchetypeStatus(repository.getAlias(),
                                  ArchetypeStatus.INVALID,
                                  e.getMessage());

            archetypePreferencesManager.enableArchetype(repository.getAlias(),
                                                        false,
                                                        true);

            LOGGER.error(String.format("Failed to validate the repository %s",
                                       repository.getAlias()),
                         e);
        }
    }

    private void finishAddArchetype(final GAV templateGAV,
                                    final File workingDirectory)
            throws GitAPIException, MavenEmbedderException {
        final Repository repository = makeArchetypeAvailable(templateGAV,
                                                             workingDirectory);

        final Archetype archetype = new Archetype(repository.getAlias(),
                                                  templateGAV,
                                                  new Date(),
                                                  ArchetypeStatus.VALID);

        archetypeConfigStorage.saveArchetype(archetype);

        archetypePreferencesManager.addArchetype(archetype.getAlias());

        archetypeListUpdatedEvent.fire(new ArchetypeListUpdatedEvent(ArchetypeListOperation.ADD));

        LOGGER.info("Archetype {} successfully added.", templateGAV);
    }

    private void updateArchetypeStatus(final String alias,
                                       final ArchetypeStatus status,
                                       final String message) {
        final Archetype archetype = archetypeConfigStorage.loadArchetype(alias);
        final Archetype updatedArchetype = new Archetype(archetype.getAlias(),
                                                         archetype.getGav(),
                                                         archetype.getCreatedDate(),
                                                         status,
                                                         message);
        archetypeConfigStorage.saveArchetype(updatedArchetype);
    }

    private List<Archetype> paginateArchetypes(final List<Archetype> archetypes,
                                               final int page,
                                               final int pageSize) {
        if (pageSize == 0) {
            return archetypes;
        }

        final Map<Integer, List<Archetype>> map = IntStream.iterate(0,
                                                                    i -> i + pageSize)
                .limit((archetypes.size() + pageSize - 1) / pageSize)
                .boxed()
                .collect(toMap(i -> i / pageSize,
                               i -> archetypes.subList(i,
                                                       min(i + pageSize,
                                                           archetypes.size()))));

        return new ArrayList<>(map.getOrDefault(page, archetypes));
    }

    private Collection<Repository> getRepositories() {
        final OrganizationalUnit archetypesOU = resolveOU();

        if (archetypesOU != null) {
            return repositoryService.getAllRepositories(archetypesOU.getSpace());
        }

        return Collections.emptyList();
    }

    private Repository resolveRepository(final String alias) {
        final OrganizationalUnit archetypesOU = resolveOU();

        String errorMsg;
        if (archetypesOU != null) {
            final Repository repository = repositoryService.getRepositoryFromSpace(archetypesOU.getSpace(),
                                                                                   alias);
            if (repository != null) {
                return repository;
            }
            errorMsg = String.format("Cannot resolve the repository <%s>.", alias);
        } else {
            errorMsg = String.format("Cannot resolve the repository <%s> when there is no archetype space available.", alias);
        }

        throw new IllegalStateException(errorMsg);
    }

    private OrganizationalUnit resolveOU() {
        final OrganizationalUnit archetypesOU =
                ouService.getOrganizationalUnit(ArchetypeConfigStorageImpl.ARCHETYPES_SPACE_NAME);

        if (archetypesOU != null && !isSpaceSetup) {
            archetypeConfigStorage.setup();
            isSpaceSetup = true;
        }

        return archetypesOU;
    }

    private Branch resolveDefaultBranch(final Repository repository) {
        return repository.getDefaultBranch()
                .orElseThrow(() -> new IllegalStateException("There is no default branch for  " + repository.getAlias()));
    }

    private File resolveRepositoryDirectory(final Repository repository) {
        final org.uberfire.java.nio.fs.jgit.util.Git git = getGitFromRepository(repository);

        return git.getRepository().getDirectory();
    }

    private List<Archetype> listFilteredArchetypes(final Predicate<Archetype> predicate) {
        return getRepositories()
                .stream()
                .map(repository -> archetypeConfigStorage.loadArchetype(repository.getAlias()))
                .filter(predicate)
                .sorted(Comparator.comparing(Archetype::getAlias))
                .collect(Collectors.toList());
    }

    private void checkArchetypeAlreadyAdded(final GAV templateGAV) {
        final List<Archetype> archetypes = listFilteredArchetypes(ArchetypeListingPredicates.matchAll())
                .stream()
                .filter(archetype -> archetype.getGav().equals(templateGAV))
                .collect(Collectors.toList());

        if (!archetypes.isEmpty()) {
            throw new ArchetypeAlreadyExistsException();
        }
    }

    private Repository makeArchetypeAvailable(final GAV templateGAV,
                                              final File workingDirectory)
            throws GitAPIException, MavenEmbedderException {
        final File repositoryDirectory = new File(workingDirectory,
                                                  templateGAV.getArtifactId());

        createTemporaryGitRepository(repositoryDirectory);

        final String targetDirectory = workingDirectory.getAbsolutePath()
                + FileSystems.getDefault().getSeparator()
                + templateGAV.getArtifactId();

        executeMaven(new ExecuteGoalsCommand(targetDirectory));

        maybeCreateArchetypesOU();

        return createArchetypeRepository(templateGAV,
                                         repositoryDirectory.toURI().toString());
    }

    private void maybeCreateArchetypesOU() {
        if (!isArchetypesOUAvailable()) {
            ouService.createOrganizationalUnit(ArchetypeConfigStorageImpl.ARCHETYPES_SPACE_NAME,
                                               DEFAULT_GROUP_ID);
            archetypeConfigStorage.setup();

            archetypePreferencesManager.initializeCustomPreferences();
        }
    }

    private RepositoryEnvironmentConfigurations createRepositoryConfig(final String repositoryUri) {
        final RepositoryEnvironmentConfigurations configurations = new RepositoryEnvironmentConfigurations();

        configurations.setInit(false);
        configurations.setOrigin(repositoryUri);
        configurations.setMirror(false);
        configurations.setAvoidIndex(true);

        return configurations;
    }

    private void cleanUpOrigin(final Repository repository) {
        final Branch defaultBranch = resolveDefaultBranch(repository);

        org.uberfire.java.nio.fs.jgit.util.Git git = getGitFromBranch(defaultBranch);

        git.removeRemote(ORIGIN_KEY,
                         REMOTE_ORIGIN_REF);
    }

    private boolean isArchetypesOUAvailable() {
        final Predicate<OrganizationalUnit> archetypesFilter =
                ou -> ou.getName().equals(ArchetypeConfigStorageImpl.ARCHETYPES_SPACE_NAME);

        return !ouService.getAllOrganizationalUnits(false,
                                                    archetypesFilter).isEmpty();
    }

    Path unpackArchetype(final String alias) {
        final Repository repository = resolveRepository(alias);

        try {
            final Path targetDirectoryPath = createTempDirectory(TEMPLATE);

            final File targetDirectory = new File(targetDirectoryPath.toString());

            final File repositoryDirectory = resolveRepositoryDirectory(repository);

            cloneRepository(repositoryDirectory,
                            targetDirectory.getAbsoluteFile());

            ioService.deleteIfExists(targetDirectoryPath.resolve(GIT_FOLDER),
                                     StandardDeleteOption.NON_EMPTY_DIRECTORIES);

            return targetDirectoryPath;
        } catch (Exception e) {
            final String msg = String.format("Failed to unpack the repository %s",
                                             repository.getAlias());
            LOGGER.error(msg, e);
            throw new IllegalStateException(msg);
        }
    }

    Repository createArchetypeRepository(final GAV templateGAV,
                                         final String repositoryUri) {
        final OrganizationalUnit archetypesOU = resolveOU();

        if (archetypesOU != null) {
            final Repository repository = repositoryService.createRepository(archetypesOU,
                                                                             GitRepository.SCHEME.toString(),
                                                                             templateGAV.toString(),
                                                                             createRepositoryConfig(repositoryUri));

            cleanUpOrigin(repository);

            return repository;
        } else {
            throw new IllegalStateException("Cannot create an archetype when there is no archetype space available.");
        }
    }

    void throwMavenExecutionException(final List<Throwable> exceptions) {
        if (exceptions.isEmpty()) {
            throw new MavenExecutionException();
        } else {
            final String message = exceptions.stream()
                    .map(Throwable::getMessage)
                    .collect(Collectors.joining(","));
            throw new MavenExecutionException(message);
        }
    }

    void executeMaven(final AbstractMavenCommand command) throws MavenEmbedderException {
        final MavenExecutionResult result = command.execute();

        if (result.hasExceptions()) {
            throwMavenExecutionException(result.getExceptions());
        }
    }

    FileSystemLock createLock(final File directory) {
        return FileSystemLockManager.getInstance().getFileSystemLock(directory,
                                                                     LOCK_NAME,
                                                                     TimeUnit.SECONDS,
                                                                     LAST_ACCESS_THRESHOLD);
    }

    void cloneRepository(final File repositoryDirectory,
                         final File destinationDirectory) throws GitAPIException {
        Git.cloneRepository()
                .setURI(repositoryDirectory.toURI().toString())
                .setDirectory(destinationDirectory)
                .call();
    }

    Path createTempDirectory(final String path) {
        return Files.createTempDirectory(path);
    }

    void createTemporaryGitRepository(final File repositoryDirectory) throws GitAPIException {
        final Git git = Git.init().setBare(false).setDirectory(repositoryDirectory).call();
        git.add().addFilepattern(".").call();
        git.commit().setMessage("Initial commit").setAuthor(SYSTEM, SYSTEM).call();
    }

    org.uberfire.java.nio.fs.jgit.util.Git getGitFromBranch(final Branch branch) {
        return ((JGitPathImpl) pathUtil.convert(branch.getPath())).getFileSystem().getGit();
    }

    org.uberfire.java.nio.fs.jgit.util.Git getGitFromRepository(final Repository repository) {
        return getGitFromBranch(resolveDefaultBranch(repository));
    }
}