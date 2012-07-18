package org.drools.guvnor.annotations.processors;

import org.drools.guvnor.client.annotations.WorkbenchPartView;
import org.drools.guvnor.client.annotations.WorkbenchPopup;

import com.google.gwt.user.client.ui.PopupPanel;

@WorkbenchPopup(identifier = "test3")
public class PopupTest3 {

    @WorkbenchPartView
    public PopupPanel getView() {
        return new PopupPanel();
    }

}
