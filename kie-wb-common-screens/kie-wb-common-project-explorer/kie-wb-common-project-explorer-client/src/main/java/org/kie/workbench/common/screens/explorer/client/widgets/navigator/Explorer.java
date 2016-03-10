/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.explorer.client.widgets.navigator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.utils.IdHelper;
import org.kie.workbench.common.screens.explorer.client.widgets.BaseViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.dropdown.CustomDropdown;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class Explorer extends Composite {

    public enum NavType {
        TREE, BREADCRUMB
    }

    private final FlowPanel container = new FlowPanel();
    private final CustomDropdown organizationUnits = new CustomDropdown();
    private final CustomDropdown repos = new CustomDropdown();
    private final CustomDropdown projects = new CustomDropdown();
    private NavigatorBreadcrumbs navigatorBreadcrumbs;
    private NavigatorExpandCollapseButton navigatorExpandCollapseButton;
    private boolean hideHeaderNavigator = false;

    private boolean isAlreadyInitialized = false;

    private BaseViewPresenter presenter = null;
    private Navigator activeNavigator = null;
    private Map<NavType, Navigator> navigators = new HashMap<NavType, Navigator>();

    public Explorer() {
        initWidget( container );
        IdHelper.setId( container, "pex_nav_" );
    }

    public void init( final NavigatorExpandCollapseButton.Mode mode,
                      final NavigatorOptions options,
                      final NavType navType,
                      final BaseViewPresenter presenter ) {
        this.presenter = presenter;
        this.navigatorExpandCollapseButton = new NavigatorExpandCollapseButton( mode );
        setNavType( navType, options );
    }

    public void setNavType( final NavType navType,
                            final NavigatorOptions options ) {
        checkNotNull( "navType", navType );
        if ( activeNavigator != null ) {
            if ( navType.equals( NavType.TREE ) && activeNavigator instanceof TreeNavigator ) {
                activeNavigator.loadContent( presenter.getActiveContent() );
                return;
            } else if ( navType.equals( NavType.BREADCRUMB ) && activeNavigator instanceof BreadcrumbNavigator ) {
                activeNavigator.loadContent( presenter.getActiveContent() );
                return;
            }
            container.remove( activeNavigator );
        }

        if ( !navigators.containsKey( navType ) ) {
            if ( navType.equals( NavType.TREE ) ) {
                activeNavigator = IOC.getBeanManager().lookupBean( TreeNavigator.class ).getInstance();
            } else {
                activeNavigator = IOC.getBeanManager().lookupBean( BreadcrumbNavigator.class ).getInstance();
            }
            activeNavigator.setPresenter( presenter );
            activeNavigator.setOptions( options );
            navigators.put( navType, activeNavigator );
        } else {
            activeNavigator = navigators.get( navType );
        }

        if ( navigatorExpandCollapseButton.isExpanded() ) {
            container.add( activeNavigator );
        }

        activeNavigator.loadContent( presenter.getActiveContent() );
    }

    public void clear() {
        for ( final Navigator navigator : navigators.values() ) {
            navigator.clear();
        }
    }

    public void setupHeader( final Set<OrganizationalUnit> organizationalUnits,
                             final OrganizationalUnit activeOrganizationalUnit,
                             final Set<Repository> repositories,
                             final Repository activeRepository,
                             final Set<Project> projects,
                             final Project activeProject ) {

        this.organizationUnits.clear();
        if ( activeOrganizationalUnit != null ) {
            this.organizationUnits.setText( activeOrganizationalUnit.getName() );
        }
        for ( final OrganizationalUnit ou : organizationalUnits ) {
            this.organizationUnits.add( new AnchorListItem( ou.getName() ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.onOrganizationalUnitSelected( ou );
                    }
                } );
            }} );
        }

        this.repos.clear();
        if ( activeRepository != null ) {
            this.repos.setText( activeRepository.getAlias() );
        }

        for ( final Repository repository : repositories ) {
            this.repos.add( new AnchorListItem( repository.getAlias() ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.onRepositorySelected( repository );
                    }
                } );
            }} );
        }

        this.projects.clear();
        if ( activeProject != null ) {
            this.projects.setText( activeProject.getProjectName() );
        }

        for ( final Project project : projects ) {
            this.projects.add( new AnchorListItem( project.getProjectName() ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.onProjectSelected( project );
                    }
                } );
            }} );
        }

        if ( organizationalUnits.isEmpty() ) {
            this.organizationUnits.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.organizationUnits.add( new AnchorListItem( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
            this.repos.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.repos.add( new AnchorListItem( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
            this.projects.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.projects.add( new AnchorListItem( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
        } else if ( repositories.isEmpty() ) {
            this.repos.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.repos.add( new AnchorListItem( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
            this.projects.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.projects.add( new AnchorListItem( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
        } else if ( projects.isEmpty() ) {
            this.projects.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.projects.add( new AnchorListItem( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
        }

        if ( !isAlreadyInitialized ) {
            container.clear();

            setupNavigatorBreadcrumbs();
            setupNavigatorExpandCollapseButton();
            addDivToAlignComponents();

            if ( activeNavigator != null && navigatorExpandCollapseButton.isExpanded() ) {
                container.add( activeNavigator );
            }

            isAlreadyInitialized = true;
        }
    }

    private void addDivToAlignComponents() {
        FlowPanel divClear = new FlowPanel();
        divClear.getElement().getStyle().setClear( Style.Clear.BOTH );
        container.add( divClear );
    }

    private void setupNavigatorExpandCollapseButton() {
        this.navigatorExpandCollapseButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent clickEvent ) {
                navigatorExpandCollapseButton.invertMode();

                if ( navigatorExpandCollapseButton.isExpanded() ) {
                    onExpandNavigator();
                } else {
                    onCollapseNavigator();
                }
            }
        } );

        FlowPanel navigatorExpandCollapseButtonContainer = new FlowPanel();
        navigatorExpandCollapseButtonContainer.getElement().getStyle().setFloat( Style.Float.RIGHT );
        navigatorExpandCollapseButtonContainer.add( navigatorExpandCollapseButton );
        container.add( navigatorExpandCollapseButtonContainer );
    }

    private void setupNavigatorBreadcrumbs() {
        this.navigatorBreadcrumbs = new NavigatorBreadcrumbs( NavigatorBreadcrumbs.Mode.HEADER ) {{
            build( organizationUnits, repos, Explorer.this.projects );
        }};

        if ( hideHeaderNavigator ) {
            navigatorBreadcrumbs.setVisible( false );
        }

        FlowPanel navigatorBreadcrumbsContainer = new FlowPanel();
        navigatorBreadcrumbsContainer.getElement().getStyle().setFloat( Style.Float.LEFT );
        navigatorBreadcrumbsContainer.add( navigatorBreadcrumbs );
        container.add( navigatorBreadcrumbsContainer );
    }

    public void hideHeaderNavigator() {
        hideHeaderNavigator = true;
        if ( navigatorBreadcrumbs != null ) {
            navigatorBreadcrumbs.setVisible( false );
        }
    }

    public void loadContent( FolderListing content ) {
        if ( content != null ) {
            activeNavigator.loadContent( content );
        }
    }

    public void loadContent( final FolderListing content,
                             final Map<FolderItem, List<FolderItem>> siblings ) {
        if ( content != null ) {
            activeNavigator.loadContent( content,
                                         siblings );
        }
    }

    private void onCollapseNavigator() {
        if ( activeNavigator.isAttached() ) {
            container.remove( activeNavigator );
        }
    }

    private void onExpandNavigator() {
        if ( !activeNavigator.isAttached() ) {
            container.add( activeNavigator );
        }
    }
}