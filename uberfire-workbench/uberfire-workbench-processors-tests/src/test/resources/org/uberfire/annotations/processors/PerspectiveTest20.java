package org.uberfire.annotations.processors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.client.annotations.ParameterMapping;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPart;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true)
public class PerspectiveTest20 {



    @WorkbenchPart(part = "noParameterScreen")
    Object nopParameter = new FlowPanel();

    @WorkbenchPanel(isDefault = true)
    @WorkbenchPart(part = "oneParameterScreen", parameters = @ParameterMapping(name="uber", val="fire"))
    Object oneParameter = new FlowPanel();

    @WorkbenchPart(part = "twoParametersScreen", parameters = {@ParameterMapping(name="uber", val="fire"),@ParameterMapping(name="uber1", val="fire1")})
    Object twoParameters = new FlowPanel();

    @PostConstruct
    public void setup() {

    }

    @OnStartup
    public void onStartup() {
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
    }
}