package org.kie.workbench.common.screens.explorer.client.widgets.navigator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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
import org.kie.workbench.common.screens.explorer.client.widgets.ViewPresenter;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.resources.NavigatorResources;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.type.DotResourceTypeDefinition;

@Dependent
public class BreadcrumbNavigator extends Composite implements Navigator {

    @Inject
    private DotResourceTypeDefinition hiddenTypeDef;

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
        int col = 0;
        navigator.setWidget( row, col, new Icon( iconType ) {{
            addStyleName( style );
        }} );
        navigator.setWidget( row, ++col, new Anchor( folderItem.getFileName() ) {{
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

        final InlineHTML renameContainer = new InlineHTML( "<i class=\"icon-font\"></i>" );
        renameContainer.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.renameItem( folderItem );
            }
        } );
        renameContainer.getElement().getStyle().setPaddingLeft( 10, Style.Unit.PX );

        final InlineHTML deleteContainer = new InlineHTML( "<i class=\"icon-trash\"></i>" );
        deleteContainer.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.deleteItem( folderItem );
            }
        } );
        deleteContainer.getElement().getStyle().setPaddingLeft( 10, Style.Unit.PX );

        iconContainer.add( copyContainer );
        iconContainer.add( renameContainer );
        iconContainer.add( deleteContainer );

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
}