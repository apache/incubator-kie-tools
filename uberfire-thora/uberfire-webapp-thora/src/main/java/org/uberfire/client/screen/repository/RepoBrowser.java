package org.uberfire.client.screen.repository;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.shared.browser.FileContent;
import org.uberfire.shared.browser.RepositoryBrowser;
import org.uberfire.shared.browser.ResultListContent;
import org.uberfire.shared.repository.RepositoryAppService;
import org.uberfire.shared.repository.RepositoryInfo;

@WorkbenchScreen(identifier = "RepoBrowser")
@Templated("repo-browser.html")
public class RepoBrowser extends Composite {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<RepositoryAppService> repositoryAppService;

    @Inject
    private Caller<RepositoryService> repositoryService;

    @Inject
    private Caller<RepositoryBrowser> repositoryBrowser;

    @DataField
    @Inject
    private Button myGitCopyButton;

    @DataField
    private Element ownerReference = DOM.createSpan();

    @DataField
    private Element repoName = DOM.createSpan();

    @DataField
    private Element repoDesc = DOM.createSpan();

    @DataField
    private Element gitDaemonURI = DOM.createSpan();

    @DataField
    private UListElement breadcrumb = Document.get().createULElement();

    @DataField
    private Element treeBrowser = DOM.createTable();

    @DataField
    private Element treeEntries = DOM.createTBody();

    @DataField
    private Element breadcrumbArea = DOM.createDiv();

    private Path root;

    private String repo;

    @PostConstruct
    private void setup() {
        glueCopy( myGitCopyButton.getElement() );
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        this.repo = placeRequest.getParameter( "repo", null );
        repositoryAppService.call( new RemoteCallback<RepositoryInfo>() {
            @Override
            public void callback( RepositoryInfo response ) {
                ownerReference.appendChild( newOwnerRef( response.getOwner() ) );
                repoName.appendChild( resetRepo( response.getName() ) );
                repoDesc.setInnerText( response.getDescription() );
                gitDaemonURI.setInnerText( response.getUri() );
                root = response.getRoot();

                loadContent( root, true );
            }
        } ).getRepositoryInfo( repo );
    }

    private void loadContent( final Path path,
                              final boolean isRoot ) {

        repositoryBrowser.call( new RemoteCallback<ResultListContent>() {
            @Override
            public void callback( ResultListContent response ) {

                breadcrumb.removeFromParent();
                breadcrumb = Document.get().createULElement();
                breadcrumb.addClassName( "inline" );
                breadcrumb.addClassName( "breadcrumb" );

                //repo
                final Element repoStrong = DOM.createElement( "strong" );
                repoStrong.appendChild( resetRepo( repo ) );
                breadcrumb.appendChild( createBreadCrumbLI( repoStrong ) );

                for ( final Path path : response.getBreadcrumbs() ) {
                    final Anchor newAnchor = new Anchor( path.getFileName() );
                    DOM.sinkEvents( newAnchor.getElement(), Event.ONCLICK );
                    DOM.setEventListener( newAnchor.getElement(), new EventListener() {
                        public void onBrowserEvent( Event event ) {
                            loadContent( path, false );
                        }
                    } );
                    breadcrumb.appendChild( createBreadCrumbLI( newAnchor.getElement() ) );
                }

                if ( !isRoot ) {
                    final Element strong = DOM.createElement( "strong" );
                    strong.setInnerText( path.getFileName() );
                    breadcrumb.appendChild( createBreadCrumbLI( strong ) );
                }

                final Anchor newAnchor = new Anchor();
                final Element icon = DOM.createElement( "i" );
                icon.addClassName( "icon-plus" );
                newAnchor.getElement().appendChild( icon );
                DOM.sinkEvents( newAnchor.getElement(), Event.ONCLICK );
                DOM.setEventListener( newAnchor.getElement(), new EventListener() {
                    public void onBrowserEvent( Event event ) {
                        placeManager.goTo( new PathPlaceRequest( path, "SourceEdit" ).addParameter( "repo", repo ) );
                    }
                } );
                breadcrumb.appendChild( createBreadCrumbLI( newAnchor.getElement() ) );

                breadcrumbArea.appendChild( breadcrumb );

                treeEntries.removeFromParent();
                treeEntries = DOM.createTBody();

                if ( !isRoot ) {
                    if ( response.getBreadcrumbs().size() == 0 ) {
                        treeEntries.appendChild( createUpFolder( root, true ) );
                    } else {
                        treeEntries.appendChild( createUpFolder( response.getBreadcrumbs().get( response.getBreadcrumbs().size() - 1 ), false ) );
                    }
                }

                for ( FileContent fileContent : response.getFiles() ) {
                    if ( fileContent.isDirectory() ) {
                        treeEntries.appendChild( createFolder( fileContent ) );
                    } else {
                        treeEntries.appendChild( createFile( fileContent ) );
                    }
                }
                treeBrowser.appendChild( treeEntries );
            }
        } ).listContent( path );
    }

