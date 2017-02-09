/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

package org.kie.workbench.common.screens.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ExampleTargetRepository;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.kie.workbench.common.screens.explorer.backend.server.ExplorerServiceHelper;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryPreferences;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.OrganizationalUnitRepositoryInfo;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.attribute.FileTime;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Service
@ApplicationScoped
public class LibraryServiceImpl implements LibraryService {

    private OrganizationalUnitService ouService;

    private RepositoryService repositoryService;

    private KieProjectService kieProjectService;

    private LibraryPreferences preferences;

    private AuthorizationManager authorizationManager;

    private SessionInfo sessionInfo;

    private ExplorerServiceHelper explorerServiceHelper;

    private KieProjectService projectService;

    private ExamplesService examplesService;

    private IOService ioService;

    public LibraryServiceImpl() {
    }

    @Inject
    public LibraryServiceImpl( final OrganizationalUnitService ouService,
                               final RepositoryService repositoryService,
                               final KieProjectService kieProjectService,
                               final LibraryPreferences preferences,
                               final AuthorizationManager authorizationManager,
                               final SessionInfo sessionInfo,
                               final ExplorerServiceHelper explorerServiceHelper,
                               final KieProjectService projectService,
                               final ExamplesService examplesService,
                               @Named("ioStrategy") final IOService ioService ) {
        this.ouService = ouService;
        this.repositoryService = repositoryService;
        this.kieProjectService = kieProjectService;
        this.preferences = preferences;
        this.authorizationManager = authorizationManager;
        this.sessionInfo = sessionInfo;
        this.explorerServiceHelper = explorerServiceHelper;
        this.projectService = projectService;
        this.examplesService = examplesService;
        this.ioService = ioService;
    }

    @Override
    public OrganizationalUnitRepositoryInfo getDefaultOrganizationalUnitRepositoryInfo() {
        return getOrganizationalUnitRepositoryInfo( getDefaultOrganizationalUnit() );
    }

    @Override
    public OrganizationalUnitRepositoryInfo getOrganizationalUnitRepositoryInfo( final OrganizationalUnit selectedOrganizationalUnit ) {
        final Repository selectedRepository = getDefaultRepository( selectedOrganizationalUnit );
        final List<OrganizationalUnit> organizationalUnits = getOrganizationalUnits();
        final OrganizationalUnit organizationalUnit = getOrganizationalUnit( selectedOrganizationalUnit.getIdentifier(), organizationalUnits ).get();
        final List<Repository> repositories = new ArrayList<>( organizationalUnit.getRepositories() );

        return new OrganizationalUnitRepositoryInfo( organizationalUnits,
                                                     organizationalUnit,
                                                     repositories,
                                                     selectedRepository );
    }

    @Override
    public LibraryInfo getLibraryInfo( final Repository selectedRepository ) {
        final Set<Project> projects = getProjects( selectedRepository );
        return new LibraryInfo( "master", projects );
    }

    @Override
    public KieProject createProject( final String projectName,
                                     final String selectedOrganizationalUnitIdentifier,
                                     final String baseURL ) {
        final OrganizationalUnit selectedOrganizationalUnit = getOrganizationalUnit( selectedOrganizationalUnitIdentifier, getOrganizationalUnits() ).get();
        final Repository selectedRepository = getDefaultRepository( selectedOrganizationalUnit );
        final Path selectedRepositoryRootPath = selectedRepository.getRoot();
        final LibraryPreferences preferences = getPreferences();

        final GAV gav = createGAV( projectName, preferences );
        final POM pom = createPOM( projectName, preferences, gav );
        final DeploymentMode mode = DeploymentMode.VALIDATED;

        final KieProject kieProject = kieProjectService.newProject( selectedRepositoryRootPath, pom, baseURL, mode );

        return kieProject;
    }

    @Override
    public Boolean thereIsAProjectInTheWorkbench() {
        return getOrganizationalUnits().stream()
                .flatMap( organizationalUnit -> organizationalUnit.getRepositories().stream()
                        .filter( repository -> authorizationManager.authorize( repository, sessionInfo.getIdentity() ) ) )
                .flatMap( repository -> repository.getBranches().stream()
                        .map( branch -> kieProjectService.getProjects( repository, branch ) ) )
                .anyMatch( projects -> projects != null && !projects.isEmpty() );
    }

    @Override
    public List<AssetInfo> getProjectAssets( final Project project ) {
        checkNotNull( "project", project );
        final Package defaultPackage = projectService.resolveDefaultPackage( project );
        final List<FolderItem> assets = explorerServiceHelper.getAssetsRecursively( defaultPackage, new ActiveOptions( Option.BUSINESS_CONTENT ) );

        return assets.stream()
                .map( asset -> {
                    final FileTime lastModifiedFileTime = (FileTime) getAttribute( asset, LibraryService.LAST_MODIFIED_TIME ).get();
                    final FileTime createdFileTime = (FileTime) getAttribute( asset, LibraryService.CREATED_TIME ).get();
                    final Date lastModifiedTime = new Date( lastModifiedFileTime.toMillis() );
                    final Date createdTime = new Date( createdFileTime.toMillis() );

                    return new AssetInfo( asset, lastModifiedTime, createdTime );
                } )
                .collect( Collectors.toList() );
    }

    @Override
    public Boolean hasProjects( final Repository repository ) {
        return !getProjects( repository ).isEmpty();
    }

    @Override
    public Boolean hasAssets( final Project project ) {
        checkNotNull( "project", project );
        final Package defaultPackage = projectService.resolveDefaultPackage( project );
        return explorerServiceHelper.hasAssets( defaultPackage );
    }

