package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.mvp.MyTestType;

@WorkbenchEditor(identifier = "test17", supportedTypes = { MyTestType.class })
public class WorkbenchEditorTest17 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }
    
    @WorkbenchPartTitleDecoration
    public IsWidget getTabWidget() {
        return null;
    }

}
