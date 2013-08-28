package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.google.gwt.user.client.ui.IsWidget;

public interface FileLoggerEditorView
        extends IsWidget {

    interface Presenter {

    }

    void setName(String name);

    void setInterval(int interval);

    void setFile(String file);
}
