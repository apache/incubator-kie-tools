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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import org.guvnor.common.services.project.backend.server.POMServiceImpl;
import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.service.POMService;
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
import org.kie.workbench.common.screens.archetype.mgmt.backend.maven.BuildProjectCommand;
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
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
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

    protected static final String BASE_KIE_PROJECT_TEMPLATE_GAV = "org.kie.templates:base-kie-project:1.0.0-TEMPLATE";

    private static final Logger LOGGER = LoggerFactory.getLogger(ArchetypeServiceImpl.class);

    private static final String ALIAS_PARAM = "alias";

    private static final String DEFAULT_GROUP_ID = "org.kie.archetypes";
    private static final String LOCK_NAME = "archetype.lock";
    private static final int LAST_ACCESS_THRESHOLD = 1;
    private static final String TEMPLATE = "TEMPLATE";
    private static final String TEMPLATE_SUFFIX = "-" + TEMPLATE;

    private static final String REMOTE_ORIGIN_REF = "refs/remotes/origin/master";

    private static final String ORIGIN_KEY = "origin";
    private static final String SYSTEM = "system";

    private static final String KIE_TEMPLATES = "kie-wb-common-archetype-mgmt-templates";
    private static final String KIE_TEMPLATES_ZIP = "org/kie/" + KIE_TEMPLATES + "/" + KIE_TEMPLATES + ".zip";

    private IOService ioService;
    private RepositoryService repositoryService;
    private OrganizationalUnitService ouService;
    private Event<ArchetypeListUpdatedEvent> archetypeListUpdatedEvent;
    private ArchetypeConfigStorage archetypeConfigStorage;
    private PathUtil pathUtil;
    private ArchetypePreferencesManager archetypePreferencesManager;
    private KieModuleService moduleService;
    private POMService pomService;

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
                                final KieModuleService moduleService,
                                final POMService pomService) {
        this.ioService = ioService;
        this.repositoryService = repositoryService;
        this.ouService = ouService;
        this.archetypeListUpdatedEvent = archetypeListUpdatedEvent;
        this.archetypeConfigStorage = archetypeConfigStorage;
        this.pathUtil = pathUtil;
        this.archetypePreferencesManager = archetypePreferencesManager;
        this.moduleService = moduleService;
        this.pomService = pomService;
    }

    private static String composeSearchableElement(final Archetype element) {
        return element.getAlias().toLowerCase();
    }

    @PostConstruct
    void postConstruct() {
        maybeCreateArchetypesOU();

        if (isArchetypesOUAvailable()) {
            archetypePreferencesManager.initializeCustomPreferences();

            checkKieTemplates();

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
    public void add(final GAV archetypeGav) {
        checkNotNull("archetypeGav", archetypeGav);

        add(archetypeGav,
            copyGav(archetypeGav));
    }

    @Override
    public void add(final GAV archetypeGav,
                    final GAV templateGav) {
        checkNotNull("archetypeGav", archetypeGav);
        checkNotNull("templateGav", templateGav);

        appendTemplateSuffix(templateGav);

        checkArchetypeAlreadyAdded(templateGav);

        final Path workingDirectoryPath = createTempDirectory(templateGav.getArtifactId());

        final File workingDirectory = new File(workingDirectoryPath.toString());

        final FileSystemLock physicalLock = createLock(workingDirectory);

        try {
            physicalLock.lock();

            executeMaven(new ArchetypeGenerateCommand(workingDirectoryPath.toString(),
                                                      archetypeGav,
                                                      templateGav));

            checkModuleValid(workingDirectoryPath.resolve(templateGav.getArtifactId()));

            finishAddExternalArchetype(templateGav,
                                       workingDirectory);
        } catch (GitAPIException | MavenEmbedderException e) {
            LOGGER.error(String.format("Failed to add the archetype %s",
                                       templateGav),
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
                listFilteredArchetypes(matchNotInternalRepositories(),
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
                        matchNotInternalRepositories(),
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
        getRepositories(matchAllRepositories()).forEach(item -> {
            validateArchetype(item);
            final Archetype archetype = archetypeConfigStorage.loadArchetype(item.getAlias());
            if (archetype.isInternal().equals(Boolean.FALSE)) {
                archetypePreferencesManager.addArchetype(archetype.getAlias());
            }
        });
    }

    @Override
    public void validate(final String alias) {
        checkNotEmpty(ALIAS_PARAM, alias);

        final Repository repository = resolveRepository(alias);

        if (repository == null) {
            throw new IllegalStateException(String.format("Repository %s cannot be resolved.", alias));
        }

        validateArchetype(repository);

        archetypePreferencesManager.addArchetype(alias);

        archetypeListUpdatedEvent.fire(new ArchetypeListUpdatedEvent(ArchetypeListOperation.VALIDATE));
    }

    @Override
    public Repository getTemplateRepository(final String alias) {
        checkNotEmpty(ALIAS_PARAM, alias);

        final Repository repository = resolveRepository(alias);

        if (repository != null) {
            checkTemplateShouldBeValid(alias);

            return repository;
        }

        throw new IllegalStateException(String.format("Repository %s cannot be resolved.", alias));
    }

    @Override
    public Optional<Repository> getBaseKieTemplateRepository() {
        final Optional<Archetype> archetype = getBaseKieArchetype();

        if (!archetype.isPresent()) {
            LOGGER.warn("The base kie project template is not registered.");
            return Optional.empty();
        }

        if (archetype.get().getStatus() != ArchetypeStatus.VALID) {
            LOGGER.warn("The state of base kie project template is invalid.");
            return Optional.empty();
        }

        final String repositoryAlias = makeRepositoryAlias(BASE_KIE_PROJECT_TEMPLATE_GAV);

        return Optional.ofNullable(resolveRepository(repositoryAlias));
    }

    @Override
    public Optional<Archetype> getBaseKieArchetype() {
        final String repositoryAlias = makeRepositoryAlias(BASE_KIE_PROJECT_TEMPLATE_GAV);

        final Archetype archetype = archetypeConfigStorage.loadArchetype(repositoryAlias);

        if (archetype == null) {
            LOGGER.warn("The base kie project archetype is not registered.");
        }

        return Optional.ofNullable(archetype);
    }

    private void checkTemplateShouldBeValid(final String alias) {
        final Archetype archetype = archetypeConfigStorage.loadArchetype(alias);

        if (archetype.getStatus() != ArchetypeStatus.VALID) {
            throw new IllegalStateException(
                    String.format("Template repository %s is invalid, thus cannot be used.", alias));
        }
    }

    private GAV copyGav(final GAV original) {
        return new GAV(original.getGroupId(),
                       original.getArtifactId(),
                       original.getVersion());
    }

    void checkKieTemplates() {
        final URL zipResource = getClass().getClassLoader().getResource(KIE_TEMPLATES_ZIP);
        if (zipResource == null) {
            LOGGER.warn("Kie templates could not be found at: {}",
                        KIE_TEMPLATES_ZIP);
            return;
        }

        final Path kieTemplatesPath = createTempDirectory(KIE_TEMPLATES);

        final File kieTemplatesDirectory = new File(kieTemplatesPath.toString());

        final FileSystemLock physicalLock = createLock(kieTemplatesDirectory);

        try {
            physicalLock.lock();

            final boolean unzipSucceeded = unzipFile(zipResource,
                                                     kieTemplatesDirectory);

            if (unzipSucceeded) {
                try (final DirectoryStream<Path> directoryStream = ioService.newDirectoryStream(kieTemplatesPath,
                                                                                                Files::isDirectory)) {
                    directoryStream.forEach(path -> {
                        final org.uberfire.backend.vfs.Path templatePomPath =
                                pathUtil.convert(path.resolve(POMServiceImpl.POM_XML));
                        final GAV templateGav = pomService.load(templatePomPath).getGav();

                        addInternalTemplate(path,
                                            templateGav);
                    });
                }
            }
        } finally {
            physicalLock.unlock();
        }
    }

    private boolean unzipFile(final URL zipResource,
                              final File destinationDirectory) {
        try (final ZipInputStream zis = new ZipInputStream(zipResource.openStream())) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                byte[] buffer = new byte[1024];
                final File file = new File(destinationDirectory, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    try (final FileOutputStream fos = new FileOutputStream(file)) {
                        int read;
                        while ((read = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, read);
                        }
                    }
                }
            }
            return true;
        } catch (IOException e) {
            LOGGER.error(String.format("Failed to unzip the file %s",
                                       destinationDirectory),
                         e);
        }
        return false;
    }

    private void addInternalTemplate(final Path workingDirectoryPath,
                                     final GAV templateGav) {
        try {
            appendTemplateSuffix(templateGav);

            checkArchetypeAlreadyAdded(templateGav);

            final File workingDirectory = new File(workingDirectoryPath.toString());

            checkModuleValid(workingDirectoryPath.resolve(templateGav.getArtifactId()));

            finishAddArchetype(templateGav,
                               workingDirectory,
                               true);
        } catch (ArchetypeAlreadyExistsException e) {
            // It is ok, nothing to do here
        } catch (Exception e) {
            LOGGER.error(String.format("Failed to add internal template %s",
                                       templateGav.toString()),
                         e);
        }
    }

    private void appendTemplateSuffix(final GAV gav) {
        gav.setVersion(gav.getVersion() + TEMPLATE_SUFFIX);
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

            final Path targetDirectoryPath = unpackArchetype(repository);

            executeMaven(new BuildProjectCommand(targetDirectoryPath.toString()));

            updateArchetypeStatus(repository.getAlias(),
                                  ArchetypeStatus.VALID,
                                  null);

            final boolean onlyOneAvailable = getRepositories(matchNotInternalRepositories()).size() == 1;

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

    private void finishAddExternalArchetype(final GAV templateGav,
                                            final File workingDirectory)
            throws GitAPIException, MavenEmbedderException {
        final File repositoryDirectory = new File(workingDirectory,
                                                  templateGav.getArtifactId());

        final Archetype archetype = finishAddArchetype(templateGav,
                                                       repositoryDirectory,
                                                       false);

        archetypePreferencesManager.addArchetype(archetype.getAlias());

        archetypeListUpdatedEvent.fire(new ArchetypeListUpdatedEvent(ArchetypeListOperation.ADD));
    }

    private Archetype finishAddArchetype(final GAV templateGav,
                                         final File repositoryDirectory,
                                         final boolean internal)
            throws GitAPIException, MavenEmbedderException {
        final Repository repository = makeArchetypeAvailable(templateGav,
                                                             repositoryDirectory);

        final Archetype archetype = new Archetype(repository.getAlias(),
                                                  templateGav,
                                                  new Date(),
                                                  ArchetypeStatus.VALID,
                                                  internal);

        archetypeConfigStorage.saveArchetype(archetype);

        LOGGER.info("Archetype {} successfully added.", templateGav);

        return archetype;
    }

    private void updateArchetypeStatus(final String alias,
                                       final ArchetypeStatus status,
                                       final String message) {
        final Archetype archetype = archetypeConfigStorage.loadArchetype(alias);
        final Archetype updatedArchetype = new Archetype(archetype.getAlias(),
                                                         archetype.getGav(),
                                                         archetype.getCreatedDate(),
                                                         status,
                                                         message,
                                                         archetype.isInternal());
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

    private Collection<Repository> getRepositories(final Predicate<Repository> filter) {
        final OrganizationalUnit archetypesOU = resolveOU();

        if (archetypesOU != null) {
            return repositoryService.getAllRepositories(archetypesOU.getSpace())
                    .stream()
                    .filter(filter)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private Repository resolveRepository(final String alias) {
        final OrganizationalUnit archetypesOU = resolveOU();

        if (archetypesOU != null) {
            return repositoryService.getRepositoryFromSpace(archetypesOU.getSpace(),
                                                            alias);
        } else {
            final String errorMsg =
                    String.format("Cannot resolve the repository <%s> when there is no archetype space available.",
                                  alias);
            throw new IllegalStateException(errorMsg);
        }
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

    private List<Archetype> listFilteredArchetypes(final Predicate<Repository> repositoryPredicate,
                                                   final Predicate<Archetype> archetypePredicate) {
        return getRepositories(repositoryPredicate)
                .stream()
                .map(repository -> archetypeConfigStorage.loadArchetype(repository.getAlias()))
                .filter(archetypePredicate)
                .sorted(Comparator.comparing(Archetype::getAlias))
                .collect(Collectors.toList());
    }

    private void checkArchetypeAlreadyAdded(final GAV templateGav) {
        final List<Archetype> archetypes = new ArrayList<>(
                listFilteredArchetypes(matchAllRepositories(),
                                       ArchetypeListingPredicates.matchGav(templateGav)));

        if (!archetypes.isEmpty()) {
            throw new ArchetypeAlreadyExistsException();
        }
    }

    private Repository makeArchetypeAvailable(final GAV templateGav,
                                              final File repositoryDirectory)
            throws GitAPIException, MavenEmbedderException {
        createTemporaryGitRepository(repositoryDirectory);

        executeMaven(new BuildProjectCommand(repositoryDirectory.getAbsolutePath()));

        return createArchetypeRepository(templateGav,
                                         repositoryDirectory.toURI().toString());
    }

    private void maybeCreateArchetypesOU() {
        if (!isArchetypesOUAvailable()) {
            ouService.createOrganizationalUnit(ArchetypeConfigStorageImpl.ARCHETYPES_SPACE_NAME,
                                               DEFAULT_GROUP_ID);
            archetypeConfigStorage.setup();

            isSpaceSetup = true;
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

    private Predicate<Repository> matchInternalRepositories() {
        return repository -> archetypeConfigStorage.loadArchetype(repository.getAlias()).isInternal();
    }

    private Predicate<Repository> matchNotInternalRepositories() {
        return matchInternalRepositories().negate();
    }

    private Predicate<Repository> matchAllRepositories() {
        return repository -> true;
    }

    Path unpackArchetype(final Repository repository) {
        try {
            final Path targetDirectoryPath = createTempDirectory(TEMPLATE);

            final File targetDirectory = new File(targetDirectoryPath.toString());

            final File repositoryDirectory = resolveRepositoryDirectory(repository);

            cloneRepository(repositoryDirectory,
                            targetDirectory.getAbsoluteFile());

            return targetDirectoryPath;
        } catch (Exception e) {
            final String msg = String.format("Failed to unpack the repository %s",
                                             repository.getAlias());
            LOGGER.error(msg, e);
            throw new IllegalStateException(msg);
        }
    }

    Repository createArchetypeRepository(final GAV templateGav,
                                         final String repositoryUri) {
        final OrganizationalUnit archetypesOU = resolveOU();

        if (archetypesOU != null) {
            final Repository repository = repositoryService.createRepository(archetypesOU,
                                                                             GitRepository.SCHEME.toString(),
                                                                             makeRepositoryAlias(templateGav.toString()),
                                                                             createRepositoryConfig(repositoryUri));

            cleanUpOrigin(repository);

            return repository;
        } else {
            throw new IllegalStateException("Cannot create an archetype when there is no archetype space available.");
        }
    }

    String makeRepositoryAlias(final String gavString) {
        return gavString.replaceAll("[.:]", "-");
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