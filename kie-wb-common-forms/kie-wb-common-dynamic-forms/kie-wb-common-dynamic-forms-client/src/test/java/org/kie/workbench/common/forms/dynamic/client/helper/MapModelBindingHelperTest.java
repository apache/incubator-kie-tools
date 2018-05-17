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

package org.kie.workbench.common.forms.dynamic.client.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.databinding.client.MapBindableProxy;
import org.jboss.errai.databinding.client.MapPropertyType;
import org.jboss.errai.databinding.client.PropertyType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.dynamic.test.util.TestFormGenerator;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.forms.dynamic.test.util.TestFormGenerator.ADDRESS_NUM;
import static org.kie.workbench.common.forms.dynamic.test.util.TestFormGenerator.ADDRESS_STREET;
import static org.kie.workbench.common.forms.dynamic.test.util.TestFormGenerator.EMPLOYEE_ADDRESS;
import static org.kie.workbench.common.forms.dynamic.test.util.TestFormGenerator.EMPLOYEE_AGE_BINDING;
import static org.kie.workbench.common.forms.dynamic.test.util.TestFormGenerator.EMPLOYEE_BIRTHDAY;
import static org.kie.workbench.common.forms.dynamic.test.util.TestFormGenerator.EMPLOYEE_MARRIED;
import static org.kie.workbench.common.forms.dynamic.test.util.TestFormGenerator.EMPLOYEE_NAME;
import static org.kie.workbench.common.forms.dynamic.test.util.TestFormGenerator.EMPLOYEE_SURNAME;

@RunWith(MockitoJUnitRunner.class)
public class MapModelBindingHelperTest {

    static final String NAME = "John";
    static final String SURNAME = "Snow";
    static final Integer AGE = 36;
    static final Date BIRTHDAY = new Date();
    static final Boolean MARRIED = false;
    static final String STREET = "Winterfell Castle";
    static final Integer NUM = 15;

    MapModelBindingHelper helper;

    MapModelRenderingContext context;

    FormDefinition employeeForm = TestFormGenerator.getEmployeeForm();

    FormDefinition addressForm = TestFormGenerator.getAddressForm();

    @Before
    public void init() {

        helper = new MapModelBindingHelper() {
            @Override
            protected void lookupPropertyGenerators() {
                // avoiding errai CDI lookup
            }
        };

        helper.initialize();

        context = new MapModelRenderingContext("");

        context.setRootForm(employeeForm);
        context.getAvailableForms().put(addressForm.getId(),
                                        addressForm);
    }

    @Test
    public void testInitContextWithEmptyModel() {
        context.setModel(new HashMap<>());

        helper.initContext(context);

        assertNotNull(context.getModel());
        assertTrue(context.getModel() instanceof MapBindableProxy);

        MapBindableProxy model = (MapBindableProxy) context.getModel();

        checkBindableProxy(model);

        assertNull(model.get(EMPLOYEE_NAME));
        assertNull(model.get(EMPLOYEE_SURNAME));
        assertNull(model.get(EMPLOYEE_AGE_BINDING));
        assertNull(model.get(EMPLOYEE_BIRTHDAY));
        assertNull(model.get(EMPLOYEE_MARRIED));
    }

    @Test
    public void testInitContextWithModel() {
        Map<String, Object> employeeMap = new HashMap<>();
        employeeMap.put(EMPLOYEE_NAME,
                        NAME);
        employeeMap.put(EMPLOYEE_SURNAME,
                        SURNAME);
        employeeMap.put(EMPLOYEE_AGE_BINDING,
                        AGE);
        employeeMap.put(EMPLOYEE_BIRTHDAY,
                        BIRTHDAY);
        employeeMap.put(EMPLOYEE_MARRIED,
                        MARRIED);

        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put(ADDRESS_STREET,
                       STREET);
        addressMap.put(ADDRESS_NUM,
                       NUM);

        employeeMap.put(EMPLOYEE_ADDRESS,
                        addressMap);

        context.setModel(employeeMap);

        helper.initContext(context);

        assertNotNull(context.getModel());
        assertTrue(context.getModel() instanceof MapBindableProxy);

        MapBindableProxy model = (MapBindableProxy) context.getModel();

        checkBindableProxy(model);

        checkModelPropertyValue(model,
                                EMPLOYEE_NAME,
                                NAME);
        checkModelPropertyValue(model,
                                EMPLOYEE_SURNAME,
                                SURNAME);
        checkModelPropertyValue(model,
                                EMPLOYEE_AGE_BINDING,
                                AGE);
        checkModelPropertyValue(model,
                                EMPLOYEE_BIRTHDAY,
                                BIRTHDAY);
        checkModelPropertyValue(model,
                                EMPLOYEE_MARRIED,
                                MARRIED);

        assertNotNull(model.get(EMPLOYEE_ADDRESS));
        assertTrue(model.get(EMPLOYEE_ADDRESS) instanceof MapBindableProxy);

        MapBindableProxy addressModel = (MapBindableProxy) model.get(EMPLOYEE_ADDRESS);
        checkModelPropertyValue(addressModel,
                                ADDRESS_STREET,
                                STREET);
        checkModelPropertyValue(addressModel,
                                ADDRESS_NUM,
                                NUM);
    }

    protected void checkModelPropertyValue(MapBindableProxy model,
                                           String property,
                                           Object expectedValue) {
        Object value = model.get(property);
        assertNotNull(value);
        assertSame(expectedValue,
                   value);
    }

    protected void checkBindableProxy(MapBindableProxy model) {
        assertFalse(model.getBeanProperties().isEmpty());

        checkSimpleTypeProperty(model.getBeanProperties().get(EMPLOYEE_NAME),
                                String.class);
        checkSimpleTypeProperty(model.getBeanProperties().get(EMPLOYEE_SURNAME),
                                String.class);
        checkSimpleTypeProperty(model.getBeanProperties().get(EMPLOYEE_AGE_BINDING),
                                Integer.class);
        checkSimpleTypeProperty(model.getBeanProperties().get(EMPLOYEE_BIRTHDAY),
                                Date.class);
        checkSimpleTypeProperty(model.getBeanProperties().get(EMPLOYEE_MARRIED),
                                Boolean.class);

        PropertyType addressPropertyType = model.getBeanProperties().get(EMPLOYEE_ADDRESS);
        assertNotNull(addressPropertyType);
        assertTrue(addressPropertyType instanceof MapPropertyType);

        MapPropertyType address = (MapPropertyType) addressPropertyType;

        checkSimpleTypeProperty(address.getPropertyTypes().get(ADDRESS_STREET),
                                String.class);
        checkSimpleTypeProperty(address.getPropertyTypes().get(ADDRESS_NUM),
                                Integer.class);
    }

    protected void checkSimpleTypeProperty(PropertyType propertyType,
                                           Class expectedClass) {
        assertNotNull(propertyType);
        assertSame(expectedClass,
                   propertyType.getType());
    }
}
