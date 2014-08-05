package org.drools.workbench.screens.enums.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.uberfire.client.common.HasBusyIndicator;
import org.kie.workbench.common.widgets.client.editor.GuvnorEditorView;

/**
 * Enum Editor View definition
 */
public interface EnumEditorView extends GuvnorEditorView,
                                        IsWidget {

    void setContent( String content );

    String getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

}
