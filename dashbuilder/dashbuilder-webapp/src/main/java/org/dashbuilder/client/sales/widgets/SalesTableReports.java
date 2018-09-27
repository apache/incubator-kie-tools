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
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.displayer.client.DisplayerLocator;

import static org.dashbuilder.shared.sales.SalesConstants.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;

/**
 * A composite widget that represents an entire dashboard sample composed using an UI binder template.
 * <p>The dashboard itself is composed by a set of Displayer instances.</p>
 */
@Dependent
public class SalesTableReports extends Composite implements GalleryWidget {

    interface SalesDashboardBinder extends UiBinder<Widget, SalesTableReports>{}
    private static final SalesDashboardBinder uiBinder = GWT.create(SalesDashboardBinder.class);

    @UiField(provided = true)
    Displayer tableByProduct;

    @UiField(provided = true)
    Displayer tableBySalesman;

    @UiField(provided = true)
    Displayer tableByCountry;

    @UiField(provided = true)
    Displayer tableByYear;

    @UiField(provided = true)
    Displayer tableAll;

    DisplayerCoordinator displayerCoordinator;
    DisplayerLocator displayerLocator;

    @Inject
    public SalesTableReports(DisplayerCoordinator displayerCoordinator, DisplayerLocator displayerLocator) {
        this.displayerCoordinator = displayerCoordinator;
        this.displayerLocator = displayerLocator;
    }

    @Override
    public String getTitle() {
        return AppConstants.INSTANCE.sales_tablereports_title();
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

        tableAll = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newTableSettings()
                .dataset(SALES_OPPS)
                .title(AppConstants.INSTANCE.sales_tablereports_all_title())
                .titleVisible(true)
                .tablePageSize(10)
                .tableOrderEnabled(true)
                .tableOrderDefault(AMOUNT, DESCENDING)
                .tableColumnPickerEnabled(false)
                .column(COUNTRY, AppConstants.INSTANCE.sales_tablereports_all_column1())
                .column(CUSTOMER, AppConstants.INSTANCE.sales_tablereports_all_column2())
                .column(PRODUCT, AppConstants.INSTANCE.sales_tablereports_all_column3())
                .column(SALES_PERSON, AppConstants.INSTANCE.sales_tablereports_all_column4())
                .column(STATUS, AppConstants.INSTANCE.sales_tablereports_all_column5())
                .column(CREATION_DATE, AppConstants.INSTANCE.sales_tablereports_all_column6())
                .column(EXPECTED_AMOUNT, AppConstants.INSTANCE.sales_tablereports_all_column7())
                .column(CLOSING_DATE, AppConstants.INSTANCE.sales_tablereports_all_column8())
                .column(AMOUNT, AppConstants.INSTANCE.sales_tablereports_all_column9())
                .filterOn(false, true, true)
                .buildSettings());

