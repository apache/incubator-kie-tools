package org.drools.workbench.screens.enums.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;

/**
 * Enum Editor View definition
 */
public interface EnumEditorView extends HasBusyIndicator,
                                        IsWidget {

    void setContent( String content );

    String getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

}
