package org.uberfire.commons.lifecycle;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PriorityDisposableRegistryTest {

    @Test
    public void testGeneralState() {
        final PriorityDisposable mocked1 = mock( PriorityDisposable.class );
        final PriorityDisposable mocked2 = mock( PriorityDisposable.class );

        PriorityDisposableRegistry.clear();
        assertEquals( 0, PriorityDisposableRegistry.getDisposables().size() );
        PriorityDisposableRegistry.register( mocked1 );
        assertEquals( 1, PriorityDisposableRegistry.getDisposables().size() );
        PriorityDisposableRegistry.register( mocked1 );
        assertEquals( 1, PriorityDisposableRegistry.getDisposables().size() );
        PriorityDisposableRegistry.register( mocked2 );
        assertEquals( 2, PriorityDisposableRegistry.getDisposables().size() );
        PriorityDisposableRegistry.clear();
        assertEquals( 0, PriorityDisposableRegistry.getDisposables().size() );
    }

}
