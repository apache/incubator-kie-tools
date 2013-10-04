package org.kie.workbench.common.screens.defaulteditor.client.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.defaulteditor.client.editor.resources.i18n.GuvnorDefaultEditorConstants;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.editors.texteditor.TextResourceType;
import org.uberfire.client.workbench.type.DotResourceType;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.type.FileNameUtil;


@Dependent
@WorkbenchEditor(identifier = "KieMetaFileTextEditor", supportedTypes = { DotResourceType.class }, priority = Integer.MAX_VALUE - 1)
public class KieMetaDataEditorPresenter
        extends GuvnorTextEditorPresenter {

    @Inject
    private DotResourceType type;

    @OnOpen
    public void onOpen() {
        super.onOpen();
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
            final PlaceRequest place) {
        super.onStartup( path, place );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return GuvnorDefaultEditorConstants.INSTANCE.MetaFileEditor( path.getFileName() );
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return super.getWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }
}