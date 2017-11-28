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
package org.dashbuilder.displayer.json;

import java.util.Arrays;
import java.util.Collection;

import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.json.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;
import static org.dashbuilder.dataset.sort.SortOrder.DESCENDING;

@RunWith(Parameterized.class)
public class DisplayerSettingsJsonTest {

    public static final DisplayerSettings OPPS_BY_PIPELINE = DisplayerSettingsFactory.newPieChartSettings()
            .uuid("opps-by-pipeline")
            .dataset("SALES_OPPS")
            .group("PIPELINE")
            .column("PIPELINE", "Pipeline")
            .column(COUNT, "Number of opps")
            .title("salesopps_displayers_by_pipeline_title")
            .titleVisible(false)
            .width(500).height(300)
            .margins(10, 10, 10, 10)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings OPPS_BY_STATUS = DisplayerSettingsFactory.newPieChartSettings()
            .uuid("opps-by-status")
            .dataset("SALES_OPPS")
            .group("STATUS")
            .column("STATUS", "Status")
            .column("AMOUNT", SUM, "Total amount")
            .title("salesopps_displayers_by_status_title")
            .titleVisible(false)
            .margins(10, 10, 10, 10)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings OPPS_BY_SALESMAN = DisplayerSettingsFactory.newPieChartSettings()
            .uuid("opps-by-salesman")
            .dataset("SALES_OPPS")
            .group("SALES_PERSON")
            .column("SALES_PERSON", "Sales person")
            .column("AMOUNT", SUM, "Total amount")
            .title("salesopps_displayers_by_salesman_title")
            .titleVisible(false)
            .margins(10, 10, 10, 10)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings OPPS_EXPECTED_PIPELINE = DisplayerSettingsFactory.newAreaChartSettings()
            .uuid("opps-expected-pipeline")
            .dataset("SALES_OPPS")
            .group("CLOSING_DATE").dynamic(24, DateIntervalType.MONTH, true)
            .column("CLOSING_DATE", "Closing date")
            .column("EXPECTED_AMOUNT", SUM, "salesopps_displayers_by_exp_pipeline_column1")
            .title("salesopps_displayers_by_exp_pipeline_title")
            .titleVisible(false)
            .width(500).height(300)
            .margins(20, 50, 100, 100)
            .filterOn(true, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings OPPS_BY_PRODUCT = DisplayerSettingsFactory.newBarChartSettings()
            .subType_Bar()
            .uuid("opps-by-product")
            .dataset("SALES_OPPS")
            .group("PRODUCT")
            .column("PRODUCT", "Product")
            .column("AMOUNT", SUM, "salesopps_displayers_by_product_column1")
            .title("salesopps_displayers_by_product_title")
            .titleVisible(false)
            .margins(10, 50, 100, 100)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings OPPS_BY_COUNTRY = DisplayerSettingsFactory.newBarChartSettings()
            .subType_Bar()
            .uuid("opps-by-country")
            .dataset("SALES_OPPS")
            .group("COUNTRY")
            .column("COUNTRY", "Country")
            .column("AMOUNT", SUM, "alesopps_displayers_by_country_column1")
            .title("salesopps_displayers_by_country_title")
            .titleVisible(false)
            .margins(10, 80, 100, 100)
            .filterOn(false, true, true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings OPPS_COUNTRY_SUMMARY = DisplayerSettingsFactory.newTableSettings()
            .uuid("opps-country-summary")
            .dataset("SALES_OPPS")
            .group("COUNTRY")
            .column("COUNTRY", "salesopps_displayers_country_summary_column1")
            .column("AMOUNT", SUM, "salesopps_displayers_country_summary_column2")
            .column(COUNT, "salesopps_displayers_country_summary_column3")
            .column("AMOUNT", AVERAGE, "salesopps_displayers_country_summary_column4")
            .column("AMOUNT", MIN, "salesopps_displayers_country_summary_column5")
            .column("AMOUNT", MAX, "salesopps_displayers_country_summary_column6")
            .title("salesopps_displayers_country_summary_title")
            .titleVisible(false)
            .tablePageSize(20)
            .filterOff(true)
            .refreshOn()
            .buildSettings();

    public static final DisplayerSettings OPPS_ALLOPPS_LISTING = DisplayerSettingsFactory.newTableSettings()
            .uuid("opps-allopps-listing")
            .dataset("SALES_OPPS")
            .title("salesopps_displayers_all_list_title")
            .titleVisible(false)
            .tablePageSize(20)
            .tableOrderEnabled(true)
            .tableOrderDefault("AMOUNT", DESCENDING)
            .filterOn(true, true, true)
            .refreshOn()
            .buildSettings();

    @Parameterized.Parameters
    public static Collection displayers() {
        return Arrays.asList(new Object[][]{
                {OPPS_ALLOPPS_LISTING},
                {OPPS_BY_COUNTRY},
                {OPPS_BY_PIPELINE},
                {OPPS_BY_PRODUCT},
                {OPPS_BY_SALESMAN},
                {OPPS_BY_STATUS},
                {OPPS_COUNTRY_SUMMARY},
                {OPPS_EXPECTED_PIPELINE}
        });
    }

    protected DisplayerSettings displayerSettings;

    public DisplayerSettingsJsonTest(DisplayerSettings settings) {
        this.displayerSettings = settings;
    }

    @Test
    public void testSettingsMarshalling() {

        DisplayerSettingsJSONMarshaller displayerJsonMarshaller = DisplayerSettingsJSONMarshaller.get();
        JsonObject _jsonObj = displayerJsonMarshaller.toJsonObject(displayerSettings);
        assertNotNull(_jsonObj.toString());

        DisplayerSettings unmarshalled = displayerJsonMarshaller.fromJsonString(_jsonObj.toString());
        assertEquals(unmarshalled, displayerSettings);
    }
}
