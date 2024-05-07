package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.QualifierAnnotation;
import org.uberfire.client.mvp.RegularAnnotation;

@QualifierAnnotation( classField = String.class, stringField = "someText", booleanField = true, intField = 13 )
@RegularAnnotation( classField = String.class, stringField = "someText", booleanField = true, intField = 13 )
@WorkbenchScreen(identifier = "test34")
public class WorkbenchScreenTest34 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }
    
    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}
