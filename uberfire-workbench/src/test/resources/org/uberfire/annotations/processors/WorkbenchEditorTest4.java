package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.mvp.MyTestType;

@WorkbenchEditor(identifier = "test4", supportedTypes = { MyTestType.class })
public class WorkbenchEditorTest4 {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}
