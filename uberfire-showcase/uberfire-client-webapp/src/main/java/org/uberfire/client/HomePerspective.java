package org.uberfire.client;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.ParameterMapping;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPart;
import org.uberfire.client.annotations.WorkbenchPerspective;

import com.google.gwt.user.client.ui.Composite;

@ApplicationScoped
@WorkbenchPerspective(
                      identifier = "HomePerspective",
                      isDefault = true)
@Templated
public class HomePerspective extends Composite {

    @DataField
    @WorkbenchPart(part = "MoodScreen" ,parameters = {@ParameterMapping(name="uber", val="fire"),@ParameterMapping(name="uber1", val="fire1")})
    WorkbenchPanelPanel moodScreen = new WorkbenchPanelPanel(100);

    @DataField
    @WorkbenchPanel
    @WorkbenchPart(part = "HomeScreen", parameters = @ParameterMapping(name="uber", val="fire"))
    WorkbenchPanelPanel homeScreen = new WorkbenchPanelPanel(100);

    @DataField
    @WorkbenchPart(part = "AnotherScreen")
    WorkbenchPanelPanel anotherScreen = new WorkbenchPanelPanel(100);


    //    @PostConstruct
    //    public void setup() {
    //        resize( moodScreen );
    //        resize( homeScreen );
    //        resize( anotherScreen );
    //    }
    //
    //    private void resize( Widget container ) {
    //        container.setPixelSize( 300, 300 );
    //    }
}