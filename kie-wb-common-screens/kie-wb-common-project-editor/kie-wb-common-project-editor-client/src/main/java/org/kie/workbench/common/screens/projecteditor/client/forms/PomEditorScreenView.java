package org.kie.workbench.common.screens.projecteditor.client.forms;

import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;

public interface PomEditorScreenView
        extends HasBusyIndicator {

    void showSaveSuccessful(String fileName);

}
