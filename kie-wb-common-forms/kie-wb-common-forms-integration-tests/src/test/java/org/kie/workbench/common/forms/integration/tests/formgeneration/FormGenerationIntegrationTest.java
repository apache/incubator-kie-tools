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

package org.kie.workbench.common.forms.integration.tests.formgeneration;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Iterables;
import org.apache.commons.io.IOUtils;
import org.eclipse.bpmn2.Definitions;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.workbench.common.forms.commons.shared.layout.FormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.commons.shared.layout.impl.StaticFormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.data.modeller.service.ext.ModelReaderService;
import org.kie.workbench.common.forms.data.modeller.service.impl.ext.dmo.runtime.RuntimeDMOModelReader;
import org.kie.workbench.common.forms.data.modeller.service.shared.ModelFinderService;
import org.kie.workbench.common.forms.editor.client.editor.FormEditorHelper;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandlerManager;
import org.kie.workbench.common.forms.editor.service.shared.ModuleFormFinderService;
import org.kie.workbench.common.forms.editor.service.shared.model.FormModelSynchronizationUtil;
import org.kie.workbench.common.forms.editor.service.shared.model.impl.FormModelSynchronizationUtilImpl;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.type.CheckBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.type.DatePickerFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.type.DecimalBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.type.IntegerBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.type.TextBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.type.MultipleSubFormFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.type.SubFormFieldType;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.fields.test.TestMetaDataEntryManager;
import org.kie.workbench.common.forms.integration.tests.TestUtils;
import org.kie.workbench.common.forms.integration.tests.formgeneration.model.FormGenerationTest_DataObject;
import org.kie.workbench.common.forms.integration.tests.formgeneration.model.FormGenerationTest_NestedObject;
import org.kie.workbench.common.forms.jbpm.model.authoring.AbstractJBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.document.provider.DocumentFieldProvider;
import org.kie.workbench.common.forms.jbpm.model.authoring.document.type.DocumentFieldType;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.FormGenerationResult;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.authoring.BPMNVFSFormDefinitionGeneratorService;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.test.TestFormModelHandlerManager;
import org.kie.workbench.common.forms.jbpm.server.service.impl.BPMNFormModelGeneratorImpl;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.util.formModel.FormModelPropertiesUtil;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FieldSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormDefinitionSerializerImpl;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormModelSerializer;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.base.options.CommentedOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.forms.jbpm.model.authoring.document.type.DocumentFieldType.DOCUMENT_TYPE;
import static org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.AbstractBPMNFormGeneratorService.generateNestedFormName;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormGenerationIntegrationTest {

    private static final String TEXTBOX_FIELDTYPE = new TextBoxFieldType().getTypeName();
    private static final String INTEGERBOX_FIELDTYPE = new IntegerBoxFieldType().getTypeName();
    private static final String CHECKBOX_FIELDTYPE = new CheckBoxFieldType().getTypeName();
    private static final String DECIMALBOX_FIELDTYPE = new DecimalBoxFieldType().getTypeName();
    private static final String TEXTAREA_FIELDTYPE = new TextAreaFieldType().getTypeName();
    private static final String SUBFORM_FIELDTYPE = new SubFormFieldType().getTypeName();
    private static final String DOCUMENT_FIELDTYPE = new DocumentFieldType().getTypeName();
    private static final String MULTIPLESUBFORM_FIELDTYPE = new MultipleSubFormFieldType().getTypeName();
    private static final String TEXBOX_FIELDTYPE = new TextBoxFieldType().getTypeName();
    private static final String DATEPICKER_FIELDTYPE = new DatePickerFieldType().getTypeName();
    private static final String PROCESS_NAME = "FormGenerationTest_Process";
    private static final String DATAOBJECT_FORM_ID = "ff9db399-fb46-4b3f-894a-81673dd5421c";
    private static final String DATA_OBJECTS_FOLDER = "model";
    private static final String PREPARED_NESTED_FORMS_FOLDER = "nestedforms";
    private static final String DEFINITIONS_FOLDER = "definitions";
    private static final String DEFINITION_PATH = DEFINITIONS_FOLDER + "/" + PROCESS_NAME + ".bpmn2";

    private static final String DATA_OBJECT_FORM = PREPARED_NESTED_FORMS_FOLDER + "/FormGenerationTest_DataObject.frm";
    private static final String NESTED_OBJECT_FORM = PREPARED_NESTED_FORMS_FOLDER + "/FormGenerationTest_NestedObject.frm";

    private static BPMNVFSFormDefinitionGeneratorService service;

    private static FieldManager fieldManager;
    private static FormModelHandlerManager formModelHandlerManager;
    @Mock
    private static ModelReaderService<Path> pathModelReaderService;

    @Mock
    private static ModuleFormFinderService formFinderService;
    private static FormDefinitionSerializer formSerializer;
    private static IOService ioService;
    @Mock
    private static CommentedOptionFactory commentedOptionFactory;
    @Mock
    private static CommentedOption commentedOption;
    private static FormModelSynchronizationUtil formModelSynchronizationUtil;

    @Mock
    private static KieModuleService moduleService;

    @Mock
    private static ModuleClassLoaderHelper moduleClassLoaderHelper;
    private static FormLayoutTemplateGenerator templateGenerator;
    private static BPMNFormModelGeneratorImpl generator;
    private static Definitions formGenerationProcessDefinitions;

    @Mock
    private static KieModule module;
    private static ClassLoader moduleClassLoader;
    private static FormEditorHelper formEditorHelper;
    private static FormModelerContent formModelerContent;
    private static BusinessProcessFormModel processFormModel;
    private static List<TaskFormModel> taskFormModels;

    @Mock
    private static ModelFinderService finderService;

    @BeforeClass
    public static void setup() {
        FormModelPropertiesUtil.registerBaseType(DOCUMENT_TYPE);
        FormModelPropertiesUtil.registerBaseType(DocumentFieldType.DOCUMENT_IMPL_TYPE);
        fieldManager = new TestFieldManager() {{
            registerFieldProvider(new DocumentFieldProvider() {{
                doRegisterFields();
            }});
        }};

        formSerializer = new FormDefinitionSerializerImpl(new FieldSerializer(),
                                                          new FormModelSerializer(),
                                                          new TestMetaDataEntryManager());

        ioService = new IOServiceDotFileImpl();
        templateGenerator = new StaticFormLayoutTemplateGenerator();
        formModelSynchronizationUtil = new FormModelSynchronizationUtilImpl(fieldManager, templateGenerator);

        moduleClassLoader = FormGenerationIntegrationTest.class.getClassLoader();

        formGenerationProcessDefinitions = TestUtils.getDefinitionsFromResources(FormGenerationIntegrationTest.class, DEFINITION_PATH);

        formModelerContent = new FormModelerContent();
        formEditorHelper = new FormEditorHelper(fieldManager, null, mock(SyncBeanManager.class));
    }

    @Before
    public void init() {

        formModelHandlerManager = new TestFormModelHandlerManager(moduleService,
                                                                  moduleClassLoaderHelper,
                                                                  fieldManager,
                                                                  finderService);

        when(pathModelReaderService.getModelReader(any())).thenReturn(new RuntimeDMOModelReader(moduleClassLoader, new RawMVELEvaluator()));

        when(formFinderService.findFormsForType(anyString(), any())).then((Answer<List<FormDefinition>>) invocationOnMock -> {
            String type = invocationOnMock.getArguments()[0].toString();
            return Collections.singletonList(readFormDefinitionForType(type));
        });

        service = new BPMNVFSFormDefinitionGeneratorService(fieldManager,
                                                            pathModelReaderService,
                                                            formModelHandlerManager,
                                                            formFinderService,
                                                            formSerializer,
                                                            ioService,
                                                            commentedOptionFactory,
                                                            formModelSynchronizationUtil);

        when(moduleService.resolveModule(any())).thenReturn(module);
        when(moduleClassLoaderHelper.getModuleClassLoader(any())).thenReturn(moduleClassLoader);

        generator = new BPMNFormModelGeneratorImpl(moduleService, moduleClassLoaderHelper);

        final Path rootPathWithNestedForms = PathFactory.newPath(DATA_OBJECTS_FOLDER, getUriOfResource(PREPARED_NESTED_FORMS_FOLDER));
        ;

        processFormModel = generator.generateProcessFormModel(formGenerationProcessDefinitions, rootPathWithNestedForms);
        taskFormModels = generator.generateTaskFormModels(formGenerationProcessDefinitions, rootPathWithNestedForms);
    }

    /**
     * Tests if all form definitions are generated correctly when you hit generate all in the designer.
     */

    @Test
    public void ProcessFormIsCorrectlyGenerated() {

        final FormGenerationResult formGenerationResult = generateForm("new_formmodeler.FormGenerationTest_Process-taskform.frm", processFormModel);

        final List<String> expectedProcessFormFields = Arrays.asList(
                "boolean", CHECKBOX_FIELDTYPE,
                "dataObject", SUBFORM_FIELDTYPE,
                "document", DOCUMENT_FIELDTYPE,
                "float", DECIMALBOX_FIELDTYPE,
                "integer", INTEGERBOX_FIELDTYPE,
                "object", TEXTAREA_FIELDTYPE,
                "string", TEXTBOX_FIELDTYPE
        );

        final FormDefinition rootForm = formGenerationResult.getRootForm();

        testFormDefinition(rootForm, expectedProcessFormFields);
        testNestedFormsHaveCorrectId(rootForm, "dataObject", DATAOBJECT_FORM_ID);
    }

    @Test
    public void inputTaskFormIsCorrectlyGenerated() {

        final FormGenerationResult formGenerationResult = generateForm("FormGenerationTest_TaskOnlyWithInputs-taskform.frm", taskFormModels.get(2));

        final List<String> expectedInputTaskFormFields = Arrays.asList(
                "_boolean", CHECKBOX_FIELDTYPE,
                "_dataObject", SUBFORM_FIELDTYPE,
                "_document", DOCUMENT_FIELDTYPE,
                "_float", DECIMALBOX_FIELDTYPE,
                "_integer", INTEGERBOX_FIELDTYPE,
                "_object", TEXTAREA_FIELDTYPE,
                "_string", TEXTBOX_FIELDTYPE
        );

        final FormDefinition rootForm = formGenerationResult.getRootForm();

        testFormDefinition(rootForm, expectedInputTaskFormFields);
        testNestedFormsHaveCorrectId(rootForm, "_dataObject", DATAOBJECT_FORM_ID);
    }

    @Test
    public void emptyTaskFormIsCorrectlyGenerated() {
        final FormGenerationResult formGenerationResult = generateForm("FormGenerationTest_EmptyTask-taskform.frm", taskFormModels.get(1));

        final FormDefinition rootForm = formGenerationResult.getRootForm();

        testFormDefinition(rootForm, Collections.emptyList());
    }

    @Test
    public void differentIOTaskFormIsCorrectlyGenerated() {

        final FormGenerationResult formGenerationResult = generateForm("FormGenerationTest_TaskWithDifferentInputsAndOutputs-taskform.frm", taskFormModels.get(0));

        final List<String> expectedProcessFormFields = Arrays.asList(
                //inputs
                "_boolean", CHECKBOX_FIELDTYPE,
                "_dataObject", SUBFORM_FIELDTYPE,
                "_document", DOCUMENT_FIELDTYPE,
                "_float", DECIMALBOX_FIELDTYPE,
                "_integer", INTEGERBOX_FIELDTYPE,
                "_object", TEXTAREA_FIELDTYPE,
                "_string", TEXTBOX_FIELDTYPE,
                //outputs
                "boolean_", CHECKBOX_FIELDTYPE,
                "dataObject_", SUBFORM_FIELDTYPE,
                "document_", DOCUMENT_FIELDTYPE,
                "float_", DECIMALBOX_FIELDTYPE,
                "integer_", INTEGERBOX_FIELDTYPE,
                "object_", TEXTAREA_FIELDTYPE,
                "string_", TEXTBOX_FIELDTYPE
        );

        final FormDefinition rootForm = formGenerationResult.getRootForm();

        testFormDefinition(rootForm, expectedProcessFormFields);
        testNestedFormsHaveCorrectId(rootForm, "_dataObject", DATAOBJECT_FORM_ID);
        testNestedFormsHaveCorrectId(rootForm, "dataObject_", DATAOBJECT_FORM_ID);
    }

    @Test
    public void sameIOTaskFormIsCorrectlyGenerated() {

        final FormGenerationResult formGenerationResult = generateForm("FormGenerationTest_TaskWithTheSameInputsAndOutputs-taskform.frm", taskFormModels.get(3));

        final List<String> expectedProcessFormFields = Arrays.asList(
                "_boolean_", CHECKBOX_FIELDTYPE,
                "_dataObject_", SUBFORM_FIELDTYPE,
                "_document_", DOCUMENT_FIELDTYPE,
                "_float_", DECIMALBOX_FIELDTYPE,
                "_integer_", INTEGERBOX_FIELDTYPE,
                "_object_", TEXTAREA_FIELDTYPE,
                "_string_", TEXTBOX_FIELDTYPE
        );

        final FormDefinition rootForm = formGenerationResult.getRootForm();

        testFormDefinition(rootForm, expectedProcessFormFields);
        testNestedFormsHaveCorrectId(rootForm, "_dataObject_", DATAOBJECT_FORM_ID);
    }

    @Test
    public void twinTaskFormIsCorrectlyGenerated() {

        final FormGenerationResult formGenerationResult = generateForm("FormGenerationTest_TwinTasks-taskform.frm", taskFormModels.get(4));

        final List<String> expectedProcessFormFields = Arrays.asList(
                "_boolean", CHECKBOX_FIELDTYPE,
                "_dataObject1", SUBFORM_FIELDTYPE,
                "_dataObject2", SUBFORM_FIELDTYPE,
                "_string", TEXTBOX_FIELDTYPE,
                "_boolean_", CHECKBOX_FIELDTYPE,
                "_string_", TEXTBOX_FIELDTYPE,
                "dataObject_", SUBFORM_FIELDTYPE,
                "integer_", INTEGERBOX_FIELDTYPE
        );

        final FormDefinition rootForm = formGenerationResult.getRootForm();

        testFormDefinition(rootForm, expectedProcessFormFields);
        testNestedFormsHaveCorrectId(rootForm, "_dataObject1", DATAOBJECT_FORM_ID);
        testNestedFormsHaveCorrectId(rootForm, "_dataObject2", DATAOBJECT_FORM_ID);
        testNestedFormsHaveCorrectId(rootForm, "dataObject_", DATAOBJECT_FORM_ID);
    }

    @Test
    public void testNestedFormsGeneration() {
        when(formFinderService.findFormsForType(anyString(), any())).thenReturn(Collections.EMPTY_LIST);

        when(commentedOptionFactory.makeCommentedOption(any())).thenReturn(commentedOption);

        final FormGenerationResult formGenerationResult = generateForm("FormGenerationTest_TwinTasks-taskform.frm", taskFormModels.get(4));

        final List<String> expectedDataObjectFormFields = Arrays.asList(
                "nestedObjectForSubform", SUBFORM_FIELDTYPE,
                "nestedObjectListForMultipleSubform", MULTIPLESUBFORM_FIELDTYPE,
                "bigDecimalDecimalBox", DECIMALBOX_FIELDTYPE,
                "bigIntegerIntegerBox", INTEGERBOX_FIELDTYPE,
                "booleanCheckBox", CHECKBOX_FIELDTYPE,
                "byteIntegerBox", INTEGERBOX_FIELDTYPE,
                "charTextBox", TEXTBOX_FIELDTYPE,
                "dateDatePicker", DATEPICKER_FIELDTYPE,
                "doubleDecimalBox", DECIMALBOX_FIELDTYPE,
                "floatDecimalBox", DECIMALBOX_FIELDTYPE,
                "longIntegerBox", INTEGERBOX_FIELDTYPE,
                "stringListBox", TEXTBOX_FIELDTYPE,
                "intIntegerBox", INTEGERBOX_FIELDTYPE,
                "shortIntegerBox", INTEGERBOX_FIELDTYPE,
                "bigDecimalRadioGroup", DECIMALBOX_FIELDTYPE,
                "bigIntegerRadioGroup", INTEGERBOX_FIELDTYPE,
                "byteRadioGroup", INTEGERBOX_FIELDTYPE,
                "charRadioGroup", TEXTBOX_FIELDTYPE,
                "doubleSlider", DECIMALBOX_FIELDTYPE,
                "doubleRadioGroup", DECIMALBOX_FIELDTYPE,
                "floatRadioGroup", DECIMALBOX_FIELDTYPE,
                "intRadioGroup", INTEGERBOX_FIELDTYPE,
                "intSlider", INTEGERBOX_FIELDTYPE,
                "longRadioGroup", INTEGERBOX_FIELDTYPE,
                "stringPicture", TEXTBOX_FIELDTYPE,
                "stringRadioGroup", TEXTBOX_FIELDTYPE,
                "stringTextArea", TEXTBOX_FIELDTYPE,
                "stringTextBox", TEXTBOX_FIELDTYPE,
                "shortRadioGroup", INTEGERBOX_FIELDTYPE
        );

        final List<String> expectedNestedDataObjectFormFields = Arrays.asList(
                "stringListBox", TEXBOX_FIELDTYPE,
                "doubleDecimalBox", DECIMALBOX_FIELDTYPE,
                "booleanCheckBox", CHECKBOX_FIELDTYPE,
                "dateDatePicker", DATEPICKER_FIELDTYPE,
                "charRadioGroup", TEXTBOX_FIELDTYPE,
                "doubleSlider", DECIMALBOX_FIELDTYPE,
                "intIntegerBox", INTEGERBOX_FIELDTYPE,
                "stringPicture", TEXBOX_FIELDTYPE,
                "stringTextArea", TEXTBOX_FIELDTYPE,
                "stringTextBox", TEXTBOX_FIELDTYPE
        );

        final FormDefinition rootForm = formGenerationResult.getRootForm();

        final List<FormDefinition> nestedForms = formGenerationResult.getNestedForms();
        assertThat(nestedForms.size()).as("Unexpected number of nested forms").isEqualTo(2);

        final FormDefinition dataObjectForm = Iterables.getOnlyElement(nestedForms.stream()
                                                                               .filter(f -> f.getName().equals(generateNestedFormName(FormGenerationTest_DataObject.class.getName())))
                                                                               .collect(Collectors.toList()));

        final FormDefinition nestedDataObjectForm = Iterables.getOnlyElement(nestedForms.stream()
                                                                                     .filter(f -> f.getName().equals(generateNestedFormName(FormGenerationTest_NestedObject.class.getName())))
                                                                                     .collect(Collectors.toList()));

        final String dataObjectFormID = dataObjectForm.getId();

        testNestedFormsHaveCorrectId(rootForm, "_dataObject1", dataObjectFormID);
        testNestedFormsHaveCorrectId(rootForm, "_dataObject2", dataObjectFormID);
        testNestedFormsHaveCorrectId(rootForm, "dataObject_", dataObjectFormID);

        final String nestedDataObjectFormID = nestedDataObjectForm.getId();

        testFormDefinition(dataObjectForm, expectedDataObjectFormFields);
        testNestedFormsHaveCorrectId(dataObjectForm, "nestedObjectForSubform", nestedDataObjectFormID);

        testFormDefinition(nestedDataObjectForm, expectedNestedDataObjectFormFields);
    }

    private FormGenerationResult generateForm(String formName, AbstractJBPMFormModel formModel) {
        final String DEFINITIONS_URI = getUriOfResource(DEFINITIONS_FOLDER);
        final Path formPath = PathFactory.newPath(formName, DEFINITIONS_URI + formName);
        return service.generateForms(formModel, formPath);
    }

    private void testFormDefinition(FormDefinition form, List<String> expectedFormFields) {
        testFormContainsCorrectFields(form, expectedFormFields);
        testAllFieldsAreAddedToLayout(form);
        testComponentMenuContainsNoFields(form);
    }

    private void testFormContainsCorrectFields(FormDefinition form, List<String> expectedFormFields) {
        List<String> actualRootFormFields = form.getFields().stream()
                .flatMap(field -> Stream.of(field.getName(), field.getFieldType().getTypeName()))
                .collect(Collectors.toList());
        assertThat(actualRootFormFields).containsExactlyInAnyOrder(expectedFormFields.stream().toArray(String[]::new));
    }

    private void testAllFieldsAreAddedToLayout(FormDefinition form) {
        List<String> expectedFieldIds = form.getFields().stream()
                .map(FieldDefinition::getId)
                .collect(Collectors.toList());
        List<String> actualFieldIdsInRows = form.getLayoutTemplate().getRows().stream()
                .filter(r -> !rowContainsHTMLComponent(r))
                .map(this::getFieldIdFromRow)
                .collect(Collectors.toList());
        assertThat(actualFieldIdsInRows).containsExactlyElementsOf(expectedFieldIds);
    }

    private void testComponentMenuContainsNoFields(FormDefinition form) {
        formModelerContent.setDefinition(form);
        formEditorHelper.initHelper(formModelerContent);
        assertThat(formEditorHelper.getAvailableFields()).isEmpty();
    }

    private void testNestedFormsHaveCorrectId(FormDefinition form, String dataObjectFieldName, String expectedFormId) {
        SubFormFieldDefinition dataObjectField = (SubFormFieldDefinition) form.getFieldByName(dataObjectFieldName);
        String nestedForm = dataObjectField.getNestedForm();
        assertThat(nestedForm)
                .as("Nested form for field: " + dataObjectField.getLabel() + " has incorrect id.")
                .isEqualTo(expectedFormId);
    }

    private String getFieldIdFromRow(LayoutRow row) {
        return row.getLayoutColumns().get(0).getLayoutComponents().get(0).getProperties().get("field_id");
    }

    private boolean rowContainsHTMLComponent(LayoutRow row) {
        return "org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent".equals(row.getLayoutColumns().get(0).getLayoutComponents().get(0).getDragTypeName());
    }

    private String getUriOfResource(String resourceName) {
        return FormGenerationIntegrationTest.class.getResource(resourceName).toString();
    }

    private FormDefinition readFormDefinitionForType(String type) {
        if (type.equals(FormGenerationTest_DataObject.class.getName())) {
            return readFormDefinition(DATA_OBJECT_FORM);
        } else if (type.equals(FormGenerationTest_NestedObject.class.getName())) {
            return readFormDefinition(NESTED_OBJECT_FORM);
        }
        return null;
    }

    private FormDefinition readFormDefinition(String path) {
        try {
            String content = IOUtils.toString(getClass().getResourceAsStream(path), Charset.defaultCharset());
            return formSerializer.deserialize(content);
        } catch (IOException e) {
        }
        return null;
    }
}
