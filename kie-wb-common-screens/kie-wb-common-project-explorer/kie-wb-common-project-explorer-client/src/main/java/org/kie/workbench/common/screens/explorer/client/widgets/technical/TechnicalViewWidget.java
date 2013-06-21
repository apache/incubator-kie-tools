/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets.technical;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import javax.enterprise.context.ApplicationScoped;

import com.github.gwtbootstrap.client.ui.Breadcrumbs;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavHeader;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavList;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.utils.Sorters;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.services.shared.context.Project;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.client.common.BusyPopup;

/**
 * Technical View implementation
 */
@ApplicationScoped
public class TechnicalViewWidget extends Composite implements TechnicalView {

    interface TechnicalViewImplBinder
            extends
            UiBinder<Widget, TechnicalViewWidget> {

    }

    private static TechnicalViewImplBinder uiBinder = GWT.create( TechnicalViewImplBinder.class );

    @UiField
    NavList items;

    @UiField
    Breadcrumbs breadcrumbs;

    //TreeSet sorts members upon insertion
    private final Set<Group> sortedGroups = new TreeSet<Group>( Sorters.GROUP_SORTER );
    private final Set<Repository> sortedRepositories = new TreeSet<Repository>( Sorters.REPOSITORY_SORTER );
    private final Set<Project> sortedProjects = new TreeSet<Project>( Sorters.PROJECT_SORTER );
    private final Set<FolderItem> sortedFolderListing = new TreeSet<FolderItem>( Sorters.FOLDER_LISTING_SORTER );

    private enum Context {
        GROUPS,
        REPOSITORIES,
        PROJECTS,
        PATHS
    }

    private Context context = Context.GROUPS;

    private TechnicalViewPresenter presenter;

    private Group activeGroup;
    private Repository activeRepository;
    private Project activeProject;
    private FolderListing activeFolderListing;

    public TechnicalViewWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final TechnicalViewPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setGroups( final Collection<Group> groups ) {
        sortedGroups.clear();
        sortedGroups.addAll( groups );
        this.activeGroup = null;
        this.activeRepository = null;
        this.activeProject = null;
        this.activeFolderListing = null;
        context = Context.GROUPS;
        hideBusyIndicator();
        refresh();
    }

    @Override
    public void setRepositories( final Collection<Repository> repositories,
                                 final Group activeGroup ) {
        sortedRepositories.clear();
        sortedRepositories.addAll( repositories );
        this.activeGroup = activeGroup;
        this.activeRepository = null;
        this.activeProject = null;
        this.activeFolderListing = null;
        context = Context.REPOSITORIES;
        hideBusyIndicator();
        refresh();
    }

    @Override
    public void setProjects( final Collection<Project> projects,
                             final Repository activeRepository,
                             final Group activeGroup ) {
        sortedProjects.clear();
        sortedProjects.addAll( projects );
        this.activeGroup = activeGroup;
        this.activeRepository = activeRepository;
        this.activeProject = null;
        this.activeFolderListing = null;
        context = Context.PROJECTS;
        hideBusyIndicator();
        refresh();
    }

    @Override
    public void setFilesAndFolders( final FolderListing activeFolderListing,
                                    final Project activeProject,
                                    final Repository activeRepository,
                                    final Group activeGroup ) {
        sortedFolderListing.clear();
        sortedFolderListing.addAll( activeFolderListing.getFolderItems() );
        this.activeGroup = activeGroup;
        this.activeRepository = activeRepository;
        this.activeProject = activeProject;
        this.activeFolderListing = activeFolderListing;
        context = Context.PATHS;
        hideBusyIndicator();
        refresh();
    }

    private void refresh() {
        switch ( context ) {
            case GROUPS:
                populateGroupView();
                break;
            case REPOSITORIES:
                populateRepositoryView();
                break;
            case PROJECTS:
                populateProjectView();
                break;
            case PATHS:
                populatePathView();
                break;
        }
        makeBreadCrumbs();
    }

    private void populateGroupView() {
        items.clear();
        items.add( new NavHeader( ProjectExplorerConstants.INSTANCE.groups() ) );
        if ( !sortedGroups.isEmpty() ) {
            for ( Group group : sortedGroups ) {
                items.add( makeGroupNavLink( group ) );
            }
        } else {
            items.add( new Label( ProjectExplorerConstants.INSTANCE.noItemsExist() ) );
        }
    }

