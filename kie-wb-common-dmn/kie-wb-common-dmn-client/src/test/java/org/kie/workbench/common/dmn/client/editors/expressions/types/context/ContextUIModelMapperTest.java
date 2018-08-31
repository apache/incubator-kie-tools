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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridCell;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RowSelectionStrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ContextUIModelMapperTest extends BaseContextUIModelMapperTest<ContextUIModelMapper> {

    @Override
    protected ContextUIModelMapper getMapper() {
        return new ContextUIModelMapper(gridWidget,
                                        () -> uiModel,
                                        () -> Optional.of(context),
                                        expressionEditorDefinitionsSupplier,
                                        listSelector,
                                        0);
    }

    @Test
    public void testFromDMNModelRowNumber() {
        mapper.fromDMNModel(0, 0);
        mapper.fromDMNModel(1, 0);

        assertThat(uiModel.getCell(0, 0).getValue().getValue()).isEqualTo(1);
        assertThat(uiModel.getCell(0, 0).getSelectionStrategy()).isSameAs(RowSelectionStrategy.INSTANCE);

        assertThat(uiModel.getCell(1, 0).getValue().getValue()).isNull();
        assertThat(uiModel.getCell(1, 0).getSelectionStrategy()).isSameAs(RowSelectionStrategy.INSTANCE);
    }

    @Test
    public void testFromDMNModelName() {
        mapper.fromDMNModel(0, 1);
        mapper.fromDMNModel(1, 1);

        assertEquals("ii1",
                     ((InformationItemCell.HasNameCell) uiModel.getCell(0, 1).getValue().getValue()).getName().getValue());
        assertEquals(ContextUIModelMapper.DEFAULT_ROW_CAPTION,
                     ((InformationItemCell.HasNameCell) uiModel.getCell(1, 1).getValue().getValue()).getName().getValue());
    }

    @Test
    public void testFromDMNModelCellTypes() {
        IntStream.range(0, 2).forEach(rowIndex -> {
            mapper.fromDMNModel(rowIndex, 0);
            mapper.fromDMNModel(rowIndex, 1);
            mapper.fromDMNModel(rowIndex, 2);
        });

        assertThat(uiModel.getCell(0, 0)).isInstanceOf(ContextGridCell.class);
        assertThat(uiModel.getCell(0, 1)).isInstanceOf(ContextGridCell.class);
        assertThat(uiModel.getCell(0, 2)).isInstanceOf(ContextGridCell.class);

        assertThat(uiModel.getCell(1, 0)).isInstanceOf(DMNGridCell.class);
        assertThat(uiModel.getCell(1, 1)).isInstanceOf(DMNGridCell.class);
        assertThat(uiModel.getCell(1, 2)).isInstanceOf(DMNGridCell.class);
    }
}
