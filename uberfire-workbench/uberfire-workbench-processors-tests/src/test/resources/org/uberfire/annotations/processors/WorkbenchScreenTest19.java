package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import org.jboss.errai.ioc.client.api.ActivatedBy;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@WorkbenchScreen(identifier = "test19")
@ActivatedBy(TestBeanActivator.class)
public class WorkbenchScreenTest19 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }
    
    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}