package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.MyTestType;
import org.uberfire.client.workbench.type.DotResourceType;

@WorkbenchEditor(identifier = "test17", supportedTypes = { MyTestType.class, DotResourceType.class })
public class WorkbenchEditorTest17 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return null;
    }

}
