package org.uberfire.client.screens;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.perspectives.SimplePerspective;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnShutdown;
import org.uberfire.lifecycle.OnStartup;

import com.google.gwt.user.client.ui.Label;

@WorkbenchScreen(identifier = "OwnedScreen", owningPerspective = SimplePerspective.class)
public class OwnedScreen {

    private final Label view = new Label("This screen is always displayed in the SimplePerspective.");

    @WorkbenchPartTitle
    public String getTitle() {
        return "Owned Screen";
    }

    @WorkbenchPartView
    public Label getView() {
        return view;
    }

    @OnStartup
    public void onStartup() {
        new Exception("OwnedScreen is starting!").printStackTrace();
    }

    @OnOpen
    public void onOpen() {
        new Exception("OwnedScreen is opening!").printStackTrace();
    }

    @OnClose
    public void onClose() {
        new Exception("OwnedScreen is opening!").printStackTrace();
    }

    @OnShutdown
    public void onShutdown() {
        new Exception("OwnedScreen is shutting down!").printStackTrace();
    }
}
