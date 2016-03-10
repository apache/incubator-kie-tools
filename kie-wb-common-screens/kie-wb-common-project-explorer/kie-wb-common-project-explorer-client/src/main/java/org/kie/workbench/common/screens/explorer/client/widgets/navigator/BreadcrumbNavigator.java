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
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.structure.client.resources.NavigatorResources;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.IconStack;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.html.Div;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.explorer.client.resources.ProjectExplorerResources;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.widgets.ActiveContextOptions;
import org.kie.workbench.common.screens.explorer.client.widgets.BaseViewPresenter;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemOperation;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.type.DotResourceTypeDefinition;

@Dependent
public class BreadcrumbNavigator extends Composite implements Navigator {

    private User user;
    private DotResourceTypeDefinition hiddenTypeDef;
    private ActiveContextOptions activeOptions;
    private FolderListing activeContent;

    private final FlowPanel container = GWT.create( FlowPanel.class );
    private final FlexTable navigator = GWT.create( FlexTable.class );
    private final PanelBody navigatorPanelBody = GWT.create( PanelBody.class );
    private final Panel navigatorPanel = GWT.create( Panel.class );

    private NavigatorOptions options = new NavigatorOptions();
    private BaseViewPresenter presenter;

    @Inject
    public BreadcrumbNavigator( final ActiveContextOptions activeOptions,
                                final DotResourceTypeDefinition hiddenTypeDef,
                                final User user ) {
        this.activeOptions = activeOptions;
        this.hiddenTypeDef = hiddenTypeDef;
        this.user = user;
    }

    @PostConstruct
    public void init() {
        navigatorPanelBody.add( navigator );
        navigatorPanel.add( navigatorPanelBody );
        navigator.setStyleName( NavigatorResources.INSTANCE.css().navigator() );
        initWidget( container );
    }

    @Override
    public void setOptions( final NavigatorOptions options ) {
        this.options = options;
    }

    @Override
    public void loadContent( final FolderListing content ) {
        loadContent( content,
                     new HashMap<FolderItem, List<FolderItem>>() );
    }

    @Override
    @SuppressWarnings("unused")
    public void loadContent( final FolderListing content,
                             final Map<FolderItem, List<FolderItem>> siblings ) {
        if ( content != null ) {
            if ( content.equals( activeContent ) ) {
                return;
            }
            activeContent = content;

            container.clear();
            navigator.removeAllRows();

            setupBreadcrumb( content );

            setupUpFolder( content );

            setupContent( content );

            container.add( navigatorPanel );

            navigator.getColumnFormatter().setWidth( 0, "15px" );
            navigator.getColumnFormatter().setWidth( 1, "15px" );
        }
    }

    @Override
    public void clear() {

    }

    @Override
    public void setPresenter( final BaseViewPresenter presenter ) {
        this.presenter = presenter;
    }

    //Package protected for unit-testing
    void setupBreadcrumb( final FolderListing content ) {
        final NavigatorBreadcrumbs navigatorBreadcrumbs = new NavigatorBreadcrumbs( NavigatorBreadcrumbs.Mode.SECOND_LEVEL );
        navigatorBreadcrumbs.build( content.getSegments(), content.getItem(), new ParameterizedCommand<FolderItem>() {
            @Override
            public void execute( final FolderItem item ) {
                presenter.onActiveFolderItemSelected( item );
            }
        } );

        final Panel panel = new Panel();
        final PanelBody panelBody = new PanelBody();
        panelBody.getElement().getStyle().setPaddingLeft( 10, Style.Unit.PX );
        panelBody.getElement().getStyle().setPaddingRight( 10, Style.Unit.PX );
        panelBody.add( navigatorBreadcrumbs );
        panel.add( panelBody );
        container.add( panel );
    }

