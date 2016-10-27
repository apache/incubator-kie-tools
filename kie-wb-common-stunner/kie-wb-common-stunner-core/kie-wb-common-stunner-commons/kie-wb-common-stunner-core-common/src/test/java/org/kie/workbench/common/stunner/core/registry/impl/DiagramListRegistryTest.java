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

package org.kie.workbench.common.stunner.core.registry.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class DiagramListRegistryTest {

    private static final String DIAGRAM_1_UUID = "UUID1";
    private static final String DIAGRAM_2_UUID = "UUID2";
    private static final String DUMMY_STRING = "test";

    private AbstractDiagramListRegistry registry;

    @Mock
    Diagram diagram1;
    @Mock
    Diagram diagram2;

    @Before
    public void setup() {
        registry = new DiagramListRegistry();
        when( diagram1.getName() ).thenReturn( DIAGRAM_1_UUID );
        when( diagram2.getName() ).thenReturn( DIAGRAM_2_UUID );
    }

    @Test
    public void testGetDiagramByUUID() {
        registry.register( diagram1 );
        registry.register( diagram2 );
        assertEquals( diagram1, registry.getDiagramByUUID( DIAGRAM_1_UUID ) );
        assertEquals( diagram2, registry.getDiagramByUUID( DIAGRAM_2_UUID ) );
        assertEquals( null, registry.getDiagramByUUID( DUMMY_STRING ) );
    }

    @Test
    public void testUpdate() {
        registry.register( diagram1 );
        registry.register( diagram2 );
        registry.update( diagram1 );
        registry.update( diagram2 );
    }

    @Test( expected = RuntimeException.class )
    public void testInvalidUpdate() {
        registry.register( diagram1 );
        registry.update( diagram2 );
    }
}
