package org.kie.workbench.common.screens.projecteditor.client.forms;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.KModuleModel;
import org.guvnor.common.services.project.service.KModuleService;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.workbench.common.screens.projecteditor.client.type.KModuleResourceType;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@WorkbenchEditor(identifier = "kmoduleScreen", supportedTypes = { KModuleResourceType.class })
public class KModuleEditorScreenPresenter {

    private       boolean            isReadOnly;
    private       Path               path;
    private KModuleEditorScreenView view;
    private final KModuleEditorPanel kModuleEditorPanel;
    private       Menus              menus;
    private final FileMenuBuilderImpl menuBuilder;
    private Caller<KModuleService> projectEditorService;

    @Inject
    public KModuleEditorScreenPresenter( KModuleEditorScreenView view,
                                         KModuleEditorPanel kModuleEditorPanel,
                                         FileMenuBuilderImpl menuBuilder,
                                         Caller<KModuleService> projectEditorService) {
        this.view = view;
        this.kModuleEditorPanel = kModuleEditorPanel;
        this.menuBuilder = menuBuilder;
        this.projectEditorService = projectEditorService;
    }

    @OnStartup
    public void init( final Path path,
                      final PlaceRequest request ) {
        this.path = path;
        this.isReadOnly = request.getParameter( "readOnly", null ) == null ? false : true;

        this.path = path;

        //Busy popup is handled by ProjectEditorScreen
        projectEditorService.call(getModelSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback(view)).load(path);


        fillMenuBar();
    }

    private RemoteCallback<KModuleModel> getModelSuccessCallback() {
        return new RemoteCallback<KModuleModel>() {

            @Override
            public void callback(final KModuleModel model) {
                kModuleEditorPanel.setData(model, false);
            }
        };
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



