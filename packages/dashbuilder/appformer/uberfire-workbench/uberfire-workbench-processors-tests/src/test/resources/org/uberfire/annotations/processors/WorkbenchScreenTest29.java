package org.uberfire.annotations.processors;

import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@WorkbenchScreen(identifier = "test29")
public class WorkbenchScreenTest29 {

    @WorkbenchPartView
    public IsElement getView() {
        return null;
    }
    
    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}