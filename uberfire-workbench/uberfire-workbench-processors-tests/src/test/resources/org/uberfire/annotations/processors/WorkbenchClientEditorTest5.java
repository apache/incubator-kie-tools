package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchClientEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.lifecycle.SetContent;

import com.google.gwt.user.client.ui.Widget;

@WorkbenchClientEditor(identifier = "editor")
public class WorkbenchClientEditorTest5 extends Widget {
    
    
    @WorkbenchPartTitle
    public String title() {
        return "title";
    }
    
    @SetContent
    public void setContent(String path, String content) {
        
    }

}
