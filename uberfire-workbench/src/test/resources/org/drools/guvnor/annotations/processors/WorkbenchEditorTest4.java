package org.drools.guvnor.annotations.processors;

import org.drools.guvnor.client.annotations.WorkbenchEditor;
import org.drools.guvnor.client.annotations.WorkbenchPartTitle;

@WorkbenchEditor(identifier = "test4", fileTypes = "test4")
public class WorkbenchEditorTest4 {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}
