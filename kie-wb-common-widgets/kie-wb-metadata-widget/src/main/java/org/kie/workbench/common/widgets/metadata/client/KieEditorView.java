package org.kie.workbench.common.widgets.metadata.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.uberfire.client.common.HasBusyIndicator;

public interface KieEditorView
        extends HasBusyIndicator, IsWidget {

    void alertReadOnly();

    void setNotDirty();

}
