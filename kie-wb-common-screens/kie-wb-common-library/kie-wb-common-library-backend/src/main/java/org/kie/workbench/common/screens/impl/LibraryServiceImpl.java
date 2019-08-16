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

import java.net.URI;
import java.net.URISyntaxException;
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
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.project.utils.NewWorkspaceProjectUtils;
import org.guvnor.structure.backend.repositories.ConfiguredRepositories;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.config.BranchPermissions;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.RepositoryUpdatedEvent;
import org.guvnor.structure.security.OrganizationalUnitAction;
import org.guvnor.structure.security.RepositoryAction;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.exception.UnauthorizedException;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.kie.workbench.common.screens.explorer.backend.server.ExplorerServiceHelper;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.library.api.AssetInfo;
import org.kie.workbench.common.screens.library.api.AssetQueryResult;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.OrganizationalUnitRepositoryInfo;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
import org.kie.workbench.common.screens.library.api.preferences.LibraryPreferences;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindAllLibraryAssetsQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.LibraryValueFileExtensionIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.LibraryValueFileNameIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.LibraryValueRepositoryRootIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.ext.security.management.api.service.UserManagerService;
import org.uberfire.ext.security.management.impl.SearchRequestImpl;
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
    private LibraryPreferences preferences;
    private AuthorizationManager authorizationManager;
    private SessionInfo sessionInfo;
    private ExplorerServiceHelper explorerServiceHelper;
    private WorkspaceProjectService projectService;
    private KieModuleService moduleService;
    private ExamplesService examplesService;
    private IOService ioService;
    private UserManagerService userManagerService;
    private IndexStatusOracle indexOracle;
    private RepositoryService repoService;
    private PathUtil pathUtil;
    private Event<NewBranchEvent> newBranchEvent;
    private ConfiguredRepositories configuredRepositories;
    private Event<RepositoryUpdatedEvent> repositoryUpdatedEvent;
    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;
    private ClusterService clusterService;

    public LibraryServiceImpl() {
    }

    @Inject
    public LibraryServiceImpl(final OrganizationalUnitService ouService,
                              final RefactoringQueryService refactoringQueryService,
                              final LibraryPreferences preferences,
                              final AuthorizationManager authorizationManager,
                              final SessionInfo sessionInfo,
                              final ExplorerServiceHelper explorerServiceHelper,
                              final WorkspaceProjectService projectService,
                              final KieModuleService moduleService,
                              final ExamplesService examplesService,
                              @Named("ioStrategy") final IOService ioService,
                              final UserManagerService userManagerService,
                              final IndexStatusOracle indexOracle,
                              final RepositoryService repoService,
                              final PathUtil pathUtil,
                              final Event<NewBranchEvent> newBranchEvent,
                              final ConfiguredRepositories configuredRepositories,
                              final Event<RepositoryUpdatedEvent> repositoryUpdatedEvent,
                              final SpaceConfigStorageRegistry spaceConfigStorageRegistry,
                              final ClusterService clusterService) {
        this.ouService = ouService;
        this.refactoringQueryService = refactoringQueryService;
        this.preferences = preferences;
        this.authorizationManager = authorizationManager;
        this.sessionInfo = sessionInfo;
        this.explorerServiceHelper = explorerServiceHelper;
        this.projectService = projectService;
        this.moduleService = moduleService;
        this.examplesService = examplesService;
        this.ioService = ioService;
        this.userManagerService = userManagerService;
        this.indexOracle = indexOracle;
        this.repoService = repoService;
        this.pathUtil = pathUtil;
        this.newBranchEvent = newBranchEvent;
        this.configuredRepositories = configuredRepositories;
        this.repositoryUpdatedEvent = repositoryUpdatedEvent;
        this.spaceConfigStorageRegistry = spaceConfigStorageRegistry;
        this.clusterService = clusterService;
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

        final List<OrganizationalUnit> organizationalUnits = getOrganizationalUnits();
        final OrganizationalUnit organizationalUnit = getOrganizationalUnit(selectedOrganizationalUnit.getIdentifier(),
                                                                            organizationalUnits).get();
        final List<Repository> repositories = new ArrayList<>(organizationalUnit.getRepositories());

        return new OrganizationalUnitRepositoryInfo(organizationalUnits,
                                                    organizationalUnit,
                                                    repositories);
    }

    @Override
    public LibraryInfo getLibraryInfo(final OrganizationalUnit organizationalUnit) {
        final List<WorkspaceProject> projects = projectService.getAllWorkspaceProjects(organizationalUnit).stream()
                .filter(p -> userCanReadProject(p))
                .collect(Collectors.toList());

        return new LibraryInfo(projects);
    }

    private boolean userCanReadProject(final WorkspaceProject project) {
        return authorizationManager.authorize(project.getRepository(),
                                              RepositoryAction.READ,
                                              sessionInfo.getIdentity())
                || project.getRepository().getContributors().stream().anyMatch(c -> c.getUsername().equals(sessionInfo.getIdentity().getIdentifier()));
    }

    @Override
    public WorkspaceProject createProject(final String projectName,
                                          final OrganizationalUnit selectedOrganizationalUnit,
                                          final String projectDescription,
                                          final DeploymentMode deploymentMode) {

        final GAV gav = createGAV(projectName,
                                  selectedOrganizationalUnit);
        final POM pom = createPOM(projectName,
                                  projectDescription,
                                  gav);

        return projectService.newProject(selectedOrganizationalUnit,
                                         pom,
                                         deploymentMode);
    }

    @Override
    public WorkspaceProject createProject(final OrganizationalUnit activeOrganizationalUnit,
                                          final POM pom,
                                          final DeploymentMode mode) {
        return projectService.newProject(activeOrganizationalUnit,
                                         pom,
                                         mode,
                                         getRepositoryContributors(activeOrganizationalUnit));
    }

    private List<Contributor> getRepositoryContributors(final OrganizationalUnit organizationalUnit) {
        final OrganizationalUnit ou = ouService.getOrganizationalUnit(organizationalUnit.getName());
        final Collection<Contributor> spaceContributors = ou.getContributors();

        final List<Contributor> contributors = spaceContributors.stream()
                .filter(c -> !sessionInfo.getIdentity().getIdentifier().equals(c.getUsername()))
                .collect(Collectors.toList());

        if (spaceContributors.size() == contributors.size()) {
            spaceContributors.add(new Contributor(sessionInfo.getIdentity().getIdentifier(),
                                                  ContributorType.CONTRIBUTOR));
            ouService.updateOrganizationalUnit(ou.getName(),
                                               ou.getDefaultGroupId(),
                                               spaceContributors);
        }
        contributors.add(new Contributor(sessionInfo.getIdentity().getIdentifier(),
                                         ContributorType.OWNER));

        return contributors;
    }

    @Override
    public Boolean thereIsAProjectInTheWorkbench() {
        return !projectService.getAllWorkspaceProjects().isEmpty();
    }

    @Override
    public AssetQueryResult getProjectAssets(final ProjectAssetsQuery query) {
        checkNotNull("query",
                     query);

        final boolean projectStillExists = ioService.exists(Paths.convert(query.getProject().getBranch().getPath()));
        if (!projectStillExists) {
            log.info("Asset lookup result: project [{}] does not exist.",
                     projectIdentifierFrom(query));
            return AssetQueryResult.nonexistent();
        } else if (!indexOracle.isIndexed(query.getProject())) {
            log.info("Asset lookup result: project [{}] is not indexed.",
                     projectIdentifierFrom(query));
            return AssetQueryResult.unindexed();
        }

        final HashSet<ValueIndexTerm> queryTerms = buildProjectAssetsQuery(query);

        final PageResponse<RefactoringPageRow> findRulesByProjectQuery = refactoringQueryService.query(new RefactoringPageRequest(FindAllLibraryAssetsQuery.NAME,
                                                                                                                                  queryTerms,
                                                                                                                                  query.getStartIndex(),
                                                                                                                                  query.getAmount(),
                                                                                                                                  Boolean.TRUE));
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

        log.info("Asset lookup result: project [{}] is indexed with {} index hits.",
                 projectIdentifierFrom(query),
                 assets.size());
        return AssetQueryResult.normal(assets.stream()
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
                                               .collect(Collectors.toList()));
    }

    private static String projectIdentifierFrom(final ProjectAssetsQuery query) {
        return Optional.ofNullable(query.getProject().getRepository())
                .map(repo -> repo.getIdentifier())
                .orElseGet(() -> query.getProject().getName());
    }

    private HashSet<ValueIndexTerm> buildProjectAssetsQuery(ProjectAssetsQuery query) {
        final HashSet<ValueIndexTerm> queryTerms = new HashSet<>();

        queryTerms.add(new LibraryValueRepositoryRootIndexTerm(query.getProject().getRootPath().toURI()));

        if (query.hasFilter()) {
            queryTerms.add(new LibraryValueFileNameIndexTerm("*" + query.getFilter() + "*",
                                                             ValueIndexTerm.TermSearchType.WILDCARD));
        }

        if (query.hasExtension()) {
            queryTerms.add(new LibraryValueFileExtensionIndexTerm(query.getExtensions()));
        }
        return queryTerms;
    }

    @Override
    public Boolean hasProjects(final OrganizationalUnit organizationalUnit) {
        return !projectService.getAllWorkspaceProjects(organizationalUnit).isEmpty();
    }

    @Override
    public Boolean hasAssets(final WorkspaceProject project) {
        checkNotNull("LibraryServiceImpl.project",
                     project);

        final boolean projectStillExists = ioService.exists(Paths.convert(project.getRootPath()));
        if (!projectStillExists) {
            return false;
        }

        final Package defaultPackage = moduleService.resolveDefaultPackage(project.getMainModule());
        return explorerServiceHelper.hasAssets(defaultPackage);
    }

    @Override
    public Set<ImportProject> getExampleProjects() {
        final String importProjectsUrl = getCustomImportProjectsUrl();
        final ExampleRepository repository = importProjectsUrl == null || importProjectsUrl.isEmpty()
                ? examplesService.getPlaygroundRepository()
                : new ExampleRepository(importProjectsUrl);

        final Set<ImportProject> projects = examplesService.getProjects(repository);

        return projects;
    }

    @Override
    public List<OrganizationalUnit> getOrganizationalUnits() {
        return new ArrayList<>(ouService.getOrganizationalUnits());
    }

    @Override
    public GAV createGAV(final String projectName,
                         final OrganizationalUnit selectedOrganizationalUnit) {
        final LibraryPreferences preferences = getPreferences();
        final String artifactId = NewWorkspaceProjectUtils.sanitizeProjectName(projectName);
        return new GAV(selectedOrganizationalUnit.getDefaultGroupId(),
                       artifactId,
                       preferences.getProjectPreferences().getVersion());
    }

    @Override
    public synchronized List<String> getAllUsers() {
        try {
            final List<User> users = userManagerService.search(new SearchRequestImpl("",
                                                                                     1,
                                                                                     Integer.MAX_VALUE)).getResults();
            return users.stream().map(User::getIdentifier).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error while searching all users: " + e.getClass().getCanonicalName() );
            return Collections.emptyList();
        }
    }

    @Override
    public void addBranch(final String newBranchName,
                          final String baseBranchName,
                          final WorkspaceProject project) {
        Branch baseBranch = project.getRepository().getBranch(baseBranchName)
                .orElseThrow(() -> new IllegalStateException("The base branch does not exists"));

        final org.uberfire.java.nio.file.Path baseBranchPath = pathUtil.convert(baseBranch.getPath());
        final String newBranchPathURI = pathUtil.replaceBranch(newBranchName,
                                                               baseBranch.getPath().toURI());
        try {
            final org.uberfire.java.nio.file.Path newBranchPath = ioService.get(new URI(newBranchPathURI));
            baseBranchPath.getFileSystem().provider().copy(baseBranchPath,
                                                           newBranchPath);
            copyBranchPermissions(newBranchName,
                                  baseBranchName,
                                  project);

            repositoryUpdatedEvent.fire(new RepositoryUpdatedEvent(repoService.getRepositoryFromSpace(project.getSpace(),
                                                                                                      project.getRepository().getAlias())));

            fireNewBranchEvent(pathUtil.convert(newBranchPath),
                               newBranchPath,
                               baseBranchPath);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void copyBranchPermissions(final String targetBranchName,
                                       final String sourceBranchName,
                                       final WorkspaceProject project) {
        final BranchPermissions branchPermissions = loadBranchPermissions(project.getSpace().getName(),
                                                                          project.getRepository().getIdentifier(),
                                                                          sourceBranchName);
        saveBranchPermissions(project.getSpace().getName(),
                              project.getRepository().getIdentifier(),
                              targetBranchName,
                              branchPermissions);
    }

    @Override
    public void removeBranch(final WorkspaceProject project,
                             final Branch branch) {
        try {
            ioService.startBatch(pathUtil.convert(branch.getPath()).getFileSystem());

            repoService.getRepositoryFromSpace(project.getSpace(), project.getRepository().getAlias()).getBranch(branch.getName()).ifPresent(updatedBranch -> {
                final org.uberfire.java.nio.file.Path branchPath = pathUtil.convert(branch.getPath());
                ioService.delete(branchPath);
                deleteBranchPermissions(project.getSpace().getName(),
                                        project.getRepository().getIdentifier(),
                                        branch.getName());
                this.repositoryUpdatedEvent.fire(new RepositoryUpdatedEvent(repoService.getRepositoryFromSpace(project.getSpace(),
                                                                                                               project.getRepository().getAlias())));
            });
        } finally {
            ioService.endBatch();
        }
    }

    @Override
    public BranchPermissions loadBranchPermissions(final String spaceName,
                                                   final String projectIdentifier,
                                                   final String branchName) {
        return spaceConfigStorageRegistry.get(spaceName).loadBranchPermissions(branchName,
                                                                               projectIdentifier);
    }

    @Override
    public void saveBranchPermissions(final String spaceName,
                                      final String projectIdentifier,
                                      final String branchName,
                                      final BranchPermissions branchPermissions) {
        spaceConfigStorageRegistry.get(spaceName).saveBranchPermissions(branchName,
                                                                        projectIdentifier,
                                                                        branchPermissions);
    }

    @Override
    public Boolean isClustered() {
        return clusterService.isAppFormerClustered();
    }

    private void deleteBranchPermissions(final String spaceName,
                                         final String projectIdentifier,
                                         final String branchName) {
        spaceConfigStorageRegistry.get(spaceName).deleteBranchPermissions(branchName,
                                                                          projectIdentifier);
    }

    private void fireNewBranchEvent(final Path targetRoot,
                                    final org.uberfire.java.nio.file.Path nioTargetRepositoryRoot,
                                    final org.uberfire.java.nio.file.Path nioSourceRepositoryRoot) {
        final Repository repository = repoService.getRepository(targetRoot);

        final Optional<Branch> toBranch = repository.getBranch(Paths.convert(nioTargetRepositoryRoot.getRoot()));

        final Optional<Branch> fromBranch = repository.getBranch(Paths.convert(nioSourceRepositoryRoot.getRoot()));

        if (toBranch.isPresent()) {
            newBranchEvent.fire(new NewBranchEvent(repository,
                                                   toBranch.get().getName(),
                                                   fromBranch.get().getName(),
                                                   sessionInfo.getIdentity()));
        } else {
            throw new IllegalStateException("Could not find a branch that was just created. The Path used was " + nioTargetRepositoryRoot.getRoot());
        }
    }

    String getCustomImportProjectsUrl() {
        return System.getProperty("org.kie.project.examples.repository.url");
    }

    LibraryPreferences getPreferences() {
        preferences.load();
        return preferences;
    }

    POM createPOM(final String projectName,
                  final String projectDescription,
                  final GAV gav) {
        return new POM(projectName,
                       projectDescription,
                       "",
                       gav);
    }

    private Optional<Object> getAttribute(final String attribute,
                                          final Map<String, Object> attributes) {
        return attributes.entrySet().stream()
                .filter(entry -> attribute.equals(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    @Override
    public int getNumberOfAssets(final ProjectAssetsQuery query) {
        HashSet<ValueIndexTerm> queryTerms = this.buildProjectAssetsQuery(query);
        return refactoringQueryService.queryHitCount(new RefactoringPageRequest(FindAllLibraryAssetsQuery.NAME,
                                                                                queryTerms,
                                                                                0,
                                                                                null));
    }

    @Override
    public int getNumberOfAssets(final WorkspaceProject module) {
        final HashSet<ValueIndexTerm> queryTerms = new HashSet<>();
        queryTerms.add(new LibraryValueRepositoryRootIndexTerm(module.getRootPath().toURI()));

        return refactoringQueryService.queryHitCount(new RefactoringPageRequest(FindAllLibraryAssetsQuery.NAME,
                                                                                queryTerms,
                                                                                0,
                                                                                null,
                                                                                Boolean.TRUE));
    }

    @Override
    public OrganizationalUnit getDefaultOrganizationalUnit() {
        final String defaultOUIdentifier = getPreferences().getOrganizationalUnitPreferences().getName();

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

    private OrganizationalUnit createDefaultOrganizationalUnit() {
        if (!authorizationManager.authorize(OrganizationalUnit.RESOURCE_TYPE,
                                            OrganizationalUnitAction.CREATE,
                                            sessionInfo.getIdentity())) {
            throw new UnauthorizedException("User :user has no permissions to :type -> :action"
                                                    .replace(":user",
                                                             sessionInfo.getIdentity().getIdentifier())
                                                    .replace(":type",
                                                             OrganizationalUnit.RESOURCE_TYPE.getName())
                                                    .replace(":action",
                                                             OrganizationalUnitAction.CREATE.getName()));
        }

        final LibraryPreferences preferences = getPreferences();

        final List<Contributor> contributors = new ArrayList<>();
        contributors.add(new Contributor(preferences.getOrganizationalUnitPreferences().getOwner(),
                                         ContributorType.OWNER));

        return ouService.createOrganizationalUnit(preferences.getOrganizationalUnitPreferences().getName(),
                                                  preferences.getOrganizationalUnitPreferences().getGroupId(),
                                                  Collections.emptyList(),
                                                  contributors);
    }

    private Optional<OrganizationalUnit> getOrganizationalUnit(final String identifier,
                                                               final Collection<OrganizationalUnit> organizationalUnits) {
        return organizationalUnits.stream()
                .filter(p -> p.getIdentifier().equalsIgnoreCase(identifier))
                .findFirst();
    }
}

