package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.screens.projecteditor.client.type.KModuleResourceType;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.workbench.widgets.menu.Menus;
import org.uberfire.shared.mvp.PlaceRequest;

@WorkbenchEditor(identifier = "kmoduleScreen", supportedTypes = { KModuleResourceType.class })
public class KModuleEditorScreenPresenter {

    private       boolean            isReadOnly;
    private       Path               path;
    private final KModuleEditorPanel kModuleEditorPanel;
    private       Menus              menus;
    private final FileMenuBuilder menuBuilder;

    @Inject
    public KModuleEditorScreenPresenter( KModuleEditorPanel kModuleEditorPanel,
                                         FileMenuBuilder menuBuilder ) {
        this.kModuleEditorPanel = kModuleEditorPanel;
        this.menuBuilder = menuBuilder;
    }

    @OnStart
    public void init( final Path path,
                      final PlaceRequest request ) {
        this.path = path;
        this.isReadOnly = request.getParameter( "readOnly", null ) == null ? false : true;

        kModuleEditorPanel.init( path, isReadOnly );

        fillMenuBar();
    }

    private void fillMenuBar() {
        if ( isReadOnly ) {
            menus = menuBuilder.addRestoreVersion( path ).build();
        }
    }

    @WorkbenchMenu
    public Menus buildMenuBar() {
        return menus;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectEditorConstants.INSTANCE.KModuleDotXml();
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return kModuleEditorPanel.asWidget();
    }

}



