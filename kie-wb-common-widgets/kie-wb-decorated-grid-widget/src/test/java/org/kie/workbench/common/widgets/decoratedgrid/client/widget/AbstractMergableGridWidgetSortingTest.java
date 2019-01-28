/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.widgets.decoratedgrid.client.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.DynamicData;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.DynamicDataRow;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SortDataEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateModelEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractMergableGridWidgetSortingTest {

    @Mock
    private ResourcesProvider resources;

    @Mock
    private AbstractCellFactory cellFactory;

    @Mock
    private AbstractCellValueFactory cellValueFactory;

    @Mock
    private CellTableDropDownDataValueMapProvider dropDownManager;

    @Mock
    private EventBus eventBus;

    @Mock
    private ImageResource imageResource;

    @Captor
    private ArgumentCaptor<UpdateModelEvent> updateModelEventCaptor;

    private AbstractMergableGridWidget widget;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(resources.collapseCellsIcon()).thenReturn(imageResource);
        when(resources.expandCellsIcon()).thenReturn(imageResource);

        this.widget = new AbstractMergableGridWidget(resources,
                                                     cellFactory,
                                                     cellValueFactory,
                                                     dropDownManager,
                                                     true,
                                                     eventBus) {
            @Override
            void redraw() {
                //NOP for sorting tests
            }

            @Override
            void redrawColumns(final int startRedrawIndex,
                               final int endRedrawIndex) {
                //NOP for sorting tests
            }

            @Override
            void createEmptyRowElement(final int index) {
                //NOP for sorting tests
            }

            @Override
            void createRowElement(final int index,
                                  final DynamicDataRow rowData) {
                //NOP for sorting tests
            }

            @Override
            void deleteRowElement(final int index) {
                //NOP for sorting tests
            }

            @Override
            void redrawRows(final int startRedrawIndex,
                            final int endRedrawIndex) {
                //NOP for sorting tests
            }

            @Override
            void removeRowElement(final int index) {
                //NOP for sorting tests
            }

            @Override
            void deselectCell(final CellValue cell) {
                //NOP for sorting tests
            }

            @Override
            void hideColumn(final int index) {
                //NOP for sorting tests
            }

            @Override
            void resizeColumn(final DynamicColumn col,
                              final int width) {
                //NOP for sorting tests
            }

            @Override
            void selectCell(final CellValue cell) {
                //NOP for sorting tests
            }

            @Override
            void showColumn(final int index) {
                //NOP for sorting tests
            }
        };
    }

    @Test
    public void testOnSortData() {
        final Coordinate origin = new Coordinate(0, 0);
        final DynamicData data = new DynamicData();
        data.addRow();
        data.addRow();
        final List<CellValue<? extends Comparable<?>>> columnData = new ArrayList<>();
        final CellValue<String> cellOne = new CellValue<>("one");
        final CellValue<String> cellTwo = new CellValue<>("two");
        columnData.add(cellOne);
        columnData.add(cellTwo);
        data.addColumn(0, columnData, true);
        widget.setData(data);

        final SortConfiguration sortConfiguration = new SortConfiguration();
        sortConfiguration.setColumnIndex(0);
        sortConfiguration.setSortDirection(SortDirection.DESCENDING);
        final SortDataEvent sortEvent = new SortDataEvent(Collections.singletonList(sortConfiguration));

        widget.onSortData(sortEvent);

        verify(eventBus).fireEvent(updateModelEventCaptor.capture());

        final UpdateModelEvent updateModelEvent = updateModelEventCaptor.getValue();
        final Map<Coordinate, List<List<CellValue<? extends Comparable<?>>>>> updates = updateModelEvent.getUpdates();
        assertNotNull(updates);
        assertEquals(1, updates.size());
        assertTrue(updates.containsKey(origin));

        final List<List<CellValue<? extends Comparable<?>>>> updates00 = updates.get(origin);
        assertEquals(2, updates00.size());
        assertEquals(1, updates00.get(0).size());
        assertEquals(1, updates00.get(1).size());
        assertEquals(cellTwo, updates00.get(0).get(0));
        assertEquals(cellOne, updates00.get(1).get(0));
    }
}
