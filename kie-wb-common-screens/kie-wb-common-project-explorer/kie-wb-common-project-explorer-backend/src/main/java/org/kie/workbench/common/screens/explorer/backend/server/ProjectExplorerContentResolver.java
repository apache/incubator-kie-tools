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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.guvnor.common.services.backend.file.LinkedDotFileFilter;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ProjectExplorerContent;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.screens.explorer.service.ProjectExplorerContentQuery;
import org.kie.workbench.common.screens.explorer.utils.Sorters;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.file.Files;
import org.uberfire.security.authz.AuthorizationManager;

import static java.util.Collections.*;
import static org.kie.workbench.common.screens.explorer.backend.server.ExplorerServiceHelper.*;

public class ProjectExplorerContentResolver {

    private LinkedDotFileFilter dotFileFilter = new LinkedDotFileFilter();

    private KieProjectService projectService;

    private ExplorerServiceHelper helper;

    private AuthorizationManager authorizationManager;

    private OrganizationalUnitService organizationalUnitService;

    @Inject
    @SessionScoped
    protected User identity;

    private OrganizationalUnit selectedOrganizationalUnit;
    private Repository selectedRepository;
    private Project selectedProject;
    private Package selectedPackage;
    private FolderItem selectedItem;
    private FolderListing folderListing;
    private Map<FolderItem, List<FolderItem>> siblings;

    private Set<OrganizationalUnit> organizationalUnits;
    private Map<String, Repository> repositories;
    private Map<String, Project> projects;

    public ProjectExplorerContentResolver() {

    }

    @Inject
    public ProjectExplorerContentResolver(
            KieProjectService projectService,
            ExplorerServiceHelper helper,
            AuthorizationManager authorizationManager,
            OrganizationalUnitService organizationalUnitService ) {
        this.projectService = projectService;
        this.helper = helper;
        this.authorizationManager = authorizationManager;
        this.organizationalUnitService = organizationalUnitService;
    }

    public ProjectExplorerContent resolve( final ProjectExplorerContentQuery query ) {

        setupSelectedItems( query );

        setSelectedOrganizationalUnit();
        setSelectedRepository();
        setSelectedProject();

        if ( selectedOrganizationalUnit == null || selectedRepository == null || selectedProject == null ) {
            return emptyProjectExplorerContent();
        } else {
            return projectExplorerContentWithSelections( query.getOptions() );
        }
    }

    private ProjectExplorerContent projectExplorerContentWithSelections( final Set<Option> options ) {

        setFolderListing( options );

        setSiblings();

        helper.store( selectedOrganizationalUnit, selectedRepository, selectedProject, folderListing, selectedPackage, options );

        return new ProjectExplorerContent(
                new TreeSet<OrganizationalUnit>( Sorters.ORGANIZATIONAL_UNIT_SORTER ) {{
                    addAll( organizationalUnits );
                }},
                selectedOrganizationalUnit,
                new TreeSet<Repository>( Sorters.REPOSITORY_SORTER ) {{
                    addAll( repositories.values() );
                }},
                selectedRepository,
                new TreeSet<Project>( Sorters.PROJECT_SORTER ) {{
                    addAll( projects.values() );
                }},
                selectedProject,
                folderListing,
                siblings
        );
    }

    private void setFolderListing( Set<Option> options ) {
        folderListing = helper.getFolderListing( selectedItem, selectedProject, selectedPackage, options );
    }

