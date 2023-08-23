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
package org.kie.workbench.common.dmn.client.widgets.grid.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

import static com.ibm.icu.impl.Assert.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils.EXPRESSION_TEXT_PADDING;
import static org.kie.workbench.common.dmn.client.widgets.grid.model.BaseHasDynamicHeightCell.DEFAULT_HEIGHT;

public abstract class BaseHasDynamicHeightCellTest<CELL extends BaseGridCell & HasDynamicHeight> {

    public static final double LINE_HEIGHT = 16.0;

    protected CELL cell;

    protected abstract CELL makeCell();

    protected abstract CELL makeCell(final double lineHeight);

    @Before
    public void setup() {
        this.cell = makeCell();
    }

    @Test
    public void testNullValue() {
        setValue(null);

        assertThat(cell.getHeight()).isEqualTo(DEFAULT_HEIGHT);
    }

    @Test
    public void testEmptyValue() {
        setValue(new BaseGridCellValue<>(""));

        assertThat(cell.getHeight()).isEqualTo(DEFAULT_HEIGHT);
    }

    @Test
    public void testSingleLineValue() {
        setValue(new BaseGridCellValue<>("cheese"));

        assertThat(cell.getHeight()).isEqualTo(LINE_HEIGHT + EXPRESSION_TEXT_PADDING * 3);
    }

    @Test
    public void testMultipleLineValue() {
        //Lines [1,2,3,4]
        setValue(new BaseGridCellValue<>("1\n2\n3\n4"));

        assertThat(cell.getHeight()).isEqualTo(4 * LINE_HEIGHT + (RendererUtils.EXPRESSION_TEXT_PADDING * 3));
    }

    @Test
    public void testMulitpleLineValueWithEmptyLines() {
        //Lines [1,2,<empty>,<empty>,3,4]
        setValue(new BaseGridCellValue<>("1\n2\n\n\n3\n4"));

        assertThat(cell.getHeight()).isEqualTo(6 * LINE_HEIGHT + (RendererUtils.EXPRESSION_TEXT_PADDING * 3));
    }

    @Test
    public void testMulitpleLineValueWithDifferentEndOfLines() {
        //Lines [1,2, ,3,4 ,<empty>]
        setValue(new BaseGridCellValue<>("1\n2\r\n \n3\n4 \r\n"));

        assertThat(cell.getHeight()).isEqualTo(6 * LINE_HEIGHT + (RendererUtils.EXPRESSION_TEXT_PADDING * 3));
    }

    @Test
    public void testMultipleLineValueWithEndingWithEmptyLine() {
        //Lines [1,2,3,<empty>]
        setValue(new BaseGridCellValue<>("1\n2\r\n3\n"));

        assertThat(cell.getHeight()).isEqualTo(4 * LINE_HEIGHT + (RendererUtils.EXPRESSION_TEXT_PADDING * 3));
    }

    @Test
    public void testEquals() {
        final CELL sameCell = makeCell();

        assertThat(cell).isEqualTo(sameCell);
        assertThat(cell.hashCode()).isEqualTo(sameCell.hashCode());
    }

    @Test
    public void testEqualsIdentity() {
        assertThat(cell).isEqualTo(cell);
        assertThat(cell.hashCode()).isEqualTo(cell.hashCode());
    }

    @Test
    public void testEqualsDifferentHeight() {
        final CELL differentCell = makeCell();
        final GridCellValue<String> differentValue = new BaseGridCellValue<>("Hello\nWorld!");
        setValue(differentCell, differentValue);

        assertThat(cell).isNotEqualTo(differentCell);
        assertThat(cell.hashCode()).isNotEqualTo(differentCell.hashCode());
    }

    @Test
    public void testEqualsDifferentLineHeight() {
        final CELL differentCell = makeCell(LINE_HEIGHT + 1);

        assertThat(cell).isNotEqualTo(differentCell);
        assertThat(cell.hashCode()).isNotEqualTo(differentCell.hashCode());
    }

    protected void setValue(final GridCellValue value) {
        setValue(cell, value);
    }

    @SuppressWarnings("unchecked")
    //It's not nice invoking the _setValue_ through reflection however I really don't want it public.
    protected void setValue(final CELL cell,
                            final GridCellValue value) {
        try {
            final Method m = BaseGridCell.class.getDeclaredMethod("setValue", GridCellValue.class);
            m.setAccessible(true);
            m.invoke(cell, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            fail(e);
        }
    }
}