    //Package protected for unit-testing
    void setupContent( final FolderListing content ) {
        final int folderCount = getFolderCount( content.getContent() );
        if ( activeOptions.isTechnicalViewActive() || folderCount > 0 ) {
            showNavigatorPanel();
        } else {
            hideNavigatorPanel();
        }

        int base = navigator.getRowCount();
        for ( int i = 0; i < content.getContent().size(); i++ ) {
            final FolderItem folderItem = content.getContent().get( i );
            if ( folderItem.getType().equals( FolderItemType.FOLDER ) && options.showDirectories() ) {
                createDirectory( base + i, folderItem );
            } else if ( folderItem.getType().equals( FolderItemType.FILE ) && options.showFiles() ) {
                if ( !options.showHiddenFiles() && !hiddenTypeDef.accept( (Path) folderItem.getItem() ) ) {
                    createFile( base + i, folderItem );
                } else if ( options.showHiddenFiles() ) {
                    createFile( base + i, folderItem );
                }
            }
        }
    }

    //Package protected for unit-testing
    void setupUpFolder( final FolderListing content ) {
        if ( options.allowUpLink() ) {
            if ( content.getSegments().size() > 0 ) {
                createUpFolder( content.getSegments().get( content.getSegments().size() - 1 ) );
            }
        }
    }

    private int getFolderCount( final List<FolderItem> content ) {
        if ( content == null || content.size() == 0 ) {
            return 0;
        }
        int folderCount = 0;
        for ( FolderItem fi : content ) {
            if ( fi.getType().equals( FolderItemType.FOLDER ) ) {
                folderCount++;
            }
        }
        return folderCount;
    }

    //Package protected for unit-testing
    void showNavigatorPanel() {
        navigatorPanel.setVisible( true );
    }

    //Package protected for unit-testing
    void hideNavigatorPanel() {
        navigatorPanel.setVisible( false );
    }

    private void createFile( final int row,
                             final FolderItem folderItem ) {
        createElement( row, folderItem, IconType.FILE_O, ProjectExplorerResources.INSTANCE.CSS().navigatoFileIcon(), new Command() {
            @Override
            public void execute() {
                presenter.onItemSelected( folderItem );
            }
        } );
    }

    private void createDirectory( final int row,
                                  final FolderItem folderItem ) {
        createElement( row, folderItem, IconType.FOLDER, ProjectExplorerResources.INSTANCE.CSS().navigatorFolderIcon(), new Command() {
            @Override
            public void execute() {
                presenter.onActiveFolderItemSelected( folderItem );
            }
        } );
    }

