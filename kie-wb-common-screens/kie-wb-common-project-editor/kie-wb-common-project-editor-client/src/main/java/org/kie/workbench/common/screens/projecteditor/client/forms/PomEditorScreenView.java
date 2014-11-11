package org.kie.workbench.common.screens.projecteditor.client.forms;

import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public interface PomEditorScreenView
        extends HasBusyIndicator {

    void showSaveSuccessful( String fileName );

}
