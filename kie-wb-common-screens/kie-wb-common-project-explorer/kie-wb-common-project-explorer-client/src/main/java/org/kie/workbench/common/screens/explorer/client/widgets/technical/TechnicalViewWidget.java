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
import javax.annotation.PostConstruct;
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
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.utils.Sorters;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;
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

    //The view being displayed
    private enum Context {
        GROUPS,
        REPOSITORIES,
        PROJECTS,
        PATHS
    }

    private Context activeContext = Context.GROUPS;

    private TechnicalViewPresenter presenter;

    @PostConstruct
    public void init() {
        //Cannot create and bind UI until after injection points have been initialized
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
        activeContext = Context.GROUPS;
        refresh();
    }

    @Override
    public void setRepositories( final Collection<Repository> repositories ) {
        sortedRepositories.clear();
        sortedRepositories.addAll( repositories );
        activeContext = Context.REPOSITORIES;
        refresh();
    }

    @Override
    public void setProjects( final Collection<Project> projects ) {
        sortedProjects.clear();
        sortedProjects.addAll( projects );
        activeContext = Context.PROJECTS;
        refresh();
    }

    @Override
    public void setItems( final FolderListing activeFolderListing ) {
        sortedFolderListing.clear();
        sortedFolderListing.addAll( activeFolderListing.getFolderItems() );
        activeContext = Context.PATHS;
        refresh();
    }

    private void refresh() {
        switch ( activeContext ) {
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

    private void refresh( final Context updatedContext ) {
        switch ( updatedContext ) {
            case GROUPS:
                if ( activeContext.equals( updatedContext ) ) {
                    populateGroupView();
                }
                break;
            case REPOSITORIES:
                if ( activeContext.equals( updatedContext ) ) {
                    populateRepositoryView();
                }
                break;
            case PROJECTS:
                if ( activeContext.equals( updatedContext ) ) {
                    populateProjectView();
                }
                break;
            case PATHS:
                if ( activeContext.equals( updatedContext ) ) {
                    populatePathView();
                    makeBreadCrumbs();
                }
                break;
        }
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
                presenter.selectGroup( group );
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
                presenter.selectGroup( null );
            }
        } );
        return navLink;
    }

    private IsWidget makeRepositoryNavLink( final Repository repository ) {
        final NavLink navLink = new NavLink( repository.getAlias() );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.selectRepository( repository );
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
                presenter.selectRepository( null );
            }
        } );
        return navLink;
    }

    private IsWidget makeProjectNavLink( final Project project ) {
        final NavLink navLink = new NavLink( project.getProjectName() );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.selectProject( project );
            }
        } );
        return navLink;
    }

    private void populatePathView() {
        items.clear();
        items.add( new NavHeader( ProjectExplorerConstants.INSTANCE.files() ) );
        items.add( makeParentFolderNavLink( presenter.getActiveFolderListing() ) );
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
                presenter.selectParentFolder( folder );
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
                presenter.selectFolder( folderItem.getPath() );
            }
        } );
        return navLink;
    }

    private IsWidget makeFileNavLink( final FolderItem folderItem ) {
        final NavLink navLink = new NavLink( folderItem.getFileName() );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.selectFile( folderItem.getPath() );
            }
        } );
        return navLink;
    }

    private void makeBreadCrumbs() {
        breadcrumbs.clear();
        makeGroupBreadCrumb();
        makeRepositoryBreadCrumb();
        makeProjectBreadCrumb();
        makeFolderListingBreadCrumb();
    }

    private void makeGroupBreadCrumb() {
        final Group activeGroup = presenter.getActiveGroup();
        if ( activeGroup == null ) {
            return;
        }
        final NavLink link = new NavLink( activeGroup.getName() );
        link.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                presenter.selectRepository( null );
            }
        } );
        breadcrumbs.add( link );
    }

    private void makeRepositoryBreadCrumb() {
        final Repository activeRepository = presenter.getActiveRepository();
        if ( activeRepository == null ) {
            return;
        }
        final NavLink link = new NavLink( activeRepository.getAlias() );
        link.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                presenter.selectProject( null );
            }
        } );
        breadcrumbs.add( link );
    }

    private void makeProjectBreadCrumb() {
        final Project activeProject = presenter.getActiveProject();
        if ( activeProject == null ) {
            return;
        }
        final NavLink link = new NavLink( activeProject.getProjectName() );
        link.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                presenter.selectProjectRoot();
            }
        } );
        breadcrumbs.add( link );
    }

    private void makeFolderListingBreadCrumb() {
        final FolderListing activeFolderListing = presenter.getActiveFolderListing();
        if ( activeFolderListing == null ) {
            return;
        }
        for ( final Path segment : activeFolderListing.getSegments() ) {
            final NavLink link = new NavLink( segment.getFileName() );
            link.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( final ClickEvent event ) {
                    presenter.selectFolder( segment );
                }
            } );
            breadcrumbs.add( link );
        }
    }

    @Override
    public void addRepository( final Repository repository ) {
        sortedRepositories.add( repository );
        refresh( Context.REPOSITORIES );
    }

    @Override
    public void addProject( final Project project ) {
        sortedProjects.add( project );
        refresh( Context.PROJECTS );
    }

    @Override
    public void addItem( final FolderItem item ) {
        sortedFolderListing.add( item );
        refresh( Context.PATHS );
    }

    @Override
    public void removeItem( final FolderItem item ) {
        sortedFolderListing.remove( item );
        refresh( Context.PATHS );
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
