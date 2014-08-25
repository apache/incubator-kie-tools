package org.kie.workbench.common.screens.defaulteditor.client.editor;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.client.common.BusyPopup;
import org.kie.uberfire.client.editors.texteditor.TextEditorPresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditorTitle;

public class KieTextEditorViewImpl
        extends TextEditorPresenter
        implements KieTextEditorView {

    private KieEditorTitle kieEditorTitle = new KieEditorTitle();

    @Override
    public void showLoading() {
        showBusyIndicator(CommonConstants.INSTANCE.Loading());
    }

    @Override
    public void showSaving() {
        showBusyIndicator(CommonConstants.INSTANCE.Saving());
    }

    @Override
    public void alertReadOnly() {
        Window.alert(CommonConstants.INSTANCE.CantSaveReadOnly());
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public void setNotDirty() {
        super.view.setDirty(false);
    }

    @Override public KieEditorTitle getTitleWidget() {
        return kieEditorTitle;
    }

    @Override public void refreshTitle(String fileName, String description) {
        kieEditorTitle.setText(fileName, description);
    }

    @Override
    public Widget asWidget() {
        return super.getWidget().asWidget();
    }

    @Override
    public void makeReadOnly() {
        super.view.makeReadOnly();
    }

    @Override
    public String getContent() {
        return super.view.getContent();
    }
}
