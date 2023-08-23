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

package org.kie.workbench.common.dmn.client.editors.included.commands;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.DMNIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardsGridComponent;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.ImportRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class RemoveDMNIncludedModelCommandTest {

    @Mock
    private DMNCardsGridComponent grid;

    @Mock
    private DMNIncludedModelActiveRecord includedModel;

    @Mock
    private DMNIncludeModelsClient client;

    @Mock
    private Event<RefreshDecisionComponents> refreshDecisionComponentsEvent;

    @Mock
    private ImportRecordEngine recordEngine;

    @Mock
    private Event<RefreshDataTypesListEvent> refreshDataTypesListEvent;

    private RemoveDMNIncludedModelCommand command;

    @Before
    public void setup() {
        command = spy(new RemoveDMNIncludedModelCommand(grid,
                                                        includedModel,
                                                        client,
                                                        refreshDecisionComponentsEvent,
                                                        recordEngine,
                                                        refreshDataTypesListEvent));
    }

    @Test
    public void testSaveDeletedIncludedModelData() {

        final Integer drgElementsCount = 11;
        final Integer dataTypesCount = 22;
        when(includedModel.getDrgElementsCount()).thenReturn(drgElementsCount);
        when(includedModel.getDataTypesCount()).thenReturn(dataTypesCount);
        doNothing().when(command).superSaveDeletedIncludedModelData();

        command.saveDeletedIncludedModelData();

        assertEquals(drgElementsCount, command.getDrgElementsCount());
        assertEquals(dataTypesCount, command.getDataTypesCount());
        verify(command).superSaveDeletedIncludedModelData();
    }

    @Test
    public void testRestoreDeletedModel() {

        final Integer drgElementsCount = 11;
        final Integer dataTypesCount = 22;

        command.setDrgElementsCount(drgElementsCount);
        command.setDataTypesCount(dataTypesCount);

        final DMNIncludedModelActiveRecord restored = command.restoreDeletedModel();

        assertEquals(drgElementsCount, restored.getDrgElementsCount());
        assertEquals(dataTypesCount, restored.getDataTypesCount());
        assertEquals(recordEngine, restored.getRecordEngine());
    }
}
