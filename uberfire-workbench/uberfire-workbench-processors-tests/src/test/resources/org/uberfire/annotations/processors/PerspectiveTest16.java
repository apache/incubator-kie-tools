package org.uberfire.annotations.processors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true)
public class PerspectiveTest16 {

    @WorkbenchPanel( panelType = MultiTabWorkbenchPanelPresenter.class,
                     parts = { "HelloWorldScreen1", "HelloWorldScreen2" } )
    Object teste = new Object();

    @WorkbenchPanel( panelType = MultiTabWorkbenchPanelPresenter.class,
                     parts = { "HelloWorldScreen1", "HelloWorldScreen2" } )
    Object teste2 = new Object();

    @PostConstruct
    public void setup() {

    }


}