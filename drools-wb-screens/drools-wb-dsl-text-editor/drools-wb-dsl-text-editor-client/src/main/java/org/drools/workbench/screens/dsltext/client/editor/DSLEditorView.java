package org.drools.workbench.screens.dsltext.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.widgets.client.editor.KieEditorView;

public interface DSLEditorView extends KieEditorView,
                                       IsWidget {

    void setContent( final String content );

    String getContent();

    boolean isDirty();

    boolean confirmClose();

    void makeReadOnly();

}
