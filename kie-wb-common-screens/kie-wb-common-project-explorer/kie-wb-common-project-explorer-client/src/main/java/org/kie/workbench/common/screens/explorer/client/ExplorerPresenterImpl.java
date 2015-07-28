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

import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.Divider;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.widgets.BranchChangeHandler;
import org.kie.workbench.common.screens.explorer.client.widgets.URLHelper;
import org.kie.workbench.common.screens.explorer.client.widgets.business.BusinessViewPresenterImpl;
import org.kie.workbench.common.screens.explorer.client.widgets.technical.TechnicalViewPresenterImpl;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

/**
 * Repository, Package, Folder and File explorer
 */
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

    @Inject
    protected PlaceManager placeManager;

    @Inject
    protected Event<ProjectContextChangeEvent> contextChangedEvent;

    private final AnchorListItem businessView = new AnchorListItem( ProjectExplorerConstants.INSTANCE.projectView() );
    private final AnchorListItem techView = new AnchorListItem( ProjectExplorerConstants.INSTANCE.repositoryView() );
    private final AnchorListItem treeExplorer = new AnchorListItem( ProjectExplorerConstants.INSTANCE.showAsFolders() );
    private final AnchorListItem breadcrumbExplorer = new AnchorListItem( ProjectExplorerConstants.INSTANCE.showAsLinks() );
    private final AnchorListItem showTagFilter = new AnchorListItem( ProjectExplorerConstants.INSTANCE.enableTagFiltering() );
    private final AnchorListItem archiveRepository = new AnchorListItem( ProjectExplorerConstants.INSTANCE.downloadRepository() );
    private final AnchorListItem archiveProject = new AnchorListItem( ProjectExplorerConstants.INSTANCE.downloadProject() );

    private ActiveOptions options = new ActiveOptions();
    private String initPath = null;

    private Button projectScreenMenuItem;

    @AfterInitialization
    public void init() {
        addBranchChangeHandlers();
    }

    @PostConstruct
    protected void postConstruct() {
        businessView.setIconFixedWidth( true );
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

        techView.setIconFixedWidth( true );
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

        treeExplorer.setIconFixedWidth( true );
        treeExplorer.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent clickEvent ) {
                if ( !options.contains( Option.TREE_NAVIGATOR ) ) {
                    showTreeNav();
                    update();
                }
            }
        } );

        breadcrumbExplorer.setIconFixedWidth( true );
        breadcrumbExplorer.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent clickEvent ) {
                if ( !options.contains( Option.BREADCRUMB_NAVIGATOR ) ) {
                    showBreadcrumbNav();
                    update();
                }
            }
        } );

        showTagFilter.setIconFixedWidth( true );
        showTagFilter.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent clickEvent ) {
                if ( options.contains( Option.SHOW_TAG_FILTER ) ) {
                    disableTagFilter();
                } else {
                    enableTagFilter();
                }
                businessViewPresenter.update( options );
                technicalViewPresenter.update( options );
            }
        } );

        archiveProject.setIcon( IconType.DOWNLOAD );
        archiveProject.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                Window.open( URLHelper.getDownloadUrl( context.getActiveProject().getRootPath() ),
                             "downloading",
                             "resizable=no,scrollbars=yes,status=no" );
            }
        } );

        archiveRepository.setIcon( IconType.DOWNLOAD );
        archiveRepository.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                Window.open( URLHelper.getDownloadUrl( context.getActiveRepository().getRoot() ),
                             "downloading",
                             "resizable=no,scrollbars=yes,status=no" );
            }
        } );
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        final boolean noContextNavigationOption = ( Window.Location.getParameterMap().containsKey( "no_context_navigation" ) );
        final String paramExplorerMode = ( ( Window.Location.getParameterMap().containsKey( "explorer_mode" ) ) ? Window.Location.getParameterMap().get( "explorer_mode" ).get( 0 ) : "" ).trim();
        final String projectPathString = ( ( Window.Location.getParameterMap().containsKey( "path" ) ) ? Window.Location.getParameterMap().get( "path" ).get( 0 ) : null );

        this.initPath = placeRequest.getParameter( "init_path", projectPathString );
        final String explorerMode = placeRequest.getParameter( "mode", "" );
        final boolean noContext = placeRequest.getParameterNames().contains( "no_context" );

        if ( explorerMode.equalsIgnoreCase( "business_tree" ) ) {
            options.addAll( Option.BUSINESS_CONTENT, Option.TREE_NAVIGATOR );
        } else if ( explorerMode.equalsIgnoreCase( "business_explorer" ) ) {
            options.addAll( Option.BUSINESS_CONTENT, Option.BREADCRUMB_NAVIGATOR );
        } else if ( explorerMode.equalsIgnoreCase( "tech_tree" ) ) {
            options.addAll( Option.TECHNICAL_CONTENT, Option.TREE_NAVIGATOR );
        } else if ( explorerMode.equalsIgnoreCase( "tech_explorer" ) ) {
            options.addAll( Option.TECHNICAL_CONTENT, Option.BREADCRUMB_NAVIGATOR );
        } else if ( paramExplorerMode.equalsIgnoreCase( "business_tree" ) ) {
            options.addAll( Option.BUSINESS_CONTENT, Option.TREE_NAVIGATOR );
        } else if ( paramExplorerMode.equalsIgnoreCase( "business_explorer" ) ) {
            options.addAll( Option.BUSINESS_CONTENT, Option.BREADCRUMB_NAVIGATOR );
        } else if ( paramExplorerMode.equalsIgnoreCase( "tech_tree" ) ) {
            options.addAll( Option.TECHNICAL_CONTENT, Option.TREE_NAVIGATOR );
        } else if ( paramExplorerMode.equalsIgnoreCase( "tech_explorer" ) ) {
            options.addAll( Option.TECHNICAL_CONTENT, Option.BREADCRUMB_NAVIGATOR );
        }

        if ( noContext || noContextNavigationOption ) {
            options.add( Option.NO_CONTEXT_NAVIGATION );
        }

        if ( options.isEmpty() ) {
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
        } else {
            config();
        }
    }

    private void config() {
        // initialization required to ensure that the Tag Filter is correctly loaded
        businessViewPresenter.update( options );
        technicalViewPresenter.update( options );

        if ( options.isEmpty() ) {
            options.addAll( Option.BUSINESS_CONTENT, Option.EXCLUDE_HIDDEN_ITEMS );
        }

        if ( options.contains( Option.SHOW_TAG_FILTER ) ) {
            showTagFilter.setIcon( IconType.CHECK );
        } else {
            showTagFilter.setIcon( null );
        }

        if ( options.contains( Option.BUSINESS_CONTENT ) ) {
            selectBusinessView();
            activateBusinessView();
        } else if ( options.contains( Option.TECHNICAL_CONTENT ) ) {
            selectTechnicalView();
            activateTechView();
        }

        setupMenuItems();
        update();
    }

    private void addBranchChangeHandlers() {
        BranchChangeHandler branchChangeHandler = new BranchChangeHandler() {

            @Override
            public void onBranchSelected( String branch ) {
                businessViewPresenter.branchChanged( branch );
                technicalViewPresenter.branchChanged( branch );

                ProjectContextChangeEvent event = new ProjectContextChangeEvent( context.getActiveOrganizationalUnit(),
                                                                                 context.getActiveRepository(),
                                                                                 context.getActiveProject(),
                                                                                 branch );

                contextChangedEvent.fire( event );
            }
        };

        businessViewPresenter.addBranchChangeHandler( branchChangeHandler );
        technicalViewPresenter.addBranchChangeHandler( branchChangeHandler );
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
        breadcrumbExplorer.setIcon( IconType.CHECK );
        treeExplorer.setIcon( null );
    }

    private void showTreeNav() {
        options.remove( Option.BREADCRUMB_NAVIGATOR );
        options.add( Option.TREE_NAVIGATOR );
        treeExplorer.setIcon( IconType.CHECK );
        breadcrumbExplorer.setIcon( null );
    }

    private void activateTechView() {
        options.remove( Option.BUSINESS_CONTENT );
        options.add( Option.TECHNICAL_CONTENT );
        techView.setIcon( IconType.CHECK );
        businessView.setIcon( null );
    }

    private void activateBusinessView() {
        options.add( Option.BUSINESS_CONTENT );
        options.remove( Option.TECHNICAL_CONTENT );
        businessView.setIcon( IconType.CHECK );
        techView.setIcon( null );
    }

    private void enableTagFilter() {
        options.add( Option.SHOW_TAG_FILTER );
        showTagFilter.setIcon( IconType.CHECK );
    }

    private void disableTagFilter() {
        options.remove( Option.SHOW_TAG_FILTER );
        showTagFilter.setIcon( null );
    }

    private void includeHiddenItems() {
        options.add( Option.INCLUDE_HIDDEN_ITEMS );
        options.remove( Option.EXCLUDE_HIDDEN_ITEMS );
    }

    private void excludeHiddenItems() {
        options.remove( Option.INCLUDE_HIDDEN_ITEMS );
        options.add( Option.EXCLUDE_HIDDEN_ITEMS );
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
        return CompassPosition.WEST;
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
                        return new BaseMenuCustom() {
                            @Override
                            public Widget build() {
                                projectScreenMenuItem = new Button() {{
                                    setSize( ButtonSize.SMALL );
                                    setText( ProjectExplorerConstants.INSTANCE.openProjectEditor() );
                                    addClickHandler( new ClickHandler() {
                                        @Override
                                        public void onClick( ClickEvent event ) {
                                            placeManager.goTo( "projectScreen" );
                                        }
                                    } );
                                }};

                                return projectScreenMenuItem;
                            }

                            @Override
                            public boolean isEnabled() {
                                return projectScreenMenuItem.isEnabled();
                            }

                        };
                    }
                } )
                .endMenu()
                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom() {
                            @Override
                            public Widget build() {
                                return new ButtonGroup() {{
                                    add( new Button() {{
                                        setToggleCaret( false );
                                        setDataToggle( Toggle.DROPDOWN );
                                        setIcon( IconType.COG );
                                        setSize( ButtonSize.SMALL );
                                        setTitle( ProjectExplorerConstants.INSTANCE.customizeView() );
                                    }} );
                                    add( new DropDownMenu() {{
                                        addStyleName( "pull-right" );
                                        add( businessView );
                                        add( techView );
                                        add( new Divider() );
                                        add( breadcrumbExplorer );
                                        add( treeExplorer );
                                        add( new Divider() );
                                        add( showTagFilter );
                                        add( new Divider() );
                                        add( archiveProject );
                                        add( archiveRepository );
                                    }} );
                                }};
                            }
                        };
                    }
                } )
                .endMenu()
                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom() {
                            @Override
                            public Widget build() {
                                return new Button() {{
                                    setIcon( IconType.REFRESH );
                                    setSize( ButtonSize.SMALL );
                                    setTitle( ProjectExplorerConstants.INSTANCE.refresh() );
                                    addClickHandler( new ClickHandler() {
                                        @Override
                                        public void onClick( ClickEvent event ) {
                                            refresh();
                                        }
                                    } );
                                }};
                            }
                        };
                    }
                } )
                .endMenu()
                .build();
    }

    public void onProjectContextChanged( @Observes final ProjectContextChangeEvent event ) {
        projectScreenMenuItem.setEnabled( ( event.getProject() != null ) );
    }

    @Override
    public void selectBusinessView() {
        businessViewPresenter.setVisible( true );
        technicalViewPresenter.setVisible( false );
        if ( initPath == null ) {
            options = businessViewPresenter.getActiveOptions();
            businessViewPresenter.initialiseViewForActiveContext( context );
        } else {
            businessViewPresenter.update( options );
            technicalViewPresenter.update( options );

            businessViewPresenter.initialiseViewForActiveContext( initPath );
            initPath = null;
        }
    }

    @Override
    public void selectTechnicalView() {
        businessViewPresenter.setVisible( false );
        technicalViewPresenter.setVisible( true );
        if ( initPath == null ) {
            options = technicalViewPresenter.getActiveOptions();
            technicalViewPresenter.initialiseViewForActiveContext( context );
        } else {
            businessViewPresenter.update( options );
            technicalViewPresenter.update( options );

            technicalViewPresenter.initialiseViewForActiveContext( initPath );
            initPath = null;
        }
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
