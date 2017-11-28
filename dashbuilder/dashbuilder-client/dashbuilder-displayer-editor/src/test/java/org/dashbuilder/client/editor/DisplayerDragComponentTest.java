package org.dashbuilder.client.editor;

import org.dashbuilder.displayer.DisplayerSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DisplayerDragComponentTest {

    @InjectMocks
    DisplayerDragComponent displayerDragComponent;

    @Test
    public void testAdjustSize(){
        final DisplayerSettings settings = mock(DisplayerSettings.class);
        when(settings.getChartWidth()).thenReturn(0);
        when(settings.getTableWidth()).thenReturn(0);

        displayerDragComponent.adjustSize(settings, 0);

        verify(settings).setTableWidth(0);

        displayerDragComponent.adjustSize(settings, 30);

        verify(settings).setTableWidth(10);
    }

}
