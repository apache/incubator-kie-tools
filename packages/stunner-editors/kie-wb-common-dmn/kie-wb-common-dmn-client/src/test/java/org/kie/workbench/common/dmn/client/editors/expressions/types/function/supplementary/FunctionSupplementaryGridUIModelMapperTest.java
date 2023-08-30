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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.BaseContextUIModelMapperTest;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RowSelectionStrategy;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class FunctionSupplementaryGridUIModelMapperTest extends BaseContextUIModelMapperTest<FunctionSupplementaryGridUIModelMapper> {

    @Override
    protected FunctionSupplementaryGridUIModelMapper getMapper(final boolean isOnlyVisualChangeAllowedSupplier) {
        return new FunctionSupplementaryGridUIModelMapper(gridWidget,
                                                          () -> uiModel,
                                                          () -> Optional.of(context),
                                                          () -> isOnlyVisualChangeAllowedSupplier,
                                                          expressionEditorDefinitionsSupplier,
                                                          listSelector,
                                                          0);
    }

    @Test
    public void testFromDMNModelRowNumber() {
        setup(false);

        mapper.fromDMNModel(0, 0);
        mapper.fromDMNModel(1, 0);

        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals(RowSelectionStrategy.INSTANCE,
                     uiModel.getCell(0, 0).getSelectionStrategy());

        assertEquals(2,
                     uiModel.getCell(1, 0).getValue().getValue());
        assertEquals(RowSelectionStrategy.INSTANCE,
                     uiModel.getCell(1, 0).getSelectionStrategy());
    }

    @Test
    public void testFromDMNModelName() {
        setup(false);

        mapper.fromDMNModel(0, 1);

        assertEquals("ii1",
                     ((InformationItemCell.HasNameAndDataTypeCell) uiModel.getCell(0, 1).getValue().getValue()).getName().getValue());
    }
}
