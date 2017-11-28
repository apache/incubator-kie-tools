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
package org.dashbuilder.client.metrics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.gallery.GalleryWidget;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.dataset.DataSetBuilder;
import org.dashbuilder.displayer.BarChartSettingsBuilder;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsBuilder;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerCoordinator;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.dashbuilder.displayer.impl.BarChartSettingsBuilderImpl;
import org.dashbuilder.renderer.client.DefaultRenderer;
import org.uberfire.mvp.Command;

import static org.dashbuilder.backend.ClusterMetricsGenerator.*;
import static org.dashbuilder.dataset.group.DateIntervalType.*;
import static org.dashbuilder.dataset.sort.SortOrder.*;
import static org.dashbuilder.dataset.group.AggregateFunctionType.*;
import static org.dashbuilder.dataset.filter.FilterFactory.*;

/**
 * A composite widget that represents an entire dashboard sample composed using an UI binder template.
 * <p>The dashboard itself is composed by a set of Displayer instances.</p>
 */
@Dependent
public class ClusterMetricsDashboard extends Composite implements GalleryWidget {

    public static final String CPU = AppConstants.INSTANCE.metrics_cluster_metricselector_cpu();
    public static final String MEMORY = AppConstants.INSTANCE.metrics_cluster_metricselector_mem();
    public static final String DISK = AppConstants.INSTANCE.metrics_cluster_metricselector_disk();
    public static final String NETWORK = AppConstants.INSTANCE.metrics_cluster_metricselector_netw();

    interface Binder extends UiBinder<Widget, ClusterMetricsDashboard> {}
    private static final Binder uiBinder = GWT.create(Binder.class);

    @UiField
    Panel messagePanel;

    @UiField
    Panel mainPanel;

    @UiField
    Panel leftPanel;

    @UiField
    Panel rightPanel;

    @UiField
    ListBox metricSelector;

    @UiField
    ListBox chartTypeSelector;

    @UiField
    Panel metricChartPanel;

    @UiField(provided = true)
    Displayer metricsTable;

    List<ClusterMetric> metricDefList = new ArrayList<>();
    Map<String,List<Integer>> metricChartDef = new HashMap<>();
    DisplayerCoordinator displayerCoordinator;
    DisplayerLocator displayerLocator;
    Displayer currentMetricChart = null;

    Timer refreshTimer = new Timer() {
        public void run() {
            displayerCoordinator.redrawAll(
                    // On success
                    new Command() {
                        public void execute() {
                            refreshTimer.schedule(1000);
                        }
                    },
                    // On Failure
                    new Command() {
                        public void execute() {

                        }
                    }
            );
        }
    };

    @Inject
    public ClusterMetricsDashboard(DisplayerCoordinator displayerCoordinator, DisplayerLocator displayerLocator) {
        this.displayerCoordinator = displayerCoordinator;
        this.displayerLocator = displayerLocator;
    }

    @Override
    public String getTitle() {
        return AppConstants.INSTANCE.metrics_cluster_title();
    }

    @Override
    public void onClose() {
        displayerCoordinator.closeAll();
        refreshTimer.cancel();
    }

    @Override
    public boolean feedsFrom(String dataSetId) {
        return "clusterMetrics".equals(dataSetId);
    }

    @Override
    public void redrawAll() {
        displayerCoordinator.redrawAll();
    }

    class ClusterMetric {
        String column;
        String title;
        String format;
        String expression;
        String bgColor;
        boolean tableVisible;
        String units;

        public ClusterMetric(String column, String title, String format, String expression, String bgColor, boolean tableVisible, String units) {
            this.column = column;
            this.title = title;
            this.format = format;
            this.expression = expression;
            this.bgColor = bgColor;
            this.tableVisible = tableVisible;
            this.units = units;
        }
    }