    @Override
    public Set<ExampleProject> getExampleProjects() {
        final String importProjectsUrl = getPreferences().getImportProjectsUrl();
        final ExampleRepository repository = importProjectsUrl == null  || importProjectsUrl.isEmpty()
                ? examplesService.getPlaygroundRepository()
                : new ExampleRepository( importProjectsUrl );

        return examplesService.getProjects( repository );
    }

    @Override
    public Project importProject( final OrganizationalUnit organizationalUnit,
                                  final Repository repository,
                                  final ExampleProject exampleProject ) {
        final ExampleOrganizationalUnit exampleOrganizationalUnit = new ExampleOrganizationalUnit( organizationalUnit.getName() );
        final ExampleTargetRepository exampleRepository = new ExampleTargetRepository( repository.getAlias() );
        final List<ExampleProject> exampleProjects = Collections.singletonList( exampleProject );

        final ProjectContextChangeEvent projectContextChangeEvent = examplesService.setupExamples( exampleOrganizationalUnit,
                                                                                                   exampleRepository,
                                                                                                   exampleProjects );

        return projectContextChangeEvent.getProject();
    }

    LibraryPreferences getPreferences() {
        preferences.load();
        return preferences;
    }

    POM createPOM( final String projectName,
                   final LibraryPreferences preferences,
                   final GAV gav ) {
        return new POM( projectName, preferences.getProjectDescription(), gav );
    }

    GAV createGAV( final String projectName,
                   final LibraryPreferences preferences ) {
        return new GAV( preferences.getProjectGroupId(), projectName, preferences.getProjectVersion() );
    }

    private Optional<Object> getAttribute( final FolderItem asset,
                                           final String attribute ) {
        final Map<String, Object> attributes = ioService.readAttributes( Paths.convert( (Path) asset.getItem() ) );
        return attributes.entrySet().stream()
                .filter( entry -> attribute.equals( entry.getKey() ) )
                .map( Map.Entry::getValue )
                .findFirst();
    }

    private List<OrganizationalUnit> getOrganizationalUnits() {
        return new ArrayList<>( ouService.getOrganizationalUnits() );
    }

    private Set<Project> getProjects( final Repository repository ) {
        return kieProjectService.getProjects( repository, getPreferences().getProjectDefaultBranch() );
    }

    private OrganizationalUnit getDefaultOrganizationalUnit() {
        final List<OrganizationalUnit> organizationalUnits = getOrganizationalUnits();
        final LibraryPreferences preferences = getPreferences();
        final String defaultOUIdentifier = preferences.getOuIdentifier();
        final Optional<OrganizationalUnit> defaultOU = getOrganizationalUnit( defaultOUIdentifier, organizationalUnits );

        if ( defaultOU.isPresent() ) {
            return defaultOU.get();
        } else if ( !organizationalUnits.isEmpty() ) {
            return organizationalUnits.get( 0 );
        } else {
            return createDefaultOrganizationalUnit();
        }
    }

    private Repository getDefaultRepository( final OrganizationalUnit ou ) {
        final String primaryDefaultRepositoryName = getPrimaryDefaultRepositoryName( ou );
        final String secondaryDefaultRepositoryName = getSecondaryDefaultRepositoryName( ou );

        final List<Repository> repositories = new ArrayList<>( ou.getRepositories() );
        final Optional<Repository> primaryRepository = repositories.stream()
                .filter( r -> r.getAlias().equalsIgnoreCase( primaryDefaultRepositoryName ) )
                .findAny();
        final Optional<Repository> secondaryRepository = repositories.stream()
                .filter( r -> r.getAlias().equalsIgnoreCase( secondaryDefaultRepositoryName ) )
                .findAny();

        if ( primaryRepository.isPresent() ) {
            return primaryRepository.get();
        } else if ( secondaryRepository.isPresent() ) {
            return secondaryRepository.get();
        } else if ( !repositories.isEmpty() ) {
            return repositories.get( 0 );
        } else {
            if ( repositoryService.getRepository( primaryDefaultRepositoryName ) == null ) {
                return createDefaultRepository( ou, primaryDefaultRepositoryName );
            } else {
                return createDefaultRepository( ou, secondaryDefaultRepositoryName );
            }
        }
    }

    private OrganizationalUnit createDefaultOrganizationalUnit() {
        final LibraryPreferences preferences = getPreferences();
        return ouService.createOrganizationalUnit( preferences.getOuIdentifier(),
                                                   preferences.getOuOwner(),
                                                   preferences.getOuGroupId() );
    }

    private Repository createDefaultRepository( final OrganizationalUnit ou,
                                                final String repositoryName ) {
        final String scheme = getPreferences().getRepositoryDefaultScheme();
        final RepositoryEnvironmentConfigurations configuration = getDefaultRepositoryEnvironmentConfigurations();

        return repositoryService.createRepository( ou,
                                                   scheme,
                                                   repositoryName,
                                                   configuration );
    }

    private Optional<OrganizationalUnit> getOrganizationalUnit( final String identifier,
                                                                final Collection<OrganizationalUnit> organizationalUnits ) {
        return organizationalUnits.stream()
                .filter( p -> p.getIdentifier().equalsIgnoreCase( identifier ) )
                .findFirst();
    }

    private String getPrimaryDefaultRepositoryName( final OrganizationalUnit ou ) {
        return getPreferences().getRepositoryAlias();
    }

    private String getSecondaryDefaultRepositoryName( final OrganizationalUnit ou ) {
        return ou.getIdentifier() + "-" + getPreferences().getRepositoryAlias();
    }

    private RepositoryEnvironmentConfigurations getDefaultRepositoryEnvironmentConfigurations() {
        final RepositoryEnvironmentConfigurations configuration = new RepositoryEnvironmentConfigurations();
        return configuration;
    }
}

