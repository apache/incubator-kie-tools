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

package org.kie.workbench.common.forms.jbpm.client;

import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.model.FieldType;

public class JBPMEditorFieldTypeProviderImplTest {

    private static final int PALETTE_FIELD_TYPES_SIZE = 2;
    private static final int EDITOR_FIELD_TYPES_SIZE = 2;

    private JBPMEditorFieldTypeProviderImpl provider;

    @Before
    public void init() {
        provider = new JBPMEditorFieldTypeProviderImpl();
        provider.init();
    }

    @Test
    public void testFunctionallity() {
        Collection<FieldType> paletteComponents = provider.getPaletteFieldTypes();

        Collection<FieldType> propertiesFieldType = provider.getFieldPropertiesFieldTypes();

        Assertions.assertThat(paletteComponents)
                .isNotNull()
                .isNotEmpty()
                .hasSize(PALETTE_FIELD_TYPES_SIZE);

        Assertions.assertThat(propertiesFieldType)
                .isNotNull()
                .isNotEmpty()
                .containsAll(paletteComponents)
                .hasSize(EDITOR_FIELD_TYPES_SIZE);
    }
}
