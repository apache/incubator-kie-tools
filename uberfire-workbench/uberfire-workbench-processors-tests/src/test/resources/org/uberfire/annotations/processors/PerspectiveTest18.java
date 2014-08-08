package org.uberfire.annotations.processors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.FlowPanel;

import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true)
public class PerspectiveTest18 {

    @WorkbenchPanel(panelType = MultiListWorkbenchPanelPresenter.class, isDefault = true, parts = "TesteScreen")
    Object teste1 = new FlowPanel();

    @WorkbenchPanel(parts = { "HelloWorldScreen1", "HelloWorldScreen2" })
    Object teste2 = new Object();

    @PostConstruct
    public void setup() {

    }


}