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

package org.kie.workbench.common.forms.integration.tests.valueprocessing;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.enterprise.inject.Instance;

import com.google.common.io.Resources;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.FormValuesProcessorImpl;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.MultipleSubFormFieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.SubFormFieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.TextAreaFormFieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.fieldProcessors.time.LocalDateFieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FieldValueProcessor;
import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.FormValuesProcessor;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.fields.test.TestMetaDataEntryManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FieldSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormDefinitionSerializerImpl;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormModelSerializer;
import org.lesscss.deps.org.apache.commons.io.Charsets;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.kie.workbench.common.forms.integration.tests.valueprocessing.TestUtils.createDate;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormValuesProcessorTest {

    private static TextAreaFormFieldValueProcessor textAreaFormFieldValueProcessor = new TextAreaFormFieldValueProcessor();
    private static MultipleSubFormFieldValueProcessor multipleSubFormFieldValueProcessor = new MultipleSubFormFieldValueProcessor();
    private static SubFormFieldValueProcessor subFormFieldValueProcessor = new SubFormFieldValueProcessor();
    private static LocalDateFieldValueProcessor localDateFieldValueProcessor = new LocalDateFieldValueProcessor();

    @Mock
    private Instance<FieldValueProcessor<? extends FieldDefinition, ?, ?>> instanceMock;

    private static FormDefinitionSerializer formSerializer = new FormDefinitionSerializerImpl(new FieldSerializer(),
                                                                                              new FormModelSerializer(),
                                                                                              new TestMetaDataEntryManager());

    @Mock
    private static BackendFormRenderingContext context;

    @Mock
    private static MapModelRenderingContext mapModelRenderingContext;

    private static FormDefinition
            textAreaTaskForm,
            localDateTaskForm,
            subformTaskForm;

    private FormValuesProcessor formValuesProcessor;

    @BeforeClass
    public static void setup() throws IOException {
        textAreaTaskForm = getFormDefinition("TextareaTask-taskform.frm");
        localDateTaskForm = getFormDefinition("LocalDateFieldTask-taskform.frm");
        subformTaskForm = getFormDefinition("SubformTask-taskform.frm");
    }

    @Test
    public void testTextAreaTaskFormValuesProcessing() throws ParseException {
        setupFormValuesProcessor(Collections.singletonList(textAreaFormFieldValueProcessor));

        final String FIELD_BINDING = "_textarea";

        //raw value of the property before it was updated
        final Date d1 = createDate("21/12/2012");
        final Map<String, Object> originalRawValues = Collections.singletonMap(FIELD_BINDING, d1);
        //values in the form updated by user
        final Map<String, Object> originalFlatValues = Collections.singletonMap(FIELD_BINDING, d1.toString());

        final Map<String, Object> writtenRawValues = formValuesProcessor.writeFormValues(textAreaTaskForm, originalFlatValues, originalRawValues, context);
        testReadingFormValues(textAreaTaskForm, writtenRawValues, originalFlatValues);
        //note: in this case it doesn't make sense to test the writeFormValues method, since it returns flatValues
        //(there is no way to reconstruct the Object back from the String value)
    }

    @Test
    public void testLocalDateFieldTaskFormValuesProcessing() {
        setupFormValuesProcessor(Collections.singletonList(localDateFieldValueProcessor));

        final String
                LOCAL_DATE_BINDING = "_localDate_",
                LOCAL_DATE_TIME_BINDING = "_localDateTime_",
                LOCAL_TIME_BINDING = "_localTime_",
                OFFSET_DATE_TIME_BINDING = "_offsetDateTime_";

        final LocalDate localDate1 = LocalDate.of(1989, 6, 6);
        final LocalDateTime localDateTime1 = LocalDateTime.of(2000, 5, 2, 3, 4);
        final LocalTime localTime1 = LocalTime.of(23, 15);
        //form engine does not support setting custom offset
        final ZoneOffset zoneOffset1 = OffsetDateTime.now().getOffset();
        final OffsetDateTime offsetDateTime1 = OffsetDateTime.of(localDate1, localTime1, zoneOffset1);

        final Map<String, Object> originalRawValues = new HashMap<String, Object>() {{
            put(LOCAL_DATE_BINDING, localDate1);
            put(LOCAL_DATE_TIME_BINDING, localDateTime1);
            put(LOCAL_TIME_BINDING, localTime1);
            put(OFFSET_DATE_TIME_BINDING, offsetDateTime1);
        }};

        localDateFieldValueProcessor.init();
        final Map<String, Object> readFlatValues = formValuesProcessor.readFormValues(localDateTaskForm, originalRawValues, context);

        final Map<String, Object> writtenRawValues = testWritingFormValues(localDateTaskForm, originalRawValues, readFlatValues, originalRawValues);
        testReadingFormValues(localDateTaskForm, writtenRawValues, readFlatValues);
    }

    @Test
    public void testSubformTaskFormValuesAreProcessedCorrectly() throws ParseException, IOException {
        setupFormValuesProcessor(Arrays.asList(subFormFieldValueProcessor, multipleSubFormFieldValueProcessor, textAreaFormFieldValueProcessor));
        setupSubformTest();

        final String
                SUBFORM_BINDING = "_subform",
                MULTIPLESUBFORM_BINDING = "multiplesubform";

        final List<NestedDO> originalMultipleSubformRawValues = Arrays.asList(
                new NestedDO(true, "Joseph", "Hello\n my\n name\n is Joseph\n", 15, 1.564, createDate("06/06/1989"), 10.0, "2", "one"),
                new NestedDO(false, "John", "Hello\n my\n name\n is John\n", 100, 40.5684, createDate("17/11/1989"), 26.0, "2", "two"),
                new NestedDO(true, "Martin", "Hello\n my\n name\n is Martin\n", 520, 20.1569, createDate("11/09/2011"), 49.0, "3", "three")
        );

        final SubformDO originalSubformRawValues = new SubformDO(true, "Joe", "This\n is\n not\n a joke!\n", 2, 3.14, createDate("06/06/1989"), 5.0, "2", "two", originalMultipleSubformRawValues);

        final Map<String, Object> originalRawValues = Collections.singletonMap(SUBFORM_BINDING, originalSubformRawValues);

        final List<Map<String, Object>> originalMultipleSubformFlatValues = Arrays.asList(
                initMultipleSubform(new HashMap<>(), FormFields::getFirstLineValue),
                initMultipleSubform(new HashMap<>(), FormFields::getSecondLineValue),
                initMultipleSubform(new HashMap<>(), FormFields::getThirdLineValue)
        );

        final Map<String, Object> originalSubformFlatValues = new HashMap<String, Object>() {{
            initSubform(this, FormFields::getSubformValue);
            put(MULTIPLESUBFORM_BINDING, originalMultipleSubformFlatValues);
        }};

        final Map<String, Object> originalFlatValues = Collections.singletonMap(SUBFORM_BINDING, originalSubformFlatValues);

        final Map<String, Object> writtenRawValues = testWritingFormValues(subformTaskForm, originalRawValues, originalFlatValues, originalRawValues);
        testReadingFormValues(subformTaskForm, writtenRawValues, originalFlatValues);
    }

    private void setupSubformTest() throws IOException {
        final String
                SUBFORM_ID = "f31d37ce-8155-4478-a464-456c013236c6",
                CREATIONFORM_ID = "6544f16e-c765-451e-882d-8202f6ea824c",
                EDITIONFORM_ID = "dd1451bf-4f38-495b-8f16-b74711246797";

        Map<String, FormDefinition> availableForms = new HashMap<String, FormDefinition>() {{
            put(SUBFORM_ID, getFormDefinition("Subform.frm"));
            put(CREATIONFORM_ID, getFormDefinition("CreationForm.frm"));
            put(EDITIONFORM_ID, getFormDefinition("EditionForm.frm"));
        }};

        when(context.getRenderingContext()).thenReturn(mapModelRenderingContext);
        when(mapModelRenderingContext.getAvailableForms()).thenReturn(availableForms);
        when(context.getClassLoader()).thenReturn(this.getClass().getClassLoader());
    }

    private Map<String, Object> testReadingFormValues(FormDefinition taskForm, Map<String, Object> originalRawValues, Map<String, Object> expectedFlatValues) {
        final Map<String, Object> originalFlatValues = formValuesProcessor.readFormValues(taskForm, originalRawValues, context);
        assertThat(originalFlatValues).isEqualTo(expectedFlatValues);
        return originalFlatValues;
    }

    private Map<String, Object> testWritingFormValues(FormDefinition taskForm, Map<String, Object> originalRawValues, Map<String, Object> updatedFlatValues, Map<String, Object> expectedUpdatedRawValues) {
        final Map<String, Object> updatedRawValues = formValuesProcessor.writeFormValues(taskForm, updatedFlatValues, originalRawValues, context);
        assertThat(updatedRawValues).isEqualTo(expectedUpdatedRawValues);
        return updatedRawValues;
    }

    private static FormDefinition getFormDefinition(String formName) throws IOException {
        return formSerializer.deserialize(loadTaskForm(formName));
    }

    private static String loadTaskForm(String taskForm) throws IOException {
        return Resources.toString(FormValuesProcessorTest.class.getResource(taskForm), Charsets.UTF_8);
    }

    private void setupFormValuesProcessor(List<FieldValueProcessor<? extends FieldDefinition, ?, ?>> processors) {
        when(instanceMock.iterator()).thenReturn(processors.iterator());
        formValuesProcessor = new FormValuesProcessorImpl(instanceMock);
    }

    private Map<String, Object> initMultipleSubform(Map<String, Object> map, Function<FormFields, Object> formValueProducer) {
        for (FormEngineFields value : FormEngineFields.values()) {
            map.put(value.getBinding(), formValueProducer.apply(value));
        }
        return initSubform(map, formValueProducer);
    }

    private Map<String, Object> initSubform(Map<String, Object> map, Function<FormFields, Object> valueProducer) {
        for (SubformFields value : SubformFields.values()) {
            map.put(value.getBinding(), valueProducer.apply(value));
        }
        return map;
    }
}