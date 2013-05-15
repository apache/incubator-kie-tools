package org.drools.workbench.screens.guided.scorecard.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;

public interface GuidedScoreCardEditorView extends HasBusyIndicator,
                                                   IsWidget {

    void setContent( final ScoreCardModel model,
                     final PackageDataModelOracle oracle );

    ScoreCardModel getModel();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

}
