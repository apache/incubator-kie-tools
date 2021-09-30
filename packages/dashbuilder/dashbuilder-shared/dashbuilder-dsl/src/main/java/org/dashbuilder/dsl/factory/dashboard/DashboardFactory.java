/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dsl.factory.dashboard;

import java.nio.file.Path;
import java.util.List;

import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dsl.model.Dashboard;
import org.dashbuilder.dsl.model.Navigation;
import org.dashbuilder.dsl.model.Page;

public class DashboardFactory {

    private DashboardFactory() {
        // empty
    }

    public static DashboardBuilder dashboardBuilder(List<Page> pages) {
        return DashboardBuilder.newBuilder(pages);
    }

    public static Dashboard dashboard(List<Page> pages) {
        return DashboardBuilder.newBuilder(pages).build();
    }

    public static Dashboard dashboard(List<Page> pages, Path componentsPath) {
        return DashboardBuilder.newBuilder(pages).componentsPath(componentsPath).build();
    }

    public static Dashboard dashboard(List<Page> pages, Navigation navigation) {
        return DashboardBuilder.newBuilder(pages).navigation(navigation).build();
    }

    public static Dashboard dashboard(List<Page> pages, List<DataSetDef> dataSets) {
        return DashboardBuilder.newBuilder(pages)
                               .dataSets(dataSets)
                               .build();
    }

    public static Dashboard dashboard(List<Page> pages, List<DataSetDef> dataSets, Path componentsPath) {
        return DashboardBuilder.newBuilder(pages)
                               .dataSets(dataSets)
                               .componentsPath(componentsPath)
                               .build();
    }

    public static Dashboard dashboard(List<Page> pages, List<DataSetDef> dataSets, Navigation navigation) {
        return DashboardBuilder.newBuilder(pages)
                               .dataSets(dataSets)
                               .navigation(navigation)
                               .build();
    }

    public static Dashboard dashboard(List<Page> pages, List<DataSetDef> dataSets, Navigation navigation, Path componentsPath) {
        return DashboardBuilder.newBuilder(pages)
                               .dataSets(dataSets)
                               .navigation(navigation)
                               .componentsPath(componentsPath)
                               .build();
    }

}
