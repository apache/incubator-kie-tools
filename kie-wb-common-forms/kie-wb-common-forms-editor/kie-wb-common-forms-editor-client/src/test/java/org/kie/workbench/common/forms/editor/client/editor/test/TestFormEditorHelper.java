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

package org.kie.workbench.common.forms.editor.client.editor.test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.forms.editor.client.editor.FormEditorHelper;
import org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;
import org.uberfire.commons.data.Pair;

public class TestFormEditorHelper extends FormEditorHelper {

    public TestFormEditorHelper(TestFieldManager fieldManager,
                                ManagedInstance<EditorFieldLayoutComponent> editorFieldLayoutComponents,
                                SyncBeanManager beanManager) {
        super(fieldManager, editorFieldLayoutComponents, beanManager);
        fieldManager.getAllBasicTypeProviders().stream().forEach((provider) -> {
            enabledPaletteFieldTypes.add(provider.getDefaultField().getFieldType());
            enabledFieldPropertiesFieldTypes.add(provider.getDefaultField().getFieldType());
        });
    }

    public Map<String, Pair<EditorFieldLayoutComponent, FieldDefinition>> getUnbindedFields() {
        return unbindedFields;
    }

    public Collection<FieldType> getEditorFieldTypes() {
        return enabledPaletteFieldTypes;
    }

    public void addAvailableFields(List<FieldDefinition> availableFields) {
        availableFields.forEach(this::addAvailableField);
    }
}
