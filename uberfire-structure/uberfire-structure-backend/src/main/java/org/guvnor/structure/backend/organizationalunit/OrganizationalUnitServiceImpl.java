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

package org.guvnor.structure.backend.organizationalunit;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.SpaceContributorsUpdatedEvent;
import org.guvnor.structure.organizationalunit.NewOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoAddedToOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoRemovedFromOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.UpdatedOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.config.RepositoryConfiguration;
import org.guvnor.structure.organizationalunit.config.RepositoryInfo;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.organizationalunit.config.SpaceInfo;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.structure.server.organizationalunit.OrganizationalUnitFactory;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.ext.security.management.api.event.UserDeletedEvent;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

@Service
@ApplicationScoped
public class OrganizationalUnitServiceImpl implements OrganizationalUnitService {

    public static final String DEFAULT_GROUP_ID = "defaultGroupId";
    public static final String DELETED = "deleted";
    private OrganizationalUnitFactory organizationalUnitFactory;

    private Event<NewOrganizationalUnitEvent> newOrganizationalUnitEvent;

    private Event<RemoveOrganizationalUnitEvent> removeOrganizationalUnitEvent;

    private Event<RepoAddedToOrganizationalUnitEvent> repoAddedToOrgUnitEvent;

    private Event<RepoRemovedFromOrganizationalUnitEvent> repoRemovedFromOrgUnitEvent;

    private Event<UpdatedOrganizationalUnitEvent> updatedOrganizationalUnitEvent;

    private AuthorizationManager authorizationManager;

    private SessionInfo sessionInfo;

    private SpacesAPI spaces;

    private RepositoryService repositoryService;

    private IOService ioService;

    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    private FileSystem systemFS;

    private Event<SpaceContributorsUpdatedEvent> spaceContributorsUpdatedEvent;
    private ConfigurationService configurationService;

    public OrganizationalUnitServiceImpl() {
    }

    @Inject
    public OrganizationalUnitServiceImpl(final OrganizationalUnitFactory organizationalUnitFactory,
                                         final RepositoryService repositoryService,
                                         final Event<NewOrganizationalUnitEvent> newOrganizationalUnitEvent,
                                         final Event<RemoveOrganizationalUnitEvent> removeOrganizationalUnitEvent,
                                         final Event<RepoAddedToOrganizationalUnitEvent> repoAddedToOrgUnitEvent,
                                         final Event<RepoRemovedFromOrganizationalUnitEvent> repoRemovedFromOrgUnitEvent,
                                         final Event<UpdatedOrganizationalUnitEvent> updatedOrganizationalUnitEvent,
                                         final AuthorizationManager authorizationManager,
                                         final SpacesAPI spaces,
                                         final SessionInfo sessionInfo,
                                         @Named("ioStrategy") final IOService ioService,
                                         final SpaceConfigStorageRegistry spaceConfigStorageRegistry,
                                         final @Named("systemFS") FileSystem systemFS,
                                         final Event<SpaceContributorsUpdatedEvent> spaceContributorsUpdatedEvent,
                                         final ConfigurationService configurationService) {
        this.organizationalUnitFactory = organizationalUnitFactory;
        this.repositoryService = repositoryService;
        this.newOrganizationalUnitEvent = newOrganizationalUnitEvent;
        this.removeOrganizationalUnitEvent = removeOrganizationalUnitEvent;
        this.repoAddedToOrgUnitEvent = repoAddedToOrgUnitEvent;
        this.repoRemovedFromOrgUnitEvent = repoRemovedFromOrgUnitEvent;
        this.updatedOrganizationalUnitEvent = updatedOrganizationalUnitEvent;
        this.authorizationManager = authorizationManager;
        this.spaces = spaces;
        this.sessionInfo = sessionInfo;
        this.ioService = ioService;
        this.spaceConfigStorageRegistry = spaceConfigStorageRegistry;
        this.systemFS = systemFS;
        this.spaceContributorsUpdatedEvent = spaceContributorsUpdatedEvent;
        this.configurationService = configurationService;
    }

