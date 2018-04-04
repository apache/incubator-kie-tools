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
package org.kie.workbench.common.forms.editor.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasPlaceHolder;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.uberfire.commons.data.Pair;

@Dependent
public class FormEditorHelper {

    public static final String UNBOUND_FIELD_NAME_PREFFIX = "__unbound_field_";

    private FieldManager fieldManager;

    private ManagedInstance<EditorFieldLayoutComponent> editorFieldLayoutComponents;

    private FormModelerContent content;

    private Map<String, FieldDefinition> availableFields = new HashMap<>();

    protected Map<String, Pair<EditorFieldLayoutComponent, FieldDefinition>> unbindedFields = new HashMap<>();

    protected Collection<FieldType> enabledPaletteFieldTypes = new ArrayList<>();
    protected Collection<FieldType> enabledFieldPropertiesFieldTypes = new ArrayList<>();

    @PostConstruct
    public void init() {
        Collection<SyncBeanDef<EditorFieldTypesProvider>> providers = IOC.getBeanManager().lookupBeans(EditorFieldTypesProvider.class);
        providers.stream().map(SyncBeanDef::getInstance)
                .sorted(Comparator.comparingInt(EditorFieldTypesProvider::getPriority))
                .forEach((EditorFieldTypesProvider editorProvider) -> {
                    enabledPaletteFieldTypes.addAll(editorProvider.getPaletteFieldTypes());
                    enabledFieldPropertiesFieldTypes.addAll(editorProvider.getFieldPropertiesFieldTypes());
                });
    }

    @Inject
    public FormEditorHelper(FieldManager fieldManager,
                            ManagedInstance<EditorFieldLayoutComponent> editorFieldLayoutComponents) {
        this.fieldManager = fieldManager;
        this.editorFieldLayoutComponents = editorFieldLayoutComponents;
    }

    public FormModelerContent getContent() {
        return content;
    }

    public void initHelper(FormModelerContent content) {
        this.content = content;

        if (unbindedFields != null && !unbindedFields.isEmpty()) {
            return;
        }

        for (FieldType baseType : enabledPaletteFieldTypes) {
            EditorFieldLayoutComponent layoutComponent = editorFieldLayoutComponents.get();
            if (layoutComponent != null) {
                FieldDefinition field = fieldManager.getDefinitionByFieldType(baseType);
                field.setName(generateUnboundFieldName(field));

                layoutComponent.init(content.getRenderingContext(),
                                     field);

                unbindedFields.put(field.getId(),
                                   new Pair<>(layoutComponent,
                                              field));
            }
        }
        addAvailableFields();
    }

    public FormDefinition getFormDefinition() {
        return content.getDefinition();
    }

    public FormModel getFormModel() {
        return getFormDefinition().getModel();
    }

    public void addAvailableFields() {
        FormModel model = getFormModel();
        FormDefinition formDefinition = getFormDefinition();

        model.getProperties().forEach(modelProperty -> {
            if (formDefinition.getFieldByBinding(modelProperty.getName()) == null) {
                addAvailableField(fieldManager.getDefinitionByModelProperty(modelProperty));
            }
        });
    }

    public void addAvailableField(FieldDefinition field) {
        if (modelContainsField(field)) {
            availableFields.put(field.getId(),
                                field);
        }
    }

    public void removeAvailableField(FieldDefinition field) {
        availableFields.remove(field.getId());
    }

    public FieldDefinition getFormField(String fieldId) {
        FieldDefinition result = content.getDefinition().getFieldById(fieldId);
        if (result == null) {

            result = availableFields.get(fieldId);

            if (result == null) {
                if (unbindedFields.containsKey(fieldId)) {
                    Pair<EditorFieldLayoutComponent, FieldDefinition> pair = unbindedFields.get(fieldId);

                    result = pair.getK2();

                    result.setLabel(result.getFieldType().getTypeName());
                    if (result instanceof HasPlaceHolder) {
                        ((HasPlaceHolder) result).setPlaceHolder(result.getFieldType().getTypeName());
                    }

                    unbindedFields.remove(result.getId());

                    FieldDefinition newField = fieldManager.getDefinitionByFieldType(result.getFieldType());
                    newField.setName(generateUnboundFieldName(newField));

                    EditorFieldLayoutComponent component = pair.getK1();

                    component.init(content.getRenderingContext(),
                                   newField);

                    unbindedFields.put(newField.getId(),
                                       new Pair<>(component,
                                                  newField));
                }
            }
        }
        return result;
    }

