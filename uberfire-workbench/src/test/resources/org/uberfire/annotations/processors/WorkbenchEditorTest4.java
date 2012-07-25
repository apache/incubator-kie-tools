package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;

@WorkbenchEditor(identifier = "test4", fileTypes = "test4")
public class WorkbenchEditorTest4 {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}
