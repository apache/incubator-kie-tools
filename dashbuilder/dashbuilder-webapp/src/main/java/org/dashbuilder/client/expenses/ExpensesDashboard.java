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
package org.dashbuilder.client.expenses;

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

import static org.dashbuilder.dataset.group.DateIntervalType.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.dashbuilder.client.expenses.ExpenseConstants.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;

/**
 * A composite widget that represents an entire dashboard sample based on a UI binder template.
 * The dashboard itself is composed by a set of Displayer instances.</p>
 * <p>The data set that feeds this dashboard is a CSV file stored into an specific server folder so
 * that is auto-deployed during server start up: <code>dashbuilder-webapp/src/main/webapp/datasets/expenseReports.csv</code></p>
 */
@Dependent
public class ExpensesDashboard extends Composite implements GalleryWidget {

    interface ExpensesDashboardBinder extends UiBinder<Widget, ExpensesDashboard>{}
    private static final ExpensesDashboardBinder uiBinder = GWT.create(ExpensesDashboardBinder.class);

    @UiField(provided = true)
    Displayer pieByOffice;

    @UiField(provided = true)
    Displayer barByDepartment;

    @UiField(provided = true)
    Displayer lineByDate;

    @UiField(provided = true)
    Displayer bubbleByEmployee;

    @UiField(provided = true)
    Displayer tableAll;

    DisplayerCoordinator displayerCoordinator;
    DisplayerLocator displayerLocator;

    @Inject
    public ExpensesDashboard(DisplayerCoordinator displayerCoordinator, DisplayerLocator displayerLocator) {
        this.displayerCoordinator = displayerCoordinator;
        this.displayerLocator = displayerLocator;
    }

    @Override
    public String getTitle() {
        return AppConstants.INSTANCE.expensesdb_title();
    }

    @Override
    public void onClose() {
        displayerCoordinator.closeAll();
    }

    @Override
    public boolean feedsFrom(String dataSetId) {
        return EXPENSES.equals(dataSetId);
    }

    @PostConstruct
    public void init() {

        // Create the chart definitions

        pieByOffice = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newPieChartSettings()
                        .dataset(EXPENSES)
                        .group(OFFICE)
                        .column(OFFICE)
                        .column(AMOUNT, SUM, "sum1")
                        .format(AppConstants.INSTANCE.expensesdb_pie_column1(), "$ #,##0.00")
                        .group(DEPARTMENT)
                        .column(DEPARTMENT)
                        .column(AMOUNT, SUM, "sum2")
                        .format(AppConstants.INSTANCE.expensesdb_pie_column2(), "$ #,##0.00")
                        .group(EMPLOYEE)
                        .column(EMPLOYEE)
                        .column(AMOUNT, SUM, "sum3")
                        .format(AppConstants.INSTANCE.expensesdb_pie_column3(), "$ #,##0.00")
                        .title(AppConstants.INSTANCE.expensesdb_pie_title())
                        .width(250).height(250)
                        .margins(10, 10, 10, 0)
                        .filterOn(true, true, true)
                        .buildSettings());

        barByDepartment = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newBarChartSettings()
                        .dataset(EXPENSES)
                        .group(DEPARTMENT)
                        .column(DEPARTMENT)
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.expensesdb_bar_column1(), "$ #,##0.00")
                        .title(AppConstants.INSTANCE.expensesdb_bar_title())
                        .width(350).height(250)
                        .margins(10, 50, 100, 20)
                        .filterOn(false, true, true)
                        .buildSettings());

        bubbleByEmployee = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newBubbleChartSettings()
                        .dataset(EXPENSES)
                        .group(EMPLOYEE)
                        .column(EMPLOYEE)
                        .column(AMOUNT, SUM).format(AppConstants.INSTANCE.expensesdb_bubble_column1(), "$ #,##0.00")
                        .column(AMOUNT, AVERAGE).format(AppConstants.INSTANCE.expensesdb_bubble_column2(), "$ #,##0.00")
                        .column(EMPLOYEE, AppConstants.INSTANCE.expensesdb_bubble_column3())
                        .column(COUNT, AppConstants.INSTANCE.expensesdb_bubble_column4())
                        .title(AppConstants.INSTANCE.expensesdb_bubble_title())
                        .titleVisible(false)
                        .width(600).height(280)
                        .margins(10, 50, 80, 0)
                        .filterOn(false, true, true)
                        .buildSettings());

        lineByDate = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newAreaChartSettings()
                        .dataset(EXPENSES)
                        .group(DATE).dynamic(8, DAY_OF_WEEK, true)
                        .column(DATE)
                        .column(AMOUNT, SUM)
                        .format(AppConstants.INSTANCE.expensesdb_line_column1(), "$ #,##0.00")
                        .title(AppConstants.INSTANCE.expensesdb_line_title())
                        .titleVisible(false)
                        .width(500).height(250)
                        .margins(10, 50, 50, 50)
                        .filterOn(true, true, true)
                        .buildSettings());

        tableAll = displayerLocator.lookupDisplayer(
                DisplayerSettingsFactory.newTableSettings()
                        .dataset(EXPENSES)
                        .title(AppConstants.INSTANCE.expensesdb_table_title())
                        .titleVisible(false)
                        .tablePageSize(10)
                        .tableOrderEnabled(true)
                        .tableOrderDefault(AMOUNT, DESCENDING)
                        .tableColumnPickerEnabled(false)
                        .column(OFFICE).format(AppConstants.INSTANCE.expensesdb_table_column1())
                        .column(DEPARTMENT).format(AppConstants.INSTANCE.expensesdb_table_column2())
                        .column(EMPLOYEE).format(AppConstants.INSTANCE.expensesdb_table_column3())
                        .column(AMOUNT).format(AppConstants.INSTANCE.expensesdb_table_column4(), "$ #,##0.00")
                        .column(DATE).format(AppConstants.INSTANCE.expensesdb_table_column5(), "MMM E dd, yyyy")
                        .filterOn(true, true, true)
                        .tableWidth(600)
                        .buildSettings());

        // Make that charts interact among them
        displayerCoordinator.addDisplayer(pieByOffice);
        displayerCoordinator.addDisplayer(barByDepartment);
        displayerCoordinator.addDisplayer(bubbleByEmployee);
        displayerCoordinator.addDisplayer(lineByDate);
        displayerCoordinator.addDisplayer(tableAll);

        // Init the dashboard from the UI Binder template
        initWidget(uiBinder.createAndBindUi(this));

        // Draw the charts
        displayerCoordinator.drawAll();
    }

    public void redrawAll() {
        displayerCoordinator.redrawAll();
    }
}
