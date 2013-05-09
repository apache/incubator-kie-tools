package org.drools.workbench.screens.scorecardxls.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;

public interface ScoreCardXLSEditorView extends IsWidget {

    void setPath( final Path path );

    void setReadOnly( final boolean isReadOnly );
}
