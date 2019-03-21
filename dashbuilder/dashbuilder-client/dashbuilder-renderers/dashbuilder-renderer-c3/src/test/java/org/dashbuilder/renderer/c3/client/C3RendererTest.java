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

import static org.mockito.Mockito.when;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.renderer.c3.client.charts.area.C3AreaChartDisplayer;
import org.dashbuilder.renderer.c3.client.charts.bar.C3BarChartDisplayer;
import org.dashbuilder.renderer.c3.client.charts.bubble.C3BubbleChartDisplayer;
import org.dashbuilder.renderer.c3.client.charts.line.C3LineChartDisplayer;
import org.dashbuilder.renderer.c3.client.charts.meter.C3MeterChartDisplayer;
import org.dashbuilder.renderer.c3.client.charts.pie.C3PieChartDisplayer;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class C3RendererTest {
    
    @Mock
    SyncBeanManager beanManager;
    @InjectMocks
    C3Renderer renderer;
    
    @Mock
    private SyncBeanDef<C3AreaChartDisplayer> areaChartDisplayerBeanDef;
    @Mock
    private SyncBeanDef<C3BarChartDisplayer> barChartDisplayerBeanDef;
    @Mock
    private SyncBeanDef<C3LineChartDisplayer> lineChartDisplayerBeanDef;
    @Mock
    private SyncBeanDef<C3PieChartDisplayer> pieChartDisplayerBeanDef;
    @Mock
    private SyncBeanDef<C3BubbleChartDisplayer> bubbleChartDisplayerBeanDef;
    @Mock
    private SyncBeanDef<C3MeterChartDisplayer> meterChartDisplayerBeanDef;
    @Mock
    private C3AreaChartDisplayer c3AreaChartDisplayer;
    @Mock
    private C3BarChartDisplayer c3BarChartDisplayer;
    @Mock
    private C3LineChartDisplayer c3LineChartDisplayer;
    @Mock
    private C3PieChartDisplayer c3PieChartDisplayer;
    @Mock
    private C3BubbleChartDisplayer c3BubbleChartDisplayer;
    @Mock
    private C3MeterChartDisplayer c3MeterChartDisplayer;
    
    private DisplayerSettings settings;
    
    
    
    @Before
    public void prepareTypes() {
        when(beanManager.lookupBean(C3AreaChartDisplayer.class)).thenReturn(areaChartDisplayerBeanDef);
        when(areaChartDisplayerBeanDef.newInstance()).thenReturn(c3AreaChartDisplayer);
        
        when(beanManager.lookupBean(C3BarChartDisplayer.class)).thenReturn(barChartDisplayerBeanDef);
        when(barChartDisplayerBeanDef.newInstance()).thenReturn(c3BarChartDisplayer);
        
        when(beanManager.lookupBean(C3LineChartDisplayer.class)).thenReturn(lineChartDisplayerBeanDef);
        when(lineChartDisplayerBeanDef.newInstance()).thenReturn(c3LineChartDisplayer);        
        
        when(beanManager.lookupBean(C3PieChartDisplayer.class)).thenReturn(pieChartDisplayerBeanDef);
        when(pieChartDisplayerBeanDef.newInstance()).thenReturn(c3PieChartDisplayer);     
        
        when(beanManager.lookupBean(C3BubbleChartDisplayer.class)).thenReturn(bubbleChartDisplayerBeanDef);
        when(bubbleChartDisplayerBeanDef.newInstance()).thenReturn(c3BubbleChartDisplayer);   
        
        when(beanManager.lookupBean(C3MeterChartDisplayer.class)).thenReturn(meterChartDisplayerBeanDef);
        when(meterChartDisplayerBeanDef.newInstance()).thenReturn(c3MeterChartDisplayer);        
        
        settings = mock(DisplayerSettings.class);
    }
    
    @Test
    public void lookupAreaChartTest() {
        when(settings.getType()).thenReturn(AREACHART);
        when(settings.getSubtype()).thenReturn(AREA_STACKED);
        renderer.lookupDisplayer(settings);
        verify(c3AreaChartDisplayer).stacked();
    }

    @Test
    public void lookupBarChartColumnStacked() {
        when(settings.getType()).thenReturn(BARCHART);
        when(settings.getSubtype()).thenReturn(COLUMN_STACKED);
        renderer.lookupDisplayer(settings);
        verify(c3BarChartDisplayer).stacked();
    }
    
    @Test
    public void lookuBarChartColumn() {
        when(settings.getType()).thenReturn(BARCHART);
        when(settings.getSubtype()).thenReturn(COLUMN);
        renderer.lookupDisplayer(settings);
        verify(c3BarChartDisplayer).notRotated();
    }

    @Test
    public void lookupBarChartStacked() {
        when(settings.getType()).thenReturn(BARCHART);
        when(settings.getSubtype()).thenReturn(BAR_STACKED);
        renderer.lookupDisplayer(settings);
        verify(c3BarChartDisplayer).stackedAndRotated();
    }

    @Test
    public void lookupBarChartBar() {
        when(settings.getType()).thenReturn(BARCHART);
        when(settings.getSubtype()).thenReturn(BAR);
        renderer.lookupDisplayer(settings);
        verify(c3BarChartDisplayer).rotated();
    }
    
    @Test
    public void lookupLineChart() {
        when(settings.getType()).thenReturn(LINECHART);
        when(settings.getSubtype()).thenReturn(LINE);
        renderer.lookupDisplayer(settings);
        verify(c3LineChartDisplayer, times(0)).smooth();
    }
    
    @Test
    public void lookupLineChartSmooth() {
        when(settings.getType()).thenReturn(LINECHART);
        when(settings.getSubtype()).thenReturn(SMOOTH);
        renderer.lookupDisplayer(settings);
        verify(c3LineChartDisplayer).smooth();
    }
    
    @Test
    public void lookupPieChart() {
        when(settings.getType()).thenReturn(PIECHART);
        when(settings.getSubtype()).thenReturn(PIE);
        renderer.lookupDisplayer(settings);
        verify(c3PieChartDisplayer, times(0)).donut();
    }
    
    @Test
    public void lookupPieChartDonut() {
        when(settings.getType()).thenReturn(PIECHART);
        when(settings.getSubtype()).thenReturn(DONUT);
        renderer.lookupDisplayer(settings);
        verify(c3PieChartDisplayer).donut();
    }
    
    @Test
    public void lookupBubbleChart() {
        when(settings.getType()).thenReturn(BUBBLECHART);
        renderer.lookupDisplayer(settings);
        verify(bubbleChartDisplayerBeanDef).newInstance();
    }
    
    @Test
    public void lookupMeterChart() {
        when(settings.getType()).thenReturn(METERCHART);
        renderer.lookupDisplayer(settings);
        verify(meterChartDisplayerBeanDef).newInstance();
    }    

}