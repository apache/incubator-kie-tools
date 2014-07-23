package org.uberfire.annotations.processors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPart;
import org.uberfire.client.annotations.WorkbenchPerspective;
@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true)
public class PerspectiveTest17 {

    @WorkbenchPanel(panelType = MultiTabWorkbenchPanelPresenter.class, isDefault =  true)
    @WorkbenchPart(part = "TesteScreen")
    Object teste1 = new FlowPanel();

    @WorkbenchPart(part = "TesteScreen1")
    Object teste2 = new Object();

    @PostConstruct
    public void setup() {

    }


}