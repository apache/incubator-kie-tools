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

package org.kie.workbench.common.dmn.client.commands.expressions.types.context;

import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.client.commands.general.BaseClearExpressionCommandTest;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClearExpressionTypeCommandTest extends BaseClearExpressionCommandTest<ClearExpressionTypeCommand, Context, ContextUIModelMapper> {

    @Mock
    private ContextUIModelMapper uiModelMapper;

    @Override
    protected Context makeTestExpression() {
        return new Context();
    }

    @Override
    protected ClearExpressionTypeCommand makeTestCommand() {
        return new ClearExpressionTypeCommand(new GridCellTuple(ROW_INDEX,
                                                                COLUMN_INDEX,
                                                                gridWidget),
                                              hasExpression,
                                              uiModelMapper,
                                              executeCanvasOperation,
                                              undoCanvasOperation);
    }

    @Override
    protected ContextUIModelMapper makeTestUiModelMapper() {
        return uiModelMapper;
    }
}
