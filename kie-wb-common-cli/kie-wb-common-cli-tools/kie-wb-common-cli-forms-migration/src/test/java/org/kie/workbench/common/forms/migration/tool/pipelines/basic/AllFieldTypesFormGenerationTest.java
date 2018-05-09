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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition.StringListBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.StringRadioGroupFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.definition.TextAreaFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxBaseDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.jbpm.model.authoring.document.definition.DocumentFieldDefinition;
import org.kie.workbench.common.forms.migration.legacy.model.Field;
import org.kie.workbench.common.forms.migration.legacy.model.Form;
import org.kie.workbench.common.forms.migration.tool.FormMigrationSummary;
import org.kie.workbench.common.forms.migration.tool.Resource;
import org.kie.workbench.common.forms.migration.tool.pipelines.MigrationContext;
import org.kie.workbench.common.forms.migration.tool.util.FormsMigrationConstants;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.migration.cli.RealSystemAccess;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AllFieldTypesFormGenerationTest extends AbstractFormDefinitionGeneratorTest {

    private static final String ALL_FIELD_TYPES = "allFieldTypes.form";

    private Map<String, Class<? extends FieldDefinition>> fieldMappings = new HashMap<>();

    @Mock
    private Path userFormPath;


    private Form userForm;

    @Override
    protected void doInit() throws Exception {

        fieldMappings.put("subform", SubFormFieldDefinition.class);
        fieldMappings.put("multiple", MultipleSubFormFieldDefinition.class);
        fieldMappings.put("text", TextBoxBaseDefinition.class);
        fieldMappings.put("textarea", TextAreaFieldDefinition.class);
        fieldMappings.put("character", CharacterBoxFieldDefinition.class);
        fieldMappings.put("float", DecimalBoxFieldDefinition.class);
        fieldMappings.put("double", DecimalBoxFieldDefinition.class);
        fieldMappings.put("bigDecimal", DecimalBoxFieldDefinition.class);
        fieldMappings.put("bigInteger", IntegerBoxFieldDefinition.class);
        fieldMappings.put("byte", IntegerBoxFieldDefinition.class);
        fieldMappings.put("short", IntegerBoxFieldDefinition.class);
        fieldMappings.put("integer", IntegerBoxFieldDefinition.class);
        fieldMappings.put("long", IntegerBoxFieldDefinition.class);
        fieldMappings.put("email", TextBoxFieldDefinition.class);
        fieldMappings.put("boolean", CheckBoxFieldDefinition.class);
        fieldMappings.put("html", TextAreaFieldDefinition.class);
        fieldMappings.put("date", DatePickerFieldDefinition.class);
        fieldMappings.put("shortDate", DatePickerFieldDefinition.class);
        fieldMappings.put("document", DocumentFieldDefinition.class);
        fieldMappings.put("select", StringListBoxFieldDefinition.class);
        fieldMappings.put("radio", StringRadioGroupFieldDefinition.class);

        List<FormMigrationSummary> summaries = new ArrayList<>();

        initForm(form -> userForm = form, DATAOBJECTS_RESOURCES, ALL_FIELD_TYPES, userFormPath);
        summaries.add(new FormMigrationSummary(new Resource<>(userForm, userFormPath)));

        context = new MigrationContext(workspaceProject, weldContainer, formsMigrationServicesCDIWrapper, new RealSystemAccess(), summaries, migrationServicesCDIWrapper);
    }

    @Test
    public void testMigration() {
        generator.execute(context);

        Assertions.assertThat(context.getSummaries())
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(context.getExtraSummaries())
                .isEmpty();

        // 1 legacyforms + 1 migrated forms
        verify(migrationServicesCDIWrapper, times(2)).write(any(Path.class), anyString(), anyString());

        FormMigrationSummary summary = context.getSummaries().iterator().next();

        Form originalForm = summary.getOriginalForm().get();

        FormDefinition newForm = summary.getNewForm().get();

        assertNotNull(newForm);

        Assertions.assertThat(newForm.getFields())
                .isNotEmpty()
                .hasSize(fieldMappings.size());

        LayoutTemplate newLayout = newForm.getLayoutTemplate();

        assertNotNull(newLayout);

        Assertions.assertThat(newLayout.getRows())
                .isNotEmpty()
                .hasSize(fieldMappings.size() + 2); // fields + 2 decorators in original form

        List<LayoutRow> rows = newLayout.getRows();

        checkDecoratorRow(rows.get(0));
        checkDecoratorRow(rows.get(1));

        IntStream indexStream = IntStream.range(0, fieldMappings.size());

        indexStream.forEach(index -> {
            FieldDefinition newField = newForm.getFields().get(index);

            assertNotNull(newField);

            Field originalField = originalForm.getField(newField.getName());

            assertNotNull(originalField);

            checkFieldDefinition(newField, newField.getName(), newField.getLabel(), newField.getBinding(), fieldMappings.get(newField.getName()), newForm, originalField);

            LayoutRow row = rows.get(index + 2);

            LayoutComponent component = checkRow(row);

            checkLayoutFormField(component, newField, newForm);

        });
    }

    protected void checkDecoratorRow(LayoutRow row) {
        LayoutComponent component = checkRow(row);

        Assertions.assertThat(component)
                .isNotNull()
                .hasFieldOrPropertyWithValue("dragTypeName", FormsMigrationConstants.HTML_COMPONENT);
    }

    private LayoutComponent checkRow(LayoutRow row) {
        Assertions.assertThat(row.getLayoutColumns())
                .isNotEmpty()
                .hasSize(1);

        LayoutColumn column = row.getLayoutColumns().get(0);
        assertEquals("12", column.getSpan());

        Assertions.assertThat(column.getLayoutComponents())
                .isNotEmpty()
                .hasSize(1);

        return column.getLayoutComponents().get(0);
    }
}
