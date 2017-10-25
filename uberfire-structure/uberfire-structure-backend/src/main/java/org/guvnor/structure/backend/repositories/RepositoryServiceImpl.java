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
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
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

import static org.guvnor.structure.repositories.EnvironmentParameters.SCHEME;
import static org.guvnor.structure.server.config.ConfigType.REPOSITORY;
import static org.uberfire.backend.server.util.Paths.convert;

@Service
@ApplicationScoped
public class RepositoryServiceImpl implements RepositoryService {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryServiceImpl.class);

    private static final int HISTORY_PAGE_SIZE = 10;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private GitMetadataStore metadataStore;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private OrganizationalUnitService organizationalUnitService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @Inject
    private RepositoryFactory repositoryFactory;

    @Inject
    private Event<NewRepositoryEvent> event;

    @Inject
    private Event<RepositoryRemovedEvent> repositoryRemovedEvent;

    @Inject
    private BackwardCompatibleUtil backward;

    @Inject
    private ConfiguredRepositories configuredRepositories;

    @Inject
    private AuthorizationManager authorizationManager;

    @Inject
    private SessionInfo sessionInfo;

    private Repository createRepository(final ConfigGroup repositoryConfig) {
        final Repository repository = repositoryFactory.newRepository(repositoryConfig);
        configurationService.addConfiguration(repositoryConfig);
        configuredRepositories.add(repository);
        return repository;
    }

    public RepositoryInfo getRepositoryInfo(final String alias) {
        final Repository repo = getRepository(alias);
        String ouName = null;
        for (final OrganizationalUnit ou : organizationalUnitService.getAllOrganizationalUnits()) {
            for (Repository repository : ou.getRepositories()) {
                if (repository.getAlias().equals(alias)) {
                    ouName = ou.getName();
                }
            }
        }

        return new RepositoryInfo(repo.getIdentifier(),
                                  alias,
                                  ouName,
                                  repo.getRoot(),
                                  repo.getPublicURIs(),
                                  getRepositoryHistory(alias,
                                                       0,
                                                       HISTORY_PAGE_SIZE));
    }

    @Override
    public List<VersionRecord> getRepositoryHistory(final String alias,
                                                    final int startIndex) {
        return getRepositoryHistory(alias,
                                    startIndex,
                                    startIndex + HISTORY_PAGE_SIZE);
    }

    @Override
    public List<VersionRecord> getRepositoryHistory(String alias,
                                                    int startIndex,
                                                    int endIndex) {
        final Repository repo = getRepository(alias);

        //This is a work-around for https://bugzilla.redhat.com/show_bug.cgi?id=1199215
        //org.kie.workbench.common.screens.contributors.backend.dataset.ContributorsManager is trying to
        //load a Repository's history for a Repository associated with an Organizational Unit before the
        //Repository has been setup.
        if (repo == null) {
            return Collections.EMPTY_LIST;
        }

        final VersionAttributeView versionAttributeView = ioService.getFileAttributeView(convert(repo.getRoot()),
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

        final List<VersionRecord> result = new ArrayList<VersionRecord>(endIndex - startIndex);
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
    public Repository getRepository(final String alias) {
        return configuredRepositories.getRepositoryByRepositoryAlias(alias);
    }

    @Override
    public Repository getRepository(final Path root) {
        return configuredRepositories.getRepositoryByRootPath(root);
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
    public Collection<Repository> getAllRepositories() {
        return configuredRepositories.getAllConfiguredRepositories();
    }

    @Override
    public Collection<Repository> getRepositories() {
        Collection<Repository> result = new ArrayList<>();
        for (Repository repository : configuredRepositories.getAllConfiguredRepositories()) {
            if (authorizationManager.authorize(repository,
                                               sessionInfo.getIdentity())) {
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

        try {

            final Repository repository = createRepository(scheme,
                                                           alias,
                                                           repositoryEnvironmentConfigurations);
            if (organizationalUnit != null && repository != null) {
                organizationalUnitService.addRepository(organizationalUnit,
                                                        repository);
            }
            metadataStore.write(alias,
                                (String) repositoryEnvironmentConfigurations.getOrigin());
            return repository;
        } catch (final Exception e) {
            logger.error("Error during create repository",
                         e);
            throw ExceptionUtilities.handleException(e);
        }
    }

    protected ConfigGroup findRepositoryConfig(final String alias) {
        final Collection<ConfigGroup> groups = configurationService.getConfiguration(ConfigType.REPOSITORY);
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
    public void removeRepository(final String alias) {
        final ConfigGroup thisRepositoryConfig = findRepositoryConfig(alias);

        try {
            configurationService.startBatch();
            if (thisRepositoryConfig != null) {
                configurationService.removeConfiguration(thisRepositoryConfig);
            }

            final Repository repo = configuredRepositories.remove(alias);
            if (repo != null) {
                repositoryRemovedEvent.fire(new RepositoryRemovedEvent(repo));
                ioService.delete(convert(repo.getRoot()).getFileSystem().getPath(null));
            }

            //Remove reference to Repository from Organizational Units
            final Collection<OrganizationalUnit> organizationalUnits = organizationalUnitService.getAllOrganizationalUnits();
            for (OrganizationalUnit ou : organizationalUnits) {
                for (Repository repository : ou.getRepositories()) {
                    if (repository.getAlias().equals(alias)) {
                        organizationalUnitService.removeRepository(ou,
                                                                   repository);
                        metadataStore.delete(alias);
                    }
                }
            }
        } catch (final Exception e) {
            logger.error("Error during remove repository",
                         e);
            throw new RuntimeException(e);
        } finally {
            configurationService.endBatch();
        }
    }

    @Override
    public Repository createRepository(final String scheme,
                                       final String alias,
                                       final RepositoryEnvironmentConfigurations repositoryEnvironmentConfigurations) {

        if (configuredRepositories.containsAlias(alias)) {
            throw new RepositoryAlreadyExistsException(alias);
        }

        Repository repo = null;
        try {
            configurationService.startBatch();
            final ConfigGroup repositoryConfig = configurationFactory.newConfigGroup(REPOSITORY,
                                                                                     alias,
                                                                                     "");
            repositoryConfig.addConfigItem(configurationFactory.newConfigItem("security:groups",
                                                                              new ArrayList<String>()));

            if (!repositoryEnvironmentConfigurations.containsConfiguration(SCHEME)) {
                repositoryConfig.addConfigItem(configurationFactory.newConfigItem(SCHEME,
                                                                                  scheme));
            }

            for (final RepositoryEnvironmentConfiguration configuration : repositoryEnvironmentConfigurations.getConfigurationList()) {
                repositoryConfig.addConfigItem(getRepositoryConfigItem(configuration));
            }

            repo = createRepository(repositoryConfig);
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

    private ConfigItem getRepositoryConfigItem(final RepositoryEnvironmentConfiguration configuration) {
        if (configuration.isSecuredConfigurationItem()) {
            return configurationFactory.newSecuredConfigItem(configuration.getName(),
                                                             configuration.getValue().toString());
        } else {
            return configurationFactory.newConfigItem(configuration.getName(),
                                                      configuration.getValue());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addGroup(final Repository repository,
                         final String group) {
        final ConfigGroup thisRepositoryConfig = findRepositoryConfig(repository.getAlias());

        if (thisRepositoryConfig != null) {
            final ConfigItem<List> groups = backward.compat(thisRepositoryConfig).getConfigItem("security:groups");
            groups.getValue().add(group);

            configurationService.updateConfiguration(thisRepositoryConfig);

            configuredRepositories.update(repositoryFactory.newRepository(thisRepositoryConfig));
        } else {
            throw new IllegalArgumentException("Repository " + repository.getAlias() + " not found");
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void removeGroup(Repository repository,
                            String group) {
        final ConfigGroup thisRepositoryConfig = findRepositoryConfig(repository.getAlias());

        if (thisRepositoryConfig != null) {
            final ConfigItem<List> groups = backward.compat(thisRepositoryConfig).getConfigItem("security:groups");
            groups.getValue().remove(group);

            configurationService.updateConfiguration(thisRepositoryConfig);

            configuredRepositories.update(repositoryFactory.newRepository(thisRepositoryConfig));
        } else {
            throw new IllegalArgumentException("Repository " + repository.getAlias() + " not found");
        }
    }

    @Override
    public List<VersionRecord> getRepositoryHistoryAll(final String alias) {
        return getRepositoryHistory(alias,
                                    0,
                                    -1);
    }

    @Override
    public Repository updateRepositoryConfiguration(final Repository repository,
                                                    final RepositoryEnvironmentConfigurations repositoryEnvironmentConfigurations) {
        final ConfigGroup thisRepositoryConfig = findRepositoryConfig(repository.getAlias());

        if (thisRepositoryConfig != null && repositoryEnvironmentConfigurations != null) {

            try {
                configurationService.startBatch();

                for (final Map.Entry<String, Object> entry : repositoryEnvironmentConfigurations.getConfigurationMap().entrySet()) {

                    ConfigItem configItem = thisRepositoryConfig.getConfigItem(entry.getKey());
                    if (configItem == null) {
                        thisRepositoryConfig.addConfigItem(configurationFactory.newConfigItem(entry.getKey(),
                                                                                              entry.getValue()));
                    } else {
                        configItem.setValue(entry.getValue());
                    }
                }

                configurationService.updateConfiguration(thisRepositoryConfig);

                final Repository updatedRepo = repositoryFactory.newRepository(thisRepositoryConfig);
                configuredRepositories.update(updatedRepo);

                return updatedRepo;
            } catch (final Exception e) {
                logger.error("Error during remove repository",
                             e);
                throw new RuntimeException(e);
            } finally {
                configurationService.endBatch();
            }
        } else {
            throw new IllegalArgumentException("Repository " + repository.getAlias() + " not found");
        }
    }
}
