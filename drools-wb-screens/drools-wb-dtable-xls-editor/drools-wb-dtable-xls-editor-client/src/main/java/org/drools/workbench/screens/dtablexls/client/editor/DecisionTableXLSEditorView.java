package org.drools.workbench.screens.dtablexls.client.editor;

import org.uberfire.backend.vfs.Path;

import com.google.gwt.user.client.ui.IsWidget;

public interface DecisionTableXLSEditorView extends IsWidget {

    void setPath( final Path path );

    void setReadOnly( final boolean isReadOnly );
}
