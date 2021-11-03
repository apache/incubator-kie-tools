package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnOpen;

public abstract class WorkbenchScreenTest18Super {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    public String inheritedGetTitle() {
        return null;
    }

    @OnOpen
    public void onOpen() {
        // hello
    }
}
