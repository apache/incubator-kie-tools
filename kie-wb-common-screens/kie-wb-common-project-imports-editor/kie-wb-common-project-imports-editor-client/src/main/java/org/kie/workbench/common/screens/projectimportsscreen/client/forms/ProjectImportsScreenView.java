package org.kie.workbench.common.screens.projectimportsscreen.client.forms;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.widgets.configresource.client.widget.unbound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public interface ProjectImportsScreenView extends KieEditorView,
                                                  IsWidget {

    void setContent(ProjectImports model, boolean isReadOnly);

}
