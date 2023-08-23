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

package org.kie.workbench.common.dmn.client.editors.included;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsPageStateProvider;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class IncludedModelsPageStateTest {

    @Mock
    private IncludedModelsPageStateProvider pageProvider;

    private IncludedModelsPageState state;

    @Before
    public void setup() {
        state = new IncludedModelsPageState();
    }

    @Test
    public void testGetCurrentDiagramNamespaceWhenPageProviderIsPresent() {

        final String expectedNamespace = "://namespace";
        when(pageProvider.getCurrentDiagramNamespace()).thenReturn(expectedNamespace);

        state.init(pageProvider);

        final String actualNamespace = state.getCurrentDiagramNamespace();

        assertEquals(expectedNamespace, actualNamespace);
    }

    @Test
    public void testGetCurrentDiagramNamespaceWhenPageProviderIsNotPresent() {

        final String expectedNamespace = "";

        state.init(null);

        final String actualNamespace = state.getCurrentDiagramNamespace();

        assertEquals(expectedNamespace, actualNamespace);
    }

    @Test
    public void testGenerateIncludedModelsWhenPageProviderIsNotPresent() {

        state.init(null);

        final List<BaseIncludedModelActiveRecord> actualIncludedModels = state.generateIncludedModels();
        final List<BaseIncludedModelActiveRecord> expectedIncludedModels = emptyList();

        assertEquals(expectedIncludedModels, actualIncludedModels);
    }

    @Test
    public void testGenerateIncludedModelsWhenPageProviderIsPresent() {

        final List<BaseIncludedModelActiveRecord> expectedIncludedModels = asList(mock(BaseIncludedModelActiveRecord.class), mock(BaseIncludedModelActiveRecord.class));

        when(pageProvider.generateIncludedModels()).thenReturn(expectedIncludedModels);
        state.init(pageProvider);

        final List<BaseIncludedModelActiveRecord> actualIncludedModels = state.generateIncludedModels();

        assertEquals(actualIncludedModels, expectedIncludedModels);
    }
}
