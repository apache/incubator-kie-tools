package org.kie.workbench.common.screens.projecteditor.client.forms;

import org.uberfire.client.common.BusyPopup;

public class KModuleEditorScreenViewImpl
        implements KModuleEditorScreenView {

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}
