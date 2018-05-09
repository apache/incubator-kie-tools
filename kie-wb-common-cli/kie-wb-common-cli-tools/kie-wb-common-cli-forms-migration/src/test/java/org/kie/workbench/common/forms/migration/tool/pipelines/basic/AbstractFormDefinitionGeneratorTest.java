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

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Before;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestMetaDataEntryManager;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.migration.legacy.model.Field;
import org.kie.workbench.common.forms.migration.legacy.model.Form;
import org.kie.workbench.common.forms.migration.legacy.services.FormSerializationManager;
import org.kie.workbench.common.forms.migration.legacy.services.impl.FormSerializationManagerImpl;
import org.kie.workbench.common.forms.migration.tool.FormMigrationSummary;
import org.kie.workbench.common.forms.migration.tool.cdi.FormsMigrationServicesCDIWrapper;
import org.kie.workbench.common.forms.migration.tool.pipelines.MigrationContext;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.BPMNFormAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.DataObjectFormAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.fields.AbstractFieldAdapter;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormLayoutComponent;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FieldSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormDefinitionSerializerImpl;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormModelSerializer;
import org.kie.workbench.common.migration.cli.MigrationServicesCDIWrapper;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public abstract class AbstractFormDefinitionGeneratorTest {

    protected static final String INVOICE_MODEL = "org.jbpm.invoices.Invoice";
    protected static final String USER_MODEL = "org.jbpm.invoices.User";
    protected static final String LINE_MODEL = "org.jbpm.invoices.InvoiceLine";

    protected static final String DATAOBJECTS_RESOURCES = "/forms/dataObjects/";
    protected static final String BPMN_RESOURCES = "/forms/bpmn/";

    protected static final String ROOT_PATH = "default:///src/main/resources/";
    protected static final String INVOICE_FORM = "invoice.form";
    protected static final String USER_FORM = "user.form";
    protected static final String LINE_FORM = "line.form";
    protected static final String PROCESS_FORM = "invoices.invoices-taskform.form";
    protected static final String TASK_FORM = "modify-taskform.form";
    protected static final String INVOICES_BPMN = "invoices.bpmn2";

    protected static final String INVOICE_USER = "invoice_user";
    protected static final String INVOICE_LINES = "lines";
    protected static final String INVOICE_TOTAL = "invoice_total";

    protected static final String USER_LOGIN = "user_login";
    protected static final String USER_PASSWORD = "user_password";

    protected static final String LINE_PRODUCT = "line_product";
    protected static final String LINE_PRICE = "price";
    protected static final String LINE_QUANTITY = "quantity";
    protected static final String LINE_TOTAL = "total";

    @Mock
    protected WorkspaceProject workspaceProject;

    @Mock
    protected WeldContainer weldContainer;

    @Mock
    protected FormsMigrationServicesCDIWrapper formsMigrationServicesCDIWrapper;

    @Mock
    protected MigrationServicesCDIWrapper migrationServicesCDIWrapper;


    @Mock
    protected IOService ioService;

    @Mock
    protected Path path;

    protected FormSerializationManager serializer = new FormSerializationManagerImpl();

    protected FormDefinitionSerializer formDefinitionSerializer;

    protected SimpleFileSystemProvider simpleFileSystemProvider = null;

    protected MigrationContext context;

    protected FormDefinitionGenerator generator;

    @Before
    public void init() throws Exception {
        formDefinitionSerializer = new FormDefinitionSerializerImpl(new FieldSerializer(), new FormModelSerializer(), new TestMetaDataEntryManager());

        when(migrationServicesCDIWrapper.getIOService()).thenReturn(ioService);
        when(formsMigrationServicesCDIWrapper.getFormDefinitionSerializer()).thenReturn(formDefinitionSerializer);

        simpleFileSystemProvider = new SimpleFileSystemProvider();
        simpleFileSystemProvider.forceAsDefault();

        when(path.toURI()).thenReturn(ROOT_PATH);

        when(workspaceProject.getRootPath()).thenReturn(path);

        generator = new FormDefinitionGenerator(DataObjectFormAdapter::new, this::getBPMNAdapter);

        doInit();
    }

    protected abstract void doInit() throws Exception;

    protected List<JBPMFormModel> getProcessFormModels() {
        return new ArrayList<>();
    }

    protected void initForm(Consumer<Form> formConsumer, String resourcePath, String name, Path formPath) throws Exception {
        Form form = serializer.loadFormFromXML(IOUtils.toString(new InputStreamReader(this.getClass().getResourceAsStream(resourcePath + name))));
        formConsumer.accept(form);
        when(formPath.toURI()).thenReturn(ROOT_PATH + name);
        when(formPath.getFileName()).thenReturn(name);
    }

    protected BPMNFormAdapter getBPMNAdapter(MigrationContext context) {
        return new BPMNFormAdapter(context) {
            @Override
            protected void readWorkspaceBPMNModels() {
                workspaceBPMNFormModels.addAll(getProcessFormModels());
            }
        };
    }

    protected void checkMovedField(Field field, String targetForm) {
        Assertions.assertThat(field)
                .isNotNull()
                .hasFieldOrPropertyWithValue("movedToForm", targetForm);
    }

    protected void checkLayoutFormField(final LayoutComponent layoutComponent, final FieldDefinition fieldDefinition, final FormDefinition formDefinition) {
        Assertions.assertThat(layoutComponent)
                .isNotNull()
                .hasFieldOrPropertyWithValue("dragTypeName", AbstractFieldAdapter.DRAGGABLE_TYPE);

        Assertions.assertThat(layoutComponent.getProperties())
                .hasEntrySatisfying(FormLayoutComponent.FORM_ID, new Condition<>(formId -> formDefinition.getId().equals(formId), "Invalid formId"))
                .hasEntrySatisfying(FormLayoutComponent.FIELD_ID, new Condition<>(fieldId -> fieldDefinition.getId().equals(fieldId), "Invalid formId"));
    }

    protected void checkFieldDefinition(FieldDefinition fieldDefinition, String name, String label, String binding, Class<? extends FieldDefinition> expectedClass, FormDefinition formDefinition, Field field) {
        Assertions.assertThat(fieldDefinition)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("label", label)
                .hasFieldOrPropertyWithValue("binding", binding)
                .hasFieldOrPropertyWithValue("id", String.valueOf(field.getId()))
                .hasFieldOrPropertyWithValue("required", field.getFieldRequired())
                .hasFieldOrPropertyWithValue("readOnly", field.getReadonly())
                .isInstanceOf(expectedClass);

        ModelProperty property = formDefinition.getModel().getProperty(fieldDefinition.getBinding());
        Assertions.assertThat(property)
                .isNotNull()
                .hasFieldOrPropertyWithValue("typeInfo", fieldDefinition.getFieldTypeInfo());
    }

    protected void verifyInvoiceForm(FormMigrationSummary summary) {
        Form originalForm = summary.getOriginalForm().get();

        Assertions.assertThat(originalForm.getFormFields())
                .isNotEmpty()
                .hasSize(3);

        FormDefinition newForm = summary.getNewForm().get();

        Assertions.assertThat(newForm.getFields())
                .isNotEmpty()
                .hasSize(3);

        Assertions.assertThat(newForm.getModel())
                .isNotNull()
                .hasFieldOrPropertyWithValue("className", INVOICE_MODEL)
                .isInstanceOf(DataObjectFormModel.class);

        IntStream indexStream = IntStream.range(0, newForm.getFields().size());

        LayoutTemplate formLayout = newForm.getLayoutTemplate();

        assertNotNull(formLayout);

        Assertions.assertThat(formLayout.getRows())
                .isNotEmpty()
                .hasSize(newForm.getFields().size());

        indexStream.forEach(index -> {
            FieldDefinition fieldDefinition = newForm.getFields().get(index);

            switch (index) {
                case 0:
                    checkFieldDefinition(fieldDefinition, INVOICE_USER, "user (invoice)", "user", SubFormFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
                    break;
                case 1:
                    checkFieldDefinition(fieldDefinition, INVOICE_LINES, "lines (invoice)", "lines", MultipleSubFormFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
                    break;
                case 3:
                    checkFieldDefinition(fieldDefinition, INVOICE_LINES, "lines (invoice)", "lines", MultipleSubFormFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
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

            checkLayoutFormField(fieldColumn.getLayoutComponents().get(0), fieldDefinition, newForm);
        });
    }

    protected void verifyUserForm(FormMigrationSummary summary) {
        Form originalForm = summary.getOriginalForm().get();

        Assertions.assertThat(originalForm.getFormFields())
                .isNotEmpty()
                .hasSize(2);

        FormDefinition newForm = summary.getNewForm().get();

        Assertions.assertThat(newForm.getFields())
                .isNotEmpty()
                .hasSize(2);

        Assertions.assertThat(newForm.getModel())
                .isNotNull()
                .hasFieldOrPropertyWithValue("className", USER_MODEL)
                .isInstanceOf(DataObjectFormModel.class);

        IntStream indexStream = IntStream.range(0, newForm.getFields().size());

        LayoutTemplate formLayout = newForm.getLayoutTemplate();

        assertNotNull(formLayout);

        Assertions.assertThat(formLayout.getRows())
                .isNotEmpty()
                .hasSize(2);

        indexStream.forEach(index -> {
            FieldDefinition fieldDefinition = newForm.getFields().get(index);

            switch (index) {
                case 0:
                    checkFieldDefinition(fieldDefinition, USER_LOGIN, "login", "login", TextBoxFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
                    break;
                case 1:
                    checkFieldDefinition(fieldDefinition, USER_PASSWORD, "password", "password", TextBoxFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
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

            checkLayoutFormField(fieldColumn.getLayoutComponents().get(0), fieldDefinition, newForm);
        });
    }

    protected void verifyLineForm(FormMigrationSummary summary) {
        Form originalForm = summary.getOriginalForm().get();

        Assertions.assertThat(originalForm.getFormFields())
                .isNotEmpty()
                .hasSize(4);

        FormDefinition newForm = summary.getNewForm().get();

        Assertions.assertThat(newForm.getFields())
                .isNotEmpty()
                .hasSize(4);

        Assertions.assertThat(newForm.getModel())
                .isNotNull()
                .hasFieldOrPropertyWithValue("className", LINE_MODEL)
                .isInstanceOf(DataObjectFormModel.class);

        IntStream indexStream = IntStream.range(0, newForm.getFields().size());

        LayoutTemplate formLayout = newForm.getLayoutTemplate();

        assertNotNull(formLayout);

        Assertions.assertThat(formLayout.getRows())
                .isNotEmpty()
                .hasSize(1);

        LayoutRow fieldRow = formLayout.getRows().get(0);

        indexStream.forEach(index -> {
            FieldDefinition fieldDefinition = newForm.getFields().get(index);

            switch (index) {
                case 0:
                    checkFieldDefinition(fieldDefinition, LINE_PRODUCT, "product", "product", TextBoxFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
                    break;
                case 1:
                    checkFieldDefinition(fieldDefinition, LINE_PRICE, "price", "price", DecimalBoxFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
                    break;
                case 2:
                    checkFieldDefinition(fieldDefinition, LINE_QUANTITY, "quantity", "quantity", IntegerBoxFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
                    break;
                case 3:
                    checkFieldDefinition(fieldDefinition, LINE_TOTAL, "total", "total", DecimalBoxFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
                    break;
            }

            assertNotNull(fieldRow);

            Assertions.assertThat(fieldRow.getLayoutColumns())
                    .isNotEmpty()
                    .hasSize(4);

            LayoutColumn fieldColumn = fieldRow.getLayoutColumns().get(index);

            assertNotNull(fieldColumn);
            assertEquals("3", fieldColumn.getSpan());

            Assertions.assertThat(fieldColumn.getLayoutComponents())
                    .isNotEmpty()
                    .hasSize(1);

            checkLayoutFormField(fieldColumn.getLayoutComponents().get(0), fieldDefinition, newForm);
        });
    }
}
