package org.uberfire.annotations.processors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true)
public class PerspectiveTest19 {

    @WorkbenchPanel( parts = "noParameterScreen" )
    Object nopParameter = new FlowPanel();

    @WorkbenchPanel( isDefault = true,
                     parts = "oneParameterScreen?uber=fire" )
    Object oneParameter = new FlowPanel();

    @WorkbenchPanel( parts = "twoParametersScreen?uber=fire&uber1=fire1" )
    Object twoParameters = new FlowPanel();

    @PostConstruct
    public void setup() {

    }


}