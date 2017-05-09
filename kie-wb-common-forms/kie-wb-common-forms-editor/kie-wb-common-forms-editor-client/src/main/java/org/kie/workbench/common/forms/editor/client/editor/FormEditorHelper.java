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
package org.kie.workbench.common.forms.editor.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorContextRequest;
import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorContextResponse;
import org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasPlaceHolder;
import org.kie.workbench.common.forms.model.DynamicModel;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.service.FieldManager;

@Dependent
public class FormEditorHelper {

    public static final String UNBINDED_FIELD_NAME_PREFFIX = "__unbinded_field_";

    private FieldManager fieldManager;

    private Event<FormEditorContextResponse> responseEvent;

    private ManagedInstance<EditorFieldLayoutComponent> editorFieldLayoutComponents;

    private FormModelerContent content;

    private Map<String, FieldDefinition> availableFields = new HashMap<String, FieldDefinition>();

    private List<EditorFieldLayoutComponent> fieldLayoutComponents;

    @Inject
    public FormEditorHelper(FieldManager fieldManager,
                            Event<FormEditorContextResponse> responseEvent,
                            ManagedInstance<EditorFieldLayoutComponent> editorFieldLayoutComponents) {
        this.fieldManager = fieldManager;
        this.responseEvent = responseEvent;
        this.editorFieldLayoutComponents = editorFieldLayoutComponents;
    }

    public FormModelerContent getContent() {
        return content;
    }

    public void initHelper(FormModelerContent content) {
        this.content = content;

        if (fieldLayoutComponents != null && !fieldLayoutComponents.isEmpty()) {
            return;
        }
        fieldLayoutComponents = new ArrayList<>();

        for (String baseType : fieldManager.getBaseFieldTypes()) {
            EditorFieldLayoutComponent layoutComponent = editorFieldLayoutComponents.get();
            if (layoutComponent != null) {
                FieldDefinition field = fieldManager.getDefinitionByFieldTypeName(baseType);
                field.setId(baseType);
                layoutComponent.init(content.getRenderingContext(),
                                     field);
                layoutComponent.setDisabled(true);
                fieldLayoutComponents.add(layoutComponent);
            }
        }
    }

    public FormDefinition getFormDefinition() {
        return content.getDefinition();
    }

    public void addAvailableFields(List<FieldDefinition> fields) {
        for (FieldDefinition field : fields) {
            addAvailableField(field);
        }
    }

    public void addAvailableField(FieldDefinition field) {
        availableFields.put(field.getId(),
                            field);
    }

    public FieldDefinition getDroppedField(String fieldId) {
        FieldDefinition result = getFormField(fieldId);

        if (result != null) {
            responseEvent.fire(new FormEditorContextResponse(getFormDefinition().getId(),
                                                             result.getId(),
                                                             this));
        }
        return result;
    }

    public FieldDefinition getFormField(String fieldId) {
        FieldDefinition result = content.getDefinition().getFieldById(fieldId);
        if (result == null) {

            result = availableFields.get(fieldId);
            if (result != null) {
                availableFields.remove(fieldId);
            } else {
                result = fieldManager.getDefinitionByFieldTypeName(fieldId);

                if (result != null) {
                    result.setName(generateUnbindedFieldName(result));
                    result.setLabel(result.getFieldType().getTypeName());
                    if (result instanceof HasPlaceHolder) {
                        ((HasPlaceHolder) result).setPlaceHolder(result.getFieldType().getTypeName());
                    }
                }
            }
            if (result != null) {
                content.getDefinition().getFields().add(result);
            }
        }
        return result;
    }

    public FieldDefinition removeField(String fieldId,
                                       boolean addToAvailables) {
        Iterator<FieldDefinition> it = content.getDefinition().getFields().iterator();

        while (it.hasNext()) {
            FieldDefinition field = it.next();
            if (field.getId().equals(fieldId)) {
                it.remove();
                if (addToAvailables && content.getModelProperties().contains(field.getBinding())) {
                    availableFields.put(field.getId(),
                                        field);
                }
                return field;
            }
        }
        return null;
    }

