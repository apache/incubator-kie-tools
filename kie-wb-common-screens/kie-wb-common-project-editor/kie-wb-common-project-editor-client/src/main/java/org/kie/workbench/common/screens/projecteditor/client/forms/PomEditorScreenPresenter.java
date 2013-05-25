package org.kie.workbench.common.screens.projecteditor.client.forms;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.workbench.common.screens.projecteditor.client.type.POMResourceType;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.mvp.PlaceRequest;

@WorkbenchEditor(identifier = "pomScreen", supportedTypes = {POMResourceType.class})
public class PomEditorScreenPresenter {

    private POMEditorPanel pomEditorPanel;
    private FileMenuBuilder menuBuilder;
    private Menus menus;
    private Path path;
    private boolean isReadOnly;


    public PomEditorScreenPresenter() {
    }

    @Inject
    public PomEditorScreenPresenter(POMEditorPanel pomEditorPanel,
                                    FileMenuBuilder menuBuilder) {
        this.pomEditorPanel = pomEditorPanel;
        this.menuBuilder = menuBuilder;
    }

    @OnStart
    public void init(final Path path,
                     final PlaceRequest request) {
        this.path = path;
        this.isReadOnly = request.getParameter("readOnly", null) == null ? false : true;

        pomEditorPanel.init(path, isReadOnly);

        fillMenuBar();
    }

    private void fillMenuBar() {
        if (isReadOnly) {
            menus = menuBuilder.addRestoreVersion(path).build();
        }
    }

    @WorkbenchMenu
    public Menus buildMenuBar() {
        return menus;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectEditorConstants.INSTANCE.PomDotXml();
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return pomEditorPanel.asWidget();
    }

}
