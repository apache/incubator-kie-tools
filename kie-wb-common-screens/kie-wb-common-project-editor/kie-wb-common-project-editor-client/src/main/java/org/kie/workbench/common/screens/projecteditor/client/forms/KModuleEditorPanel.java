package org.kie.workbench.common.screens.projecteditor.client.forms;

import javax.inject.Inject;

import org.guvnor.common.services.project.model.KBaseModel;
import org.guvnor.common.services.project.model.KModuleModel;
import org.kie.workbench.common.screens.projecteditor.client.widgets.ListFormComboPanel;
import org.kie.workbench.common.services.datamodel.oracle.ProjectDataModelOracle;
import org.kie.workbench.common.widgets.client.popups.text.FormPopup;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopup;

public class KModuleEditorPanel
        extends ListFormComboPanel<KBaseModel> {

    private KModuleModel model;

    private final KModuleEditorPanelView view;

    @Inject
    public KModuleEditorPanel(KBaseForm form,
                              TextBoxFormPopup namePopup,
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
    }

    @Override
    protected KBaseModel createNew(String name) {
        KBaseModel model = new KBaseModel();
        model.setName(name);
        return model;
    }

}
