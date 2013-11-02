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
package org.kie.workbench.common.screens.explorer.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Divider;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.context.ProjectContext;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.widgets.business.BusinessViewPresenterImpl;
import org.kie.workbench.common.screens.explorer.client.widgets.technical.TechnicalViewPresenterImpl;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.common.ContextDropdownButton;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.*;

/**
 * Repository, Package, Folder and File explorer
 */
@ApplicationScoped
@WorkbenchScreen(identifier = "org.kie.guvnor.explorer")
public class ExplorerPresenterImpl implements ExplorerPresenter {

    @Inject
    private ExplorerView view;

    @Inject
    private BusinessViewPresenterImpl businessViewPresenter;

    @Inject
    private TechnicalViewPresenterImpl technicalViewPresenter;

    @Inject
    private ProjectContext context;

    @Inject
    protected Caller<ExplorerService> explorerService;

    private final NavLink businessView = new NavLink( "Project View" );
    private final NavLink techView = new NavLink( "Repository View" );
    private final NavLink treeExplorer = new NavLink( "Show as Links" );
    private final NavLink breadcrumbExplorer = new NavLink( "Show as Folders" );
//    private final NavLink hiddenFiles = new NavLink( "Display hidden items" );

    private Set<Option> options = new HashSet<Option>( Arrays.asList( Option.BUSINESS_CONTENT, Option.EXCLUDE_HIDDEN_ITEMS ) );

    @AfterInitialization
    public void init() {
        explorerService.call( new RemoteCallback<Set<Option>>() {
                                  @Override
                                  public void callback( Set<Option> o ) {
                                      if ( o != null && !o.isEmpty() ) {
                                          options.clear();
                                          options.addAll( o );
                                      }
                                      config();
                                  }
                              }, new ErrorCallback<Object>() {
                                  @Override
                                  public boolean error( Object o,
                                                        Throwable throwable ) {
                                      config();
                                      return false;
                                  }
                              }
                            ).getLastUserOptions();
    }

    private void config() {
        businessView.setIconSize( IconSize.SMALL );
        businessView.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent clickEvent ) {
                if ( !options.contains( Option.BUSINESS_CONTENT ) ) {
                    selectBusinessView();
                    activateBusinessView();
                    setupMenuItems();
                }
            }
        } );

        techView.setIconSize( IconSize.SMALL );
        techView.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent clickEvent ) {
                if ( !options.contains( Option.TECHNICAL_CONTENT ) ) {
                    selectTechnicalView();
                    activateTechView();
                    setupMenuItems();
                }
            }
        } );

