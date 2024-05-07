package org.uberfire.annotations.processors;

import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@WorkbenchScreen(identifier = "test32")
public class WorkbenchScreenTest32 {

    @WorkbenchPartView
    public IsElement getView() {
        return null;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return null;
    }

    @WorkbenchPartTitleDecoration
    public IsElement getTitleWidget() {
        return null;
    }

}
