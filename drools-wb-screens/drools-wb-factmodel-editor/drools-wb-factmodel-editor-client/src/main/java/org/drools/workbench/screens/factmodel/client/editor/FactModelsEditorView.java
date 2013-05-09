package org.drools.workbench.screens.factmodel.client.editor;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.guvnor.commons.ui.client.widget.HasBusyIndicator;
import org.drools.workbench.screens.factmodel.model.FactMetaModel;
import org.drools.workbench.screens.factmodel.model.FactModels;

public interface FactModelsEditorView extends HasBusyIndicator,
                                              IsWidget {

    void setContent( final FactModels content,
                     final List<FactMetaModel> superTypeFactModels,
                     final ModelNameHelper modelNameHelper );

    FactModels getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

}
