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

package org.kie.workbench.common.forms.data.modeller.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.editor.model.FormModelSynchronizationResult;
import org.kie.workbench.common.forms.editor.model.TypeConflict;
import org.kie.workbench.common.forms.editor.service.backend.SourceFormModelNotFoundException;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasMaxLength;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.HasPlaceHolder;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DataObjectFormModelHandlerTest extends AbstractModelFinderTest {

    private DataObjectFormModelHandler handler;

    private DataObjectFormModel formModel;

    private FieldManager fieldManager;

    private ModelProperty removedModelProperty = new ModelPropertyImpl("removed", new TypeInfoImpl(Boolean.class.getName()));
    private ModelProperty removedModelProperty2 = new ModelPropertyImpl("removed2", new TypeInfoImpl(String.class.getName()));

    private ModelProperty conflictModelProperty = new ModelPropertyImpl("primitiveInt", new TypeInfoImpl(String.class.getName()));
    private ModelProperty conflictModelProperty2 = new ModelPropertyImpl("primitiveFloat", new TypeInfoImpl(String.class.getName()));

    @BeforeClass
    public static void setUp() throws Exception {
        initialize();

        buildModules("module1", "module2", "module3");
    }

    @Before
    public void init() throws Exception {

        createModel(MODEL_TYPE);

        handler = weldContainer.select(DataObjectFormModelHandler.class).get();
        fieldManager = weldContainer.select(FieldManager.class).get();

        handler.init(formModel, currentModulePath);
    }

    @Test
    public void testModelProperties() {
        handler.synchronizeFormModel();

        formModel.getProperties().forEach(this::checkCommonProperties);
    }

    @Test
    public void testModelSynchronization() {
        FormModelSynchronizationResult synchResult = handler.synchronizeFormModel();

        Assertions.assertThat(synchResult.getRemovedProperties())
                .hasSize(2)
                .contains(removedModelProperty, removedModelProperty2);

        Assertions.assertThat(synchResult.getPropertyConflicts())
                .hasSize(2);

        TypeConflict primitiveIntConflict = synchResult.getConflict(conflictModelProperty.getName());

        Assertions.assertThat(primitiveIntConflict)
                .isNotNull();

        assertEquals(conflictModelProperty.getTypeInfo(), primitiveIntConflict.getBefore());

        Assertions.assertThat(primitiveIntConflict.getNow())
                .isNotNull()
                .hasFieldOrPropertyWithValue("type", TypeKind.BASE)
                .hasFieldOrPropertyWithValue("className", int.class.getName())
                .hasFieldOrPropertyWithValue("multiple", false);

        TypeConflict primitiveFloatConflict = synchResult.getConflict(conflictModelProperty2.getName());

        Assertions.assertThat(primitiveFloatConflict)
                .isNotNull();

        assertEquals(conflictModelProperty2.getTypeInfo(), primitiveFloatConflict.getBefore());

        Assertions.assertThat(primitiveFloatConflict.getNow())
                .isNotNull()
                .hasFieldOrPropertyWithValue("type", TypeKind.BASE)
                .hasFieldOrPropertyWithValue("className", float.class.getName())
                .hasFieldOrPropertyWithValue("multiple", false);
    }

    private FieldDefinition checkCommonProperties(ModelProperty modelProperty) {

        FieldDefinition formField = fieldManager.getDefinitionByModelProperty(modelProperty);

        String dataFieldClassName = modelProperty.getTypeInfo().getClassName();
        TypeInfo fieldTypeInfo = formField.getFieldTypeInfo();

        //test common properties
        assertEquals(dataFieldClassName, fieldTypeInfo.getClassName());
        assertNotEquals(TypeKind.ENUM, fieldTypeInfo.getType());
        assertEquals(modelProperty.getTypeInfo().isMultiple(), fieldTypeInfo.isMultiple());
        assertEquals(modelProperty.getName().toLowerCase(), formField.getLabel().toLowerCase());
        assertEquals(modelProperty.getName(), formField.getBinding());
        assertEquals(dataFieldClassName, formField.getStandaloneClassName());
        assertFalse(formField.getReadOnly());
        assertFalse(formField.getRequired());
        assertTrue(formField.getValidateOnChange());

        //test interface specific properties
        if (formField instanceof HasPlaceHolder) {
            assertEquals(modelProperty.getName().toLowerCase(), ((HasPlaceHolder) formField).getPlaceHolder().toLowerCase());
        }
        if (formField instanceof HasMaxLength) {
            long maxLength = ((HasMaxLength) formField).getMaxLength();

            assertTrue(formField instanceof CharacterBoxFieldDefinition ? maxLength == 1 : maxLength == 100);
        }
        return formField;
    }

    @Test
    public void testCheckModelSource() {
        Assertions.assertThatCode(() -> handler.checkSourceModel())
                .doesNotThrowAnyException();

        createModel("com.example.model.WrongModel");
        handler.init(formModel, currentModulePath);
        Assertions.assertThatThrownBy(() -> handler.checkSourceModel())
                .isInstanceOf(SourceFormModelNotFoundException.class);
    }

    private void createModel(String modelType) {
        formModel = new DataObjectFormModel("Model", modelType);

        // properties to be removed during model synchronization
        formModel.addProperty(removedModelProperty);
        formModel.addProperty(removedModelProperty2);

        // properties with type conflict
        formModel.addProperty(conflictModelProperty);
        formModel.addProperty(conflictModelProperty2);
    }
}
