/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.fields;

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.forms.migration.legacy.model.Field;
import org.kie.workbench.common.forms.migration.tool.FormMigrationSummary;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.FieldAdapter;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormLayoutComponent;
import org.kie.workbench.common.forms.model.HasPlaceHolder;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public abstract class AbstractFieldAdapter implements FieldAdapter {

    public static final String DRAGGABLE_TYPE = "org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent";

    @Override
    public void parseField(Field originalField, FormMigrationSummary formSummary, FormDefinition newFormDefinition, Consumer<LayoutComponent> layoutElementConsumer) {
        FieldDefinition fieldDefinition = getFieldDefinition(originalField);

        fieldDefinition.setId(String.valueOf(originalField.getId()));
        fieldDefinition.setName(originalField.getFieldName());
        fieldDefinition.setLabel(lookupI18nValue(originalField.getLabel()));
        fieldDefinition.setHelpMessage(lookupI18nValue(originalField.getTitle()));
        fieldDefinition.setStandaloneClassName(StringUtils.defaultIfBlank(originalField.getBag(), originalField.getFieldType().getFieldClass()));
        fieldDefinition.setReadOnly(originalField.getReadonly());
        fieldDefinition.setRequired(originalField.getFieldRequired());

        String binding = StringUtils.defaultString(StringUtils.defaultIfBlank(originalField.getInputBinding(), originalField.getOutputBinding()));

        if (!StringUtils.isEmpty(binding)) {
            if (binding.contains("/")) {
                binding = binding.substring(binding.indexOf("/") + 1);
            }

            ModelPropertyImpl property = new ModelPropertyImpl(binding, fieldDefinition.getFieldTypeInfo());
            newFormDefinition.getModel().getProperties().add(property);
        }

        fieldDefinition.setBinding(binding);

        if(fieldDefinition instanceof HasPlaceHolder) {
            ((HasPlaceHolder)fieldDefinition).setPlaceHolder(fieldDefinition.getLabel());
        }

        newFormDefinition.getFields().add(fieldDefinition);
        LayoutComponent component = new LayoutComponent(DRAGGABLE_TYPE);
        component.addProperty(FormLayoutComponent.FORM_ID,
                              newFormDefinition.getId());
        component.addProperty(FormLayoutComponent.FIELD_ID,
                              fieldDefinition.getId());

        layoutElementConsumer.accept(component);
    }

    protected abstract FieldDefinition getFieldDefinition(Field originalField);
}
