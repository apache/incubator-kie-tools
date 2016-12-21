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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

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
import org.kie.workbench.common.screens.explorer.backend.server.ExplorerServiceHelper;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryPreferences;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

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

    private IOService ioService;

    public LibraryServiceImpl() {
    }

    @Inject
    public LibraryServiceImpl( OrganizationalUnitService ouService,
                               RepositoryService repositoryService,
                               KieProjectService kieProjectService,
                               LibraryPreferences preferences,
                               AuthorizationManager authorizationManager,
                               SessionInfo sessionInfo,
                               ExplorerServiceHelper explorerServiceHelper,
                               KieProjectService projectService,
                               @Named("ioStrategy") IOService ioService ) {
        this.ouService = ouService;
        this.repositoryService = repositoryService;
        this.kieProjectService = kieProjectService;
        this.preferences = preferences;
        this.authorizationManager = authorizationManager;
        this.sessionInfo = sessionInfo;
        this.explorerServiceHelper = explorerServiceHelper;
        this.projectService = projectService;
        this.ioService = ioService;
    }

    @Override
    public Collection<OrganizationalUnit> getOrganizationalUnits() {
        return ouService.getOrganizationalUnits();
    }

    @Override
    public OrganizationalUnit getDefaultOrganizationalUnit() {
        final Collection<OrganizationalUnit> organizationalUnits = getOrganizationalUnits();
        final LibraryPreferences preferences = getPreferences();

        return getOU( preferences.getOuIdentifier(), organizationalUnits ).orElseGet( this::createDefaultOU );
    }

    private Optional<OrganizationalUnit> getOU( String ouIdentifier,
                                                Collection<OrganizationalUnit> organizationalUnits ) {
        Optional<OrganizationalUnit> targetOU = organizationalUnits.stream()
                .filter( p -> p.getIdentifier().equalsIgnoreCase( ouIdentifier ) ).findFirst();
        return targetOU;
    }

    private OrganizationalUnit createDefaultOU() {
        LibraryPreferences preferences = getPreferences();

        return ouService.createOrganizationalUnit( preferences.getOuIdentifier(), preferences.getOuOwner(),
                                                   preferences.getOuGroupId() );
    }

    @Override
    public LibraryInfo getDefaultLibraryInfo() {

        OrganizationalUnit defaultOU = getDefaultOrganizationalUnit();

        LibraryInfo libraryInfo = new LibraryInfo(
                defaultOU,
                defaultOU,
                getProjects( defaultOU ),
                getOrganizationalUnits(),
                getPreferences().getOuAlias() );

        return libraryInfo;
    }

    @Override
    public LibraryInfo getLibraryInfo( String selectedOuIdentifier ) {
        Collection<OrganizationalUnit> organizationalUnits = getOrganizationalUnits();
        OrganizationalUnit defaultOU = getDefaultOrganizationalUnit();
        OrganizationalUnit selectedOU = getOU( selectedOuIdentifier, organizationalUnits ).get();

        LibraryInfo libraryInfo = new LibraryInfo(
                defaultOU,
                selectedOU,
                getProjects( selectedOU ),
                organizationalUnits,
                getPreferences().getOuAlias() );

        return libraryInfo;

    }

    @Override
    public KieProject newProject( String projectName,
                                  String selectOu,
                                  String baseURL ) {
        Collection<OrganizationalUnit> organizationalUnits = getOrganizationalUnits();
        OrganizationalUnit selectedOU = getOU( selectOu, organizationalUnits ).get();
        Repository repository = getDefaultRepository( selectedOU );
        Path repoRoot = repository.getRoot();
        LibraryPreferences preferences = getPreferences();

        GAV gav = createGAV( projectName, preferences );

        POM pom = createPOM( projectName, preferences, gav );
        DeploymentMode mode = DeploymentMode.VALIDATED;

        KieProject kieProject = kieProjectService.newProject( repoRoot, pom, baseURL, mode );

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
    public List<FolderItem> getProjectAssets( final Project project ) {
        checkNotNull( "project", project );
        final Package defaultPackage = projectService.resolveDefaultPackage( project );
        return explorerServiceHelper.getAssetsRecursively( defaultPackage, new ActiveOptions( Option.BUSINESS_CONTENT ) );
    }

    POM createPOM( String projectName,
                   LibraryPreferences preferences,
                   GAV gav ) {
        return new POM( projectName, preferences.getProjectDescription(), gav );
    }

    GAV createGAV( String projectName,
                   LibraryPreferences preferences ) {
        return new GAV( preferences.getProjectGroupId(), projectName, preferences.getProjectVersion() );
    }

    Set<Project> getProjects( OrganizationalUnit ou ) {

        Repository defaultRepository = getDefaultRepository( ou );

        return kieProjectService.getProjects( defaultRepository, getPreferences().getProjectDefaultBranch() );
    }

    Repository getDefaultRepository( OrganizationalUnit ou ) {
        String defaultRepositoryName = getDefaultRepositoryName( ou );
        Optional<Repository> repo = ou.getRepositories().stream()
                .filter( r -> r.getAlias().equalsIgnoreCase( defaultRepositoryName ) )
                .findAny();
        if ( !repo.isPresent() ) {
            return createDefaultRepo( ou );
        }
        return repo.get();
    }

    private Repository createDefaultRepo( OrganizationalUnit ou ) {
        LibraryPreferences preferences = getPreferences();

        final String scheme = preferences.getRepositoryDefaultScheme();
        final String alias = getDefaultRepositoryName( ou );
        final RepositoryEnvironmentConfigurations configuration = getDefaultRepositoryEnvironmentConfigurations();

        return repositoryService.createRepository( ou, scheme, alias, configuration );
    }

    RepositoryEnvironmentConfigurations getDefaultRepositoryEnvironmentConfigurations() {
        final RepositoryEnvironmentConfigurations configuration = new RepositoryEnvironmentConfigurations();
        configuration.setManaged( true );
        return configuration;
    }

    String getDefaultRepositoryName( OrganizationalUnit ou ) {
        return ou.getIdentifier() + "-" + getPreferences().getRepositoryAlias();
    }

    LibraryPreferences getPreferences() {
        preferences.load();
        return preferences;
    }
}

