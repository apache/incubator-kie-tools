/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.factory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNGraphFactory;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementGraphFactoryImplTest {

    @Mock
    private BPMNGraphFactory bpmnGraphFactory;

    private CaseManagementGraphFactoryImpl factory;

    @Before
    public void setup() {
        this.factory = new CaseManagementGraphFactoryImpl(bpmnGraphFactory);
    }

    @Test
    public void assertSetDiagramType() {
        factory.init();
        verify(bpmnGraphFactory,
               times(1)).setDiagramType(eq(CaseManagementDiagram.class));
    }

    @Test
    public void assertFactoryType() {
        // It is important that CaseManagementGraphFactoryImpl declares it relates to the CaseManagementGraphFactory
        // otherwise all sorts of things break. This test attempts to drawer the importance of this to future changes
        // should someone decide to change the apparent innocuous method in CaseManagementGraphFactoryImpl.
        assertEquals(CaseManagementGraphFactory.class,
                     factory.getFactoryType());
    }

    @Test
    public void testBuild() {
        factory.init();
        factory.build("uuid1",
                      "defSet1");
        verify(bpmnGraphFactory,
               times(1)).build(eq("uuid1"),
                               eq("defSet1"));
    }
}
