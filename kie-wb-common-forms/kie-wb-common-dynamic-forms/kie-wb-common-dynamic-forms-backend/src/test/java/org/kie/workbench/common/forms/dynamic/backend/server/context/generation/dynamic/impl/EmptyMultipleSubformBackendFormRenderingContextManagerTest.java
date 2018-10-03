/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl.model.Person;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.JavaFormModel;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.PortableJavaModel;
import org.kie.workbench.common.forms.model.impl.TypeInfoImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class EmptyMultipleSubformBackendFormRenderingContextManagerTest extends AbstractBackendFormRenderingContextManagerTest {

    protected SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    protected List<Person> persons;

    @Test
    public void testEmptyModels() {
        try {
            initContentMarshallerClassLoader(Person.class, false);

            Map<String, Object> formValues = doReadNestedData();

            List<Map<String, Object>> personMaps = (List<Map<String, Object>>) formValues.get("persons");

            Map<String, Object> bran = new HashMap<>();
            bran.put("id",
                     4);
            bran.put("name",
                     "Bran");
            bran.put("lastName",
                     "Stark");
            bran.put("birthday",
                     sdf.parse("14-01-2000"));

            Map<String, Object> sansa = new HashMap<>();
            sansa.put("id",
                      5);
            sansa.put("name",
                      "Sansa");
            sansa.put("lastName",
                      "Stark");
            sansa.put("birthday",
                      sdf.parse("14-11-2005"));

            personMaps.add(bran);
            personMaps.add(sansa);

            Map<String, Object> result = contextManager.updateContextData(context.getTimestamp(),
                                                                          formValues).getFormData();

            assertNotNull("Result cannot be null ",
                          result);
            assertTrue("Result must contain only one entry",
                       result.size() == 1);

            assertTrue("Processed map must contain value for field 'person'",
                       result.containsKey("persons"));
            assertNotNull("Processed map must contain value for field 'person'",
                          result.get("persons"));
            assertTrue("Persons must be a List",
                       result.get("persons") instanceof List);

            List<Person> value = (List) result.get("persons");

            assertEquals("There should be 2 persons",
                         2,
                         value.size());

            String[] names = new String[]{"Bran", "Sansa"};

            for (int i = 0; i < value.size(); i++) {
                assertEquals("Name must be equal",
                             names[i],
                             value.get(i).getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Map<String, Object> doReadNestedData() {
        return context.getRenderingContext().getModel();
    }

    @Override
    protected FormDefinition[] getNestedForms() {

        JavaFormModel model = new PortableJavaModel(Person.class.getName());

        FormDefinition creationForm = new FormDefinition(model);
        creationForm.setId("person-creation");

        FieldDefinition field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(Long.class.getName()));
        field.setName("id");
        field.setBinding("id");
        creationForm.getFields().add(field);

        field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(String.class.getName()));
        field.setName("name");
        field.setBinding("name");
        creationForm.getFields().add(field);

        field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(String.class.getName()));
        field.setName("lastName");
        field.setBinding("lastName");
        creationForm.getFields().add(field);

        field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(Date.class.getName()));
        field.setName("birthday");
        field.setBinding("birthday");
        creationForm.getFields().add(field);

        FormDefinition editionForm = new FormDefinition(model);
        editionForm.setId("person-edition");

        field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(Long.class.getName()));
        field.setName("id");
        field.setBinding("id");
        editionForm.getFields().add(field);

        field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(String.class.getName()));
        field.setName("name");
        field.setBinding("name");
        editionForm.getFields().add(field);

        field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(String.class.getName()));
        field.setName("lastName");
        field.setBinding("lastName");
        editionForm.getFields().add(field);

        field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(Date.class.getName()));
        field.setName("birthday");
        field.setBinding("birthday");
        editionForm.getFields().add(field);

        return new FormDefinition[]{creationForm, editionForm};
    }

    @Override
    protected FormDefinition getRootForm() {
        FormDefinition form = new FormDefinition(new PortableJavaModel(Person.class.getName()));
        form.setId("form");

        FieldDefinition field = fieldManager.getDefinitionByDataType(new TypeInfoImpl(TypeKind.OBJECT,
                                                                                      Person.class.getName(),
                                                                                      true));
        field.setName("persons");
        field.setBinding("persons");

        MultipleSubFormFieldDefinition multpleSubForm = (MultipleSubFormFieldDefinition) field;

        multpleSubForm.setCreationForm("person-creation");
        multpleSubForm.setEditionForm("person-edition");

        form.getFields().add(field);

        return form;
    }

    @Override
    protected Map<String, Object> generateFormData() {
        return new HashMap<>();
    }
}
