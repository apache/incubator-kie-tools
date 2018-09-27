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
package org.dashbuilder.client.sales.widgets;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.gallery.GalleryWidget;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.dashbuilder.renderer.client.DefaultRenderer;

import static org.dashbuilder.dataset.group.DateIntervalType.*;
import static org.dashbuilder.dataset.date.DayOfWeek.*;
import static org.dashbuilder.shared.sales.SalesConstants.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;

/**
 * A composite widget that represents an entire dashboard sample composed using an UI binder template.
 * <p>The dashboard itself is composed by a set of Displayer instances.</p>
 */
@Dependent
public class SalesExpectedByDate extends Composite implements GalleryWidget {

    interface SalesDashboardBinder extends UiBinder<Widget, SalesExpectedByDate>{}
    private static final SalesDashboardBinder uiBinder = GWT.create(SalesDashboardBinder.class);

    @UiField(provided = true)
    Displayer areaChartByDate;

    @UiField(provided = true)
    Displayer pieChartYears;

    @UiField(provided = true)
    Displayer pieChartQuarters;

    @UiField(provided = true)
    Displayer barChartDayOfWeek;

    @UiField(provided = true)
    Displayer pieChartByPipeline;

    @UiField(provided = true)
    Displayer tableAll;

    @UiField(provided = true)
    Displayer countrySelector;

    @UiField(provided = true)
    Displayer customerSelector;

    @UiField(provided = true)
    Displayer salesmanSelector;

    DisplayerCoordinator displayerCoordinator;
    DisplayerLocator displayerLocator;

    @Inject
    public SalesExpectedByDate(DisplayerCoordinator displayerCoordinator, DisplayerLocator displayerLocator) {
        this.displayerCoordinator = displayerCoordinator;
        this.displayerLocator = displayerLocator;
    }

    @Override
    public String getTitle() {
        return AppConstants.INSTANCE.sales_bydate_title();
    }

    @Override
    public void onClose() {
        displayerCoordinator.closeAll();
    }

    @Override
    public boolean feedsFrom(String dataSetId) {
        return SALES_OPPS.equals(dataSetId);
    }

    @Override
    public void redrawAll() {
        displayerCoordinator.redrawAll();
    }

    @PostConstruct
    public void init() {

        // Create the chart definitions

        areaChartByDate = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newAreaChartSettings()
                .dataset(SALES_OPPS)
                .group(CREATION_DATE).dynamic(30, QUARTER, true)
                .column(CREATION_DATE, "Creation date")
                .column(EXPECTED_AMOUNT, SUM).format(AppConstants.INSTANCE.sales_bydate_area_column1(), "$ #,###")
                .title(AppConstants.INSTANCE.sales_bydate_area_title())
                .titleVisible(true)
                .width(700).height(200)
                .margins(10, 100, 80, 50)
                .xAxisAngle(45)
                .filterOn(true, true, true)
                .buildSettings());

