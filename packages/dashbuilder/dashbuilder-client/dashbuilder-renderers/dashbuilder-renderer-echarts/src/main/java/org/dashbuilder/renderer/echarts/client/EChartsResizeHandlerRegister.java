/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.renderer.echarts.client;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.Window;
import org.dashbuilder.renderer.echarts.client.js.ECharts.Chart;

@ApplicationScoped
public class EChartsResizeHandlerRegister {

    List<Chart> charts;

    @PostConstruct
    public void setup() {
        charts = new ArrayList<>();
        Window.addResizeHandler(v -> charts.forEach(Chart::resize));
    }

    public void add(Chart chart) {
        charts.add(chart);
    }

    public void clear() {
        charts.clear();
    }

    public void remove(Chart chart) {
        charts.remove(chart);
    }

}
