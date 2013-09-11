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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Collapse;
import com.github.gwtbootstrap.client.ui.CollapseTrigger;
import com.github.gwtbootstrap.client.ui.Divider;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavList;
import com.github.gwtbootstrap.client.ui.SplitDropdownButton;
import com.github.gwtbootstrap.client.ui.WellNavList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.utils.Classifier;
import org.kie.workbench.common.screens.explorer.client.utils.Sorters;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.workbench.type.AnyResourceType;
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
    SplitDropdownButton ddOrganizationalUnits;

    @UiField
    SplitDropdownButton ddRepositories;

    @UiField
    SplitDropdownButton ddProjects;

    @UiField
    SplitDropdownButton ddPackages;

    @UiField
    WellNavList itemsContainer;

    @Inject
    Classifier classifier;

    //TreeSet sorts members upon insertion
    private final Set<OrganizationalUnit> sortedOrganizationalUnits = new TreeSet<OrganizationalUnit>( Sorters.ORGANIZATIONAL_UNIT_SORTER );
    private final Set<Repository> sortedRepositories = new TreeSet<Repository>( Sorters.REPOSITORY_SORTER );
    private final Set<Project> sortedProjects = new TreeSet<Project>( Sorters.PROJECT_SORTER );
    private final Set<Package> sortedPackages = new TreeSet<Package>( Sorters.PACKAGE_SORTER );
    private final Set<FolderItem> sortedFolderItems = new TreeSet<FolderItem>( Sorters.ITEM_SORTER );

    private BusinessViewPresenter presenter;

    @PostConstruct
    public void init() {
        //Cannot create and bind UI until after injection points have been initialized
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final BusinessViewPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setContent( final Set<OrganizationalUnit> organizationalUnits,
                            final OrganizationalUnit organizationalUnit,
                            final Set<Repository> repositories,
                            final Repository repository,
                            final Set<Project> projects,
                            final Project project,
                            final Set<Package> packages,
                            final Package pkg,
                            final Collection<FolderItem> items ) {
        setOrganizationalUnits( organizationalUnits,
                                organizationalUnit );
        setRepositories( repositories,
                         repository );
        setProjects( projects,
                     project );
        setPackages( packages,
                     pkg );
        setItems( items );
    }

    private void setOrganizationalUnits( final Collection<OrganizationalUnit> organizationalUnits,
                                         final OrganizationalUnit selectedOrganizationalUnit ) {
        ddOrganizationalUnits.clear();
        if ( !organizationalUnits.isEmpty() ) {
            sortedOrganizationalUnits.clear();
            sortedOrganizationalUnits.addAll( organizationalUnits );

            for ( OrganizationalUnit organizationalUnit : sortedOrganizationalUnits ) {
                ddOrganizationalUnits.add( makeOrganizationalUnitNavLink( organizationalUnit ) );
            }

            ddOrganizationalUnits.setText( selectedOrganizationalUnit.getName() );
            ddOrganizationalUnits.getTriggerWidget().setEnabled( true );

        } else {
            setItems( Collections.EMPTY_LIST );
            ddOrganizationalUnits.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddOrganizationalUnits.getTriggerWidget().setEnabled( false );
            ddRepositories.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddRepositories.getTriggerWidget().setEnabled( false );
            ddProjects.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddProjects.getTriggerWidget().setEnabled( false );
            ddPackages.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddPackages.getTriggerWidget().setEnabled( false );
        }
    }

    private IsWidget makeOrganizationalUnitNavLink( final OrganizationalUnit organizationalUnit ) {
        final NavLink navLink = new NavLink( organizationalUnit.getName() );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                ddOrganizationalUnits.setText( organizationalUnit.getName() );
                presenter.organizationalUnitSelected( organizationalUnit );
            }
        } );
        return navLink;
    }

    private void setRepositories( final Collection<Repository> repositories,
                                  final Repository selectedRepository ) {
        ddRepositories.clear();

        if ( !repositories.isEmpty() ) {
            sortedRepositories.clear();
            sortedRepositories.addAll( repositories );

            for ( Repository repository : sortedRepositories ) {
                ddRepositories.add( makeRepositoryNavLink( repository ) );
            }

            ddRepositories.setText( selectedRepository.getAlias() );
            ddRepositories.getTriggerWidget().setEnabled( true );

        } else {
            setItems( Collections.EMPTY_LIST );
            ddRepositories.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddRepositories.getTriggerWidget().setEnabled( false );
            ddProjects.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddProjects.getTriggerWidget().setEnabled( false );
            ddPackages.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddPackages.getTriggerWidget().setEnabled( false );
        }
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

    private void setProjects( final Collection<Project> projects,
                              final Project selectedProject ) {
        ddProjects.clear();

        if ( !projects.isEmpty() ) {
            sortedProjects.clear();
            sortedProjects.addAll( projects );

            for ( Project project : sortedProjects ) {
                ddProjects.add( makeProjectNavLink( project ) );
            }

            ddProjects.setText( selectedProject.getProjectName() );
            ddProjects.getTriggerWidget().setEnabled( true );

        } else {
            setItems( Collections.EMPTY_LIST );
            ddProjects.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddProjects.getTriggerWidget().setEnabled( false );
            ddPackages.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddPackages.getTriggerWidget().setEnabled( false );
        }
    }

    private IsWidget makeProjectNavLink( final Project project ) {
        final NavLink navLink = new NavLink( project.getProjectName() );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                ddProjects.setText( project.getProjectName() );
                presenter.projectSelected( project );
            }
        } );
        return navLink;
    }

    private void setPackages( final Collection<Package> packages,
                              final Package selectedPackage ) {
        ddPackages.clear();

        if ( !packages.isEmpty() ) {
            sortedPackages.clear();
            sortedPackages.addAll( packages );

            for ( Package pkg : sortedPackages ) {
                ddPackages.add( makePackageNavLink( pkg ) );
            }

            ddPackages.setText( selectedPackage.getCaption() );
            ddPackages.getTriggerWidget().setEnabled( true );

        } else {
            setItems( Collections.EMPTY_LIST );
            ddPackages.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            ddPackages.getTriggerWidget().setEnabled( false );
        }
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
    public void setItems( final Collection<FolderItem> folderItems ) {
        itemsContainer.clear();
        sortedFolderItems.clear();
        sortedFolderItems.addAll( folderItems );
        if ( !folderItems.isEmpty() ) {
            final Map<ClientResourceType, Collection<FolderItem>> resourceTypeGroups = classifier.group( sortedFolderItems );
            final TreeMap<ClientResourceType, Collection<FolderItem>> sortedResourceTypeGroups = new TreeMap<ClientResourceType, Collection<FolderItem>>( Sorters.RESOURCE_TYPE_GROUP_SORTER );
            sortedResourceTypeGroups.putAll( resourceTypeGroups );

            final Iterator<Map.Entry<ClientResourceType, Collection<FolderItem>>> itr = sortedResourceTypeGroups.entrySet().iterator();
            while ( itr.hasNext() ) {
                final Map.Entry<ClientResourceType, Collection<FolderItem>> e = itr.next();

                final CollapseTrigger collapseTrigger = makeTriggerWidget( e.getKey() );

                final Collapse collapse = new Collapse();
                collapse.setExistTrigger( true );
                collapse.setId( e.getKey().getSuffix() );
                final NavList itemsNavList = new NavList();
                collapse.add( itemsNavList );
                for ( FolderItem folderItem : e.getValue() ) {
                    itemsNavList.add( makeItemNavLink( e.getKey(),
                                                       folderItem ) );
                }
                collapse.setDefaultOpen( false );

                itemsContainer.add( collapseTrigger );
                itemsContainer.add( collapse );
                if ( itr.hasNext() ) {
                    itemsContainer.add( new Divider() );
                }
            }

        } else {
            itemsContainer.add( new Label( ProjectExplorerConstants.INSTANCE.noItemsExist() ) );
        }
    }

    private CollapseTrigger makeTriggerWidget( final ClientResourceType resourceType ) {
        final CollapseTrigger collapseTrigger = new CollapseTrigger( "#" + resourceType.getSuffix() );
        final String description = getResourceTypeDescription( resourceType );
        final IsWidget icon = resourceType.getIcon();
        if ( icon == null ) {
            collapseTrigger.setWidget( new TriggerWidget( description ) );
        } else {
            collapseTrigger.setWidget( new TriggerWidget( icon,
                                                          description ) );

        }
        return collapseTrigger;
    }

    private String getResourceTypeDescription( final ClientResourceType resourceType ) {
        String description = resourceType.getDescription();
        description = ( description == null || description.isEmpty() ) ? MISCELLANEOUS : description;
        return description;
    }

    private IsWidget makeItemNavLink( final ClientResourceType resourceType,
                                      final FolderItem folderItem ) {
        String fileName = folderItem.getFileName();
        if ( !( resourceType instanceof AnyResourceType ) ) {
            fileName = Utils.getBaseFileName( fileName );
        }
        final NavLink navLink = new NavLink( fileName );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.itemSelected( folderItem );
            }
        } );
        return navLink;
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
