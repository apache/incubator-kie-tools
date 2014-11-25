package org.drools.workbench.screens.workitems.client.editor;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;

public interface WorkItemsEditorView extends KieEditorView,
                                             IsWidget {

    void setContent( final String content,
                     final List<String> workItemImages );

    String getContent();

}
