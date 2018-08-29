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

package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.datepicker;

import java.util.Date;
import java.util.HashMap;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.junit.GWTMockUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.util.TimeZoneUtils;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.ext.widgets.common.client.common.DatePicker;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.DATE_FORMAT;
import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.KIE_TIMEZONE_OFFSET;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({DatePickerSingletonDOMElementFactory.class, DateTimeFormat.class, TimeZoneUtils.class})
@RunWith(PowerMockRunner.class)
public class DatePickerSingletonDOMElementFactoryTest {

    private static final String TEST_DATE_FORMAT = "MM-dd-yyyy HH:mm:ss Z";

    @Mock
    private GridLienzoPanel gridPanel;

    @Mock
    private GridLayer gridLayer;

    @Mock
    private GuidedDecisionTableView gridWidget;

    @Mock
    private DatePicker datePicker;

    @BeforeClass
    public static void setupStatic() {
        preventGWTCreateError();
        setStandardTimeZone();
        mockStaticMethods();
        initializeApplicationPreferences();
    }

    private static void preventGWTCreateError() {
        GWTMockUtilities.disarm();
    }

    private static void initializeApplicationPreferences() {
        ApplicationPreferences.setUp(new HashMap<String, String>() {{
            put(KIE_TIMEZONE_OFFSET, "10800000");
            put(DATE_FORMAT, TEST_DATE_FORMAT);
        }});
    }

    private static void mockStaticMethods() {
        mockStatic(DateTimeFormat.class);
        PowerMockito.when(DateTimeFormat.getFormat(anyString())).thenReturn(mock(DateTimeFormat.class));
    }

    private static void setStandardTimeZone() {
        System.setProperty("user.timezone", "Europe/Vilnius");
    }

    @Test
    public void testGetValue() {

        final DatePickerSingletonDOMElementFactory factory = spy(makeFactory());
        final Date date = mock(Date.class);
        final Date convertedDate = mock(Date.class);

        doReturn(datePicker).when(factory).getWidget();
        when(datePicker.getValue()).thenReturn(date);

        mockStatic(TimeZoneUtils.class);
        PowerMockito.when(TimeZoneUtils.convertToServerTimeZone(date)).thenReturn(convertedDate);

        final Date actualDate = factory.getValue();

        assertEquals(convertedDate, actualDate);
    }

    private DatePickerSingletonDOMElementFactory makeFactory() {
        return new DatePickerSingletonDOMElementFactory(gridPanel, gridLayer, gridWidget);
    }
}
