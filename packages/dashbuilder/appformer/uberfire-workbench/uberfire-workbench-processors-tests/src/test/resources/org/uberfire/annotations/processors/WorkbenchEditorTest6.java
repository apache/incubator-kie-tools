package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.mvp.MyTestType;

@WorkbenchEditor(identifier = "test6", supportedTypes = { MyTestType.class })
public class WorkbenchEditorTest6 extends SimplePanel {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}
