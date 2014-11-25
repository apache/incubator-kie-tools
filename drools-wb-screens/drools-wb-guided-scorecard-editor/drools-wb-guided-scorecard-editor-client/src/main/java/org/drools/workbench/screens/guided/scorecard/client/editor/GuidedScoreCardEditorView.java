package org.drools.workbench.screens.guided.scorecard.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;

public interface GuidedScoreCardEditorView extends KieEditorView,
                                                   IsWidget {

    void setContent( final ScoreCardModel model,
                     final AsyncPackageDataModelOracle oracle );

    ScoreCardModel getModel();

    void refreshFactTypes();

}
