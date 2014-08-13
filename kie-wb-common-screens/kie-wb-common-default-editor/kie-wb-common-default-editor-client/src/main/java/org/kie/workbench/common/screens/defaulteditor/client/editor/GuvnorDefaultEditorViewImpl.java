package org.kie.workbench.common.screens.defaulteditor.client.editor;

import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.client.common.BusyPopup;
import org.kie.uberfire.client.editors.defaulteditor.DefaultFileEditorPresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.ObservablePath;

public class GuvnorDefaultEditorViewImpl
        implements GuvnorDefaultEditorView {

    @Inject
    private DefaultFileEditorPresenter presenter;

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

    @Override public void setNotDirty() {
    }

    @Override
    public Widget asWidget() {
        return presenter.view;
    }

    @Override public void onStartup(ObservablePath path) {
        presenter.onStartup(path);
    }
}
