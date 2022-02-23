package org.uberfire.annotations.processors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;

@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true)
public class PerspectiveTest18 implements IsElement {

    @Inject
    @WorkbenchPanel(panelType = MultiListWorkbenchPanelPresenter.class, isDefault = true, parts = "TesteScreen")
    Div teste1;

    @Inject
    @WorkbenchPanel(parts = { "HelloWorldScreen1", "HelloWorldScreen2" })
    Div teste2;

    @PostConstruct
    public void setup() {

    }


}