    private Node createBreadCrumbLI( final Element enclosedElement ) {
        final LIElement liElement = Document.get().createLIElement();
        liElement.getStyle().setPadding( 0, Style.Unit.PX );
        liElement.getStyle().setMargin( 0, Style.Unit.PX );
        liElement.appendChild( enclosedElement );
        final Element divider = DOM.createSpan();
        divider.addClassName( "divider" );
        divider.setInnerText( "/" );
        liElement.appendChild( divider );
        return liElement;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Repository Browser";
    }

    private Element newOwnerRef( final String owner ) {
        final Anchor label = new Anchor( owner );
        DOM.sinkEvents( label.getElement(), Event.ONCLICK );
        DOM.setEventListener( label.getElement(), new EventListener() {
            public void onBrowserEvent( Event event ) {
                placeManager.goTo( new DefaultPlaceRequest( "UserProfile" ).addParameter( "user_name", owner ) );
            }
        } );

        return label.getElement();
    }

    private Element resetRepo( final String repoName ) {
        final Anchor label = new Anchor( repoName );
        DOM.sinkEvents( label.getElement(), Event.ONCLICK );
        DOM.setEventListener( label.getElement(), new EventListener() {
            public void onBrowserEvent( Event event ) {
                loadContent( root, true );
            }
        } );

        return label.getElement();
    }

    private Element createUpFolder( final Path parent,
                                    final boolean isRoot ) {
        final Element tr = DOM.createTR();
        {
            final Element iconCol = DOM.createTD();
            tr.appendChild( iconCol );
        }

        {
            final Element contentCol = DOM.createTD();
            {
                contentCol.addClassName( "content" );

                final Anchor directory = new Anchor( ".." );
                DOM.sinkEvents( directory.getElement(), Event.ONCLICK );
                DOM.setEventListener( directory.getElement(), new EventListener() {
                    public void onBrowserEvent( Event event ) {
                        loadContent( parent, isRoot );
                    }
                } );
                contentCol.appendChild( directory.getElement() );
            }
            tr.appendChild( contentCol );
        }

        {
            final Element ageCol = DOM.createTD();
            tr.appendChild( ageCol );
        }

        {
            final Element messageCol = DOM.createTD();
            tr.appendChild( messageCol );
        }

        return tr;
    }

