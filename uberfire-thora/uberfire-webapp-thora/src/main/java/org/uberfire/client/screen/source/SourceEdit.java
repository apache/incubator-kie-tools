package org.uberfire.client.screen.source;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
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
import org.uberfire.client.widgets.ace.AceEditor;
import org.uberfire.client.widgets.ace.AceEditorCallback;
import org.uberfire.client.widgets.ace.AceEditorMode;
import org.uberfire.client.widgets.gravatar.GravatarImage;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.shared.source.SourceContent;
import org.uberfire.shared.source.SourceService;

import static org.uberfire.client.screen.source.SourceBreadcrumbUtil.*;
import static org.uberfire.client.widgets.ace.AceEditorTheme.*;

@WorkbenchScreen(identifier = "SourceEdit")
@Templated("source-edit.html")
public class SourceEdit extends Composite {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<SourceService> sourceService;

    @Inject
    private Identity identity;

    private String repo;

    private final AceEditor editor;

    @DataField
    Element userName = DOM.createSpan();
    @DataField
    Element userEmail = DOM.createSpan();

    @DataField
    @Inject
    TextBox commitSummaryBox;

    @DataField
    @Inject
    Image userImage;

    @DataField
    @Inject
    TextArea commitDescriptionBox;

    @DataField
    @Inject
    Button cancelButton;

    @DataField
    @Inject
    Button commitButton;

    @DataField
    @Inject
    Button previewButton;

    @DataField
    private UListElement breadcrumb = Document.get().createULElement();

    @DataField
    private Element editArea = DOM.createDiv();

    @DataField
    @Inject
    TextBox fileNameBox;

    private PathPlaceRequest placeRequest;
    private String previewPlace = "";

    @Inject
    Event<EditorTextContentChanged> event;

    private String fileName;

