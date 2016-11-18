/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.lienzo.toolbox;

import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.lienzo.toolbox.HoverTimer.Actions;
import org.mockito.Mock;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class HoverTimerTest {
    @Mock
    private Actions actions;
    @Mock
    Timer timer;
    @Mock
    private NodeMouseExitEvent exitEvent;
    @Mock
    NodeMouseEnterEvent enterEvent;

    private HoverTimer hoverTimer;

    @Before
    public void setUp() {
        when( actions.isReadyToHide() ).thenReturn( true );
        hoverTimer = spy( new HoverTimer( actions ) );
        when( hoverTimer.createTimer() ).thenReturn( timer );
    }

    @Test
    public void testFirstMouseExit() {
        hoverTimer.onNodeMouseExit( exitEvent );
        verify( actions ).isReadyToHide();
        verify( timer ).schedule( HoverTimer.TIMEOUT );
    }

    @Test
    public void testNotReadyMouseExit() {
        when( actions.isReadyToHide() ).thenReturn( false );
        hoverTimer.onNodeMouseExit( exitEvent );
        verify( actions ).isReadyToHide();
        verify( timer, times( 0 ) ).schedule( anyInt() );
    }

    @Test
    public void testTwiceMouseExit() {
        hoverTimer.onNodeMouseExit( exitEvent );
        hoverTimer.onNodeMouseExit( exitEvent );
        verify( actions, times( 2 ) ).isReadyToHide();
        verify( timer, times( 1 ) ).schedule( HoverTimer.TIMEOUT );
    }

    @Test
    public void testMouseEnter() {
        hoverTimer.onNodeMouseExit( exitEvent );
        hoverTimer.onNodeMouseEnter( enterEvent );
        verify( timer ).cancel();
        verify( actions ).onMouseEnter();
        assertNull( hoverTimer.getTimer() );
    }

    @Test
    public void testMouseEnterWithoutExit() {
        hoverTimer.onNodeMouseEnter( enterEvent );
        verify( timer, times( 0 ) ).cancel();
        verify( actions ).onMouseEnter();
        assertNull( hoverTimer.getTimer() );
    }

    @Test
    public void testTimer() {
        Timer t = new HoverTimer( actions ).createTimer();
        t.run();
        verify( actions ).onMouseExit();
    }
}
