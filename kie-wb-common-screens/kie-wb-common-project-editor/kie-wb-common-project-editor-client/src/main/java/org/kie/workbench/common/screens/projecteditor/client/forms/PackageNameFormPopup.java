package org.kie.workbench.common.screens.projecteditor.client.forms;

import org.kie.workbench.common.widgets.client.popups.text.FormPopup;
import org.kie.workbench.common.widgets.client.popups.text.FormPopupView;

import javax.inject.Inject;
import java.util.List;

public class PackageNameFormPopup
        extends FormPopup {

    private final PackageNameFormPopupView view;

    @Inject
    public PackageNameFormPopup(PackageNameFormPopupView view) {
        super(view);

        this.view = view;
    }

    public void setPackageNames(List<String> packageNames) {
        for(String packageName:packageNames){
            view.addItem(packageName);
        }
    }
}
