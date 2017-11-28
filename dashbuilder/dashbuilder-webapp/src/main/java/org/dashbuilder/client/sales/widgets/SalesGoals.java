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
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerLocator;

import static org.dashbuilder.shared.sales.SalesConstants.*;
import static org.dashbuilder.dataset.group.DateIntervalType.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;

/**
 * A composite widget that represents an entire dashboard sample composed using an UI binder template.
 * <p>The dashboard itself is composed by a set of Displayer instances.</p>
 */
@Dependent
public class SalesGoals extends Composite implements GalleryWidget {

    interface SalesDashboardBinder extends UiBinder<Widget, SalesGoals>{}
    private static final SalesDashboardBinder uiBinder = GWT.create(SalesDashboardBinder.class);

    @UiField(provided = true)
    Displayer meterChartAmount;

    @UiField(provided = true)
    Displayer lineChartByDate;

    @UiField(provided = true)
    Displayer barChartByProduct;

    @UiField(provided = true)
    Displayer barChartByEmployee;

    @UiField(provided = true)
    Displayer bubbleByCountry;

    DisplayerCoordinator displayerCoordinator;
    DisplayerLocator displayerLocator;

    @Inject
    public SalesGoals(DisplayerCoordinator displayerCoordinator, DisplayerLocator displayerLocator) {
        this.displayerCoordinator = displayerCoordinator;
        this.displayerLocator = displayerLocator;

        // Create the chart definitions

        meterChartAmount = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newMeterChartSettings()
                .dataset(SALES_OPPS)
                .column(AMOUNT, SUM, AppConstants.INSTANCE.sales_goals_meter_column1())
                .expression("value/1000")
                .format(AppConstants.INSTANCE.sales_goals_meter_column1(), "$ #,### K")
                .title(AppConstants.INSTANCE.sales_goals_meter_title())
                .titleVisible(true)
                .width(200).height(200)
                .meter(0, 15000, 25000, 35000)
                .filterOn(false, true, true)
                .buildSettings());

        lineChartByDate = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newLineChartSettings()
                .dataset(SALES_OPPS)
                .group(CLOSING_DATE).dynamic(60, MONTH, true)
                .column(CLOSING_DATE).format(AppConstants.INSTANCE.sales_goals_line_column1())
                .column(AMOUNT, SUM).format(AppConstants.INSTANCE.sales_goals_line_column2(), "$ #,### K").expression("value/1000")
                .column(EXPECTED_AMOUNT, SUM).format(AppConstants.INSTANCE.sales_goals_line_column3(), "$ #,### K").expression("value/1000")
                .title(AppConstants.INSTANCE.sales_goals_line_title())
                .titleVisible(true)
                .width(800).height(200)
                .margins(10, 80, 80, 100)
                .xAxisAngle(30)
                .filterOn(false, true, true)
                .buildSettings());

        barChartByProduct = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newBarChartSettings()
                .subType_Column()
                .dataset(SALES_OPPS)
                .group(PRODUCT)
                .column(PRODUCT).format(AppConstants.INSTANCE.sales_goals_bar_byproduct_column1())
                .column(AMOUNT, SUM).format(AppConstants.INSTANCE.sales_goals_bar_byproduct_column2(), "$ #,### K").expression("value/1000")
                .column(EXPECTED_AMOUNT, SUM).format(AppConstants.INSTANCE.sales_goals_bar_byproduct_column3(), "$ #,### K").expression("value/1000")
                .title(AppConstants.INSTANCE.sales_goals_bar_byproduct_title())
                .titleVisible(true)
                .width(400).height(150)
                .margins(10, 80, 80, 10)
                .xAxisAngle(30)
                .filterOn(false, true, true)
                .buildSettings());

        barChartByEmployee = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newBarChartSettings()
                .subType_Column()
                .dataset(SALES_OPPS)
                .group(SALES_PERSON)
                .column(SALES_PERSON).format(AppConstants.INSTANCE.sales_goals_bar_byempl_column1())
                .column(AMOUNT, SUM).format(AppConstants.INSTANCE.sales_goals_bar_byempl_column2(), "$ #,### K").expression("value/1000")
                .sort(AMOUNT, DESCENDING)
                .title(AppConstants.INSTANCE.sales_goals_bar_byempl_title())
                .titleVisible(true)
                .width(400).height(150)
                .margins(10, 80, 80, 10)
                .xAxisAngle(30)
                .filterOn(false, true, true)
                .buildSettings());

        bubbleByCountry = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newBubbleChartSettings()
                .dataset(SALES_OPPS)
                .group(COUNTRY)
                .column(COUNTRY, "Country")
                .column(COUNT, "#opps").format(AppConstants.INSTANCE.sales_goals_bubble_column1(), "#,###")
                .column(PROBABILITY, AVERAGE).format(AppConstants.INSTANCE.sales_goals_bubble_column2(), "#,###")
                .column(COUNTRY, "Country")
                .column(EXPECTED_AMOUNT, SUM).expression("value/1000").format(AppConstants.INSTANCE.sales_goals_bubble_column3(), "$ #,##0.00 K")
                .title(AppConstants.INSTANCE.sales_goals_bubble_title())
                .width(550).height(250)
                .margins(10, 30, 50, 0)
                .filterOn(false, true, true)
                .buildSettings());

        // Make the charts interact among them
        displayerCoordinator.addDisplayer(meterChartAmount);
        displayerCoordinator.addDisplayer(lineChartByDate);
        displayerCoordinator.addDisplayer(barChartByProduct);
        displayerCoordinator.addDisplayer(barChartByEmployee);
        displayerCoordinator.addDisplayer(bubbleByCountry);

        // Init the dashboard from the UI Binder template
        initWidget(uiBinder.createAndBindUi(this));

        // Draw the charts
        displayerCoordinator.drawAll();
    }

    @Override
    public String getTitle() {
        return AppConstants.INSTANCE.sales_goals_title();
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
}
