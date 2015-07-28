

/*
* Copyright 2010 JBoss Inc
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.kie.workbench.common.widgets.client.widget;

import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import org.guvnor.common.services.shared.file.upload.FileManagerFields;
import org.guvnor.common.services.shared.file.upload.FileOperation;
import org.gwtbootstrap3.client.ui.Form;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.FileUpload;

/**
 * This wraps a file uploader utility
 */
public class AttachmentFileWidget extends Composite {

    private final Form form = new Form();
    private FileUpload up;
    private final HorizontalPanel fields = new HorizontalPanel();

    private final TextBox fieldFilePath = getHiddenField( FileManagerFields.FORM_FIELD_PATH,
                                                          "" );
    private final TextBox fieldFileName = getHiddenField( FileManagerFields.FORM_FIELD_NAME,
                                                          "" );
    private final TextBox fieldFileFullPath = getHiddenField( FileManagerFields.FORM_FIELD_FULL_PATH,
                                                              "" );
    private final TextBox fieldFileOperation = getHiddenField( FileManagerFields.FORM_FIELD_OPERATION,
                                                               "" );

    private Command successCallback;
    private Command errorCallback;
    private String[] validFileExtensions;

    private ClickHandler uploadButtonClickHanlder;

    public AttachmentFileWidget() {
        setup( false );
    }

    public AttachmentFileWidget( final String[] validFileExtensions ) {
        setup( false );
        setAccept( validFileExtensions );
    }

    public AttachmentFileWidget( final boolean addFileUpload ) {
        setup( addFileUpload );
    }

    public AttachmentFileWidget( final String[] validFileExtensions,
                                 final boolean addFileUpload ) {
        setup( addFileUpload );
        setAccept( validFileExtensions );
    }

    private void setup( boolean addFileUpload ) {
        up = new FileUpload( new org.uberfire.mvp.Command() {
            @Override
            public void execute() {
                uploadButtonClickHanlder.onClick( null );
            }
        }, addFileUpload );
        up.setName( FileManagerFields.UPLOAD_FIELD_NAME_ATTACH );
        form.setEncoding( FormPanel.ENCODING_MULTIPART );
        form.setMethod( FormPanel.METHOD_POST );

        //See https://code.google.com/p/google-web-toolkit/issues/detail?id=4682
        FormElement.as( form.getElement() ).setAcceptCharset( "UTF-8" );
        final Hidden field = new Hidden();
        field.setName( "utf8char" );
        field.setValue( "\u8482" );
        form.add( field );

        form.addSubmitHandler( new Form.SubmitHandler() {
            @Override
            public void onSubmit( final Form.SubmitEvent event ) {
                final String fileName = up.getFilename();
                if ( fileName == null || "".equals( fileName ) ) {
                    Window.alert( CommonConstants.INSTANCE.UploadSelectAFile() );
                    event.cancel();
                    executeCallback( errorCallback );
                    return;
                }
                if ( validFileExtensions != null && validFileExtensions.length != 0 ) {
                    boolean isValid = false;
                    for ( String extension : validFileExtensions ) {
                        if ( fileName.endsWith( extension ) ) {
                            isValid = true;
                            break;
                        }
                    }
                    if ( !isValid ) {
                        Window.alert( CommonConstants.INSTANCE.UploadFileTypeNotSupported() + "\n\n" + CommonConstants.INSTANCE.UploadFileTypeSupportedExtensions0( makeValidFileExtensionsText() ) );
                        event.cancel();
                        executeCallback( errorCallback );
                        return;
                    }
                }

            }

            private String makeValidFileExtensionsText() {
                final StringBuilder sb = new StringBuilder();
                for ( int i = 0; i < validFileExtensions.length; i++ ) {
                    sb.append( "\"" ).append( validFileExtensions[ i ] ).append( ( ( i < validFileExtensions.length - 1 ? "\", " : "\"" ) ) );
                }
                return sb.toString();
            }

        } );

        form.addSubmitCompleteHandler( new Form.SubmitCompleteHandler() {

            @Override
            public void onSubmitComplete( final Form.SubmitCompleteEvent event ) {
                if ( "OK".equalsIgnoreCase( event.getResults() ) ) {
                    executeCallback( successCallback );
                    Window.alert( CommonConstants.INSTANCE.UploadSuccess() );
                } else {
                    executeCallback( errorCallback );
                    if ( event.getResults().contains( "org.uberfire.java.nio.file.FileAlreadyExistsException" ) ) {
                        Window.alert( org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants.INSTANCE.ExceptionFileAlreadyExists0( fieldFileName.getText() ) );
                        //ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionFileAlreadyExists0( fieldFileName.getText() ) );
                    } else if ( event.getResults().contains( "DecisionTableParseException" ) ) {
                        Window.alert( CommonConstants.INSTANCE.UploadGenericError() );
                        //ErrorPopup.showMessage( CommonConstants.INSTANCE.UploadGenericError() );
                    } else {
                        Window.alert( org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants.INSTANCE.ExceptionGeneric0( event.getResults() ) );
                        //ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( event.getResults() ) );
                    }
                }
                reset();
            }

        } );

        fields.add( up );

        fields.add( fieldFilePath );
        fields.add( fieldFileName );
        fields.add( fieldFileFullPath );
        fields.add( fieldFileOperation );

        form.add( fields );

        initWidget( form );
    }

    private void executeCallback( final Command callback ) {
        if ( callback == null ) {
            return;
        }
        callback.execute();
    }

    public void reset() {
        form.reset();
    }

    public void submit( final Path context,
                        final String fileName,
                        final String targetUrl,
                        final Command successCallback,
                        final Command errorCallback ) {
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;

        fieldFileName.setText( fileName );
        fieldFilePath.setText( context.toURI() );
        fieldFileOperation.setText( FileOperation.CREATE.toString() );
        fieldFileFullPath.setText( "" );

        form.setAction( targetUrl );
        form.submit();
    }

    public void submit( final Path path,
                        final String targetUrl,
                        final Command successCallback,
                        final Command errorCallback ) {
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;

        fieldFileOperation.setText( FileOperation.UPDATE.toString() );
        fieldFileFullPath.setText( path.toURI() );
        fieldFileName.setText( "" );
        fieldFilePath.setText( "" );

        form.setAction( targetUrl );
        form.submit();
    }

    private void setAccept( final String[] validFileExtensions ) {
        this.validFileExtensions = validFileExtensions;
        final InputElement element = up.getElement().cast();
        element.setAccept( makeAcceptString( validFileExtensions ) );
    }

    private String makeAcceptString( final String[] validFileExtensions ) {
        if ( validFileExtensions == null || validFileExtensions.length == 0 ) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for ( String fileExtension : validFileExtensions ) {
            sb.append( fileExtension ).append( "," );
        }
        sb.substring( 0,
                      sb.length() - 1 );
        return sb.toString();
    }

    private TextBox getHiddenField( final String name,
                                    final String value ) {
        final TextBox t = new TextBox();
        t.setName( name );
        t.setText( value );
        t.setVisible( false );
        return t;
    }

    public void addClickHandler( final ClickHandler clickHandler ) {
        this.uploadButtonClickHanlder = clickHandler;
    }

    public void setEnabled( boolean b ) {
        up.setEnabled( b );
    }
}

