/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.i18n.client.DateTimeFormat;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.datepicker.DatePickerDOMElement;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.datepicker.DatePickerSingletonDOMElementFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.common.DatePicker;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;

import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.DATE_FORMAT;
import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.KIE_TIMEZONE_OFFSET;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class DateUiColumnTest {

    private static final String TEST_DATE_FORMAT = "MM-dd-yyyy HH:mm:ss Z";

    @Mock
    private DatePickerSingletonDOMElementFactory factory;

    @BeforeClass
    public static void setup() {
        System.setProperty("user.timezone", "Europe/Vilnius");

        ApplicationPreferences.setUp(new HashMap<String, String>() {{
            put(KIE_TIMEZONE_OFFSET, "10800000");
            put(DATE_FORMAT, TEST_DATE_FORMAT);
        }});
    }

    @Test
    public void testDateUiColumnTextFormat() {

        final Text text = mock(Text.class);
        final GridBodyCellRenderContext context = mock(GridBodyCellRenderContext.class);
        final String clientDate = "05-01-2018 00:00:00 -0300";
        final String clientFormattedWithServerTimeZoneDate = "05-01-2018 06:00:00 +0300";

        renderer().doRenderCellContent(text, date(clientDate), context);

        verify(text).setText(clientFormattedWithServerTimeZoneDate);
    }

    private BaseSingletonDOMElementUiColumn.CellRenderer<Date, DatePicker, DatePickerDOMElement> renderer() {
        return DateUiColumn.makeColumnRenderer(factory);
    }

    private Date date(final String dateString) {
        return DateTimeFormat.getFormat(TEST_DATE_FORMAT).parse(dateString);
    }
}
