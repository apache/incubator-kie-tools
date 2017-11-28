/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.shared.dashboard.events;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * @deprecated Since version 0.7, dashboards are created from the Content Manager perspective. This class is
 * still needed in order to deal with old dashboards created from existing installations.
 */
@Portable
public class DashboardDeletedEvent {

    private String dashboardId;
    private String dashboardName;

    public DashboardDeletedEvent() {
    }

    public DashboardDeletedEvent(String dashboardId, String dashboardName) {
        this.dashboardId = dashboardId;
        this.dashboardName = dashboardName;
    }

    public String getDashboardId() {
        return dashboardId;
    }

    public String getDashboardName() {
        return dashboardName;
    }
}
