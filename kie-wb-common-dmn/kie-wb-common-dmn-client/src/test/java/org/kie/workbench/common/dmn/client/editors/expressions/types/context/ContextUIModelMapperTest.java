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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.impl.RowSelectionStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class ContextUIModelMapperTest extends BaseContextUIModelMapperTest<ContextUIModelMapper> {

    @Override
    protected ContextUIModelMapper getMapper() {
        return new ContextUIModelMapper(() -> uiModel,
                                        () -> Optional.of(context),
                                        expressionEditorDefinitionsSupplier);
    }

    @Test
    public void testFromDMNModelRowNumber() {
        mapper.fromDMNModel(0, 0);
        mapper.fromDMNModel(1, 0);

        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals(RowSelectionStrategy.INSTANCE,
                     uiModel.getCell(0, 0).getSelectionStrategy());

        assertNull(uiModel.getCell(1, 0).getValue().getValue());
        assertEquals(RowSelectionStrategy.INSTANCE,
                     uiModel.getCell(1, 0).getSelectionStrategy());
    }
}
