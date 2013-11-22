package org.uberfire.client.editors.defaulteditor;

import javax.annotation.PostConstruct;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.common.FileUpload;
import org.uberfire.mvp.Command;

public class DefaultFileEditorView
        extends Composite
        implements DefaultFileEditorPresenter.View {

    private Path path;

    interface DefaultFileEditorViewBinder
            extends
            UiBinder<ResizeLayoutPanel, DefaultFileEditorView> {

    }

    private static DefaultFileEditorViewBinder uiBinder = GWT.create(DefaultFileEditorViewBinder.class);

    @UiField(provided = true)
    FileUpload fileUpload;

    @UiField
    WellForm form;

    @UiField
    TextBox pathToFolder;

    @UiField
    Button downloadButton;

    @PostConstruct
    public void init() {
        fileUpload = createFileUpload();

        initWidget(uiBinder.createAndBindUi(this));

        initForm();
    }

    private void initForm() {
        form.setAction(GWT.getModuleBaseURL() + "defaulteditor/upload");
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        form.addSubmitHandler(new Form.SubmitHandler() {
            @Override
            public void onSubmit(final Form.SubmitEvent event) {
                String fileName = fileUpload.getFilename();
                if (isNullOrEmpty(fileName)) {
                    BusyPopup.close();
                    Window.alert("Please select a file to upload");
                    event.cancel();
                }
            }

            private boolean isNullOrEmpty(String fileName) {
                return fileName == null || "".equals(fileName);
            }
        });

        form.addSubmitCompleteHandler(new Form.SubmitCompleteHandler() {
            public void onSubmitComplete(final Form.SubmitCompleteEvent event) {
                if ("OK".equalsIgnoreCase(event.getResults())) {
                    BusyPopup.close();
                    Window.alert("Uploaded successfully");
                }
            }
        });
    }

    private FileUpload createFileUpload() {
        return new FileUpload(new Command() {
            @Override
            public void execute() {
                BusyPopup.showMessage("Uploading");

                pathToFolder.setText(path.toURI());

                form.submit();
            }
        }, true);
    }

    @Override
    public void setPath(Path path) {
        this.path = path;
    }

    @UiHandler("downloadButton")
    public void handleClick(ClickEvent event) {
        Window.open(getFileDownloadURL(),
                "downloading",
                "resizable=no,scrollbars=yes,status=no");
    }

    private String getFileDownloadURL() {
        return GWT.getModuleBaseURL() + "defaulteditor/download?path=" + path.toURI();
    }

}
