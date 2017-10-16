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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
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
import org.guvnor.structure.security.OrganizationalUnitAction;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ExampleTargetRepository;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.kie.workbench.common.screens.explorer.backend.server.ExplorerServiceHelper;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.OrganizationalUnitRepositoryInfo;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
import org.kie.workbench.common.screens.library.api.index.LibraryValueFileNameIndexTerm;
import org.kie.workbench.common.screens.library.api.index.LibraryValueProjectRootPathIndexTerm;
import org.kie.workbench.common.screens.library.api.preferences.LibraryInternalPreferences;
import org.kie.workbench.common.screens.library.api.preferences.LibraryPreferences;
import org.kie.workbench.common.screens.projecteditor.util.NewProjectUtils;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.attribute.FileTime;
import org.uberfire.paging.PageResponse;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Service
@ApplicationScoped
public class LibraryServiceImpl implements LibraryService {

    private static final Logger log = LoggerFactory.getLogger(LibraryServiceImpl.class);

    private RefactoringQueryService refactoringQueryService;
    private OrganizationalUnitService ouService;
    private RepositoryService repositoryService;
    private KieProjectService kieProjectService;
    private LibraryPreferences preferences;

    private LibraryInternalPreferences internalPreferences;

    private AuthorizationManager authorizationManager;
    private SessionInfo sessionInfo;
    private ExplorerServiceHelper explorerServiceHelper;
    private KieProjectService projectService;
    private ExamplesService examplesService;
    private IOService ioService;

    public LibraryServiceImpl() {
    }

    @Inject
    public LibraryServiceImpl(final OrganizationalUnitService ouService,
                              final RepositoryService repositoryService,
                              final KieProjectService kieProjectService,
                              final RefactoringQueryService refactoringQueryService,
                              final LibraryPreferences preferences,
                              final AuthorizationManager authorizationManager,
                              final SessionInfo sessionInfo,
                              final ExplorerServiceHelper explorerServiceHelper,
                              final KieProjectService projectService,
                              final ExamplesService examplesService,
                              @Named("ioStrategy") final IOService ioService,
                              final LibraryInternalPreferences internalPreferences) {
        this.ouService = ouService;
        this.repositoryService = repositoryService;
        this.kieProjectService = kieProjectService;
        this.refactoringQueryService = refactoringQueryService;
        this.preferences = preferences;
        this.authorizationManager = authorizationManager;
        this.sessionInfo = sessionInfo;
        this.explorerServiceHelper = explorerServiceHelper;
        this.projectService = projectService;
        this.examplesService = examplesService;
        this.ioService = ioService;
        this.internalPreferences = internalPreferences;
    }

    @Override
    public OrganizationalUnitRepositoryInfo getDefaultOrganizationalUnitRepositoryInfo() {
        return getOrganizationalUnitRepositoryInfo(getDefaultOrganizationalUnit());
    }

    @Override
    public OrganizationalUnitRepositoryInfo getOrganizationalUnitRepositoryInfo(final OrganizationalUnit selectedOrganizationalUnit) {
        if (selectedOrganizationalUnit == null) {
            return null;
        }

        final Repository selectedRepository = getDefaultRepository(selectedOrganizationalUnit);
        final List<OrganizationalUnit> organizationalUnits = getOrganizationalUnits();
        final OrganizationalUnit organizationalUnit = getOrganizationalUnit(selectedOrganizationalUnit.getIdentifier(),
                                                                            organizationalUnits).get();
        final List<Repository> repositories = new ArrayList<>(organizationalUnit.getRepositories());

        return new OrganizationalUnitRepositoryInfo(organizationalUnits,
                                                    organizationalUnit,
                                                    repositories,
                                                    selectedRepository);
    }

    @Override
    public LibraryInfo getLibraryInfo(final Repository selectedRepository,
                                      final String branch) {
        final List<Project> projects = getProjects(selectedRepository,
                                                   branch);
        return new LibraryInfo(branch,
                               projects);
    }

