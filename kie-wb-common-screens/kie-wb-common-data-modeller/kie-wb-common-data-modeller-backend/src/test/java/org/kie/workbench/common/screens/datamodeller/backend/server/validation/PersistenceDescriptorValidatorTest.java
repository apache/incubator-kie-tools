/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.backend.server.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistableDataObject;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.Property;
import org.kie.workbench.common.screens.datamodeller.model.persistence.TransactionType;
import org.kie.workbench.common.screens.datamodeller.validation.PersistenceDescriptorValidator;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.kie.workbench.common.screens.datamodeller.backend.server.validation.PersistenceDescriptorValidationMessages.newErrorMessage;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PersistenceDescriptorValidatorTest {

    private static final String VERSION = "version";

    private static final String JTA_DATASOURCE = "datasource";

    private static final String PERSISTENCE_UNIT_NAME = "persistenceUnitName";

    private static final String PERSISTENCE_PROVIDER = "persistenceProvider";

    private PersistenceDescriptorValidator validator;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private ModuleClassLoaderHelper classLoaderHelper;

    @Mock
    private Path path;

    @Mock
    private KieModule module;

    private ClassLoader classLoader;

    private PersistenceDescriptorModel descriptor;

    @Before
    public void setUp() {
        validator = new PersistenceDescriptorValidatorImpl(moduleService,
                                                           classLoaderHelper);
        descriptor = createValidDescriptor();

        classLoader = this.getClass().getClassLoader();
        when(moduleService.resolveModule(path)).thenReturn(module);
        when(classLoaderHelper.getModuleClassLoader(module)).thenReturn(classLoader);
    }

    @Test
    public void testValidateValidDescriptor() {
        List<ValidationMessage> result = validator.validate(path,
                                                            descriptor);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testValidateInvalidModule() {
        when(moduleService.resolveModule(path)).thenReturn(null);
        List<ValidationMessage> result = validator.validate(path,
                                                            descriptor);
        //no more errors are produced since the validations stops if the module is not found.
        assertEquals(1,
                     result.size());
        ValidationMessage expectedMessage = newErrorMessage(PersistenceDescriptorValidationMessages.DESCRIPTOR_NOT_BELONG_TO_PROJECT_ID,
                                                            PersistenceDescriptorValidationMessages.DESCRIPTOR_NOT_BELONG_TO_PROJECT);
        assertEquals(expectedMessage,
                     result.get(0));
    }

    @Test
    public void testValidateMissingPersistenceUnit() {
        descriptor.setPersistenceUnit(null);
        List<ValidationMessage> result = validator.validate(path,
                                                            descriptor);
        //no more errors are produced since the validation stops if the persistence unit is missing.
        assertEquals(1,
                     result.size());
        ValidationMessage expectedMessage = newErrorMessage(PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_NOT_FOUND_ID,
                                                            PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_NOT_FOUND);
        assertEquals(expectedMessage,
                     result.get(0));
    }

    @Test
    public void testValidateMissingPersistenceUnitName() {
        descriptor.getPersistenceUnit().setName(null);
        List<ValidationMessage> result = validator.validate(path,
                                                            descriptor);
        ValidationMessage expectedMessage;
        //persistence unit name validation should fail
        expectedMessage = newErrorMessage(PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_NAME_EMPTY_ID,
                                          PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_NAME_EMPTY);
        assertTrue(result.contains(expectedMessage));
    }

    @Test
    public void testValidateMissingPersistenceProvider() {
        descriptor.getPersistenceUnit().setProvider(null);
        List<ValidationMessage> result = validator.validate(path,
                                                            descriptor);
        ValidationMessage expectedMessage;
        //persistence provider validation should fail
        expectedMessage = newErrorMessage(PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_PROVIDER_ID,
                                          PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_PROVIDER_EMPTY);
        assertTrue(result.contains(expectedMessage));
    }

    @Test
    public void testValidateMissingTransactionType() {
        descriptor.getPersistenceUnit().setTransactionType(null);
        List<ValidationMessage> result = validator.validate(path,
                                                            descriptor);
        ValidationMessage expectedMessage;
        //transaction type validation should fail
        expectedMessage = newErrorMessage(PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_TRANSACTION_TYPE_EMPTY_ID,
                                          PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_TRANSACTION_TYPE_EMPTY);
        assertTrue(result.contains(expectedMessage));
    }

    @Test
    public void testValidateMissingJtaDataSource() {
        descriptor.getPersistenceUnit().setTransactionType(TransactionType.JTA);
        descriptor.getPersistenceUnit().setJtaDataSource(null);
        List<ValidationMessage> result = validator.validate(path,
                                                            descriptor);
        ValidationMessage expectedMessage;
        //jta datasource validation should fail
        expectedMessage = newErrorMessage(PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_JTA_DATASOURCE_EMPTY_ID,
                                          PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_JTA_DATASOURCE_EMPTY);
        assertTrue(result.contains(expectedMessage));
    }

    @Test
    public void testValidateMissingNonJtaDataSource() {
        descriptor.getPersistenceUnit().setTransactionType(TransactionType.RESOURCE_LOCAL);
        descriptor.getPersistenceUnit().setNonJtaDataSource(null);
        List<ValidationMessage> result = validator.validate(path,
                                                            descriptor);
        ValidationMessage expectedMessage;
        //jta datasource validation should fail
        expectedMessage = newErrorMessage(PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_NON_JTA_DATASOURCE_EMPTY_ID,
                                          PersistenceDescriptorValidationMessages.PERSISTENCE_UNIT_NON_JTA_DATASOURCE_EMPTY);
        assertTrue(result.contains(expectedMessage));
    }

    @Test
    public void testValidateNonPersistableClass() {
        //add a non persistable class
        descriptor.getPersistenceUnit().getClasses().add(new PersistableDataObject(NonPersistableClass1.class.getName()));
        List<ValidationMessage> result = validator.validate(path,
                                                            descriptor);
        ValidationMessage expectedMessage;
        //validation for the non persistable class should fail.
        expectedMessage = newErrorMessage(PersistenceDescriptorValidationMessages.CLASS_NOT_PERSISTABLE_ID,
                                          MessageFormat.format(PersistenceDescriptorValidationMessages.CLASS_NOT_PERSISTABLE,
                                                               NonPersistableClass1.class.getName()),
                                          NonPersistableClass1.class.getName());
        assertTrue(result.contains(expectedMessage));
    }

    @Test
    public void testValidatePropertyWithMissingName() {
        //add a property with no name
        descriptor.getPersistenceUnit().addProperty(new Property(null,
                                                                 "someValue"));
        List<ValidationMessage> result = validator.validate(path,
                                                            descriptor);
        ValidationMessage expectedMessage;
        //validation for the property with missing name should fail.
        expectedMessage = newErrorMessage(PersistenceDescriptorValidationMessages.INDEXED_PROPERTY_NAME_EMPTY_ID,
                                          MessageFormat.format(PersistenceDescriptorValidationMessages.INDEXED_PROPERTY_NAME_EMPTY,
                                                               "3"),
                                          "3");
        assertTrue(result.contains(expectedMessage));
    }

    @Test
    public void testValidatePropertyWithMissingValue() {
        //add a property with no name
        descriptor.getPersistenceUnit().addProperty(new Property("someName",
                                                                 null));
        List<ValidationMessage> result = validator.validate(path,
                                                            descriptor);
        ValidationMessage expectedMessage;
        //validation for the property with missing value should fail.
        expectedMessage = PersistenceDescriptorValidationMessages.newWarningMessage(PersistenceDescriptorValidationMessages.PROPERTY_VALUE_EMPTY_ID,
                                                                                    MessageFormat.format(PersistenceDescriptorValidationMessages.PROPERTY_VALUE_EMPTY,
                                                                                                         "someName"),
                                                                                    "someName");
        assertTrue(result.contains(expectedMessage));
    }

    private PersistenceDescriptorModel createValidDescriptor() {
        PersistenceDescriptorModel descriptor = new PersistenceDescriptorModel();
        descriptor.setVersion(VERSION);
        PersistenceUnitModel unit = new PersistenceUnitModel();
        descriptor.setPersistenceUnit(unit);
        unit.setJtaDataSource(JTA_DATASOURCE);
        unit.setName(PERSISTENCE_UNIT_NAME);
        unit.setProvider(PERSISTENCE_PROVIDER);
        unit.setTransactionType(TransactionType.JTA);

        List<Property> properties = new ArrayList<>();
        properties.add(new Property("name1",
                                    "value1"));
        properties.add(new Property("name2",
                                    "value2"));
        unit.setProperties(properties);

        List<PersistableDataObject> classes = new ArrayList<>();
        classes.add(new PersistableDataObject(PersistableClass1.class.getName()));
        classes.add(new PersistableDataObject(PersistableClass2.class.getName()));
        classes.add(new PersistableDataObject(PersistableClass3.class.getName()));
        descriptor.getPersistenceUnit().setClasses(classes);

        return descriptor;
    }
}