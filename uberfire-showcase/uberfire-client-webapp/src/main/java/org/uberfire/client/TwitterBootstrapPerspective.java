package org.uberfire.client;

import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPart;
import org.uberfire.client.annotations.WorkbenchParts;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleDnDWorkbenchPanelPresenter;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

@Templated
@WorkbenchPerspective(identifier="TwitterBootstrapPerspective")
public class TwitterBootstrapPerspective extends Composite {

    @Inject
    @DataField
    @WorkbenchPanel(panelType=MultiTabWorkbenchPanelPresenter.class)
    @WorkbenchParts(@WorkbenchPart(part="MoodScreen"))
    FlowPanel tabPanel;

    @Inject
    @DataField
    @WorkbenchPanel(panelType=MultiListWorkbenchPanelPresenter.class)
    @WorkbenchParts(@WorkbenchPart(part="HelloWorldScreen"))
    FlowPanel listPanel;

    @Inject
    @DataField
    @WorkbenchPanel(panelType=SimpleDnDWorkbenchPanelPresenter.class)
    @WorkbenchParts(@WorkbenchPart(part="HomeScreen"))
    FlowPanel simplePanel;

}