        pieChartYears = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newPieChartSettings()
                .dataset(SALES_OPPS)
                .group(CREATION_DATE).dynamic(YEAR, true)
                .column(CREATION_DATE, "Year")
                .column(COUNT, "#occs").format(AppConstants.INSTANCE.sales_bydate_pie_years_column1(), "#,###")
                .title(AppConstants.INSTANCE.sales_bydate_pie_years_title())
                .titleVisible(true)
                .width(200).height(150)
                .margins(0, 0, 0, 0)
                .filterOn(false, true, false)
                .buildSettings());

        pieChartQuarters = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newPieChartSettings()
                .dataset(SALES_OPPS)
                .group(CREATION_DATE).fixed(QUARTER, true)
                .column(CREATION_DATE, "Creation date")
                .column(COUNT, "#occs").format(AppConstants.INSTANCE.sales_bydate_pie_quarters_column1(), "#,###")
                .title(AppConstants.INSTANCE.sales_bydate_pie_quarters_title())
                .titleVisible(true)
                .width(200).height(150)
                .margins(0, 0, 0, 0)
                .filterOn(false, true, false)
                .buildSettings());

        barChartDayOfWeek = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newBarChartSettings()
                .subType_Bar()
                .dataset(SALES_OPPS)
                .group(CREATION_DATE).fixed(DAY_OF_WEEK, true).firstDay(SUNDAY)
                .column(CREATION_DATE, "Creation date")
                .column(COUNT, "#occs").format(AppConstants.INSTANCE.sales_bydate_bar_weekday_column1(), "#,###")
                .title(AppConstants.INSTANCE.sales_bydate_bar_weekday_title())
                .titleVisible(true)
                .width(200).height(150)
                .margins(0, 20, 80, 10)
                .filterOn(false, true, true)
                .buildSettings());


        pieChartByPipeline = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newPieChartSettings()
                        .dataset(SALES_OPPS)
                        .group(PIPELINE)
                        .column(PIPELINE, "Pipeline")
                        .column(COUNT, "#opps").format(AppConstants.INSTANCE.sales_bydate_pie_pipe_column1(), "#,###")
                        .title(AppConstants.INSTANCE.sales_bydate_pie_pipe_title())
                        .titleVisible(true)
                        .width(200).height(150)
                        .margins(0, 0, 0, 0)
                        .filterOn(false, true, true)
                        .buildSettings());

        tableAll = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newTableSettings()
                        .dataset(SALES_OPPS)
                        .title(AppConstants.INSTANCE.sales_bydate_title())
                        .titleVisible(false)
                        .tablePageSize(10)
                        .tableWidth(800)
                        .tableOrderEnabled(true)
                        .tableOrderDefault(AMOUNT, DESCENDING)
                        .tableColumnPickerEnabled(false)
                        .renderer(DefaultRenderer.UUID)
                        .column(COUNTRY, AppConstants.INSTANCE.sales_bydate_table_column1())
                        .column(CUSTOMER, AppConstants.INSTANCE.sales_bydate_table_column2())
                        .column(PRODUCT, AppConstants.INSTANCE.sales_bydate_table_column3())
                        .column(SALES_PERSON, AppConstants.INSTANCE.sales_bydate_table_column4())
                        .column(STATUS, AppConstants.INSTANCE.sales_bydate_table_column5())
                        .column(AMOUNT).format(AppConstants.INSTANCE.sales_bydate_table_column6(), "$ #,###")
                        .column(EXPECTED_AMOUNT).format(AppConstants.INSTANCE.sales_bydate_table_column7(), "$ #,###")
                        .column(CREATION_DATE).format(AppConstants.INSTANCE.sales_bydate_table_column8(), "MMM dd, yyyy")
                        .column(CLOSING_DATE).format(AppConstants.INSTANCE.sales_bydate_table_column9(), "MMM dd, yyyy")
                        .filterOn(false, true, true)
                        .buildSettings());

        // Create the selectors

        countrySelector = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newSelectorSettings()
                        .dataset(SALES_OPPS)
                        .group(COUNTRY)
                        .column(COUNTRY, "Country")
                        .column(COUNT, "#Opps").format("#Opps", "#,###")
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.sales_bydate_selector_total(), "$ #,##0.00")
                        .sort(COUNTRY, ASCENDING)
                        .subtype(DisplayerSubType.SELECTOR_DROPDOWN)
                        .width(150)
                        .multiple(true)
                        .filterOn(false, true, true)
                        .buildSettings());

        salesmanSelector = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newSelectorSettings()
                        .dataset(SALES_OPPS)
                        .group(SALES_PERSON)
                        .column(SALES_PERSON, "Employee")
                        .column(COUNT, "#Opps").format("#Opps", "#,###")
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.sales_bydate_selector_total(), "$ #,##0.00")
                        .sort(SALES_PERSON, ASCENDING)
                        .subtype(DisplayerSubType.SELECTOR_DROPDOWN)
                        .width(150)
                        .multiple(true)
                        .filterOn(false, true, true)
                        .buildSettings());

        customerSelector = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newSelectorSettings()
                        .dataset(SALES_OPPS)
                        .group(CUSTOMER)
                        .column(CUSTOMER, "Customer")
                        .column(COUNT, "#Opps").format("#Opps", "#,###")
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.sales_bydate_selector_total(), "$ #,##0.00")
                        .sort(CUSTOMER, ASCENDING)
                        .subtype(DisplayerSubType.SELECTOR_DROPDOWN)
                        .width(150)
                        .multiple(true)
                            .filterOn(false, true, true)
                        .buildSettings());

        // Make the displayers interact among them
        displayerCoordinator.addDisplayer(areaChartByDate);
        displayerCoordinator.addDisplayer(pieChartYears);
        displayerCoordinator.addDisplayer(pieChartQuarters);
        displayerCoordinator.addDisplayer(barChartDayOfWeek);
        displayerCoordinator.addDisplayer(pieChartByPipeline);
        displayerCoordinator.addDisplayer(tableAll);
        displayerCoordinator.addDisplayer(countrySelector);
        displayerCoordinator.addDisplayer(salesmanSelector);
        displayerCoordinator.addDisplayer(customerSelector);

        // Init the dashboard from the UI Binder template
        initWidget(uiBinder.createAndBindUi(this));

        // Draw the charts
        displayerCoordinator.drawAll();
    }
}
