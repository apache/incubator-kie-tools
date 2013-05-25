package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchToolBar;
import org.uberfire.client.mvp.MyTestType;
import org.uberfire.workbench.model.toolbar.ToolBar;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

@WorkbenchEditor(identifier = "test15", supportedTypes = { MyTestType.class })
public class WorkbenchEditorTest15 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

    @WorkbenchToolBar
    public ToolBar getToolBar() {
        return null;
    }

}
