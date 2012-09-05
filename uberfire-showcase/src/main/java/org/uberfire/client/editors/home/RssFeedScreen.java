package org.uberfire.client.editors.home;


import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import javax.enterprise.context.Dependent;

@Dependent
@WorkbenchScreen(identifier = "rssFeed")
public class RssFeedScreen {

    @WorkbenchPartTitle
    public String getTitle() {
        return "Randon RSS Feed";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return new Label("Feeding it here");
    }
}
