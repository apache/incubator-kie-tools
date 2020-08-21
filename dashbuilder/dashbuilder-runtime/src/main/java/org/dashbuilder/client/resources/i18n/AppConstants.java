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

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface AppConstants extends Messages {

    public static final AppConstants INSTANCE = GWT.create(AppConstants.class);

    String logoutMenuTooltip();

    String errorLoadingDashboards();

    String loadingDashboards();

    String errorUploadingDashboards();

    String uploadingDashboards();

    String runtimeScreenTitle();

    String uploadDashboardsTitle();

    String notFoundScreenTitle();

    String notFoundDashboard(String perspectiveName);

    String routerScreenTitle();

    String dashboardsListScreenTitle();

    String dashboardListTooltip();

    String listDashboardsScreenTitle();

    String routerPerspective();
    
    String dashboardAlreadyImport(String newModelId, String exitingModel);

    String importSuccess(String fileName);

    String disconnectedFromServer();

    String couldNotConnectToServer();

    String sessionTimeout();

    String invalidBusResponseProbablySessionTimeout();

}