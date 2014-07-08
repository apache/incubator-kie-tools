package org.drools.workbench.screens.guided.scorecard.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.kie.uberfire.client.common.HasBusyIndicator;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;

public interface GuidedScoreCardEditorView extends HasBusyIndicator,
                                                   IsWidget {

    void setContent( final ScoreCardModel model,
                     final AsyncPackageDataModelOracle oracle );

    ScoreCardModel getModel();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

    void refreshFactTypes();

}
