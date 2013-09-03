package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.defaulteditor.client.editor.GuvnorTextEditorPresenter;
import org.kie.workbench.common.screens.projecteditor.client.type.POMResourceType;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

//@WorkbenchEditor(identifier = "pomScreen", supportedTypes = {POMResourceType.class})
public class PomEditorScreenPresenter
        extends GuvnorTextEditorPresenter {

    @OnStartup
    public void onStartup(final Path path,
            final PlaceRequest place) {
        super.onStartup(path, place);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @OnClose
    public void onClose() {
        super.onClose();
    }

}