    private void setSiblings() {
        if ( folderListing.getSegments().size() > 1 ) {
            final ListIterator<FolderItem> li = folderListing.getSegments().listIterator( folderListing.getSegments().size() );
            while ( li.hasPrevious() ) {
                final FolderItem currentItem = li.previous();
                final List<FolderItem> result = new ArrayList<FolderItem>();
                result.add( currentItem );

                if ( currentItem.getItem() instanceof Package ) {
                    result.addAll( getSegmentSiblings( (Package) currentItem.getItem() ) );
                } else if ( currentItem.getItem() instanceof Path ) {
                    result.addAll( getSegmentSiblings( (Path) currentItem.getItem() ) );
                }
                siblings.put( currentItem,
                              result );
            }
        }
        if ( selectedItem != null && selectedItem.getType().equals( FolderItemType.FOLDER ) &&
                !siblings.containsKey( selectedItem ) ) {
            final List<FolderItem> result = new ArrayList<FolderItem>();
            result.add( selectedItem );

            if ( selectedItem.getItem() instanceof Package ) {
                result.addAll( getSegmentSiblings( (Package) selectedItem.getItem() ) );
            } else if ( selectedItem.getItem() instanceof Path ) {
                result.addAll( getSegmentSiblings( (Path) selectedItem.getItem() ) );
            }
            siblings.put( selectedItem,
                          result );
        }

        if ( folderListing.getItem().getType().equals( FolderItemType.FOLDER ) &&
                !siblings.containsKey( folderListing.getItem() ) ) {
            final List<FolderItem> result = new ArrayList<FolderItem>();
            result.add( folderListing.getItem() );

            if ( folderListing.getItem().getItem() instanceof Package ) {
                result.addAll( getSegmentSiblings( (Package) folderListing.getItem().getItem() ) );
            } else if ( folderListing.getItem().getItem() instanceof Path ) {
                result.addAll( getSegmentSiblings( (Path) folderListing.getItem().getItem() ) );
            }
            if ( !result.isEmpty() ) {
                siblings.put( folderListing.getItem(),
                              result );
            }
        }

        //Sort sibling lists before returning to client
        for ( Map.Entry<FolderItem, List<FolderItem>> e : siblings.entrySet() ) {
            Collections.sort( e.getValue(),
                              Sorters.ITEM_SORTER );
        }
    }

    private ProjectExplorerContent emptyProjectExplorerContent() {
        return new ProjectExplorerContent(
                new TreeSet<OrganizationalUnit>( Sorters.ORGANIZATIONAL_UNIT_SORTER ) {{
                    addAll( organizationalUnits );
                }},
                selectedOrganizationalUnit,
                new TreeSet<Repository>( Sorters.REPOSITORY_SORTER ) {{
                    addAll( repositories.values() );
                }},
                selectedRepository,
                new TreeSet<Project>( Sorters.PROJECT_SORTER ) {{
                    addAll( projects.values() );
                }},
                selectedProject,
                new FolderListing( null, Collections.<FolderItem>emptyList(), Collections.<FolderItem>emptyList() ),
                Collections.<FolderItem, List<FolderItem>>emptyMap()
        );
    }

    private void setSelectedProject() {
        projects = getProjects( selectedRepository );

        if ( selectedProject == null || !projects.containsKey( selectedProject.getProjectName() ) ) {
            selectedProject = ( projects.isEmpty() ? null : projects.values().iterator().next() );
        } else {
            selectedProject = projects.get( selectedProject.getProjectName() );
        }
    }

    private void setSelectedRepository() {
        repositories = getRepositories( selectedOrganizationalUnit );
        if ( selectedRepository == null || !repositories.containsKey( selectedRepository.getAlias() ) ) {
            selectedRepository = ( repositories.isEmpty() ? null : repositories.values().iterator().next() );

        } else if ( isCurrentRepositoryUpToDate() ) {

            String branch = selectedRepository.getCurrentBranch();
            selectedRepository = repositories.get( selectedRepository.getAlias() );
            if ( selectedRepository instanceof GitRepository ) {
                ( (GitRepository) selectedRepository ).changeBranch( branch );
            }
        }
    }

    private boolean isCurrentRepositoryUpToDate() {
        return !selectedRepository.equals( repositories.get( selectedRepository.getAlias() ) );
    }

    private void setSelectedOrganizationalUnit() {
        organizationalUnits = getOrganizationalUnits();
        if ( !organizationalUnits.contains( selectedOrganizationalUnit ) ) {
            selectedOrganizationalUnit = ( organizationalUnits.isEmpty() ? null : organizationalUnits.iterator().next() );
        }
    }

