/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells;

import java.util.Date;
import java.util.HashMap;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.util.TimeZoneUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.uberfire.ext.widgets.common.client.common.DatePicker;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.DATE_FORMAT;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class PopupDateEditCellTest {

    private static final String TEST_DATE_FORMAT = "MM-dd-yyyy";

    @GwtMock
    private DatePicker datePicker;

    @GwtMock
    private ValueUpdater<Date> valueUpdater;

    @GwtMock
    private SafeHtmlRenderer<String> renderer;

    @Captor
    private ArgumentCaptor<Date> dateCaptor;

    private PopupDateEditCell cell;

    @Before
    public void setup() {
        ApplicationPreferences.setUp(new HashMap<String, String>() {{
            put(DATE_FORMAT, TEST_DATE_FORMAT);
        }});

        cell = spy(new PopupDateEditCell(false) {

            @Override
            DatePicker getDatePicker() {
                return PopupDateEditCellTest.this.datePicker;
            }

            @Override
            ValueUpdater<Date> getValueUpdater() {
                return PopupDateEditCellTest.this.valueUpdater;
            }

            @Override
            SafeHtmlRenderer<String> getRenderer() {
                return PopupDateEditCellTest.this.renderer;
            }

            @Override
            public void setValue(final Context context,
                                 final Element parent,
                                 final Date value) {
                // Nothing.
            }
        });
    }

    @Test
    public void testStartEditing() {

        final Cell.Context context = mock(Cell.Context.class);
        final Element parent = mock(Element.class);
        final Date expectedDate = makeDate("04-01-2018");

        cell.startEditing(context, parent, expectedDate);

        verify(datePicker).setValue(dateCaptor.capture());

        final Date actualDate = dateCaptor.getValue();

        assertEquals(expectedDate, actualDate);
    }

    @Test
    public void testCommit() {

        final Date clientDate = makeDate("04-01-2018");
        final Date serverDate = makeDate("04-01-2018");

        when(datePicker.getValue()).thenReturn(clientDate);
        doReturn(serverDate).when(cell).convertToServerTimeZone(clientDate);

        cell.commit();

        verify(cell).setValue(any(Cell.Context.class), any(Element.class), eq(serverDate));
        verify(valueUpdater).update(eq(serverDate));
    }

    @Test
    public void testCommitWhenValueUpdaterIsNull() {

        final Date clientDate = makeDate("04-01-2018");
        final Date serverDate = makeDate("04-01-2018");

        when(datePicker.getValue()).thenReturn(clientDate);
        doReturn(null).when(cell).getValueUpdater();
        doReturn(serverDate).when(cell).convertToServerTimeZone(clientDate);

        cell.commit();

        verify(cell).setValue(any(Cell.Context.class), any(Element.class), eq(serverDate));
        verify(valueUpdater, never()).update(any(Date.class));
    }

    @Test
    public void testRender() {

        final Cell.Context context = mock(Cell.Context.class);
        final SafeHtmlBuilder safeHtmlBuilder = mock(SafeHtmlBuilder.class);
        final Date clientDate = makeDate("05-01-2018");

        cell.render(context, clientDate, safeHtmlBuilder);

        verify(renderer).render(eq("05-01-2018"));
    }

    @Test
    public void testRenderWhenValueIsNull() {

        final Cell.Context context = mock(Cell.Context.class);
        final SafeHtmlBuilder safeHtmlBuilder = mock(SafeHtmlBuilder.class);

        cell.render(context, null, safeHtmlBuilder);

        verify(renderer, never()).render(anyString());
    }

    @Test
    public void testGetPattern() {
        assertEquals(TEST_DATE_FORMAT, cell.getPattern());
    }

    private Date makeDate(final String dateString) {
        return TimeZoneUtils.FORMATTER.parse(dateString);
    }
}