    private Element createFolder( final FileContent fileContent ) {
        final Element tr = DOM.createTR();
        {
            final Element iconCol = DOM.createTD();
            {
                iconCol.addClassName( "icon" );
                final Element icon = DOM.createElement( "i" );
                icon.addClassName( "icon-folder-close" );
                iconCol.appendChild( icon );
            }
            tr.appendChild( iconCol );
        }

        {
            final Element contentCol = DOM.createTD();
            {
                contentCol.addClassName( "content" );

                final Anchor directory = new Anchor( fileContent.getPath().getFileName() );
                DOM.sinkEvents( directory.getElement(), Event.ONCLICK );
                DOM.setEventListener( directory.getElement(), new EventListener() {
                    public void onBrowserEvent( Event event ) {
                        loadContent( fileContent.getPath(), false );
                    }
                } );
                contentCol.appendChild( directory.getElement() );
            }
            tr.appendChild( contentCol );
        }

        {
            final Element ageCol = DOM.createTD();
            {
                ageCol.addClassName( "age" );
                ageCol.setInnerText( fileContent.getAge() );
            }
            tr.appendChild( ageCol );
        }

        {
            final Element messageCol = DOM.createTD();
            {
                messageCol.addClassName( "message" );

                final Element message = DOM.createSpan();
                message.addClassName( "message" );
                message.getStyle().setColor( "#4E575B" );
                message.setInnerText( fileContent.getLastMessage() );
                messageCol.appendChild( message );

                final Element colOpen = DOM.createSpan();
                colOpen.addClassName( "message" );
                colOpen.getStyle().setColor( "#4E575B" );
                colOpen.setInnerText( " [" );
                messageCol.appendChild( colOpen );

                final Anchor commiterRef = new Anchor( fileContent.getLastCommiter() );
                DOM.sinkEvents( commiterRef.getElement(), Event.ONCLICK );
                DOM.setEventListener( commiterRef.getElement(), new EventListener() {
                    public void onBrowserEvent( Event event ) {
                        placeManager.goTo( new DefaultPlaceRequest( "UserProfile" ).addParameter( "user_email", fileContent.getLastCommiterEmail() ).addParameter( "user_name", fileContent.getLastCommiter() ) );
                    }
                } );
                messageCol.appendChild( commiterRef.getElement() );

                final Element colClose = DOM.createSpan();
                colClose.addClassName( "message" );
                colClose.getStyle().setColor( "#4E575B" );
                colClose.setInnerText( "]" );
                messageCol.appendChild( colClose );
            }
            tr.appendChild( messageCol );
        }

        return tr;
    }

    private Element createFile( final FileContent fileContent ) {
        final Element tr = DOM.createTR();
        {
            final Element iconCol = DOM.createTD();
            {
                iconCol.addClassName( "icon" );
                final Element icon = DOM.createElement( "i" );
                icon.addClassName( "icon-file-alt" );
                iconCol.appendChild( icon );
            }
            tr.appendChild( iconCol );
        }

        {
            final Element contentCol = DOM.createTD();
            {
                contentCol.addClassName( "content" );

                final Anchor directory = new Anchor( fileContent.getPath().getFileName() );
                DOM.sinkEvents( directory.getElement(), Event.ONCLICK );
                DOM.setEventListener( directory.getElement(), new EventListener() {
                    public void onBrowserEvent( Event event ) {
                        placeManager.goTo( new PathPlaceRequest( fileContent.getPath(), "SourceViewer" ).addParameter( "repo", repo ) );
                    }
                } );
                contentCol.appendChild( directory.getElement() );
            }
            tr.appendChild( contentCol );
        }

        {
            final Element ageCol = DOM.createTD();
            {
                ageCol.addClassName( "age" );
                ageCol.setInnerText( fileContent.getAge() );
            }
            tr.appendChild( ageCol );
        }

        {
            final Element messageCol = DOM.createTD();
            {
                messageCol.addClassName( "message" );

                final Element message = DOM.createSpan();
                message.addClassName( "message" );
                message.getStyle().setColor( "#4E575B" );
                message.setInnerText( fileContent.getLastMessage() );
                messageCol.appendChild( message );

                final Element colOpen = DOM.createSpan();
                colOpen.addClassName( "message" );
                colOpen.getStyle().setColor( "#4E575B" );
                colOpen.setInnerText( " [" );
                messageCol.appendChild( colOpen );

                final Anchor commiterRef = new Anchor( fileContent.getLastCommiter() );
                DOM.sinkEvents( commiterRef.getElement(), Event.ONCLICK );
                DOM.setEventListener( commiterRef.getElement(), new EventListener() {
                    public void onBrowserEvent( Event event ) {
                        placeManager.goTo( new DefaultPlaceRequest( "UserProfile" ).addParameter( "user_email", fileContent.getLastCommiterEmail() ).addParameter( "user_name", fileContent.getLastCommiter() ) );
                    }
                } );
                messageCol.appendChild( commiterRef.getElement() );

                final Element colClose = DOM.createSpan();
                colClose.addClassName( "message" );
                colClose.getStyle().setColor( "#4E575B" );
                colClose.setInnerText( "]" );
                messageCol.appendChild( colClose );
            }
            tr.appendChild( messageCol );
        }

        return tr;
    }

    public static native void glueCopy( final Element element ) /*-{
        var clip = new $wnd.ZeroClipboard(element);
    }-*/;
}
