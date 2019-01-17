package org.dashbuilder.renderer.c3.client;

import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_AMOUNT;
import static org.dashbuilder.dataset.ExpenseReportsData.COLUMN_DATE;
import static org.dashbuilder.dataset.group.AggregateFunctionType.SUM;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSettingsFactory;
import org.dashbuilder.renderer.c3.client.charts.line.C3LineChartDisplayer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class C3ResizableChartTest extends C3BaseTest {
    
    private static final int SIZE = 300;
    
    private C3LineChartDisplayer displayer;

    @Test
    public void c3Resizable() {
        DisplayerSettings resizableSettings = DisplayerSettingsFactory.newBarChartSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DATE)
                .column(COLUMN_DATE)
                .column(COLUMN_AMOUNT, SUM)
                .width(SIZE)
                .height(SIZE)
                .resizableOn(SIZE, SIZE)
                .buildSettings();
        displayer = c3LineChartDisplayer(resizableSettings);
        displayer.draw();
        C3LineChartDisplayer.View view = displayer.getView();
        verify(c3Factory, times(0)).c3ChartSize(300, 300);
        verify(view).setResizable(SIZE, SIZE);
    }
    
    @Test
    public void c3NotResizable() {
        DisplayerSettings notResizableSettings = DisplayerSettingsFactory.newBarChartSettings()
                .dataset(EXPENSES)
                .group(COLUMN_DATE)
                .column(COLUMN_DATE)
                .column(COLUMN_AMOUNT, SUM)
                .width(SIZE)
                .height(SIZE)
                .buildSettings();
        displayer = c3LineChartDisplayer(notResizableSettings);
        displayer.draw();
        C3LineChartDisplayer.View view = displayer.getView();
        verify(c3Factory).c3ChartSize(300, 300);
        verify(view, times(0)).setResizable(SIZE, SIZE);
    }

}
