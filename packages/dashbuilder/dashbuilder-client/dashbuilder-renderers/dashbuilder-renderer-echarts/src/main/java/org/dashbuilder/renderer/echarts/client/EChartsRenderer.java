/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.AbstractRendererLibrary;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.renderer.echarts.client.exports.ResourcesInjector;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

import static org.dashbuilder.displayer.DisplayerSubType.AREA;
import static org.dashbuilder.displayer.DisplayerSubType.AREA_STACKED;
import static org.dashbuilder.displayer.DisplayerSubType.BAR;
import static org.dashbuilder.displayer.DisplayerSubType.BAR_STACKED;
import static org.dashbuilder.displayer.DisplayerSubType.COLUMN;
import static org.dashbuilder.displayer.DisplayerSubType.COLUMN_STACKED;
import static org.dashbuilder.displayer.DisplayerSubType.DONUT;
import static org.dashbuilder.displayer.DisplayerSubType.LINE;
import static org.dashbuilder.displayer.DisplayerSubType.PIE;
import static org.dashbuilder.displayer.DisplayerSubType.SMOOTH;
import static org.dashbuilder.displayer.DisplayerType.AREACHART;
import static org.dashbuilder.displayer.DisplayerType.BARCHART;
import static org.dashbuilder.displayer.DisplayerType.BUBBLECHART;
import static org.dashbuilder.displayer.DisplayerType.LINECHART;
import static org.dashbuilder.displayer.DisplayerType.METERCHART;
import static org.dashbuilder.displayer.DisplayerType.PIECHART;
import static org.dashbuilder.displayer.DisplayerType.SCATTERCHART;
import static org.dashbuilder.displayer.DisplayerType.TIMESERIES;

@ApplicationScoped
public class EChartsRenderer extends AbstractRendererLibrary {

    private static final DisplayerType DEFAULT_CHART = BARCHART;

    public static final String UUID = "echarts";

    private static final List<DisplayerType> SUPPORTED_TYPES = Arrays.asList(LINECHART,
            BARCHART,
            PIECHART,
            AREACHART,
            BUBBLECHART,
            METERCHART, 
            SCATTERCHART,
            TIMESERIES);

    @PostConstruct
    public void prepare() {
        ResourcesInjector.ensureEChartsInjected();
    }

    @Inject
    protected SyncBeanManager beanManager;

    @Override
    public String getUUID() {
        return UUID;
    }

    @Override
    public String getName() {
        return "ECharts";
    }

    @Override
    public List<DisplayerSubType> getSupportedSubtypes(DisplayerType type) {
        var displayerType = type == null ? DEFAULT_CHART : type;
        switch (displayerType) {
            case LINECHART:
                return Arrays.asList(LINE, SMOOTH);
            case BARCHART:
                return Arrays.asList(BAR, BAR_STACKED, COLUMN, COLUMN_STACKED);
            case PIECHART:
                return Arrays.asList(PIE, DONUT);
            case AREACHART:
                return Arrays.asList(AREA, AREA_STACKED);
            default:
                return Collections.emptyList();
        }
    }

    public Displayer lookupDisplayer(DisplayerSettings displayerSettings) {
        var displayerType = displayerSettings.getType() == null ? DEFAULT_CHART : displayerSettings.getType();
        switch (displayerType) {
            case LINECHART:
            case BARCHART:
            case AREACHART:
            case SCATTERCHART:
                return beanManager.lookupBean(EChartsXYChartDisplayer.class).newInstance();
            case TIMESERIES:
                return beanManager.lookupBean(EChartsTimeseriesDisplayer.class).newInstance();
            case BUBBLECHART:
                return beanManager.lookupBean(EChartsBubbleChartDisplayer.class).newInstance();
            case PIECHART:
                return beanManager.lookupBean(EChartsPieChartDisplayer.class).newInstance();
            case METERCHART:
                return beanManager.lookupBean(EChartsMeterChartDisplayer.class).newInstance();
            case MAP:
            default:
                throw new IllegalArgumentException("Type not supported by ECharts, use C3 renderer instead");
        }
    }

    @Override
    public List<DisplayerType> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    @Override
    public boolean isDefault(DisplayerType type) {
        return SUPPORTED_TYPES.contains(type);
    }
}
