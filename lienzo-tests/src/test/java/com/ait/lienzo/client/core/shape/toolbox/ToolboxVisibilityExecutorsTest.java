package com.ait.lienzo.client.core.shape.toolbox;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(LienzoMockitoTestRunner.class)
public class ToolboxVisibilityExecutorsTest {

    @Test
    public void testAnimationDelay() {
        assertEquals(Double.valueOf(0d), Double.valueOf(ToolboxVisibilityExecutors.ANIMATION_ALPHA_DELAY_MILLIS));
        assertEquals(Double.valueOf(0d), Double.valueOf(ToolboxVisibilityExecutors.ANIMATION_SCALE_DELAY_MILLIS));
    }
}
