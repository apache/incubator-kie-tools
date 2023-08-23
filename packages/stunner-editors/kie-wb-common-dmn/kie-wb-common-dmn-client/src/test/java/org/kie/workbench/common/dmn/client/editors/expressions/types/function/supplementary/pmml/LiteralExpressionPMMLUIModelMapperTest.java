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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml;

import java.util.Optional;

import org.junit.Test;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionUIModelMapperTest;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridCell;

import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.widgets.grid.model.BaseHasDynamicHeightCell.DEFAULT_HEIGHT;

public class LiteralExpressionPMMLUIModelMapperTest extends LiteralExpressionUIModelMapperTest {

    @Override
    protected LiteralExpressionUIModelMapper getMapper() {
        return new LiteralExpressionPMMLUIModelMapper(() -> uiModel,
                                                      () -> Optional.of(literalExpression),
                                                      listSelector,
                                                      DEFAULT_HEIGHT,
                                                      "placeholder");
    }

    @Test
    public void testFromDmn_CellType() {
        mapper.fromDMNModel(0, 0);

        assertTrue(uiModel.getCell(0, 0) instanceof DMNGridCell);
    }
}
