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

        form.addSubmitHandler( new AbstractForm.SubmitHandler() {
            @Override
            public void onSubmit( final AbstractForm.SubmitEvent event ) {
                String fileName = fileUpload.getFilename();
                if ( isNullOrEmpty( fileName ) ) {
                    Window.alert( CoreConstants.INSTANCE.SelectFileToUpload() );
                    executeCallback( errorCallback );
                    event.cancel();
                }
            }

            private boolean isNullOrEmpty( String fileName ) {
                return fileName == null || "".equals( fileName );
            }
        } );

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
                form.submit();
            }
        }, showUpload );
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

    private void executeCallback( final Command callback ) {
        if ( callback == null ) {
            return;
        }
        callback.execute();
    }

}
