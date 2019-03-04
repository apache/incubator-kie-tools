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

package org.kie.workbench.common.dmn.client.editors.included.imports;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Import;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModel;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class IncludedModelsIndexTest {

    @Mock
    private IncludedModel includedModel;

    @Mock
    private Import anImport;

    private String uuid = "123";

    private IncludedModelsIndex modelsIndex;

    @Before
    public void setup() {

        when(includedModel.getUUID()).thenReturn(uuid);

        modelsIndex = new IncludedModelsIndex();
        modelsIndex.index(includedModel, anImport);
    }

    @Test
    public void testIndex() {

        final IncludedModel includedModel = mock(IncludedModel.class);
        final Import expectedImport = mock(Import.class);
        final String uuid = "456";

        when(includedModel.getUUID()).thenReturn(uuid);

        modelsIndex.index(includedModel, expectedImport);

        final Import actualImport = modelsIndex.getImport(includedModel);

        assertEquals(expectedImport, actualImport);
    }

    @Test
    public void testGetImport() {
        final Import anImport = modelsIndex.getImport(includedModel);
        assertEquals(this.anImport, anImport);
    }

    @Test
    public void testClear() {
        assertEquals(1, modelsIndex.size());
        modelsIndex.clear();
        assertEquals(0, modelsIndex.size());
    }
}
