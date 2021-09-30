package org.dashbuilder.common.client.editor.file;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;

import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.ext.widgets.common.client.common.FileUpload;
import org.uberfire.mvp.Command;

/**
 * <p>The FileUploadEditor view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class FileUploadEditorView extends Composite implements FileUploadEditor.View {

    private static final String STYLE_ERROR = " control-group has-error ";

    interface Binder extends UiBinder<Widget, FileUploadEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    @Editor.Ignore
    FlowPanel mainPanel;

    @UiField
    @Editor.Ignore
    FormPanel formPanel;

    @UiField
    @Editor.Ignore
    Tooltip errorTooltip;

    @UiField(provided = true)
    @Editor.Ignore
    org.uberfire.ext.widgets.common.client.common.FileUpload fileUpload;

    @UiField
    @Editor.Ignore
    org.gwtbootstrap3.client.ui.Label fileLabel;

    @UiField
    org.gwtbootstrap3.client.ui.Icon loadingIcon;

    FileUploadEditor presenter;

    @Override
    public void init(final FileUploadEditor presenter) {
        this.presenter = presenter;
    }

    @UiConstructor
    public FileUploadEditorView() {
        fileUpload = createFileUpload();
        initWidget(Binder.BINDER.createAndBindUi(this));
        initFormPanel();
    }

    private FileUpload createFileUpload() {
        return new FileUpload( new Command() {
            @Override
            public void execute() {
                presenter.fileUploadHandler();
            }
        }, true );
    }

    private void initFormPanel() {
        formPanel.setEncoding( FormPanel.ENCODING_MULTIPART );
        formPanel.setMethod( FormPanel.METHOD_POST );
        formPanel.setWidget( fileUpload );
        formPanel.addSubmitHandler(new FormPanel.SubmitHandler() {
            @Override
            public void onSubmit(final FormPanel.SubmitEvent event) {
                final boolean isFireEvent = presenter.onSubmit();
                if (!isFireEvent) {
                    event.cancel();
                }
            }
        });
        formPanel.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete(final FormPanel.SubmitCompleteEvent event) {
                presenter.onSubmitComplete(event.getResults());
            }
        });
    }

    @Override
    public FileUploadEditor.View addHelpContent(final String title, final String content, final Placement placement) {
        final Tooltip tooltip = new Tooltip(fileUpload);
        tooltip.setContainer("body");
        tooltip.setShowDelayMs(1000);
        tooltip.setPlacement(placement);
        tooltip.setTitle(content);
        formPanel.add(tooltip);
        return this;
    }

    @Override
    public FileUploadEditor.View setFileUploadName(final String name) {
        fileUpload.setName(name);
        return this;
    }

    @Override
    public FileUploadEditor.View setFileUploadVisible(final boolean visible) {
        fileUpload.setVisible(visible);
        return this;
    }

    @Override
    public FileUploadEditor.View setFileLabelText(final String text) {
        fileLabel.setText(text);
        return this;
    }

    @Override
    public FileUploadEditor.View setFileLabelVisible(final boolean visible) {
        fileLabel.setVisible(visible);
        return this;
    }

    @Override
    public FileUploadEditor.View setLoadingImageVisible(final boolean visible) {
        loadingIcon.setVisible(visible);
        return this;
    }

    @Override
    public String getFileName() {
        return fileUpload.getFilename();
    }

    @Override
    public FileUploadEditor.View setFormAction(final String action) {
        formPanel.setAction( action );
        return this;
    }

    @Override
    public FileUploadEditor.View submit() {
        formPanel.submit();
        return this;
    }

    @Override
    public FileUploadEditor.View showError(final SafeHtml message) {
        mainPanel.addStyleName(STYLE_ERROR);
        errorTooltip.setTitle(message.asString());
        return this;
    }

    @Override
    public FileUploadEditor.View clearError() {
        mainPanel.removeStyleName(STYLE_ERROR);
        errorTooltip.setTitle("");
        return this;
    }
    
    @Override
    public FileUploadEditor.View setAccept(String type) {
        fileUpload.setAccept(type);
        return this;
    }

    @Override
    public FileUploadEditor.View clear() {
        fileUpload.clear();
        return this;
    }
}
