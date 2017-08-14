/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.comboBoxEditor;

import java.util.List;
import java.util.Objects;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeListener;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.comboBoxEditor.annotation.FixedValues;
import org.kie.workbench.common.stunner.bpmn.forms.model.ConditionalComboBoxFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.util.MultipleFieldStringSerializer;
import org.kie.workbench.common.stunner.core.client.definition.adapter.binding.ClientBindingUtils;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;

/**
 * Render a {@link ConditionalComboBoxFieldDefinition} based on a relatedField with condition to be {@code not empty} or {@code null} to be editable.
 */
@Dependent
public class ConditionalComboBoxFieldRenderer extends AbstractComboBoxFieldRenderer<ConditionalComboBoxFieldDefinition> {

  public static final String TYPE_NAME = ConditionalComboBoxFieldDefinition.FIELD_TYPE.getTypeName();

  private AdapterManager adapterManager;

  @Inject
  public ConditionalComboBoxFieldRenderer(@FixedValues final ComboBoxFixedValuesWidgetView comboBoxEditor,
                                          AdapterManager adapterManager) {
    super(comboBoxEditor);
    this.adapterManager = adapterManager;
  }

  @Override
  public void init(final FormRenderingContext renderingContext,
                   ConditionalComboBoxFieldDefinition field) {
    super.init(renderingContext,
               field);
    initializeRelatedFieldCondition(renderingContext,
                                    field);
  }

  private void initializeRelatedFieldCondition(FormRenderingContext renderingContext,
                                               ConditionalComboBoxFieldDefinition field) {
    if (Objects.nonNull(field.getRelatedField())) {
      List<String> fields = extractFields(field);
      if (Objects.isNull(field) || fields.isEmpty()) {
        return;
      }
      initializeListeners(fields);
      checkCurrentRelatedFieldValues(renderingContext,
                                     fields);
    }
  }

  private void checkCurrentRelatedFieldValues(FormRenderingContext renderingContext,
                                              List<String> fields) {
    final Object formModel = renderingContext.getModel();
    setReadOnly(fields.stream().allMatch(f -> {
      Object relatedFieldDefinition = ClientBindingUtils.getProxiedValue(formModel,
                                                                         f);
      return verifyReadOnlyCondition(adapterManager.forProperty().getValue(relatedFieldDefinition));
    }));
  }

  private void initializeListeners(List<String> fields) {
    fields.forEach(f -> fieldChangeListeners.add(
        new FieldChangeListener(f,
                                (name, value) -> refreshFieldCondition(value)))

    );
  }

  private List<String> extractFields(ConditionalComboBoxFieldDefinition field) {
    return MultipleFieldStringSerializer.deserialize(field.getRelatedField());
  }

  public void refreshFieldCondition(Object conditionValue) {
    boolean readOnly = verifyReadOnlyCondition(conditionValue);
    setReadOnly(readOnly);
  }

  private boolean verifyReadOnlyCondition(Object conditionValue) {
    return Objects.isNull(conditionValue) || String.valueOf(conditionValue).trim().isEmpty();
  }

  @Override
  public String getName() {
    return TYPE_NAME;
  }

  @Override
  public String getSupportedCode() {
    return TYPE_NAME;
  }

  @Override
  public Class<ConditionalComboBoxFieldDefinition> getSupportedFieldDefinition() {
    return ConditionalComboBoxFieldDefinition.class;
  }
}