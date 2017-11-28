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

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.client.expenses.ExpensesDashboard;
import org.dashbuilder.client.metrics.ClusterMetricsDashboard;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.client.sales.widgets.SalesDistributionByCountry;
import org.dashbuilder.client.sales.widgets.SalesExpectedByDate;
import org.dashbuilder.client.sales.widgets.SalesGoals;
import org.dashbuilder.client.sales.widgets.SalesTableReports;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.date.TimeAmount;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.events.DataSetModifiedEvent;
import org.dashbuilder.dataset.events.DataSetPushOkEvent;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.INFO;

@WorkbenchScreen(identifier = "GalleryWidgetScreen")
public class GalleryWidgetPresenter {

    private GalleryWidget widget;
    private SalesGoals salesGoals;
    private SalesExpectedByDate salesExpectedByDate;
    private SalesDistributionByCountry salesDistributionByCountry;
    private SalesTableReports salesTableReports;
    private ExpensesDashboard expensesDashboard;
    private ClusterMetricsDashboard clusterMetricsDashboard;
    private Event<NotificationEvent> workbenchNotification;

    @Inject
    public GalleryWidgetPresenter(SalesGoals salesGoals,
                                  SalesExpectedByDate salesExpectedByDate,
                                  SalesDistributionByCountry salesDistributionByCountry,
                                  SalesTableReports salesTableReports,
                                  ExpensesDashboard expensesDashboard,
                                  ClusterMetricsDashboard clusterMetricsDashboard,
                                  Event<NotificationEvent> workbenchNotification) {
        this.salesGoals = salesGoals;
        this.salesExpectedByDate = salesExpectedByDate;
        this.salesDistributionByCountry = salesDistributionByCountry;
        this.salesTableReports = salesTableReports;
        this.expensesDashboard = expensesDashboard;
        this.clusterMetricsDashboard = clusterMetricsDashboard;
        this.workbenchNotification = workbenchNotification;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return widget.getTitle();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return widget;
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        String widgetId = placeRequest.getParameter("widgetId",
                                                    "");
        widget = getWidget(widgetId);
    }

    @OnClose
    public void onClose() {
        widget.onClose();
    }

    private GalleryWidget getWidget(String widgetId) {
        if ("salesGoal".equals(widgetId)) {
            return salesGoals;
        }
        if ("salesPipeline".equals(widgetId)) {
            return salesExpectedByDate;
        }
        if ("salesPerCountry".equals(widgetId)) {
            return salesDistributionByCountry;
        }
        if ("salesReports".equals(widgetId)) {
            return salesTableReports;
        }
        if ("expenseReports".equals(widgetId)) {
            return expensesDashboard;
        }
        if ("clusterMetrics".equals(widgetId)) {
            return clusterMetricsDashboard;
        }

        throw new IllegalArgumentException(AppConstants.INSTANCE.gallerywidget_unknown() + widgetId);
    }

    // Catch some data set related events and display workbench notifications only and only if:
    // - The data set refresh is enabled and
    // - It's refresh rate is greater than 60 seconds (avoid tons of notifications in "real-time" scenarios)

    private void onDataSetModifiedEvent(@Observes DataSetModifiedEvent event) {
        checkNotNull("event",
                     event);

        DataSetDef def = event.getDataSetDef();
        String targetUUID = event.getDataSetDef().getUUID();
        TimeAmount timeFrame = def.getRefreshTimeAmount();
        boolean noRealTime = timeFrame == null || timeFrame.toMillis() > 60000;

        if ((!def.isRefreshAlways() || noRealTime) && widget != null && widget.feedsFrom(targetUUID)) {
            workbenchNotification.fire(new NotificationEvent(AppConstants.INSTANCE.gallerywidget_dataset_modif(),
                                                             INFO));
            widget.redrawAll();
        }
    }

    private void onDataSetPushOkEvent(@Observes DataSetPushOkEvent event) {
        checkNotNull("event",
                     event);
        checkNotNull("event",
                     event.getDataSetMetadata());

        DataSetMetadata metadata = event.getDataSetMetadata();
        DataSetDef def = metadata.getDefinition();
        TimeAmount timeFrame = def.getRefreshTimeAmount();
        if (timeFrame == null || timeFrame.toMillis() > 60000) {
            int estimazedSizeKbs = event.getDataSetMetadata().getEstimatedSize() / 1000;
            workbenchNotification.fire(new NotificationEvent(
                    AppConstants.INSTANCE.gallerywidget_dataset_loaded(def.getProvider().toString(),
                                                                       estimazedSizeKbs),
                    INFO));
        }
    }
}
