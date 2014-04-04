package org.uberfire.client.workbench.panels.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.Widget;

@Dependent
@Named("TemplateWorkbenchPanelView")
public class TemplateWorkbenchPanelView extends AbstractTemplateWorkbenchPanelView<TemplateWorkbenchPanelPresenter> {

    @Inject
    public TemplateWorkbenchPanelView() {
        initWidget( panel );
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}
