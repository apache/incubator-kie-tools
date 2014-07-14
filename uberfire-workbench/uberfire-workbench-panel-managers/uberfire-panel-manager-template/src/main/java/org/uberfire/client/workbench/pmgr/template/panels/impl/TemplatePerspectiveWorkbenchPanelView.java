package org.uberfire.client.workbench.pmgr.template.panels.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import org.uberfire.client.workbench.pmgr.template.TemplatePanelDefinitionImpl;

import com.google.gwt.user.client.ui.Widget;

@Dependent
@Named("TemplatePerspectiveWorkbenchPanelView")
public class TemplatePerspectiveWorkbenchPanelView extends AbstractTemplateWorkbenchPanelView<TemplatePerspectiveWorkbenchPanelPresenter> {

    @Override
    public Widget asWidget() {
        TemplatePanelDefinitionImpl definition = (TemplatePanelDefinitionImpl) getPresenter().getDefinition();
        return definition.getRealPresenterWidget();
    }

}
