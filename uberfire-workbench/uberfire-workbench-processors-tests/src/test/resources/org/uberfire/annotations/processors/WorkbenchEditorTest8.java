package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchEditor.LockingStrategy;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.MyTestType;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnLostFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;

@WorkbenchEditor(identifier = "test8", supportedTypes = { MyTestType.class }, lockingStrategy = LockingStrategy.OPTIMISTIC)
public class WorkbenchEditorTest8 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

    @OnStartup
    public void onStartup(final ObservablePath path) {
    }

    @OnMayClose
    public boolean onMayClose() {
        return true;
    }

    @OnClose
    public void onClose() {
    }

    @OnOpen
    public void onOpen() {
    }

    @OnLostFocus
    public void onLostFocus() {
    }

    @OnFocus
    public void onFocus() {
    }

}
