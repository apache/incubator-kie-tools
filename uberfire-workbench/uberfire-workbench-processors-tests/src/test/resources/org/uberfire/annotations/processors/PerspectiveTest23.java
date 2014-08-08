package org.uberfire.annotations.processors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.FlowPanel;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true)
public class PerspectiveTest23 {

    @WorkbenchPanel( parts = "whatevzz" )
    Object nopParameter = new FlowPanel();

    @Perspective
    PerspectiveDefinition getDefaultLayout() {
        return null;
    }
}
