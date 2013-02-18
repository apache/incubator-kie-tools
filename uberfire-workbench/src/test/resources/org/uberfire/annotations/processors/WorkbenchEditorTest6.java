package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;

import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.mvp.MyTestType;
import org.uberfire.security.annotations.All;
import org.uberfire.security.annotations.Roles;

@WorkbenchEditor(identifier = "test6", supportedTypes = { MyTestType.class })
@All @Roles({"ADMIN", "SUDO"})
public class WorkbenchEditorTest6 extends SimplePanel {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}
