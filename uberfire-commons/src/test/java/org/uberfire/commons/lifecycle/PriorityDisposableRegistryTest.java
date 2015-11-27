/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.commons.lifecycle;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class PriorityDisposableRegistryTest {

    @Test
    public void testGeneralDisposableState() {
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

    @Test
    public void testGeneralRegistryState() {
        final PriorityDisposable mocked1 = mock( PriorityDisposable.class );
        final PriorityDisposable mocked2 = mock( PriorityDisposable.class );

        PriorityDisposableRegistry.clear();
        assertEquals( 0, PriorityDisposableRegistry.getRegistry().size() );
        PriorityDisposableRegistry.register( "refName", mocked1 );
        assertEquals( 1, PriorityDisposableRegistry.getRegistry().size() );
        PriorityDisposableRegistry.register("refName", mocked1 );
        assertEquals( 1, PriorityDisposableRegistry.getRegistry().size() );
        PriorityDisposableRegistry.register("refName1", mocked2 );
        assertEquals( 2, PriorityDisposableRegistry.getRegistry().size() );
        PriorityDisposableRegistry.unregister("refName" );
        assertEquals( 1, PriorityDisposableRegistry.getRegistry().size() );
        PriorityDisposableRegistry.clear();
        assertEquals( 0, PriorityDisposableRegistry.getRegistry().size() );
    }

}
