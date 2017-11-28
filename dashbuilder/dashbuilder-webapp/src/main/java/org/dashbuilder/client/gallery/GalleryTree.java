/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.gallery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;
import org.dashbuilder.renderer.client.DefaultRenderer;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.dashbuilder.dataset.group.DateIntervalType.*;
import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.dashbuilder.dataset.date.Month.*;
import static org.dashbuilder.shared.sales.SalesConstants.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;

/**
 * The Gallery tree.
 */
@Dependent
public class GalleryTree {

    private List<GalleryTreeNode> mainNodes = new ArrayList<>();
    private DisplayerSettingsJSONMarshaller jsonMarshaller = DisplayerSettingsJSONMarshaller.get();

    public List<GalleryTreeNode> getMainNodes() {
        return mainNodes;
    }

    @PostConstruct
    private void init() {
        initBarChartCategory();
        initPieChartCategory();
        initLineChartCategory();
        initAreaChartCategory();
        initBubbleChartCategory();
        initTableReportCategory();
        initMeterChartCategory();
        initMetricCategory();
        initMapChartCategory();
        initSelectorCategory();
        initDashboardCategory();
    }

    private PlaceRequest createPlaceRequest(DisplayerSettings displayerSettings) {
        String json = jsonMarshaller.toJsonString(displayerSettings);
        Map<String,String> params = new HashMap<>();
        params.put("json", json);
        params.put("edit", "false");
        params.put("showRendererSelector", "true");
        return new DefaultPlaceRequest("DisplayerScreen", params);
    }

    private PlaceRequest createPlaceRequest(String widgetId) {
        Map<String,String> params = new HashMap<>();
        params.put("widgetId", widgetId);
        return new DefaultPlaceRequest("GalleryWidgetScreen", params);
    }

