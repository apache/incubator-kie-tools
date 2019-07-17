package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchClientEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;

import com.google.gwt.user.client.ui.Widget;

@WorkbenchClientEditor(identifier = "editor")
public class WorkbenchClientEditorTest4 extends Widget {
    
    
    @WorkbenchPartTitle
    public String title() {
        return "title";
    }

}
