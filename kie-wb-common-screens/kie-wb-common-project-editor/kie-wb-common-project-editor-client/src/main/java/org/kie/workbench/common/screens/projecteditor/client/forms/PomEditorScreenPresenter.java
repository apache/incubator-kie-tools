package org.kie.workbench.common.screens.projecteditor.client.forms;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.workbench.common.screens.projecteditor.client.type.POMResourceType;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.uberfire.backend.vfs.Path;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@WorkbenchEditor(identifier = "pomScreen", supportedTypes = {POMResourceType.class})
public class PomEditorScreenPresenter {

    private Caller<POMService> pomService;
    private POMEditorPanel pomEditorPanel;
    private FileMenuBuilder menuBuilder;
    private Menus menus;
    private Path path;
    private boolean isReadOnly;
    private PomEditorScreenView view;
    private POM model;


    public PomEditorScreenPresenter() {
    }

    @Inject
    public PomEditorScreenPresenter(POMEditorPanel pomEditorPanel,
                                    PomEditorScreenView view,
                                    Caller<POMService> pomService,
                                    FileMenuBuilder menuBuilder) {
        this.pomEditorPanel = pomEditorPanel;
        this.view = view;
        this.pomService=pomService;
        this.menuBuilder = menuBuilder;
    }

    @OnStartup
    public void init(final Path path,
                     final PlaceRequest request) {
        this.path = path;
        this.isReadOnly = request.getParameter("readOnly", null) == null ? false : true;

        this.path = path;

        pomService.call(getModelSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback(view)).load(path);

        fillMenuBar();
    }

    private RemoteCallback<POM> getModelSuccessCallback() {
        return new RemoteCallback<POM>() {

            @Override
            public void callback(final POM model) {
                PomEditorScreenPresenter.this.model = model;
                pomEditorPanel.setPOM(model, false);
            }
        };
    }

    public void save(final String commitMessage,
                     final Command callback,
                     final Metadata metadata) {
        //Busy popup is handled by ProjectEditorScreen
        pomService.call(getSaveSuccessCallback(callback),
                new HasBusyIndicatorDefaultErrorCallback(view)).save(path,
                model,
                metadata,
                commitMessage);
    }

    private RemoteCallback<Path> getSaveSuccessCallback(final Command callback) {
        return new RemoteCallback<Path>() {

            @Override
            public void callback(final Path path) {
                callback.execute();
                view.showSaveSuccessful("pom.xml");
            }
        };
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
