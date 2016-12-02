/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.services.verifier.core.checks.base;

import java.util.Collection;

import org.drools.workbench.services.verifier.core.cache.inspectors.RuleInspector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith( MockitoJUnitRunner.class )
public class PairChecksTest {

    private PairChecks pairChecks;

    @Mock
    private RuleInspector a;

    @Mock
    private RuleInspector b;

    @Mock
    private RuleInspector c;

    private PairCheck pairCheckOne;
    private PairCheck pairCheckTwo;

    @Before
    public void setUp() throws Exception {
        pairChecks = new PairChecks();
        pairCheckOne = new PairCheck( a, b );
        pairChecks.add( pairCheckOne );
        pairCheckTwo = new PairCheck( b, a );
        pairChecks.add( pairCheckTwo );
        pairChecks.add( new PairCheck( a, c ) );
        pairChecks.add( new PairCheck( c, a ) );
    }

    @Test
    public void getA() throws Exception {
        final Collection<PairCheck> pairChecks = this.pairChecks.get( a );
        assertEquals( 4, pairChecks.size() );
        assertTrue( pairChecks.contains( pairCheckOne ) );
        assertTrue( pairChecks.contains( pairCheckTwo ) );
    }

    @Test
    public void getB() throws Exception {
        final Collection<PairCheck> pairChecks = this.pairChecks.get( b );
        assertEquals( 2, pairChecks.size() );
        assertTrue( pairChecks.contains( pairCheckOne ) );
        assertTrue( pairChecks.contains( pairCheckTwo ) );
    }

    @Test
    public void removeB() throws Exception {
        final Collection<PairCheck> pairChecks = this.pairChecks.remove( b );
        assertEquals( 2, pairChecks.size() );
        assertTrue( pairChecks.contains( pairCheckOne ) );
        assertTrue( pairChecks.contains( pairCheckTwo ) );

        assertTrue( this.pairChecks.get( b ).isEmpty() );

        final Collection<PairCheck> pairChecksForA = this.pairChecks.get( a );
        assertEquals( 2, pairChecksForA.size() );
        assertFalse( pairChecksForA.contains( pairCheckOne ) );
        assertFalse( pairChecksForA.contains( pairCheckTwo ) );
    }

    @Test
    public void removeA() throws Exception {
        final Collection<PairCheck> pairChecks = this.pairChecks.remove( a );

        assertEquals( 4, pairChecks.size() );

        assertTrue( this.pairChecks.get( a ).isEmpty() );
        assertTrue( this.pairChecks.get( b ).isEmpty() );
        assertTrue( this.pairChecks.get( c ).isEmpty() );
    }
}