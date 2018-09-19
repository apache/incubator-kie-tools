/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.common.services.project.backend.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.appformer.maven.integration.Aether;
import org.appformer.maven.integration.embedder.MavenEmbedder;
import org.appformer.maven.integration.embedder.MavenProjectLoader;
import org.appformer.maven.integration.embedder.MavenSettings;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.guvnor.common.services.project.backend.server.utils.POMContentHandler;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.MavenRepositorySource;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.preferences.GAVPreferences;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.shared.preferences.GuvnorPreferenceScopes;
import org.guvnor.common.services.shared.preferences.WorkbenchPreferenceScopeResolutionStrategies;
import org.jboss.errai.bus.server.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.annotations.Customizable;
import org.uberfire.backend.server.cdi.workspace.WorkspaceScoped;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

import static org.guvnor.common.services.project.backend.server.MavenLocalRepositoryUtils.getRepositoryPath;
import static org.guvnor.common.services.project.backend.server.MavenLocalRepositoryUtils.tearDownMavenRepository;

@Service
@WorkspaceScoped
public class ModuleRepositoryResolverImpl
        implements ModuleRepositoryResolver {

    private static final Logger log = LoggerFactory.getLogger(ModuleRepositoryResolverImpl.class);

    private IOService ioService;

    private POMContentHandler pomContentHandler = new POMContentHandler();

    private Instance<GAVPreferences> gavPreferencesProvider;

    private WorkbenchPreferenceScopeResolutionStrategies scopeResolutionStrategies;

    public ModuleRepositoryResolverImpl() {
    }

    @Inject
    public ModuleRepositoryResolverImpl(final @Named("ioStrategy") IOService ioService,
                                        final Instance<GAVPreferences> gavPreferencesProvider,
                                        @Customizable final WorkbenchPreferenceScopeResolutionStrategies scopeResolutionStrategies) {
        this.ioService = ioService;
        this.gavPreferencesProvider = gavPreferencesProvider;
        this.scopeResolutionStrategies = scopeResolutionStrategies;
    }

    @Override
    public Set<MavenRepositoryMetadata> getRemoteRepositoriesMetaData() {
        final Set<MavenRepositoryMetadata> repositories = new HashSet<MavenRepositoryMetadata>();

        final Aether aether = Aether.getAether();
        final Map<MavenRepositorySource, Collection<RemoteRepository>> remoteRepositories = getRemoteRepositories();

        //Local Repository
        repositories.add(makeRepositoryMetaData(aether.getSession().getLocalRepository(),
                                                MavenRepositorySource.LOCAL));

        if (remoteRepositories.isEmpty()) {
            return repositories;
        }

        for (Map.Entry<MavenRepositorySource, Collection<RemoteRepository>> e : remoteRepositories.entrySet()) {
            repositories.addAll(makeRepositoriesMetaData(e.getValue(),
                                                         e.getKey()));
        }

        return repositories;
    }

    @Override
    public Set<MavenRepositoryMetadata> getRemoteRepositoriesMetaData(final Module module) {
        if (module == null) {
            return Collections.emptySet();
        }

        final Set<MavenRepositoryMetadata> repositories = new HashSet<>();

        try {
            //Load Project's pom.xml
            final Path pomXMLPath = module.getPomXMLPath();
            final org.uberfire.java.nio.file.Path nioPomXMLPath = Paths.convert(pomXMLPath);
            final String pomXML = ioService.readAllString(nioPomXMLPath);

            final InputStream pomStream = new ByteArrayInputStream(pomXML.getBytes(StandardCharsets.UTF_8));
            final MavenProject mavenProject = MavenProjectLoader.parseMavenPom(pomStream);
            final Map<MavenRepositorySource, Collection<RemoteRepository>> remoteRepositories = getRemoteRepositories(mavenProject);

            //Local Repository
            repositories.add(makeRepositoryMetaData(Aether.getAether().getSession().getLocalRepository(),
                                                    MavenRepositorySource.LOCAL));

            if (remoteRepositories.isEmpty()) {
                return repositories;
            }

            for (Map.Entry<MavenRepositorySource, Collection<RemoteRepository>> e : remoteRepositories.entrySet()) {
                repositories.addAll(makeRepositoriesMetaData(e.getValue(),
                                                             e.getKey()));
            }
        } catch (IllegalArgumentException iae) {
            log.error("Unable to get Remote Repositories for Project '%s'. Returning empty Collection. ",
                      module.getModuleName(),
                      iae);
        } catch (NoSuchFileException nsfe) {
            log.error("Unable to get Remote Repositories for Project '%s'. Returning empty Collection. ",
                      module.getModuleName(),
                      nsfe);
        } catch (org.uberfire.java.nio.IOException ioe) {
            log.error("Unable to get Remote Repositories for Project '%s'. Returning empty Collection. ",
                      module.getModuleName(),
                      ioe);
        }

        return repositories;
    }

    private Set<MavenRepositoryMetadata> makeRepositoriesMetaData(final Collection<? extends ArtifactRepository> repositories,
                                                                  final MavenRepositorySource source) {
        final Set<MavenRepositoryMetadata> metadata = new HashSet<MavenRepositoryMetadata>();
        for (ArtifactRepository repository : repositories) {
            final MavenRepositoryMetadata md = makeRepositoryMetaData(repository,
                                                                      source);
            if (md != null) {
                metadata.add(md);
            }
        }
        return metadata;
    }

    private MavenRepositoryMetadata makeRepositoryMetaData(final ArtifactRepository repository,
                                                           final MavenRepositorySource source) {
        if (repository instanceof LocalRepository) {
            final LocalRepository localRepository = (LocalRepository) repository;
            return new MavenRepositoryMetadata(localRepository.getId(),
                                               MavenSettings.getSettings().getLocalRepository(),
                                               source);
        } else if (repository instanceof RemoteRepository) {
            final RemoteRepository remoteRepository = (RemoteRepository) repository;
            return new MavenRepositoryMetadata(remoteRepository.getId(),
                                               remoteRepository.getUrl(),
                                               source);
        }
        return null;
    }

    @Override
    public Set<MavenRepositoryMetadata> getRepositoriesResolvingArtifact(final GAV gav,
                                                                         final MavenRepositoryMetadata... filter) {
        GAVPreferences gavPreferences = gavPreferencesProvider.get();
        gavPreferences.load();
        if (gavPreferences.isConflictingGAVCheckDisabled()) {
            return Collections.EMPTY_SET;
        }

        final Set<MavenRepositoryMetadata> repositoriesResolvingArtifact = new HashSet<MavenRepositoryMetadata>();

        try {
            //Construct the Project's pom.xml
            final String pomXML = pomContentHandler.toString(new POM(gav));

            final InputStream pomStream = new ByteArrayInputStream(pomXML.getBytes(StandardCharsets.UTF_8));
            final MavenProject mavenProject = MavenProjectLoader.parseMavenPom(pomStream);
            repositoriesResolvingArtifact.addAll(getRepositoriesResolvingArtifact(gav,
                                                                                  mavenProject));

            //Filter results if necessary
            if (filter != null && filter.length > 0) {
                repositoriesResolvingArtifact.retainAll(Arrays.asList(filter));
            }
        } catch (IOException ioe) {
            log.error("Unable to get Remote Repositories for Project '" + gav.toString() + "'. Returning empty Collection. ",
                      ioe);
        }

        return repositoriesResolvingArtifact;
    }

    @Override
    public Set<MavenRepositoryMetadata> getRepositoriesResolvingArtifact(final GAV gav,
                                                                         final Module module,
                                                                         final MavenRepositoryMetadata... filter) {
        GAVPreferences gavPreferences = gavPreferencesProvider.get();
        final PreferenceScopeResolutionStrategyInfo scopeResolutionStrategyInfo = scopeResolutionStrategies.getUserInfoFor(GuvnorPreferenceScopes.PROJECT,
                                                                                                                           module.getEncodedIdentifier());
        gavPreferences.load(scopeResolutionStrategyInfo);
        if (gavPreferences.isConflictingGAVCheckDisabled()) {
            return Collections.EMPTY_SET;
        }

        final Set<MavenRepositoryMetadata> repositoriesResolvingArtifact = new HashSet<MavenRepositoryMetadata>();

        try {
            //Load Project's pom.xml
            final Path pomXMLPath = module.getPomXMLPath();
            final org.uberfire.java.nio.file.Path nioPomXMLPath = Paths.convert(pomXMLPath);
            final String pomXML = ioService.readAllString(nioPomXMLPath);

            final InputStream pomStream = new ByteArrayInputStream(pomXML.getBytes(StandardCharsets.UTF_8));
            final MavenProject mavenProject = MavenProjectLoader.parseMavenPom(pomStream);
            repositoriesResolvingArtifact.addAll(getRepositoriesResolvingArtifact(gav,
                                                                                  mavenProject));

            //Filter results if necessary
            if (filter != null && filter.length > 0) {
                repositoriesResolvingArtifact.retainAll(Arrays.asList(filter));
            }
        } catch (IllegalArgumentException iae) {
            log.error("Unable to get Remote Repositories for Project '" + module.getModuleName() + "'. Returning empty Collection. ",
                      iae);
        } catch (NoSuchFileException nsfe) {
            log.error("Unable to get Remote Repositories for Project '" + module.getModuleName() + "'. Returning empty Collection. ",
                      nsfe);
        } catch (org.uberfire.java.nio.IOException ioe) {
            log.error("Unable to get Remote Repositories for Project '" + module.getModuleName() + "'. Returning empty Collection. ",
                      ioe);
        }

        return repositoriesResolvingArtifact;
    }

    @Override
    public Set<MavenRepositoryMetadata> getRepositoriesResolvingArtifact(final String pomXML,
                                                                         final MavenRepositoryMetadata... filter) {
        GAVPreferences gavPreferences = gavPreferencesProvider.get();
        gavPreferences.load();
        if (gavPreferences.isConflictingGAVCheckDisabled()) {
            return Collections.EMPTY_SET;
        }

        final InputStream pomStream = new ByteArrayInputStream(pomXML.getBytes(StandardCharsets.UTF_8));
        final MavenProject mavenProject = MavenProjectLoader.parseMavenPom(pomStream);
        final GAV gav = new GAV(mavenProject.getGroupId(),
                                mavenProject.getArtifactId(),
                                mavenProject.getVersion());

        final Set<MavenRepositoryMetadata> repositoriesResolvingArtifact = new HashSet<MavenRepositoryMetadata>();
        repositoriesResolvingArtifact.addAll(getRepositoriesResolvingArtifact(gav,
                                                                              mavenProject));

        //Filter results if necessary
        if (filter != null && filter.length > 0) {
            repositoriesResolvingArtifact.retainAll(Arrays.asList(filter));
        }

        return repositoriesResolvingArtifact;
    }

    private Set<MavenRepositoryMetadata> getRepositoriesResolvingArtifact(final GAV gav,
                                                                          final MavenProject mavenProject) {
        ArtifactResult result = null;
        ArtifactRequest artifactRequest = null;

        final String artifactName = gav.toString();
        final Artifact artifact = new DefaultArtifact(artifactName);
        final Aether aether = new Aether(mavenProject);

        final Set<MavenRepositoryMetadata> repositoriesResolvingArtifact = new HashSet<MavenRepositoryMetadata>();
        final Map<MavenRepositorySource, Collection<RemoteRepository>> repositories = getRemoteRepositories(mavenProject);

        //Local Repository
        artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        try {
            result = aether.getSystem().resolveArtifact(aether.getSession(),
                                                        artifactRequest);
            if (result != null && result.isResolved()) {
                final MavenRepositoryMetadata artifactRepositoryMetaData = makeRepositoryMetaData(result.getRepository(),
                                                                                                  MavenRepositorySource.LOCAL);
                if (artifactRepositoryMetaData != null) {
                    repositoriesResolvingArtifact.add(artifactRepositoryMetaData);
                }
            }
        } catch (ArtifactResolutionException are) {
            //Ignore - this means the Artifact could not be resolved against the given RemoteRepository
        }

        //Remote Repositories
        try {
            for (Map.Entry<MavenRepositorySource, Collection<RemoteRepository>> e : repositories.entrySet()) {
                for (ArtifactRepository repository : e.getValue()) {
                    artifactRequest = new ArtifactRequest();
                    artifactRequest.setArtifact(artifact);
                    java.nio.file.Path tempLocalRepositoryBasePath = null;
                    try {
                        // Maven always tries to resolve against LocalRepository first, which is not much use when we want to check
                        // if the Artifact is available on a RemoteRepository. Therefore substitute the default RepositorySystemSession
                        // with one that provides a LocalRepositoryManager that always uses an empty transient LocalRepository to ensure
                        // Maven does not resolve Artifacts locally.
                        artifactRequest.addRepository((RemoteRepository) repository);
                        tempLocalRepositoryBasePath = getRepositoryPath(gav);
                        result = aether.getSystem().resolveArtifact(new MavenRepositorySystemSessionWrapper(tempLocalRepositoryBasePath.toString(),
                                                                                                            aether.getSession()),
                                                                    artifactRequest);

                        if (result != null && result.isResolved()) {
                            final MavenRepositoryMetadata artifactRepositoryMetaData = makeRepositoryMetaData(result.getRepository(),
                                                                                                              e.getKey());
                            if (artifactRepositoryMetaData != null) {
                                repositoriesResolvingArtifact.add(artifactRepositoryMetaData);
                            }
                        }
                    } catch (ArtifactResolutionException are) {
                        //Ignore - this means the Artifact could not be resolved against the given RemoteRepository
                    } finally {
                        tearDownMavenRepository(tempLocalRepositoryBasePath);
                    }
                }
            }
        } catch (IOException ioe) {
            log.error("Error resolving '" + gav.toString() + "' against Repositories. Returning empty Collection. ",
                      ioe);
        }

        return repositoriesResolvingArtifact;
    }

    private Map<MavenRepositorySource, Collection<RemoteRepository>> getRemoteRepositories() {
        final Map<MavenRepositorySource, Collection<RemoteRepository>> repositories = new HashMap<MavenRepositorySource, Collection<RemoteRepository>>();

        //Settings.xml Repositories
        final Collection<RemoteRepository> settingsRepositories = new HashSet<RemoteRepository>(MavenSettings.getMavenRepositoryConfiguration().getRemoteRepositoriesForRequest());
        if (settingsRepositories != null) {
            repositories.put(MavenRepositorySource.SETTINGS,
                             settingsRepositories);
        }

        return repositories;
    }

    private Map<MavenRepositorySource, Collection<RemoteRepository>> getRemoteRepositories(final MavenProject mavenProject) {
        //Get Local and Settings.xml Repositories
        final Map<MavenRepositorySource, Collection<RemoteRepository>> repositories = new HashMap<MavenRepositorySource, Collection<RemoteRepository>>();
        repositories.putAll(getRemoteRepositories());

        //Project's Repositories, includes those in setting.xml
        final Collection<RemoteRepository> projectRepositories = new HashSet<RemoteRepository>(mavenProject.getRemoteProjectRepositories());
        if (projectRepositories != null) {
            //Remove Project Repositories that are in settings.xml
            final Collection<RemoteRepository> settingsRepositories = repositories.get(MavenRepositorySource.SETTINGS);
            removeProjectRepositoriesThatAreInSettings(projectRepositories,
                                                       settingsRepositories);
            repositories.put(MavenRepositorySource.PROJECT,
                             projectRepositories);
        }

        //Project's <distributionManagement> Repositories
        final org.apache.maven.artifact.repository.ArtifactRepository distributionManagementRepository = mavenProject.getDistributionManagementArtifactRepository();
        if (distributionManagementRepository != null) {
            repositories.put(MavenRepositorySource.DISTRIBUTION_MANAGEMENT,
                             new HashSet<RemoteRepository>() {{
                                 add(convertToArtifactRepository(distributionManagementRepository));
                             }});
        }

        return repositories;
    }

    private void removeProjectRepositoriesThatAreInSettings(final Collection<RemoteRepository> projectRepositories,
                                                            final Collection<RemoteRepository> settingsRepositories) {
        final Collection<RemoteRepository> projectRepositoriesToRemove = new HashSet<RemoteRepository>();
        final Iterator<RemoteRepository> projectRepositoryItr = projectRepositories.iterator();
        while (projectRepositoryItr.hasNext()) {
            final RemoteRepository projectRepository = projectRepositoryItr.next();
            for (RemoteRepository settingsRepository : settingsRepositories) {
                if (projectRepository.getId().equals(settingsRepository.getId())) {
                    if (projectRepository.getUrl().equals(settingsRepository.getUrl())) {
                        projectRepositoriesToRemove.add(projectRepository);
                    }
                }
            }
        }
        projectRepositories.removeAll(projectRepositoriesToRemove);
    }

    private RemoteRepository convertToArtifactRepository(final org.apache.maven.artifact.repository.ArtifactRepository artifactRepository) {
        final MavenEmbedder mavenEmbedder = MavenProjectLoader.newMavenEmbedder(MavenSettings.getSettings().isOffline());
        final RemoteRepository.Builder remoteRepoBuilder = new RemoteRepository.Builder(artifactRepository.getId(),
                                                                                        artifactRepository.getLayout().getId(),
                                                                                        artifactRepository.getUrl())
                .setSnapshotPolicy(new RepositoryPolicy(true,
                                                        RepositoryPolicy.UPDATE_POLICY_DAILY,
                                                        RepositoryPolicy.CHECKSUM_POLICY_WARN))
                .setReleasePolicy(new RepositoryPolicy(true,
                                                       RepositoryPolicy.UPDATE_POLICY_ALWAYS,
                                                       RepositoryPolicy.CHECKSUM_POLICY_WARN));

        final Settings settings = MavenSettings.getSettings();
        final Server server = settings.getServer(artifactRepository.getId());

        if (server != null) {
            final Authentication authentication = mavenEmbedder
                    .getMavenSession()
                    .getRepositorySession()
                    .getAuthenticationSelector()
                    .getAuthentication(remoteRepoBuilder.build());
            remoteRepoBuilder.setAuthentication(authentication);
        }

        return remoteRepoBuilder.build();
    }
}
