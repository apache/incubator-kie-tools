package org.kie.workbench.common.screens.projecteditor.client.forms;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.projecteditor.client.widgets.ListFormComboPanel;
import org.kie.workbench.common.services.project.service.KModuleService;
import org.kie.workbench.common.services.project.service.model.KBaseModel;
import org.kie.workbench.common.services.project.service.model.KModuleModel;
import org.kie.workbench.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.popups.text.FormPopup;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;

import javax.inject.Inject;

public class KModuleEditorPanel
        extends ListFormComboPanel<KBaseModel> {

    private KModuleModel model;

    private final KModuleEditorPanelView view;
    private boolean hasBeenInitialized = false;

    @Inject
    public KModuleEditorPanel(KBaseForm form,
                              FormPopup namePopup,
                              KModuleEditorPanelView view) {
        super(view, form, namePopup);

        this.view = view;
    }

    public void setData(KModuleModel model, boolean isReadOnly) {
        this.model = model;

        if (isReadOnly) {
            view.makeReadOnly();
        }

        setItems(model.getKBases());
        hasBeenInitialized = true;
    }

    @Override
    protected KBaseModel createNew(String name) {
        KBaseModel model = new KBaseModel();
        model.setName(name);
        return model;
    }

    public boolean hasBeenInitialized() {
        return hasBeenInitialized;
    }
}
