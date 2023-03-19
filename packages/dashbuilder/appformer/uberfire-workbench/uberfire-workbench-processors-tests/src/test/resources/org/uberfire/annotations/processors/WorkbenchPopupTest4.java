package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPopup;

@WorkbenchPopup(identifier = "test4")
public class WorkbenchPopupTest4 extends SimplePanel {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}
