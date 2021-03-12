/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.IntegerUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.StringUiColumn;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.TextBoxIntegerSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.TextBoxSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TableSortComparatorTest {

    private BaseGridColumn integerUiColumn = new IntegerUiColumn(Collections.EMPTY_LIST,
                                                                 100,
                                                                 true,
                                                                 true,
                                                                 mock(GuidedDecisionTablePresenter.Access.class),
                                                                 mock(TextBoxIntegerSingletonDOMElementFactory.class));
    private BaseGridColumn stringUiColumn = new StringUiColumn(Collections.EMPTY_LIST,
                                                               100,
                                                               true,
                                                               true,
                                                               mock(GuidedDecisionTablePresenter.Access.class),
                                                               mock(TextBoxSingletonDOMElementFactory.class));

    private TableSortComparator comparator;
    private List<GridRow> rows = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        integerUiColumn.setIndex(1);
        stringUiColumn.setIndex(2);

        comparator = new TableSortComparator();

        final BaseGridRowMock a = new BaseGridRowMock();
        a.getCells().put(0, new BaseGridCell<>(new BaseGridCellValue<>(1))); // Row number
        a.getCells().put(1, new BaseGridCell<>(new BaseGridCellValue<>(3))); // Integer column
        a.getCells().put(2, new BaseGridCell<>(new BaseGridCellValue<>("a"))); // String column
        rows.add(a);
        final BaseGridRowMock b = new BaseGridRowMock();
        b.getCells().put(0, new BaseGridCell<>(new BaseGridCellValue<>(2)));
        b.getCells().put(1, new BaseGridCell<>(new BaseGridCellValue<>(100)));
        b.getCells().put(2, new BaseGridCell<>(new BaseGridCellValue<>("c")));
        rows.add(b);
        final BaseGridRowMock c = new BaseGridRowMock();
        c.getCells().put(0, new BaseGridCell<>(new BaseGridCellValue<>(3)));
        c.getCells().put(1, new BaseGridCell<>(new BaseGridCellValue<>(1)));
        c.getCells().put(2, new BaseGridCell<>(new BaseGridCellValue<>("b")));
        rows.add(c);
        final BaseGridRowMock d = new BaseGridRowMock();
        d.getCells().put(0, new BaseGridCell<>(new BaseGridCellValue<>(4)));
        d.getCells().put(1, new BaseGridCell<>(new BaseGridCellValue<>(100)));
        d.getCells().put(2, new BaseGridCell<>(new BaseGridCellValue<>("x")));
        rows.add(d);
    }

    @Test(expected = ModelSynchronizer.VetoException.class)
    public void testInvalidColumn() throws ModelSynchronizer.VetoException {

        final BaseGridRowMock badRow = new BaseGridRowMock();
        badRow.getCells().put(0, new BaseGridCell<>(new BaseGridCellValue<>(4)));
        badRow.getCells().put(1, new BaseGridCell<>(new BaseGridCellValue<>(new NotComparable())));
        badRow.getCells().put(2, new BaseGridCell<>(new BaseGridCellValue<>("x")));
        rows.add(badRow);

        comparator.sort(rows,
                        integerUiColumn);
    }

    @Test
    public void testSortIntegerColumn() throws ModelSynchronizer.VetoException {

        final List<Integer> sort = comparator.sort(rows,
                                                   integerUiColumn);

        assertEquals(rows.size(),
                     sort.size());
        assertEquals(Integer.valueOf(2), sort.get(0));
        assertEquals(Integer.valueOf(0), sort.get(1));
        assertEquals(Integer.valueOf(1), sort.get(2));
        assertEquals(Integer.valueOf(3), sort.get(3));

        assertEquals(1, rows.get(0).getCells().get(1).getValue().getValue());
        assertEquals(3, rows.get(1).getCells().get(1).getValue().getValue());
        assertEquals(100, rows.get(2).getCells().get(1).getValue().getValue());
        assertEquals(100, rows.get(3).getCells().get(1).getValue().getValue());
    }

    @Test
    public void testSortStringColumn() throws ModelSynchronizer.VetoException {

        final List<Integer> sort = comparator.sort(rows,
                                                   stringUiColumn);

        assertEquals(rows.size(),
                     sort.size());
        assertEquals(Integer.valueOf(0), sort.get(0));
        assertEquals(Integer.valueOf(2), sort.get(1));
        assertEquals(Integer.valueOf(1), sort.get(2));
        assertEquals(Integer.valueOf(3), sort.get(3));

        assertEquals("a", rows.get(0).getCells().get(2).getValue().getValue());
        assertEquals("b", rows.get(1).getCells().get(2).getValue().getValue());
        assertEquals("c", rows.get(2).getCells().get(2).getValue().getValue());
        assertEquals("x", rows.get(3).getCells().get(2).getValue().getValue());
    }

    @Test
    public void testSecondSortReversesOrder() throws ModelSynchronizer.VetoException {

        comparator.sort(rows,
                        stringUiColumn);

        final List<Integer> sort = comparator.sort(rows,
                                                   stringUiColumn);

        assertEquals(Integer.valueOf(3), sort.get(0));
        assertEquals(Integer.valueOf(1), sort.get(1));
        assertEquals(Integer.valueOf(2), sort.get(2));
        assertEquals(Integer.valueOf(0), sort.get(3));

        assertEquals("x", rows.get(0).getCells().get(2).getValue().getValue());
        assertEquals("c", rows.get(1).getCells().get(2).getValue().getValue());
        assertEquals("b", rows.get(2).getCells().get(2).getValue().getValue());
        assertEquals("a", rows.get(3).getCells().get(2).getValue().getValue());
    }

    @Test
    public void testThirdSort() throws ModelSynchronizer.VetoException {

        comparator.sort(rows,
                        integerUiColumn);

        comparator.sort(rows,
                        integerUiColumn);

        final List<Integer> sort = comparator.sort(rows,
                                                   integerUiColumn);

        assertEquals(Integer.valueOf(2), sort.get(0));
        assertEquals(Integer.valueOf(0), sort.get(1));
        assertEquals(Integer.valueOf(3), sort.get(2));
        assertEquals(Integer.valueOf(1), sort.get(3));

        assertEquals(1, rows.get(0).getCells().get(1).getValue().getValue());
        assertEquals(3, rows.get(1).getCells().get(1).getValue().getValue());
        assertEquals(100, rows.get(2).getCells().get(1).getValue().getValue());
        assertEquals(100, rows.get(3).getCells().get(1).getValue().getValue());
    }

    @Test
    public void testSecondSortOnADifferentColumnGivesLowestToHighestOrder() throws ModelSynchronizer.VetoException {

        comparator.sort(rows,
                        integerUiColumn);
        final List<Integer> sort = comparator.sort(rows,
                                                   stringUiColumn);

        assertEquals(rows.size(),
                     sort.size());
        assertEquals(Integer.valueOf(0), sort.get(0));
        assertEquals(Integer.valueOf(2), sort.get(1));
        assertEquals(Integer.valueOf(1), sort.get(2));
        assertEquals(Integer.valueOf(3), sort.get(3));

        assertEquals("a", rows.get(0).getCells().get(2).getValue().getValue());
        assertEquals("b", rows.get(1).getCells().get(2).getValue().getValue());
        assertEquals("c", rows.get(2).getCells().get(2).getValue().getValue());
        assertEquals("x", rows.get(3).getCells().get(2).getValue().getValue());
    }

    class BaseGridRowMock extends BaseGridRow {

        @Override
        public Map<Integer, GridCell<?>> getCells() {
            return cells;
        }
    }

    class NotComparable {

    }
}