    @PostConstruct
    public void init() {

        // Create the metric definitions
        metricDefList.add(new ClusterMetric(COLUMN_CPU0, AppConstants.INSTANCE.metrics_cluster_column_cpu(), "#,##0", null, "84ADF4", true, AppConstants.INSTANCE.metrics_cluster_column_cpu_y()));
        metricDefList.add(new ClusterMetric(COLUMN_DISK_FREE, AppConstants.INSTANCE.metrics_cluster_column_df(), "#,##0 Gb", null, "BCF3EE", false, AppConstants.INSTANCE.metrics_cluster_column_df_y()));
        metricDefList.add(new ClusterMetric(COLUMN_DISK_USED, AppConstants.INSTANCE.metrics_cluster_column_du(), "#,##0 Gb", null, "BCF3EE", true, AppConstants.INSTANCE.metrics_cluster_column_du_y()));
        metricDefList.add(new ClusterMetric(COLUMN_MEMORY_FREE, AppConstants.INSTANCE.metrics_cluster_column_memf(), "#,##0.00 Gb", null, "F9AEAF", false, AppConstants.INSTANCE.metrics_cluster_column_memf_y()));
        metricDefList.add(new ClusterMetric(COLUMN_MEMORY_USED, AppConstants.INSTANCE.metrics_cluster_column_memu(), "#,##0.00 Gb", null, "F9AEAF", true, AppConstants.INSTANCE.metrics_cluster_column_memu_y()));
        metricDefList.add(new ClusterMetric(COLUMN_PROCESSES_RUNNING, AppConstants.INSTANCE.metrics_cluster_column_procsrn(), "#,##0", null, "A4EEC8", false, AppConstants.INSTANCE.metrics_cluster_column_procsrn_y()));
        metricDefList.add(new ClusterMetric(COLUMN_PROCESSES_SLEEPING, AppConstants.INSTANCE.metrics_cluster_column_procssl(), "#,##0", null, "A4EEC8", true, AppConstants.INSTANCE.metrics_cluster_column_procssl_y()));
        metricDefList.add(new ClusterMetric(COLUMN_NETWORK_RX, AppConstants.INSTANCE.metrics_cluster_column_netrx(), "#,##0 Kb/s", null, "F5AC47", false, AppConstants.INSTANCE.metrics_cluster_column_netrx_y()));
        metricDefList.add(new ClusterMetric(COLUMN_NETWORK_TX, AppConstants.INSTANCE.metrics_cluster_column_nettx(), "#,##0 Kb/s", null, "F5AC47", true, AppConstants.INSTANCE.metrics_cluster_column_nettx_y()));

        metricChartDef.put(CPU, Arrays.asList(0));
        metricChartDef.put(DISK, Arrays.asList(1,2));
        metricChartDef.put(MEMORY, Arrays.asList(3,4));
        metricChartDef.put(NETWORK, Arrays.asList(7,8));

        // Init the metrics table
        DisplayerSettingsBuilder tableBuilder = DisplayerSettingsFactory.newTableSettings()
                .renderer(DefaultRenderer.UUID)
                .tableWidth(700)
                .tableOrderDefault(COLUMN_SERVER, ASCENDING)
                .filterOn(true, true, false)
                .dataset("clusterMetrics")
                .tableColumnPickerEnabled(false)
                .filter(COLUMN_TIMESTAMP, timeFrame("now -2second till now"))
                .group(COLUMN_SERVER)
                .column(COLUMN_SERVER).format("Server")
                .column(COLUMN_TIMESTAMP).format(AppConstants.INSTANCE.metrics_cluster_column_time(), "HH:mm:ss");

        for (ClusterMetric metric : metricDefList) {
            if (metric.tableVisible) {
                tableBuilder.column(metric.column, AVERAGE);
                tableBuilder.format(metric.title, metric.format);
                tableBuilder.expression(metric.column, metric.expression);
            }
        }

        metricsTable = displayerLocator.lookupDisplayer(tableBuilder.buildSettings());
        displayerCoordinator.addDisplayer(metricsTable);

        // Init the dashboard from the UI Binder template
        initWidget(uiBinder.createAndBindUi(this));
        mainPanel.getElement().setAttribute("cellpadding", "5");

        // Init the box metrics
        leftPanel.clear();
        for (Integer metricIdx : Arrays.asList(0, 1, 3, 5, 7)) {
            ClusterMetric metric = metricDefList.get(metricIdx);
            Displayer metricDisplayer = displayerLocator.lookupDisplayer(
                    DisplayerSettingsFactory.newMetricSettings()
                            .dataset("clusterMetrics")
                            .filter(COLUMN_TIMESTAMP, timeFrame("now -2second till now"))
                            .column(metric.column, AVERAGE)
                            .expression(metric.expression)
                            .format(metric.title, metric.format)
                            .title(metric.title)
                            .titleVisible(true)
                            .width(200).height(90)
                            .margins(10, 10, 10, 10)
                            .backgroundColor(metric.bgColor)
                            .filterOff(true)
                            .buildSettings());

            displayerCoordinator.addDisplayer(metricDisplayer);
            leftPanel.add(metricDisplayer);
        }

        // Init the metric selector
        metricSelector.clear();
        metricSelector.addItem(CPU);
        metricSelector.addItem(MEMORY);
        metricSelector.addItem(DISK);
        metricSelector.addItem(NETWORK);

        // Init the chart type selector
        chartTypeSelector.clear();
        chartTypeSelector.addItem(AppConstants.INSTANCE.metrics_cluster_chartselector_bar());
        chartTypeSelector.addItem(AppConstants.INSTANCE.metrics_cluster_chartselector_line());
        chartTypeSelector.addItem(AppConstants.INSTANCE.metrics_cluster_chartselector_area());

        // Init the metric chart
        currentMetricChart = createChartMetric(CPU);
        metricChartPanel.clear();
        metricChartPanel.add(currentMetricChart);
        displayerCoordinator.addDisplayer(currentMetricChart);

        // Draw the charts
        displayerCoordinator.drawAll(
                // On success
                new Command() {
                    public void execute() {
                        messagePanel.setVisible(false);
                        mainPanel.setVisible(true);
                        refreshTimer.schedule(1000);
                    }
                },
                // On Failure
                new Command() {
                    public void execute() {

                    }
                }
        );
    }