    public SourceEdit() {
        this.editor = new AceEditor( editArea );
        editor.setWidth( "99.9%" );
        editor.setHeight( "299px" );

        editArea.appendChild( editor.asWidget().getElement() );

        editor.setTheme( CHROME );
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        this.placeRequest = (PathPlaceRequest) placeRequest;
        this.repo = placeRequest.getParameter( "repo", "" );
        this.fileName = placeRequest.getParameter( "file_name", "" );
        this.previewButton.setVisible( false );

        final String _email = identity.getProperty( "email", "--" );
        userImage.setUrl( new GravatarImage( _email, 40 ).getUrl() );
        userEmail.setInnerText( _email );

        final Anchor commiterRef = new Anchor( identity.getName() );
        DOM.sinkEvents( commiterRef.getElement(), com.google.gwt.user.client.Event.ONCLICK );
        DOM.setEventListener( commiterRef.getElement(), new EventListener() {
            public void onBrowserEvent( com.google.gwt.user.client.Event event ) {
                placeManager.goTo( new DefaultPlaceRequest( "UserProfile" ).addParameter( "user_name", identity.getName() ) );
            }
        } );
        userName.appendChild( commiterRef.getElement() );

        sourceService.call( new RemoteCallback<SourceContent>() {
            @Override
            public void callback( SourceContent response ) {
                breadcrumb.appendChild( repo( repo ) );

                fileNameBox.removeFromParent();

                if ( response.getContent() == null ) {
                    for ( int i = 0; i < response.getBreadcrumb().size(); i++ ) {
                        breadcrumb.appendChild( directory( response.getBreadcrumb().get( i ) ) );
                    }
                    breadcrumb.appendChild( fileName( "", fileNameBox ) );
                } else {
                    for ( int i = 0; i < response.getBreadcrumb().size() - 1; i++ ) {
                        breadcrumb.appendChild( directory( response.getBreadcrumb().get( i ) ) );
                    }
                    breadcrumb.appendChild( fileName( fileName, fileNameBox ) );
                }

                if ( fileName != null && !fileName.isEmpty() ) {

                    if ( fileName.endsWith( ".java" ) ) {
                        editor.setMode( AceEditorMode.JAVA );
                    } else if ( fileName.endsWith( ".xml" ) ) {
                        editor.setMode( AceEditorMode.XML );
                    } else if ( fileName.endsWith( ".htm" ) || fileName.endsWith( ".html" ) ) {
                        editor.setMode( AceEditorMode.HTML );
                        previewButton.setVisible( true );
                        previewPlace = "HTMLPreview";
                    } else if ( fileName.endsWith( ".md" ) ) {
                        editor.setMode( AceEditorMode.MARKDOWN );
                        previewButton.setVisible( true );
                        previewPlace = "MDPreview";
                    } else if ( fileName.endsWith( ".asciidoc" ) ) {
                        editor.setMode( AceEditorMode.ASCIIDOC );
                    } else if ( fileName.endsWith( ".js" ) ) {
                        editor.setMode( AceEditorMode.JAVASCRIPT );
                    } else if ( fileName.endsWith( ".css" ) ) {
                        editor.setMode( AceEditorMode.CSS );
                    } else if ( fileName.endsWith( ".yaml" ) ) {
                        editor.setMode( AceEditorMode.YAML );
                    } else if ( fileName.endsWith( ".rb" ) ) {
                        editor.setMode( AceEditorMode.RUBY );
                    } else if ( fileName.endsWith( ".go" ) ) {
                        editor.setMode( AceEditorMode.GOLANG );
                    } else if ( fileName.endsWith( ".sql" ) ) {
                        editor.setMode( AceEditorMode.SQL );
                    } else if ( fileName.endsWith( ".jsp" ) ) {
                        editor.setMode( AceEditorMode.JSP );
                    } else if ( fileName.endsWith( ".py" ) ) {
                        editor.setMode( AceEditorMode.PYTHON );
                    } else if ( fileName.endsWith( ".php" ) ) {
                        editor.setMode( AceEditorMode.PHP );
                    } else if ( fileName.endsWith( ".sh" ) ) {
                        editor.setMode( AceEditorMode.SH );
                    } else if ( fileName.endsWith( ".scss" ) ) {
                        editor.setMode( AceEditorMode.SCSS );
                    }
                    editor.setText( response.getContent() );
                }

                editor.addOnChangeHandler( new AceEditorCallback() {
                    @Override
                    public void invokeAceCallback( JavaScriptObject obj ) {
                        event.fire( new EditorTextContentChanged( editor.getText() ) );
                    }
                } );

            }
        } ).getContent( path(), fileName );

        if ( fileName == null || fileName.isEmpty() ) {
            commitSummaryBox.getElement().setAttribute( "placeHolder", "New file..." );
        } else {
            commitSummaryBox.getElement().setAttribute( "placeHolder", "Update " + fileName );
        }
    }

    private Path path() {
        return this.placeRequest.getPath();
    }

    @EventHandler("commitButton")
    public void commitButton( final ClickEvent e ) {
        sourceService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void o ) {
                Window.alert( "File Saved." );
                placeManager.closePlace( placeRequest );
            }
        } ).commit( repo, path(), getFileName(), editor.getText(), identity.getName(), identity.getProperty( "email", null ), commitSummaryBox.getText(), commitDescriptionBox.getText() );
    }

    private String getFileName() {
        return fileNameBox.getText();
    }

    @EventHandler("cancelButton")
    public void cancelButton( final ClickEvent e ) {
        placeManager.closePlace( placeRequest );
        placeManager.goTo( new PathPlaceRequest( path(), "SourceViewer" ).addParameter( "repo", repo ) );
    }

    @EventHandler("previewButton")
    public void previewButton( final ClickEvent e ) {
        placeManager.goTo( new PathPlaceRequest( path(), previewPlace ).addParameter( "content", editor.getText() ) );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Source Edit";
    }
}
