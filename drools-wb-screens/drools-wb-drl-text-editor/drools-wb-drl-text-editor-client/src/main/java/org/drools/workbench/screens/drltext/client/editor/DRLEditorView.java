package org.drools.workbench.screens.drltext.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;

public interface DRLEditorView extends HasBusyIndicator,
                                       IsWidget {

    void setContent( final AsyncPackageDataModelOracle oracle );

    void setContent( final String content,
                     final AsyncPackageDataModelOracle oracle );

    String getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

}
