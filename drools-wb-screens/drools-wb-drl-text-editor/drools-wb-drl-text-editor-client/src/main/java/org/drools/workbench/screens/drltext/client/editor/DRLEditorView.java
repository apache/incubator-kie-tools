package org.drools.workbench.screens.drltext.client.editor;

import java.util.List;

import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;
import org.uberfire.client.mvp.UberView;

public interface DRLEditorView extends HasBusyIndicator,
                                       UberView<DRLEditorPresenter> {

    void setContent( final String drl,
                     final List<String> fullyQualifiedClassNames );

    void setContent( final String dslr,
                     final List<String> fullyQualifiedClassNames,
                     final List<DSLSentence> dslConditions,
                     final List<DSLSentence> dslActions );

    String getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

}
