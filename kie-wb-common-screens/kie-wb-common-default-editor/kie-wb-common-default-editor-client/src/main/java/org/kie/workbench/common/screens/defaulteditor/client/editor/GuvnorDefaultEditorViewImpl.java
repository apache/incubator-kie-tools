package org.kie.workbench.common.screens.defaulteditor.client.editor;

import javax.inject.Inject;

import org.kie.uberfire.client.editors.defaulteditor.DefaultFileEditorPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.uberfire.backend.vfs.ObservablePath;

public class GuvnorDefaultEditorViewImpl
        extends KieEditorViewImpl
        implements GuvnorDefaultEditorView {

    private final DefaultFileEditorPresenter presenter;

    @Inject
    public GuvnorDefaultEditorViewImpl(DefaultFileEditorPresenter presenter) {
        this.presenter = presenter;
        initWidget(this.presenter.view);
    }

    @Override public void setNotDirty() {
    }

    @Override public void onStartup(ObservablePath path) {
        presenter.onStartup(path);
    }
}
