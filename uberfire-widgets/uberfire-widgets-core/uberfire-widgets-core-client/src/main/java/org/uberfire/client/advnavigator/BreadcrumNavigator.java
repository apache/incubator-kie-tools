package org.uberfire.client.advnavigator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.resources.NavigatorResources;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.navigator.DataContent;
import org.uberfire.navigator.FileNavigatorService;
import org.uberfire.navigator.NavigatorContent;
import org.uberfire.workbench.type.DotResourceTypeDefinition;

@Dependent
@Named("FlatNav")
public class BreadcrumNavigator extends Composite implements Navigator {

    @Inject
    private Caller<FileNavigatorService> navigatorService;

    @Inject
    private DotResourceTypeDefinition hiddenTypeDef;

    private final FlowPanel container = new FlowPanel();
    private final FlexTable navigator = new FlexTable() {{
        setStyleName( NavigatorResources.INSTANCE.css().navigator() );
    }};
    private NavigatorOptions options = NavigatorOptions.DEFAULT;
    private ParameterizedCommand<Path> fileActionCommand = null;

    @PostConstruct
    public void init() {
        initWidget( container );
    }

    @Override
    public void loadContent( final Path path ) {
        if ( path != null ) {
            navigatorService.call( new RemoteCallback<NavigatorContent>() {
                @Override
                public void callback( final NavigatorContent response ) {

                    container.clear();
                    navigator.removeAllRows();

                    setupBreadcrumb( response, path );

                    setupUpFolder( response );

                    setupContent( response );

                    container.add( navigator );
                }
            } ).listContent( path );
        }
    }

    private void setupBreadcrumb( final NavigatorContent response,
                                  final Path path ) {
        container.add( new NavigatorBreadcrumbs( NavigatorBreadcrumbs.Mode.SECOND_LEVEL ) {{
            build( response.getRoot(), response.getBreadcrumbs(), path, new ParameterizedCommand<Path>() {
                @Override
                public void execute( final Path path ) {
                    loadContent( path );
                }
            } );
        }} );
    }

    private void setupContent( final NavigatorContent content ) {
        int base = navigator.getRowCount();
        for ( int i = 0; i < content.getContent().size(); i++ ) {
            final DataContent dataContent = content.getContent().get( i );
            if ( dataContent.isDirectory() && options.showDirectories() ) {
                createDirectory( base + i, dataContent );
            } else if ( options.showFiles() ) {
                if ( !options.showHiddenFiles() && !hiddenTypeDef.accept( dataContent.getPath() ) ) {
                    createFile( base + i, dataContent );
                } else if ( options.showHiddenFiles() ) {
                    createFile( base + i, dataContent );
                }
            }
        }
    }

    private void setupUpFolder( final NavigatorContent content ) {
        if ( options.allowUpLink() ) {
            if ( content.getBreadcrumbs().size() > 0 ) {
                createUpFolder( content.getBreadcrumbs().get( content.getBreadcrumbs().size() - 1 ) );
            }
        }
    }

    private void createFile( final int row,
                             final DataContent dataContent ) {
        createElement( row, dataContent, IconType.FILE_ALT, NavigatorResources.INSTANCE.css().navigatoFileIcon(), new Command() {
            @Override
            public void execute() {
                if ( fileActionCommand != null ) {
                    fileActionCommand.execute( dataContent.getPath() );
                }
            }
        } );
    }

    private void createDirectory( final int row,
                                  final DataContent dataContent ) {
        createElement( row, dataContent, IconType.FOLDER_CLOSE, NavigatorResources.INSTANCE.css().navigatorFolderIcon(), new Command() {
            @Override
            public void execute() {
                loadContent( dataContent.getPath() );
            }
        } );
    }

    private void createUpFolder( final Path path ) {
        int col = 0;
        navigator.setText( 0, col, "" );

        navigator.setWidget( 0, ++col, new Anchor( ".." ) {{
            addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    loadContent( path );
                }
            } );
        }} );

        if ( options.showItemAge() ) {
            navigator.setText( 0, ++col, "" );
        }
        if ( options.showItemMessage() ) {
            navigator.setText( 0, ++col, "" );
        }
    }

    private void createElement( final int row,
                                final DataContent dataContent,
                                final IconType iconType,
                                final String style,
                                final Command onClick ) {
        int col = 0;
        navigator.setWidget( row, col, new Icon( iconType ) {{
            addStyleName( style );
        }} );
        navigator.setWidget( row, ++col, new Anchor( dataContent.getPath().getFileName() ) {{
            addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    onClick.execute();
                }
            } );
        }} );

        if ( options.showItemAge() ) {
            navigator.setText( row, ++col, dataContent.getAge() );
        }

        if ( options.showItemMessage() ) {
            final Element messageCol = DOM.createSpan();
            messageCol.addClassName( NavigatorResources.INSTANCE.css().navigatorMessage() );
            {
                final Element message = DOM.createSpan();
                message.addClassName( NavigatorResources.INSTANCE.css().message() );
                message.setInnerText( dataContent.getLastMessage() );
                messageCol.appendChild( message );

                if ( options.showItemLastUpdater() ) {
                    final Element colOpen = DOM.createSpan();
                    colOpen.addClassName( NavigatorResources.INSTANCE.css().message() );
                    colOpen.setInnerText( " [" );
                    messageCol.appendChild( colOpen );

                    final Anchor commiterRef = new Anchor( dataContent.getLastCommiter() );
                    DOM.sinkEvents( commiterRef.getElement(), Event.ONCLICK );
                    DOM.setEventListener( commiterRef.getElement(), new EventListener() {
                        public void onBrowserEvent( Event event ) {
                        }
                    } );
                    messageCol.appendChild( commiterRef.getElement() );

                    final Element colClose = DOM.createSpan();
                    colClose.addClassName( NavigatorResources.INSTANCE.css().message() );
                    colClose.setInnerText( "]" );
                    messageCol.appendChild( colClose );
                }
            }

            navigator.setWidget( row, ++col, new Widget() {{
                setElement( messageCol );
            }} );
        }
    }
}