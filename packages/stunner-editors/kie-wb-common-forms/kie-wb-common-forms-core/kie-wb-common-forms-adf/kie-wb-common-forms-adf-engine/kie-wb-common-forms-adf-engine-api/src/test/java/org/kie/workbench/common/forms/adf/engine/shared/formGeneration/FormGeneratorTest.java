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


package org.kie.workbench.common.forms.adf.engine.shared.formGeneration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.model.Person;
import org.kie.workbench.common.forms.adf.engine.shared.test.AbstractFormGenerationTest;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class FormGeneratorTest extends AbstractFormGenerationTest {

    @Before
    @Override
    public void init() {
        super.init();
    }

    @Test
    public void testGenerateFormForModel() {
        FormDefinition form = generator.generateFormForModel(model);

        testGeneratedForm(form,
                          Person.class.getName());
    }

    @Test
    public void testGenerateFormForClass() {
        FormDefinition form = generator.generateFormForClass(Person.class);

        testGeneratedForm(form,
                          Person.class.getName());
    }

    @Test
    public void testGenerateFormForClassName() {
        FormDefinition form = generator.generateFormForClassName(Person.class.getName());

        testGeneratedForm(form,
                          Person.class.getName());
    }

    @Test
    public void testGenerateFormForModelWithFilters() {
        FormDefinition form = generator.generateFormForModel(model, getFilters());

        checkFormGeneratedWithFilters(form);
    }

    @Test
    public void testGenerateFormForClassWithFilters() {
        FormDefinition form = generator.generateFormForClass(Person.class, getFilters());

        checkFormGeneratedWithFilters(form);
    }

    @Test
    public void testGenerateFormForClassNameWithFilters() {
        FormDefinition form = generator.generateFormForClassName(Person.class.getName(), getFilters());

        checkFormGeneratedWithFilters(form);
    }

    protected FormElementFilter[] getFilters() {
        FormElementFilter nameFilter = new FormElementFilter("name", o -> false);
        FormElementFilter lastNameFilter = new FormElementFilter("lastName", o -> false);

        return new FormElementFilter[]{nameFilter, lastNameFilter};
    }

    protected void checkFormGeneratedWithFilters(FormDefinition formDefinition) {
        assertNotNull(formDefinition);

        assertEquals(formDefinition.getFields().size(), formDefinition.getLayoutTemplate().getRows().size());

        assertNull(formDefinition.getFieldByBinding("name"));
        assertNull(formDefinition.getFieldByBinding("lastName"));
    }
}
