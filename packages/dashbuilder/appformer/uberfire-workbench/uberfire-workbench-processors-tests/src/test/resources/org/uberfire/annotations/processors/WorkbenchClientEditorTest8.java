package org.uberfire.annotations.processors;

import org.uberfire.client.annotations.WorkbenchClientEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.lifecycle.GetContent;
import org.uberfire.lifecycle.SetContent;
import org.uberfire.lifecycle.GetPreview;

import com.google.gwt.user.client.ui.Widget;

import elemental2.promise.Promise;
import org.uberfire.lifecycle.Validate;

@WorkbenchClientEditor(identifier = "editor")
public class WorkbenchClientEditorTest8 extends Widget {

    @WorkbenchPartTitle
    public String title() {
        return "title";
    }

    @SetContent
    public Promise setContent(String path, String content) {
        return null;
    }

    @GetContent
    public Promise getContent() {
        return null;
    }

    @Validate
    public Promise validate() {
        return null;
    }
}
