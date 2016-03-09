/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.core.client.editors.defaulteditor;

import java.util.Iterator;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.base.form.AbstractForm;
import org.uberfire.ext.widgets.common.client.common.FileUpload;
import org.uberfire.ext.widgets.common.client.common.FileUploadFormEncoder;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;
import org.uberfire.mvp.Command;

public abstract class DefaultEditorFileUploadBase
        extends Composite {

    interface DefaultEditorFileUploadBaseBinder
            extends
            UiBinder<Widget, DefaultEditorFileUploadBase> {

    }

    private static DefaultEditorFileUploadBaseBinder uiBinder = GWT.create( DefaultEditorFileUploadBaseBinder.class );

    private Command successCallback;
    private Command errorCallback;

    private FileUploadFormEncoder formEncoder = new FileUploadFormEncoder();

    @UiField
    Form form;

    @UiField(provided = true)
    FileUpload fileUpload;

    public DefaultEditorFileUploadBase() {
        this( true );
    }

    public DefaultEditorFileUploadBase( boolean showUpload ) {
        fileUpload = createFileUpload( showUpload );

        initWidget( uiBinder.createAndBindUi( this ) );

        initForm();
    }

    void initForm() {
        form.setEncoding( FormPanel.ENCODING_MULTIPART );
        form.setMethod( FormPanel.METHOD_POST );

        formEncoder.addUtf8Charset( form );

        // Validation is not performed in a SubmitHandler as it fails to be invoked with GWT-Bootstrap3. See:-
        // - https://issues.jboss.org/browse/GUVNOR-2302 and
        // - the underlying cause https://github.com/gwtbootstrap3/gwtbootstrap3/issues/375
        // Validation is now performed prior to the form being submitted.

        form.addSubmitCompleteHandler( new AbstractForm.SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete( final AbstractForm.SubmitCompleteEvent event ) {
                if ( "OK".equalsIgnoreCase( event.getResults() ) ) {
                    Window.alert( CoreConstants.INSTANCE.UploadSuccess() );
                    executeCallback( successCallback );

                } else if ( "FAIL".equalsIgnoreCase( event.getResults() ) ) {
                    Window.alert( CoreConstants.INSTANCE.UploadFail() );
                    executeCallback( errorCallback );
                }
            }
        } );
    }

    private FileUpload createFileUpload( boolean showUpload ) {
        return new FileUpload( new Command() {
            @Override
            public void execute() {
                form.setAction( GWT.getModuleBaseURL() + "defaulteditor/upload" + createParametersForURL() );
                if ( isValid() ) {
                    form.submit();
                }
            }

        }, showUpload );
    }

    //Package protected to support overriding for tests
    boolean isValid() {
        String fileName = fileUpload.getFilename();
        if ( isNullOrEmpty( fileName ) ) {
            Window.alert( CoreConstants.INSTANCE.SelectFileToUpload() );
            executeCallback( errorCallback );
            return false;
        }
        return true;
    }

    private boolean isNullOrEmpty( String fileName ) {
        return fileName == null || "".equals( fileName );
    }

    private String createParametersForURL() {
        String parameters = "?";
        Map<String, String> map = getParameters();
        Iterator<String> iterator = map.keySet().iterator();
        while ( iterator.hasNext() ) {
            String parameter = iterator.next();
            parameters += parameter + "=" + map.get( parameter );
            if ( iterator.hasNext() ) {
                parameters += "&";
            }
        }
        return parameters;
    }

    protected abstract Map<String, String> getParameters();

    public void upload( final Command successCallback,
                        final Command errorCallback ) {
        this.successCallback = successCallback;
        this.errorCallback = errorCallback;
        fileUpload.upload();
    }

    public String getFormFileName() {
        return fileUpload.getFilename();
    }

    private void executeCallback( final Command callback ) {
        if ( callback == null ) {
            return;
        }
        callback.execute();
    }

}
