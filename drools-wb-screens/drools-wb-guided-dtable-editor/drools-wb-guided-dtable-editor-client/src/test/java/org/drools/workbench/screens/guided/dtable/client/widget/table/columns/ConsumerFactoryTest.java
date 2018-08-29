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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.datepicker.DatePickerDOMElement;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.MultiValueDOMElement;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.MultiValueSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.SingleValueDOMElement;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.common.DatePicker;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionStrategy;

import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.DATE_FORMAT;
import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.KIE_TIMEZONE_OFFSET;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ConsumerFactoryTest {

    private static final String KEY = "key";

    private static final String VALUE = "value";

    private static final String TEST_DATE_FORMAT = "MM-dd-yyyy HH:mm:ss Z";

    @Mock
    private MultiValueSingletonDOMElementFactory factory;

    @Mock
    private MultiValueDOMElement multiValueDOMElement;

    @Mock
    private ListBox multiValueWidget;

    @Mock
    private GridCell cell;

    private Map<String, String> enumLookups;

    @BeforeClass
    public static void setupStatic() {
        System.setProperty("user.timezone", "Europe/Vilnius");

        ApplicationPreferences.setUp(new HashMap<String, String>() {{
            put(KIE_TIMEZONE_OFFSET, "10800000");
            put(DATE_FORMAT, TEST_DATE_FORMAT);
        }});
    }

    @Before
    public void setup() {
        enumLookups = new HashMap<>();
        when(multiValueDOMElement.getWidget()).thenReturn(multiValueWidget);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void makeOnCreationCallbackListBoxSingleSelect() {
        setupMultipleSelectTest(ConsumerFactory.MAX_VISIBLE_ROWS,
                                false);

        final Consumer callback = ConsumerFactory.makeOnCreationCallback(factory,
                                                                         cell,
                                                                         enumLookups);
        callback.accept(multiValueDOMElement);

        verifyMultipleSelectTest(ConsumerFactory.MAX_VISIBLE_ROWS);

        verify(multiValueWidget,
               never()).setVisibleItemCount(anyInt());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void makeOnCreationCallbackListBoxMultipleSelect() {
        setupMultipleSelectTest(ConsumerFactory.MAX_VISIBLE_ROWS,
                                true);

        final Consumer callback = ConsumerFactory.makeOnCreationCallback(factory,
                                                                         cell,
                                                                         enumLookups);
        callback.accept(multiValueDOMElement);

        verifyMultipleSelectTest(ConsumerFactory.MAX_VISIBLE_ROWS);

        verify(multiValueWidget).setVisibleItemCount(eq(ConsumerFactory.MAX_VISIBLE_ROWS));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void makeOnCreationCallbackListBoxMultipleSelectMoreThanMaximum() {
        setupMultipleSelectTest(ConsumerFactory.MAX_VISIBLE_ROWS + 1,
                                true);

        final Consumer callback = ConsumerFactory.makeOnCreationCallback(factory,
                                                                         cell,
                                                                         enumLookups);
        callback.accept(multiValueDOMElement);

        verify(multiValueWidget).setVisibleItemCount(eq(ConsumerFactory.MAX_VISIBLE_ROWS));
        verifyMultipleSelectTest(ConsumerFactory.MAX_VISIBLE_ROWS + 1);
    }

    @Test
    public void testMakeOnCreationCallback() {

        final DatePickerDOMElement datePickerDomElement = mock(DatePickerDOMElement.class);
        final DatePicker datePickerWidget = mock(DatePicker.class);
        final String serverDate = "05-01-2018 06:00:00 +0300";
        final String dateConvertedFromServerTimezone = "05-01-2018 00:00:00 -0300";
        final Consumer<SingleValueDOMElement<Date, DatePicker>> callback = ConsumerFactory.makeOnCreationCallback(makeGridCell(asDate(serverDate)));

        when(datePickerDomElement.getWidget()).thenReturn(datePickerWidget);

        callback.accept(datePickerDomElement);

        verify(datePickerWidget).setValue(asDate(dateConvertedFromServerTimezone));
    }

    private void setupMultipleSelectTest(final int enumLookupSize,
                                         final boolean isMultipleSelect) {
        IntStream.rangeClosed(1,
                              enumLookupSize).forEach(i -> enumLookups.put(KEY + i,
                                                                           VALUE + i));
        when(multiValueWidget.isMultipleSelect()).thenReturn(isMultipleSelect);
    }

    @SuppressWarnings("unchecked")
    private void verifyMultipleSelectTest(final int enumLookupSize) {
        IntStream.range(1,
                        enumLookupSize).forEach(i -> verify(multiValueWidget).addItem(eq(VALUE + i),
                                                                                      eq(KEY + i)));
        verify(factory).toWidget(eq(cell),
                                 eq(multiValueWidget));
    }

    private Date asDate(final String dateString) {
        return format().parse(dateString);
    }

    private DateTimeFormat format() {
        return DateTimeFormat.getFormat(TEST_DATE_FORMAT);
    }

    private GridCell<Date> makeGridCell(final Date date) {
        return new GridCell<Date>() {
            @Override
            public GridCellValue<Date> getValue() {
                return () -> date;
            }

            @Override
            public boolean isMerged() {
                return false;
            }

            @Override
            public int getMergedCellCount() {
                return 0;
            }

            @Override
            public boolean isCollapsed() {
                return false;
            }

            @Override
            public void collapse() {
                // Nothing.
            }

            @Override
            public void expand() {
                // Nothing.
            }

            @Override
            public void reset() {
                // Nothing.
            }

            @Override
            public CellSelectionStrategy getSelectionStrategy() {
                return null;
            }

            @Override
            public void setSelectionStrategy(final CellSelectionStrategy selectionStrategy) {
                // Nothing.
            }
        };
    }
}