    public void removeField(String fieldId,
                            boolean addToAvailables) {

        FormDefinition formDefinition = content.getDefinition();

        FieldDefinition fieldToRemove = formDefinition.getFieldById(fieldId);

        if (fieldToRemove != null) {

            formDefinition.getFields().remove(fieldToRemove);

            if (addToAvailables) {

                FieldDefinition originalField = fieldToRemove;

                if(fieldToRemove.getBinding() == null) {
                    return;
                }

                ModelProperty property = formDefinition.getModel().getProperty(fieldToRemove.getBinding());

                fieldToRemove = fieldManager.getDefinitionByModelProperty(property);

                fieldToRemove.setId(originalField.getId());

                availableFields.put(fieldToRemove.getId(),
                                    fieldToRemove);
            }
        }
    }

    public boolean modelContainsField(FieldDefinition fieldDefinition) {
        FormModel formModel = getFormModel();

        return formModel.getProperty(fieldDefinition.getBinding()) != null;
    }

    public List<String> getCompatibleModelFields(FieldDefinition field) {
        return availableFields.values()
                .stream()
                .filter(availableField -> availableField.getFieldTypeInfo().equals(field.getFieldTypeInfo()))
                .map(FieldDefinition::getBinding)
                .collect(Collectors.toList());
    }

    public List<String> getCompatibleFieldTypes(FieldDefinition field) {
        List<String> editorFieldTypeCodes = enabledFieldPropertiesFieldTypes.stream().map(FieldType::getTypeName).collect(Collectors.toList());
        return fieldManager.getCompatibleFields(field).stream().filter((fieldCode) -> editorFieldTypeCodes.contains(fieldCode))
                .collect(Collectors.toList());
    }

    public FieldDefinition switchToField(FieldDefinition originalField,
                                         String newBinding) {

        if (newBinding != null && !"".equals(newBinding)) {
            Optional<FieldDefinition> availableFieldOptional = availableFields.values()
                    .stream()
                    .filter(availableField -> availableField.getBinding().equals(newBinding)).findFirst();

            if (availableFieldOptional.isPresent()) {
                FieldDefinition availableField = availableFieldOptional.get();

                FieldDefinition resultField = fieldManager.getFieldFromProvider(originalField.getFieldType().getTypeName(),
                                                                                availableField.getFieldTypeInfo());

                if (resultField == null) {
                    // this happens when trying to bind to a property which is of an unsupported type for the current FieldFefintion
                    resultField = fieldManager.getFieldFromProvider(availableField.getFieldType().getTypeName(),
                                                                    availableField.getFieldTypeInfo());
                }

                resultField.copyFrom(originalField);

                resultField.setId(availableField.getId());
                resultField.setName(availableField.getName());
                resultField.setStandaloneClassName(availableField.getStandaloneClassName());
                resultField.setBinding(newBinding);

                return resultField;
            }
        }

        // If we arrive here is because we have a dynamic binding or we are unbinding a field
        FieldDefinition resultField = fieldManager.getFieldFromProvider(originalField.getFieldType().getTypeName(), originalField.getFieldTypeInfo());

        if (newBinding == null || newBinding.equals("")) {
            // unbinding a field
            resultField.setName(generateUnboundFieldName(resultField));
            resultField.setBinding("");
        }

        resultField.copyFrom(originalField);
        resultField.setBinding(newBinding);

        if (resultField.getName() == null) {
            // edge case we only get here with dynamic bindings
            resultField.setName(newBinding);
        }

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

        return resultDefinition;
    }

    public void saveFormField(FieldDefinition originalField,
                              FieldDefinition fieldCopy) {
        if (originalField.getBinding() != null && !originalField.getBinding().equals(fieldCopy.getBinding())) {
            addAvailableField(originalField);
        }
        if (fieldCopy.getBinding() != null && !fieldCopy.getBinding().isEmpty()) {
            removeAvailableField(fieldCopy);
        }
        content.getDefinition().getFields().remove(originalField);
        content.getDefinition().getFields().add(fieldCopy);
    }

    public String generateUnboundFieldName(FieldDefinition field) {
        return UNBOUND_FIELD_NAME_PREFFIX + field.getId();
    }

    public Collection<EditorFieldLayoutComponent> getBaseFieldsDraggables() {
        return unbindedFields.values().stream().map(Pair::getK1).collect(Collectors.toList());
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
