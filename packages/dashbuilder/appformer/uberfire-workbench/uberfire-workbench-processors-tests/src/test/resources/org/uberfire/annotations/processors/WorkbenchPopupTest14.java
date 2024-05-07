package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.PopupPanel;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.QualifierAnnotation;
import org.uberfire.client.mvp.RegularAnnotation;

@QualifierAnnotation( classField = String.class, stringField = "someText", booleanField = true, intField = 13 )
@RegularAnnotation( classField = String.class, stringField = "someText", booleanField = true, intField = 13 )
@WorkbenchPopup(identifier = "test14")
public class WorkbenchPopupTest14 {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

    @WorkbenchPartView
    public PopupPanel getView() {
        return new PopupPanel();
    }

}