    private void setupSelectedItems( ProjectExplorerContentQuery query ) {

        clear( query );

        final UserExplorerLastData lastContent = helper.getLastContent();
        final UserExplorerData userContent = helper.loadUserContent();

        if ( !lastContent.isDataEmpty() ) {
            if ( query.getOrganizationalUnit() == null && query.getRepository() == null && query.getProject() == null ) {
                if ( query.getOptions().contains( Option.BUSINESS_CONTENT ) && lastContent.getLastPackage() != null ) {
                    selectedOrganizationalUnit = lastContent.getLastPackage().getOrganizationalUnit();
                    selectedRepository = lastContent.getLastPackage().getRepository();
                    selectedProject = lastContent.getLastPackage().getProject();
                    selectedPackage = lastContent.getLastPackage().getPkg();
                    selectedItem = null;
                } else if ( query.getOptions().contains( Option.TECHNICAL_CONTENT ) && lastContent.getLastFolderItem() != null ) {
                    selectedOrganizationalUnit = lastContent.getLastFolderItem().getOrganizationalUnit();
                    selectedRepository = lastContent.getLastFolderItem().getRepository();
                    selectedProject = lastContent.getLastFolderItem().getProject();
                    selectedItem = lastContent.getLastFolderItem().getItem();
                    selectedPackage = null;
                }
            } else if ( query.getOptions().contains( Option.BUSINESS_CONTENT ) && lastContent.getLastPackage() != null ) {
                if ( !query.getOrganizationalUnit().equals( lastContent.getLastPackage().getOrganizationalUnit() ) ||
                        query.getRepository() != null && !query.getRepository().equals( lastContent.getLastPackage().getRepository() ) ||
                        query.getProject() != null && !query.getProject().equals( lastContent.getLastPackage().getProject() ) ) {
                    selectedOrganizationalUnit = loadOrganizationalUnit( query.getOrganizationalUnit(), userContent );
                    selectedRepository = loadRepository( selectedOrganizationalUnit, query.getRepository(), userContent );
                    selectedProject = loadProject( selectedOrganizationalUnit, selectedRepository, query.getProject(), userContent );
                    selectedPackage = loadPackage( selectedOrganizationalUnit, selectedRepository, selectedProject, query.getPkg(), userContent );
                    selectedItem = null;
                }
            } else if ( query.getOptions().contains( Option.TECHNICAL_CONTENT ) && lastContent.getLastFolderItem() != null ) {
                if ( !query.getOrganizationalUnit().equals( lastContent.getLastFolderItem().getOrganizationalUnit() ) ||
                        query.getRepository() != null && !query.getRepository().equals( lastContent.getLastFolderItem().getRepository() ) ||
                        query.getProject() != null && !query.getProject().equals( lastContent.getLastFolderItem().getProject() ) ) {
                    selectedOrganizationalUnit = loadOrganizationalUnit( query.getOrganizationalUnit(), userContent );
                    selectedRepository = loadRepository( selectedOrganizationalUnit, query.getRepository(), userContent );
                    selectedProject = loadProject( selectedOrganizationalUnit, selectedRepository, query.getProject(), userContent );
                    selectedItem = loadFolderItem( selectedOrganizationalUnit, selectedRepository, selectedProject, query.getItem(), userContent );
                    selectedPackage = null;
                }
            }
        }
    }

    private void clear( ProjectExplorerContentQuery query ) {
        selectedOrganizationalUnit = query.getOrganizationalUnit();
        selectedRepository = query.getRepository();
        selectedProject = query.getProject();
        selectedPackage = query.getPkg();
        selectedItem = query.getItem();
        folderListing = null;
        siblings = new HashMap<FolderItem, List<FolderItem>>();
    }

    private List<FolderItem> getSegmentSiblings( final Path path ) {
        final List<FolderItem> result = new ArrayList<FolderItem>();
        org.uberfire.java.nio.file.Path nioParentPath = Paths.convert( path ).getParent();

        for ( org.uberfire.java.nio.file.Path sibling : Files.newDirectoryStream( nioParentPath, dotFileFilter ) ) {
            result.add( toFolderItem( sibling ) );
        }

        return result;
    }

