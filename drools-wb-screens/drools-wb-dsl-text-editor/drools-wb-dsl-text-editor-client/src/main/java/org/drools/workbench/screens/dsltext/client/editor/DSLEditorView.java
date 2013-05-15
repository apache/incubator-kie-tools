package org.drools.workbench.screens.dsltext.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;

public interface DSLEditorView extends HasBusyIndicator,
                                       IsWidget {

    void setContent( final String content );

    String getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void makeReadOnly();

    void alertReadOnly();

}
