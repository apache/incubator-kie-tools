/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projecteditor.client.forms;

import javax.inject.Inject;

import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.kie.workbench.common.screens.projecteditor.client.widgets.ListFormComboPanel;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopup;

public class KModuleEditorPanel
        extends ListFormComboPanel<KBaseModel> {

    private KModuleEditorPanelView view;

    public KModuleEditorPanel() {
        super(null, null, null);
    }

    @Inject
    public KModuleEditorPanel(KBaseForm form,
                              TextBoxFormPopup namePopup,
                              KModuleEditorPanelView view) {
        super(view, form, namePopup);

        this.view = view;
    }

    public void setData(KModuleModel model, boolean isReadOnly) {

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
