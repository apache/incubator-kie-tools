package org.uberfire.annotations.processors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPart;
import org.uberfire.client.annotations.WorkbenchParts;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.workbench.model.PanelType;

@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true)
public class PerspectiveTest13 {

    @WorkbenchPanel(panelType = PanelType.MULTI_TAB, isDefault =  true)
    @WorkbenchParts({@WorkbenchPart(part = "HelloWorldScreen1"),@WorkbenchPart(part = "HelloWorldScreen2")})
    Object teste = new Object();

    @PostConstruct
    public void setup() {

    }


}