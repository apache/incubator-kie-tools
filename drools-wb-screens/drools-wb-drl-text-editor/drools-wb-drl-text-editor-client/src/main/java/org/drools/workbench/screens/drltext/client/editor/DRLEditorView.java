package org.drools.workbench.screens.drltext.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;

public interface DRLEditorView extends HasBusyIndicator,
                                       IsWidget {

    void setContent( final String content,
                     final PackageDataModelOracle dataModel );

    String getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

}
