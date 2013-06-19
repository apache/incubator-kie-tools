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
package org.kie.workbench.common.screens.explorer.client.widgets.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavList;
import com.github.gwtbootstrap.client.ui.SplitDropdownButton;
import com.github.gwtbootstrap.client.ui.Well;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.utils.Classifier;
import org.kie.workbench.common.screens.explorer.client.utils.Sorters;
import org.kie.workbench.common.screens.explorer.model.Item;
import org.kie.workbench.common.services.shared.context.Package;
import org.kie.workbench.common.services.shared.context.Project;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.client.workbench.type.ClientResourceType;

/**
 * Business View implementation
 */
@ApplicationScoped
public class BusinessViewWidget extends Composite implements BusinessView {

    interface BusinessViewImplBinder
            extends
            UiBinder<Widget, BusinessViewWidget> {

    }

    private static BusinessViewImplBinder uiBinder = GWT.create( BusinessViewImplBinder.class );

    private static final String MISCELLANEOUS = "Miscellaneous";

    @UiField
    Well breadCrumbs;

    @UiField
    SplitDropdownButton ddGroups;

    @UiField
    SplitDropdownButton ddRepositories;

    @UiField
    SplitDropdownButton ddProjects;

    @UiField
    SplitDropdownButton ddPackages;

    @UiField
    Accordion itemsContainer;

    @Inject
    Classifier classifier;

    //TreeSet sorts members upon insertion
    private final Set<Group> sortedGroups = new TreeSet<Group>( Sorters.GROUP_SORTER );
    private final Set<Repository> sortedRepositories = new TreeSet<Repository>( Sorters.REPOSITORY_SORTER );
    private final Set<Project> sortedProjects = new TreeSet<Project>( Sorters.PROJECT_SORTER );
    private final Set<Package> sortedPackages = new TreeSet<Package>( Sorters.PACKAGE_SORTER );

    private BusinessViewPresenter presenter;

    public BusinessViewWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final BusinessViewPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setGroups( final Collection<Group> groups,
                           final Group activeGroup ) {
        ddGroups.clear();
        if ( !groups.isEmpty() ) {
            sortedGroups.clear();
            sortedGroups.addAll( groups );

            for ( Group group : sortedGroups ) {
                ddGroups.add( makeGroupNavLink( group ) );
            }

            final Group selectedGroup = getSelectedGroup( sortedGroups,
                                                          activeGroup );
            ddGroups.setText( selectedGroup.getName() );
            ddGroups.getTriggerWidget().setEnabled( true );
            presenter.groupSelected( selectedGroup );
        } else {
            ddGroups.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddGroups.getTriggerWidget().setEnabled( false );
            ddRepositories.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddRepositories.getTriggerWidget().setEnabled( false );
            ddProjects.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddProjects.getTriggerWidget().setEnabled( false );
            ddPackages.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddPackages.getTriggerWidget().setEnabled( false );
        }
    }

    private Group getSelectedGroup( final Set<Group> groups,
                                    final Group activeGroup ) {
        if ( groups.isEmpty() ) {
            return null;
        }
        if ( activeGroup != null && groups.contains( activeGroup ) ) {
            return activeGroup;
        }
        return groups.iterator().next();
    }

