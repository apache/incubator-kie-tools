package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.MyTestType;
import org.uberfire.client.workbench.file.DotResourceType;

@WorkbenchEditor(identifier = "test18", supportedTypes = { MyTestType.class, DotResourceType.class })
public class WorkbenchEditorTest19 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return null;
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitleWidget() {
        return null;
    }

}
