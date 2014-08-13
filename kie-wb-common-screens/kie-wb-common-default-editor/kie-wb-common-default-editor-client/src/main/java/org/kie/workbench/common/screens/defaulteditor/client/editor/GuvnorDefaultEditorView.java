package org.kie.workbench.common.screens.defaulteditor.client.editor;

import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.backend.vfs.ObservablePath;

public interface GuvnorDefaultEditorView
        extends KieEditorView {

    void onStartup(ObservablePath path);

}
