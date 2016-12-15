package org.uberfire.client;

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
public class PerspectiveTest12 implements IsElement {

    @Inject
    @WorkbenchPanel(panelType = MultiTabWorkbenchPanelPresenter.class,
                    isDefault = true,
                    parts = { "HelloWorldScreen1", "HelloWorldScreen2" } )
    Div teste;

    @Inject
    @WorkbenchPanel( parts = "HelloWorldScreen3" )
    Div teste2;

    @Inject
    @WorkbenchPanel( parts = "HelloWorldScreen4" )
    Div teste3;

    @Inject
    @WorkbenchPanel( parts = "HelloWorldScreen5" )
    Div teste4;

    @PostConstruct
    public void setup() {

    }


}