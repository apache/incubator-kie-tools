package org.uberfire.client.editors.defaulteditor;

import javax.annotation.PostConstruct;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import org.uberfire.backend.vfs.Path;

public class DefaultFileEditorView
        extends Composite
        implements DefaultFileEditorPresenter.View {

    interface DefaultFileEditorViewBinder
            extends
            UiBinder<ResizeLayoutPanel, DefaultFileEditorView> {

    }

    private static DefaultFileEditorViewBinder uiBinder = GWT.create(DefaultFileEditorViewBinder.class);

    @UiField
    DefaultEditorFileUpload fileUpload;

    @UiField
    Button downloadButton;

    @PostConstruct
    public void init() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setPath(Path path) {
        fileUpload.setPath(path);
    }

    @UiHandler("downloadButton")
    public void handleClick(ClickEvent event) {
        fileUpload.download();
    }

}
