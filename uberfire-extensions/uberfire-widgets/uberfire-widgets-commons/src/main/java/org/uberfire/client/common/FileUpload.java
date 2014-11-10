package org.uberfire.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.mvp.Command;

public class FileUpload
        extends Composite {

    private final Command command;

    interface FileUploadBinder extends UiBinder<Widget, FileUpload> {

    }

    @UiField
    AnchorElement uploadButton;

    @UiField
    AnchorElement chooseButton;

    @UiField
    InputElement file;

    @UiField
    InputElement fileText;

    private boolean isDisabled = false;

    private static FileUploadBinder uiBinder = GWT.create( FileUploadBinder.class );

    public FileUpload() {
        this(null, false);
    }

    public FileUpload( final Command command ) {
        this( command, true );
    }

    public FileUpload( final Command command,
                       boolean displayUploadButton ) {
        initWidget(uiBinder.createAndBindUi(this));
        this.command = command;
        fileText.setReadOnly( true );

        DOM.sinkEvents( (Element) file.cast(), Event.ONCHANGE );
        DOM.setEventListener( (Element) file.cast(), new EventListener() {
            public void onBrowserEvent( final Event event ) {
                fileText.setValue( file.getValue() );
            }
        } );

        DOM.sinkEvents( (Element) chooseButton.cast(), Event.ONCLICK );
        DOM.setEventListener( (Element) chooseButton.cast(), new EventListener() {
            public void onBrowserEvent( final Event event ) {
                file.click();
            }
        } );

        if ( displayUploadButton ) {
            DOM.sinkEvents( (Element) uploadButton.cast(), Event.ONCLICK );
            DOM.setEventListener( (Element) uploadButton.cast(), new EventListener() {
                public void onBrowserEvent( final Event event ) {
                    if ( isDisabled ) {
                        return;
                    }
                    if ( command != null ) {
                        command.execute();
                    }
                }
            } );
        } else {
            uploadButton.removeFromParent();
            uploadButton = null;
        }
    }

    public void setName( final String name ) {
        file.setName( name );
    }

    public void upload() {
        command.execute();
    }

    public String getFilename() {
        return file.getValue();
    }

    public void setEnabled( boolean b ) {
        if ( uploadButton == null ) {
            return;
        }
        if ( !b ) {
            isDisabled = true;
            uploadButton.addClassName( "disabled" );
        } else {
            isDisabled = false;
            uploadButton.removeClassName( "disabled" );
        }
    }

}
