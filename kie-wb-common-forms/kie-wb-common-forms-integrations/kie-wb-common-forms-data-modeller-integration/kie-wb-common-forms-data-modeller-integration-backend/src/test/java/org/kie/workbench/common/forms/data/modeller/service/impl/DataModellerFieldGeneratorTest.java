/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.data.modeller.service.impl;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.screens.datamodeller.model.maindomain.MainDomainAnnotations;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationDefinitionImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DataModellerFieldGeneratorTest extends AbstractDataObjectTest {

    public static final String PACKAGE = "org.kie.workbench.common.forms.test";
    public static final String DATA_OBJECT_NAME = "Test";

    public static final String NESTED_MODEL_TYPE = PACKAGE + ".NestedModel";

    public static final String MODEL_NAME = "test";

    public static final int EXPECTED_FIELDS = 6;

    protected DataModellerFieldGenerator generator;

    @Before
    public void initTest() {
        generator = new DataModellerFieldGenerator(new TestFieldManager());
    }

    @Test
    public void testBasicDataObject() {
        DataObject dataObject = generateDataObject(false,
                                                   false);

        checkGeneratedFields(dataObject,
                             generator.getFieldsFromDataObject(MODEL_NAME,
                                                               dataObject),
                             false);
    }

    @Test
    public void testBasicDataObjectWithLabel() {
        DataObject dataObject = generateDataObject(false,
                                                   true);

        checkGeneratedFields(dataObject,
                             generator.getFieldsFromDataObject(MODEL_NAME,
                                                               dataObject),
                             true);
    }

    @Test
    public void testDataObjectWithPersistence() {
        DataObject dataObject = generateDataObject(true,
                                                   false);

        checkGeneratedFields(dataObject,
                             generator.getFieldsFromDataObject(MODEL_NAME,
                                                               dataObject),
                             false);
    }

    @Test
    public void testDataObjectWithPersistenceAndLabel() {
        DataObject dataObject = generateDataObject(true,
                                                   true);

        checkGeneratedFields(dataObject,
                             generator.getFieldsFromDataObject(MODEL_NAME,
                                                               dataObject),
                             true);
    }

    protected void checkGeneratedFields(DataObject dataObject,
                                        List<FieldDefinition> fields,
                                        boolean withLabels) {
        assertNotNull(fields);
        assertFalse(fields.isEmpty());
        assertEquals(EXPECTED_FIELDS,
                     fields.size());

        fields.forEach(fieldDefinition -> {
            assertNotNull(fieldDefinition.getBinding());

            ObjectProperty property = dataObject.getProperty(fieldDefinition.getBinding());
            assertNotNull(property);

            assertEquals(property.getClassName(),
                         fieldDefinition.getStandaloneClassName());

            if (withLabels) {
                assertEquals(LABEL_SUFFIX + property.getName(),
                             fieldDefinition.getLabel());
            } else {
                assertEquals(property.getName(),
                             fieldDefinition.getLabel());
            }

            if (property.isMultiple()) {
                assertTrue(fieldDefinition instanceof MultipleSubFormFieldDefinition);
            }
        });
    }

    protected DataObject generateDataObject(boolean withPersistence,
                                            boolean withLabels) {
        DataObject result = new DataObjectImpl(PACKAGE,
                                               DATA_OBJECT_NAME);

        addProperty(result,
                    DataModellerFieldGenerator.SERIAL_VERSION_UID,
                    Long.class.getName(),
                    false,
                    false);

        if (withPersistence) {
            ObjectProperty property = addProperty(result,
                                                  "id",
                                                  Long.class.getName(),
                                                  false,
                                                  false);
            property.addAnnotation(new AnnotationImpl(new AnnotationDefinitionImpl(DataModellerFieldGenerator.PERSISTENCE_ANNOTATION)));
        }

        addProperty(result,
                    "text",
                    String.class.getName(),
                    false,
                    withLabels);
        addProperty(result,
                    "integer",
                    Integer.class.getName(),
                    false,
                    withLabels);
        addProperty(result,
                    "date",
                    Date.class.getName(),
                    false,
                    withLabels);
        addProperty(result,
                    "boolean",
                    Boolean.class.getName(),
                    false,
                    withLabels);
        addProperty(result,
                    "model",
                    NESTED_MODEL_TYPE,
                    false,
                    withLabels);
        addProperty(result,
                    "multipleModel",
                    NESTED_MODEL_TYPE,
                    true,
                    withLabels);

        return result;
    }
}
