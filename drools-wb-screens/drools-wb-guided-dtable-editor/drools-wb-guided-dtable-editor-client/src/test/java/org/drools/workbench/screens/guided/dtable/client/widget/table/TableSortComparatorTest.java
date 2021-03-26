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

import org.assertj.core.api.Assertions;
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

        Assertions.assertThat(sort).containsExactly(2, 0, 1, 3);
        Assertions.assertThat(rows)
                .extracting(row -> (Integer) row.getCells().get(1).getValue().getValue())
                .containsExactly(1, 3, 100, 100);
    }

    @Test
    public void testSortStringColumn() throws ModelSynchronizer.VetoException {

        final List<Integer> sort = comparator.sort(rows,
                                                   stringUiColumn);

        Assertions.assertThat(sort).containsExactly(0, 2, 1, 3);
        Assertions.assertThat(rows)
                .extracting(row -> (String) row.getCells().get(2).getValue().getValue())
                .containsExactly("a", "b", "c", "x");
    }

    @Test
    public void testSecondSortReversesOrder() throws ModelSynchronizer.VetoException {

        comparator.sort(rows,
                        stringUiColumn);

        final List<Integer> sort = comparator.sort(rows,
                                                   stringUiColumn);

        Assertions.assertThat(sort).containsExactly(3, 1, 2, 0);
        Assertions.assertThat(rows)
                .extracting(row -> (String) row.getCells().get(2).getValue().getValue())
                .containsExactly("x", "c", "b", "a");
    }

    @Test
    public void testThirdSort() throws ModelSynchronizer.VetoException {

        comparator.sort(rows,
                        integerUiColumn);

        comparator.sort(rows,
                        integerUiColumn);

        final List<Integer> sort = comparator.sort(rows,
                                                   integerUiColumn);

        Assertions.assertThat(sort).containsExactly(2, 0, 3, 1);
        Assertions.assertThat(rows)
                .extracting(row -> (Integer) row.getCells().get(1).getValue().getValue())
                .containsExactly(1, 3, 100, 100);
    }

    @Test
    public void testSecondSortOnADifferentColumnGivesLowestToHighestOrder() throws ModelSynchronizer.VetoException {

        comparator.sort(rows,
                        integerUiColumn);
        final List<Integer> sort = comparator.sort(rows,
                                                   stringUiColumn);

        Assertions.assertThat(sort).containsExactly(0, 2, 1, 3);
        Assertions.assertThat(rows)
                .extracting(row -> (String) row.getCells().get(2).getValue().getValue())
                .containsExactly("a", "b", "c", "x");
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