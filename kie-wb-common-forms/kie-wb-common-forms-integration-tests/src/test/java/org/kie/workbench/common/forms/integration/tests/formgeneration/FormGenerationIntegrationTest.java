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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Iterables;
import org.eclipse.bpmn2.Definitions;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.commons.shared.layout.FormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.commons.shared.layout.impl.StaticFormLayoutTemplateGenerator;
import org.kie.workbench.common.forms.data.modeller.service.DataObjectFinderService;
import org.kie.workbench.common.forms.data.modeller.service.impl.DataObjectFinderServiceImpl;
import org.kie.workbench.common.forms.editor.backend.service.impl.VFSFormFinderServiceImpl;
import org.kie.workbench.common.forms.editor.client.editor.FormEditorHelper;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandlerManager;
import org.kie.workbench.common.forms.editor.service.shared.VFSFormFinderService;
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
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.datamodeller.service.ServiceException;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.driver.FilterHolder;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriver;
import org.kie.workbench.common.services.datamodeller.driver.ModelDriverException;
import org.kie.workbench.common.services.datamodeller.driver.impl.JavaRoasterModelDriver;
import org.kie.workbench.common.services.datamodeller.driver.model.ModelDriverResult;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.base.options.CommentedOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.forms.jbpm.model.authoring.document.type.DocumentFieldType.DOCUMENT_TYPE;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormGenerationIntegrationTest {

    private static final String
            TEXTBOX_FIELDTYPE = new TextBoxFieldType().getTypeName(),
            INTEGERBOX_FIELDTYPE = new IntegerBoxFieldType().getTypeName(),
            CHECKBOX_FIELDTYPE = new CheckBoxFieldType().getTypeName(),
            DECIMALBOX_FIELDTYPE = new DecimalBoxFieldType().getTypeName(),
            TEXTAREA_FIELDTYPE = new TextAreaFieldType().getTypeName(),
            SUBFORM_FIELDTYPE = new SubFormFieldType().getTypeName(),
            DOCUMENT_FIELDTYPE = new DocumentFieldType().getTypeName(),
            MULTIPLESUBFORM_FIELDTYPE = new MultipleSubFormFieldType().getTypeName(),
            TEXBOX_FIELDTYPE = new TextBoxFieldType().getTypeName(),
            DATEPICKER_FIELDTYPE = new DatePickerFieldType().getTypeName(),
            PROCESS_NAME = "FormGenerationTest_Process",
            DATAOBJECT_FORM_ID = "ff9db399-fb46-4b3f-894a-81673dd5421c",
            DATA_OBJECTS_FOLDER = "model",
            PREPARED_NESTED_FORMS_FOLDER = "nestedforms",
            JAVA_MODEL_FOLDER = "data-object-sources",
            DEFINITIONS_FOLDER = "definitions",
            DEFINITION_PATH = DEFINITIONS_FOLDER + "/" + PROCESS_NAME + ".bpmn2";

    private static BPMNVFSFormDefinitionGeneratorService service;

    private static FieldManager fieldManager;
    private static FormModelHandlerManager formModelHandlerManager;
    private static VFSFormFinderService formFinderService;
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
    private static Path rootPathWithNestedForms;
    private static Path rootPathWithoutNestedForms;
    private static Definitions formGenerationProcessDefinitions;

    @Mock
    private static KieModule module;
    private static ClassLoader moduleClassLoader;
    private static FormEditorHelper formEditorHelper;
    private static FormModelerContent formModelerContent;
    private static BusinessProcessFormModel processFormModel;
    private static List<TaskFormModel> taskFormModels;

    @Mock
    private static DataModelerService dataModelerService;
    private static DataObjectFinderService finderService;
    private static DataModel dataModel;

    @Mock
    private static FilterHolder filterHolder;

    @BeforeClass
    public static void setup() throws ClassNotFoundException {
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
        formEditorHelper = new FormEditorHelper(fieldManager, null);
    }

    @Before
    public void init() {
        final String nestedformsUri = getUriOfResource(PREPARED_NESTED_FORMS_FOLDER);
        final String modelUri = getUriOfResource(JAVA_MODEL_FOLDER);

        rootPathWithNestedForms = PathFactory.newPath(DATA_OBJECTS_FOLDER, nestedformsUri);
        rootPathWithoutNestedForms = PathFactory.newPath(DATA_OBJECTS_FOLDER, modelUri);

        finderService = new DataObjectFinderServiceImpl(moduleService, dataModelerService);

        formModelHandlerManager = new TestFormModelHandlerManager(moduleService,
                                                                  moduleClassLoaderHelper,
                                                                  fieldManager,
                                                                  finderService);

        formFinderService = new VFSFormFinderServiceImpl(ioService,
                                                         moduleService,
                                                         formSerializer);

        service = new BPMNVFSFormDefinitionGeneratorService(fieldManager,
                                                            formModelHandlerManager,
                                                            formFinderService,
                                                            formSerializer,
                                                            ioService,
                                                            commentedOptionFactory,
                                                            formModelSynchronizationUtil);

        when(moduleService.resolveModule(any())).thenReturn(module);
        when(moduleClassLoaderHelper.getModuleClassLoader(any())).thenReturn(moduleClassLoader);

        generator = new BPMNFormModelGeneratorImpl(moduleService,
                                                   moduleClassLoaderHelper);
        processFormModel = generator.generateProcessFormModel(formGenerationProcessDefinitions,
                                                              rootPathWithNestedForms);
        taskFormModels = generator.generateTaskFormModels(formGenerationProcessDefinitions, rootPathWithNestedForms);
    }

    /**
     * Tests if all form definitions are generated correctly when you hit generate all in the designer.
     */

    @Test
    public void ProcessFormIsCorrectlyGenerated() {
        when(module.getRootPath()).thenReturn(rootPathWithNestedForms);

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
        when(module.getRootPath()).thenReturn(rootPathWithNestedForms);

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
        when(module.getRootPath()).thenReturn(rootPathWithNestedForms);

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
        when(module.getRootPath()).thenReturn(rootPathWithNestedForms);

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
        when(module.getRootPath()).thenReturn(rootPathWithNestedForms);

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
        when(module.getRootPath()).thenReturn(rootPathWithoutNestedForms);

        ModelDriver modelDriver = new JavaRoasterModelDriver(ioService,
                                                             Paths.convert(rootPathWithoutNestedForms),
                                                             moduleClassLoader,
                                                             filterHolder);
        try {
            ModelDriverResult result = modelDriver.loadModel();
            dataModel = result.getDataModel();
        } catch (ModelDriverException e) {
            throw new ServiceException("It was not possible to load model for URI: " + rootPathWithoutNestedForms.toURI(), e);
        }
        when(dataModelerService.loadModel(module)).thenReturn(dataModel);
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
                                                                               .filter(f -> f.getName().equals("FormGenerationTest_DataObject"))
                                                                               .collect(Collectors.toList()));

        final FormDefinition nestedDataObjectForm = Iterables.getOnlyElement(nestedForms.stream()
                                                                                     .filter(f -> f.getName().equals("FormGenerationTest_NestedObject"))
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
        assertThat(actualRootFormFields).containsExactlyElementsOf(expectedFormFields);
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
}
