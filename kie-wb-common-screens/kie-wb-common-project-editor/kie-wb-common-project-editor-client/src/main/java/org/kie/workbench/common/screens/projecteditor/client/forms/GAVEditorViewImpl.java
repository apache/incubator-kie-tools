package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.dom.client.Style;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class GAVEditorViewImpl
        extends Composite
        implements GAVEditorView {

    interface Binder
            extends UiBinder<Widget, GAVEditorViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    TextBox groupIdTextBox;

    @UiField
    Icon gavHelpIcon;

    @UiField
    Icon gavHelpIcon2;

    @UiField
    Icon gavHelpIcon3;

    @UiField
    TextBox artifactIdTextBox;

    @UiField
    TextBox versionIdTextBox;

    private Presenter presenter;

    public GAVEditorViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));

        gavHelpIcon.getElement().getStyle().setPaddingLeft( 10, Style.Unit.PX );
        gavHelpIcon.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        gavHelpIcon2.getElement().getStyle().setPaddingLeft( 10, Style.Unit.PX );
        gavHelpIcon2.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        gavHelpIcon3.getElement().getStyle().setPaddingLeft( 10, Style.Unit.PX );
        gavHelpIcon3.getElement().getStyle().setCursor(Style.Cursor.POINTER);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setGroupId(String id) {
        groupIdTextBox.setText(id);
    }

    @UiHandler("groupIdTextBox")
    public void onGroupIdChange(KeyUpEvent event) {
        if(checkIsInValid(groupIdTextBox.getText())) {
            Window.alert(ProjectEditorResources.CONSTANTS.XMLMarkIsNotAllowed());
            return;
        }
        presenter.onGroupIdChange(groupIdTextBox.getText());
    }

    @Override
    public void setArtifactId(String id) {
        artifactIdTextBox.setText(id);
    }

    @Override
    public void setReadOnly() {
        groupIdTextBox.setReadOnly(true);
        artifactIdTextBox.setReadOnly(true);
        versionIdTextBox.setReadOnly(true);
    }

    @UiHandler("artifactIdTextBox")
    public void onArtifactIdChange(KeyUpEvent event) {
        if(checkIsInValid(artifactIdTextBox.getText())) {
            Window.alert(ProjectEditorResources.CONSTANTS.XMLMarkIsNotAllowed());
            return;
        }
        presenter.onArtifactIdChange(artifactIdTextBox.getText());
    }

    @Override
    public void setVersionId(String versionId) {
        versionIdTextBox.setText(versionId);
    }

    @UiHandler("versionIdTextBox")
    public void onVersionIdChange(KeyUpEvent event) {
        if(checkIsInValid(versionIdTextBox.getText())) {
            Window.alert(ProjectEditorResources.CONSTANTS.XMLMarkIsNotAllowed());
            return;
        }
        presenter.onVersionIdChange(versionIdTextBox.getText());
    }
    
    boolean checkIsInValid(String content) {
        if(content !=null && (content.contains("<") || content.contains(">") || content.contains("&"))) {
            return true;
        }
        
        return false;
    }
}
