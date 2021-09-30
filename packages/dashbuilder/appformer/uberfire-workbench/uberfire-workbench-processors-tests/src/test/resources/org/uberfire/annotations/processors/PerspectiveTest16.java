package org.uberfire.annotations.processors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import javax.inject.Inject;

@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true)
public class PerspectiveTest16 implements IsElement {

    @Inject
    @WorkbenchPanel( panelType = MultiTabWorkbenchPanelPresenter.class,
                     parts = { "HelloWorldScreen1", "HelloWorldScreen2" } )
    Div teste;

    @Inject
    @WorkbenchPanel( panelType = MultiTabWorkbenchPanelPresenter.class,
                     parts = { "HelloWorldScreen1", "HelloWorldScreen2" } )
    Div teste2;

    @PostConstruct
    public void setup() {

    }


}