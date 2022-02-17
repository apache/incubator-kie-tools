package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.annotations.WorkbenchClientEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;

@WorkbenchClientEditor(identifier = "editor")
public class WorkbenchClientEditorTest4 extends Widget {
    
    
    @WorkbenchPartTitle
    public String title() {
        return "title";
    }

}
