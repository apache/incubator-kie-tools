package org.uberfire.client;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;

import com.google.gwt.user.client.ui.Composite;

@ApplicationScoped
@WorkbenchPerspective(identifier = "AnotherPerspective")
@Templated("another_template.html")
public class AnotherPerspective extends Composite {

    @DataField
    @WorkbenchPanel(isDefault = true,
                    panelType = MultiTabWorkbenchPanelPresenter.class,
                    parts = { "HomeScreen", "MoodScreen" } )
    WorkbenchPanelPanel homeScreen = new WorkbenchPanelPanel();

}