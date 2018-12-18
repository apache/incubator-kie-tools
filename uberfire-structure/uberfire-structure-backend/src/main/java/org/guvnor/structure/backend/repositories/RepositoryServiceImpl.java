/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.structure.backend.repositories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.GitMetadataStore;
import org.guvnor.structure.repositories.NewRepositoryEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryAlreadyExistsException;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfiguration;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryInfo;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.TextUtil;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.version.impl.PortableVersionRecord;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

import static org.guvnor.structure.repositories.EnvironmentParameters.SCHEME;
import static org.guvnor.structure.server.config.ConfigType.REPOSITORY;
import static org.uberfire.backend.server.util.Paths.convert;

@Service
@ApplicationScoped
public class RepositoryServiceImpl implements RepositoryService {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryServiceImpl.class);

    private static final int HISTORY_PAGE_SIZE = 10;

    private IOService ioService;

    private GitMetadataStore metadataStore;

    private ConfigurationService configurationService;

    private OrganizationalUnitService organizationalUnitService;

    private ConfigurationFactory configurationFactory;

    private RepositoryFactory repositoryFactory;

    private Event<NewRepositoryEvent> event;

    private Event<RepositoryRemovedEvent> repositoryRemovedEvent;

    private BackwardCompatibleUtil backward;

    private ConfiguredRepositories configuredRepositories;

    private AuthorizationManager authorizationManager;

    private User user;

    private SpacesAPI spacesAPI;

    public RepositoryServiceImpl() {
    }

    @Inject
    public RepositoryServiceImpl(@Named("ioStrategy") final IOService ioService,
                                 final GitMetadataStore metadataStore,
                                 final ConfigurationService configurationService,
                                 final OrganizationalUnitService organizationalUnitService,
                                 final ConfigurationFactory configurationFactory,
                                 final RepositoryFactory repositoryFactory,
                                 final Event<NewRepositoryEvent> event,
                                 final Event<RepositoryRemovedEvent> repositoryRemovedEvent,
                                 final BackwardCompatibleUtil backward,
                                 final ConfiguredRepositories configuredRepositories,
                                 final AuthorizationManager authorizationManager,
                                 final User user,
                                 final SpacesAPI spacesAPI) {
        this.ioService = ioService;
        this.metadataStore = metadataStore;
        this.configurationService = configurationService;
        this.organizationalUnitService = organizationalUnitService;
        this.configurationFactory = configurationFactory;
        this.repositoryFactory = repositoryFactory;
        this.event = event;
        this.repositoryRemovedEvent = repositoryRemovedEvent;
        this.backward = backward;
        this.configuredRepositories = configuredRepositories;
        this.authorizationManager = authorizationManager;
        this.user = user;
        this.spacesAPI = spacesAPI;
    }

    @Override
    public RepositoryInfo getRepositoryInfo(final Space space,
                                            final String alias) {
        Repository repo = getRepositoryFromSpace(space,
                                                 alias);

        return new RepositoryInfo(repo.getIdentifier(),
                                  alias,
                                  repo.getSpace().getName(),
                                  getRepositoryRootPath(repo),
                                  repo.getPublicURIs(),
                                  getRepositoryHistory(repo.getSpace(),
                                                       alias,
                                                       0,
                                                       HISTORY_PAGE_SIZE));
    }

    private Path getRepositoryRootPath(final Repository repo) {
        if (repo.getDefaultBranch().isPresent()) {
            return repo.getDefaultBranch().get().getPath();
        } else {
            return null;
        }
    }

    @Override
    public List<VersionRecord> getRepositoryHistory(final Space space,
                                                    final String alias,
                                                    final int startIndex) {
        return getRepositoryHistory(space,
                                    alias,
                                    startIndex,
                                    startIndex + HISTORY_PAGE_SIZE);
    }

    @Override
    public List<VersionRecord> getRepositoryHistory(final Space space,
                                                    final String alias,
                                                    int startIndex,
                                                    int endIndex) {
        final Repository repo = getRepositoryFromSpace(space,
                                                       alias);

        //This is a work-around for https://bugzilla.redhat.com/show_bug.cgi?id=1199215
        //org.kie.workbench.common.screens.contributors.backend.dataset.ContributorsManager is trying to
        //load a Repository's history for a Repository associated with an Organizational Unit before the
        //Repository has been setup.
        if (repo == null) {
            return Collections.EMPTY_LIST;
        }

        if (repo.getDefaultBranch().isPresent()) {
            throw new IllegalStateException("Repository should have at least one branch.");
        }

        final VersionAttributeView versionAttributeView = ioService.getFileAttributeView(convert(repo.getDefaultBranch().get().getPath()),
                                                                                         VersionAttributeView.class);
        final List<VersionRecord> records = versionAttributeView.readAttributes().history().records();

        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex < 0 || endIndex > records.size()) {
            endIndex = records.size();
        }
        if (startIndex >= records.size() || startIndex >= endIndex) {
            return Collections.emptyList();
        }

        Collections.reverse(records);

        final List<VersionRecord> result = new ArrayList<>(endIndex - startIndex);
        for (VersionRecord record : records.subList(startIndex,
                                                    endIndex)) {
            result.add(new PortableVersionRecord(record.id(),
                                                 record.author(),
                                                 record.email(),
                                                 record.comment(),
                                                 record.date(),
                                                 record.uri()));
        }

        return result;
    }

    @Override
    public Repository getRepositoryFromSpace(final Space space,
                                             final String alias) {
        return configuredRepositories.getRepositoryByRepositoryAlias(space,
                                                                     alias);
    }

    @Override
    public Repository getRepository(final Path root) {
        Space space = spacesAPI.resolveSpace(root.toURI()).orElseThrow(() -> new IllegalArgumentException("Cannot resolve space from given path: " + root));
        return configuredRepositories.getRepositoryByRootPath(space,
                                                              root);
    }

    @Override
    public Repository getRepository(final Space space,
                                    final Path root) {
        return configuredRepositories.getRepositoryByRootPath(space,
                                                              root);
    }

    @Override
    public String normalizeRepositoryName(String name) {
        return TextUtil.normalizeRepositoryName(name);
    }

    @Override
    public boolean validateRepositoryName(String name) {
        return name != null && !"".equals(name) && name.equals(normalizeRepositoryName(name));
    }

    @Override
    public Collection<Repository> getAllRepositories(final Space space) {
        return configuredRepositories.getAllConfiguredRepositories(space);
    }

    @Override
    public Collection<Repository> getAllRepositoriesFromAllUserSpaces() {
        List<Repository> allRepos = new ArrayList<>();

        for (Space space : organizationalUnitService.getAllUserSpaces()) {
            allRepos.addAll(configuredRepositories.getAllConfiguredRepositories(space));
        }

        return allRepos;
    }

    @Override
    public Collection<Repository> getRepositories(final Space space) {
        Collection<Repository> result = new ArrayList<>();
        for (Repository repository : configuredRepositories.getAllConfiguredRepositories(space)) {
            if (authorizationManager.authorize(repository, user)) {
                result.add(repository);
            }
        }
        return result;
    }

    @Override
    public Repository createRepository(final OrganizationalUnit organizationalUnit,
                                       final String scheme,
                                       final String alias,
                                       final RepositoryEnvironmentConfigurations repositoryEnvironmentConfigurations) throws RepositoryAlreadyExistsException {

        return createRepository(organizationalUnit,
                                scheme,
                                alias,
                                repositoryEnvironmentConfigurations,
                                organizationalUnit.getContributors());
    }

    @Override
    public Repository createRepository(final OrganizationalUnit organizationalUnit,
                                       final String scheme,
                                       final String alias,
                                       final RepositoryEnvironmentConfigurations repositoryEnvironmentConfigurations,
                                       final Collection<Contributor> contributors) throws RepositoryAlreadyExistsException {

        try {
            repositoryEnvironmentConfigurations.setSpace(organizationalUnit.getName());

            Space space = spacesAPI.getSpace(organizationalUnit.getName());
            String newAlias = createFreshRepositoryAlias(alias,
                                                         space);

            final Repository repository = createRepository(scheme,
                                                           newAlias,
                                                           new Space(organizationalUnit.getName()),
                                                           repositoryEnvironmentConfigurations,
                                                           contributors);
            if (organizationalUnit != null && repository != null) {
                organizationalUnitService.addRepository(organizationalUnit,
                                                        repository);
            }
            metadataStore.write(newAlias,
                                (String) repositoryEnvironmentConfigurations.getOrigin());
            return repository;
        } catch (final Exception e) {
            logger.error("Error during create repository",
                         e);
            throw ExceptionUtilities.handleException(e);
        }
    }

    protected String createFreshRepositoryAlias(final String alias,
                                                final Space space) {
        int index = 0;
        String suffix = "";
        while (this.getRepositoryFromSpace(space,
                                           alias + suffix) != null) {
            suffix = "-" + ++index;
        }

        return alias + suffix;
    }

    protected ConfigGroup findRepositoryConfig(final String alias,
                                               final String spaceName) {
        final Collection<ConfigGroup> groups = configurationService.getConfiguration(ConfigType.REPOSITORY,
                                                                                     spaceName);
        if (groups != null) {
            for (ConfigGroup groupConfig : groups) {
                if (groupConfig.getName().equals(alias)) {
                    return groupConfig;
                }
            }
        }
        return null;
    }

    @Override
    public void removeRepository(final Space space,
                                 final String alias) {
        final ConfigGroup thisRepositoryConfig = findRepositoryConfig(alias,
                                                                      space.getName());

        try {
            configurationService.startBatch();
            OrganizationalUnit orgUnit = Optional
                    .ofNullable(organizationalUnitService.getOrganizationalUnit(space.getName()))
                    .orElseThrow(() -> new IllegalArgumentException(String
                                                                            .format("The given space [%s] does not exist.",
                                                                                    space.getName())));
            doRemoveRepository(orgUnit,
                               alias,
                               thisRepositoryConfig,
                               repo -> repositoryRemovedEvent.fire(new RepositoryRemovedEvent(repo)));
        } catch (final Exception e) {
            logger.error("Error during remove repository",
                         e);
            throw new RuntimeException(e);
        } finally {
            configurationService.endBatch();
        }
    }

    @Override
    public void removeRepositories(final Space space,
                                   final Set<String> aliases) {
        try {
            configurationService.startBatch();
            OrganizationalUnit orgUnit = Optional
                    .ofNullable(organizationalUnitService.getOrganizationalUnit(space.getName()))
                    .orElseThrow(() -> new IllegalArgumentException(String.format("The given space [%s] does not exist.",
                                                                                  space.getName())));

            for (final String alias : aliases) {
                doRemoveRepository(orgUnit,
                                   alias,
                                   findRepositoryConfig(alias,
                                                        space.getName()),
                                   repo -> {
                                   });
            }
        } catch (final Exception e) {
            logger.error("Error while removing repositories",
                         e);
            throw new RuntimeException(e);
        } finally {
            configurationService.endBatch();
        }
    }

    private void doRemoveRepository(final OrganizationalUnit orgUnit,
                                    final String alias,
                                    final ConfigGroup thisRepositoryConfig,
                                    final Consumer<Repository> notification) throws Exception {
        if (thisRepositoryConfig != null) {
            configurationService.removeConfiguration(thisRepositoryConfig);
        }

        final Repository repo = configuredRepositories.remove(orgUnit.getSpace(),
                                                              alias);
        if (repo != null) {
            notification.accept(repo);

            Branch defaultBranch = repo.getDefaultBranch().orElseThrow(() -> new IllegalStateException("Repository should have at least one branch."));
            ioService.delete(convert(defaultBranch.getPath()).getFileSystem().getPath(null));
        }

        //Remove reference to Repository from Organizational Units
        for (Repository repository : orgUnit.getRepositories()) {
            if (repository.getAlias().equals(alias)) {
                organizationalUnitService.removeRepository(orgUnit,
                                                           repository);
                metadataStore.delete(alias);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addGroup(final Repository repository,
                         final String group) {
        final ConfigGroup thisRepositoryConfig = findRepositoryConfig(repository.getAlias(),
                                                                      repository.getSpace().getName());

        if (thisRepositoryConfig != null) {
            final ConfigItem<List> groups = backward.compat(thisRepositoryConfig).getConfigItem("security:groups");
            groups.getValue().add(group);

            configurationService.updateConfiguration(thisRepositoryConfig);

            configuredRepositories.update(repository.getSpace(),
                                          repositoryFactory.newRepository(thisRepositoryConfig));
        } else {
            throw new IllegalArgumentException("Repository " + repository.getAlias() + " not found");
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void removeGroup(Repository repository,
                            String group) {
        final ConfigGroup thisRepositoryConfig = findRepositoryConfig(repository.getAlias(),
                                                                      repository.getSpace().getName());

        if (thisRepositoryConfig != null) {
            final ConfigItem<List> groups = backward.compat(thisRepositoryConfig).getConfigItem("security:groups");
            groups.getValue().remove(group);

            configurationService.updateConfiguration(thisRepositoryConfig);

            configuredRepositories.update(repository.getSpace(),
                                          repositoryFactory.newRepository(thisRepositoryConfig));
        } else {
            throw new IllegalArgumentException("Repository " + repository.getAlias() + " not found");
        }
    }

    @Override
    public void updateContributors(final Repository repository,
                                   final List<Contributor> contributors) {
        final ConfigGroup thisRepositoryConfig = findRepositoryConfig(repository.getAlias(),
                                                                      repository.getSpace().getName());

        if (thisRepositoryConfig != null) {
            final ConfigItem<List<Contributor>> contributorsConfigItem = thisRepositoryConfig.getConfigItem("contributors");
            contributorsConfigItem.setValue(contributors);

            configurationService.updateConfiguration(thisRepositoryConfig);

            configuredRepositories.update(repository.getSpace(),
                                          repositoryFactory.newRepository(thisRepositoryConfig));
        } else {
            throw new IllegalArgumentException("Repository " + repository.getAlias() + " not found");
        }
    }

    @Override
    public List<VersionRecord> getRepositoryHistoryAll(final Space space,
                                                       final String alias) {
        return getRepositoryHistory(space,
                                    alias,
                                    0,
                                    -1);
    }

    private Repository createRepository(final String scheme,
                                        final String alias,
                                        final Space space,
                                        final RepositoryEnvironmentConfigurations repositoryEnvironmentConfigurations,
                                        final Collection<Contributor> contributors) {

        if (configuredRepositories.containsAlias(space,
                                                 alias)) {
            throw new RepositoryAlreadyExistsException(alias);
        }

        Repository repo = null;
        try {
            configurationService.startBatch();
            final ConfigGroup repositoryConfig = configurationFactory.newConfigGroup(REPOSITORY,
                                                                                     space.getName(),
                                                                                     alias,
                                                                                     "");
            repositoryConfig.addConfigItem(configurationFactory.newConfigItem("security:groups",
                                                                              new ArrayList<String>()));
            repositoryConfig.addConfigItem(configurationFactory.newConfigItem("contributors",
                                                                              contributors));

            if (!repositoryEnvironmentConfigurations.containsConfiguration(SCHEME)) {
                repositoryConfig.addConfigItem(configurationFactory.newConfigItem(SCHEME,
                                                                                  scheme));
            }

            for (final RepositoryEnvironmentConfiguration configuration : repositoryEnvironmentConfigurations.getConfigurationList()) {
                repositoryConfig.addConfigItem(getRepositoryConfigItem(configuration));
            }

            repo = createRepository(repositoryConfig,
                                    space);
            return repo;
        } catch (final Exception e) {
            logger.error("Error during create repository",
                         e);
            throw ExceptionUtilities.handleException(e);
        } finally {
            configurationService.endBatch();
            if (repo != null) {
                event.fire(new NewRepositoryEvent(repo));
            }
        }
    }

    private Repository createRepository(final ConfigGroup repositoryConfig,
                                        final Space space) {
        final Repository repository = repositoryFactory.newRepository(repositoryConfig);
        configurationService.addConfiguration(repositoryConfig);
        configuredRepositories.add(space,
                                   repository);
        return repository;
    }

    private ConfigItem getRepositoryConfigItem(final RepositoryEnvironmentConfiguration configuration) {
        if (configuration.isSecuredConfigurationItem()) {
            return configurationFactory.newSecuredConfigItem(configuration.getName(),
                                                             configuration.getValue().toString());
        } else {
            return configurationFactory.newConfigItem(configuration.getName(),
                                                      configuration.getValue());
        }
    }

    public class NoActiveSpaceInTheContext extends RuntimeException {

    }
}
