package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FileLoggerEditorViewImpl
        extends Composite
        implements FileLoggerEditorView {

    interface Binder
            extends
            UiBinder<Widget, FileLoggerEditorViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    TextBox nameTextBox;

    @UiField
    TextBox fileNameTextBox;

    @UiField
    TextBox intervalTextBox;

    public FileLoggerEditorViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setName(String name) {
        nameTextBox.setText(name);
    }

    @Override
    public void setInterval(int interval) {
        intervalTextBox.setText(Integer.toString(interval));
    }

    @Override
    public void setFile(String file) {
        fileNameTextBox.setText(file);
    }

}
