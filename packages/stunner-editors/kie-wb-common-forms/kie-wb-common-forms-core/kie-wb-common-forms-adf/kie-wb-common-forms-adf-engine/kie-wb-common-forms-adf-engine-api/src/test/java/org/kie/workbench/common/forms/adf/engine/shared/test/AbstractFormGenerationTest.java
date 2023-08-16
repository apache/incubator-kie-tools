/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.adf.engine.shared.test;

import java.util.Date;

import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.I18nHelper;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.layout.LayoutGenerator;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.model.Address;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.model.Height;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.model.Person;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.model.Weapon;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.model.Weight;
import org.kie.workbench.common.forms.adf.service.definitions.FormDefinitionSettings;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FieldElement;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormLayoutComponent;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class AbstractFormGenerationTest {

    protected I18nHelper i18nHelper;

    protected FieldManager fieldManager = new TestFieldManager();

    protected TestFormGenerator generator;

    protected TestFormGenerationResourcesProvider resourcesProvider;

    protected Person model = new Person();

    protected void init() {
        i18nHelper = spy(I18nHelper.class);

        resourcesProvider = new TestFormGenerationResourcesProvider();

        generator = new TestFormGenerator(new LayoutGenerator(),
                                          i18nHelper);

        generator.registerProcessor(new TestFieldElementProcessor(fieldManager,
                                                                  new TestPropertyValueExtractor()));

        generator.registerResources(resourcesProvider);

        model.setName("John");
        model.setLastName("Snow");
        model.setMarried(Boolean.FALSE);
        model.setBirthDay(new Date());
        model.setWeight(new Weight(75));
        model.setHeight(new Height(1.80));
        model.setAddress(new Address("Main Street",
                                     1,
                                     "Winterfell"));
        model.getWeapons().add(new Weapon("Sword",
                                          10));
        model.getWeapons().add(new Weapon("Axe",
                                          15));
    }

    protected void testGeneratedForm(FormDefinition form,
                                     String modelName) {
        assertNotNull(form);

        FormDefinitionSettings settings = resourcesProvider.getDefinitionSettings().get(modelName);

        assertEquals(settings.getModelType(),
                     form.getId());
        assertEquals(settings.getModelType(),
                     form.getName());

        assertEquals(settings.getFormElements().size(),
                     form.getFields().size());

        settings.getFormElements().forEach(element -> {
            FieldElement fieldElement = (FieldElement) element;
            FieldDefinition field = form.getFieldByBinding(fieldElement.getBinding());

            assertNotNull(field);

            assertEquals(fieldElement.getName(),
                         field.getName());
            if (!fieldElement.getPreferredType().equals(FieldType.class)) {
                assertEquals(fieldElement.getPreferredType(),
                             field.getFieldType().getClass());
            }
            assertEquals(fieldElement.getTypeInfo().getClassName(),
                         field.getStandaloneClassName());
            assertEquals(fieldElement.isReadOnly(),
                         field.getReadOnly());
            assertEquals(fieldElement.isRequired(),
                         field.getRequired());
            assertEquals(fieldElement.getBinding(),
                         field.getBinding());
            verify(i18nHelper,
                   atLeast(1)).getTranslation(field.getName() + ".label");
        });

        assertNotNull(form.getLayoutTemplate());
        assertEquals(settings.getFormElements().size(),
                     form.getLayoutTemplate().getRows().size());

        form.getLayoutTemplate().getRows().forEach(row -> {
            assertNotNull(row);
            assertEquals(1,
                         row.getLayoutColumns().size());
            LayoutColumn column = row.getLayoutColumns().get(0);
            assertNotNull(column);
            assertEquals("12",
                         column.getSpan());
            assertEquals(1,
                         column.getLayoutComponents().size());

            LayoutComponent layoutComponent = column.getLayoutComponents().get(0);

            String formId = layoutComponent.getProperties().get(FormLayoutComponent.FORM_ID);
            assertNotNull(formId);
            assertEquals(form.getId(),
                         formId);

            String fieldId = layoutComponent.getProperties().get(FormLayoutComponent.FIELD_ID);
            assertNotNull(fieldId);
            FieldDefinition field = form.getFieldById(fieldId);
            assertNotNull(field);
        });
    }
}
