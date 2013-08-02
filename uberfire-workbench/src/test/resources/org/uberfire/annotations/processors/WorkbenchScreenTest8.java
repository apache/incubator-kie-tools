package org.uberfire.annotations.processors;

import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnLostFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

@WorkbenchScreen(identifier = "test8")
public class WorkbenchScreenTest8 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

    @OnStartup
    public void onStartup() {
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
