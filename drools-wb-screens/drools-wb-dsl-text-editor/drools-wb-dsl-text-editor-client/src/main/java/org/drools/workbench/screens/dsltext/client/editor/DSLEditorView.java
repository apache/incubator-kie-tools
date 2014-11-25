package org.drools.workbench.screens.dsltext.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;

public interface DSLEditorView extends KieEditorView,
                                       IsWidget {

    void setContent( final String content );

    String getContent();

    void makeReadOnly();

}
