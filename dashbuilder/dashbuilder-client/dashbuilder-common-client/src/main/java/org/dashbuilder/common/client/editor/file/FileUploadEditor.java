package org.dashbuilder.common.client.editor.file;

import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.HasEditorErrors;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.editor.LeafAttributeEditor;
import org.dashbuilder.common.client.resources.i18n.DashbuilderCommonConstants;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.events.NotificationEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.List;

import static org.uberfire.workbench.events.NotificationEvent.NotificationType.ERROR;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;

/**
 * <p>Presenter for editor component that wraps a gwt bootstrap file upload component an additionally provides:</p>
 * <ul>
 * <li>Error messages - Show validation error messages.</li>
 * <li>Dashbuilder File Upload Servlet integration - It uses the UF Dashbuilder servlet for uploading files and provides a listener to obtain the uploaded file path.</li>
 * </ul>
 *
 * @since 0.4.0
 */
@Dependent
public class FileUploadEditor implements IsWidget, LeafAttributeEditor<String> {

    public interface View extends UberView<FileUploadEditor> {

        View addHelpContent(final String title, final String content, final Placement placement);

        View setFileUploadName(String name);

        View setFileUploadVisible(boolean visible);

        View setFileLabelText(String text);

        View setFileLabelVisible(boolean visible);

        View setLoadingImageVisible(boolean visible);

        String getFileName();

        View setFormAction(String action);

        View submit();

        View showError(final SafeHtml message);

        View clearError();

        View setAccept(String type);

        View clear();
    }

    public interface FileUploadEditorCallback {

        String getUploadFileName();

        String getUploadFileUrl();
    }

    Event<NotificationEvent> workbenchNotification;
    Event<org.dashbuilder.common.client.event.ValueChangeEvent<String>> valueChangeEvent;
    public View view;

    String value;
    FileUploadEditorCallback callback;

    @Inject
    public FileUploadEditor(final Event<org.dashbuilder.common.client.event.ValueChangeEvent<String>> valueChangeEvent,
                            final Event<NotificationEvent> workbenchNotification,
                            final View view) {
        this.valueChangeEvent = valueChangeEvent;
        this.workbenchNotification = workbenchNotification;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.setLoadingImageVisible(false);
        view.setFileUploadName("fileUpload");
    }

    public void configure(final String fileUploadName, final FileUploadEditorCallback callback) {
        view.setFileUploadName(fileUploadName);
        this.callback = callback;
    }

    public void addHelpContent(final String title, final String content, final Placement placement) {
        view.addHelpContent(title, content, placement);
    }

    @Override
    public void showErrors(List<EditorError> errors) {
        StringBuilder sb = new StringBuilder();
        for (EditorError error : errors) {

            if (error.getEditor() == this) {
                sb.append("\n").append(error.getMessage());
            }
        }

        boolean hasErrors = sb.length() > 0;
        if (!hasErrors) {
            view.clearError();
            return;
        }

        // Show the errors.
        view.showError(new SafeHtmlBuilder().appendEscaped(sb.substring(1)).toSafeHtml());
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void setValue(final String value) {
        this.value = value;
        if ( !isNullOrEmpty( view.getFileName() ) ) {
            view.setFileLabelVisible(false);
        } else if ( !isNullOrEmpty( value ) ) {

            int slash = value.lastIndexOf( "/" ) != -1 ? value.lastIndexOf( "/" ) : value.lastIndexOf( "\\" );

            if ( slash == -1 ) {
                view.setFileLabelText( value );
            } else {
                view.setFileLabelText( value.substring( slash + 1 ) );
            }
            view.setFileLabelVisible(true);
        }
    }

    @Override
    public String getValue() {
        return value;
    }

    String getUploadFileName() {
        assert callback != null;
        return callback.getUploadFileName();
    }


    String getUploadFileUrl() {
        assert callback != null;
        return callback.getUploadFileUrl();
    }

    boolean onSubmit() {
        final String fileName = view.getFileName();
        if ( isNullOrEmpty( fileName ) ) {
            return false;
        } else {
            view.setFileUploadVisible(false);
            view.setLoadingImageVisible(true);
        }
        return true;
    }

    void onSubmitComplete(final String results) {
        view.clearError();
        view.setFileUploadVisible(true);
        view.setLoadingImageVisible(false);
        onValueChanged(view.getFileName());

        // Show wb notification.
        if ( "OK".equalsIgnoreCase( results ) ) {
            workbenchNotification.fire(new NotificationEvent(DashbuilderCommonConstants.INSTANCE.uploadSuccessful(), SUCCESS));
        } else if ( "FAIL".equalsIgnoreCase( results ) ) {
            workbenchNotification.fire(new NotificationEvent(DashbuilderCommonConstants.INSTANCE.uploadFailed(), ERROR));
        } else if ( "FAIL - ALREADY EXISTS".equalsIgnoreCase( results ) ) {
            workbenchNotification.fire(new NotificationEvent(DashbuilderCommonConstants.INSTANCE.uploadFailedAlreadyExists(), ERROR));
        }
    }

    void fileUploadHandler() {
        final String _f = getUploadFileName();
        final String _a = getUploadFileUrl();
        view.setFormAction( _a );
        setValue( _f );
        view.setFileLabelVisible(false);
        view.submit();
    }

    private boolean isNullOrEmpty( final String fileName ) {
        return fileName == null || "".equals( fileName );
    }

    void onValueChanged(final String value) {
        // Check value is not same one as current.
        if (this.value != null && this.value.equals(value)) return;

        // Clear error messages on the view.
        view.clearError();

        // Set the new value.
        String before = this.value;
        this.value = value;

        // Fire the value change event.
        valueChangeEvent.fire(new org.dashbuilder.common.client.event.ValueChangeEvent<String>(this, before, this.value));
    }

    public void setAccept(String type) {
        view.setAccept(type);
    }
}
