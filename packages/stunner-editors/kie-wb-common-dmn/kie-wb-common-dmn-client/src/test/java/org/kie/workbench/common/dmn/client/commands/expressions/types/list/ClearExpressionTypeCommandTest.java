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
package org.kie.workbench.common.dmn.client.commands.expressions.types.list;
/**/

import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.client.commands.general.BaseClearExpressionCommandTest;
import org.kie.workbench.common.dmn.client.editors.expressions.types.list.ListUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClearExpressionTypeCommandTest extends BaseClearExpressionCommandTest<ClearExpressionTypeCommand, List, ListUIModelMapper> {

    @Mock
    private ListUIModelMapper uiModelMapper;

    @Override
    protected List makeTestExpression() {
        return new List();
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
    protected ListUIModelMapper makeTestUiModelMapper() {
        return uiModelMapper;
    }
}
