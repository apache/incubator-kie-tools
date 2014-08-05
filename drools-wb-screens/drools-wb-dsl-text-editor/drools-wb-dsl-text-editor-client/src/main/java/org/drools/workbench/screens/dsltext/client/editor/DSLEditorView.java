package org.drools.workbench.screens.dsltext.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.uberfire.client.common.HasBusyIndicator;
import org.kie.workbench.common.widgets.client.editor.GuvnorEditorView;

public interface DSLEditorView extends GuvnorEditorView,
                                       IsWidget {

    void setContent( final String content );

    String getContent();

    boolean isDirty();

    boolean confirmClose();

    void makeReadOnly();

}
