package org.uberfire.annotations.processors;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.MyTestType;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@WorkbenchEditor(identifier = "test14", supportedTypes = { MyTestType.class })
public class WorkbenchEditorTest14 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

    @OnStartup
    public void onStartup(final Path path) {
    }

    @OnStartup
    public void onStartup(final Path path,
                        final PlaceRequest place) {
    }

}
