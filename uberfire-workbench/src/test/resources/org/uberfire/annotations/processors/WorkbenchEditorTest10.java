package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

@WorkbenchEditor(identifier = "test10", fileTypes = "test10")
public class WorkbenchEditorTest10 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

    @WorkbenchMenu
    public String getMenuBar() {
        return "";
    }

}
