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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import org.guvnor.common.services.shared.file.upload.FileManagerFields;
import org.guvnor.common.services.shared.file.upload.FileOperation;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;

/**
 * This wraps a file uploader utility
 */
public class AttachmentFileWidget extends Composite {

    private final FormPanel form = new FormPanel();
    private final HorizontalPanel fields = new HorizontalPanel();

    private Command successCallback;
    private Command errorCallback;

    public AttachmentFileWidget() {
        form.setEncoding( FormPanel.ENCODING_MULTIPART );
        form.setMethod( FormPanel.METHOD_POST );

        form.addSubmitCompleteHandler( new FormPanel.SubmitCompleteHandler() {

            @Override
            public void onSubmitComplete( final FormPanel.SubmitCompleteEvent event ) {
                if ( "OK".equalsIgnoreCase( event.getResults() ) ) {
                    executeCallback( successCallback );
                    Window.alert( CommonConstants.INSTANCE.UploadSuccess() );
                } else {
                    executeCallback( errorCallback );
                    Window.alert( CommonConstants.INSTANCE.UploadFailure0( event.getResults() ) );
                }
            }

        } );

        final FileUpload up = new FileUpload();
        up.setName( FileManagerFields.UPLOAD_FIELD_NAME_ATTACH );

        fields.add( up );
        form.add( fields );

        initWidget( form );
    }

    private void executeCallback( final Command callback ) {
        if ( callback == null ) {
            return;
        }
        callback.execute();
    }

    public void submit( final Path context,
                        final String fileName,
                        final String targetUrl,
                        final Command successCallback,
                        final Command errorCallback ) {
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;
        fields.add( getHiddenField( FileManagerFields.FORM_FIELD_PATH,
                                    context.toURI() ) );
        fields.add( getHiddenField( FileManagerFields.FORM_FIELD_NAME,
                                    fileName ) );
        fields.add( getHiddenField( FileManagerFields.FORM_FIELD_OPERATION,
                                    FileOperation.CREATE.toString() ) );
        form.setAction( targetUrl );
        form.submit();
    }

    public void submit( final Path path,
                        final String targetUrl,
                        final Command successCallback,
                        final Command errorCallback ) {
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;
        fields.add( getHiddenField( FileManagerFields.FORM_FIELD_FULL_PATH,
                                    path.toURI() ) );
        fields.add( getHiddenField( FileManagerFields.FORM_FIELD_OPERATION,
                                    FileOperation.UPDATE.toString() ) );
        form.setAction( targetUrl );
        form.submit();
    }

    private TextBox getHiddenField( final String name,
                                    final String value ) {
        final TextBox t = new TextBox();
        t.setName( name );
        t.setText( value );
        t.setVisible( false );
        return t;
    }

}
