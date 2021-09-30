package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import org.jboss.errai.ioc.client.api.ActivatedBy;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.MyTestType;

@WorkbenchEditor(identifier = "test18", supportedTypes = { MyTestType.class })
@ActivatedBy(TestBeanActivator.class)
public class WorkbenchEditorTest18 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}