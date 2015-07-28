/*
 * Copyright 2015 JBoss Inc
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
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.workbench.common.screens.explorer.client.resources.ProjectExplorerResources;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.utils.IdHelper;
import org.kie.workbench.common.screens.explorer.client.widgets.ViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.dropdown.CustomDropdown;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class Explorer extends Composite {

    public enum Mode {
        REGULAR, EXPANDED, COLLAPSED
    }

    public enum NavType {
        TREE, BREADCRUMB
    }

    private final FlowPanel container = new FlowPanel();
    private final CustomDropdown organizationUnits = new CustomDropdown();
    private final CustomDropdown repos = new CustomDropdown();
    private final CustomDropdown prjs = new CustomDropdown();
    private NavigatorBreadcrumbs navigatorBreadcrumbs;
    private boolean hideHeaderNavigator = false;

    private Mode mode = Mode.REGULAR;

    private boolean isAlreadyInitialized = false;

    private ViewPresenter presenter = null;
    private Navigator activeNavigator = null;
    private Map<NavType, Navigator> navigators = new HashMap<NavType, Navigator>();

    public Explorer() {
        initWidget( container );
        IdHelper.setId( container, "pex_nav_" );
    }

    public void init( final Mode mode,
                      final NavigatorOptions options,
                      final NavType navType,
                      final ViewPresenter presenter ) {
        this.presenter = presenter;
        this.mode = mode;
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

        if ( mode.equals( Mode.EXPANDED ) ) {
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
                        presenter.organizationalUnitSelected( ou );
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
                        presenter.repositorySelected( repository );
                    }
                } );
            }} );
        }

        this.prjs.clear();
        if ( activeProject != null ) {
            this.prjs.setText( activeProject.getProjectName() );
        }

        for ( final Project project : projects ) {
            this.prjs.add( new AnchorListItem( project.getProjectName() ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.projectSelected( project );
                    }
                } );
            }} );
        }

        if ( organizationalUnits.isEmpty() ) {
            this.organizationUnits.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.organizationUnits.add( new AnchorListItem( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
            this.organizationUnits.setEnableTriggerWidget( false );
            this.repos.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.repos.add( new AnchorListItem( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
            this.repos.setEnableTriggerWidget( false );
            this.prjs.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.prjs.setEnableTriggerWidget( false );
            this.prjs.add( new AnchorListItem( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
        } else if ( repositories.isEmpty() ) {
            this.repos.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.repos.setEnableTriggerWidget( false );
            this.repos.add( new AnchorListItem( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
            this.prjs.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.prjs.setEnableTriggerWidget( false );
            this.prjs.add( new AnchorListItem( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
        } else if ( projects.isEmpty() ) {
            this.prjs.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.prjs.add( new AnchorListItem( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
            this.prjs.setEnableTriggerWidget( false );
        }

        if ( !isAlreadyInitialized ) {
            container.clear();
            if ( !mode.equals( Mode.REGULAR ) ) {
                final Button button = new Button();
                button.setIcon( IconType.CHEVRON_DOWN );
                button.setPull( Pull.RIGHT );
                button.getElement().getStyle().setMarginTop( 10, Style.Unit.PX );
                button.addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( final ClickEvent clickEvent ) {
                        if ( button.getIcon().equals( IconType.CHEVRON_DOWN ) ) {
                            button.setIcon( IconType.CHEVRON_UP );
                            onExpandNavigator();
                        } else {
                            button.setIcon( IconType.CHEVRON_DOWN );
                            onCollapseNavigator();
                        }
                    }
                } );

                if ( mode.equals( Mode.COLLAPSED ) ) {
                    button.setIcon( IconType.CHEVRON_DOWN );
                } else {
                    button.setIcon( IconType.CHEVRON_UP );
                }
                container.add( button );
            }

            this.navigatorBreadcrumbs = new NavigatorBreadcrumbs( NavigatorBreadcrumbs.Mode.HEADER ) {{
                build( organizationUnits, repos, prjs );
            }};

            if ( hideHeaderNavigator ) {
                navigatorBreadcrumbs.setVisible( false );
            }

            container.add( navigatorBreadcrumbs );

            if ( activeNavigator != null && mode.equals( Mode.EXPANDED ) ) {
                container.add( activeNavigator );
            }

            isAlreadyInitialized = true;
        }
    }

    public void hideHeaderNavigator() {
        hideHeaderNavigator = true;
        if ( navigatorBreadcrumbs != null ) {
            navigatorBreadcrumbs.setVisible( false );
        }
    }

    public void loadContent( FolderListing folderListing ) {
        this.loadContent( folderListing, null );
    }

    public void loadContent( final FolderListing content,
                             final Map<FolderItem, List<FolderItem>> siblings ) {
        if ( content != null ) {
            activeNavigator.loadContent( content, siblings );
        }
    }

    private void onCollapseNavigator() {
        if ( activeNavigator.isAttached() ) {
            container.remove( activeNavigator );
        }
        mode = Mode.COLLAPSED;
    }

    private void onExpandNavigator() {
        if ( !activeNavigator.isAttached() ) {
            container.add( activeNavigator );
        }
        mode = Mode.EXPANDED;
    }
}