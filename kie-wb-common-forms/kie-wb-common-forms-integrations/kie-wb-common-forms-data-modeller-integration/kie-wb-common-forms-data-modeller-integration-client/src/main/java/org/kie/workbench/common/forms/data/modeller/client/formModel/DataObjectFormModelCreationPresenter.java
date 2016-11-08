/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.data.modeller.client.formModel;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.data.modeller.client.resources.i18n.DataModellerIntegrationConstants;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.service.DataObjectFinderService;
import org.kie.workbench.common.forms.editor.client.handler.formModel.FormModelCreationView;
import org.uberfire.backend.vfs.Path;

@Dependent
public class DataObjectFormModelCreationPresenter implements FormModelCreationView<DataObjectFormModel> {

    protected Caller<DataObjectFinderService> finderService;

    protected DataObjectFormModelCreationView view;

    protected TranslationService translationService;

    @Inject
    public DataObjectFormModelCreationPresenter( Caller<DataObjectFinderService> finderService,
                                                 DataObjectFormModelCreationView view,
                                                 TranslationService translationService ) {
        this.finderService = finderService;
        this.view = view;
        this.translationService = translationService;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void init( Path projectPath ) {
        finderService.call( dataObjectFormModels -> view.setFormModels( (List<DataObjectFormModel>)dataObjectFormModels ) ).getAvailableDataObjects(
                projectPath );
    }

    @Override
    public DataObjectFormModel getFormModel() {
        return view.getSelectedFormModel();
    }

    @Override
    public boolean isValid() {
        return view.isValid();
    }

    @Override
    public String getLabel() {
        return translationService.getTranslation( DataModellerIntegrationConstants.DataObject );
    }

    @Override
    public void reset() {
        view.reset();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
