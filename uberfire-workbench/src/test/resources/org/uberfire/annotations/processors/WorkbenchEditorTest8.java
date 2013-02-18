package org.uberfire.annotations.processors;

import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnClose;
import org.uberfire.client.annotations.OnFocus;
import org.uberfire.client.annotations.OnLostFocus;
import org.uberfire.client.annotations.OnMayClose;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.mvp.MyTestType;

@WorkbenchEditor(identifier = "test8", supportedTypes = { MyTestType.class })
public class WorkbenchEditorTest8 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

    @OnStart
    public void onStart(final Path path) {
    }

    @OnMayClose
    public boolean onMayClose() {
        return true;
    }

    @OnClose
    public void onClose() {
    }

    @OnReveal
    public void onReveal() {
    }

    @OnLostFocus
    public void onLostFocus() {
    }

    @OnFocus
    public void onFocus() {
    }

}
