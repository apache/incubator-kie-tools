package org.uberfire.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;

@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true)
public class PerspectiveTest12 {

    @WorkbenchPanel(panelType = MultiTabWorkbenchPanelPresenter.class,
                    isDefault = true,
                    parts = { "HelloWorldScreen1", "HelloWorldScreen2" } )
    Object teste = new Object();

    @WorkbenchPanel( parts = "HelloWorldScreen3" )
    Object teste2 = new Object();

    @WorkbenchPanel( parts = "HelloWorldScreen4" )
    Object teste3 = new Object();

    @WorkbenchPanel( parts = "HelloWorldScreen5" )
    Object teste4 = new Object();

    @PostConstruct
    public void setup() {

    }


}