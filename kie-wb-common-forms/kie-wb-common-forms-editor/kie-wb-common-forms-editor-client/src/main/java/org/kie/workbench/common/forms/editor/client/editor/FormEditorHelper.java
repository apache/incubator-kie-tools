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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.kie.workbench.common.forms.editor.client.EditorFieldTypesProvider;
import org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasPlaceHolder;
import org.kie.workbench.common.forms.model.DynamicModel;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.model.HasFormModelProperties;
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

    protected Collection<FieldType> editorFieldTypes = new ArrayList<>();

    @PostConstruct
    public void init() {
        Collection<SyncBeanDef<EditorFieldTypesProvider>> providers = IOC.getBeanManager().lookupBeans(EditorFieldTypesProvider.class);
        providers.stream().map(SyncBeanDef::getInstance)
                .sorted((EditorFieldTypesProvider providerA, EditorFieldTypesProvider providerB) -> providerA.getPriority() - providerB.getPriority())
                .forEach((EditorFieldTypesProvider editorProvider) -> editorFieldTypes.addAll(editorProvider.getFieldTypes()));
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

        for (FieldType baseType : editorFieldTypes) {
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
        addAvailableFields(content.getAvailableFields());
    }

    public FormDefinition getFormDefinition() {
        return content.getDefinition();
    }

    public FormModel getFormModel() {
        return getFormDefinition().getModel();
    }

    public void addAvailableFields(List<FieldDefinition> fields) {
        for (FieldDefinition field : fields) {
            addAvailableField(field);
        }
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
            if (result != null) {
                availableFields.remove(fieldId);
            } else {
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
                if (addToAvailables && modelContainsField(field)) {
                    availableFields.put(field.getId(),
                                        field);
                }
                return field;
            }
        }
        return null;
    }

    public boolean modelContainsField(FieldDefinition fieldDefinition) {
        FormModel formModel = getFormModel();

        if (formModel instanceof HasFormModelProperties) {
            return ((HasFormModelProperties) formModel).getProperty(fieldDefinition.getBinding()) != null;
        }
        return false;
    }

    public List<String> getCompatibleModelFields(FieldDefinition field) {
        Collection<String> compatibles = fieldManager.getCompatibleTypes(field);

        Set<String> result = new TreeSet<>();
        if (field.getBinding() != null && !field.getBinding().isEmpty()) {
            result.add(field.getBinding());
        }

        availableFields.values().stream().filter(availableField -> compatibles.contains(availableField.getStandaloneClassName())).forEach(availableField -> result.add(availableField.getBinding()));

        return new ArrayList<>(result);
    }

    public List<String> getCompatibleFieldTypes(FieldDefinition field) {
        List<String> editorFieldTypeCodes = editorFieldTypes.stream().map(FieldType::getTypeName).collect(Collectors.toList());
        return fieldManager.getCompatibleFields(field).stream().filter((fieldCode) -> editorFieldTypeCodes.contains(fieldCode))
                .collect(Collectors.toList());
    }

    public FieldDefinition switchToField(FieldDefinition originalField,
                                         String bindingExpression) {

        FieldDefinition resultField = fieldManager.getDefinitionByFieldTypeName(originalField.getFieldType().getTypeName());

        if (bindingExpression == null || bindingExpression.equals("") || content.getDefinition().getFieldByBinding(bindingExpression) != null) {
            resultField.setName(generateUnboundFieldName(resultField));
            resultField.setBinding("");
        } else {
            // Search if there's an available field with the specified binding
            for (Iterator<FieldDefinition> it = availableFields.values().iterator(); it.hasNext(); ) {
                FieldDefinition availableField = it.next();
                if (availableField.getBinding().equals(bindingExpression)) {

                    // Check types if we are binding a fields on dynamicModel && change field type if needed
                    if (content.getDefinition().getModel() instanceof DynamicModel && !resultField.getFieldType().equals(availableField.getFieldType())) {
                        resultField = fieldManager.getFieldFromProvider(availableField.getFieldType().getTypeName(),
                                                                        availableField.getFieldTypeInfo());
                    }

                    resultField.setId(availableField.getId());
                    resultField.setName(availableField.getName());

                    resultField.copyFrom(availableField);

                    return resultField;
                }
            }
        }

        // If we arrive here is because we have a dynamic binding or we are unbinding a field
        resultField.copyFrom(originalField);
        resultField.setBinding(bindingExpression);

        if (resultField.getName() == null) {
            String name = bindingExpression;
            if (name == null || name.isEmpty()) {
                name = generateUnboundFieldName(resultField);
            }
            resultField.setName(name);
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
