package org.kie.workbench.common.projecteditor.client.forms;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.services.project.service.model.KBaseModel;
import org.kie.workbench.common.services.project.service.model.KModuleModel;
import org.kie.workbench.common.services.project.service.KModuleService;
import org.kie.workbench.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.popups.text.FormPopup;
import org.kie.workbench.common.projecteditor.client.widgets.ListFormComboPanel;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Command;

import javax.inject.Inject;

public class KModuleEditorPanel
        extends ListFormComboPanel<KBaseModel> {

    private final Caller<KModuleService> projectEditorServiceCaller;

    private KModuleModel model;
    private Path path;

    private final KModuleEditorPanelView view;
    private boolean hasBeenInitialized = false;

    @Inject
    public KModuleEditorPanel(Caller<KModuleService> projectEditorServiceCaller,
                              KBaseForm form,
                              FormPopup namePopup,
                              KModuleEditorPanelView view) {
        super(view, form, namePopup);

        this.projectEditorServiceCaller = projectEditorServiceCaller;
        this.view = view;
    }

    public void init(Path path,
                     boolean readOnly) {
        this.path = path;
        if (readOnly) {
            view.makeReadOnly();
        }
        //Busy popup is handled by ProjectEditorScreen
        projectEditorServiceCaller.call(getModelSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback(view)).load(path);
    }

    private RemoteCallback<KModuleModel> getModelSuccessCallback() {
        return new RemoteCallback<KModuleModel>() {

            @Override
            public void callback(final KModuleModel model) {
                KModuleEditorPanel.this.model = model;
                setItems(model.getKBases());
                hasBeenInitialized = true;
            }
        };
    }

    @Override
    protected KBaseModel createNew(String name) {
        KBaseModel model = new KBaseModel();
        model.setName(name);
        return model;
    }

    public void save(String commitMessage,
                     final Command callback,
                     Metadata metadata) {
        //Busy popup is handled by ProjectEditorScreen
        projectEditorServiceCaller.call(getSaveSuccessCallback(callback),
                new HasBusyIndicatorDefaultErrorCallback(view)).save(path,
                model,
                metadata,
                commitMessage);
    }

    private RemoteCallback<Path> getSaveSuccessCallback(final Command callback) {
        return new RemoteCallback<Path>() {

            @Override
            public void callback(final Path path) {
                view.showSaveSuccessful("kmodule.xml");
                callback.execute();
            }
        };
    }

    public boolean hasBeenInitialized() {
        return hasBeenInitialized;
    }
}
