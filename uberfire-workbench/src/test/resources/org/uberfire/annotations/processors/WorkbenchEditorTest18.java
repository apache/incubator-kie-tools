package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

@WorkbenchEditor(identifier = "test18", fileTypes = "test18")
public class WorkbenchEditorTest18 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    public String getTabTitle() {
        return null;
    }

    @WorkbenchPartTitle
    public IsWidget getTabWidget() {
        return null;
    }

}
