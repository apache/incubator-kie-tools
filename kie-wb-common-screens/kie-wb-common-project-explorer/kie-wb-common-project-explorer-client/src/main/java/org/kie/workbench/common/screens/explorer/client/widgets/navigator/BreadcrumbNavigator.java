package org.kie.workbench.common.screens.explorer.client.widgets.navigator;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.structure.client.resources.NavigatorResources;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.widgets.ViewPresenter;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.type.DotResourceTypeDefinition;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Tooltip;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;

@Dependent
public class BreadcrumbNavigator extends Composite implements Navigator {

    @Inject
    private DotResourceTypeDefinition hiddenTypeDef;

    @Inject
    private User user;
    
    private FolderListing activeContent;

    private final FlowPanel container = new FlowPanel();
    private final FlexTable navigator = new FlexTable() {{
        setStyleName( NavigatorResources.INSTANCE.css().navigator() );
    }};
    private NavigatorOptions options = new NavigatorOptions();
    private ViewPresenter presenter;

    @PostConstruct
    public void init() {
        initWidget( container );
    }

    @Override
    public void setOptions( final NavigatorOptions options ) {
        this.options = options;
    }

    @Override
    public void loadContent( final FolderListing content,
                             final Map<FolderItem, List<FolderItem>> siblings ) {
        loadContent( content );
    }

    @Override
    public void loadContent( final FolderListing content ) {
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

            container.add( navigator );
        }
    }

    @Override
    public void clear() {

    }

    @Override
    public void setPresenter( final ViewPresenter presenter ) {
        this.presenter = presenter;
    }

    private void setupBreadcrumb( final FolderListing content ) {
        container.add( new NavigatorBreadcrumbs( NavigatorBreadcrumbs.Mode.SECOND_LEVEL ) {{
            build( content.getSegments(), content.getItem(), new ParameterizedCommand<FolderItem>() {
                @Override
                public void execute( final FolderItem item ) {
                    presenter.activeFolderItemSelected( item );
                }
            } );
        }} );
    }

    private void setupContent( final FolderListing content ) {
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

    private void setupUpFolder( final FolderListing content ) {
        if ( options.allowUpLink() ) {
            if ( content.getSegments().size() > 0 ) {
                createUpFolder( content.getSegments().get( content.getSegments().size() - 1 ) );
            }
        }
    }

    private void createFile( final int row,
                             final FolderItem folderItem ) {
        createElement( row, folderItem, IconType.FILE_ALT, NavigatorResources.INSTANCE.css().navigatoFileIcon(), new Command() {
            @Override
            public void execute() {
                presenter.itemSelected( folderItem );
            }
        } );
    }

    private void createDirectory( final int row,
                                  final FolderItem folderItem ) {
        createElement( row, folderItem, IconType.FOLDER_CLOSE, NavigatorResources.INSTANCE.css().navigatorFolderIcon(), new Command() {
            @Override
            public void execute() {
                presenter.activeFolderItemSelected( folderItem );
            }
        } );
    }

    private void createUpFolder( final FolderItem item ) {
        int col = 0;
        navigator.setText( 0, col, "" );
        navigator.setText( 0, ++col, "" );

        navigator.setWidget( 0, ++col, new Anchor( ".." ) {{
            addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    presenter.activeFolderItemSelected( item );
                }
            } );
        }} );

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
        
        final Boolean locked = (folderItem.getLockedBy() != null);
        final Boolean lockOwned = (locked && folderItem.getLockedBy().equals( user.getIdentifier() ));
        
        int col = 0;
        navigator.setWidget( row, col, new Icon( iconType ) {{
            addStyleName( style );
        }} );
        
        col++;
        if ( locked ) {
            final InlineHTML lock = new InlineHTML( "<i class=\"icon-lock\"" + ((lockOwned) ? "style=\"color:#0083d0\"" : "") + "></i>" );
            navigator.setWidget( row,
                                 col,
                                 lock );

            new Tooltip() {
                {
                    setWidget( lock );
                    setText( (lockOwned) ? ProjectExplorerConstants.INSTANCE.lockOwnedHint() :
                            ProjectExplorerConstants.INSTANCE.lockHint() + " " + folderItem.getLockedBy() );
                    setPlacement( Placement.TOP );
                    setShowDelay( 1000 );
                    reconfigure();
                }
            };
        }
        
        navigator.setWidget( row, ++col, new Anchor( folderItem.getFileName().replaceAll( " ", "\u00a0" ) ) {{
            addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    onClick.execute();
                }
            } );
        }} );

        final FlowPanel iconContainer = new FlowPanel();

        final InlineHTML copyContainer = new InlineHTML( "<i class=\"icon-copy\"></i>" );
        copyContainer.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.copyItem( folderItem );
            }
        } );

        final InlineHTML renameContainer = new InlineHTML( getRenameIcon( locked && !lockOwned ) );
        renameContainer.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if (!locked) {
                    presenter.renameItem( folderItem );
                }
            }
        } );
        renameContainer.getElement().getStyle().setPaddingLeft( 10, Style.Unit.PX );

        final InlineHTML deleteContainer = new InlineHTML( getDeleteIcon( locked && !lockOwned ) );
        deleteContainer.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if (!locked) {
                    presenter.deleteItem( folderItem );
                }
            }
        } );
        deleteContainer.getElement().getStyle().setPaddingLeft( 10, Style.Unit.PX );

        iconContainer.add( copyContainer );
        iconContainer.add( renameContainer );
        iconContainer.add( deleteContainer );

        if (folderItem.getType().equals(FolderItemType.FOLDER)) {

            final InlineHTML archiveContainer = new InlineHTML("<i class=\"icon-archive\"></i>");
            archiveContainer.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.uploadArchivedFolder( folderItem );
                }
            });
            archiveContainer.getElement().getStyle().setPaddingLeft(10, Style.Unit.PX);
            iconContainer.add(archiveContainer);
            new Tooltip() {{
                setWidget( archiveContainer );
                setText( CommonConstants.INSTANCE.Archive() );
                setPlacement( Placement.TOP );
                setShowDelay( 1000 );
                reconfigure();
            }};

        }

        new Tooltip() {{
            setWidget( copyContainer );
            setText( CommonConstants.INSTANCE.Copy() );
            setPlacement( Placement.TOP );
            setShowDelay( 1000 );
            reconfigure();
        }};

        new Tooltip() {{
            setWidget( renameContainer );
            setText( CommonConstants.INSTANCE.Rename() );
            setPlacement( Placement.TOP );
            setShowDelay( 1000 );
            reconfigure();
        }};

        new Tooltip() {{
            setWidget( deleteContainer );
            setText( CommonConstants.INSTANCE.Delete() );
            setPlacement( Placement.TOP );
            setShowDelay( 1000 );
            reconfigure();
        }};

        navigator.setWidget( row, ++col, iconContainer );
    }
    
    private String getRenameIcon( boolean allowed ) {
        String icon = "<i class=\"icon-font\"></i>";
        return (allowed) ? ban( icon ) : icon;
    };

    private String getDeleteIcon( boolean allowed ) {
        String icon = "<i class=\"icon-trash\"></i>";
        return (allowed) ? ban( icon ) : icon;
    }
    
    private String ban(String icon) {
        return "<span class=\"icon-stack\">" + icon + 
                "<i class=\"icon-ban-circle icon-stack-base\"></i></span>";
    }
}