/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.backend.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramLookupRequest;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DiagramLookupServiceImplTest {

    private static final String NAME = "name1";

    @Mock
    private IOService ioService;

    @Mock
    private DiagramServiceImpl diagramService;

    @Mock
    private Diagram<Graph, Metadata> diagram;

    private DiagramLookupServiceImpl tested;

    @Before
    public void setup() {
        when(diagram.getName()).thenReturn(NAME);
        tested = new DiagramLookupServiceImpl(ioService,
                                              diagramService);
    }

    @Test
    public void testMatches() {
        String criteria = DiagramLookupRequest.CRITERIA_NAME + "=" + NAME;
        String criteria1 = DiagramLookupRequest.CRITERIA_NAME + "=" + "name2";
        String criteria2 = "";
        assertTrue(tested.matches(criteria, diagram));
        assertFalse(tested.matches(criteria1, diagram));
        assertTrue(tested.matches(criteria2, diagram));
    }
}