    protected Displayer createChartMetric(String group) {

        DisplayerType type = DisplayerType.BARCHART;
        switch (chartTypeSelector.getSelectedIndex()) {
            case 1: type = DisplayerType.LINECHART; break;
            case 2: type = DisplayerType.AREACHART; break;
        }

        BarChartSettingsBuilder<BarChartSettingsBuilderImpl> builder = DisplayerSettingsFactory.newBarChartSettings()
                .title(group)
                .titleVisible(false)
                .width(700).height(200)
                .margins(30, 5, 60, 10)
                .legendOff()
                .filterOff(true)
                .dataset("clusterMetrics");

        if (DisplayerType.BARCHART.equals(type)) {
            builder.filter(COLUMN_TIMESTAMP, timeFrame("begin[minute] till end[minute]"));
            builder.group(COLUMN_TIMESTAMP).fixed(SECOND, true);
            builder.column(COLUMN_TIMESTAMP).format(AppConstants.INSTANCE.metrics_cluster_column_time());
            builder.subType_StackedColumn();
        } else {
            builder.filter(COLUMN_TIMESTAMP, timeFrame("-60second till now"));
            builder.group(COLUMN_TIMESTAMP).dynamic(60, SECOND, true);
            builder.column(COLUMN_TIMESTAMP).format(AppConstants.INSTANCE.metrics_cluster_column_time());
        }

        List<Integer> metricIdxs = metricChartDef.get(group);
        for (Integer metricIdx : metricIdxs) {
            ClusterMetric metric = metricDefList.get(metricIdx);
                builder.column(metric.column, AVERAGE);
                builder.expression(metric.expression);
                builder.format(metric.title, metric.format);
                builder.yAxisTitle(metric.units);
        }

        DisplayerSettings settings = builder.buildSettings();
        settings.setType(type);
        return displayerLocator.lookupDisplayer(settings);
    }

    @UiHandler("chartTypeSelector")
    public void onChartTypeSelected(ChangeEvent changeEvent) {
        onMetricSelected(changeEvent);
    }

    @UiHandler("metricSelector")
    public void onMetricSelected(ChangeEvent changeEvent) {

        // Dispose the current metric chart
        currentMetricChart.close();
        displayerCoordinator.removeDisplayer(currentMetricChart);

        // Create the metric chart
        String title = metricSelector.getValue(metricSelector.getSelectedIndex());
        currentMetricChart = createChartMetric(title);
        currentMetricChart.draw();
        displayerCoordinator.addDisplayer(currentMetricChart);

        // Update the dashboard view
        metricChartPanel.clear();
        metricChartPanel.add(currentMetricChart);
    }
}
