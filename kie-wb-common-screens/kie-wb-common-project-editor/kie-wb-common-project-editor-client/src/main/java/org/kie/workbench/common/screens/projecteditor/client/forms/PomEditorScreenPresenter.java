package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.defaulteditor.client.editor.GuvnorTextEditorPresenter;
import org.kie.workbench.common.screens.projecteditor.client.type.POMResourceType;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@WorkbenchEditor(identifier = "pomScreen", supportedTypes = { POMResourceType.class })
public class PomEditorScreenPresenter
        extends GuvnorTextEditorPresenter {

    @OnStartup
    public void onStartup( final ObservablePath path,
                           final PlaceRequest place ) {
        super.onStartup( path, place );
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return super.getMenus();
    }

    @OnOpen
    public void onOpen() {
        super.onOpen();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "pom.xml";
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return super.getWidget();
    }
}
