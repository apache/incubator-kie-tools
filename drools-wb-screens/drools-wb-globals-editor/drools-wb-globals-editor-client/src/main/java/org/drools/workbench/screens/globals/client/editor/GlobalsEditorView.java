package org.drools.workbench.screens.globals.client.editor;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.globals.model.Global;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;

/**
 * Globals Editor View definition
 */
public interface GlobalsEditorView extends KieEditorView,
                                           IsWidget {

    void setContent( final List<Global> globals,
                     final List<String> fullyQualifiedClassNames,
                     final boolean isReadOnly );

}
