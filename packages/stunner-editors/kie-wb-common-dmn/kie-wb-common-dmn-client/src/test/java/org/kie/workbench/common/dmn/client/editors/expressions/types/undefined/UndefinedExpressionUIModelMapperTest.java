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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined;

import java.util.Optional;
import java.util.function.Supplier;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UndefinedExpressionUIModelMapperTest {

    @Mock
    private LiteralExpression expression;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private DMNModelInstrumentedBase hasExpressionDMNModelInstrumentedBase;

    @Mock
    private LiteralExpressionGrid editor;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private TranslationService translationService;

    @Mock
    private UndefinedExpressionColumn uiColumn;

    private GridData uiModel;

    private Supplier<Optional<GridCellValue<?>>> cellValueSupplier;

    private UndefinedExpressionUIModelMapper mapper;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.uiModel = new BaseGridData();
        this.uiModel.appendColumn(uiColumn);
        this.uiModel.appendRow(new BaseGridRow());
        this.mapper = new UndefinedExpressionUIModelMapper(() -> uiModel,
                                                           () -> Optional.ofNullable(expression),
                                                           listSelector,
                                                           translationService,
                                                           hasExpression);
        this.cellValueSupplier = () -> Optional.of(new ExpressionCellValue(Optional.of(editor)));

        when(hasExpression.asDMNModelInstrumentedBase()).thenReturn(hasExpressionDMNModelInstrumentedBase);
    }

    @Test
    public void testFromDMNModel() {
        mapper.fromDMNModel(0, 0);

        assertThat(mapper.getUiModel().get().getCell(0, 0)).isInstanceOf(UndefinedExpressionCell.class);
    }

    @Test
    public void testToDMNModelNoEditor() {
        when(editor.getExpression()).thenReturn(Optional::empty);

        mapper.toDMNModel(0, 0, cellValueSupplier);

        verify(hasExpression).setExpression(eq(null));
    }

    @Test
    public void testToDMNModelWithEditor() {
        when(editor.getExpression()).thenReturn(() -> Optional.of(expression));

        mapper.toDMNModel(0, 0, cellValueSupplier);

        verify(hasExpression).setExpression(eq(expression));
        verify(expression).setParent(eq(hasExpressionDMNModelInstrumentedBase));
    }
}
