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

package org.kie.workbench.common.dmn.client.widgets.grid;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableRowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(LienzoMockitoTestRunner.class)
public class BaseExpressionGridThemeTest {

    private BaseExpressionGridTheme theme;

    @Before
    public void setUp() throws Exception {
        theme = new BaseExpressionGridTheme();
    }

    @Test
    public void testGetBodyBackgroundRowNumberColumn() throws Exception {
        final RowNumberColumn column = mock(RowNumberColumn.class);
        final Rectangle rectangle = theme.getBodyBackground(column);
        assertEquals(BaseExpressionGridTheme.ROW_NUMBER_BACKGROUND_FILL_COLOUR, rectangle.getFillColor());
    }

    @Test
    public void testGetBodyBackgroundDecisionTableRowNumberColumn() throws Exception {
        final RowNumberColumn column = mock(DecisionTableRowNumberColumn.class);
        final Rectangle rectangle = theme.getBodyBackground(column);
        assertEquals(BaseExpressionGridTheme.ROW_NUMBER_BACKGROUND_FILL_COLOUR, rectangle.getFillColor());
    }
}
