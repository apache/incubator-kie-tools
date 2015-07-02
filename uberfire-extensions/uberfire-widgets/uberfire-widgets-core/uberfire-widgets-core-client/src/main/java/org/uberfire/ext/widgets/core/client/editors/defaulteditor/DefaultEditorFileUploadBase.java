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
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.FileUpload;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;
import org.uberfire.mvp.Command;

public abstract class DefaultEditorFileUploadBase
        extends Composite {

    interface DefaultEditorFileUploadBaseBinder
            extends
            UiBinder<Widget, DefaultEditorFileUploadBase> {

    }

    private static DefaultEditorFileUploadBaseBinder uiBinder = GWT.create( DefaultEditorFileUploadBaseBinder.class );

    @UiField
    Form form;

    @UiField( provided = true )
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

        form.addSubmitHandler( new AbstractForm.SubmitHandler() {
            @Override
            public void onSubmit( final AbstractForm.SubmitEvent event ) {
                String fileName = fileUpload.getFilename();
                if ( isNullOrEmpty( fileName ) ) {
                    BusyPopup.close();
                    Window.alert( CoreConstants.INSTANCE.SelectFileToUpload() );
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
                } else if ( "FAIL".equalsIgnoreCase( event.getResults() ) ) {
                    Window.alert( CoreConstants.INSTANCE.UploadFail() );
                }
                BusyPopup.close();
            }
        } );
    }

    private FileUpload createFileUpload( boolean showUpload ) {
        return new FileUpload( new Command() {
            @Override
            public void execute() {
                //BusyPopup.showMessage(CoreConstants.INSTANCE.Uploading());

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

    public void upload() {
        fileUpload.upload();
    }
}
