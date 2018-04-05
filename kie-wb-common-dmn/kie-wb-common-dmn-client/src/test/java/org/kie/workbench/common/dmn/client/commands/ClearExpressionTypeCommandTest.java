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

package org.kie.workbench.common.dmn.client.commands;

import java.util.Optional;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.client.commands.general.BaseClearExpressionCommandTest;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionContainerUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClearExpressionTypeCommandTest extends BaseClearExpressionCommandTest<ClearExpressionTypeCommand, Expression, ExpressionContainerUIModelMapper> {

    private static final String UUID = "uuid";

    @Mock
    private ExpressionGridCache expressionGridCache;

    @Mock
    private ExpressionContainerUIModelMapper uiModelMapper;

    @Mock
    private BaseExpressionGrid expressionGrid;

    @Before
    @Override
    public void setup() {
        super.setup();

        when(expressionGridCache.getExpressionGrid(eq(UUID))).thenReturn(Optional.of(expressionGrid));
    }

    @Override
    protected Expression makeTestExpression() {
        return new LiteralExpression();
    }

    @Override
    protected ClearExpressionTypeCommand makeTestCommand() {
        return new ClearExpressionTypeCommand(new GridCellTuple(ROW_INDEX,
                                                                COLUMN_INDEX,
                                                                gridWidget),
                                              UUID,
                                              hasExpression,
                                              uiModelMapper,
                                              expressionGridCache,
                                              gridLayer::batch);
    }

    @Override
    protected ExpressionContainerUIModelMapper makeTestUiModelMapper() {
        return uiModelMapper;
    }

    @Override
    public void executeCanvasCommand() {
        super.executeCanvasCommand();

        verify(expressionGridCache).removeExpressionGrid(eq(UUID));
    }

    @Override
    public void undoCanvasCommand() {
        super.undoCanvasCommand();

        verify(expressionGridCache).putExpressionGrid(eq(UUID), eq(Optional.of(expressionGrid)));
    }
}
