package org.drools.workbench.screens.scorecardxls.client.editor;

import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberView;

public interface ScoreCardXLSEditorView
        extends KieEditorView, UberView<ScoreCardXLSEditorView.Presenter> {

    interface Presenter {

        void reload();

    }

    void setPath(final Path path);

    void setReadOnly(final boolean isReadOnly);

    void setConcurrentUpdateSessionInfo(final ObservablePath.OnConcurrentUpdateEvent eventInfo);

}
