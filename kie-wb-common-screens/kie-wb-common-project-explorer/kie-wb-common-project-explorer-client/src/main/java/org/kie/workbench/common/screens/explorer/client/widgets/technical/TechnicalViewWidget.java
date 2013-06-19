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

import com.github.gwtbootstrap.client.ui.Label;
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

    //TreeSet sorts members upon insertion
    private final Set<Group> sortedGroups = new TreeSet<Group>( Sorters.GROUP_SORTER );
    private final Set<Repository> sortedRepositories = new TreeSet<Repository>( Sorters.REPOSITORY_SORTER );
    private final Set<Project> sortedProjects = new TreeSet<Project>( Sorters.PROJECT_SORTER );

    private enum Context {
        GROUP,
        REPOSITORY,
        PROJECT
    }

    private Context context;

    private TechnicalViewPresenter presenter;

    public TechnicalViewWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final TechnicalViewPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setGroups( final Collection<Group> groups ) {
        if ( !groups.isEmpty() ) {
            sortedGroups.clear();
            sortedGroups.addAll( groups );
            populateGroupView();
        } else {
            items.add( new Label( ProjectExplorerConstants.INSTANCE.noItemsExist() ) );
        }
    }

    @Override
    public void setRepositories( final Group parentGroup,
                                 final Collection<Repository> repositories ) {
        if ( !repositories.isEmpty() ) {
            sortedRepositories.clear();
            sortedRepositories.addAll( repositories );
        } else {
            items.add( new Label( ProjectExplorerConstants.INSTANCE.noItemsExist() ) );
        }
    }

    @Override
    public void setProjects( final Repository parentRepository,
                             final Collection<Project> projects ) {
        if ( !projects.isEmpty() ) {
            sortedProjects.clear();
            sortedProjects.addAll( projects );
        } else {
            items.add( new Label( ProjectExplorerConstants.INSTANCE.noItemsExist() ) );
        }
    }

    public void refresh() {

    }

    private void populateGroupView() {
        items.clear();
        for ( Group group : sortedGroups ) {
            items.add( makeGroupNavLink( group ) );
        }
    }

    private IsWidget makeGroupNavLink( final Group group ) {
        final NavLink navLink = new NavLink( group.getName() );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                populateRepositoryView();
            }
        } );
        return navLink;
    }

    private IsWidget makeParentGroupNavLink() {
        final NavLink navLink = new NavLink( ".." );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                populateGroupView();
            }
        } );
        return navLink;
    }

    private void populateRepositoryView() {
        items.clear();
        items.add( makeParentGroupNavLink() );
        for ( Repository repository : sortedRepositories ) {
            items.add( makeRepositoryNavLink( repository ) );
        }
    }

    private IsWidget makeRepositoryNavLink( final Repository repository ) {
        final NavLink navLink = new NavLink( repository.getAlias() );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                populateProjectView();
            }
        } );
        return navLink;
    }

    private IsWidget makeParentRepositoryNavLink() {
        final NavLink navLink = new NavLink( ".." );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                populateRepositoryView();
            }
        } );
        return navLink;
    }

    private void populateProjectView() {
        items.clear();
        items.add( makeParentRepositoryNavLink() );
        for ( Project project : sortedProjects ) {
            items.add( makeProjectNavLink( project ) );
        }
    }

    private IsWidget makeProjectNavLink( final Project project ) {
        final NavLink navLink = new NavLink( project.getTitle() );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
//                presenter.projectSelected( project );
            }
        } );
        return navLink;
    }

}