        tableByCountry = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newTableSettings()
                .dataset(SALES_OPPS)
                .group(COUNTRY)
                .column(COUNTRY, AppConstants.INSTANCE.sales_tablereports_bycountry_column1())
                .column(COUNT, AppConstants.INSTANCE.sales_tablereports_bycountry_column2())
                .column(AMOUNT, MIN, AppConstants.INSTANCE.sales_tablereports_bycountry_column3())
                .column(AMOUNT, MAX, AppConstants.INSTANCE.sales_tablereports_bycountry_column4())
                .column(AMOUNT, AVERAGE, AppConstants.INSTANCE.sales_tablereports_bycountry_column5())
                .column(AMOUNT, SUM, AppConstants.INSTANCE.sales_tablereports_bycountry_column6())
                .title(AppConstants.INSTANCE.sales_tablereports_bycountry_title())
                .titleVisible(false)
                .tablePageSize(10)
                .tableOrderEnabled(true)
                .tableOrderDefault("Total", DESCENDING)
                .tableColumnPickerEnabled(false)
                .filterOn(false, true, true)
                .buildSettings());

        tableByProduct = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newTableSettings()
                .dataset(SALES_OPPS)
                .group(PRODUCT)
                .column(PRODUCT, AppConstants.INSTANCE.sales_tablereports_byproduct_column1())
                .column(COUNT, AppConstants.INSTANCE.sales_tablereports_byproduct_column2())
                .column(AMOUNT, MIN, AppConstants.INSTANCE.sales_tablereports_byproduct_column3())
                .column(AMOUNT, MAX, AppConstants.INSTANCE.sales_tablereports_byproduct_column4())
                .column(AMOUNT, AVERAGE, AppConstants.INSTANCE.sales_tablereports_byproduct_column5())
                .column(AMOUNT, SUM, AppConstants.INSTANCE.sales_tablereports_byproduct_column6())
                .title(AppConstants.INSTANCE.sales_tablereports_byproduct_title())
                .titleVisible(false)
                .tablePageSize(10)
                .tableOrderEnabled(true)
                .tableOrderDefault("Total", DESCENDING)
                .tableColumnPickerEnabled(false)
                .filterOn(false, true, true)
                .buildSettings());

        tableBySalesman = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newTableSettings()
                .dataset(SALES_OPPS)
                .group(SALES_PERSON)
                .column(SALES_PERSON, AppConstants.INSTANCE.sales_tablereports_bysalesman_column1())
                .column(COUNT, AppConstants.INSTANCE.sales_tablereports_bysalesman_column2())
                .column(AMOUNT, MIN, AppConstants.INSTANCE.sales_tablereports_bysalesman_column3())
                .column(AMOUNT, MAX, AppConstants.INSTANCE.sales_tablereports_bysalesman_column4())
                .column(AMOUNT, AVERAGE, AppConstants.INSTANCE.sales_tablereports_bysalesman_column5())
                .column(AMOUNT, SUM, AppConstants.INSTANCE.sales_tablereports_bysalesman_column6())
                .title(AppConstants.INSTANCE.sales_tablereports_bysalesman_title())
                .titleVisible(false)
                .tablePageSize(10)
                .tableOrderEnabled(true)
                .tableOrderDefault("Total", DESCENDING)
                .tableColumnPickerEnabled(false)
                .filterOn(false, true, true)
                .buildSettings());

        tableByYear = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newTableSettings()
                .dataset(SALES_OPPS)
                .group(CREATION_DATE).dynamic(DateIntervalType.YEAR, true)
                .column(CREATION_DATE, AppConstants.INSTANCE.sales_tablereports_byyear_column1())
                .column(COUNT, AppConstants.INSTANCE.sales_tablereports_byyear_column2())
                .column(AMOUNT, MIN, AppConstants.INSTANCE.sales_tablereports_byyear_column3())
                .column(AMOUNT, MAX, AppConstants.INSTANCE.sales_tablereports_byyear_column4())
                .column(AMOUNT, AVERAGE, AppConstants.INSTANCE.sales_tablereports_byyear_column5())
                .column(AMOUNT, SUM, AppConstants.INSTANCE.sales_tablereports_byyear_column6())
                .title(AppConstants.INSTANCE.sales_tablereports_byyear_title())
                .titleVisible(false)
                .tablePageSize(10)
                .tableOrderEnabled(true)
                .tableOrderDefault("Total", DESCENDING)
                .tableColumnPickerEnabled(false)
                .filterOn(false, true, true)
                .buildSettings());

        // Make that charts interact among them
        displayerCoordinator.addDisplayer(tableByCountry);
        displayerCoordinator.addDisplayer(tableByProduct);
        displayerCoordinator.addDisplayer(tableBySalesman);
        displayerCoordinator.addDisplayer(tableByYear);
        displayerCoordinator.addDisplayer(tableAll);

        // Init the dashboard from the UI Binder template
        initWidget(uiBinder.createAndBindUi(this));

        // Draw the charts
        displayerCoordinator.drawAll();
    }
}
