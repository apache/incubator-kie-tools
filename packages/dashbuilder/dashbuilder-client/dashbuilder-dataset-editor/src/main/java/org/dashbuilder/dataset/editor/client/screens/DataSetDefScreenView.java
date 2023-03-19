package org.dashbuilder.dataset.editor.client.screens;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.ext.editor.commons.client.BaseEditorView;

public interface DataSetDefScreenView extends BaseEditorView, IsWidget {
    void setWidget(IsWidget widget);
}
