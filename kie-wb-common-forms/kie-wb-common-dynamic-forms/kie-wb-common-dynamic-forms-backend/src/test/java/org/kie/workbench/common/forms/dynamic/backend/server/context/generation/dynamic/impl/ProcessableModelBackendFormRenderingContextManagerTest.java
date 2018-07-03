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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time.converters.LocalDateConverter;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time.converters.LocalDateTimeConverter;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time.converters.LocalTimeConverter;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.marshalling.time.converters.OffsetDateTimeConverter;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.model.ProcessableModel;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.ModelPropertyImpl;
import org.kie.workbench.common.forms.model.impl.PortableJavaModel;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class ProcessableModelBackendFormRenderingContextManagerTest extends AbstractBackendFormRenderingContextManagerTest {

    private static final String ROOT_FORM_ID = "rootFormId";
    private static final String NESTED_FORM_ID = "nestedFormId";

    private static final String MODEL = "model";

    private static final String BIG_DECIMAL = "bigDecimal";
    private static final String BIG_INTEGER = "bigInteger";
    private static final String A_FLOAT = "aFloat";
    private static final String LOCAL_DATE = "localDate";
    private static final String LOCAL_DATE_TIME = "localDateTime";
    private static final String LOCAL_TIME = "localTime";
    private static final String OFFSET_DATE_TIME = "offsetDateTime";
    private static final String CHILD = "child";
    private static final String CHILDREN = "children";

    private ProcessableModel model;

    @Override
    public void initTest() {
        super.initTest();

        try {
            when(classLoader.loadClass(anyString())).thenAnswer((Answer<Class>) invocationOnMock -> ProcessableModel.class);
        } catch (ClassNotFoundException e) {

        }
    }

    @Test
    public void testReadValues() {
        Map<String, Object> result = context.getRenderingContext().getModel();

        Map<String, Object> model = (Map<String, Object>) result.get(MODEL);

        testFlatModel(model);

        testFlatModel((Map<String, Object>) model.get(CHILD));

        testFlatModel(((List<Map<String, Object>>) model.get(CHILDREN)).get(0));
    }

    private void testFlatModel(Map<String, Object> model) {
        testFlatValue(model.get(BIG_DECIMAL), BigDecimal.class);
        testFlatValue(model.get(BIG_INTEGER), BigInteger.class);
        testFlatValue(model.get(A_FLOAT), Float.class);
        testFlatValue(model.get(LOCAL_DATE), Date.class);
        testFlatValue(model.get(LOCAL_DATE_TIME), Date.class);
        testFlatValue(model.get(LOCAL_TIME), Date.class);
        testFlatValue(model.get(OFFSET_DATE_TIME), Date.class);
    }

    private void testFlatValue(Object value, Class type) {
        Assertions.assertThat(value)
                .isNotNull()
                .isInstanceOf(type);
    }

    @Test
    public void testProcessValues() {
        Date date = new Date();
        date.setTime(date.getTime() + 5000);

        Map<String, Object> modelValues = getFormData(date);

        modelValues.put(CHILD, getFormData(date));

        List<Map<String, Object>> children = new ArrayList<>();

        modelValues.put(CHILDREN, children);

        Map<String, Object> firstChildModel = getFormData(date);
        firstChildModel.put(MapModelRenderingContext.FORM_ENGINE_OBJECT_IDX, 0);
        firstChildModel.put(MapModelRenderingContext.FORM_ENGINE_EDITED_OBJECT, true);

        children.add(firstChildModel);
        children.add(getFormData(date));

        Map<String, Object> formValues = new HashMap<>();

        formValues.put(MODEL, modelValues);

        Map<String, Object> result = contextManager.updateContextData(context.getTimestamp(), formValues).getFormData();

        Object updatedModel = result.get(MODEL);

        Assertions.assertThat(updatedModel)
                .isNotNull()
                .isSameAs(model);

        verifyModel(model, date);

        verifyModel(model.getChild(), date);

        Assertions.assertThat(model.getChildren())
                .isNotNull()
                .hasSize(2);

        model.getChildren().stream().forEach(child -> verifyModel(child, date));
    }

    private void verifyModel(ProcessableModel model, Date date) {
        Assertions.assertThat(model)
                .isNotNull()
                .hasFieldOrPropertyWithValue(BIG_DECIMAL, new BigDecimal("0.2"))
                .hasFieldOrPropertyWithValue(A_FLOAT, Float.valueOf("0.2"))
                .hasFieldOrPropertyWithValue(BIG_INTEGER, new BigInteger("2"))
                .hasFieldOrPropertyWithValue(LOCAL_DATE, new LocalDateConverter().toRawValue(date))
                .hasFieldOrPropertyWithValue(LOCAL_DATE_TIME, new LocalDateTimeConverter().toRawValue(date))
                .hasFieldOrPropertyWithValue(LOCAL_TIME, new LocalTimeConverter().toRawValue(date))
                .hasFieldOrPropertyWithValue(OFFSET_DATE_TIME, new OffsetDateTimeConverter().toRawValue(date));
    }

    private Map<String, Object> getFormData(Date date) {
        Map<String, Object> modelFormValues = new HashMap<>();
        modelFormValues.put(BIG_DECIMAL, new BigDecimal("0.2"));
        modelFormValues.put(A_FLOAT, Float.valueOf("0.2"));
        modelFormValues.put(BIG_INTEGER, new BigInteger("2"));
        modelFormValues.put(LOCAL_DATE, date);
        modelFormValues.put(LOCAL_DATE_TIME, date);
        modelFormValues.put(LOCAL_TIME, date);
        modelFormValues.put(OFFSET_DATE_TIME, date);

        return modelFormValues;
    }

    @Override
    protected Map<String, Object> generateFormData() {
        model = getModel();
        model.setChild(getModel());
        model.setChildren(new ArrayList<>());
        model.getChildren().add(getModel());

        Map<String, Object> data = new HashMap<>();

        data.put(MODEL, model);

        return data;
    }

    private ProcessableModel getModel() {
        ProcessableModel newModel = new ProcessableModel();

        newModel.setBigDecimal(new BigDecimal("0.1"));
        newModel.setaFloat(Float.valueOf("0.1"));
        newModel.setBigInteger(new BigInteger("1"));
        newModel.setLocalDate(LocalDate.now());
        newModel.setLocalDateTime(LocalDateTime.now());
        newModel.setLocalTime(LocalTime.now());
        newModel.setOffsetDateTime(OffsetDateTime.now());

        return newModel;
    }

    @Override
    protected FormDefinition getRootForm() {
        FormDefinition form = new FormDefinition();
        FieldDefinition field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(TypeKind.OBJECT, ProcessableModel.class.getName(), false));

        field.setName(MODEL);
        field.setBinding(MODEL);

        SubFormFieldDefinition subForm = (SubFormFieldDefinition) field;
        subForm.setNestedForm(ROOT_FORM_ID);

        form.getFields().add(field);

        return form;
    }

    @Override
    protected FormDefinition[] getNestedForms() {
        return new FormDefinition[]{getFormDefinition(ROOT_FORM_ID, true), getFormDefinition(NESTED_FORM_ID, false)};
    }

    private FormDefinition getFormDefinition(final String id, final boolean appendNested) {
        final FormDefinition formDefinition = new FormDefinition(new PortableJavaModel(ProcessableModel.class.getName()));

        formDefinition.setId(id);

        appendFieldDefinition(new ModelPropertyImpl(BIG_DECIMAL, new TypeInfoImpl(BigDecimal.class.getName())), formDefinition);
        appendFieldDefinition(new ModelPropertyImpl(BIG_INTEGER, new TypeInfoImpl(BigInteger.class.getName())), formDefinition);
        appendFieldDefinition(new ModelPropertyImpl(A_FLOAT, new TypeInfoImpl(Float.class.getName())), formDefinition);
        appendFieldDefinition(new ModelPropertyImpl(LOCAL_DATE, new TypeInfoImpl(LocalDate.class.getName())), formDefinition);
        appendFieldDefinition(new ModelPropertyImpl(LOCAL_DATE_TIME, new TypeInfoImpl(LocalDateTime.class.getName())), formDefinition);
        appendFieldDefinition(new ModelPropertyImpl(LOCAL_TIME, new TypeInfoImpl(LocalTime.class.getName())), formDefinition);
        appendFieldDefinition(new ModelPropertyImpl(OFFSET_DATE_TIME, new TypeInfoImpl(OffsetDateTime.class.getName())), formDefinition);

        if (appendNested) {
            appendFieldDefinition(new ModelPropertyImpl(CHILD, new TypeInfoImpl(TypeKind.OBJECT, ProcessableModel.class.getName(), false)), formDefinition);
            ((SubFormFieldDefinition) formDefinition.getFieldByBinding(CHILD)).setNestedForm(NESTED_FORM_ID);

            appendFieldDefinition(new ModelPropertyImpl(CHILDREN, new TypeInfoImpl(TypeKind.OBJECT, ProcessableModel.class.getName(), true)), formDefinition);
            ((MultipleSubFormFieldDefinition) formDefinition.getFieldByBinding(CHILDREN)).setCreationForm(NESTED_FORM_ID);
            ((MultipleSubFormFieldDefinition) formDefinition.getFieldByBinding(CHILDREN)).setEditionForm(NESTED_FORM_ID);
        }

        return formDefinition;
    }

    private void appendFieldDefinition(final ModelProperty property, final FormDefinition formDefinition) {
        formDefinition.getModel().getProperties().add(property);

        FieldDefinition field = fieldManager.getDefinitionByDataType(property.getTypeInfo());
        field.setName(property.getName());
        field.setBinding(property.getName());
        formDefinition.getFields().add(field);
    }
}