    private IsWidget makeGroupNavLink( final Group group ) {
        final NavLink navLink = new NavLink( group.getName() );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                ddGroups.setText( group.getName() );
                presenter.groupSelected( group );
            }
        } );
        return navLink;
    }

    @Override
    public void setRepositories( final Collection<Repository> repositories,
                                 final Repository activeRepository ) {
        ddRepositories.clear();
        if ( !repositories.isEmpty() ) {
            sortedRepositories.clear();
            sortedRepositories.addAll( repositories );

            for ( Repository repository : sortedRepositories ) {
                ddRepositories.add( makeRepositoryNavLink( repository ) );
            }

            final Repository selectedRepository = getSelectedRepository( sortedRepositories,
                                                                         activeRepository );
            ddRepositories.setText( selectedRepository.getAlias() );
            ddRepositories.getTriggerWidget().setEnabled( true );
            presenter.repositorySelected( selectedRepository );
        } else {
            ddRepositories.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddRepositories.getTriggerWidget().setEnabled( false );
            ddProjects.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddProjects.getTriggerWidget().setEnabled( false );
            ddPackages.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddPackages.getTriggerWidget().setEnabled( false );
        }
    }

    private Repository getSelectedRepository( final Set<Repository> repositories,
                                              final Repository activeRepository ) {
        if ( repositories.isEmpty() ) {
            return null;
        }
        if ( activeRepository != null && repositories.contains( activeRepository ) ) {
            return activeRepository;
        }
        return repositories.iterator().next();
    }

    private IsWidget makeRepositoryNavLink( final Repository repository ) {
        final NavLink navLink = new NavLink( repository.getAlias() );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                ddRepositories.setText( repository.getAlias() );
                presenter.repositorySelected( repository );
            }
        } );
        return navLink;
    }

    @Override
    public void setProjects( final Collection<Project> projects,
                             final Project activeProject ) {
        ddProjects.clear();
        if ( !projects.isEmpty() ) {
            sortedProjects.clear();
            sortedProjects.addAll( projects );

            for ( Project project : sortedProjects ) {
                ddProjects.add( makeProjectNavLink( project ) );
            }

            final Project selectedProject = getSelectedProject( sortedProjects,
                                                                activeProject );
            ddProjects.setText( selectedProject.getTitle() );
            ddProjects.getTriggerWidget().setEnabled( true );
            presenter.projectSelected( selectedProject );
        } else {
            ddProjects.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddProjects.getTriggerWidget().setEnabled( false );
            ddPackages.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddPackages.getTriggerWidget().setEnabled( false );
        }
    }

    private Project getSelectedProject( final Set<Project> projects,
                                        final Project activeProject ) {
        if ( projects.isEmpty() ) {
            return null;
        }
        if ( activeProject != null && projects.contains( activeProject ) ) {
            return activeProject;
        }
        return projects.iterator().next();
    }

    private IsWidget makeProjectNavLink( final Project project ) {
        final NavLink navLink = new NavLink( project.getTitle() );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                ddProjects.setText( project.getTitle() );
                presenter.projectSelected( project );
            }
        } );
        return navLink;
    }

    @Override
    public void setPackages( final Collection<Package> packages,
                             final Package activePackage ) {
        ddPackages.clear();
        if ( !packages.isEmpty() ) {
            sortedPackages.clear();
            sortedPackages.addAll( packages );

            for ( Package pkg : sortedPackages ) {
                ddPackages.add( makePackageNavLink( pkg ) );
            }

            final Package selectedPackage = getSelectedPackage( sortedPackages,
                                                                activePackage );
            ddPackages.setText( selectedPackage.getCaption() );
            ddPackages.getTriggerWidget().setEnabled( true );
            presenter.packageSelected( selectedPackage );
        } else {
            ddPackages.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddPackages.getTriggerWidget().setEnabled( false );
        }
    }

    private Package getSelectedPackage( final Set<Package> packages,
                                        final Package activePackage ) {
        if ( packages.isEmpty() ) {
            return null;
        }
        if ( activePackage != null && packages.contains( activePackage ) ) {
            return activePackage;
        }
        return packages.iterator().next();
    }

    private IsWidget makePackageNavLink( final Package pkg ) {
        final NavLink navLink = new NavLink( pkg.getCaption() );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                ddPackages.setText( pkg.getCaption() );
                presenter.packageSelected( pkg );
            }
        } );
        return navLink;
    }

    @Override
    public void setItems( final Collection<Item> items ) {
        itemsContainer.clear();
        if ( !items.isEmpty() ) {
            final List<Item> sortedItems = new ArrayList<Item>( items );
            Collections.sort( sortedItems,
                              Sorters.ITEM_SORTER );
            final Map<ClientResourceType, Collection<Item>> resourceTypeGroups = classifier.group( sortedItems );
            final TreeMap<ClientResourceType, Collection<Item>> sortedResourceTypeGroups = new TreeMap<ClientResourceType, Collection<Item>>( Sorters.RESOURCE_TYPE_GROUP_SORTER );
            sortedResourceTypeGroups.putAll( resourceTypeGroups );
            for ( Map.Entry<ClientResourceType, Collection<Item>> e : sortedResourceTypeGroups.entrySet() ) {
                final AccordionGroup group = new AccordionGroup();
                group.addCustomTrigger( makeTriggerWidget( e.getKey() ) );
                final NavList itemsNavList = new NavList();
                group.add( itemsNavList );
                for ( Item item : e.getValue() ) {
                    itemsNavList.add( makeItemNavLink( item ) );
                }
                itemsContainer.add( group );
            }

        } else {
            itemsContainer.add( new Label( ProjectExplorerConstants.INSTANCE.noItemsExist() ) );
        }
    }

    private AccordionGroupTriggerWidget makeTriggerWidget( final ClientResourceType resourceType ) {
        final String description = getResourceTypeDescription( resourceType );
        final IsWidget icon = resourceType.getIcon();
        if ( icon == null ) {
            return new AccordionGroupTriggerWidget( description );
        }
        return new AccordionGroupTriggerWidget( icon,
                                                description );
    }

    private String getResourceTypeDescription( final ClientResourceType resourceType ) {
        String description = resourceType.getDescription();
        description = ( description == null || description.isEmpty() ) ? MISCELLANEOUS : description;
        return description;
    }

    private IsWidget makeItemNavLink( final Item item ) {
        final NavLink navLink = new NavLink( item.getFileName() );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.itemSelected( item );
            }
        } );
        return navLink;
    }

    @Override
    public void addRepository( final Repository newRepository ) {
        ddRepositories.clear();
        sortedRepositories.add( newRepository );
        for ( Repository repository : sortedRepositories ) {
            ddRepositories.add( makeRepositoryNavLink( repository ) );
        }
    }

    @Override
    public void addProject( final Project newProject ) {
        ddProjects.clear();
        sortedProjects.add( newProject );
        for ( Project project : sortedProjects ) {
            ddProjects.add( makeProjectNavLink( project ) );
        }
    }

    @Override
    public void addPackage( final Package newPackage ) {
        ddPackages.clear();
        sortedPackages.add( newPackage );
        for ( Package pkg : sortedPackages ) {
            ddPackages.add( makePackageNavLink( pkg ) );
        }
    }

}
