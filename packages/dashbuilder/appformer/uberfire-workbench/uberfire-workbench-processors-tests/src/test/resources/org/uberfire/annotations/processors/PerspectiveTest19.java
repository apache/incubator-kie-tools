package org.uberfire.annotations.processors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import javax.inject.Inject;


@ApplicationScoped
@WorkbenchPerspective(
        identifier = "HomePerspective",
        isDefault = true)
public class PerspectiveTest19 implements IsElement {

    @Inject
    @WorkbenchPanel( parts = "noParameterScreen" )
    Div nopParameter;

    @Inject
    @WorkbenchPanel( isDefault = true,
                     parts = "oneParameterScreen?uber=fire" )
    Div oneParameter;

    @Inject
    @WorkbenchPanel( parts = "twoParametersScreen?uber=fire&uber1=fire1" )
    Div twoParameters;

    @PostConstruct
    public void setup() {

    }


}