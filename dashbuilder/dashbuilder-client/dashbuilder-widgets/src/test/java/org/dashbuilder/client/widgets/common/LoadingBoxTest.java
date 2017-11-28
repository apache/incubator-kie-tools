package org.dashbuilder.client.widgets.common;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class LoadingBoxTest {
    
    @Mock LoadingBox.View view;
    
    private LoadingBox presenter;
    
    @Before
    public void setup() {
        // The presenter instance to test.
        presenter = new LoadingBox(view);
    }

    @Test
    public void testShow() throws Exception {
        presenter.show();
        verify(view, times(1)).show(anyString());
    }

    @Test
    public void testHide() throws Exception {
        presenter.hide();
        verify(view, times(1)).close();
    }
    
}
