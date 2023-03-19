/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.c3.client.charts.map;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.dashbuilder.displayer.MapColorScheme;
import org.dashbuilder.renderer.c3.client.charts.map.geojson.CountriesGeoJsonService;

public class D3MapConf {

    private String title;
    private Map<String, Double> data;
    private boolean markers;
    private boolean regions;
    private String backgroundColor = "#DDDDFF";
    private CountriesGeoJsonService countriesGeoJsonService;
    private Function<Double, String> formatter;
    private Consumer<String> pathClickHandler;
    private MapColorScheme colorScheme;
    private boolean showLegend;

    public D3MapConf(String title,
                     Map<String, Double> data,
                     boolean markers,
                     boolean regions,
                     String backgroundColor,
                     CountriesGeoJsonService countriesGeoJsonService,
                     Function<Double, String> formatter,
                     Consumer<String> pathClickHandler,
                     MapColorScheme colorScheme,
                     boolean showLegend) {
        this.title = title;
        this.data = data;
        this.markers = markers;
        this.regions = regions;
        this.backgroundColor = backgroundColor;
        this.formatter = formatter;
        this.countriesGeoJsonService = countriesGeoJsonService;
        this.pathClickHandler = pathClickHandler;
        this.colorScheme = colorScheme;
        this.showLegend = showLegend;
    }

    public static D3MapConf of(String title,
                               Map<String, Double> data,
                               boolean markers,
                               boolean regions,
                               String backgroundColor,
                               CountriesGeoJsonService countriesGeoJsonService,
                               Function<Double, String> formatter,
                               Consumer<String> pathClickHandler,
                               MapColorScheme colorScheme,
                               boolean showLegend) {
        return new D3MapConf(title,
                             data,
                             markers,
                             regions,
                             backgroundColor,
                             countriesGeoJsonService,
                             formatter,
                             pathClickHandler,
                             colorScheme,
                             showLegend);
    }

    public boolean isMarkers() {
        return markers;
    }

    public boolean isRegions() {
        return regions;
    }

    public Map<String, Double> getData() {
        return data;
    }

    public String getTitle() {
        return title;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public Function<Double, String> getFormatter() {
        return formatter;
    }

    public CountriesGeoJsonService getCountriesGeoJsonService() {
        return countriesGeoJsonService;
    }

    public Consumer<String> getPathClickHandler() {
        return pathClickHandler;
    }

    public MapColorScheme getColorScheme() {
        return colorScheme;
    }

    public boolean isShowLegend() {
        return showLegend;
    }
}
