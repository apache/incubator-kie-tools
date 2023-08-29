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
import java.util.Objects;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.rendering.Renderer;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeListener;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.comboBoxEditor.annotation.FixedValues;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.forms.model.ConditionalComboBoxFieldDefinition;
import org.kie.workbench.common.stunner.bpmn.util.MultipleFieldStringSerializer;
import org.kie.workbench.common.stunner.core.client.definition.adapter.binding.ClientBindingUtils;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;

/**
 * Render a {@link ConditionalComboBoxFieldDefinition} based on a relatedField with condition to be {@code not empty} or {@code null} to be editable.
 * The relatedField should can be just one simple field e.g. "name", or it can be a list of fields separated by a delimiter {@code ;} e.g. "documentation;name".
 * Each relatedField can be composed by an hierarchy that is a sequence of sub-fields that should be separated by  a delimiter {@code .} e.g. "general.name"
 */
@Dependent
@Renderer(fieldDefinition = ConditionalComboBoxFieldDefinition.class)
public class ConditionalComboBoxFieldRenderer extends AbstractComboBoxFieldRenderer<ConditionalComboBoxFieldDefinition> {

    public static final String TYPE_NAME = ConditionalComboBoxFieldDefinition.FIELD_TYPE.getTypeName();

    private AdapterManager adapterManager;

    @Inject
    public ConditionalComboBoxFieldRenderer(@FixedValues final ComboBoxFixedValuesWidgetView comboBoxEditor,
                                            final ClientTranslationService translationService,
                                            final AdapterManager adapterManager) {
        super(comboBoxEditor, translationService);
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

    private Object getModelFromFormContext(FormRenderingContext renderingContext) {
        if (Objects.isNull(renderingContext.getModel()) && Objects.nonNull(renderingContext.getParentContext())) {
            return getModelFromFormContext(renderingContext.getParentContext());
        }
        return renderingContext.getModel();
    }

    private void checkCurrentRelatedFieldValues(FormRenderingContext renderingContext,
                                                List<String> fields) {
        final Object formModel = getModelFromFormContext(renderingContext);

        setReadOnly(fields.stream().allMatch(f -> {
            Object relatedFieldDefinition = getFormFieldProxiedDefinition(formModel,
                                                                          extractSubFields(f));
            return verifyReadOnlyCondition(adapterManager.forProperty().getValue(relatedFieldDefinition));
        }));
    }

    private Object getFormFieldProxiedDefinition(Object formModel,
                                                 String... fieldSequence) {
        if (fieldSequence == null || fieldSequence.length == 0) {
            throw new IllegalArgumentException("Empty fields to get from model");
        }
        String firstField = Stream.of(fieldSequence).findFirst().get();
        Object proxiedDefinition = ClientBindingUtils.getProxiedValue(formModel,
                                                                      firstField);
        if (proxiedDefinition instanceof BPMNPropertySet) {
            return getFormFieldProxiedDefinition(proxiedDefinition,
                                                 Stream.of(fieldSequence).filter(f -> !Objects.equals(f,
                                                                                                      firstField)).toArray(String[]::new));
        }
        return proxiedDefinition;
    }

    private void initializeListeners(List<String> fields) {
        fields.forEach(field -> fieldChangeListeners.add(
                new FieldChangeListener(extractLastSubField(field),
                                        (name, value) -> refreshFieldCondition(value)))

        );
    }

    private String extractLastSubField(String field) {
        String[] extractedSubFields = extractSubFields(field);
        return extractedSubFields[extractedSubFields.length - 1];
    }

    private String[] extractSubFields(String field) {
        return MultipleFieldStringSerializer.deserializeSubfields(field).stream().toArray(String[]::new);
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
}
