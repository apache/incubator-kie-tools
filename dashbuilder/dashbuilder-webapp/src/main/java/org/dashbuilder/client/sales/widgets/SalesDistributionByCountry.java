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
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.dashbuilder.renderer.client.DefaultRenderer;

import static org.dashbuilder.shared.sales.SalesConstants.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;

/**
 * A composite widget that represents an entire dashboard sample composed using an UI binder template.
 * <p>The dashboard itself is composed by a set of Displayer instances.</p>
 */
@Dependent
public class SalesDistributionByCountry extends Composite implements GalleryWidget {

    interface SalesDashboardBinder extends UiBinder<Widget, SalesDistributionByCountry>{}
    private static final SalesDashboardBinder uiBinder = GWT.create(SalesDashboardBinder.class);

    @UiField(provided = true)
    Displayer bubbleByCountry;

    @UiField(provided = true)
    Displayer mapByCountry;

    @UiField(provided = true)
    Displayer tableAll;

    DisplayerCoordinator displayerCoordinator;
    DisplayerLocator displayerLocator;

    @Inject
    public SalesDistributionByCountry(DisplayerCoordinator displayerCoordinator, DisplayerLocator displayerLocator) {
        this.displayerCoordinator = displayerCoordinator;
        this.displayerLocator = displayerLocator;
    }

    @Override
    public String getTitle() {
        return AppConstants.INSTANCE.sales_bycountry_title();
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

        bubbleByCountry = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newBubbleChartSettings()
                .dataset(SALES_OPPS)
                .group(COUNTRY)
                .column(COUNTRY, "Country")
                .column(COUNT, "count").format(AppConstants.INSTANCE.sales_bycountry_bubble_column1(), "#,##0")
                .column(PROBABILITY, AVERAGE).format(AppConstants.INSTANCE.sales_bycountry_bubble_column2(), "#,##0")
                .column(COUNTRY, "Country")
                .column(EXPECTED_AMOUNT, SUM).expression("value/1000").format(AppConstants.INSTANCE.sales_bycountry_bubble_column3(), "$ #,##0 K")
                .title(AppConstants.INSTANCE.sales_bycountry_bubble_title())
                .width(450).height(300)
                .margins(20, 50, 50, 0)
                .filterOn(false, true, true)
                .buildSettings());

        mapByCountry = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newMapChartSettings()
                .dataset(SALES_OPPS)
                .group(COUNTRY)
                .column(COUNTRY, "Country")
                .column(COUNT, "Number of opportunities")
                .column(EXPECTED_AMOUNT, SUM).expression("value/1000").format(AppConstants.INSTANCE.sales_bycountry_map_column1(), "$ #,##0 K")
                .title(AppConstants.INSTANCE.sales_bycountry_map_title())
                .width(450).height(290)
                .margins(10, 10, 10, 10)
                .filterOn(false, true, true)
                .buildSettings());

        tableAll = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newTableSettings()
                .dataset(SALES_OPPS)
                .title(AppConstants.INSTANCE.sales_bycountry_table_title())
                .titleVisible(true)
                .tablePageSize(10)
                .tableOrderEnabled(true)
                .tableOrderDefault(AMOUNT, DESCENDING)
                .tableColumnPickerEnabled(false)
                .column(COUNTRY, AppConstants.INSTANCE.sales_bycountry_table_column1())
                .column(CUSTOMER, AppConstants.INSTANCE.sales_bycountry_table_column2())
                .column(PRODUCT, AppConstants.INSTANCE.sales_bycountry_table_column3())
                .column(SALES_PERSON, AppConstants.INSTANCE.sales_bycountry_table_column4())
                .column(STATUS, AppConstants.INSTANCE.sales_bycountry_table_column5())
                .column(CREATION_DATE, AppConstants.INSTANCE.sales_bycountry_table_column6())
                .column(EXPECTED_AMOUNT, AppConstants.INSTANCE.sales_bycountry_table_column7())
                .column(CLOSING_DATE, AppConstants.INSTANCE.sales_bycountry_table_column8())
                .column(AMOUNT).expression("value/1000").format(AppConstants.INSTANCE.sales_bycountry_table_column9(), "$ #,##0 K")
                .filterOn(true, true, true)
                .tableWidth(900)
                .renderer(DefaultRenderer.UUID)
                .buildSettings());

        // Make that charts interact among them
        displayerCoordinator.addDisplayer(bubbleByCountry);
        displayerCoordinator.addDisplayer(mapByCountry);
        displayerCoordinator.addDisplayer(tableAll);

        // Init the dashboard from the UI Binder template
        initWidget(uiBinder.createAndBindUi(this));

        // Draw the charts
        displayerCoordinator.drawAll();
    }
}
