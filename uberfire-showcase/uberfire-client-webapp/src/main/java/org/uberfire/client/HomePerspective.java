package org.uberfire.client;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;

import com.google.gwt.user.client.ui.Composite;

@ApplicationScoped
@WorkbenchPerspective(identifier = "HomePerspective",
                      isDefault = true)
@Templated
public class HomePerspective extends Composite {

    @DataField
    @WorkbenchPanel(parts = "MoodScreen?uber=fire&uber1=fire1")
    WorkbenchPanelPanel moodScreen = new WorkbenchPanelPanel( 100 );

    @DataField
    @WorkbenchPanel(parts = "HomeScreen?uber=fire")
    WorkbenchPanelPanel homeScreen = new WorkbenchPanelPanel( 100 );

    @DataField
    @WorkbenchPanel(parts = "AnotherScreen")
    WorkbenchPanelPanel anotherScreen = new WorkbenchPanelPanel( 100 );

}