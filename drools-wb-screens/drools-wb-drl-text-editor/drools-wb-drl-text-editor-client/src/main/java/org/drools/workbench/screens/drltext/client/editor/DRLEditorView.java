package org.drools.workbench.screens.drltext.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;

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
