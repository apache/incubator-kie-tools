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

package org.kie.workbench.common.stunner.forms.backend.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.eclipse.bpmn2.Definitions;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.jbpm.simulation.util.BPMN2Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.data.modeller.service.DataObjectFinderService;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandlerManager;
import org.kie.workbench.common.forms.editor.service.shared.VFSFormFinderService;
import org.kie.workbench.common.forms.editor.service.shared.model.FormModelSynchronizationUtil;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.BPMNFormModelGenerator;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.BPMNFormGeneratorService;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.authoring.BPMNVFSFormDefinitionGeneratorService;
import org.kie.workbench.common.forms.jbpm.server.service.impl.BPMNFormModelGeneratorImpl;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.MetaDataEntry;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.impl.meta.entries.FieldReadOnlyEntry;
import org.kie.workbench.common.forms.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.forms.backend.gen.FormGenerationModelProviders;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormDefinitionGeneratorImplTest {

    private static final String PROCESS_NAME = "simple_process_generation";
    private static final String PROCESS_ID = "invoices.simple_process_generation";

    private static final String FULL_TASK_NAME = "full";
    private static final String FULL_TASK_ID = "0A60C2C0-DC97-41B5-8D77-092E42468C4B";

    private static final String INPUTS_TASK_NAME = "inputs";
    private static final String INPUTS_TASK_ID = "4BADBF9B-F9E9-40AE-ABCE-DD7DA7D814E3";

    private static final String OUTPUTS_TASK_NAME = "outputs";
    private static final String OUTPUTS_TASK_ID = "DC8FA0ED-EE7F-4FE6-9C3E-CCD8B7E6ACEA";

    private static final String RESOURCES_PATH = "/definitions/";

    private static final String PROCESS_PATH = RESOURCES_PATH + "simple_process_generation.bpmn";

    private static final String STRING_VARIABLE = "_string";
    private static final String INTEGER_VARIABLE = "_integer";
    private static final String BOOLEAN_VARIABLE = "_boolean";
    private static final String FLOAT_VARIABLE = "_float";
    private static final String OBJECT_VARIABLE = "_object";

    private static final Map<String, String> VARIABLES = new HashMap<String, String>() {{
        put(STRING_VARIABLE,
            String.class.getName());
        put(INTEGER_VARIABLE,
            Integer.class.getName());
        put(BOOLEAN_VARIABLE,
            Boolean.class.getName());
        put(FLOAT_VARIABLE,
            Float.class.getName());
        put(OBJECT_VARIABLE,
            Object.class.getName());
    }};

    @Mock
    private FormGenerationModelProviders formGenerationModelProviders;

    @Mock
    private IOService ioService;

    @Mock
    private Path path;
    @Mock
    private KieModuleService kieModuleService;
    @Mock
    private KieModule module;
    @Mock
    private ModuleClassLoaderHelper moduleClassLoaderHelper;
    @Mock
    private ClassLoader moduleClassLoader;

    @Mock
    private FormDefinitionSerializer formDefinitionSerializer;
    @Mock
    private VFSFormFinderService formFinderService;
    @Mock
    private CommentedOptionFactory commentedOptionFactory;
    @Mock
    private FormModelSynchronizationUtil synchronizationUtil;
    @Mock
    private DataObjectFinderService dataObjectFinderService;

    private FormDefinitionGeneratorImpl generator;

    @Mock
    private Diagram diagram;
    @Mock
    private Metadata metadata;
    @Mock
    private Path diagramPath;
    @Mock
    private Graph graph;

    @Captor
    private ArgumentCaptor<JBPMFormModel> formModelArgumentCaptor;

    @Captor
    private ArgumentCaptor<FormDefinition> formDefinitionArgumentCaptor;

    @Before
    public void init() throws Exception {

        SimpleFileSystemProvider simpleFileSystemProvider = new SimpleFileSystemProvider();
        simpleFileSystemProvider.forceAsDefault();

        Definitions processDefinitions = BPMN2Utils.getDefinitions(FormDefinitionGeneratorImplTest.class.getResourceAsStream(PROCESS_PATH));

        // Prepare BPMNFormModelGenerator
        when(kieModuleService.resolveModule(any())).thenReturn(module);
        when(module.getRootPath()).thenReturn(path);
        when(moduleClassLoaderHelper.getModuleClassLoader(module)).thenReturn(moduleClassLoader);
        when(moduleClassLoader.loadClass(anyString())).thenAnswer(invocation -> Object.class);
        BPMNFormModelGenerator bpmnFormModelGenerator = spy(new BPMNFormModelGeneratorImpl(kieModuleService, moduleClassLoaderHelper));

        FormModelHandlerManager formModelHandlerManager = new TestFormModelHandlerManager(kieModuleService,
                                                                                          moduleClassLoaderHelper,
                                                                                          new TestFieldManager()
        );

        BPMNFormGeneratorService<Path> bpmnFormGeneratorService = new BPMNVFSFormDefinitionGeneratorService(new TestFieldManager(),
                                                                                                            formModelHandlerManager,
                                                                                                            formFinderService,
                                                                                                            formDefinitionSerializer,
                                                                                                            ioService,
                                                                                                            commentedOptionFactory,
                                                                                                            synchronizationUtil);

        generator = spy(new TestFormDefinitionGeneratorImpl(formGenerationModelProviders,
                                                            ioService,
                                                            bpmnFormModelGenerator,
                                                            formDefinitionSerializer,
                                                            bpmnFormGeneratorService,
                                                            processDefinitions));

        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getPath()).thenReturn(diagramPath);
        when(diagram.getGraph()).thenReturn(graph);
        when(diagramPath.toURI()).thenReturn("default:///src/main/resources" + PROCESS_PATH);
    }

    @Test
    public void testGenerateProcessForm() {
        generator.generateProcessForm(diagram);

        verify(generator, times(1)).createFormForModel(formModelArgumentCaptor.capture(), any());
        verify(formDefinitionSerializer, times(1)).serialize(formDefinitionArgumentCaptor.capture());

        verify(ioService, times(1)).createFile(any());
        verify(ioService, times(1)).write(any(), anyString());

        JBPMFormModel formModel = formModelArgumentCaptor.getValue();

        Assertions.assertThat(formModel)
                .isNotNull()
                .isInstanceOf(BusinessProcessFormModel.class);

        checkProcessFormGenerated((BusinessProcessFormModel) formModel, formDefinitionArgumentCaptor.getValue());
    }

    @Test
    public void testGenerateFullTaskForm() {
        checkSingleSelectedTaskFormGenerated(FULL_TASK_ID, FULL_TASK_NAME);
    }

    @Test
    public void testGenerateInputsTaskForm() {
        checkSingleSelectedTaskFormGenerated(INPUTS_TASK_ID, INPUTS_TASK_NAME);
    }

    @Test
    public void testGenerateOutputsTaskForm() {
        checkSingleSelectedTaskFormGenerated(OUTPUTS_TASK_ID, OUTPUTS_TASK_NAME);
    }

    private void checkSingleSelectedTaskFormGenerated(String taskId, String taskName) {
        generator.generateSelectedForms(diagram, taskId);

        verify(generator, times(1)).createFormForModel(formModelArgumentCaptor.capture(), any());

        verify(formDefinitionSerializer, times(1)).serialize(formDefinitionArgumentCaptor.capture());

        verify(ioService, times(1)).startBatch(any());
        verify(ioService, times(1)).getFileSystem(any());
        verify(ioService, times(1)).createFile(any());
        verify(ioService, times(1)).write(any(), anyString());
        verify(ioService, times(1)).endBatch();

        JBPMFormModel formModel = formModelArgumentCaptor.getValue();

        FormDefinition taskForm = formDefinitionArgumentCaptor.getValue();

        Assertions.assertThat(formModel)
                .isNotNull()
                .isInstanceOf(TaskFormModel.class);

        checkTaskFormGenerated((TaskFormModel) formModel, taskName, taskForm);
    }

    @Test
    public void testGenerateSelectedTaskForms() {
        generator.generateSelectedForms(diagram, FULL_TASK_ID, INPUTS_TASK_ID, OUTPUTS_TASK_ID);

        verify(generator, times(3)).createFormForModel(formModelArgumentCaptor.capture(), any());
        verify(formDefinitionSerializer, times(3)).serialize(formDefinitionArgumentCaptor.capture());

        verify(ioService, times(1)).startBatch(any());
        verify(ioService, times(1)).getFileSystem(any());
        verify(ioService, times(3)).createFile(any());
        verify(ioService, times(3)).write(any(), anyString());
        verify(ioService, times(1)).endBatch();

        List<JBPMFormModel> formModels = formModelArgumentCaptor.getAllValues();

        List<FormDefinition> taskForms = formDefinitionArgumentCaptor.getAllValues();

        Assertions.assertThat(formModels)
                .isNotEmpty()
                .hasSize(taskForms.size())
                .allMatch(formModel -> formModel instanceof TaskFormModel);

        for (int i = 0; i < formModels.size(); i++) {
            TaskFormModel taskFormModel = (TaskFormModel) formModels.get(i);
            FormDefinition taskForm = taskForms.get(i);

            checkTaskFormGenerated(taskFormModel, taskFormModel.getTaskName(), taskForm);
        }
    }

    @Test
    public void testGenerateAllForms() {
        generator.generateAllForms(diagram);

        verify(generator, times(4)).createFormForModel(formModelArgumentCaptor.capture(), any());
        verify(formDefinitionSerializer, times(4)).serialize(formDefinitionArgumentCaptor.capture());

        verify(ioService, times(1)).startBatch(any());
        verify(ioService, times(1)).getFileSystem(any());
        verify(ioService, times(4)).createFile(any());
        verify(ioService, times(4)).write(any(), anyString());
        verify(ioService, times(1)).endBatch();

        List<JBPMFormModel> formModels = formModelArgumentCaptor.getAllValues();
        List<FormDefinition> forms = formDefinitionArgumentCaptor.getAllValues();

        Assertions.assertThat(formModels)
                .isNotEmpty()
                .hasSize(forms.size())
                .hasSize(4);

        for (int i = 0; i < formModels.size(); i++) {
            JBPMFormModel formModel = formModels.get(i);

            FormDefinition form = forms.get(i);

            if (formModel instanceof BusinessProcessFormModel) {
                checkProcessFormGenerated((BusinessProcessFormModel) formModel, form);
            } else {
                TaskFormModel taskFormModel = (TaskFormModel) formModel;
                checkTaskFormGenerated(taskFormModel, taskFormModel.getTaskName(), form);
            }
        }
    }

    private void checkProcessFormGenerated(BusinessProcessFormModel formModel, FormDefinition formDefinition) {
        checkProcessFormModel(formModel);
        checkGeneratedForm(formDefinition, formModel, false);
    }

    private void checkTaskFormGenerated(TaskFormModel taskFormModel, String expectedTaskName, FormDefinition taskForm) {
        boolean readOnly = expectedTaskName.equals(INPUTS_TASK_NAME);
        checkTaskFormModel(taskFormModel, expectedTaskName, readOnly);
        checkGeneratedForm(taskForm, taskFormModel, readOnly);
    }

    private void checkGeneratedForm(FormDefinition formDefinition, JBPMFormModel expectedFormModel, boolean readOnly) {
        Assertions.assertThat(formDefinition)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", expectedFormModel.getFormName() + ".frm");

        Assertions.assertThat(formDefinition.getFields())
                .isNotEmpty()
                .hasSize(VARIABLES.size());

        assertEquals(expectedFormModel, formDefinition.getModel());

        VARIABLES.forEach((key, value) -> {
            FieldDefinition field = formDefinition.getFieldByBinding(key);
            switch (key) {
                case STRING_VARIABLE:
                    checkStringField(field, readOnly);
                    break;
                case INTEGER_VARIABLE:
                    checkIntegerField(field, readOnly);
                    break;
                case BOOLEAN_VARIABLE:
                    checkBooleanField(field, readOnly);
                    break;
                case FLOAT_VARIABLE:
                    checkFloatField(field, readOnly);
                    break;
                case OBJECT_VARIABLE:
                    checkObjectField(field, readOnly);
                    break;
            }
        });
    }

    private void checkStringField(FieldDefinition field, boolean readOnly) {
        checkFieldDefinition(field, TextBoxFieldDefinition.class, STRING_VARIABLE, readOnly);
    }

    private void checkIntegerField(FieldDefinition field, boolean readOnly) {
        checkFieldDefinition(field, IntegerBoxFieldDefinition.class, INTEGER_VARIABLE, readOnly);
    }

    private void checkBooleanField(FieldDefinition field, boolean readOnly) {
        checkFieldDefinition(field, CheckBoxFieldDefinition.class, BOOLEAN_VARIABLE, readOnly);
    }

    private void checkFloatField(FieldDefinition field, boolean readOnly) {
        checkFieldDefinition(field, DecimalBoxFieldDefinition.class, FLOAT_VARIABLE, readOnly);
    }

    private void checkObjectField(FieldDefinition field, boolean readOnly) {
        checkFieldDefinition(field, TextAreaFieldDefinition.class, OBJECT_VARIABLE, readOnly);
    }

    private void checkFieldDefinition(FieldDefinition field, Class<? extends FieldDefinition> type, String fieldName, boolean readOnly) {
        Assertions.assertThat(field)
                .isNotNull()
                .isInstanceOf(type)
                .hasFieldOrPropertyWithValue("name", fieldName)
                .hasFieldOrPropertyWithValue("binding", fieldName)
                .hasFieldOrPropertyWithValue("readOnly", readOnly)
                .hasFieldOrPropertyWithValue("standaloneClassName", VARIABLES.get(fieldName));
    }

    private void checkProcessFormModel(BusinessProcessFormModel formModel) {
        Assertions.assertThat(formModel)
                .hasFieldOrPropertyWithValue("processId", PROCESS_ID)
                .hasFieldOrPropertyWithValue("processName", PROCESS_NAME);

        Assertions.assertThat(formModel.getProperties())
                .isNotEmpty()
                .hasSize(VARIABLES.size());

        VARIABLES.forEach((key, value) -> {
            ModelProperty property = formModel.getProperty(key);
            assertNotNull(property);
            assertEquals(value, property.getTypeInfo().getClassName());
        });
    }

    private void checkTaskFormModel(TaskFormModel formModel, String taskName, boolean readOnly) {
        Assertions.assertThat(formModel)
                .hasFieldOrPropertyWithValue("processId", PROCESS_ID)
                .hasFieldOrPropertyWithValue("taskName", taskName);

        Assertions.assertThat(formModel.getProperties())
                .isNotEmpty()
                .hasSize(VARIABLES.size());

        VARIABLES.forEach((key, value) -> {
            ModelProperty property = formModel.getProperty(key);
            assertNotNull(property);
            assertEquals(value, property.getTypeInfo().getClassName());

            MetaDataEntry readOnlyEntry = property.getMetaData().getEntry(FieldReadOnlyEntry.NAME);

            Assertions.assertThat(readOnlyEntry)
                    .isNotNull()
                    .isInstanceOf(FieldReadOnlyEntry.class)
                    .hasFieldOrPropertyWithValue("value", readOnly);
        });
    }
}
