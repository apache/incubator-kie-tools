package org.dashbuilder.displayer.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PerspectiveCoordinatorTest {

    PerspectiveCoordinator coordinator;

    @Before
    public void init(){
        final RendererManager rendererManager = mock(RendererManager.class);
        final DisplayerCoordinator displayerCoordinator = new DisplayerCoordinator(rendererManager);
        coordinator = new PerspectiveCoordinator(displayerCoordinator);
    }

    @Test
    public void testDisplayerList() {
        assertNotNull(coordinator.getDisplayerList());
        assertEquals(0, coordinator.getDisplayerList().size());

        final Displayer displayer = mock(Displayer.class);
        coordinator.addDisplayer(displayer);

        assertEquals(1, coordinator.getDisplayerList().size());

        coordinator.removeDisplayer(displayer);

        assertEquals(0, coordinator.getDisplayerList().size());
    }

}