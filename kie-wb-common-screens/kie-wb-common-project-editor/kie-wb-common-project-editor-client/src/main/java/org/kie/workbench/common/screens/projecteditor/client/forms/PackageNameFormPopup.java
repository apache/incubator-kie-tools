package org.kie.workbench.common.screens.projecteditor.client.forms;

import org.kie.workbench.common.widgets.client.popups.text.FormPopup;
import org.kie.workbench.common.widgets.client.popups.text.FormPopupView;

import javax.inject.Inject;

public class PackageNameFormPopup
        extends FormPopup {

    @Inject
    public PackageNameFormPopup(PackageNameFormPopupView view) {
        super(view);

        view.setName("A");
        view.setName("B");
        view.setName("C");
    }
}
