package org.uberfire.client.views.pfly.multiscreen;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.workbench.widgets.listbar.ResizeFlowPanel;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class MultiScreenViewTest {

    @Mock
    ResizeFlowPanel content;

    @InjectMocks
    MultiScreenView view;

    @Test
    public void testResize() {
        view.onResize();

        verify(content).onResize();
    }
}
