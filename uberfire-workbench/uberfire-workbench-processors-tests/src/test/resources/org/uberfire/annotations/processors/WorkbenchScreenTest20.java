package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnLostFocus;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;

@WorkbenchScreen(identifier = "test20")
public class WorkbenchScreenTest20 {

    @WorkbenchPartView
    private IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    private String getTitle() {
        return "title";
    }

    @OnStartup
    private void onStartup() {
    }

    @OnMayClose
    private boolean onMayClose() {
        return true;
    }

    @OnClose
    private void onClose() {
    }

    @OnOpen
    private void onOpen() {
    }

    @OnLostFocus
    private void onLostFocus() {
    }

    @OnFocus
    private void onFocus() {
    }

}