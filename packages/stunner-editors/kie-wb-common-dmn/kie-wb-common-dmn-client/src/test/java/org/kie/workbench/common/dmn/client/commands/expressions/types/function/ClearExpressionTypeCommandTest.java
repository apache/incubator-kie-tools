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

package org.kie.workbench.common.dmn.client.commands.expressions.types.function;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.client.commands.general.BaseClearExpressionCommandTest;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.FunctionUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.KindUtilities;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class ClearExpressionTypeCommandTest extends BaseClearExpressionCommandTest<ClearExpressionTypeCommand, FunctionDefinition, FunctionUIModelMapper> {

    @Mock
    private FunctionUIModelMapper uiModelMapper;

    @Override
    protected FunctionDefinition makeTestExpression() {
        return new FunctionDefinition();
    }

    @Override
    protected ClearExpressionTypeCommand makeTestCommand() {
        return new ClearExpressionTypeCommand(new GridCellTuple(ROW_INDEX,
                                                                COLUMN_INDEX,
                                                                gridWidget),
                                              expression,
                                              uiModelMapper,
                                              executeCanvasOperation,
                                              undoCanvasOperation);
    }

    @Override
    protected FunctionUIModelMapper makeTestUiModelMapper() {
        return uiModelMapper;
    }

    @Test
    public void executeGraphCommand() {
        makeCommand();

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).execute(graphCommandExecutionContext));

        assertNull(expression.getExpression());
        assertEquals(FunctionDefinition.Kind.FEEL,
                     KindUtilities.getKind(expression));

        verifyZeroInteractions(uiModelMapper);
    }

    @Test
    public void undoGraphCommand() {
        expression.setExpression(expression);
        KindUtilities.setKind(expression,
                              FunctionDefinition.Kind.JAVA);

        makeCommand();

        //Execute then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).execute(graphCommandExecutionContext));
        assertNull(expression.getExpression());
        assertEquals(FunctionDefinition.Kind.FEEL,
                     KindUtilities.getKind(expression));

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).undo(graphCommandExecutionContext));
        assertEquals(expression,
                     expression.getExpression());
        assertEquals(FunctionDefinition.Kind.JAVA,
                     KindUtilities.getKind(expression));

        verifyZeroInteractions(uiModelMapper);
    }

    @Test
    public void allowCanvasCommand() {
        makeCommand();

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).allow(canvasHandler));

        verifyZeroInteractions(uiModelMapper);
    }
}
