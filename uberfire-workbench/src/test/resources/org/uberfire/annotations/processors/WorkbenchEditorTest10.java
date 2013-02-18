package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.MyTestType;
import org.uberfire.client.mvp.UberView;

@WorkbenchEditor(identifier = "test10", supportedTypes = { MyTestType.class })
public class WorkbenchEditorTest10 {

    @WorkbenchPartView
    public UberView<WorkbenchEditorTest10> getView() {
        return null;
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
