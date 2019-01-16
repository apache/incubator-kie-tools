/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.client.widgets.grid.model;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorGridRow.DEFAULT_HEIGHT;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class LiteralExpressionGridRowTest {

    private static final double TEXT_LINE_HEIGHT = 10.0;

    @Test
    public void testEmptyRow() throws Exception {
        final GridRow row = new LiteralExpressionGridRow(TEXT_LINE_HEIGHT);
        assertThat(row.getHeight()).isEqualTo(DEFAULT_HEIGHT);
    }

    @Test
    public void testGetHeightWithEmptyExpressionText() {
        assertRowHeight("",
                        LiteralExpressionGridRow.DEFAULT_HEIGHT);
    }

    private void assertRowHeight(final String value,
                                 final double expectedHeight) {
        final GridRow row = spy(new LiteralExpressionGridRow(TEXT_LINE_HEIGHT));
        final Map<Integer, GridCell> cells = new HashMap<Integer, GridCell>() {{
            put(0, new BaseGridCell<>(new BaseGridCellValue<>(value)));
        }};

        doReturn(cells).when(row).getCells();
        assertThat(row.getHeight()).isEqualTo(expectedHeight);
    }

    @Test
    public void testGetHeightWithMultiLineExpressionText() {
        //Lines [1,2,3,4]
        assertRowHeight("1\n2\n3\n4",
                        4 * TEXT_LINE_HEIGHT
                                + (RendererUtils.EXPRESSION_TEXT_PADDING * 3));
    }

    @Test
    public void testGetHeightWithEmptyLinesExpressionText() {
        //Lines [1,2,<empty>,<empty>,3,4]
        assertRowHeight("1\n2\n\n\n3\n4",
                        6 * TEXT_LINE_HEIGHT
                                + (RendererUtils.EXPRESSION_TEXT_PADDING * 3));
    }

    @Test
    public void testGetHeightWithDifferentEndOfLinesExpressionText() {
        //Lines [1,2, ,3,4 ,<empty>]
        assertRowHeight("1\n2\r\n \n3\n4 \r\n",
                        6 * TEXT_LINE_HEIGHT
                                + (RendererUtils.EXPRESSION_TEXT_PADDING * 3));
    }

    @Test
    public void testGetHeightEndingWithEmptyLineExpressionText() {
        //Lines [1,2,3,<empty>]
        assertRowHeight("1\n2\r\n3\n",
                        4 * TEXT_LINE_HEIGHT
                                + (RendererUtils.EXPRESSION_TEXT_PADDING * 3));
    }
}
