package org.kie.workbench.common.screens.explorer.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SplitDropdownButton;
import com.github.gwtbootstrap.client.ui.Well;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.explorer.client.utils.Sorters;
import org.kie.workbench.common.screens.explorer.model.Item;
import org.kie.workbench.common.services.shared.project.Package;
import org.kie.workbench.common.services.project.service.model.*;
import org.kie.workbench.common.services.shared.project.Project;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;

/**
 * The Explorer's view implementation
 */
public class ExplorerViewImpl extends Composite implements ExplorerView {

    interface ExplorerViewImplBinder
            extends
            UiBinder<Widget, ExplorerViewImpl> {

    }

    private static ExplorerViewImplBinder uiBinder = GWT.create( ExplorerViewImplBinder.class );

    @UiField
    Well breadCrumbs;

    @UiField
    Accordion itemsContainer;

    private final SplitDropdownButton ddGroups = new SplitDropdownButton();
    private final SplitDropdownButton ddRepositories = new SplitDropdownButton();
    private final SplitDropdownButton ddProjects = new SplitDropdownButton();
    private final SplitDropdownButton ddPackages = new SplitDropdownButton();

    private ExplorerPresenter presenter;

    public ExplorerViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
        ddGroups.setIcon( IconType.GROUP );
        ddRepositories.setIcon( IconType.BUILDING );
        ddProjects.setIcon( IconType.DESKTOP );
        ddPackages.setIcon( IconType.FOLDER_OPEN );
        breadCrumbs.add( ddGroups );
        breadCrumbs.add( ddRepositories );
        breadCrumbs.add( ddProjects );
        breadCrumbs.add( ddPackages );
    }

    @Override
    public void init( final ExplorerPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setGroups( final Collection<Group> groups,
                           final Group activeGroup ) {
        ddGroups.clear();
        if ( !groups.isEmpty() ) {
            final List<Group> sortedGroups = new ArrayList<Group>( groups );
            Collections.sort( sortedGroups,
                              Sorters.GROUP_SORTER );

            final Group selectedGroup = getSelectedGroup( sortedGroups,
                                                          activeGroup );

            for ( Group group : sortedGroups ) {
                ddGroups.add( makeGroupNavLink( group ) );
            }
            ddGroups.setText( selectedGroup.getName() );
            ddGroups.getTriggerWidget().setEnabled( true );
            presenter.groupSelected( selectedGroup );
        } else {
            ddGroups.setText( Constants.INSTANCE.nullEntry() );
            ddGroups.getTriggerWidget().setEnabled( false );
            ddRepositories.setText( Constants.INSTANCE.nullEntry() );
            ddRepositories.getTriggerWidget().setEnabled( false );
            ddProjects.setText( Constants.INSTANCE.nullEntry() );
            ddProjects.getTriggerWidget().setEnabled( false );
            ddPackages.setText( Constants.INSTANCE.nullEntry() );
            ddPackages.getTriggerWidget().setEnabled( false );
            setItems( Collections.EMPTY_LIST );
        }
    }

    private Group getSelectedGroup( final List<Group> groups,
                                    final Group activeGroup ) {
        if ( groups.isEmpty() ) {
            return null;
        }
        if ( activeGroup != null && groups.contains( activeGroup ) ) {
            return activeGroup;
        }
        return groups.get( 0 );
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
            final List<Repository> sortedRepositories = new ArrayList<Repository>( repositories );
            Collections.sort( sortedRepositories,
                              Sorters.REPOSITORY_SORTER );

            final Repository selectedRepository = getSelectedRepository( sortedRepositories,
                                                                         activeRepository );

            for ( Repository repository : sortedRepositories ) {
                ddRepositories.add( makeRepositoryNavLink( repository ) );
            }
            ddRepositories.setText( selectedRepository.getAlias() );
            ddRepositories.getTriggerWidget().setEnabled( true );
            presenter.repositorySelected( selectedRepository );
        } else {
            ddRepositories.setText( Constants.INSTANCE.nullEntry() );
            ddRepositories.getTriggerWidget().setEnabled( false );
            ddProjects.setText( Constants.INSTANCE.nullEntry() );
            ddProjects.getTriggerWidget().setEnabled( false );
            ddPackages.setText( Constants.INSTANCE.nullEntry() );
            ddPackages.getTriggerWidget().setEnabled( false );
            setItems( Collections.EMPTY_LIST );
        }
    }

    private Repository getSelectedRepository( final List<Repository> repositories,
                                              final Repository activeRepository ) {
        if ( repositories.isEmpty() ) {
            return null;
        }
        if ( activeRepository != null && repositories.contains( activeRepository ) ) {
            return activeRepository;
        }
        return repositories.get( 0 );
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
            final List<Project> sortedProjects = new ArrayList<Project>( projects );
            Collections.sort( sortedProjects,
                              Sorters.PROJECT_SORTER );

            final Project selectedProject = getSelectedProject( sortedProjects,
                                                                activeProject );

            for ( Project project : sortedProjects ) {
                ddProjects.add( makeProjectNavLink( project ) );
            }
            ddProjects.setText( selectedProject.getTitle() );
            ddProjects.getTriggerWidget().setEnabled( true );
            presenter.projectSelected( selectedProject );
        } else {
            ddProjects.setText( Constants.INSTANCE.nullEntry() );
            ddProjects.getTriggerWidget().setEnabled( false );
            ddPackages.setText( Constants.INSTANCE.nullEntry() );
            ddPackages.getTriggerWidget().setEnabled( false );
            setItems( Collections.EMPTY_LIST );
        }
    }

    private Project getSelectedProject( final List<Project> projects,
                                        final Project activeProject ) {
        if ( projects.isEmpty() ) {
            return null;
        }
        if ( activeProject != null && projects.contains( activeProject ) ) {
            return activeProject;
        }
        return projects.get( 0 );
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
            final List<Package> sortedPackages = new ArrayList<Package>( packages );
            Collections.sort( sortedPackages,
                              Sorters.PACKAGE_SORTER );

            final Package selectedPackage = getSelectedPackage( sortedPackages,
                                                                activePackage );

            for ( Package pkg : sortedPackages ) {
                ddPackages.add( makePackageNavLink( pkg ) );
            }
            ddPackages.setText( selectedPackage.getCaption() );
            ddPackages.getTriggerWidget().setEnabled( true );
            presenter.packageSelected( selectedPackage );
        } else {
            ddPackages.setText( Constants.INSTANCE.nullEntry() );
            ddPackages.getTriggerWidget().setEnabled( false );
            setItems( Collections.EMPTY_LIST );
        }
    }

    private Package getSelectedPackage( final List<Package> packages,
                                        final Package activePackage ) {
        if ( packages.isEmpty() ) {
            return null;
        }
        if ( activePackage != null && packages.contains( activePackage ) ) {
            return activePackage;
        }
        return packages.get( 0 );
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
            final AccordionGroup group = new AccordionGroup();
            group.setHeading( "Items" );
            for ( Item item : sortedItems ) {
                group.add( makeItemNavLink( item ) );
            }
            itemsContainer.add( group );
        } else {
            itemsContainer.add( new Label( Constants.INSTANCE.nullEntry() ) );
        }
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
}
