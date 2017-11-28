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
package org.dashbuilder.renderer.client.metric;

import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.dashbuilder.dataset.ExpenseReportsData.*;

@RunWith(MockitoJUnitRunner.class)
public class MetricDisplayerTest extends AbstractDisplayerTest {

    public static final String HTML_TEMPLATE =
            "<div id=\"${this}\" style=\"background-color:${bgColor}; width:${width}px; height:${height}px; " +
            "margin-top:${marginTop}px; margin-right:${marginRight}px; margin-bottom:${marginBottom}px; margin-left:${marginLeft}px;\">\n" +
            "  <span>${title}</span>\n" +
            "  <span>${value}</span>\n" +
            "</div>";

    public static final String JS_TEMPLATE = "if (${isFilterEnabled}) {  \n" +
            "  var filterOn = ${isFilterOn};\n" +
            "  ${this}.style.cursor=\"pointer\";\n" +
            "  ${this}.onclick = function() {\n" +
            "    filterOn = !filterOn;\n" +
            "    ${this}.style.backgroundColor = filterOn ? \"lightblue\" : \"${bgColor}\";\n" +
            "    ${doFilter};\n" +
            "  };\n" +
            "}";

    @Mock
    MetricDisplayer.View view;

    @Mock
    DisplayerListener listener;

    public MetricDisplayer createMetricDisplayer(DisplayerSettings settings) {
        MetricDisplayer displayer = initDisplayer(new MetricDisplayer(view), settings);
        displayer.addListener(listener);
        return displayer;
    }

    @Before
    public void setUp() {
        when(view.getUniqueId()).thenReturn("test");
    }

    @Test
    public void testDraw() {
        DisplayerSettings engExpenses = DisplayerSettingsFactory.newMetricSettings()
                .dataset(EXPENSES)
                .filter(COLUMN_DEPARTMENT, FilterFactory.equalsTo("Engineering"))
                .column(COLUMN_AMOUNT, AggregateFunctionType.SUM)
                .title("Title").titleVisible(true)
                .width(300).height(200)
                .margins(10, 20, 30, 40)
                .backgroundColor("FDE8D4")
                .filterOff(true)
                .htmlTemplate(HTML_TEMPLATE)
                .jsTemplate("alert('${value.raw}');")
                .buildSettings();

        MetricDisplayer presenter = createMetricDisplayer(engExpenses);
        presenter.draw();

        verify(view).setHtml("<div id=\"test_this\" style=\"background-color:#FDE8D4; width:300px; height:200px; " +
                "margin-top:10px; margin-right:40px; margin-bottom:20px; margin-left:30px;\">\n" +
                "  <span>Title</span>\n" +
                "  <span>7,650.16</span>\n" +
                "</div>");

        verify(view).eval("alert('7650.16');");
    }

    @Test
    public void testDefaultTemplates() {
        DisplayerSettings engExpenses = DisplayerSettingsFactory.newMetricSettings()
                .dataset(EXPENSES)
                .buildSettings();

        MetricDisplayer presenter = createMetricDisplayer(engExpenses);
        String html = presenter.getHtmlTemplate();
        String js = presenter.getJsTemplate();
        assertFalse(StringUtils.isBlank(html));
        assertFalse(StringUtils.isBlank(js));
        assertFalse(html.equals(js));
    }

    @Test
    public void testNoData() {
        DisplayerSettings empty = DisplayerSettingsFactory.newMetricSettings()
                .dataset(EXPENSES)
                .filter(COLUMN_ID, FilterFactory.isNull())
                .column(COLUMN_AMOUNT)
                .title("Title").titleVisible(true)
                .width(300).height(200)
                .margins(10, 20, 30, 40)
                .backgroundColor("FDE8D4")
                .htmlTemplate(HTML_TEMPLATE)
                .buildSettings();

        when(view.getNoDataString()).thenReturn("0,0");
        MetricDisplayer presenter = createMetricDisplayer(empty);
        MetricDisplayer.View view = presenter.getView();
        presenter.draw();

        verify(view, atLeastOnce()).getNoDataString();
        verify(view).setHtml("<div id=\"test_this\" style=\"background-color:#FDE8D4; width:300px; height:200px; " +
                "margin-top:10px; margin-right:40px; margin-bottom:20px; margin-left:30px;\">\n" +
                "  <span>Title</span>\n" +
                "  <span>0,0</span>\n" +
                "</div>");
    }