    @Override
    public KieProject createProject(final String projectName,
                                    final OrganizationalUnit selectedOrganizationalUnit,
                                    final Repository selectedRepository,
                                    final String baseURL,
                                    final String projectDescription,
                                    final DeploymentMode mode) {
        final Path selectedRepositoryRootPath = selectedRepository.getRoot();

        final GAV gav = createGAV(projectName,
                                  selectedOrganizationalUnit);
        final POM pom = createPOM(projectName,
                                  projectDescription,
                                  gav);

        final KieProject kieProject = kieProjectService.newProject(selectedRepositoryRootPath,
                                                                   pom,
                                                                   baseURL,
                                                                   mode);

        return kieProject;
    }

    @Override
    public Boolean thereIsAProjectInTheWorkbench() {
        return getOrganizationalUnits().stream()
                .flatMap(organizationalUnit -> organizationalUnit.getRepositories().stream()
                        .filter(repository -> authorizationManager.authorize(repository,
                                                                             sessionInfo.getIdentity())))
                .flatMap(repository -> repository.getBranches().stream()
                        .map(branch -> kieProjectService.getProjects(repository,
                                                                     branch)))
                .anyMatch(projects -> projects != null && !projects.isEmpty());
    }

    @Override
    public List<AssetInfo> getProjectAssets(final ProjectAssetsQuery query) {
        checkNotNull("query",
                     query);

        final boolean projectStillExists = ioService.exists(Paths.convert(query.getProject().getRootPath()));
        if (!projectStillExists) {
            return Collections.emptyList();
        }

        final HashSet<ValueIndexTerm> queryTerms = new HashSet<>();

        queryTerms.add(new LibraryValueProjectRootPathIndexTerm(query.getProject().getRootPath().toURI()));

        if (query.hasFilter()) {
            queryTerms.add(new LibraryValueFileNameIndexTerm("*" + query.getFilter() + "*",
                                                             ValueIndexTerm.TermSearchType.WILDCARD));
        }

        final PageResponse<RefactoringPageRow> findRulesByProjectQuery = refactoringQueryService.query(new RefactoringPageRequest(FindAllLibraryAssetsQuery.NAME,
                                                                                                                                  queryTerms,
                                                                                                                                  query.getStartIndex(),
                                                                                                                                  query.getAmount()));
        final List<FolderItem> assets = findRulesByProjectQuery
                .getPageRowList()
                .stream()
                .map(row -> {
                    final Path path = (Path) row.getValue();
                    return new FolderItem(path,
                                          path.getFileName(),
                                          FolderItemType.FILE,
                                          false,
                                          Paths.readLockedBy(path),
                                          Collections.<String>emptyList(),
                                          explorerServiceHelper.getRestrictedOperations(path));
                })
                .collect(Collectors.toList());

        return assets.stream()
                .map(asset -> {
                    AssetInfo info = null;
                    try {
                        final Map<String, Object> attributes = ioService.readAttributes(Paths.convert((Path) asset.getItem()));

                        final FileTime lastModifiedFileTime = (FileTime) getAttribute(LibraryService.LAST_MODIFIED_TIME,
                                                                                      attributes).get();
                        final FileTime createdFileTime = (FileTime) getAttribute(LibraryService.CREATED_TIME,
                                                                                 attributes).get();
                        final Date lastModifiedTime = new Date(lastModifiedFileTime.toMillis());
                        final Date createdTime = new Date(createdFileTime.toMillis());
                        info = new AssetInfo(asset,
                                             lastModifiedTime,
                                             createdTime);
                    } catch (NoSuchFileException nfe) {
                        log.debug("File '" + asset.getFileName() + "' in LibraryIndex but not VFS. Suspected deletion. Skipping.");
                    }
                    return Optional.ofNullable(info);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean hasProjects(final Repository repository,
                               final String branch) {
        return !getProjects(repository,
                            branch).isEmpty();
    }

    @Override
    public Boolean hasAssets(final Project project) {
        checkNotNull("project",
                     project);
        final Package defaultPackage = projectService.resolveDefaultPackage(project);
        return explorerServiceHelper.hasAssets(defaultPackage);
    }

    @Override
    public Set<ExampleProject> getExampleProjects() {
        final String importProjectsUrl = getCustomImportProjectsUrl();
        final ExampleRepository repository = importProjectsUrl == null || importProjectsUrl.isEmpty()
                ? examplesService.getPlaygroundRepository()
                : new ExampleRepository(importProjectsUrl);

        return examplesService.getProjects(repository);
    }

    @Override
    public Project importProject(final ExampleProject exampleProject) {
        final OrganizationalUnit ou = getDefaultOrganizationalUnit();
        return importProject(ou,
                             getDefaultRepository(ou),
                             "master",
                             exampleProject);
    }

    @Override
    public Project importProject(final OrganizationalUnit organizationalUnit,
                                 final Repository repository,
                                 final String branch,
                                 final ExampleProject exampleProject) {
        final ExampleOrganizationalUnit exampleOrganizationalUnit = new ExampleOrganizationalUnit(organizationalUnit.getName());
        final ExampleTargetRepository exampleRepository = new ExampleTargetRepository(repository.getAlias());
        final List<ExampleProject> exampleProjects = Collections.singletonList(exampleProject);

        final ProjectContextChangeEvent projectContextChangeEvent = examplesService.setupExamples(exampleOrganizationalUnit,
                                                                                                  exampleRepository,
                                                                                                  branch,
                                                                                                  exampleProjects);

        return projectContextChangeEvent.getProject();
    }

    @Override
    public List<OrganizationalUnit> getOrganizationalUnits() {
        return new ArrayList<>(ouService.getOrganizationalUnits());
    }

    @Override
    public GAV createGAV(final String projectName,
                         final OrganizationalUnit selectedOrganizationalUnit) {
        final LibraryPreferences preferences = getPreferences();
        final String artifactId = NewProjectUtils.sanitizeProjectName(projectName);
        return new GAV(selectedOrganizationalUnit.getDefaultGroupId(),
                       artifactId,
                       preferences.getProjectPreferences().getVersion());
    }

    String getCustomImportProjectsUrl() {
        return System.getProperty("org.kie.project.examples.repository.url");
    }

    LibraryPreferences getPreferences() {
        preferences.load();
        return preferences;
    }

    LibraryInternalPreferences getInternalPreferences() {
        internalPreferences.load();
        return internalPreferences;
    }

    POM createPOM(final String projectName,
                  final String projectDescription,
                  final GAV gav) {
        return new POM(projectName,
                       projectDescription,
                       gav);
    }

    private Optional<Object> getAttribute(final String attribute,
                                          final Map<String, Object> attributes) {
        return attributes.entrySet().stream()
                .filter(entry -> attribute.equals(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    private List<Project> getProjects(final Repository repository,
                                      final String branch) {
        return new ArrayList<>(kieProjectService.getProjects(repository,
                                                             branch));
    }

    private OrganizationalUnit getDefaultOrganizationalUnit() {
        String defaultOUIdentifier = getInternalPreferences().getLastOpenedOrganizationalUnit();
        if (defaultOUIdentifier == null || defaultOUIdentifier.isEmpty()) {
            defaultOUIdentifier = getPreferences().getOrganizationalUnitPreferences().getName();
        }

        final List<OrganizationalUnit> organizationalUnits = getOrganizationalUnits();
        final Optional<OrganizationalUnit> defaultOU = getOrganizationalUnit(defaultOUIdentifier,
                                                                             organizationalUnits);

        if (defaultOU.isPresent()) {
            return defaultOU.get();
        } else if (!organizationalUnits.isEmpty()) {
            return organizationalUnits.get(0);
        } else {
            return createDefaultOrganizationalUnit();
        }
    }

    private Repository getDefaultRepository(final OrganizationalUnit ou) {
        final String lastOpenedRepositoryName = getInternalPreferences().getLastOpenedRepository();
        final String primaryDefaultRepositoryName = getPrimaryDefaultRepositoryName(ou);
        final String secondaryDefaultRepositoryName = getSecondaryDefaultRepositoryName(ou);

        final List<Repository> repositories = new ArrayList<>(ou.getRepositories());
        final Optional<Repository> lastOpenedRepository = getRepositoryByName(lastOpenedRepositoryName,
                                                                              repositories);
        final Optional<Repository> primaryRepository = getRepositoryByName(primaryDefaultRepositoryName,
                                                                           repositories);
        final Optional<Repository> secondaryRepository = getRepositoryByName(secondaryDefaultRepositoryName,
                                                                             repositories);

        if (lastOpenedRepository.isPresent()) {
            return lastOpenedRepository.get();
        } else if (primaryRepository.isPresent()) {
            return primaryRepository.get();
        } else if (secondaryRepository.isPresent()) {
            return secondaryRepository.get();
        } else if (!repositories.isEmpty()) {
            return repositories.get(0);
        } else {
            if (repositoryService.getRepository(primaryDefaultRepositoryName) == null) {
                return createDefaultRepository(ou,
                                               primaryDefaultRepositoryName);
            } else if (repositoryService.getRepository(secondaryDefaultRepositoryName) == null) {
                return createDefaultRepository(ou,
                                               secondaryDefaultRepositoryName);
            } else {
                int i = 1;
                while (repositoryService.getRepository(secondaryDefaultRepositoryName + "-" + ++i) != null) {
                }
                return createDefaultRepository(ou,
                                               secondaryDefaultRepositoryName + "-" + i);
            }
        }
    }

    private Optional<Repository> getRepositoryByName(String lastOpenedRepositoryName,
                                                     List<Repository> repositories) {
        return repositories.stream()
                .filter(r -> r.getAlias().equalsIgnoreCase(lastOpenedRepositoryName))
                .findAny();
    }

    private OrganizationalUnit createDefaultOrganizationalUnit() {
        if (!authorizationManager.authorize(OrganizationalUnit.RESOURCE_TYPE,
                                            OrganizationalUnitAction.CREATE,
                                            sessionInfo.getIdentity())) {
            return null;
        }

        final LibraryPreferences preferences = getPreferences();
        return ouService.createOrganizationalUnit(preferences.getOrganizationalUnitPreferences().getName(),
                                                  preferences.getOrganizationalUnitPreferences().getOwner(),
                                                  preferences.getOrganizationalUnitPreferences().getGroupId());
    }

    private Repository createDefaultRepository(final OrganizationalUnit ou,
                                               final String repositoryName) {
        final String scheme = getPreferences().getRepositoryPreferences().getScheme();
        final RepositoryEnvironmentConfigurations configuration = getDefaultRepositoryEnvironmentConfigurations();

        return repositoryService.createRepository(ou,
                                                  scheme,
                                                  repositoryName,
                                                  configuration);
    }

    private Optional<OrganizationalUnit> getOrganizationalUnit(final String identifier,
                                                               final Collection<OrganizationalUnit> organizationalUnits) {
        return organizationalUnits.stream()
                .filter(p -> p.getIdentifier().equalsIgnoreCase(identifier))
                .findFirst();
    }

    private String getPrimaryDefaultRepositoryName(final OrganizationalUnit ou) {
        return getPreferences().getRepositoryPreferences().getName();
    }

    String getSecondaryDefaultRepositoryName(final OrganizationalUnit ou) {
        final String sanitizedOuIdentifier = ou.getIdentifier().replaceAll("[^A-Za-z0-9]",
                                                                           "-");
        final String repositoryNameSuffix = getPreferences().getRepositoryPreferences().getName();
        final String repositoryName = sanitizedOuIdentifier + "-" + repositoryNameSuffix;

        return repositoryName;
    }

    private RepositoryEnvironmentConfigurations getDefaultRepositoryEnvironmentConfigurations() {
        final RepositoryEnvironmentConfigurations configuration = new RepositoryEnvironmentConfigurations();
        return configuration;
    }
}

