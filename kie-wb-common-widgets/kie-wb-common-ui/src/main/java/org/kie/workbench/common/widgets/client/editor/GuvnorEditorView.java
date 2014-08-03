package org.kie.workbench.common.widgets.client.editor;

import org.kie.uberfire.client.common.HasBusyIndicator;

public interface GuvnorEditorView
        extends HasBusyIndicator {

    void alertReadOnly();

    void setNotDirty();

}
