/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table.model;

import java.util.List;

import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GuidedDecisionTableUiModelTest {

    @Mock
    private ModelSynchronizer synchronizer;
    private GuidedDecisionTableUiModel guidedDecisionTableUiModel;

    @Before
    public void setUp() throws Exception {
        guidedDecisionTableUiModel = new GuidedDecisionTableUiModel(synchronizer);
    }

    @Test
    public void name() throws ModelSynchronizer.VetoException {
        final List<Integer> sort = guidedDecisionTableUiModel.sort(new BaseGridColumn<String>(mock(GridColumn.HeaderMetaData.class),
                                                                                              mock(GridColumnRenderer.class),
                                                                                              100));
        verify(synchronizer).sort(any());
        verify(synchronizer).updateSystemControlledColumnValues();
        assertTrue(sort.isEmpty());
    }
}