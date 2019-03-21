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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.dashbuilder.common.client.widgets.FilterLabelSet;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.dashbuilder.renderer.c3.client.charts.area.C3AreaChartDisplayer;
import org.dashbuilder.renderer.c3.client.charts.line.C3LineChartDisplayer;
import org.dashbuilder.renderer.c3.client.charts.map.D3MapDisplayer;
import org.dashbuilder.renderer.c3.client.charts.map.geojson.CountriesGeoJsonService;
import org.dashbuilder.renderer.c3.client.charts.meter.C3MeterChartDisplayer;
import org.dashbuilder.renderer.c3.client.jsbinding.C3AxisInfo;
import org.dashbuilder.renderer.c3.client.jsbinding.C3AxisX;
import org.dashbuilder.renderer.c3.client.jsbinding.C3AxisY;
import org.dashbuilder.renderer.c3.client.jsbinding.C3ChartConf;
import org.dashbuilder.renderer.c3.client.jsbinding.C3ChartData;
import org.dashbuilder.renderer.c3.client.jsbinding.C3JsTypesFactory;
import org.dashbuilder.renderer.c3.client.jsbinding.C3Tick;
import org.mockito.Mock;

public class C3BaseTest extends AbstractDisplayerTest {

    @Mock
    C3JsTypesFactory c3Factory;

    @Mock
    protected C3AxisInfo c3AxisInfo;
    @Mock
    protected C3AxisY c3AxisY;
    @Mock
    protected C3AxisX c3AxisX;
    @Mock
    protected  C3Tick c3Tick;
    @Mock
    protected C3ChartConf c3Conf;
    @Mock
    protected C3ChartData c3ChartData;
    @Mock
    FilterLabelSet filterLabelSet;
    @Mock
    C3LineChartDisplayer.View c3LineChartview;
    @Mock
    C3AreaChartDisplayer.View c3AreaChartview;
    @Mock
    C3MeterChartDisplayer.View c3Meterview;
    @Mock 
    D3MapDisplayer.View d3MapDisplayerView;
    @Mock
    CountriesGeoJsonService geoService;


    public C3LineChartDisplayer c3LineChartDisplayer(DisplayerSettings settings) {
        c3Factory = mockC3JsTypesFactory();
        return initDisplayer(new C3LineChartDisplayer(c3LineChartview, filterLabelSet, c3Factory), settings);
    }
    
    public C3AreaChartDisplayer c3AreaChartDisplayer(DisplayerSettings settings) {
        c3Factory = mockC3JsTypesFactory();
        return initDisplayer(new C3AreaChartDisplayer(c3AreaChartview, filterLabelSet, c3Factory), settings);
    }
    
    public C3MeterChartDisplayer c3MeterChartDisplayer(DisplayerSettings settings) {
        c3Factory = mockC3JsTypesFactory();
        return initDisplayer(new C3MeterChartDisplayer(c3Meterview, filterLabelSet, c3Factory), settings);
    }
    
    public D3MapDisplayer d3MapDisplayer(DisplayerSettings settings) {
        return initDisplayer(new D3MapDisplayer(filterLabelSet, d3MapDisplayerView, geoService), settings);
    }
    
    private C3JsTypesFactory mockC3JsTypesFactory() {
        when(c3AxisInfo.getX()).thenReturn(c3AxisX);
        when(c3AxisInfo.getY()).thenReturn(c3AxisY);
        when(c3AxisX.getTick()).thenReturn(c3Tick);
        when(c3Conf.getAxis()).thenReturn(c3AxisInfo);
        when(c3Factory.c3ChartData(any(), any(), any(), any(), any())).thenReturn(c3ChartData);
        when(c3Factory.c3ChartConf(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(c3Conf);
        return c3Factory;
    }

}