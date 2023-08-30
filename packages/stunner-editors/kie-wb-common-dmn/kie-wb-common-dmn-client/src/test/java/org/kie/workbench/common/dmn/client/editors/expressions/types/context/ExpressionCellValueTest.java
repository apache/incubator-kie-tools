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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ExpressionCellValueTest {

    @Mock
    private BaseExpressionGrid editor;

    private ExpressionCellValue ecv;

    private void setup(final Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> editor) {
        this.ecv = new ExpressionCellValue(editor);
    }

    @Test
    public void testMinimumWidthWhenNoEditorSet() {
        setup(Optional.empty());

        assertFalse(ecv.getMinimumWidth().isPresent());
    }

    @Test
    public void testMinimumWidthWhenEditorSet() {
        doReturn(100.0).when(editor).getMinimumWidth();

        setup(Optional.of(editor));

        final Optional<Double> oMinimumWidth = ecv.getMinimumWidth();

        assertTrue(oMinimumWidth.isPresent());
        assertEquals(100.0,
                     oMinimumWidth.get(),
                     0.0);
    }
}