    public void userRemoved(final @Observes UserDeletedEvent event) {
        final String removedUserIdentifier = event.getIdentifier();
        for (OrganizationalUnit organizationalUnit : getAllOrganizationalUnits()) {
            final boolean userRemoved = organizationalUnit.getContributors().removeIf(c -> c.getUsername().equals(removedUserIdentifier));
            if (userRemoved) {
                updateOrganizationalUnit(organizationalUnit.getName(),
                                         organizationalUnit.getDefaultGroupId(),
                                         organizationalUnit.getContributors());
            }

            for (Repository repository : organizationalUnit.getRepositories()) {
                final List<Contributor> updatedRepositoryContributors = new ArrayList<>(repository.getContributors());
                final boolean repositoryContributorRemoved = updatedRepositoryContributors.removeIf(c -> c.getUsername().equals(removedUserIdentifier));
                if (repositoryContributorRemoved) {
                    repositoryService.updateContributors(repository,
                                                         updatedRepositoryContributors);
                }
            }
        }
    }

    @Override
    public OrganizationalUnit getOrganizationalUnit(final String name) {
        return getOrganizationalUnit(name,
                                     false);
    }

    @Override
    public OrganizationalUnit getOrganizationalUnit(final String name,
                                                    final boolean includeDeleted) {
        if (spaceConfigStorageRegistry.exist(name) && !isDeleted(name)) {
            SpaceInfo spaceInfo = this.spaceConfigStorageRegistry.get(name).loadSpaceInfo();
            if (spaceInfo != null) {
                return organizationalUnitFactory.newOrganizationalUnit(spaceInfo);
            }
        } else {
            if (includeDeleted) {
                return this.getAllDeletedOrganizationalUnit()
                        .stream()
                        .filter(organizationalUnit -> organizationalUnit.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
            }
        }

        return null;
    }

    @Override
    public Collection<OrganizationalUnit> getAllOrganizationalUnits() {
        return getAllOrganizationalUnits(false);
    }

    @Override
    public Collection<OrganizationalUnit> getAllOrganizationalUnits(final boolean includeDeleted) {
        return this.getAllOrganizationalUnits(includeDeleted, (ou) -> !ou.getName().startsWith("."));
    }

    @Override
    public Collection<OrganizationalUnit> getAllOrganizationalUnits(final boolean includeDeleted, final Predicate<OrganizationalUnit> filter) {
        final List<OrganizationalUnit> spaces = new ArrayList<>();

        try (DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(getNiogitPath())) {
            for (java.nio.file.Path spacePath : stream) {
                final File spaceDirectory = spacePath.toFile();

                if (spaceDirectory.isDirectory() && !spaceDirectory.getName().equals("system") && !isDeleted(spaceDirectory.getName())) {
                    SpaceConfigStorage configStorage = this.spaceConfigStorageRegistry.get(spaceDirectory.getName());
                    if (configStorage.isInitialized()) {
                        OrganizationalUnit ou = getOrganizationalUnit(spaceDirectory.getName(),
                                                                      false);
                        if (ou != null) {
                            spaces.add(ou);
                        }
                    }
                }
            }

            if (includeDeleted) {
                spaces.addAll(this.getAllDeletedOrganizationalUnit());
            }

            return spaces.stream().filter(filter).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<OrganizationalUnit> getAllDeletedOrganizationalUnit() {
        List<ConfigGroup> spaceConfiguration = this.configurationService.getConfiguration(ConfigType.SPACE);
        return spaceConfiguration.stream()
                .filter(configGroup -> Optional.ofNullable(configGroup.getConfigItem(DELETED)).isPresent())
                .map(configGroup -> this.createDeletedOrganizationalUnit(configGroup))
                .collect(Collectors.toList());
    }

    protected boolean isDeleted(String spaceName) {
        List<ConfigGroup> spaceConfigurations = this.configurationService.getConfiguration(ConfigType.SPACE);
        return spaceConfigurations.stream()
                .filter(spaceConfiguration -> spaceConfiguration.getName().equalsIgnoreCase(spaceName) &&
                        spaceConfiguration.getConfigItem(DELETED) != null)
                .findFirst()
                .isPresent();
    }

    public void onRemoveOrganizationalUnit(@Observes RemoveOrganizationalUnitEvent event) {
        if (event.getOrganizationalUnit() != null && event.getOrganizationalUnit().getSpace() != null) {
            this.spaceConfigStorageRegistry.remove(event.getOrganizationalUnit().getSpace().getName());
        }
    }

    @Override
    public Collection<Space> getAllUserSpaces() {
        return getAllOrganizationalUnits()
                .stream()
                .map(ou -> spaces.getSpace(ou.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<OrganizationalUnit> getOrganizationalUnits() {
        return getOrganizationalUnits(false);
    }

    @Override
    public Collection<OrganizationalUnit> getOrganizationalUnits(final boolean includeDeleted) {
        final List<OrganizationalUnit> result = new ArrayList<>();
        for (OrganizationalUnit ou : getAllOrganizationalUnits(includeDeleted)) {
            if (authorizationManager.authorize(ou,
                                               sessionInfo.getIdentity())
                    || ou.getContributors().stream().anyMatch(c -> c.getUsername().equals(sessionInfo.getIdentity().getIdentifier()))) {
                result.add(ou);
            }
        }
        return result;
    }

    @Override
    public OrganizationalUnit createOrganizationalUnit(final String name,
                                                       final String defaultGroupId) {

        return createOrganizationalUnit(name,
                                        defaultGroupId,
                                        new ArrayList<>());
    }

    @Override
    public OrganizationalUnit createOrganizationalUnit(final String name,
                                                       final String defaultGroupId,
                                                       final Collection<Repository> repositories) {

        return createOrganizationalUnit(name,
                                        defaultGroupId,
                                        repositories,
                                        new ArrayList<>());
    }

    @Override
    public OrganizationalUnit createOrganizationalUnit(final String name,
                                                       final String defaultGroupId,
                                                       final Collection<Repository> repositories,
                                                       final Collection<Contributor> contributors) {
        return createOrganizationalUnit(name,
                                        defaultGroupId,
                                        repositories,
                                        contributors,
                                        null);
    }

    @Override
    public OrganizationalUnit createOrganizationalUnit(final String name,
                                                       final String defaultGroupId,
                                                       final Collection<Repository> repositories,
                                                       final Collection<Contributor> contributors,
                                                       final String description) {
        if (spaceDirectoryExists(name)) {
            return null;
        }

        OrganizationalUnit newOrganizationalUnit = null;

        try {
            String _defaultGroupId = defaultGroupId == null || defaultGroupId.trim().isEmpty() ? getSanitizedDefaultGroupId(name) : defaultGroupId;
            final SpaceInfo spaceInfo = new SpaceInfo(name,
                                                      description,
                                                      _defaultGroupId,
                                                      contributors,
                                                      getRepositoryAliases(repositories),
                                                      Collections.emptyList());
            spaceConfigStorageRegistry.get(name).saveSpaceInfo(spaceInfo);
            newOrganizationalUnit = organizationalUnitFactory.newOrganizationalUnit(spaceInfo);

            return newOrganizationalUnit;
        } finally {
            if (newOrganizationalUnit != null) {
                newOrganizationalUnitEvent.fire(new NewOrganizationalUnitEvent(newOrganizationalUnit,
                                                                               getUserInfo(sessionInfo)));
            }
        }
    }

    private List<RepositoryInfo> getRepositoryAliases(final Collection<Repository> repositories) {
        return repositories.stream()
                .map(repository -> new RepositoryInfo(repository.getAlias(),
                                                      false,
                                                      new RepositoryConfiguration(repository.getEnvironment())))
                .collect(Collectors.toList());
    }

    @Override
    public OrganizationalUnit updateOrganizationalUnit(final String name,
                                                       final String defaultGroupId) {
        return updateOrganizationalUnit(name,
                                        defaultGroupId,
                                        null);
    }

    @Override
    public OrganizationalUnit updateOrganizationalUnit(String name,
                                                       String defaultGroupId,
                                                       Collection<Contributor> contributors) {
        return updateOrganizationalUnit(name,
                                        defaultGroupId,
                                        contributors,
                                        null);
    }

    @Override
    public OrganizationalUnit updateOrganizationalUnit(String name,
                                                       String defaultGroupId,
                                                       Collection<Contributor> contributors,
                                                       String description) {
        return spaceConfigStorageRegistry.getBatch(name)
                .run(context -> {
                    OrganizationalUnit updatedOrganizationalUnit = null;
                    try {
                        SpaceInfo spaceInfo = context.getSpaceInfo();

                        // As per loadOrganizationalUnits(), all Organizational Units should have the default group id value set
                        String _defaultGroupId = defaultGroupId == null || defaultGroupId.trim().isEmpty() ?
                                spaceInfo.getDefaultGroupId() : defaultGroupId;
                        spaceInfo.setDefaultGroupId(_defaultGroupId);

                        if (contributors != null) {
                            spaceInfo.setContributors(contributors);
                        }

                        if (description != null) {
                            spaceInfo.setDescription(description);
                        }

                        context.saveSpaceInfo();

                        updatedOrganizationalUnit = getOrganizationalUnit(name);

                        checkChildrenRepositoryContributors(updatedOrganizationalUnit);

                        return updatedOrganizationalUnit;
                    } finally {
                        if (updatedOrganizationalUnit != null) {
                            updatedOrganizationalUnitEvent.fire(new UpdatedOrganizationalUnitEvent(updatedOrganizationalUnit,
                                                                                                   getUserInfo(sessionInfo)));
                            if (contributors != null) {
                                spaceContributorsUpdatedEvent.fire(new SpaceContributorsUpdatedEvent(updatedOrganizationalUnit));
                            }
                        }
                    }
                });
    }

    void checkChildrenRepositoryContributors(final OrganizationalUnit updatedOrganizationalUnit) {
        repositoryService.getAllRepositories(updatedOrganizationalUnit.getSpace()).forEach(repository -> {
            final List<Contributor> updatedRepositoryContributors = repository.getContributors().stream()
                    .filter(contributor -> updatedOrganizationalUnit.getContributors().stream()
                            .anyMatch(spaceContributor -> spaceContributor.getUsername().equals(contributor.getUsername())))
                    .collect(Collectors.toList());

            if (updatedRepositoryContributors.size() != repository.getContributors().size()) {
                repositoryService.updateContributors(repository,
                                                     updatedRepositoryContributors);
            }
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addRepository(final OrganizationalUnit organizationalUnit,
                              final Repository repository) {

        spaceConfigStorageRegistry.getBatch(organizationalUnit.getName())
                .run(context -> {
                    try {
                        context.getSpaceInfo().getRepositories().add(new RepositoryInfo(repository.getAlias(),
                                                                                        false,
                                                                                        new RepositoryConfiguration(repository.getEnvironment())));

                        context.saveSpaceInfo();
                    } finally {
                        repoAddedToOrgUnitEvent.fire(new RepoAddedToOrganizationalUnitEvent(organizationalUnit,
                                                                                            repository,
                                                                                            getUserInfo(sessionInfo)));
                    }

                    return null;
                });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void removeRepository(final OrganizationalUnit organizationalUnit,
                                 final Repository repository) {

        spaceConfigStorageRegistry.getBatch(organizationalUnit.getName())
                .run(context -> {
                    try {
                        context.getSpaceInfo().getRepositories()
                                .stream()
                                .filter(repositoryInfo -> repositoryInfo.getName().equals(repository.getAlias()) && !repositoryInfo.isDeleted())
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("Repository not found"))
                                .setDeleted(true);

                        context.saveSpaceInfo();

                        return null;
                    } finally {
                        repoRemovedFromOrgUnitEvent.fire(new RepoRemovedFromOrganizationalUnitEvent(organizationalUnit,
                                                                                                    repository,
                                                                                                    getUserInfo(sessionInfo)));
                    }
                });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addGroup(final OrganizationalUnit organizationalUnit,
                         final String group) {

        spaceConfigStorageRegistry.getBatch(organizationalUnit.getName())
                .run(context -> {
                    OrganizationalUnit updatedOrganizationalUnit = null;
                    try {
                        context.getSpaceInfo().getSecurityGroups().add(group);
                        context.saveSpaceInfo();

                        updatedOrganizationalUnit = getOrganizationalUnit(organizationalUnit.getName());

                        return null;
                    } finally {
                        if (updatedOrganizationalUnit != null) {
                            updatedOrganizationalUnitEvent.fire(new UpdatedOrganizationalUnitEvent(updatedOrganizationalUnit,
                                                                                                   getUserInfo(sessionInfo)));
                        }
                    }
                });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void removeGroup(final OrganizationalUnit organizationalUnit,
                            final String group) {

        spaceConfigStorageRegistry.getBatch(organizationalUnit.getName())
                .run(context -> {
                    OrganizationalUnit updatedOrganizationalUnit = null;
                    try {
                        context.getSpaceInfo().getSecurityGroups().remove(group);
                        context.saveSpaceInfo();

                        updatedOrganizationalUnit = getOrganizationalUnit(organizationalUnit.getName());
                    } finally {
                        if (updatedOrganizationalUnit != null) {
                            updatedOrganizationalUnitEvent.fire(new UpdatedOrganizationalUnitEvent(updatedOrganizationalUnit,
                                                                                                   getUserInfo(sessionInfo)));
                        }
                    }

                    return null;
                });
    }

    @Override
    public void removeOrganizationalUnit(String groupName) {
        final OrganizationalUnit organizationalUnit = getOrganizationalUnit(groupName);

        if (organizationalUnit != null) {
            repositoryService.removeRepositories(organizationalUnit.getSpace(),
                                                 organizationalUnit.getRepositories().stream().map(repo -> repo.getAlias()).collect(Collectors.toSet()));
            removeSpaceDirectory(organizationalUnit);
            removeOrganizationalUnitEvent.fire(new RemoveOrganizationalUnitEvent(organizationalUnit,
                                                                                 getUserInfo(sessionInfo)));
        }
    }

    private void removeSpaceDirectory(final OrganizationalUnit organizationalUnit) {

        this.configurationService.startBatch();
        ConfigGroup configGroup = new ConfigGroup();
        configGroup.setType(ConfigType.SPACE);
        configGroup.setName(organizationalUnit.getSpace().getName());
        ConfigItem<Boolean> deletedConfigItem = new ConfigItem<>();
        deletedConfigItem.setName(DELETED);
        deletedConfigItem.setValue(true);
        configGroup.addConfigItem(deletedConfigItem);
        ConfigItem<String> defaultGroupIdConfigItem = new ConfigItem<>();
        defaultGroupIdConfigItem.setName(DEFAULT_GROUP_ID);
        defaultGroupIdConfigItem.setValue(organizationalUnit.getDefaultGroupId());
        configGroup.addConfigItem(defaultGroupIdConfigItem);
        this.configurationService.addConfiguration(configGroup);
        this.configurationService.endBatch();
    }

    @Override
    public OrganizationalUnit getParentOrganizationalUnit(final Repository repository) {
        for (final OrganizationalUnit organizationalUnit : getAllOrganizationalUnits()) {
            if (organizationalUnit.getRepositories() != null) {
                for (final Repository ouRepository : organizationalUnit.getRepositories()) {
                    if (ouRepository.getAlias().equals(repository.getAlias())) {
                        return organizationalUnit;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<OrganizationalUnit> getOrganizationalUnits(Repository repository) {
        final ArrayList<OrganizationalUnit> result = new ArrayList<>();

        for (final OrganizationalUnit organizationalUnit : getAllOrganizationalUnits()) {
            if (organizationalUnit.getRepositories() != null) {
                for (final Repository ouRepository : organizationalUnit.getRepositories()) {
                    if (ouRepository.getAlias().equals(repository.getAlias())) {
                        result.add(organizationalUnit);
                    }
                }
            }
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public String getSanitizedDefaultGroupId(final String proposedGroupId) {
        //Only [A-Za-z0-9_\-.] are valid so strip everything else out
        return proposedGroupId != null ? proposedGroupId.replaceAll("[^A-Za-z0-9_\\-.]",
                                                                    "") : proposedGroupId;
    }

    @Override
    public Boolean isValidGroupId(final String proposedGroupId) {
        if (proposedGroupId != null && !proposedGroupId.trim().isEmpty()) {
            if (proposedGroupId.length() == getSanitizedDefaultGroupId(proposedGroupId).length()) {
                return true;
            }
        }
        return false;
    }

    protected String getUserInfo(SessionInfo sessionInfo) {
        try {
            return sessionInfo.getIdentity().getIdentifier();
        } catch (final Exception e) {
            return "system";
        }
    }

    java.nio.file.Path getNiogitPath() {
        return systemFS.getPath("/").toFile().getParentFile().getParentFile().toPath();
    }

    boolean spaceDirectoryExists(String spaceName) {
        SpaceConfigStorage configStorage = this.spaceConfigStorageRegistry.get(spaceName);
        return getNiogitPath().resolve(spaceName).toFile().exists() &&
                configStorage.isInitialized();
    }

    private OrganizationalUnit createDeletedOrganizationalUnit(ConfigGroup configGroup) {
        String spaceName = configGroup.getName();
        String defaultGroupId = configGroup.getConfigItemValue(DEFAULT_GROUP_ID);
        return new OrganizationalUnitImpl(spaceName,
                                          defaultGroupId,
                                          true);
    }
}
