package org.drools.guvnor.annotations.processors;

import org.drools.guvnor.backend.vfs.Path;
import org.drools.guvnor.client.annotations.OnClose;
import org.drools.guvnor.client.annotations.OnFocus;
import org.drools.guvnor.client.annotations.OnLostFocus;
import org.drools.guvnor.client.annotations.OnMayClose;
import org.drools.guvnor.client.annotations.OnReveal;
import org.drools.guvnor.client.annotations.OnStart;
import org.drools.guvnor.client.annotations.WorkbenchEditor;
import org.drools.guvnor.client.annotations.WorkbenchPartTitle;
import org.drools.guvnor.client.annotations.WorkbenchPartView;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

@WorkbenchEditor(identifier = "test8", fileTypes = "test8")
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