    private void initBarChartCategory() {
        GalleryTreeNodeList nodeList = new GalleryTreeNodeList(AppConstants.INSTANCE.gallerytree_bar());
        mainNodes.add(nodeList);

        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_bar_horiz(), createPlaceRequest(
                DisplayerSettingsFactory.newBarChartSettings()
                        .subType_Bar()
                        .dataset(SALES_OPPS)
                        .group(PRODUCT)
                        .column(PRODUCT, "Product")
                        .column(AMOUNT, SUM)
                        .expression("value/1000")
                        .format(AppConstants.INSTANCE.gallerytree_bar_horiz_column1(), "$ #,### K")
                        .title(AppConstants.INSTANCE.gallerytree_bar_horiz_title())
                        .width(600).height(400)
                        .resizableOn(1200, 800)
                        .margins(50, 80, 120, 120)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_bar_vert(), createPlaceRequest(
                DisplayerSettingsFactory.newBarChartSettings()
                        .subType_Column()
                        .dataset(SALES_OPPS)
                        .group(PRODUCT)
                        .column(PRODUCT, "Product")
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_bar_vert_column1(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_bar_vert_title())
                        .set3d(true)
                        .width(600).height(400)
                        .resizableOn(1200, 800)
                        .margins(50, 80, 120, 120)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_bar_multi(), createPlaceRequest(
                DisplayerSettingsFactory.newBarChartSettings()
                        .subType_Bar()
                        .dataset(SALES_OPPS)
                        .group(COUNTRY)
                        .column(COUNTRY, "Country")
                        .column(AMOUNT, MIN).format(AppConstants.INSTANCE.gallerytree_bar_multi_column1(), "$ #,###")
                        .column(AMOUNT, MAX).format(AppConstants.INSTANCE.gallerytree_bar_multi_column2(), "$ #,###")
                        .column(AMOUNT, AVERAGE).format(AppConstants.INSTANCE.gallerytree_bar_multi_column3(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_bar_multi_title())
                        .width(700).height(600)
                        .resizableOn(1200, 800)
                        .margins(50, 80, 120, 120)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_bar_stacked(), createPlaceRequest(
                DisplayerSettingsFactory.newBarChartSettings()
                        .subType_StackedColumn()
                        .dataset(SALES_OPPS)
                        .group(COUNTRY)
                        .column(COUNTRY, "Country")
                        .column(AMOUNT, MIN).format(AppConstants.INSTANCE.gallerytree_bar_multi_column1(), "$ #,###")
                        .column(AMOUNT, MAX).format(AppConstants.INSTANCE.gallerytree_bar_multi_column2(), "$ #,###")
                        .column(AMOUNT, AVERAGE).format(AppConstants.INSTANCE.gallerytree_bar_multi_column3(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_bar_multi_title())
                        .width(800).height(400)
                        .margins(50, 80, 120, 120)
                        .legendOn("top")
                        .filterOn(false, true, true)
                        .buildSettings()
        )));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_bar_vert_dd(), createPlaceRequest(
                DisplayerSettingsFactory.newBarChartSettings()
                        .subType_Column()
                        .dataset(SALES_OPPS)
                        .group(PIPELINE)
                        .column(PIPELINE, "Pipeline")
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_bar_vert_dd_column1(), "$ #,###")
                        .group(STATUS)
                        .column(STATUS, "Status")
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_bar_vert_dd_column2(), "$ #,###")
                        .group(SALES_PERSON)
                        .column(SALES_PERSON, "Sales person")
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_bar_vert_dd_column3(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_bar_vert_dd_title())
                        .width(600).height(400)
                        .resizableOn(1200, 800)
                        .margins(50, 80, 120, 120)
                        .filterOn(true, false, false)
                        .buildSettings()
        )));
    }

    private void initPieChartCategory() {
        GalleryTreeNodeList nodeList = new GalleryTreeNodeList(AppConstants.INSTANCE.gallerytree_pie());
        mainNodes.add(nodeList);

        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_pie_basic(), createPlaceRequest(
                DisplayerSettingsFactory.newPieChartSettings()
                        .dataset(SALES_OPPS)
                        .group(STATUS)
                        .column(STATUS)
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_pie_basic_column1(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_pie_basic_title())
                        .width(500)
                        .margins(10, 10, 10, 150)
                        .subType_Pie()
                        .legendOn("right")
                        .resizableOn(1200, 800)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));

        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_pie_3d(), createPlaceRequest(
                DisplayerSettingsFactory.newPieChartSettings()
                        .dataset(SALES_OPPS)
                        .group(STATUS)
                        .column(STATUS)
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_pie_3d_column1(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_pie_3d_title())
                        .width(500)
                        .margins(10, 10, 10, 150)
                        .subType_Pie_3d()
                        .legendOn("right")
                        .resizableOn(1200, 800)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));

        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_pie_donut(), createPlaceRequest(
                DisplayerSettingsFactory.newPieChartSettings()
                        .dataset(SALES_OPPS)
                        .group(STATUS)
                        .column(STATUS)
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_pie_donut_column1(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_pie_donut_title())
                        .width(500)
                        .margins(10, 10, 10, 150)
                        .subType_Donut()
                        .legendOn("right")
                        .margins(10, 10, 10, 10)
                        .resizableOn(1200, 800)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));

        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_pie_dd(), createPlaceRequest(
                DisplayerSettingsFactory.newPieChartSettings()
                        .dataset(SALES_OPPS)
                        .group(PIPELINE)
                        .column(PIPELINE, "Pipeline")
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_pie_dd_column1(), "$ #,###")
                        .group(STATUS)
                        .column(STATUS, "Status")
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_pie_dd_column2(), "$ #,###")
                        .group(SALES_PERSON)
                        .column(SALES_PERSON, "Sales person")
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_pie_dd_column3(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_pie_dd_title())
                        .margins(10, 10, 10, 10)
                        .resizableOn(1200, 800)
                        .filterOn(true, false, false)
                        .buildSettings()
        )));
    }

    private void initLineChartCategory() {
        GalleryTreeNodeList nodeList = new GalleryTreeNodeList(AppConstants.INSTANCE.gallerytree_line());
        mainNodes.add(nodeList);

        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_line_basic(), createPlaceRequest(
                DisplayerSettingsFactory.newLineChartSettings()
                        .dataset(SALES_OPPS)
                        .group(CLOSING_DATE).dynamic(12, MONTH, true)
                        .column(CLOSING_DATE).format(AppConstants.INSTANCE.gallerytree_line_basic_column1(), "MMM dd, yyyy")
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_line_basic_column2(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_line_basic_title())
                        .margins(20, 50, 100, 120)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_line_multi(), createPlaceRequest(
                DisplayerSettingsFactory.newLineChartSettings()
                        .dataset(SALES_OPPS)
                        .group(COUNTRY)
                        .column(COUNTRY, "Country")
                        .column(AMOUNT, MIN).format(AppConstants.INSTANCE.gallerytree_line_multi_column1(), "$ #,###")
                        .column(AMOUNT, MAX).format(AppConstants.INSTANCE.gallerytree_line_multi_column2(), "$ #,###")
                        .column(AMOUNT, AVERAGE).format(AppConstants.INSTANCE.gallerytree_line_multi_column3(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_line_multi_title())
                        .margins(30, 100, 80, 80)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_line_multi_static(), createPlaceRequest(
                DisplayerSettingsFactory.newLineChartSettings()
                        .title(AppConstants.INSTANCE.gallerytree_line_multi_static_title())
                        .margins(20, 80, 50, 120)
                        .column("month", "Month")
                        .column("2014").format(AppConstants.INSTANCE.gallerytree_line_multi_static_column1(), "$ #,###")
                        .column("2015").format(AppConstants.INSTANCE.gallerytree_line_multi_static_column2(), "$ #,###")
                        .column("2016").format(AppConstants.INSTANCE.gallerytree_line_multi_static_column3(), "$ #,###")
                        .dataset(DataSetFactory.newDataSetBuilder()
                                .label("month")
                                .number("2014")
                                .number("2015")
                                .number("2016")
                                .row(JANUARY, 1000d, 2000d, 3000d)
                                .row(FEBRUARY, 1400d, 2300d, 2000d)
                                .row(MARCH, 1300d, 2000d, 1400d)
                                .row(APRIL, 900d, 2100d, 1500d)
                                .row(MAY, 1300d, 2300d, 1600d)
                                .row(JUNE, 1010d, 2000d, 1500d)
                                .row(JULY, 1050d, 2400d, 3000d)
                                .row(AUGUST, 2300d, 2000d, 3200d)
                                .row(SEPTEMBER, 1900d, 2700d, 3000d)
                                .row(OCTOBER, 1200d, 2200d, 3100d)
                                .row(NOVEMBER, 1400d, 2100d, 3100d)
                                .row(DECEMBER, 1100d, 2100d, 4200d)
                                .buildDataSet())
                        .buildSettings()
        )));
    }

    private void initAreaChartCategory() {
        GalleryTreeNodeList nodeList = new GalleryTreeNodeList(AppConstants.INSTANCE.gallerytree_area());
        mainNodes.add(nodeList);

        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_area_basic(), createPlaceRequest(
                DisplayerSettingsFactory.newAreaChartSettings()
                        .dataset(SALES_OPPS)
                        .group(CLOSING_DATE).dynamic(24, MONTH, true)
                        .column(CLOSING_DATE, "Closing date")
                        .column(EXPECTED_AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_area_basic_column1(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_area_basic_title())
                        .width(700).height(300)
                        .margins(20, 50, 100, 120)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_area_fixed(), createPlaceRequest(
                DisplayerSettingsFactory.newAreaChartSettings()
                        .dataset(SALES_OPPS)
                        .group(CLOSING_DATE).fixed(MONTH, true).firstMonth(JANUARY).asc()
                        .column(CLOSING_DATE).format(AppConstants.INSTANCE.gallerytree_area_fixed_column1())
                        .column(EXPECTED_AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_area_fixed_column2(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_area_fixed_title())
                        .margins(20, 80, 100, 100)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_area_dd(), createPlaceRequest(
                DisplayerSettingsFactory.newAreaChartSettings()
                        .dataset(SALES_OPPS)
                        .group(CLOSING_DATE).dynamic(12, true)
                        .column(CLOSING_DATE).format(AppConstants.INSTANCE.gallerytree_area_dd_column1())
                        .column(EXPECTED_AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_area_dd_column2(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_area_dd_title())
                        .margins(20, 70, 100, 120)
                        .filterOn(true, true, true)
                        .buildSettings()
        )));
    }

    private void initBubbleChartCategory() {
        GalleryTreeNodeList nodeList = new GalleryTreeNodeList(AppConstants.INSTANCE.gallerytree_bubble());
        mainNodes.add(nodeList);

        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_bubble_basic(), createPlaceRequest(
                DisplayerSettingsFactory.newBubbleChartSettings()
                        .dataset(SALES_OPPS)
                        .group(COUNTRY)
                        .column(COUNTRY, "Country")
                        .column(COUNT, "#opps").format(AppConstants.INSTANCE.gallerytree_bubble_basic_column1(), "#,###")
                        .column(PROBABILITY, AVERAGE).format(AppConstants.INSTANCE.gallerytree_bubble_basic_column2(), "#,###")
                        .column(COUNTRY, AppConstants.INSTANCE.gallerytree_bubble_basic_column4())
                        .column(EXPECTED_AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_bubble_basic_column3(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_bubble_basic_title())
                        .width(700).height(400)
                        .margins(20, 50, 50, 0)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));
    }

    private void initMeterChartCategory() {
        GalleryTreeNodeList nodeList = new GalleryTreeNodeList(AppConstants.INSTANCE.gallerytree_meter());
        mainNodes.add(nodeList);

        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_meter_basic(), createPlaceRequest(
                DisplayerSettingsFactory.newMeterChartSettings()
                        .title(AppConstants.INSTANCE.gallerytree_meter_basic_title())
                        .dataset(SALES_OPPS)
                        .column(AMOUNT, SUM, AppConstants.INSTANCE.gallerytree_meter_basic_column1())
                        .expression("value/1000")
                        .format(AppConstants.INSTANCE.gallerytree_meter_basic_column1(), "$ #,### K")
                        .width(400).height(200)
                        .meter(0, 15000, 25000, 35000)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_meter_multi(), createPlaceRequest(
                DisplayerSettingsFactory.newMeterChartSettings()
                        .title(AppConstants.INSTANCE.gallerytree_meter_multi_title())
                        .dataset(SALES_OPPS)
                        .group(CREATION_DATE).dynamic(12, YEAR, true)
                        .column(CREATION_DATE, "Year")
                        .column(AMOUNT, SUM)
                        .expression("value/1000")
                        .format(AppConstants.INSTANCE.gallerytree_meter_multi_column1(), "$ #,###")
                        .width(600).height(200)
                        .meter(0, 1000, 3000, 5000)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_meter_multi_static(), createPlaceRequest(
                DisplayerSettingsFactory.newMeterChartSettings()
                        .title(AppConstants.INSTANCE.gallerytree_meter_multi_static_title())
                        .width(500).height(200)
                        .meter(30, 160, 190, 220)
                        .column("person").format(AppConstants.INSTANCE.gallerytree_meter_multi_static_column1())
                        .column("heartRate").format(AppConstants.INSTANCE.gallerytree_meter_multi_static_column2(), "#,### bpm")
                        .dataset(DataSetFactory.newDataSetBuilder()
                                .label("person")
                                .number("heartRate")
                                .row("David", 52)
                                .row("Roger", 120)
                                .row("Mark", 74)
                                .row("Michael", 78)
                                .row("Kris", 74)
                                .buildDataSet())
                        .buildSettings()
        )));
    }

    private void initMetricCategory() {
        GalleryTreeNodeList nodeList = new GalleryTreeNodeList(AppConstants.INSTANCE.gallerytree_metrics());
        mainNodes.add(nodeList);

        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_metrics_basic(), createPlaceRequest(
                DisplayerSettingsFactory.newMetricSettings()
                        .title(AppConstants.INSTANCE.gallerytree_metrics_basic_title())
                        .titleVisible(true)
                        .dataset(SALES_OPPS)
                        .filter(CLOSING_DATE, timeFrame("begin[quarter February] till now"))
                        .column(AMOUNT, SUM).expression("value/1000").format(AppConstants.INSTANCE.gallerytree_metrics_basic_column1(), "$ #,### K")
                        .width(300).height(150)
                        .margins(50, 50, 50, 50)
                        .backgroundColor("FDE8D4")
                        .filterOn(false, false, true)
                        .buildSettings()
        )));

        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_metrics_basic_static(), createPlaceRequest(
                DisplayerSettingsFactory.newMetricSettings()
                        .title(AppConstants.INSTANCE.gallerytree_metrics_basic_static_title())
                        .titleVisible(true)
                        .column("tweets").format(AppConstants.INSTANCE.gallerytree_metrics_basic_static_column1(), "#,###")
                        .width(300).height(150)
                        .margins(50, 50, 50, 50)
                        .backgroundColor("ADE8D4")
                        .filterOff(true)
                        .dataset(DataSetFactory.newDataSetBuilder()
                        .number("tweets")
                        .row(54213d)
                        .buildDataSet())
                        .buildSettings()
        )));
    }

    private void initMapChartCategory() {
        GalleryTreeNodeList nodeList = new GalleryTreeNodeList(AppConstants.INSTANCE.gallerytree_map());
        mainNodes.add(nodeList);

        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_map_region(), createPlaceRequest(
                DisplayerSettingsFactory.newMapChartSettings()
                        .dataset(SALES_OPPS)
                        .group(COUNTRY)
                        .column(COUNTRY, "Country")
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_map_region_column1(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_map_region_title())
                        .subType_Region_Map()
                        .width(700).height(500)
                        .margins(10, 10, 10, 10)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_map_marker(), createPlaceRequest(
                DisplayerSettingsFactory.newMapChartSettings()
                        .dataset(SALES_OPPS)
                        .group(COUNTRY)
                        .column(COUNTRY, "Country")
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_map_marker_column1(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_map_marker_title())
                        .subType_Marker_Map()
                        .width(700).height(500)
                        .margins(10, 10, 10, 10)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));
    }

    private void initTableReportCategory() {
        GalleryTreeNodeList nodeList = new GalleryTreeNodeList(AppConstants.INSTANCE.gallerytree_table());
        mainNodes.add(nodeList);

        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_table_basic(), createPlaceRequest(
                DisplayerSettingsFactory.newTableSettings()
                        .dataset(SALES_OPPS)
                        .column(COUNTRY, AppConstants.INSTANCE.gallerytree_table_basic_column1())
                        .column(CUSTOMER, AppConstants.INSTANCE.gallerytree_table_basic_column2())
                        .column(PRODUCT, AppConstants.INSTANCE.gallerytree_table_basic_column3())
                        .column(SALES_PERSON, AppConstants.INSTANCE.gallerytree_table_basic_column4())
                        .column(STATUS, AppConstants.INSTANCE.gallerytree_table_basic_column5())
                        .column(SOURCE, AppConstants.INSTANCE.gallerytree_table_basic_column6())
                        .column(CREATION_DATE, AppConstants.INSTANCE.gallerytree_table_basic_column7())
                        .column(EXPECTED_AMOUNT, AppConstants.INSTANCE.gallerytree_table_basic_column8())
                        .column(CLOSING_DATE).format(AppConstants.INSTANCE.gallerytree_table_basic_column9(), "MMM dd, yyyy")
                        .column(AMOUNT).format(AppConstants.INSTANCE.gallerytree_table_basic_column10(), "$ #,##0.00")
                        .title(AppConstants.INSTANCE.gallerytree_table_basic_title())
                        .tablePageSize(10)
                        .tableOrderEnabled(true)
                        .tableOrderDefault(AMOUNT, DESCENDING)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_table_filtered(), createPlaceRequest(
                DisplayerSettingsFactory.newTableSettings()
                        .dataset(SALES_OPPS)
                        .column(CUSTOMER, AppConstants.INSTANCE.gallerytree_table_filtered_column1())
                        .column(PRODUCT, AppConstants.INSTANCE.gallerytree_table_filtered_column2())
                        .column(STATUS, AppConstants.INSTANCE.gallerytree_table_filtered_column3())
                        .column(SOURCE, AppConstants.INSTANCE.gallerytree_table_filtered_column4())
                        .column(CREATION_DATE, AppConstants.INSTANCE.gallerytree_table_filtered_column5())
                        .column(EXPECTED_AMOUNT).format(AppConstants.INSTANCE.gallerytree_table_filtered_column6(), "$ #,##0.00")
                        .column(CLOSING_DATE).format(AppConstants.INSTANCE.gallerytree_table_filtered_column7(), "MMM dd, yyyy")
                        .column(AMOUNT).format(AppConstants.INSTANCE.gallerytree_table_filtered_column8(), "$ #,##0.00")
                        .filter(COUNTRY, OR(equalsTo("United States"), equalsTo("Brazil")))
                        .title(AppConstants.INSTANCE.gallerytree_table_filtered_title())
                        .tablePageSize(10)
                        .tableOrderEnabled(true)
                        .tableOrderDefault(AMOUNT, DESCENDING)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_table_grouped(), createPlaceRequest(
                DisplayerSettingsFactory.newTableSettings()
                        .dataset(SALES_OPPS)
                        .group(COUNTRY)
                        .column(COUNTRY, AppConstants.INSTANCE.gallerytree_table_grouped_column1())
                        .column(COUNT, "#Opps").format(AppConstants.INSTANCE.gallerytree_table_grouped_column2(), "#,##0")
                        .column(AMOUNT, MIN).format(AppConstants.INSTANCE.gallerytree_table_grouped_column3(), "$ #,###")
                        .column(AMOUNT, MAX).format(AppConstants.INSTANCE.gallerytree_table_grouped_column4(), "$ #,###")
                        .column(AMOUNT, AVERAGE).format(AppConstants.INSTANCE.gallerytree_table_grouped_column5(), "$ #,###")
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.gallerytree_table_grouped_column6(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_table_grouped_title())
                        .tablePageSize(10)
                        .tableOrderEnabled(true)
                        .tableOrderDefault(COUNTRY, DESCENDING)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_table_default_dd(), createPlaceRequest(
                DisplayerSettingsFactory.newTableSettings()
                        .dataset(SALES_OPPS)
                        .column(COUNTRY, AppConstants.INSTANCE.gallerytree_table_default_dd_column1())
                        .column(CUSTOMER, AppConstants.INSTANCE.gallerytree_table_default_dd_column2())
                        .column(PRODUCT, AppConstants.INSTANCE.gallerytree_table_default_dd_column3())
                        .column(SALES_PERSON, AppConstants.INSTANCE.gallerytree_table_default_dd_column4())
                        .column(STATUS, AppConstants.INSTANCE.gallerytree_table_default_dd_column5())
                        .column(SOURCE, AppConstants.INSTANCE.gallerytree_table_default_dd_column6())
                        .column(CREATION_DATE, AppConstants.INSTANCE.gallerytree_table_default_dd_column7())
                        .column(EXPECTED_AMOUNT).format(AppConstants.INSTANCE.gallerytree_table_default_dd_column8(), "$ #,###")
                        .column(CLOSING_DATE).format(AppConstants.INSTANCE.gallerytree_table_default_dd_column9(), "MMM dd, yyyy")
                        .column(AMOUNT).format(AppConstants.INSTANCE.gallerytree_table_default_dd_column10(), "$ #,###")
                        .title(AppConstants.INSTANCE.gallerytree_table_default_dd_title())
                        .tablePageSize(10)
                        .tableOrderEnabled(true)
                        .tableOrderDefault(AMOUNT, DESCENDING)
                        .filterOn(true, true, true)
                        .renderer(DefaultRenderer.UUID)
                        .buildSettings()
        )));
    }

    private void initSelectorCategory() {
        GalleryTreeNodeList nodeList = new GalleryTreeNodeList(AppConstants.INSTANCE.gallerytree_selector());
        mainNodes.add(nodeList);

        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_selector_dropdown(), createPlaceRequest(
                DisplayerSettingsFactory.newSelectorSettings()
                        .dataset(SALES_OPPS)
                        .group(COUNTRY)
                        .column(COUNTRY, "Country")
                        .column(COUNT, "#Opps").format("#Opps", "#,###")
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.sales_bydate_selector_total(), "$ #,##0.00")
                        .sort(COUNTRY, ASCENDING)
                        .title(AppConstants.INSTANCE.gallerytree_selector_dropdown()).titleVisible(true)
                        .subtype(DisplayerSubType.SELECTOR_DROPDOWN)
                        .width(200)
                        .margins(20, 0, 20, 0)
                        .multiple(true)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));

        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_selector_labels(), createPlaceRequest(
                DisplayerSettingsFactory.newSelectorSettings()
                        .dataset(SALES_OPPS)
                        .group(COUNTRY)
                        .column(COUNTRY, "Country")
                        .column(COUNT, "#Opps").format("#Opps", "#,###")
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.sales_bydate_selector_total(), "$ #,##0.00")
                        .sort(COUNTRY, ASCENDING)
                        .title(AppConstants.INSTANCE.gallerytree_selector_labels()).titleVisible(true)
                        .subtype(DisplayerSubType.SELECTOR_LABELS)
                        .width(-1)
                        .margins(20, 0, 20, 0)
                        .multiple(true)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));

        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_selector_slider(), createPlaceRequest(
                DisplayerSettingsFactory.newSelectorSettings()
                        .dataset(SALES_OPPS)
                        .title(AppConstants.INSTANCE.gallerytree_selector_slider()).titleVisible(true)
                        .subtype(DisplayerSubType.SELECTOR_SLIDER)
                        .column(CREATION_DATE).format("Creation date", "dd MMM, yyyy")
                        .margins(20, 0, 20, 0)
                        .width(-1)
                        .filterOn(false, true, true)
                        .buildSettings()
        )));
    }

    private void initDashboardCategory() {
        GalleryTreeNodeList nodeList = new GalleryTreeNodeList(AppConstants.INSTANCE.gallerytree_db());
        mainNodes.add(nodeList);

        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_db_salesgoals(), createPlaceRequest("salesGoal")));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_db_salespipe(), createPlaceRequest("salesPipeline")));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_db_salespcountry(), createPlaceRequest("salesPerCountry")));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_db_salesreps(), createPlaceRequest("salesReports")));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_db_expreps(), createPlaceRequest("expenseReports")));
        nodeList.add(new GalleryPlaceRequest(AppConstants.INSTANCE.gallerytree_db_clustermetrics(), createPlaceRequest("clusterMetrics")));
    }
}
