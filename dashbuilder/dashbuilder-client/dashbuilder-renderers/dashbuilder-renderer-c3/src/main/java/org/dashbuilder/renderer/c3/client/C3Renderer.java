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
package org.dashbuilder.renderer.c3.client;

import static org.dashbuilder.displayer.DisplayerSubType.AREA;
import static org.dashbuilder.displayer.DisplayerSubType.AREA_STACKED;
import static org.dashbuilder.displayer.DisplayerSubType.BAR;
import static org.dashbuilder.displayer.DisplayerSubType.BAR_STACKED;
import static org.dashbuilder.displayer.DisplayerSubType.COLUMN;
import static org.dashbuilder.displayer.DisplayerSubType.COLUMN_STACKED;
import static org.dashbuilder.displayer.DisplayerSubType.DONUT;
import static org.dashbuilder.displayer.DisplayerSubType.LINE;
import static org.dashbuilder.displayer.DisplayerSubType.MAP_MARKERS;
import static org.dashbuilder.displayer.DisplayerSubType.MAP_REGIONS;
import static org.dashbuilder.displayer.DisplayerSubType.PIE;
import static org.dashbuilder.displayer.DisplayerSubType.PIE_3D;
import static org.dashbuilder.displayer.DisplayerSubType.SMOOTH;
import static org.dashbuilder.displayer.DisplayerType.AREACHART;
import static org.dashbuilder.displayer.DisplayerType.BARCHART;
import static org.dashbuilder.displayer.DisplayerType.BUBBLECHART;
import static org.dashbuilder.displayer.DisplayerType.LINECHART;
import static org.dashbuilder.displayer.DisplayerType.MAP;
import static org.dashbuilder.displayer.DisplayerType.METERCHART;
import static org.dashbuilder.displayer.DisplayerType.PIECHART;

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
import org.dashbuilder.renderer.c3.client.charts.area.C3AreaChartDisplayer;
import org.dashbuilder.renderer.c3.client.charts.bar.C3BarChartDisplayer;
import org.dashbuilder.renderer.c3.client.charts.bubble.C3BubbleChartDisplayer;
import org.dashbuilder.renderer.c3.client.charts.line.C3LineChartDisplayer;
import org.dashbuilder.renderer.c3.client.charts.map.D3MapDisplayer;
import org.dashbuilder.renderer.c3.client.charts.meter.C3MeterChartDisplayer;
import org.dashbuilder.renderer.c3.client.charts.pie.C3PieChartDisplayer;
import org.dashbuilder.renderer.c3.client.exports.ResourcesInjector;
import org.jboss.errai.ioc.client.container.SyncBeanManager;


@ApplicationScoped
public class C3Renderer extends AbstractRendererLibrary {

    public static final String UUID = "c3";
    
    @PostConstruct
    public void prepare() {
        ResourcesInjector.ensureC3Injected();
    }
    
    @Inject
    protected SyncBeanManager beanManager;

    private static List<DisplayerType> SUPPORTED_TYPES = Arrays.asList(LINECHART, 
                                                                       BARCHART, 
                                                                       PIECHART, 
                                                                       AREACHART, 
                                                                       BUBBLECHART,
                                                                       METERCHART,
                                                                       MAP);
    
    @Override
    public String getUUID() {
        return UUID;
    }

    @Override
    public String getName() {
        return "C3 Charts";
    }

    @Override
    public List<DisplayerSubType> getSupportedSubtypes(DisplayerType displayerType) {
        switch (displayerType) {
            case LINECHART:
                return Arrays.asList(LINE, SMOOTH);
            case BARCHART:
                return Arrays.asList(BAR, BAR_STACKED, COLUMN, COLUMN_STACKED);    
            case PIECHART:
                return Arrays.asList(PIE, DONUT, PIE_3D);
            case AREACHART:
                return Arrays.asList(AREA, AREA_STACKED);
            case MAP:
                return Arrays.asList(MAP_MARKERS, MAP_REGIONS);  
            default:
                return Collections.emptyList();
        }
    }

    public Displayer lookupDisplayer(DisplayerSettings displayerSettings) {
        DisplayerType displayerType = displayerSettings.getType();
        DisplayerSubType subtype = displayerSettings.getSubtype();
        C3AbstractDisplayer displayer;
        switch (displayerType) {
            case LINECHART:
                displayer = getLineChartForSubType(subtype);
                break;
            case BARCHART:
                displayer = createBarChartForSubType(subtype);
                break;
            case PIECHART:
                displayer = getPieChartForSubType(subtype);
                break;
            case AREACHART:
                displayer = getAreaChartForSubType(subtype);
                break;
            case BUBBLECHART:
                displayer = beanManager.lookupBean(C3BubbleChartDisplayer.class).newInstance();
                break;
            case METERCHART:
                displayer = beanManager.lookupBean(C3MeterChartDisplayer.class).newInstance();                
                break;
            case MAP:
                displayer = createMapChartForSubType(subtype);                
                break;                
            default:
                return null;
        }
        return displayer;
    }

    private D3MapDisplayer createMapChartForSubType(DisplayerSubType subtype) {
        ResourcesInjector.ensureD3GeoProjectionInjected();
        D3MapDisplayer displayer = beanManager.lookupBean(D3MapDisplayer.class).newInstance();
        if (subtype == MAP_MARKERS) {
                displayer =  displayer.markers();
        } else {
            displayer = displayer.regions();
        }
        return displayer;
    }

    private C3Displayer createBarChartForSubType(DisplayerSubType subtype) {
        C3Displayer displayer;
        switch (subtype) {
            case BAR:
                displayer = beanManager.lookupBean(C3BarChartDisplayer.class)
                                        .newInstance()
                                        .rotated();
                break;
            case BAR_STACKED:
                displayer = beanManager.lookupBean(C3BarChartDisplayer.class)
                                       .newInstance()
                                       .stackedAndRotated();
                break;
            case COLUMN:
                displayer = beanManager.lookupBean(C3BarChartDisplayer.class)
                                       .newInstance()
                                       .notRotated();
                break;
            case COLUMN_STACKED:
                displayer = beanManager.lookupBean(C3BarChartDisplayer.class)
                                       .newInstance()
                                       .stacked();
                break;                
            default:
                displayer = beanManager.lookupBean(C3BarChartDisplayer.class)
                                       .newInstance()
                                       .rotated();
                break;
        }
        return displayer;
    }

    private C3Displayer getLineChartForSubType(DisplayerSubType subtype) {
        C3LineChartDisplayer displayer =  beanManager.lookupBean(C3LineChartDisplayer.class)
                                                     .newInstance();
        if (subtype == SMOOTH) { 
            displayer = displayer.smooth();
        } 
        return displayer;
    }
    
    private C3Displayer getPieChartForSubType(DisplayerSubType subtype) {
        C3PieChartDisplayer displayer = beanManager.lookupBean(C3PieChartDisplayer.class)
                                                   .newInstance();
        if (subtype == DONUT) { 
            displayer = displayer.donut();
        } 
        return displayer;
    }
    
    private C3Displayer getAreaChartForSubType(DisplayerSubType subtype) {
        C3AreaChartDisplayer areaChartDisplayer = beanManager.lookupBean(C3AreaChartDisplayer.class)
                .newInstance();
        if (subtype == AREA_STACKED) {
            areaChartDisplayer = areaChartDisplayer.stacked();
        }
        return areaChartDisplayer;
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