/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationwizard;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.ValuePairEditorProvider;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.generic.GenericValuePairEditor;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationParseRequest;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationParseResponse;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

@Dependent
public class ValuePairEditorPage
        extends CreateAnnotationWizardPage
        implements ValuePairEditorPageView.Presenter {

    private ValuePairEditorPageView view;

    private ValuePairEditorProvider valuePairEditorProvider;

    private AnnotationValuePairDefinition valuePairDefinition;

    private Object currentValue = null;

    @Inject
    public ValuePairEditorPage(ValuePairEditorPageView view,
                               ValuePairEditorProvider valuePairEditorProvider,
                               Caller<DataModelerService> modelerService,
                               Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent) {
        super(modelerService, wizardPageStatusChangeEvent);
        this.valuePairEditorProvider = valuePairEditorProvider;
        this.view = view;
        view.init(this);
        setTitle("");
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void init(AnnotationDefinition annotationDefinition,
                     AnnotationValuePairDefinition valuePairDefinition, ElementType target, KieModule project) {

        this.annotationDefinition = annotationDefinition;
        setValuePairDefinition(valuePairDefinition);
        this.target = target;
        this.project = project;

        setStatus(isRequired() ? PageStatus.NOT_VALIDATED : PageStatus.VALIDATED);
    }

    public String getStringValue() {
        return view.getStringValue();
    }

    public AnnotationValuePairDefinition getValuePairDefinition() {
        return valuePairDefinition;
    }

    public Object getCurrentValue() {
        return currentValue;
    }

    @Override
    public void onValidate() {
        modelerService.call(getOnValidateValidateSuccessCallback(), new CreateAnnotationWizard.CreateAnnotationWizardErrorCallback())
                .resolveParseRequest(new AnnotationParseRequest(annotationDefinition.getClassName(), target,
                                                                valuePairDefinition.getName(), getStringValue()), project);
    }

    @Override
    public void onValueChange() {
        PageStatus nextStatus = PageStatus.NOT_VALIDATED;
        currentValue = view.getValuePairEditor().getValue();

        if (view.getValuePairEditor() instanceof GenericValuePairEditor) {
            //for the generic editor we should use the validate button

            //available options
            //1) the value pair is required
            //      1.1) then we need a value != null
            //      1.2 that the value is validated.
            //2) the value pair is not required
            //      2.1) if a value != null has been entered, then it should be validated
            //      2.2) if a value == null has been entered, then NO validation is needed

            if (isRequired()) {
                if (isEmpty(currentValue)) {
                    setHelpMessage(Constants.INSTANCE.advanced_domain_wizard_value_pair_editor_page_message_enter_required_value_and_validate());
                } else {
                    setHelpMessage(Constants.INSTANCE.advanced_domain_wizard_value_pair_editor_page_message_value_not_validated());
                }
                nextStatus = PageStatus.NOT_VALIDATED;
            } else {
                if (isEmpty(currentValue)) {
                    setHelpMessage(Constants.INSTANCE.advanced_domain_wizard_value_pair_editor_page_message_enter_optional_value_and_validate());
                    nextStatus = PageStatus.VALIDATED;
                } else {
                    setHelpMessage(Constants.INSTANCE.advanced_domain_wizard_value_pair_editor_page_message_value_not_validated());
                    nextStatus = PageStatus.NOT_VALIDATED;
                }
            }
        } else if (view.getValuePairEditor().isValid() &&
                ((isRequired() && view.getValuePairEditor().getValue() != null) || !isRequired())) {
            nextStatus = PageStatus.VALIDATED;
        }

        setStatus(nextStatus);
    }

    private void setValuePairDefinition(AnnotationValuePairDefinition valuePairDefinition) {
        this.valuePairDefinition = valuePairDefinition;

        String required = isRequired() ? "* " : "";
        setTitle("  -> " + required + valuePairDefinition.getName());

        initValuePairEditor(valuePairDefinition);

        if (view.getValuePairEditor() instanceof GenericValuePairEditor) {
            if (isRequired()) {
                setHelpMessage(Constants.INSTANCE.advanced_domain_wizard_value_pair_editor_page_message_enter_required_value_and_validate());
            } else {
                setHelpMessage(Constants.INSTANCE.advanced_domain_wizard_value_pair_editor_page_message_enter_optional_value_and_validate());
            }
        }
    }

    private void initValuePairEditor(AnnotationValuePairDefinition valuePairDefinition) {
        ValuePairEditor valuePairEditor = valuePairEditorProvider.getValuePairEditor(valuePairDefinition);
        view.setValuePairEditor(valuePairEditor);
    }

    private RemoteCallback<AnnotationParseResponse> getOnValidateValidateSuccessCallback() {
        return new RemoteCallback<AnnotationParseResponse>() {

            @Override
            public void callback(AnnotationParseResponse annotationParseResponse) {
                PageStatus newStatus;

                if (!annotationParseResponse.hasErrors() && annotationParseResponse.getAnnotation() != null) {
                    currentValue = annotationParseResponse.getAnnotation().getValue(valuePairDefinition.getName());
                    newStatus = PageStatus.VALIDATED;
                    setHelpMessage(Constants.INSTANCE.advanced_domain_wizard_value_pair_editor_page_message_value_validated());
                } else {
                    currentValue = null;
                    newStatus = PageStatus.NOT_VALIDATED;
                    String errorMessage = Constants.INSTANCE.advanced_domain_wizard_value_pair_editor_page_message_value_not_validated();
                    errorMessage += "\n" + buildErrorList(annotationParseResponse.getErrors());
                    setHelpMessage(errorMessage);
                }

                setStatus(newStatus);
            }
        };
    }

    private void clearHelpMessage() {
        view.clearHelpMessage();
    }

    private void setHelpMessage(String helpMessage) {
        view.setHelpMessage(helpMessage);
    }

    private boolean isRequired() {
        return valuePairDefinition != null && valuePairDefinition.getDefaultValue() == null;
    }

    private boolean isEmpty(Object value) {
        return value == null || "".equals(value.toString().trim());
    }
}
