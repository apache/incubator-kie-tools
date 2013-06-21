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

    public TechnicalViewWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final TechnicalViewPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setGroups( final Collection<Group> groups,
                           final Group activeGroup ) {
        sortedGroups.clear();
        sortedGroups.addAll( groups );
        this.activeGroup = activeGroup;
        refresh( Context.GROUPS );
    }

    @Override
    public void setRepositories( final Collection<Repository> repositories,
                                 final Repository activeRepository ) {
        sortedRepositories.clear();
        sortedRepositories.addAll( repositories );
        this.activeRepository = activeRepository;
        refresh( Context.REPOSITORIES );
    }

    @Override
    public void setProjects( final Collection<Project> projects,
                             final Project activeProject ) {
        sortedProjects.clear();
        sortedProjects.addAll( projects );
        this.activeProject = activeProject;
        refresh( Context.PROJECTS );
    }

    private void refresh( final Context context ) {
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
        items.add( makeParentProjectNavLink() );
        items.add( new Label( "-- TODO-- " ) );
    }

    private IsWidget makeParentProjectNavLink() {
        final NavLink navLink = new NavLink( ".." );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                context = Context.PROJECTS;
                presenter.projectSelected( null );
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

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

}
