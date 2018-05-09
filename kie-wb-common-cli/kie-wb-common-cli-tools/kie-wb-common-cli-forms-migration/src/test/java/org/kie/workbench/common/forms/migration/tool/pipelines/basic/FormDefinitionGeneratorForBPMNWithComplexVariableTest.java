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

package org.kie.workbench.common.forms.migration.tool.pipelines.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.migration.legacy.model.DataHolder;
import org.kie.workbench.common.forms.migration.legacy.model.Field;
import org.kie.workbench.common.forms.migration.legacy.model.Form;
import org.kie.workbench.common.forms.migration.legacy.services.impl.FieldTypeBuilder;
import org.kie.workbench.common.forms.migration.tool.FormMigrationSummary;
import org.kie.workbench.common.forms.migration.tool.Resource;
import org.kie.workbench.common.forms.migration.tool.bpmn.BPMNAnalyzer;
import org.kie.workbench.common.forms.migration.tool.bpmn.BPMNProcess;
import org.kie.workbench.common.forms.migration.tool.pipelines.MigrationContext;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.DataObjectFormAdapter;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.migration.cli.RealSystemAccess;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FormDefinitionGeneratorForBPMNWithComplexVariableTest extends AbstractFormDefinitionGeneratorTest {

    @Mock
    private Path userFormPath;

    @Mock
    private Path lineFormPath;

    @Mock
    private Path processFormPath;

    @Mock
    private Path taskFormPath;

    private Form userForm;
    private Form lineForm;
    private Form processForm;
    private Form taskForm;

    @Override
    protected void doInit() throws Exception {

        List<FormMigrationSummary> summaries = new ArrayList<>();

        initForm(form -> userForm = form, DATAOBJECTS_RESOURCES, USER_FORM, userFormPath);
        summaries.add(new FormMigrationSummary(new Resource<>(userForm, userFormPath)));

        initForm(form -> lineForm = form, DATAOBJECTS_RESOURCES, LINE_FORM, lineFormPath);
        summaries.add(new FormMigrationSummary(new Resource<>(lineForm, lineFormPath)));

        initForm(form -> processForm = form, BPMN_RESOURCES, PROCESS_FORM, processFormPath);
        summaries.add(new FormMigrationSummary(new Resource<>(processForm, processFormPath)));

        initForm(form -> taskForm = form, BPMN_RESOURCES, TASK_FORM, taskFormPath);
        summaries.add(new FormMigrationSummary(new Resource<>(taskForm, taskFormPath)));

        context = new MigrationContext(workspaceProject, weldContainer, formsMigrationServicesCDIWrapper, new RealSystemAccess(), summaries, migrationServicesCDIWrapper);

        generator = new FormDefinitionGenerator(DataObjectFormAdapter::new, this::getBPMNAdapter);
    }

    @Override
    protected List<JBPMFormModel> getProcessFormModels() {
        BPMNAnalyzer analyzer = new BPMNAnalyzer();
        BPMNProcess process = analyzer.read(this.getClass().getResourceAsStream(BPMN_RESOURCES + INVOICES_BPMN));
        return process.getFormModels();
    }

    @Test
    public void testMigration() {
        generator.execute(context);

        Assertions.assertThat(context.getSummaries())
                .isNotEmpty()
                .hasSize(4);

        Assertions.assertThat(context.getExtraSummaries())
                .isNotEmpty()
                .hasSize(2);

        // 4 legacyforms + 4 migrated forms + 2 new forms for nested models
        verify(migrationServicesCDIWrapper, times(10)).write(any(Path.class), anyString(), anyString());

        context.getSummaries().forEach(summary -> {
            assertTrue(summary.getResult().isSuccess());
            switch (summary.getBaseFormName() + ".form") {
                case PROCESS_FORM:
                    verifyProcessForm(summary);
                    break;
                case TASK_FORM:
                    verifyTaskForm(summary);
                    break;
                case USER_FORM:
                    verifyUserForm(summary);
                    break;
                case LINE_FORM:
                    verifyLineForm(summary);
                    break;
            }
        });
    }

    private void verifyProcessForm(FormMigrationSummary summary) {
        verifyBPMNForm(summary, BusinessProcessFormModel.class);
    }

    private void verifyTaskForm(FormMigrationSummary summary) {
        verifyBPMNForm(summary, TaskFormModel.class);
    }

    private void verifyBPMNForm(FormMigrationSummary summary, Class<? extends JBPMFormModel> modelType) {
        Form originalForm = summary.getOriginalForm().get();

        Assertions.assertThat(originalForm.getFormFields())
                .hasSize(4);

        Field originalInvoiceUser = originalForm.getField(INVOICE_USER);
        Field originalLines = originalForm.getField(INVOICE_LINES);
        Field originalTotal = originalForm.getField(INVOICE_TOTAL);

        DataHolder originalDataHolder = originalForm.getHolders().iterator().next();

        String expectedExtraForm = summary.getBaseFormName() + "-" + originalDataHolder.getUniqeId();

        checkMovedField(originalInvoiceUser, expectedExtraForm);
        checkMovedField(originalLines, expectedExtraForm);
        checkMovedField(originalTotal, expectedExtraForm);

        Field invoiceField = originalForm.getField("invoice");

        Assertions.assertThat(invoiceField)
                .isNotNull()
                .hasFieldOrPropertyWithValue("bag", INVOICE_MODEL)
                .hasFieldOrPropertyWithValue("sourceLink", summary.getBaseFormName() + "-" + invoiceField.getFieldName())
                .hasFieldOrPropertyWithValue("inputBinding", originalDataHolder.getInputId())
                .hasFieldOrPropertyWithValue("outputBinding", originalDataHolder.getOuputId())
                .extracting("defaultSubform").doesNotContainNull();

        assertNotNull(invoiceField);
        assertEquals(FieldTypeBuilder.SUBFORM, invoiceField.getFieldType().getCode());

        FormDefinition newFormDefinition = summary.getNewForm().get();

        Assertions.assertThat(newFormDefinition.getModel())
                .isNotNull()
                .isInstanceOf(modelType);

        Assertions.assertThat(newFormDefinition.getModel().getProperties())
                .hasSize(1)
                .extracting("typeInfo.className")
                .contains(INVOICE_MODEL);

        Assertions.assertThat(newFormDefinition.getFields())
                .isNotNull()
                .hasSize(1);

        FieldDefinition newInvoiceField = newFormDefinition.getFieldByName("invoice");

        Assertions.assertThat(newInvoiceField)
                .isNotNull()
                .isInstanceOf(SubFormFieldDefinition.class)
                .hasFieldOrPropertyWithValue("standaloneClassName", INVOICE_MODEL);

        LayoutTemplate newFormLayout = newFormDefinition.getLayoutTemplate();

        assertNotNull(newFormLayout);

        Assertions.assertThat(newFormLayout.getRows())
                .isNotEmpty()
                .hasSize(1);

        LayoutRow newLayoutRow = newFormLayout.getRows().get(0);

        assertNotNull(newLayoutRow);

        Assertions.assertThat(newLayoutRow.getLayoutColumns())
                .isNotEmpty()
                .hasSize(1);

        LayoutColumn newLayoutColumn = newLayoutRow.getLayoutColumns().get(0);

        assertNotNull(newLayoutColumn);
        assertEquals("12", newLayoutColumn.getSpan());

        Assertions.assertThat(newLayoutColumn.getLayoutComponents())
                .isNotEmpty()
                .hasSize(1);

        checkLayoutFormField(newLayoutColumn.getLayoutComponents().get(0), newInvoiceField, newFormDefinition);

        FormMigrationSummary extraSummary = context.getExtraSummaries()
                .stream()
                .filter(extra -> extra.getBaseFormName().equals(expectedExtraForm))
                .findAny()
                .orElse(null);

        FormDefinition newExtraFormDefinition = extraSummary.getNewForm().get();

        checkInvoiceFormDefinition(newExtraFormDefinition, originalForm);
    }

    protected void checkInvoiceFormDefinition(FormDefinition invoiceForm, Form originalForm) {
        assertNotNull(invoiceForm);

        Assertions.assertThat(invoiceForm.getModel())
                .isNotNull()
                .isInstanceOf(DataObjectFormModel.class)
                .hasFieldOrPropertyWithValue("className", INVOICE_MODEL);

        Assertions.assertThat(invoiceForm.getFields())
                .isNotEmpty()
                .hasSize(3);

        IntStream indexStream = IntStream.range(0, invoiceForm.getFields().size());

        LayoutTemplate formLayout = invoiceForm.getLayoutTemplate();

        assertNotNull(formLayout);

        Assertions.assertThat(formLayout.getRows())
                .isNotEmpty()
                .hasSize(3);

        indexStream.forEach(index -> {
            FieldDefinition fieldDefinition = invoiceForm.getFields().get(index);

            switch (index) {
                case 0:
                    checkFieldDefinition(fieldDefinition, "invoice_user", "User Data:", "user", SubFormFieldDefinition.class, invoiceForm, originalForm.getField(fieldDefinition.getName()));
                    break;
                case 1:
                    checkFieldDefinition(fieldDefinition, "lines", "Invoice Lines", "lines", MultipleSubFormFieldDefinition.class, invoiceForm, originalForm.getField(fieldDefinition.getName()));
                    break;
                case 2:
                    checkFieldDefinition(fieldDefinition, "invoice_total", "Invoice Total:", "total", DecimalBoxFieldDefinition.class, invoiceForm, originalForm.getField(fieldDefinition.getName()));
                    break;
            }

            LayoutRow fieldRow = formLayout.getRows().get(index);

            assertNotNull(fieldRow);

            Assertions.assertThat(fieldRow.getLayoutColumns())
                    .isNotEmpty()
                    .hasSize(1);

            LayoutColumn fieldColumn = fieldRow.getLayoutColumns().get(0);

            assertNotNull(fieldColumn);
            assertEquals("12", fieldColumn.getSpan());

            Assertions.assertThat(fieldColumn.getLayoutComponents())
                    .isNotEmpty()
                    .hasSize(1);

            checkLayoutFormField(fieldColumn.getLayoutComponents().get(0), fieldDefinition, invoiceForm);
        });
    }
}
