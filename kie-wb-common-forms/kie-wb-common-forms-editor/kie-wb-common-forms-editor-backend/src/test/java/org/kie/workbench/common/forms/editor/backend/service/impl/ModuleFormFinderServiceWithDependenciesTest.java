/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.backend.service.impl;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.forms.model.FormDefinition;

public class ModuleFormFinderServiceWithDependenciesTest extends AbstractModuleFormFinderServiceImplTest{

    @BeforeClass
    public static void init() throws Exception {
        initialize();

        buildModules("module1", "module2");
    }

    @Test
    public void testFindAllForms() {
        List<FormDefinition> result = formFinderService.findAllForms(currentModulePath);

        Assertions.assertThat(result)
                .isNotNull()
                .hasSize(5);
    }

    @Test
    public void testFindModuleFormsById() {
        testFindFormById(FORM_MODULE_2_1);
        testFindFormById(FORM_MODULE_2_2);

        testFindFormById(FORM_MODULE_1_1);
        testFindFormById(FORM_MODULE_1_2);
        testFindFormById(FORM_MODULE_1_3);
    }

    @Test
    public void testFindModuleFormsByType() {
        testFindFormByType(PERSON, FORM_MODULE_2_1, FORM_MODULE_1_1);
        testFindFormByType(ADDRESS, FORM_MODULE_2_2, FORM_MODULE_1_2);
        testFindFormByType(OFFICE, FORM_MODULE_1_3);
    }
}
