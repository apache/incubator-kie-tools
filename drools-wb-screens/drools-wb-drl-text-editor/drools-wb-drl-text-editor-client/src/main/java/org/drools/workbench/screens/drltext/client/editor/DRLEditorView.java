package org.drools.workbench.screens.drltext.client.editor;

import java.util.List;

import com.google.gwt.user.client.ui.RequiresResize;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.client.mvp.UberView;

public interface DRLEditorView extends KieEditorView,
                                       RequiresResize,
                                       UberView<DRLEditorPresenter> {

    void setContent( final String drl,
                     final List<String> fullyQualifiedClassNames );

    void setContent( final String dslr,
                     final List<String> fullyQualifiedClassNames,
                     final List<DSLSentence> dslConditions,
                     final List<DSLSentence> dslActions );

    String getContent();

}
