package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import org.jboss.errai.ioc.client.api.ActivatedBy;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import static org.uberfire.client.annotations.WorkbenchEditor.LockingStrategy.PESSIMISTIC;
import org.uberfire.client.mvp.MyTestType;
import org.uberfire.security.annotations.Roles;

@WorkbenchEditor(identifier = "test27", lockingStrategy = PESSIMISTIC)
public class WorkbenchEditorTest27 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}