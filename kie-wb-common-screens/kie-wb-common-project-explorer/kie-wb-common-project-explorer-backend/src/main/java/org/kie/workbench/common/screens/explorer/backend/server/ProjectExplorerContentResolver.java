/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.screens.explorer.backend.server;

import org.guvnor.common.services.backend.file.LinkedDotFileFilter;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.screens.explorer.utils.Sorters;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.UserServicesImpl;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.async.DescriptiveRunnable;
import org.uberfire.commons.async.SimpleAsyncExecutorService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.security.Identity;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.server.cdi.AppResourcesAuthz;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

import static java.util.Collections.emptyList;
import static org.kie.workbench.common.screens.explorer.backend.server.ExplorerServiceHelper.toFolderItem;

public class ProjectExplorerContentResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectExplorerContentResolver.class);

    private LinkedDotFileFilter dotFileFilter = new LinkedDotFileFilter();

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private KieProjectService projectService;

    @Inject
    private UserServicesImpl userServices;

    @Inject
    private ExplorerServiceHelper helper;

    @Inject
    @AppResourcesAuthz
    private AuthorizationManager authorizationManager;

    @Inject
    private OrganizationalUnitService organizationalUnitService;

    @Inject
    @SessionScoped
    private Identity identity;

    private OrganizationalUnit selectedOrganizationalUnit;
    private Repository selectedRepository;
    private Project selectedProject;
    private Package selectedPackage;
    private FolderItem selectedItem;
    private FolderListing folderListing;
    private Map<FolderItem, List<FolderItem>> siblings;

    private Set<OrganizationalUnit> organizationalUnits;
    private Map<String, Repository> repositories;
    private Set<Project> projects;

    public ProjectExplorerContent resolve(final ProjectExplorerContentQuery query) {

        setupSelectedItems(query);

        setSelectedOrganizationalUnit();
        setSelectedRepository();
        setSelectedProject();

        if (selectedOrganizationalUnit == null || selectedRepository == null || selectedProject == null) {
            return emptyProjectExplorerContent();
        } else {
            return projectExplorerContentWithSelections(query.getOptions());
        }
    }

    private ProjectExplorerContent projectExplorerContentWithSelections(final Set<Option> options) {
        if (selectedItem == null) {
            final List<FolderItem> segments;
            if (options.contains(Option.BUSINESS_CONTENT)) {
                final Package defaultPackage;
                if (selectedPackage == null) {
                    defaultPackage = projectService.resolveDefaultPackage(selectedProject);
                    segments = emptyList();
                } else {
                    defaultPackage = selectedPackage;
                    segments = helper.getPackageSegments(selectedPackage);
                }
                folderListing = new FolderListing(toFolderItem(defaultPackage),
                        helper.getItems(defaultPackage),
                        segments);
            } else {
                folderListing = helper.getFolderListing(selectedProject.getRootPath());
            }
        } else {
            folderListing = helper.getFolderListing(selectedItem);
        }

        if (selectedPackage != null && folderListing == null) {
            folderListing = new FolderListing(toFolderItem(selectedPackage),
                    helper.getItems(selectedPackage),
                    helper.getPackageSegments(selectedPackage));
        }

        if (folderListing.getSegments().size() > 1) {
            final ListIterator<FolderItem> li = folderListing.getSegments().listIterator(folderListing.getSegments().size());
            while (li.hasPrevious()) {
                final FolderItem currentItem = li.previous();
                final List<FolderItem> result = new ArrayList<FolderItem>();

                if (currentItem.getItem() instanceof Package) {
                    result.addAll(getSegmentSiblings((Package) currentItem.getItem()));
                } else if (currentItem.getItem() instanceof Path) {
                    result.addAll(getSegmentSiblings((Path) currentItem.getItem()));
                }
                siblings.put(currentItem, result);
            }
        }

        if (selectedItem != null && selectedItem.getType().equals(FolderItemType.FOLDER) &&
                !siblings.containsKey(selectedItem)) {
            final List<FolderItem> result = new ArrayList<FolderItem>();

            if (selectedItem.getItem() instanceof Package) {
                result.addAll(getSegmentSiblings((Package) selectedItem.getItem()));
            } else if (selectedItem.getItem() instanceof Path) {
                result.addAll(getSegmentSiblings((Path) selectedItem.getItem()));
            }
            siblings.put(selectedItem, result);
        }

        if (folderListing.getItem().getType().equals(FolderItemType.FOLDER) &&
                !siblings.containsKey(folderListing.getItem())) {
            final List<FolderItem> result = new ArrayList<FolderItem>();

            if (folderListing.getItem().getItem() instanceof Package) {
                result.addAll(getSegmentSiblings((Package) folderListing.getItem().getItem()));
            } else if (folderListing.getItem().getItem() instanceof Path) {
                result.addAll(getSegmentSiblings((Path) folderListing.getItem().getItem()));
            }
            if (!result.isEmpty()) {
                siblings.put(folderListing.getItem(), result);
            }
        }

        final org.uberfire.java.nio.file.Path userNavPath = userServices.buildPath("explorer", "user.nav");
        final org.uberfire.java.nio.file.Path lastUserNavPath = userServices.buildPath("explorer", "last.user.nav");

        final OrganizationalUnit _selectedOrganizationalUnit = selectedOrganizationalUnit;
        final Repository _selectedRepository = selectedRepository;
        final Project _selectedProject = selectedProject;
        final FolderItem _selectedItem = folderListing.getItem();
        final Package _selectedPackage;
        if (selectedPackage != null) {
            _selectedPackage = selectedPackage;
        } else if (folderListing.getItem().getItem() instanceof Package) {
            _selectedPackage = (Package) folderListing.getItem().getItem();
        } else {
            _selectedPackage = null;
        }

        SimpleAsyncExecutorService.getDefaultInstance().execute(new DescriptiveRunnable() {
            @Override
            public String getDescription() {
                return "Serialize Navigation State";
            }

            @Override
            public void run() {
                try {
                    helper.store(userNavPath, lastUserNavPath, _selectedOrganizationalUnit,
                            _selectedRepository, _selectedProject,
                            _selectedPackage, _selectedItem, options);
                } catch (final Exception e) {
                    LOGGER.error("Can't serialize user's state navigation", e);
                }
            }
        });

        return new ProjectExplorerContent(
                new TreeSet<OrganizationalUnit>(Sorters.ORGANIZATIONAL_UNIT_SORTER) {{
                    addAll(organizationalUnits);
                }},
                selectedOrganizationalUnit,
                new TreeSet<Repository>(Sorters.REPOSITORY_SORTER) {{
                    addAll(repositories.values());
                }},
                selectedRepository,
                new TreeSet<Project>(Sorters.PROJECT_SORTER) {{
                    addAll(projects);
                }},
                selectedProject,
                folderListing,
                siblings
        );
    }

    private ProjectExplorerContent emptyProjectExplorerContent() {
        return new ProjectExplorerContent(
                new TreeSet<OrganizationalUnit>(Sorters.ORGANIZATIONAL_UNIT_SORTER) {{
                    addAll(organizationalUnits);
                }},
                selectedOrganizationalUnit,
                new TreeSet<Repository>(Sorters.REPOSITORY_SORTER) {{
                    addAll(repositories.values());
                }},
                selectedRepository,
                new TreeSet<Project>(Sorters.PROJECT_SORTER) {{
                    addAll(projects);
                }},
                selectedProject,
                new FolderListing(null, Collections.<FolderItem>emptyList(), Collections.<FolderItem>emptyList()),
                Collections.<FolderItem, List<FolderItem>>emptyMap()
        );
    }

    private void setSelectedProject() {
        projects = getProjects(selectedRepository);
        if (!projects.contains(selectedProject)) {
            selectedProject = (projects.isEmpty() ? null : projects.iterator().next());
        }
    }

    private void setSelectedRepository() {
        repositories = getRepositories(selectedOrganizationalUnit);
        if (selectedRepository == null || !repositories.containsKey(selectedRepository.getAlias())) {
            selectedRepository = (repositories.isEmpty() ? null : repositories.values().iterator().next());
        }
    }

    private void setSelectedOrganizationalUnit() {
        organizationalUnits = getOrganizationalUnits();
        if (!organizationalUnits.contains(selectedOrganizationalUnit)) {
            selectedOrganizationalUnit = (organizationalUnits.isEmpty() ? null : organizationalUnits.iterator().next());
        }
    }

    private void setupSelectedItems(ProjectExplorerContentQuery query) {

        clear(query);

        final UserExplorerLastData lastContent = getUserExplorerLastData(query.isBranchChangeFlag());
        final UserExplorerData userContent = helper.loadUserContent();

        if (!lastContent.isDataEmpty()) {
            if (query.getOrganizationalUnit() == null && query.getRepository() == null && query.getProject() == null) {
                if (query.getOptions().contains(Option.BUSINESS_CONTENT) && lastContent.getLastPackage() != null) {
                    selectedOrganizationalUnit = lastContent.getLastPackage().getOrganizationalUnit();
                    selectedRepository = lastContent.getLastPackage().getRepository();
                    selectedProject = lastContent.getLastPackage().getProject();
                    selectedPackage = lastContent.getLastPackage().getPkg();
                    selectedItem = null;
                } else if (query.getOptions().contains(Option.TECHNICAL_CONTENT) && lastContent.getLastFolderItem() != null) {
                    selectedOrganizationalUnit = lastContent.getLastFolderItem().getOrganizationalUnit();
                    selectedRepository = lastContent.getLastFolderItem().getRepository();
                    selectedProject = lastContent.getLastFolderItem().getProject();
                    selectedItem = lastContent.getLastFolderItem().getItem();
                    selectedPackage = null;
                }
            } else if (query.getOptions().contains(Option.BUSINESS_CONTENT) && lastContent.getLastPackage() != null) {
                if (!query.getOrganizationalUnit().equals(lastContent.getLastPackage().getOrganizationalUnit()) ||
                        query.getRepository() != null && !query.getRepository().equals(lastContent.getLastPackage().getRepository()) ||
                        query.getProject() != null && !query.getProject().equals(lastContent.getLastPackage().getProject())) {
                    selectedOrganizationalUnit = loadOrganizationalUnit(query.getOrganizationalUnit(), userContent);
                    selectedRepository = loadRepository(selectedOrganizationalUnit, query.getRepository(), userContent);
                    selectedProject = loadProject(selectedOrganizationalUnit, selectedRepository, query.getProject(), userContent);
                    selectedPackage = loadPackage(selectedOrganizationalUnit, selectedRepository, selectedProject, query.getPkg(), userContent);
                    selectedItem = null;
                }
            } else if (query.getOptions().contains(Option.TECHNICAL_CONTENT) && lastContent.getLastFolderItem() != null) {
                if (!query.getOrganizationalUnit().equals(lastContent.getLastFolderItem().getOrganizationalUnit()) ||
                        query.getRepository() != null && !query.getRepository().equals(lastContent.getLastFolderItem().getRepository()) ||
                        query.getProject() != null && !query.getProject().equals(lastContent.getLastFolderItem().getProject())) {
                    selectedOrganizationalUnit = loadOrganizationalUnit(query.getOrganizationalUnit(), userContent);
                    selectedRepository = loadRepository(selectedOrganizationalUnit, query.getRepository(), userContent);
                    selectedProject = loadProject(selectedOrganizationalUnit, selectedRepository, query.getProject(), userContent);
                    selectedItem = loadFolderItem(selectedOrganizationalUnit, selectedRepository, selectedProject, query.getItem(), userContent);
                    selectedPackage = null;
                }
            }
        }
    }

    private UserExplorerLastData getUserExplorerLastData(boolean changeFlag) {
        if (changeFlag) {
            return helper.getLastContent();
        } else {
            return UserExplorerLastData.EMPTY;
        }
    }

    private void clear(ProjectExplorerContentQuery query) {
        if (query.isBranchChangeFlag()) {
            selectedOrganizationalUnit = null;
            selectedRepository = query.getRepository();
            selectedProject = null;
            selectedPackage = null;
            selectedItem = null;
        } else {
            selectedOrganizationalUnit = query.getOrganizationalUnit();
            selectedRepository = query.getRepository();
            selectedProject = query.getProject();
            selectedPackage = query.getPkg();
            selectedItem = query.getItem();
        }
        folderListing = null;
        siblings = new HashMap<FolderItem, List<FolderItem>>();
    }


    private List<FolderItem> getSegmentSiblings(final Path path) {
        final List<FolderItem> result = new ArrayList<FolderItem>();
        org.uberfire.java.nio.file.Path nioParentPath = Paths.convert(path).getParent();

        for (org.uberfire.java.nio.file.Path sibling : Files.newDirectoryStream(nioParentPath, dotFileFilter)) {
            result.add(toFolderItem(sibling));
        }

        return result;
    }

    private List<FolderItem> getSegmentSiblings(final Package pkg) {
        final List<FolderItem> result = new ArrayList<FolderItem>();
        final Package parentPkg = projectService.resolveParentPackage(pkg);
        if (parentPkg == null) {
            return emptyList();
        }
        final Set<Package> siblings = projectService.resolvePackages(parentPkg);
        if (siblings != null && !siblings.isEmpty()) {
            for (final Package sibling : siblings) {
                if (!sibling.equals(pkg)) {
                    result.add(toFolderItem(sibling));
                }
            }
        }

        return result;
    }

    private OrganizationalUnit loadOrganizationalUnit(final OrganizationalUnit organizationalUnit,
                                                      final UserExplorerData content) {
        if (organizationalUnit != null) {
            return organizationalUnit;
        }

        return content.getOrganizationalUnit();
    }

    private Repository loadRepository(final OrganizationalUnit organizationalUnit,
                                      final Repository repository,
                                      final UserExplorerData content) {
        if (organizationalUnit == null) {
            return null;
        }
        if (repository != null) {
            return repository;
        }

        return content.get(organizationalUnit);
    }

    private Project loadProject(final OrganizationalUnit organizationalUnit,
                                final Repository repository,
                                final Project project,
                                final UserExplorerData content) {
        if (repository == null) {
            return null;
        }
        if (project != null) {
            return project;
        }

        return content.get(organizationalUnit, repository);
    }

    private Package loadPackage(final OrganizationalUnit organizationalUnit,
                                final Repository repository,
                                final Project project,
                                final Package pkg,
                                final UserExplorerData content) {
        if (project == null) {
            return null;
        }

        if (pkg != null) {
            return pkg;
        }

        return content.getPackage(organizationalUnit, repository, project);
    }

    private FolderItem loadFolderItem(final OrganizationalUnit organizationalUnit,
                                      final Repository repository,
                                      final Project project,
                                      final FolderItem item,
                                      final UserExplorerData content) {
        if (project == null) {
            return null;
        }

        if (item != null) {
            return item;
        }

        return content.getFolderItem(organizationalUnit, repository, project);
    }

    private Set<OrganizationalUnit> getOrganizationalUnits() {
        final Collection<OrganizationalUnit> organizationalUnits = organizationalUnitService.getOrganizationalUnits();
        final Set<OrganizationalUnit> authorizedOrganizationalUnits = new HashSet<OrganizationalUnit>();
        for (OrganizationalUnit organizationalUnit : organizationalUnits) {
            if (authorizationManager.authorize(organizationalUnit,
                    identity)) {
                authorizedOrganizationalUnits.add(organizationalUnit);
            }
        }
        return authorizedOrganizationalUnits;
    }

    private Map<String, Repository> getRepositories(final OrganizationalUnit organizationalUnit) {
        final Map<String, Repository> authorizedRepositories = new HashMap<String, Repository>();
        if (organizationalUnit == null) {
            return authorizedRepositories;
        }
        //Reload OrganizationalUnit as the organizational unit's repository list might have been changed server-side
        final Collection<Repository> repositories = organizationalUnitService.getOrganizationalUnit(organizationalUnit.getName()).getRepositories();
        for (Repository repository : repositories) {
            if (authorizationManager.authorize(repository,
                    identity)) {
                authorizedRepositories.put(repository.getAlias(), repository);
            }
        }
        return authorizedRepositories;
    }

    private Set<Project> getProjects(final Repository repository) {
        final Set<Project> authorizedProjects = new HashSet<Project>();
        if (repository == null) {
            return authorizedProjects;
        }
        final Path repositoryRoot = repository.getRoot();
        final DirectoryStream<org.uberfire.java.nio.file.Path> nioRepositoryPaths = ioService.newDirectoryStream(Paths.convert(repositoryRoot));
        for (org.uberfire.java.nio.file.Path nioRepositoryPath : nioRepositoryPaths) {
            if (Files.isDirectory(nioRepositoryPath)) {
                final org.uberfire.backend.vfs.Path projectPath = Paths.convert(nioRepositoryPath);
                final Project project = projectService.resolveProject(projectPath);
                if (project != null) {
                    if (authorizationManager.authorize(project,
                            identity)) {
                        authorizedProjects.add(project);
                    }
                }
            }
        }
        return authorizedProjects;
    }
}
