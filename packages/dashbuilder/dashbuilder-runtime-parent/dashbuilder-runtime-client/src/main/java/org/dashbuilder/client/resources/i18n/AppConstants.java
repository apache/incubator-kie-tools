/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.resources.i18n;

public class AppConstants {

    private AppConstants() {
        // empty
    }

    public static String logoutMenuTooltip() {
        return "";
    }

    public static String errorLoadingDashboards() {
        return "";
    }

    public static String loadingDashboards() {
        return "Loading dashboard";
    }

    public static String uploadingDashboards() {
        return "Uploading Loading dashboards";
    }

    public static String runtimeScreenTitle() {
        return "";
    }

    public static String uploadDashboardsTitle() {
        return "";
    }

    public static String notFoundScreenTitle() {
        return "";
    }

    public static String notFoundDashboard(String perspectiveName) {
        return "Dashboard " + perspectiveName + " not found. Please review the dashboard name and try again.";
    }

    public static String routerScreenTitle() {
        return "";
    }

    public static String dashboardsListScreenTitle() {
        return "";
    }

    public static String dashboardListTooltip() {
        return "";
    }

    public static String listDashboardsScreenTitle() {
        return "";
    }

    public static String routerPerspective() {
        return "";
    }

    public static String dashboardAlreadyImport(String newModelId, String exitingModel) {
        return "";
    }

    public static String importSuccess(String fileName) {
        return "";
    }

    public static String disconnectedFromServer() {
        return "";
    }

    public static String couldNotConnectToServer() {
        return "Could not connect to server. This very likely means a network problem.";
    }

    public static String invalidBusResponseProbablySessionTimeout() {
        return "Invalid response received from the server. This very likely means that you have been logged out due to inactivity.";
    }

    public static String dashboardOpenTooltip() {
        return "";
    }

    public static String defaultErrorMessage() {
        return "";
    }

    public static String notAuthorized() {
        return "Not Authorized";
    }

    public static String notAuthorizedTitle() {
        return "";
    }

    public static String notAbleToLoadDashboard(String message) {
        return "";
    }

    public static String clientMode() {
        return "";
    }

    public static String emptyEditorMode() {
        return "No content to display. Start editing to see the result here.";
    }

    public static String emptyClientMode() {
        return "Dashboards were not imported. You can import a dashboard by creating a supported YAML/JSON file";
    }

    public static String emptyWithImportId(String modelId) {
        return "Not able to load <strong>" + modelId +
                "</strong>. You can import a dashboard by creating a supported YAML/JSON file";
    }

    public static String errorContentTitle() {
        return "Error loading content";
    }

    public static String samplesScreenTitle() {
        return "";
    }

    public static String emptyScreenTrySamples() {
        return "or you can try samples below.";
    }

}
