/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.comboBoxEditor;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.SelectorFieldRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerBPMNConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.forms.model.ComboBoxFieldDefinition;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

@Dependent
public abstract class AbstractComboBoxFieldRenderer<T extends ComboBoxFieldDefinition>
        extends SelectorFieldRenderer<T, StringSelectorOption, String> {

    private ComboBoxWidgetView view;

    private ClientTranslationService translationService;

    private ListBoxValues valueListBoxValues;

    @Inject
    public AbstractComboBoxFieldRenderer(final ComboBoxWidgetView comboBoxEditor, final ClientTranslationService translationService) {
        this.view = comboBoxEditor;
        this.translationService = translationService;
    }

    @Override
    protected void refreshInput(Map<String, String> optionsValues,
                                String defaultValue) {
        setComboBoxValues(optionsValues);
    }

    protected void setComboBoxValues(final List<String> values) {
        valueListBoxValues = createDefaultListBoxValues();
        valueListBoxValues.addValues(values);
        view.setComboBoxValues(valueListBoxValues);
    }

    protected void setComboBoxValues(final Map<String, String> values) {
        valueListBoxValues = createDefaultListBoxValues();

        valueListBoxValues.addValues(values);
        view.setComboBoxValues(valueListBoxValues);
    }

    private ListBoxValues createDefaultListBoxValues() {
        return new ListBoxValues(ComboBoxWidgetView.CUSTOM_PROMPT,
                                 translationService.getValue(StunnerBPMNConstants.EDIT) + " ",
                                 null);
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup = formGroupsInstance.get();

        view.setReadOnly(field.getReadOnly());
        refreshSelectorOptions();

        formGroup.render(view.asWidget(), field);
        return formGroup;
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        view.setReadOnly(readOnly);
        field.setReadOnly(readOnly);
    }
}
