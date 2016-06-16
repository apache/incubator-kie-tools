package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.MyTestType;

@WorkbenchEditor(identifier = "test3", supportedTypes = { MyTestType.class })
public class WorkbenchEditorTest3 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

}
