/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.jbpm.client.formModel;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.editor.client.handler.formModel.FormModelCreationViewManager;
import org.kie.workbench.common.forms.jbpm.client.resources.i18n.Constants;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMProcessModel;
import org.kie.workbench.common.forms.jbpm.service.shared.BPMFinderService;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberElement;

@Dependent
public class JBPMFormModelCreationPresenterManager implements FormModelCreationViewManager<JBPMFormModel>,
                                                              JBPMFormModelCreationView.Presenter {

    protected Caller<BPMFinderService> finderService;

    protected JBPMFormModelCreationView view;

    protected TranslationService translationService;

    protected NewResourcePresenter newResourcePresenter;

    private JBPMFormModel model;

    @Inject
    public JBPMFormModelCreationPresenterManager(Caller<BPMFinderService> finderService,
                                                 JBPMFormModelCreationView view,
                                                 TranslationService translationService,
                                                 NewResourcePresenter newResourcePresenter) {
        this.finderService = finderService;
        this.view = view;
        this.translationService = translationService;
        this.newResourcePresenter = newResourcePresenter;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void init(Path projectPath) {
        finderService.call(dataObjectFormModels -> view.setProcessModels((List<JBPMProcessModel>) dataObjectFormModels)).getAvailableProcessModels(
                projectPath);
        model = null;
    }

    @Override
    public JBPMFormModel getFormModel() {
        return model;
    }

    @Override
    public boolean isValid() {
        if (model == null) {
            view.setErrorMessage(translationService.getTranslation(Constants.InvalidFormModel));
            return false;
        }
        view.clearValidationErrors();
        return true;
    }

    @Override
    public String getLabel() {
        return translationService.getTranslation(Constants.Process);
    }

    @Override
    public void reset() {
        view.reset();
    }

    @Override
    public UberElement getView() {
        return view;
    }

    @Override
    public void setModel(JBPMFormModel model) {

        this.model = model;

        if (model != null) {
            newResourcePresenter.setResourceName(model.getFormName());
        }
    }
}
