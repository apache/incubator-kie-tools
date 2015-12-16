/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.analysis.checks.util;

import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RedundancyTest {

    @Test
    public void testSubsumes001() throws Exception {
        ArrayList<IsSubsuming> a = new ArrayList<IsSubsuming>();
        ArrayList<IsSubsuming> b = new ArrayList<IsSubsuming>();

        assertTrue( Redundancy.subsumes( a, b ) );
        assertTrue( Redundancy.subsumes( b, a ) );

    }

    @Test
    public void testSubsumes002() throws Exception {
        ArrayList<IsSubsuming> a = new ArrayList<IsSubsuming>();

        ArrayList<IsSubsuming> b = new ArrayList<IsSubsuming>();
        b.add( mock( IsSubsuming.class ) );

        assertFalse( Redundancy.subsumes( a, b ) );
        assertTrue( Redundancy.subsumes( b, a ) );

    }

    @Test
    public void testSubsumes003() throws Exception {
        ArrayList<Integer> a = new ArrayList<Integer>();
        a.add( 1 );

        ArrayList<Integer> b = new ArrayList<Integer>();
        b.add( 1 );
        b.add( 2 );

        assertFalse( Redundancy.subsumes( a, b ) );
        assertTrue( Redundancy.subsumes( b, a ) );

    }
}