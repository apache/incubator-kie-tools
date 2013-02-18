package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartView;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.mvp.MyTestType;
import org.uberfire.security.annotations.All;
import org.uberfire.security.annotations.Roles;

@WorkbenchEditor(identifier = "test3", supportedTypes = { MyTestType.class })
@All @Roles({"ADMIN", "SUDO"})
public class WorkbenchEditorTest3 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

}
