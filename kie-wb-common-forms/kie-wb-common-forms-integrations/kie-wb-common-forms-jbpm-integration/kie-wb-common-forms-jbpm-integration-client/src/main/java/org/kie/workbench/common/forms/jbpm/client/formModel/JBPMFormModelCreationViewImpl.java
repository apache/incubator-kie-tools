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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.text.shared.Renderer;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.jbpm.client.resources.i18n.Constants;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMProcessModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;

@Templated
public class JBPMFormModelCreationViewImpl implements JBPMFormModelCreationView,
                                                      IsElement {

    @DataField
    DivElement formGroup = Document.get().createDivElement();

    @DataField
    DivElement modelHelpBlock = Document.get().createDivElement();

    @DataField
    protected ValueListBox<JBPMProcessModel> processes = new ValueListBox(new Renderer<JBPMProcessModel>() {
        @Override
        public String render(JBPMProcessModel model) {
            if (model == null) {
                return "";
            }
            return model.getProcessFormModel().getProcessName();
        }

        @Override
        public void render(JBPMProcessModel model,
                           Appendable appendable) throws IOException {
            appendable.append(render(model));
        }
    });

    @DataField
    protected ValueListBox<JBPMFormModel> tasks = new ValueListBox<>(new Renderer<JBPMFormModel>() {
        @Override
        public String render(JBPMFormModel model) {
            String result = "";

            if (model != null) {
                if (model instanceof BusinessProcessFormModel) {
                    result = translationService.format(Constants.JBPMFormModelCreationViewImplStartProcessForm,
                                                       model.getFormName());
                } else if (model instanceof TaskFormModel) {
                    result = translationService.format(Constants.JBPMFormModelCreationViewImplTaskName,
                                                       ((TaskFormModel) model).getTaskName(),
                                                       model.getFormName());
                }
            }

            return result;
        }

        @Override
        public void render(JBPMFormModel object,
                           Appendable appendable) throws IOException {
            appendable.append(render(object));
        }
    });

    protected TranslationService translationService;

    protected Presenter presenter;

    @Inject
    public JBPMFormModelCreationViewImpl(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostConstruct
    protected void init() {
        processes.addValueChangeHandler(event -> {
            JBPMProcessModel model = processes.getValue();
            if (model != null) {
                List<JBPMFormModel> models = new ArrayList<JBPMFormModel>();
                models.add(model.getProcessFormModel());
                models.addAll(model.getTaskFormModels());
                tasks.setValue(null);
                tasks.setAcceptableValues(models);
            } else {
                tasks.setAcceptableValues(new ArrayList<>());
                tasks.setValue(null,
                               true);
            }
        });

        tasks.addValueChangeHandler(event -> {
            presenter.setModel(event.getValue());
            clearValidationErrors();
        });
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setProcessModels(List<JBPMProcessModel> processModels) {
        processes.setValue(null);
        processes.setAcceptableValues(processModels);
    }

    @Override
    public void reset() {
        processes.setValue(null);
        tasks.setValue(null);
        tasks.setAcceptableValues(new ArrayList<>());
        clearValidationErrors();
    }

    @Override
    public void clearValidationErrors() {
        formGroup.removeClassName(ValidationState.ERROR.getCssName());
        modelHelpBlock.setInnerText("");
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        formGroup.addClassName(ValidationState.ERROR.getCssName());
        modelHelpBlock.setInnerText(errorMessage);
    }
}