    @Test
    public void testNoFilter() {
        DisplayerSettings empty = DisplayerSettingsFactory.newMetricSettings()
                .dataset(EXPENSES)
                .column(COLUMN_AMOUNT)
                .filterOn(false, true, true)
                .buildSettings();

        MetricDisplayer presenter = createMetricDisplayer(empty);
        MetricDisplayer.View view = presenter.getView();
        presenter.draw();

        reset(view);
        reset(listener);
        presenter.filterApply();

        verify(listener, never()).onFilterEnabled(eq(presenter), any(DataSetFilter.class));
    }

    @Test
    public void testSwitchOnFilter() {
        DisplayerSettings empty = DisplayerSettingsFactory.newMetricSettings()
                .dataset(EXPENSES)
                .filter(COLUMN_ID, FilterFactory.isNull())
                .column(COLUMN_AMOUNT)
                .filterOn(false, true, true)
                .buildSettings();

        MetricDisplayer presenter = createMetricDisplayer(empty);
        MetricDisplayer.View view = presenter.getView();
        presenter.draw();

        reset(view);
        reset(listener);
        presenter.updateFilter();

        verify(listener).onFilterEnabled(eq(presenter), any(DataSetFilter.class));
    }

    @Test
    public void testSwitchOffFilter() {
        DisplayerSettings empty = DisplayerSettingsFactory.newMetricSettings()
                .dataset(EXPENSES)
                .filter(COLUMN_ID, FilterFactory.isNull())
                .column(COLUMN_AMOUNT)
                .filterOn(false, true, true)
                .buildSettings();

        MetricDisplayer presenter = createMetricDisplayer(empty);
        MetricDisplayer.View view = presenter.getView();
        presenter.draw();
        presenter.filterApply();

        reset(view);
        reset(listener);
        presenter.filterReset();

        verify(listener).onFilterReset(eq(presenter), any(DataSetFilter.class));
    }

    @Test
    public void testFilterOn() {
        DisplayerSettings empty = DisplayerSettingsFactory.newMetricSettings()
                .dataset(EXPENSES)
                .filter(COLUMN_ID, FilterFactory.isNull())
                .column(COLUMN_AMOUNT)
                .filterOn(false, true, true)
                .jsTemplate(JS_TEMPLATE)
                .buildSettings();

        MetricDisplayer presenter = createMetricDisplayer(empty);
        presenter.setFilterOn(true);
        presenter.draw();
        assertEquals(presenter.isFilterOn(), true);
        verify(view).eval("if (true) {  \n" +
                "  var filterOn = true;\n" +
                "  document.getElementById(\"test_this\").style.cursor=\"pointer\";\n" +
                "  document.getElementById(\"test_this\").onclick = function() {\n" +
                "    filterOn = !filterOn;\n" +
                "    document.getElementById(\"test_this\").style.backgroundColor = filterOn ? \"lightblue\" : \"white\";\n" +
                "    window.metricDisplayerDoFilter('test');\n" +
                "  };\n" +
                "}");
    }

    @Test
    public void testFilterOff() {
        DisplayerSettings empty = DisplayerSettingsFactory.newMetricSettings()
                .dataset(EXPENSES)
                .filter(COLUMN_ID, FilterFactory.isNull())
                .column(COLUMN_AMOUNT)
                .filterOn(false, true, true)
                .jsTemplate(JS_TEMPLATE)
                .buildSettings();

        MetricDisplayer presenter = createMetricDisplayer(empty);
        presenter.setFilterOn(false);
        presenter.draw();
        assertEquals(presenter.isFilterOn(), false);
        verify(view).eval("if (true) {  \n" +
                "  var filterOn = false;\n" +
                "  document.getElementById(\"test_this\").style.cursor=\"pointer\";\n" +
                "  document.getElementById(\"test_this\").onclick = function() {\n" +
                "    filterOn = !filterOn;\n" +
                "    document.getElementById(\"test_this\").style.backgroundColor = filterOn ? \"lightblue\" : \"white\";\n" +
                "    window.metricDisplayerDoFilter('test');\n" +
                "  };\n" +
                "}");
    }
}