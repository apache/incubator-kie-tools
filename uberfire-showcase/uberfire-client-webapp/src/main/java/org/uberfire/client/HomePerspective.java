package org.uberfire.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.ParameterMapping;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPart;
import org.uberfire.client.annotations.WorkbenchPerspective;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

@ApplicationScoped
@WorkbenchPerspective(
                      identifier = "HomePerspective",
                      isDefault = true)
@Templated
public class HomePerspective extends Composite {

    @DataField
    @WorkbenchPart(part = "MoodScreen" ,parameters = {@ParameterMapping(name="uber", val="fire"),@ParameterMapping(name="uber1", val="fire1")})
    FlowPanel moodScreen = new FlowPanel();

    @DataField
    @WorkbenchPanel
    @WorkbenchPart(part = "HomeScreen", parameters = @ParameterMapping(name="uber", val="fire"))
    FlowPanel homeScreen = new FlowPanel();

    @DataField
    @WorkbenchPart(part = "AnotherScreen")
    FlowPanel anotherScreen = new FlowPanel();


    @PostConstruct
    public void setup() {
    }

}