    private IsWidget makeGroupNavLink( final Group group ) {
        final NavLink navLink = new NavLink( group.getName() );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.groupSelected( group );
            }
        } );
        return navLink;
    }

    private void populateRepositoryView() {
        items.clear();
        items.add( new NavHeader( ProjectExplorerConstants.INSTANCE.repositories() ) );
        items.add( makeParentGroupNavLink() );
        if ( !sortedRepositories.isEmpty() ) {
            for ( Repository repository : sortedRepositories ) {
                items.add( makeRepositoryNavLink( repository ) );
            }
        } else {
            items.add( new Label( ProjectExplorerConstants.INSTANCE.noItemsExist() ) );
        }
    }

    private IsWidget makeParentGroupNavLink() {
        final NavLink navLink = new NavLink( ".." );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.groupSelected( null );
            }
        } );
        return navLink;
    }

    private IsWidget makeRepositoryNavLink( final Repository repository ) {
        final NavLink navLink = new NavLink( repository.getAlias() );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.repositorySelected( repository );
            }
        } );
        return navLink;
    }

    private void populateProjectView() {
        items.clear();
        items.add( new NavHeader( ProjectExplorerConstants.INSTANCE.projects() ) );
        items.add( makeParentRepositoryNavLink() );
        if ( !sortedProjects.isEmpty() ) {
            for ( Project project : sortedProjects ) {
                items.add( makeProjectNavLink( project ) );
            }
        } else {
            items.add( new Label( ProjectExplorerConstants.INSTANCE.noItemsExist() ) );
        }
    }

    private IsWidget makeParentRepositoryNavLink() {
        final NavLink navLink = new NavLink( ".." );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.repositorySelected( null );
            }
        } );
        return navLink;
    }

    private IsWidget makeProjectNavLink( final Project project ) {
        final NavLink navLink = new NavLink( project.getTitle() );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.projectSelected( project );
            }
        } );
        return navLink;
    }

    private void populatePathView() {
        items.clear();
        items.add( new NavHeader( ProjectExplorerConstants.INSTANCE.files() ) );
        items.add( makeParentFolderNavLink( activeFolderListing ) );
        if ( !sortedFolderListing.isEmpty() ) {
            for ( FolderItem folderItem : sortedFolderListing ) {
                switch ( folderItem.getType() ) {
                    case FOLDER:
                        items.add( makeFolderNavLink( folderItem ) );
                        break;
                    case FILE:
                        items.add( makeFileNavLink( folderItem ) );
                        break;
                }
            }
        } else {
            items.add( new Label( ProjectExplorerConstants.INSTANCE.noItemsExist() ) );
        }
    }

    private IsWidget makeParentFolderNavLink( final FolderListing folder ) {
        final NavLink navLink = new NavLink( ".." );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.parentFolderSelected( folder );
            }
        } );
        return navLink;
    }

    private IsWidget makeFolderNavLink( final FolderItem folderItem ) {
        final NavLink navLink = new NavLink( folderItem.getFileName() );
        navLink.setIcon( IconType.FOLDER_CLOSE );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.folderSelected( folderItem.getPath() );
            }
        } );
        return navLink;
    }

    private IsWidget makeFileNavLink( final FolderItem folderItem ) {
        final NavLink navLink = new NavLink( folderItem.getFileName() );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.fileSelected( folderItem.getPath() );
            }
        } );
        return navLink;
    }

    private void makeBreadCrumbs() {
        breadcrumbs.clear();
        if ( activeGroup != null ) {
            breadcrumbs.add( makeGroupBreadCrumb( activeGroup ) );
        }
        if ( activeRepository != null ) {
            breadcrumbs.add( makeRepositoryBreadCrumb( activeRepository ) );
        }
        if ( activeProject != null ) {
            breadcrumbs.add( makeProjectBreadCrumb( activeProject ) );
        }
        if ( activeFolderListing != null ) {
            breadcrumbs.add( makeFolderListingBreadCrumb( activeFolderListing ) );
        }
    }

    private IsWidget makeGroupBreadCrumb( final Group activeGroup ) {
        return new NavLink( activeGroup.getName() );
    }

    private IsWidget makeRepositoryBreadCrumb( final Repository activeRepository ) {
        return new NavLink( activeRepository.getAlias() );
    }

    private IsWidget makeProjectBreadCrumb( final Project activeProject ) {
        return new NavLink( activeProject.getTitle() );
    }

    private IsWidget makeFolderListingBreadCrumb( final FolderListing actFolderListing ) {
        return new NavLink( actFolderListing.getPath().toURI() );
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

}
