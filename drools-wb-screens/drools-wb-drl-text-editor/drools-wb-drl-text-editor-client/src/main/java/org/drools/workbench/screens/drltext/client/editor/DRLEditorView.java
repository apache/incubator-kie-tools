package org.drools.workbench.screens.drltext.client.editor;

import java.util.List;

import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;
import org.uberfire.client.mvp.UberView;

public interface DRLEditorView extends HasBusyIndicator,
                                       UberView<DRLEditorPresenter> {

    void setContent( final List<String> fullyQualifiedClassNames );

    void setContent( final String content,
                     final List<String> fullyQualifiedClassNames );

    String getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

}
