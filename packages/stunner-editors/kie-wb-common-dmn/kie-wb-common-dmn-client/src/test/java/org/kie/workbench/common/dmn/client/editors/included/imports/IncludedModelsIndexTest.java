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

package org.kie.workbench.common.dmn.client.editors.included.imports;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class IncludedModelsIndexTest {

    @Mock
    private BaseIncludedModelActiveRecord includedModel;

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

        final BaseIncludedModelActiveRecord includedModel = mock(BaseIncludedModelActiveRecord.class);
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
    public void testGetIndexedImports() {

        final List<Import> imports = new ArrayList<>(modelsIndex.getIndexedImports());

        assertEquals(1, imports.size());
        assertEquals(anImport, imports.get(0));
    }

    @Test
    public void testClear() {
        assertEquals(1, modelsIndex.size());
        modelsIndex.clear();
        assertEquals(0, modelsIndex.size());
    }
}
