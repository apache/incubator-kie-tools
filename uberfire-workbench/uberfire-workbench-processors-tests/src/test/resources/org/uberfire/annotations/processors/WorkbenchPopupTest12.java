package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.PopupPanel;

import org.jboss.errai.ioc.client.api.ActivatedBy;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;

@WorkbenchPopup(identifier = "test12")
@ActivatedBy(TestBeanActivator.class)
public class WorkbenchPopupTest12 {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

    @WorkbenchPartView
    public PopupPanel getView() {
        return new PopupPanel();
    }

}
