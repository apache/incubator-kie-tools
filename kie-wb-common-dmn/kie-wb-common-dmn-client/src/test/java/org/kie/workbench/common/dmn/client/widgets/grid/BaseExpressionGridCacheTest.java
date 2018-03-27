/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.grid;

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridDataCache;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(LienzoMockitoTestRunner.class)
public class BaseExpressionGridCacheTest extends BaseExpressionGridTest {

    @Mock
    private GridRow uiRow;

    @Mock
    private GridColumn uiColumn;

    private GridDataCache.CacheResult<DMNGridData> cacheResult = new GridDataCache.CacheResult<>(new DMNGridData(), false);

    @Override
    @SuppressWarnings("unchecked")
    public BaseExpressionGrid getGrid() {
        final HasExpression hasExpression = mock(HasExpression.class);
        final Optional<LiteralExpression> expression = Optional.of(mock(LiteralExpression.class));
        final Optional<HasName> hasName = Optional.of(mock(HasName.class));

        return new BaseExpressionGrid(parentCell,
                                      Optional.empty(),
                                      hasExpression,
                                      expression,
                                      hasName,
                                      gridPanel,
                                      gridLayer,
                                      cacheResult,
                                      renderer,
                                      definitionUtils,
                                      sessionManager,
                                      sessionCommandManager,
                                      canvasCommandFactory,
                                      cellEditorControls,
                                      listSelector,
                                      translationService,
                                      0) {
            @Override
            protected BaseUIModelMapper makeUiModelMapper() {
                return mapper;
            }

            @Override
            protected void initialiseUiColumns() {
                model.appendColumn(uiColumn);
            }

            @Override
            protected void initialiseUiModel() {
                model.appendRow(uiRow);
            }

            @Override
            protected boolean isHeaderHidden() {
                return false;
            }
        };
    }

    @Test
    public void testCachedModelReuse() {
        //Initial CacheResult and Grid were created during Unit Test setup
        final BaseExpressionGrid grid1 = grid;
        assertGridModelDefinition(grid1);

        cacheResult = new GridDataCache.CacheResult<>(cacheResult.getGridData(), true);
        final BaseExpressionGrid grid2 = getGrid();
        assertGridModelDefinition(grid2);

        assertThat(grid1.getModel()).isSameAs(grid2.getModel());
        assertThat(grid1).isNotSameAs(grid2);
    }

    private void assertGridModelDefinition(final BaseExpressionGrid grid) {
        assertThat(grid.getModel().getRowCount()).isEqualTo(1);
        assertThat(grid.getModel().getRows().get(0)).isSameAs(uiRow);
        assertThat(grid.getModel().getColumnCount()).isEqualTo(1);
        assertThat(grid.getModel().getColumns().get(0)).isSameAs(uiColumn);
    }
}
