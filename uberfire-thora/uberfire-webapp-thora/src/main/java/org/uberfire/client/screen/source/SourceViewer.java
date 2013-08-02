package org.uberfire.client.screen.source;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.backend.vfs.Path;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.shared.source.PathContentUpdated;
import org.uberfire.shared.source.SourceLinedContent;
import org.uberfire.shared.source.SourceService;

import static org.uberfire.client.screen.source.SourceBreadcrumbUtil.*;

@WorkbenchScreen(identifier = "SourceViewer")
@Templated("source-viewer.html")
public class SourceViewer extends Composite {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<SourceService> sourceService;

    private Path path;
    private String repo;
    private Path dirPath;

    @DataField
    private UListElement breadcrumb = Document.get().createULElement();

    @DataField
    private Element lineNumbers = DOM.createTD();

    @DataField
    private Element sourceCode = DOM.createTD();

    @DataField
    private Element sourceContent = DOM.createTR();

    @DataField
    @Inject
    private Button editButton;

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        this.path = ( (PathPlaceRequest) placeRequest ).getPath();
        this.repo = placeRequest.getParameter( "repo", "" );

        loadContent( true );
    }

    private void loadContent( final boolean loadBreadcrumb ) {
        sourceService.call( new RemoteCallback<SourceLinedContent>() {
            @Override
            public void callback( SourceLinedContent response ) {
                if ( loadBreadcrumb ) {
                    breadcrumb.appendChild( repo( repo ) );
                    for ( int i = 0; i < response.getBreadcrumb().size(); i++ ) {
                        if ( i == response.getBreadcrumb().size() - 1 ) {
                            breadcrumb.appendChild( fileName( response.getBreadcrumb().get( i ), null ) );
                        } else {
                            breadcrumb.appendChild( directory( response.getBreadcrumb().get( i ) ) );
                        }
                    }
                }
                dirPath = response.getDirPath();
                lineNumbers.removeFromParent();
                lineNumbers = DOM.createTD();
                lineNumbers.addClassName( "blob-line-nums" );
                sourceContent.appendChild( lineNumbers );

                sourceCode.removeFromParent();
                sourceCode = DOM.createTD();
                sourceCode.addClassName( "blob-line-code" );

                final Element sourceWrapper = DOM.createDiv();
                sourceWrapper.addClassName( "highlight" );

                for ( int i = 0; i < response.getContent().size(); i++ ) {
                    final String lineContent = response.getContent().get( i );
                    final String lineNum = String.valueOf( i + 1 );
                    final String lineId = "L" + lineNum;

                    {
                        final Element line = DOM.createSpan();
                        line.setId( lineId );
                        line.setInnerText( lineNum );
                        lineNumbers.appendChild( line );
                    }
                    {
                        final Element content = DOM.createDiv();
                        content.setId( lineId );
                        content.addClassName( "line" );
                        boolean endOfSpacesInTheBeggining = false;
                        final SafeHtmlBuilder sb = new SafeHtmlBuilder();
                        for ( char c : lineContent.toCharArray() ) {
                            if ( endOfSpacesInTheBeggining ) {
                                sb.append( c );
                            } else if ( c == ' ' ) {
                                sb.appendHtmlConstant( "&nbsp;" );
                            } else if ( c == '\t' ) {
                                sb.appendHtmlConstant( "&nbsp;&nbsp;" );
                            } else {
                                sb.append( c );
                                endOfSpacesInTheBeggining = true;
                            }
                        }
                        content.setInnerHTML( sb.appendHtmlConstant( "<br/>" ).toSafeHtml().asString() );
                        sourceWrapper.appendChild( content );
                    }
                    sourceCode.appendChild( sourceWrapper );
                    sourceContent.appendChild( sourceCode );
                }
            }
        } ).getLinedContent( path );

    }

    @EventHandler("editButton")
    public void editButton( ClickEvent e ) {
        placeManager.goTo( new PathPlaceRequest( dirPath, "SourceEdit" ).addParameter( "repo", repo ).addParameter( "file_name", path.getFileName() ) );
    }

    private void onPathChange( @Observes PathContentUpdated pathContentUpdated ) {
        if ( pathContentUpdated.getPath().equals( path ) ) {
            loadContent( false );
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Source Viewer: " + path.getFileName();
    }
}
