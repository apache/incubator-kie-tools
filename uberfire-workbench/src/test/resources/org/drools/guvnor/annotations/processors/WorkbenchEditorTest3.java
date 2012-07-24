package org.drools.guvnor.annotations.processors;

import org.drools.guvnor.client.annotations.WorkbenchEditor;
import org.drools.guvnor.client.annotations.WorkbenchPartView;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

@WorkbenchEditor(identifier = "test3", fileTypes = "test3")
public class WorkbenchEditorTest3 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

}