//        hiddenFiles.setIconSize( IconSize.SMALL );
//        hiddenFiles.addClickHandler( new ClickHandler() {
//            @Override
//            public void onClick( ClickEvent clickEvent ) {
//                if ( options.contains( Option.EXCLUDE_HIDDEN_ITEMS ) ) {
//                    includeHiddenItems();
//                } else {
//                    excludeHiddenItems();
//                }
//                update();
//            }
//        } );

        treeExplorer.setIconSize( IconSize.SMALL );
        treeExplorer.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent clickEvent ) {
                if ( !options.contains( Option.TREE_NAVIGATOR ) ) {
                    showTreeNav();
                    update();
                }
            }
        } );

        breadcrumbExplorer.setIconSize( IconSize.SMALL );
        breadcrumbExplorer.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent clickEvent ) {
                if ( !options.contains( Option.BREADCRUMB_NAVIGATOR ) ) {
                    showBreadcrumbNav();
                    update();
                }
            }
        } );

        if ( options.contains( Option.BUSINESS_CONTENT ) ) {
            selectBusinessView();
            activateBusinessView();
        } else {
            selectTechnicalView();
            activateTechView();
        }

        setupMenuItems();
        update();
    }

    private void setupMenuItems() {
        if ( options == null ) {
            return;
        }
        if ( options.contains( Option.EXCLUDE_HIDDEN_ITEMS ) ) {
            excludeHiddenItems();
        } else {
            includeHiddenItems();
        }

        if ( options.contains( Option.TREE_NAVIGATOR ) ) {
            showTreeNav();
        } else {
            showBreadcrumbNav();
        }
    }

    private void showBreadcrumbNav() {
        options.add( Option.BREADCRUMB_NAVIGATOR );
        options.remove( Option.TREE_NAVIGATOR );
        breadcrumbExplorer.setIcon( IconType.ASTERISK );
        treeExplorer.setIcon( null );
    }

    private void showTreeNav() {
        options.remove( Option.BREADCRUMB_NAVIGATOR );
        options.add( Option.TREE_NAVIGATOR );
        treeExplorer.setIcon( IconType.ASTERISK );
        breadcrumbExplorer.setIcon( null );
    }

    private void activateTechView() {
        options.remove( Option.BUSINESS_CONTENT );
        options.add( Option.TECHNICAL_CONTENT );
        techView.setIcon( IconType.ASTERISK );
        businessView.setIcon( null );
//        hiddenFiles.setDisabled( false );
    }

    private void activateBusinessView() {
        options.add( Option.BUSINESS_CONTENT );
        options.remove( Option.TECHNICAL_CONTENT );
        businessView.setIcon( IconType.ASTERISK );
        techView.setIcon( null );
//        hiddenFiles.setDisabled( true );
    }

    private void includeHiddenItems() {
        options.add( Option.INCLUDE_HIDDEN_ITEMS );
        options.remove( Option.EXCLUDE_HIDDEN_ITEMS );
//        hiddenFiles.setIcon( IconType.CHECK );
    }

    private void excludeHiddenItems() {
        options.remove( Option.INCLUDE_HIDDEN_ITEMS );
        options.add( Option.EXCLUDE_HIDDEN_ITEMS );
//        hiddenFiles.setIcon( null );
    }

    @WorkbenchPartView
    public UberView<ExplorerPresenterImpl> getView() {
        return this.view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectExplorerConstants.INSTANCE.explorerTitle();
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return Position.WEST;
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new MenuCustom<Widget>() {

                            @Override
                            public Widget build() {
                                return new ContextDropdownButton() {
                                    {
                                        displayCaret( false );
                                        setRightDropdown( true );
                                        setIcon( IconType.COG );
                                        setSize( MINI );

                                        add( businessView );
                                        add( techView );
                                        add( new Divider() );
                                        add( breadcrumbExplorer );
                                        add( treeExplorer );
//                                        add( new Divider() );
//                                        add( hiddenFiles );
                                    }
                                };
                            }

                            @Override
                            public boolean isEnabled() {
                                return false;
                            }

                            @Override
                            public void setEnabled( boolean enabled ) {

                            }

                            @Override
                            public String getContributionPoint() {
                                return null;
                            }

                            @Override
                            public String getCaption() {
                                return null;
                            }

                            @Override
                            public MenuPosition getPosition() {
                                return null;
                            }

                            @Override
                            public int getOrder() {
                                return 0;
                            }

                            @Override
                            public void addEnabledStateChangeListener( EnabledStateChangeListener listener ) {

                            }

                            @Override
                            public String getSignatureId() {
                                return null;
                            }

                            @Override
                            public Collection<String> getRoles() {
                                return null;
                            }

                            @Override
                            public Collection<String> getTraits() {
                                return null;
                            }
                        };
                    }
                } ).endMenu().build();
    }

    @Override
    public void selectBusinessView() {
        businessViewPresenter.setVisible( true );
        technicalViewPresenter.setVisible( false );
        options = businessViewPresenter.getActiveOptions();
        businessViewPresenter.initialiseViewForActiveContext( context.getActiveOrganizationalUnit(),
                                                              context.getActiveRepository(),
                                                              context.getActiveProject(),
                                                              context.getActivePackage() );
    }

    @Override
    public void selectTechnicalView() {
        businessViewPresenter.setVisible( false );
        technicalViewPresenter.setVisible( true );
        options = technicalViewPresenter.getActiveOptions();
        technicalViewPresenter.initialiseViewForActiveContext( context.getActiveOrganizationalUnit(),
                                                               context.getActiveRepository(),
                                                               context.getActiveProject(),
                                                               context.getActivePackage() );
    }

    @Override
    public void refresh() {
        if ( businessViewPresenter.isVisible() ) {
            businessViewPresenter.refresh();

        } else if ( technicalViewPresenter.isVisible() ) {
            technicalViewPresenter.refresh();
        }
    }

    private void update() {
        if ( businessViewPresenter.isVisible() ) {
            businessViewPresenter.update( options );

        } else if ( technicalViewPresenter.isVisible() ) {
            technicalViewPresenter.update( options );
        }
    }
}