    private void createUpFolder( final FolderItem item ) {
        int col = 0;
        navigator.setText( 0, col, "" );
        navigator.setText( 0, ++col, "" );

        final Anchor anchor = new Anchor();
        anchor.setIcon( IconType.LEVEL_UP );
        anchor.setIconSize( IconSize.LARGE );
        anchor.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onActiveFolderItemSelected( item );
            }
        } );
        navigator.setWidget( 0, ++col, anchor );

        navigator.setText( 0, ++col, "" );

        if ( options.showItemAge() ) {
            navigator.setText( 0, ++col, "" );
        }
        if ( options.showItemMessage() ) {
            navigator.setText( 0, ++col, "" );
        }
    }

    private void createElement( final int row,
                                final FolderItem folderItem,
                                final IconType iconType,
                                final String style,
                                final Command onClick ) {

        final Boolean locked = ( folderItem.getLockedBy() != null );
        final Boolean lockOwned = ( locked && folderItem.getLockedBy().equals( user.getIdentifier() ) );
        final Boolean hasLockedItems = folderItem.hasLockedItems();

        int col = 0;
        final Icon icon = new Icon( iconType );
        icon.addStyleName( style );
        navigator.setWidget( row, col, icon );

        col++;
        if ( locked ) {
            final Icon lock = new Icon( IconType.LOCK );
            if ( lockOwned ) {
                lock.getElement().getStyle().setColor( "#0083d0" );
            }

            final Tooltip lockTooltip = new Tooltip( lock );
            lockTooltip.setTitle( ( lockOwned ) ? ProjectExplorerConstants.INSTANCE.lockOwnedHint() :
                                          ProjectExplorerConstants.INSTANCE.lockHint() + " " + folderItem.getLockedBy() );
            lockTooltip.setPlacement( Placement.TOP );
            lockTooltip.setShowDelayMs( 1000 );

            navigator.setWidget( row, col, lockTooltip );
        }

        final Anchor anchor = GWT.create( Anchor.class );
        anchor.setText( folderItem.getFileName().replaceAll( " ", "\u00a0" ) );
        anchor.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                onClick.execute();
            }
        } );
        navigator.setWidget( row, ++col, anchor );

        final Div iconContainer = new Div();
        iconContainer.setPull( Pull.RIGHT );
        iconContainer.addStyleName( ProjectExplorerResources.INSTANCE.CSS().iconContainer() );

        if ( folderItem.canDoOperation( FolderItemOperation.COPY ) ) {
            final Icon copyContainer = new Icon( IconType.COPY );
            copyContainer.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    presenter.copyItem( folderItem );
                }
            } );

            final Tooltip copyTooltip = new Tooltip( copyContainer, CommonConstants.INSTANCE.Copy() );
            copyTooltip.setPlacement( Placement.TOP );
            copyTooltip.setShowDelayMs( 1000 );
            iconContainer.add( copyTooltip );
        }

        if ( folderItem.canDoOperation( FolderItemOperation.RENAME ) ) {
            final Boolean disabledRename = ( locked && !lockOwned ) || hasLockedItems;
            final Widget renameContainer = getRenameIcon( disabledRename );
            renameContainer.addDomHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    if ( !disabledRename ) {
                        presenter.renameItem( folderItem );
                    }
                }
            }, ClickEvent.getType() );

            final Tooltip renameTooltip = new Tooltip( renameContainer, CommonConstants.INSTANCE.Rename() );
            renameTooltip.setPlacement( Placement.TOP );
            renameTooltip.setShowDelayMs( 1000 );
            iconContainer.add( renameTooltip );
        }

        if ( folderItem.canDoOperation( FolderItemOperation.DELETE ) ) {
            final Boolean disabledDelete = ( locked && !lockOwned ) || hasLockedItems;
            final Widget deleteContainer = getDeleteIcon( disabledDelete );
            deleteContainer.addDomHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    if ( !disabledDelete ) {
                        presenter.deleteItem( folderItem );
                    }
                }
            }, ClickEvent.getType() );

            final Tooltip deleteTooltip = new Tooltip( deleteContainer, CommonConstants.INSTANCE.Delete() );
            deleteTooltip.setPlacement( Placement.TOP );
            deleteTooltip.setShowDelayMs( 1000 );
            iconContainer.add( deleteTooltip );
        }

        if ( folderItem.getType().equals( FolderItemType.FOLDER ) && folderItem.canDoOperation( FolderItemOperation.ARCHIVE ) ) {

            final Icon archiveContainer = new Icon( IconType.ARCHIVE );
            archiveContainer.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    presenter.uploadArchivedFolder( folderItem );
                }
            } );

            final Tooltip archiveTooltip = new Tooltip( archiveContainer, CommonConstants.INSTANCE.Archive() );
            archiveTooltip.setPlacement( Placement.TOP );
            archiveTooltip.setShowDelayMs( 1000 );
            iconContainer.add( archiveTooltip );
        }

        navigator.setWidget( row, ++col, iconContainer );
    }

    private Widget getRenameIcon( boolean disabled ) {
        final Icon icon = new Icon( IconType.PENCIL );
        return ( disabled ) ? ban( icon ) : icon;
    }

    private Widget getDeleteIcon( boolean disabled ) {
        final Icon icon = new Icon( IconType.TRASH );
        return ( disabled ) ? ban( icon ) : icon;
    }

    private Widget ban( final Icon icon ) {
        icon.setStackTop( true );
        final Icon ban = new Icon( IconType.BAN );
        final IconStack iconStack = new IconStack();
        iconStack.add( icon, false );
        iconStack.add( ban, true );
        return iconStack;
    }
}