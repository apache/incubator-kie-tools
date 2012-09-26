package org.uberfire.annotations.processors;

import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.shared.mvp.PlaceRequest;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

@WorkbenchEditor(identifier = "test14", fileTypes = "test14")
public class WorkbenchEditorTest14 {

    @WorkbenchPartView
    public IsWidget getView() {
        return new SimplePanel();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

    @OnStart
    public void onStart(final Path path) {
    }

    @OnStart
    public void onStart(final Path path,
                        final PlaceRequest place) {
    }

}
