package org.kie.workbench.common.screens.projectimportsscreen.client.forms;

import javax.inject.Inject;

import org.guvnor.common.services.project.model.ProjectImports;
import org.kie.workbench.common.widgets.configresource.client.widget.unbound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;

public class ProjectImportsScreenViewImpl
        extends KieEditorViewImpl
        implements ProjectImportsScreenView {

    private ImportsWidgetPresenter importsWidget;

    public ProjectImportsScreenViewImpl() {
    }

    @Inject
    public ProjectImportsScreenViewImpl(ImportsWidgetPresenter importsWidget) {
        this.importsWidget = importsWidget;
        initWidget(this.importsWidget.asWidget());
    }

    @Override
    public void setContent(ProjectImports model, boolean isReadOnly) {
        importsWidget.setContent(model, isReadOnly);
    }
}