    public void onFieldRequest(@Observes FormEditorContextRequest request) {
        if (content == null) {
            return;
        }
        if (request.getFormId().equals(content.getDefinition().getId())) {
            responseEvent.fire(new FormEditorContextResponse(request.getFormId(),
                                                             request.getFieldId(),
                                                             this));
        }
    }

    public List<String> getCompatibleModelFields(FieldDefinition field) {
        Collection<String> compatibles = fieldManager.getCompatibleFields(field);

        Set<String> result = new TreeSet<>();
        if (field.getBinding() != null && !field.getBinding().isEmpty()) {
            result.add(field.getBinding());
        }
        for (String compatibleType : compatibles) {
            for (FieldDefinition definition : availableFields.values()) {
                if (definition.getFieldType().getTypeName().equals(compatibleType) && definition.getBinding() != null) {
                    result.add(definition.getBinding());
                }
            }
        }
        return new ArrayList<>(result);
    }

    public Collection<String> getCompatibleFieldTypes(FieldDefinition field) {
        return fieldManager.getCompatibleFields(field);
    }

    public FieldDefinition switchToField(FieldDefinition originalField,
                                         String bindingExpression) {

        if (content.getDefinition().getFieldByBinding(bindingExpression) != null) {
            return null;
        }

        FieldDefinition resultField = fieldManager.getDefinitionByFieldTypeName(originalField.getFieldType().getTypeName());

        if (bindingExpression == null || bindingExpression.equals("")) {
            resultField.setName(generateUnbindedFieldName(resultField));
        } else {
            // Search if there's an available field with the specified binding
            for (Iterator<FieldDefinition> it = availableFields.values().iterator(); it.hasNext(); ) {
                FieldDefinition availableField = it.next();
                if (availableField.getBinding().equals(bindingExpression)) {

                    // Check types if we are binding a fields on dynamicModel && change field type if needed
                    if (content.getDefinition().getModel() instanceof DynamicModel && !resultField.getFieldType().equals(availableField.getFieldType())) {
                        resultField = fieldManager.getFieldFromProvider(availableField.getFieldType().getTypeName(), availableField.getFieldTypeInfo());
                    }

                    resultField.setId(availableField.getId());
                    resultField.setName(availableField.getName());

                    resultField.copyFrom(availableField);

                    content.getDefinition().getFields().add(resultField);

                    it.remove();

                    return resultField;
                }
            }
        }

        // If we arrive here is because we have a dynamic binding or we are unbinding a field
        resultField.copyFrom(originalField);
        resultField.setBinding(bindingExpression);

        if(resultField.getName() == null) {
            String name = bindingExpression;
            if(name == null || name.isEmpty()) {
                name = generateUnbindedFieldName(resultField);
            }
            resultField.setName(name);
        }
        content.getDefinition().getFields().add(resultField);

        return resultField;
    }

    public FieldDefinition switchToFieldType(FieldDefinition field,
                                             String fieldCode) {
        FieldDefinition resultDefinition = fieldManager.getFieldFromProvider(fieldCode,
                                                                             field.getFieldTypeInfo());

        resultDefinition.copyFrom(field);
        resultDefinition.setId(field.getId());
        resultDefinition.setName(field.getName());

        removeField(field.getId(),
                    false);

        content.getDefinition().getFields().add(resultDefinition);

        return resultDefinition;
    }

    public String generateUnbindedFieldName(FieldDefinition field) {
        return UNBINDED_FIELD_NAME_PREFFIX + field.getId();
    }

    public List<EditorFieldLayoutComponent> getBaseFieldsDraggables() {
        return fieldLayoutComponents;
    }

    public Map<String, FieldDefinition> getAvailableFields() {
        return availableFields;
    }

    public FormEditorRenderingContext getRenderingContext() {
        return content.getRenderingContext();
    }

    @PreDestroy
    public void destroy() {
        editorFieldLayoutComponents.destroyAll();
    }
}
