/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.client.widgets;

import java.util.Arrays;

import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.sort.SortOrder;
import org.dashbuilder.displayer.DisplayerConstraints;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.Position;
import org.dashbuilder.displayer.client.DataSetHandler;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.dashbuilder.displayer.client.RendererLibrary;
import org.dashbuilder.displayer.client.RendererManager;
import org.dashbuilder.displayer.client.events.DisplayerSettingsChangedEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.properties.editor.model.validators.PropertyFieldValidator;
import org.uberfire.mocks.EventSourceMock;

import static org.dashbuilder.displayer.DisplayerAttributeDef.*;
import static org.dashbuilder.displayer.DisplayerAttributeGroupDef.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DisplayerSettingsEditorTest {

    @Mock
    DisplayerSettingsEditor.View view;

    @Mock
    Displayer displayer;

    @Mock
    DataSetHandler dataSetHandler;

    @Mock
    EventSourceMock<DisplayerSettingsChangedEvent> event;

    @Mock
    DisplayerLocator displayerLocator;

    @Mock
    RendererManager rendererManager;

    @Mock
    RendererLibrary rendererA;

    @Mock
    RendererLibrary rendererB;

    DisplayerSettingsEditor presenter;

    @Before
    public void init() throws Exception {
        when(rendererManager.getRenderersForType(any(DisplayerType.class))).thenReturn(Arrays.asList(rendererA, rendererB));
        when(rendererA.getUUID()).thenReturn("rendererA");
        when(rendererB.getUUID()).thenReturn("rendererB");
        when(rendererManager.getRendererForDisplayer(any(DisplayerSettings.class))).thenReturn(rendererB);

        when(displayerLocator.lookupDisplayer(any(DisplayerSettings.class))).thenReturn(displayer);
        when(displayer.getDataSetHandler()).thenReturn(dataSetHandler);
        when(displayer.getDisplayerConstraints()).thenReturn(new DisplayerConstraints(null)
                .supportsAttribute(TYPE)
                .supportsAttribute(SUBTYPE)
                .supportsAttribute(RENDERER)
                .supportsAttribute(GENERAL_GROUP)
                .supportsAttribute(COLUMNS_GROUP)
                .supportsAttribute(REFRESH_GROUP)
                .supportsAttribute(FILTER_GROUP)
                .supportsAttribute(CHART_GROUP)
                .supportsAttribute(TABLE_GROUP)
                .supportsAttribute(AXIS_GROUP)
                .supportsAttribute(METER_GROUP));

        presenter = new DisplayerSettingsEditor(view, displayerLocator, rendererManager, event);

        // Call to init implies calling to presenter.show() internally
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                presenter.show();
                return null;
            }
        }).when(dataSetHandler).lookupDataSet(any(DataSetReadyCallback.class));
    }

    @Test
    public void testGeneralSettings() {

        when(dataSetHandler.getLastDataSet()).thenReturn(DataSetFactory.newDataSetBuilder()
                .label("dept")
                .number("amount")
                .buildDataSet());

        when(displayer.getDisplayerSettings()).thenReturn(DisplayerSettingsFactory.newBarChartSettings()
                .dataset("dset")
                .group("dept")
                .column("dept").format("Department")
                .column("amount").format("Total", "#.##0").expression("value/100")
                .title("Sales by dept")
                .titleVisible(true)
                .legendOn(Position.BOTTOM)
                .width(400).height(200)
                .xAxisTitle("Depts")
                .yAxisTitle("Amount $")
                .margins(10, 80, 80, 100)
                .filterOn(false, true, true)
                .refreshOn(3, false)
                .buildSettings());

        // Call to init implies calling to presenter.show() internally (see above)
        presenter.init(displayer);

        verify(view).clear();
        verify(view).addTextProperty(TITLE, "Sales by dept");
        verify(view).addBooleanProperty(TITLE_VISIBLE, true);
        verify(view, never()).addBooleanProperty(EXPORT_TO_CSV, false);
        verify(view, never()).addBooleanProperty(EXPORT_TO_XLS, false);
        verify(view).addTextProperty(eq(CHART_WIDTH), eq("400"), any(DisplayerSettingsEditor.LongValidator.class));
        verify(view).addTextProperty(eq(CHART_HEIGHT), eq("200"), any(DisplayerSettingsEditor.LongValidator.class));
        verify(view).addColorProperty(eq(CHART_BGCOLOR), anyString());
        verify(view).addBooleanProperty(CHART_3D, false);
        verify(view).addTextProperty(eq(CHART_MARGIN_TOP), eq("10"), any(DisplayerSettingsEditor.LongValidator.class));
        verify(view).addTextProperty(eq(CHART_MARGIN_BOTTOM), eq("80"), any(DisplayerSettingsEditor.LongValidator.class));
        verify(view).addTextProperty(eq(CHART_MARGIN_LEFT), eq("80"), any(DisplayerSettingsEditor.LongValidator.class));
        verify(view).addTextProperty(eq(CHART_MARGIN_RIGHT), eq("100"), any(DisplayerSettingsEditor.LongValidator.class));
        verify(view).addBooleanProperty(CHART_SHOWLEGEND, true);
        verify(view).addListProperty(eq(CHART_LEGENDPOSITION), anyListOf(String.class), anyString());
        verify(view).addBooleanProperty(XAXIS_SHOWLABELS, true);
        verify(view).addTextProperty(XAXIS_LABELSANGLE, "0");
        verify(view).addTextProperty(XAXIS_TITLE, "Depts");
        verify(view).addBooleanProperty(YAXIS_SHOWLABELS, true);
        verify(view).addTextProperty(YAXIS_TITLE, "Amount $");
        verify(view).addBooleanProperty(FILTER_ENABLED, true);
        verify(view).addBooleanProperty(FILTER_SELFAPPLY_ENABLED, false);
        verify(view).addBooleanProperty(FILTER_NOTIFICATION_ENABLED, true);
        verify(view).addBooleanProperty(FILTER_LISTENING_ENABLED, true);
        verify(view).addTextProperty(eq(REFRESH_INTERVAL), eq("3"), any(DisplayerSettingsEditor.LongValidator.class));
        verify(view).addBooleanProperty(REFRESH_STALE_DATA, false);
        verify(view).addTextProperty(eq("columns.dept.name"), anyString(), eq("Department"));
        verify(view).addTextProperty(eq("columns.amount.name"), anyString(), eq("Total"));
        verify(view).addTextProperty(eq("columns.amount.expression"), anyString(), eq("value/100"));
        verify(view).addTextProperty(eq("columns.amount.pattern"), anyString(), eq("#.##0"));
        verify(view).show();
    }

    @Test
    public void testChangeAttributes() {
        DisplayerSettings settings = DisplayerSettingsFactory.newBarChartSettings().buildSettings();
        when(displayer.getDisplayerSettings()).thenReturn(settings);

        presenter.init(displayer);
        presenter.onAttributeChanged(TITLE.getFullId(), "Test");
        presenter.onAttributeChanged(TITLE_VISIBLE.getFullId(), "true");
        presenter.onAttributeChanged(EXPORT_TO_CSV.getFullId(), "false");
        presenter.onAttributeChanged(EXPORT_TO_XLS.getFullId(), "false");
        presenter.onAttributeChanged(CHART_HEIGHT.getFullId(), "400");
        presenter.onAttributeChanged("columns.amount.name", "Total");
        presenter.onAttributeChanged("columns.amount.pattern", "#.###,00");
        presenter.onAttributeChanged("columns.amount.expression", "value");

        assertEquals(settings.getTitle(), "Test");
        assertEquals(settings.isTitleVisible(), true);
        assertEquals(settings.isCSVExportAllowed(), false);
        assertEquals(settings.isExcelExportAllowed(), false);
        assertEquals(settings.getChartHeight(), 400);
        assertEquals(settings.getColumnSettings("amount").getColumnName(), "Total");
        assertEquals(settings.getColumnSettings("amount").getValuePattern(), "#.###,00");
        assertEquals(settings.getColumnSettings("amount").getValueExpression(), "value");

        verify(event, atLeastOnce()).fire(any(DisplayerSettingsChangedEvent.class));
    }

    @Test
    public void testTableSettings() {

        when(dataSetHandler.getLastDataSet()).thenReturn(DataSetFactory.newDataSetBuilder()
                .label("dept")
                .date("date")
                .number("amount")
                .buildDataSet());

        when(displayer.getDisplayerConstraints()).thenReturn(
                new DisplayerConstraints(null)
                        .supportsAttribute(TABLE_GROUP)
                        .supportsAttribute(EXPORT_GROUP));

        when(displayer.getDisplayerSettings()).thenReturn(DisplayerSettingsFactory.newTableSettings()
                .tablePageSize(10)
                .tableWidth(500)
                .tableOrderEnabled(true)
                .tableOrderDefault("date", SortOrder.ASCENDING)
                .tableColumnPickerEnabled(false)
                .allowCsvExport(true)
                .allowExcelExport(false)
                .buildSettings());

        presenter.init(displayer);

        verify(view).clear();
        verify(view).addTextProperty(eq(TABLE_WIDTH), eq("500"), any(DisplayerSettingsEditor.LongValidator.class));
        verify(view).addBooleanProperty(TABLE_SORTENABLED, true);
        verify(view).addListProperty(eq(TABLE_SORTCOLUMNID), anyListOf(String.class), eq("date"));
        verify(view).addListProperty(eq(TABLE_SORTORDER), anyListOf(String.class), eq(SortOrder.ASCENDING.toString()));
        verify(view).addBooleanProperty(TABLE_COLUMN_PICKER_ENABLED, false);
        verify(view).addBooleanProperty(EXPORT_TO_CSV, true);
        verify(view).addBooleanProperty(EXPORT_TO_XLS, false);
        verify(view).show();
    }

    @Test
    public void testMeterSettings() {

        when(displayer.getDisplayerConstraints()).thenReturn(
                new DisplayerConstraints(null)
                        .supportsAttribute(METER_GROUP));


        when(displayer.getDisplayerSettings()).thenReturn(DisplayerSettingsFactory.newMeterChartSettings()
                        .meter(0, 100, 500, 900)
                        .buildSettings());

        presenter.init(displayer);

        verify(view).clear();
        verify(view).addTextProperty(eq(METER_START), eq("0"), any(DisplayerSettingsEditor.LongValidator.class));
        verify(view).addTextProperty(eq(METER_WARNING), eq("100"), any(DisplayerSettingsEditor.LongValidator.class));
        verify(view).addTextProperty(eq(METER_CRITICAL), eq("500"), any(DisplayerSettingsEditor.LongValidator.class));
        verify(view).addTextProperty(eq(METER_END), eq("900"), any(DisplayerSettingsEditor.LongValidator.class));
        verify(view).show();
    }

    @Test
    public void testRenderer() {
        DisplayerSettings settings = DisplayerSettingsFactory.newBarChartSettings()
                .renderer("rendererB")
                .buildSettings();

        when(rendererManager.getRendererForDisplayer(settings)).thenReturn(rendererB);
        when(displayer.getDisplayerConstraints()).thenReturn(
                new DisplayerConstraints(null)
                        .supportsAttribute(RENDERER));

        when(displayer.getDisplayerSettings()).thenReturn(settings);

        presenter.init(displayer);
        verify(view).clear();
        verify(view).addListProperty(RENDERER, Arrays.asList("rendererA", "rendererB"), "rendererB");
        verify(view).show();
    }

    @Test
    public void testSupportedAttrs() {
        when(displayer.getDisplayerConstraints()).thenReturn(new DisplayerConstraints(null)
                .supportsAttribute(TYPE)
                .supportsAttribute(SUBTYPE)
                .supportsAttribute(TITLE)
                .supportsAttribute(FILTER_GROUP));

        when(displayer.getDisplayerSettings()).thenReturn(DisplayerSettingsFactory.newBarChartSettings().buildSettings());

        presenter.init(displayer);
        assertEquals(presenter.isSupported(TYPE), true);
        assertEquals(presenter.isSupported(SUBTYPE), true);
        assertEquals(presenter.isSupported(TITLE), true);
        assertEquals(presenter.isSupported(TITLE_VISIBLE), false);
        assertEquals(presenter.isSupported(RENDERER), false);
        assertEquals(presenter.isSupported(FILTER_ENABLED), true);
        assertEquals(presenter.isSupported(FILTER_LISTENING_ENABLED), true);
        assertEquals(presenter.isSupported(FILTER_NOTIFICATION_ENABLED), true);
        assertEquals(presenter.isSupported(FILTER_SELFAPPLY_ENABLED), true);
    }

    @Test
    public void testLongValidator() {
        PropertyFieldValidator validator = presenter.createLongValidator();
        assertEquals(validator.validate("500"), true);
        assertEquals(validator.validate("500d"), false);
        assertEquals(validator.validate("aaa"), false);
    }

    @Test
    public void testMeterValidator() {
        DisplayerSettings settings = DisplayerSettingsFactory.newMeterChartSettings()
                .meter(0, 100, 500, 900)
                .buildSettings();

        when(displayer.getDisplayerSettings()).thenReturn(settings);
        presenter.init(displayer);

        PropertyFieldValidator validator = presenter.createMeterValidator(settings, 0);
        assertEquals(validator.validate("aaa"), false);
        assertEquals(validator.validate("0"), true);
        assertEquals(validator.validate("99"), true);
        assertEquals(validator.validate("100"), true);
        assertEquals(validator.validate("101"), false);
        assertEquals(validator.validate("-999999999999"), true);

        validator = presenter.createMeterValidator(settings, 1);
        assertEquals(validator.validate("0"), true);
        assertEquals(validator.validate("99"), true);
        assertEquals(validator.validate("100"), true);
        assertEquals(validator.validate("101"), true);
        assertEquals(validator.validate("500"), true);
        assertEquals(validator.validate("501"), false);
        assertEquals(validator.validate("-1"), false);

        validator = presenter.createMeterValidator(settings, 2);
        assertEquals(validator.validate("99"), false);
        assertEquals(validator.validate("100"), true);
        assertEquals(validator.validate("900"), true);
        assertEquals(validator.validate("901"), false);

        validator = presenter.createMeterValidator(settings, 3);
        assertEquals(validator.validate("499"), false);
        assertEquals(validator.validate("500"), true);
        assertEquals(validator.validate("900"), true);
        assertEquals(validator.validate("10000000000000"), true);
    }
}