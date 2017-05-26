/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.table.keyboard;

import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseKeyboardTest {

    @Mock
    protected GridLayer gridLayer;

    @Mock
    protected GuidedDecisionTableView gridWidget;

    @Mock
    protected GuidedDecisionTableView.Presenter dtPresenter;

    @Mock
    private GridColumn.HeaderMetaData headerMetaData;

    @Mock
    private GridColumnRenderer<?> columnRenderer;

    @Mock
    private BaseGridRendererHelper rendererHelper;

    protected GridData uiModel = new BaseGridData();

    @Before
    public void setup() {
        for (int size = 0; size < 3; size++) {
            this.uiModel.appendRow(new BaseGridRow());
            this.uiModel.appendColumn(new BaseGridColumn<>(headerMetaData,
                                                           columnRenderer,
                                                           100.0));
        }

        when(gridWidget.getModel()).thenReturn(uiModel);
        when(gridWidget.getPresenter()).thenReturn(dtPresenter);
        when(gridWidget.getRendererHelper()).thenReturn(rendererHelper);
    }
}


