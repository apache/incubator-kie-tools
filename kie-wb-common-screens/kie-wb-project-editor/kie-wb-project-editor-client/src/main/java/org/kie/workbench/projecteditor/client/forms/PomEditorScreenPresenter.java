package org.kie.workbench.projecteditor.client.forms;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.kie.workbench.widgets.common.client.menu.FileMenuBuilder;
import org.kie.workbench.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.workbench.projecteditor.client.type.POMResourceType;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.*;
import org.uberfire.client.workbench.widgets.menu.Menus;
import org.uberfire.shared.mvp.PlaceRequest;

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
