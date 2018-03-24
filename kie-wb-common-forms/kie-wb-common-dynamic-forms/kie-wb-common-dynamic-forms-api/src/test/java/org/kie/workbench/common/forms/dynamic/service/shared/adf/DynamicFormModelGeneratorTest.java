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

package org.kie.workbench.common.forms.dynamic.service.shared.adf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.engine.shared.FormBuildingService;
import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.model.Address;
import org.kie.workbench.common.forms.adf.engine.shared.impl.FormBuildingServiceImpl;
import org.kie.workbench.common.forms.adf.engine.shared.test.AbstractFormGenerationTest;
import org.kie.workbench.common.forms.adf.engine.shared.test.TestPropertyValueExtractor;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.StaticModelFormRenderingContext;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class DynamicFormModelGeneratorTest extends AbstractFormGenerationTest {

    private DynamicFormModelGenerator dynamicFormModelGenerator;

    private FormBuildingService formBuildingService;

    @Before
    @Override
    public void init() {
        super.init();

        formBuildingService = new FormBuildingServiceImpl(generator);

        dynamicFormModelGenerator = new DynamicFormModelGenerator(formBuildingService,
                                                                  new TestPropertyValueExtractor());
    }

    @Test
    public void testGenerateContextForModel() {
        StaticModelFormRenderingContext context = dynamicFormModelGenerator.getContextForModel(model);

        assertNotNull(context);

        assertNotNull(context.getRootForm());

        assertEquals(3,
                     context.getAvailableForms().size());

        context.getAvailableForms().forEach((id, form) -> {
            testGeneratedForm(form,
                              id);
        });
    }

    @Test
    public void testGenerateContextForModelWithFilters() {
        FormElementFilter nameFilter = new FormElementFilter("name", o -> true);
        FormElementFilter lastNameFilter = new FormElementFilter("lastName", o -> false);
        FormElementFilter addressStreetFilter = new FormElementFilter("address.street", o -> true);
        FormElementFilter addressNumFilter = new FormElementFilter("address.number", o -> false);

        StaticModelFormRenderingContext context = dynamicFormModelGenerator.getContextForModel(model, nameFilter, lastNameFilter, addressStreetFilter, addressNumFilter);

        assertEquals(3, context.getAvailableForms().size());

        FormDefinition rootForm = context.getRootForm();

        assertNotNull(rootForm);
        assertEquals(rootForm.getFields().size(), rootForm.getLayoutTemplate().getRows().size());

        assertNotNull(rootForm.getFieldByBinding("name"));
        assertNull(rootForm.getFieldByBinding("lastName"));

        FormDefinition addressForm = context.getAvailableForms().get(Address.class.getName());

        assertNotNull(addressForm);
        assertEquals(addressForm.getFields().size(), addressForm.getLayoutTemplate().getRows().size());

        assertNotNull(addressForm.getFieldByBinding("street"));
        assertNull(addressForm.getFieldByBinding("number"));
    }
}