    private List<FolderItem> getSegmentSiblings( final Package pkg ) {
        final List<FolderItem> result = new ArrayList<FolderItem>();
        final Package parentPkg = projectService.resolveParentPackage( pkg );
        if ( parentPkg == null ) {
            return emptyList();
        }
        final Set<Package> siblings = projectService.resolvePackages( parentPkg );
        if ( siblings != null && !siblings.isEmpty() ) {
            for ( final Package sibling : siblings ) {
                if ( !sibling.equals( pkg ) ) {
                    result.add( toFolderItem( sibling ) );
                }
            }
        }

        return result;
    }

    private OrganizationalUnit loadOrganizationalUnit( final OrganizationalUnit organizationalUnit,
                                                       final UserExplorerData content ) {
        if ( organizationalUnit != null ) {
            return organizationalUnit;
        }

        return content.getOrganizationalUnit();
    }

    private Repository loadRepository( final OrganizationalUnit organizationalUnit,
                                       final Repository repository,
                                       final UserExplorerData content ) {
        if ( organizationalUnit == null ) {
            return null;
        }
        if ( repository != null ) {
            return repository;
        }

        return content.get( organizationalUnit );
    }

    private Project loadProject( final OrganizationalUnit organizationalUnit,
                                 final Repository repository,
                                 final Project project,
                                 final UserExplorerData content ) {
        if ( repository == null ) {
            return null;
        }
        if ( project != null ) {
            return project;
        }

        return content.get( organizationalUnit, repository );
    }

    private Package loadPackage( final OrganizationalUnit organizationalUnit,
                                 final Repository repository,
                                 final Project project,
                                 final Package pkg,
                                 final UserExplorerData content ) {
        if ( project == null ) {
            return null;
        }

        if ( pkg != null ) {
            return pkg;
        }

        return content.getPackage( organizationalUnit, repository, project );
    }

    private FolderItem loadFolderItem( final OrganizationalUnit organizationalUnit,
                                       final Repository repository,
                                       final Project project,
                                       final FolderItem item,
                                       final UserExplorerData content ) {
        if ( project == null ) {
            return null;
        }

        if ( item != null ) {
            return item;
        }

        return content.getFolderItem( organizationalUnit, repository, project );
    }

    private Set<OrganizationalUnit> getOrganizationalUnits() {
        final Collection<OrganizationalUnit> organizationalUnits = organizationalUnitService.getOrganizationalUnits();
        final Set<OrganizationalUnit> authorizedOrganizationalUnits = new HashSet<OrganizationalUnit>();
        for ( OrganizationalUnit organizationalUnit : organizationalUnits ) {
            if ( authorizationManager.authorize( organizationalUnit,
                                                 identity ) ) {
                authorizedOrganizationalUnits.add( organizationalUnit );
            }
        }
        return authorizedOrganizationalUnits;
    }

    private Map<String, Repository> getRepositories( final OrganizationalUnit organizationalUnit ) {
        final Map<String, Repository> authorizedRepositories = new HashMap<String, Repository>();
        if ( organizationalUnit == null ) {
            return authorizedRepositories;
        }
        //Reload OrganizationalUnit as the organizational unit's repository list might have been changed server-side
        final Collection<Repository> repositories = organizationalUnitService.getOrganizationalUnit( organizationalUnit.getName() ).getRepositories();
        for ( Repository repository : repositories ) {
            if ( authorizationManager.authorize( repository,
                                                 identity ) ) {
                authorizedRepositories.put( repository.getAlias(), repository );
            }
        }
        return authorizedRepositories;
    }

    private Map<String, Project> getProjects( final Repository repository ) {
        final Map<String, Project> authorizedProjects = new HashMap<String, Project>();

        if ( repository == null ) {
            return authorizedProjects;
        } else {
            Set<Project> allProjects = projectService.getProjects( repository, repository.getCurrentBranch() );

            for ( Project project : allProjects ) {
                if ( authorizationManager.authorize( project,
                                                     identity ) ) {
                    authorizedProjects.put( project.getProjectName(), project );
                }
            }

            return authorizedProjects;
        }
    }
}
