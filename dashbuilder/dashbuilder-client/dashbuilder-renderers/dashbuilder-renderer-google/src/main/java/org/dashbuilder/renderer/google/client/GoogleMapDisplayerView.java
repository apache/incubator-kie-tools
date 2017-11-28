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
package org.dashbuilder.renderer.google.client;

import com.googlecode.gwt.charts.client.geochart.GeoChart;
import com.googlecode.gwt.charts.client.geochart.GeoChartOptions;
import com.googlecode.gwt.charts.client.options.DisplayMode;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.renderer.google.client.resources.i18n.GoogleDisplayerConstants;

public class GoogleMapDisplayerView
        extends GoogleCategoriesDisplayerView<GoogleMapDisplayer>
        implements GoogleMapDisplayer.View {

    protected GeoChart map = null;

    @Override
    public String getGroupsTitle() {
        return GoogleDisplayerConstants.INSTANCE.common_Locations();
    }

    @Override
    public String getColumnsTitle() {
        return GoogleDisplayerConstants.INSTANCE.common_Series();
    }

    @Override
    public void setFilterEnabled(boolean enabled) {
        if (enabled) {
            checkMapCreated();
            map.addSelectHandler(createSelectHandler(map));
        }
    }

    @Override
    public void createChart() {
        map  = new GeoChart();
    }

    @Override
    public void drawChart() {
        checkMapCreated();
        map.draw(getDataTable(), createMapOptions());
        super.showDisplayer(map);
    }

    protected void checkMapCreated() {
        if (map == null) {
            throw new RuntimeException("Map not created. Call to view.createChart() first");
        }
    }

    protected GeoChartOptions createMapOptions() {
        GeoChartOptions options = GeoChartOptions.create();
        options.setWidth(width);
        options.setHeight(height);
        options.setDisplayMode(DisplayerSubType.MAP_REGIONS.equals(subType) ? DisplayMode.REGIONS : DisplayMode.MARKERS);
        return options;
    }
}
