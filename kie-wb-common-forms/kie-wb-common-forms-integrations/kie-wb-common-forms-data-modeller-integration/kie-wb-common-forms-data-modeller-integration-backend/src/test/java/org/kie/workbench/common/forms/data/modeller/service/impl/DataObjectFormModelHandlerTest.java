/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.data.modeller.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.service.DataObjectFinderService;
import org.kie.workbench.common.forms.editor.service.backend.SourceFormModelNotFoundException;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasMaxLength;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.HasPlaceHolder;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.datePicker.definition.DatePickerFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.CharacterBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.screens.datamodeller.backend.server.handler.JPADomainHandler;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.DataModelImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.PropertyTypeFactoryImpl;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataObjectFormModelHandlerTest extends AbstractDataObjectTest {

    private final String NESTED_CLASSNAME = "com.test.Address";

    private DataModel dataModel;

    private DataObject dataObject;

    private DataObjectFormModelHandler handler;

    private DataObjectFormModel formModel;

    private FieldManager fieldManager = new TestFieldManager();

    @Mock
    private KieModuleService moduleService;

    @Mock
    private DataModelerService dataModelerService;

    @Mock
    private Path path;

    private DataObjectFinderService finderService;

    @Mock
    private KieModule module;

    @Mock
    private ModuleClassLoaderHelper moduleClassLoaderHelper;

    @Mock
    private ClassLoader classLoader;

    @Before
    public void setUp() throws Exception {

        when(moduleService.resolveModule(any())).thenReturn(module);
        when(moduleClassLoaderHelper.getModuleClassLoader(module)).thenReturn(classLoader);
        when(classLoader.loadClass(any())).thenAnswer((Answer<Class>) invocation -> String.class);

        createModel();

        finderService = new DataObjectFinderServiceImpl(moduleService,
                                                        dataModelerService);

        handler = new DataObjectFormModelHandler(moduleService,
                                                 moduleClassLoaderHelper,
                                                 finderService,
                                                 new TestFieldManager()) {
            @Override
            protected Locale getLocale() {
                return Locale.ENGLISH;
            }
        };
        when(dataModelerService.loadModel(any())).thenReturn(dataModel);
        List<DataObjectFormModel> formModels = finderService.getAvailableDataObjects(path);
        formModel = formModels.get(0);
        handler.init(formModel,
                     path);
        handler.synchronizeFormModel();
    }

    @Test
    public void usesRestrictedPropertyName() throws Exception {
        final ObjectProperty property = mock(ObjectProperty.class);
        doReturn("serialVersionUID").when(property).getName();

        assertFalse(DataObjectFormModelHandler.isValidDataObjectProperty(property));
    }

    @Test
    public void usesRestrictedAnnotation() throws Exception {
        final ObjectProperty property = mock(ObjectProperty.class);

        doReturn(mock(Annotation.class)).when(property).getAnnotation("javax.persistence.Id");

        assertFalse(DataObjectFormModelHandler.isValidDataObjectProperty(property));
    }

    @Test
    public void usesLegalAnnotation() throws Exception {
        final ObjectProperty property = mock(ObjectProperty.class);

        doReturn(null).when(property).getAnnotation("I.am.OK");

        assertTrue(DataObjectFormModelHandler.isValidDataObjectProperty(property));
    }

    @Test
    public void dataTypesGenerateCorrectFieldDefinitions() {
        //all possible types of data object fields - expected field types couples
        Map<String, List<String>> expectedFieldTypes = new HashMap<>();
        expectedFieldTypes.put(DecimalBoxFieldDefinition.class.getSimpleName(),
                               Arrays.asList(BigDecimal.class.getName(),
                                             Double.class.getName(),
                                             Float.class.getName(),
                                             double.class.getName(),
                                             float.class.getName()));
        expectedFieldTypes.put(IntegerBoxFieldDefinition.class.getSimpleName(),
                               Arrays.asList(BigInteger.class.getName(),
                                             Byte.class.getName(),
                                             Integer.class.getName(),
                                             Long.class.getName(),
                                             Short.class.getName(),
                                             byte.class.getName(),
                                             int.class.getName(),
                                             long.class.getName(),
                                             short.class.getName()));
        expectedFieldTypes.put(CheckBoxFieldDefinition.class.getSimpleName(),
                               Arrays.asList(Boolean.class.getName(),
                                             boolean.class.getName()));
        expectedFieldTypes.put(TextBoxFieldDefinition.class.getSimpleName(),
                               Arrays.asList(String.class.getName()));
        expectedFieldTypes.put(CharacterBoxFieldDefinition.class.getSimpleName(),
                               Arrays.asList(Character.class.getName(),
                                             char.class.getName()));
        expectedFieldTypes.put(DatePickerFieldDefinition.class.getSimpleName(),
                               Arrays.asList(Date.class.getName(),
                                             LocalDate.class.getName(),
                                             LocalDateTime.class.getName(),
                                             OffsetDateTime.class.getName(),
                                             LocalTime.class.getName()));
        //TODO: Update after JBPM-5911 is fixed (move date-types from SubFormFieldType to DatePickerFieldType)
        expectedFieldTypes.put(SubFormFieldDefinition.class.getSimpleName(),
                               Collections.singletonList(NESTED_CLASSNAME));
        expectedFieldTypes.put(MultipleSubFormFieldDefinition.class.getSimpleName(),
                               Collections.singletonList(NESTED_CLASSNAME));

        formModel = handler.createFormModel(dataObject, path);

        List<FieldDefinition> formModelFields = formModel.getProperties().stream().map(fieldManager::getDefinitionByModelProperty).collect(Collectors.toList());
        formFieldsShouldNotBeGeneratedForPersistenceId(formModelFields);
        listsOfBasicDataTypesShouldBeExcludedFromTheFormFields(formModelFields);

        for (FieldDefinition field : formModelFields) {
            String dataType = field.getStandaloneClassName();
            String fieldDefinition = field.getClass().getSimpleName();
            assertTrue(expectedFieldTypes.get(fieldDefinition).contains(dataType));
        }
    }

    //JBPM-5718 reproducer
    private void formFieldsShouldNotBeGeneratedForPersistenceId(List<FieldDefinition> formModelFields) {
        assertNotNull(dataObject.getProperty("id"));

        List<ObjectProperty> properties = finderService.getDataObjectProperties(formModel.getClassName(),
                                                                                path);

        properties.forEach(property -> assertNotEquals("id",
                                                       property.getName()));

        assertEquals("Form field should be generated for every data field except of persistence id",
                     formModelFields.size(),
                     properties.size());
    }

    //JBPM-5912 reproducer, TODO: implement after JBPM-5912 is fixed
    private void listsOfBasicDataTypesShouldBeExcludedFromTheFormFields(List<FieldDefinition> formModelFields) {
    }

    @Test
    public void textBoxHasCorrectProperties() {
        checkCommonProperties("String");
    }

    @Test
    public void characterBoxHasCorrectProperties() {
        checkCommonProperties("char");
    }

    @Test
    public void integerBoxHasCorrectProperties() {
        checkCommonProperties("Short");
    }

    @Test
    public void decimalBoxHasCorrectProperties() {
        checkCommonProperties("Double");
    }

    @Test
    public void checkBoxHasCorrectProperties() {
        checkCommonProperties("Boolean");
    }

    @Test
    public void datePickerHasCorrectProperties() {
        formModel = handler.createFormModel(dataObject, path);

        DatePickerFieldDefinition datePicker = (DatePickerFieldDefinition) checkCommonProperties("Date");
        assertTrue(datePicker.getShowTime());
    }

    @Test
    public void subformHasCorrectProperties() {
        formModel = handler.createFormModel(dataObject, path);

        SubFormFieldDefinition subForm = (SubFormFieldDefinition) checkCommonProperties("address");
        assertEquals(null,
                     subForm.getNestedForm());
    }

    @Test
    public void multipleSubformHasCorrectProperties() {
        formModel = handler.createFormModel(dataObject, path);

        MultipleSubFormFieldDefinition multipleSubform = (MultipleSubFormFieldDefinition) checkCommonProperties("address_list");
        assertEquals(null,
                     multipleSubform.getCreationForm());
        assertEquals(null,
                     multipleSubform.getEditionForm());
        assertEquals(Collections.emptyList(),
                     multipleSubform.getColumnMetas());
    }

    private FieldDefinition checkCommonProperties(String dataFieldName) {

        ObjectProperty dataField = dataObject.getProperty(dataFieldName);

        FieldDefinition formField = fieldManager.getDefinitionByModelProperty(formModel.getProperty(dataFieldName));
        String dataFieldClassName = dataField.getClassName();
        TypeInfo fieldTypeInfo = formField.getFieldTypeInfo();

        //test common properties
        assertEquals(dataFieldClassName,
                     fieldTypeInfo.getClassName());
        assertNotEquals(TypeKind.ENUM,
                        fieldTypeInfo.getType());
        assertEquals(dataField.isMultiple(),
                     fieldTypeInfo.isMultiple());
        assertEquals(dataField.getName().toLowerCase(),
                     formField.getLabel().toLowerCase());
        assertEquals(dataField.getName(),
                     formField.getBinding());
        assertEquals(dataFieldClassName,
                     formField.getStandaloneClassName());
        assertFalse(formField.getReadOnly());
        assertFalse(formField.getRequired());
        assertTrue(formField.getValidateOnChange());

        //test interface specific properties
        if (formField instanceof HasPlaceHolder) {
            assertEquals(dataField.getName().toLowerCase(),
                         ((HasPlaceHolder) formField).getPlaceHolder().toLowerCase());
        }
        if (formField instanceof HasMaxLength) {
            long maxLength = ((HasMaxLength) formField).getMaxLength();
            assertTrue(formField instanceof CharacterBoxFieldDefinition ? maxLength == 1 : maxLength == 100);
        }
        return formField;
    }

    @Test
    public void testCheckModelSource() {
        Assertions.assertThatCode(() -> handler.checkSourceModel())
                .doesNotThrowAnyException();

        handler.dataObject = null;

        Assertions.assertThatThrownBy(() -> handler.checkSourceModel())
                .isInstanceOf(SourceFormModelNotFoundException.class);
    }

    private void createModel() {
        dataModel = new DataModelImpl();
        dataObject = dataModel.addDataObject("Person1");

        //makeTheClassPersistable
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("persistable",
                   true);
        JPADomainHandler jpaDomainHandler = new JPADomainHandler();
        jpaDomainHandler.setDefaultValues(dataObject,
                                          params);

        // adding serialVersionUID field
        addProperty(dataObject,
                    DataObjectFormModelHandler.SERIAL_VERSION_UID,
                    Long.class.getName(),
                    false,
                    false);

        //add all base type properties
        PropertyTypeFactoryImpl propertyTypeFactory = new PropertyTypeFactoryImpl();

        propertyTypeFactory.getBasePropertyTypes().forEach(baseProperty -> addProperty(dataObject,
                                                                                       baseProperty.getName(),
                                                                                       baseProperty.getClassName(),
                                                                                       false,
                                                                                       false));

        //add data object property
        addProperty(dataObject,
                    "address",
                    NESTED_CLASSNAME,
                    false,
                    false);

        //add list of data objects
        addProperty(dataObject,
                    "address_list",
                    NESTED_CLASSNAME,
                    true,
                    false);
    }
}
